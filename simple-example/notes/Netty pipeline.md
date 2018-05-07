## Netty中的责任链
看了不少关于Netty的书籍（实际上只有《Netty In Action》而已😳）和博客，对Netty的核心组件有了一点理解，特别是对其中的pipeline印象特别深刻。因为之前有读过一本《How Tomcat Works》，其中也有pipeline的概念。现在有必要用自己的文字将这个东西解释清楚。

### 来源
曾经很长一段时间，我都不清楚这个pipeline到底在什么地方使用的。尤其是各种方法调用的时候一脸懵逼。在多次睡梦中无意间知道了这个东西来自何处。下面看一段代码：

```java
protected void doRead() {
    if (!readPending) {
        return;
    }
    readPending = false;
    final ChannelConfig config = config();
    final ChannelPipeline pipeline = pipeline();
    final RecvByteBufAllocator.Handle allocHandle = unsafe().recvBufAllocHandle();
    allocHandle.reset(config);
    boolean closed = false;
    Throwable exception = null;
    try {
        do {
            int localRead = doReadMessages(readBuf);
            if (localRead == 0) {
                break;
            }
            if (localRead < 0) {
                closed = true;
                break;
            }
            allocHandle.incMessagesRead(localRead);
        } while (allocHandle.continueReading());
    } catch (Throwable t) {
        exception = t;
    }
    boolean readData = false;
    int size = readBuf.size();
    if (size > 0) {
        readData = true;
        for (int i = 0; i < size; i++) {
            readPending = false;
            pipeline.fireChannelRead(readBuf.get(i));
        }
        readBuf.clear();
        allocHandle.readComplete();
        pipeline.fireChannelReadComplete();
    }
    if (exception != null) {
        if (exception instanceof IOException) {
            closed = true;
        }
        pipeline.fireExceptionCaught(exception);
    }
    if (closed) {
        if (isOpen()) {
            unsafe().close(unsafe().voidPromise());
        }
    } else if (readPending || config.isAutoRead() || !readData && isActive()) {
        read();
    }
}
```
这段代码的用途是获取客户端的输入。当然它并没直接去读取，而是将其内容包装成``readBuf``，交给pipeline去完成。在这段代码中涉及到了三个方法的调用：fireChannelRead、fireChannelReadComplete和fireExceptionCaught。这样具体的处理就交给了pipeline去执行。实际上这种代码编写的方式也是一种设计模式，观察者模式。这里的观察者就是pipeline中的handler，而被观察者就是它自己。每当有触发fireXXX事件后都会将事件依次传递给pipeline中的handler。因此在具体的编码过程中，设计模式的使用绝对不是单一的，有可能是多个模式的混合。这段代码中就是根据具体情况来触发不同的事件，这些事件由pipeline中的handler来处理。

那么这个pipeline是怎么来的呢？那就得追踪一下具体方法的调用了。它是其顶级父类``AbstractChannel``的``pipeline()``方法。其返回一个``DefaultChannelPipeline``的实例。不用解释都知道这个``DefaultChannelPipeline``一定是``ChannelPipeline``的默认实现。

这个pipeline知道怎么获取的了，现在的关注点就该放到pipeline的具体实现了。

### PIPELINE
这个接口的JAVADOC文档写的很详细。

```plain
 *                                                 I/O Request
 *                                            via {@link Channel} or
 *                                        {@link ChannelHandlerContext}
 *                                                      |
 *  +---------------------------------------------------+---------------+
 *  |                           ChannelPipeline         |               |
 *  |                                                  \|/              |
 *  |    +---------------------+            +-----------+----------+    |
 *  |    | Inbound Handler  N  |            | Outbound Handler  1  |    |
 *  |    +----------+----------+            +-----------+----------+    |
 *  |              /|\                                  |               |
 *  |               |                                  \|/              |
 *  |    +----------+----------+            +-----------+----------+    |
 *  |    | Inbound Handler N-1 |            | Outbound Handler  2  |    |
 *  |    +----------+----------+            +-----------+----------+    |
 *  |              /|\                                  .               |
 *  |               .                                   .               |
 *  | ChannelHandlerContext.fireIN_EVT() ChannelHandlerContext.OUT_EVT()|
 *  |        [ method call]                       [method call]         |
 *  |               .                                   .               |
 *  |               .                                  \|/              |
 *  |    +----------+----------+            +-----------+----------+    |
 *  |    | Inbound Handler  2  |            | Outbound Handler M-1 |    |
 *  |    +----------+----------+            +-----------+----------+    |
 *  |              /|\                                  |               |
 *  |               |                                  \|/              |
 *  |    +----------+----------+            +-----------+----------+    |
 *  |    | Inbound Handler  1  |            | Outbound Handler  M  |    |
 *  |    +----------+----------+            +-----------+----------+    |
 *  |              /|\                                  |               |
 *  +---------------+-----------------------------------+---------------+
 *                  |                                  \|/
 *  +---------------+-----------------------------------+---------------+
 *  |               |                                   |               |
 *  |       [ Socket.read() ]                    [ Socket.write() ]     |
 *  |                                                                   |
 *  |  Netty Internal I/O Threads (Transport Implementation)            |
 *  +-------------------------------------------------------------------+
```
文档中画的一张图很好解释了这个pipeline的内部构造。然而这只是高度抽象的，具体内部怎么实现的还得去追踪源码瞧一瞧。Inbound handler通常是用来读取远程节点输入的数据，而Outbound则相反。与之对应的API分别为``SocketChannel#read(ByteBuffer)``和``SocketChannel#write(ByteBuffer)``。这个图中最底部的一层可以理解为网卡的操作，最顶层为应用程序。顺序可不能搞反。有一个简单的栗子：

```java
p.addLast("1", new InboundHandlerA());
p.addLast("2", new InboundHandlerB());
p.addLast("3", new OutboundHandlerA());
p.addLast("4", new OutboundHandlerB());
p.addLast("5", new InboundOutboundHandlerX());
```
这个栗子中，inbound事件的传递顺序为1-2-3-4-5.outbound则相反。事实上不是这样的，因为3和4没有实现InboundHandler，因此inbound事件的传递顺序为1-2-5.同理，1和2没有实现OutboundHandler，因此outbound事件为5-4-3.

```java
public interface ChannelPipeline
        extends ChannelInboundInvoker, ChannelOutboundInvoker, Iterable<Entry<String, ChannelHandler>> {
        // 省略。。。
}
```
这个接口虽然继承了ChannelInboundInvoker，但还是将其所有方法重写了。一直不清楚为何在接口继承中将方法重写的意义。

了解了pipeline其中的功能，那就看看其内部是如何实现的。

```java

final AbstractChannelHandlerContext head;
final AbstractChannelHandlerContext tail;

private final Channel channel;
private final ChannelFuture succeededFuture;
private final VoidChannelPromise voidPromise;
protected DefaultChannelPipeline(Channel channel) {
    this.channel = ObjectUtil.checkNotNull(channel, "channel");
    succeededFuture = new SucceededChannelFuture(channel, null);
    voidPromise =  new VoidChannelPromise(channel, true);

    tail = new TailContext(this);
    head = new HeadContext(this);

    head.next = tail;
    tail.prev = head;
}
```
构造器参数传的是一个channel，这点一定是没的商量的。因为每个channel对应一个pipeline。这样理解起来很费力，具体就是一个客户端连进你的Netty服务器，那么这个服务器就会为你这个客户端创建一个channel，同时也会创建一个pipeline。重点是tail和head这两个成员属性。这种写法很容易想到了一种数据结构--链表。然而还不是一般的链表，这是双向链表。而这个链表中所存放的元素则是AbstractChannelHandlerContext对象。因此这个pipeline中放的并不是handler而是AbstractChannelHandlerContext。

针对最常见的api如addLast的具体实现：

```java
public final ChannelPipeline addLast(String name, ChannelHandler handler) {
    return addLast(null, name, handler);
}
@Override
public final ChannelPipeline addLast(EventExecutorGroup group, String name, ChannelHandler handler) {
    final AbstractChannelHandlerContext newCtx;
    synchronized (this) {
        checkMultiplicity(handler);
        newCtx = newContext(group, filterName(name, handler), handler);
        addLast0(newCtx);
        if (!registered) {
            newCtx.setAddPending();
            callHandlerCallbackLater(newCtx, true);
            return this;
        }
        EventExecutor executor = newCtx.executor();
        if (!executor.inEventLoop()) {
            newCtx.setAddPending();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    callHandlerAdded0(newCtx);
                }
            });
            return this;
        }
    }
    callHandlerAdded0(newCtx);
    return this;
}
private AbstractChannelHandlerContext newContext(EventExecutorGroup group, String name, ChannelHandler handler) {
    return new DefaultChannelHandlerContext(this, childExecutor(group), name, handler);
}
private void addLast0(AbstractChannelHandlerContext newCtx) {
    AbstractChannelHandlerContext prev = tail.prev;
    newCtx.prev = prev;
    newCtx.next = tail;
    prev.next = newCtx;
    tail.prev = newCtx;
}
```
这里将handler包装成了AbstractChannelHandlerContext然后再添加到链表中。与此同事也会触发一些事件如handlerAdded、handlerRemoved等。这些事件不属于pipeline的事件范围了，而是属于ChannelHandler。而addLast0这个方法就很常规了，无非就是拆链解链。添加的顺序为新添加的节点要挨着tail节点。不然为什么把名字取成addLast呀。

```java
final class DefaultChannelHandlerContext extends AbstractChannelHandlerContext {
    private final ChannelHandler handler;
    DefaultChannelHandlerContext(
            DefaultChannelPipeline pipeline, EventExecutor executor, String name, ChannelHandler handler) {
        super(pipeline, executor, name, isInbound(handler), isOutbound(handler));
        if (handler == null) {
            throw new NullPointerException("handler");
        }
        this.handler = handler;
    }
    @Override
    public ChannelHandler handler() {
        return handler;
    }
    private static boolean isInbound(ChannelHandler handler) {
        return handler instanceof ChannelInboundHandler;
    }
    private static boolean isOutbound(ChannelHandler handler) {
        return handler instanceof ChannelOutboundHandler;
    }
}

```
而DefaultChannelHandlerContext则是对AbstractChannelHandlerContext的默认实现。其中持有了一个ChannelHandler的成员变量。而AbstractChannelHandlerContext则是ChannelHandlerContext接口的直接实现。

```java
public interface ChannelHandlerContext extends AttributeMap, ChannelInboundInvoker, ChannelOutboundInvoker {

    Channel channel();

    EventExecutor executor();

    String name();

    ChannelHandler handler();

    boolean isRemoved();

    // 重写自ChannelInboundInvoker的所有方法
    // 省略。。。
}
```

下面就针对某个事件来具体分析这个事件在pipeline是如何流转的。

```java
@Override
public final ChannelPipeline fireChannelRead(Object msg) {
    AbstractChannelHandlerContext.invokeChannelRead(head, msg);
    return this;
}
```
这段代码是当有读事件时触发的具体操作。具体的执行由一个静态方法完成：

```java
static void invokeChannelRead(final AbstractChannelHandlerContext next, Object msg) {
    final Object m = next.pipeline.touch(ObjectUtil.checkNotNull(msg, "msg"), next);
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
        next.invokeChannelRead(m);
    } else {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                next.invokeChannelRead(m);
            }
        });
    }
}
```
先不关心是不是在eventLoop中，反正最终要执行的是next.invokeChannelRead(m)这段代码。next变量为传入的参数head。它也是AbstractChannelHandlerContext的实例：

```java
private void invokeChannelRead(Object msg) {
	 // 先判断这个handler是不是添加到pipeline中去了
    if (invokeHandler()) {
        try {
            ((ChannelInboundHandler) handler()).channelRead(this, msg);
        } catch (Throwable t) {
            notifyHandlerException(t);
        }
    } else {
    	  // 没有就调用下一个
        fireChannelRead(msg);
    }
}
```
最终调用的是ChannelInboundHandler的channelRead方法。而ChannelInboundHandler为接口类型，有很多自定义的实现。那也就是handler()决定到底是用哪个实现。事实上，第一个调用的实例为head，也就是handler()返回的实例是head。head这个比tail厉害：

```java
final class HeadContext extends AbstractChannelHandlerContext
            implements ChannelOutboundHandler, ChannelInboundHandler {
	// ...
}

final class TailContext extends AbstractChannelHandlerContext implements ChannelInboundHandler {
	//...
}
```

head实现了2个接口，in & out。而tail只实现了一个。

具体看看head中的channelRead干了些什么事：

```java
@Override
public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ctx.fireChannelRead(msg);
}
@Override
public ChannelHandlerContext fireChannelRead(final Object msg) {
    invokeChannelRead(findContextInbound(), msg);
    return this;
}
```
具体调用的是ChannelHandlerContext的事件触发ctx.fireChannelRead。而这个方法又回到了起点，只是参数不同罢了。之前传入的参数是head，这次是findContextInbound()：

```java
private AbstractChannelHandlerContext findContextInbound() {
    AbstractChannelHandlerContext ctx = this;
    do {
        ctx = ctx.next;
    } while (!ctx.inbound);
    return ctx;
}
```
这个方法具体功能是找下一个inboundhandler，找到了就到此为止，然后继续调用invokeChannelRead。如此反复，直到最后的tail为止。前提是inboundhandler中的channelRead的实现必须将事件传递下去，如果每个inboundhandler都把事件传递下去，事件一定能到tail节点。

看看tail的chaanelRead的实现：

```java
@Override
public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    onUnhandledInboundMessage(msg);
}
protected void onUnhandledInboundMessage(Object msg) {
    try {
        logger.debug(
                "Discarded inbound message {} that reached at the tail of the pipeline. " +
                        "Please check your pipeline configuration.", msg);
    } finally {
        ReferenceCountUtil.release(msg);
    }
}
```
最后的处理方法是将其丢弃。

这样，一个read的事件就这样从头到尾（head-->tail）的传递就明白了。这个过程最重要的中介就是ChannelHandlerContext，它能够决定事件是否要继续传递，以及确定下一个handler是谁。而pipeline则提供一个管道，让所有事件能在这个管道中传递。这种设计模式就是责任链模式。在tomcat中的vavle也是这种设计，其中也有pipeline组件。

### 总结
责任链模式的使用场景并不罕见，但是理解其正真的思想我觉得还是得花点时间。理解倒是次要的，运用起来才是关键。在实际的编码中单纯使用某一种设计模式不太现实，通常是很多设计模式组合起来。代码读多了，倒也真的是有所收获，起码再看很长一段代码后不会觉得很慌。正如那句话"读书破万卷，下笔如有神"，我相信看得多了，自己去写自然也不会差到哪里去。