## Netty中的线程模型之OIO

和高性能NIO相比，个人认为OIO的实现相对要简单一点，选择这个难度稍微低一点的实现来肯对目前的菜鸡我而言更现实。

本质上而言，eventloopgroup就是线程池。

![image](http://7xsfwn.com1.z0.glb.clouddn.com/OioEventLoopGroup.png)

Netty针对JDK的实现做了进一步的加强。

对于OIO的第一反应是「阻塞」，「性能差」，「耗资源」。事实上确实如此，但为什么还要写一篇文章（笔记）来对它进行描述呢？在之前，我以为Netty并没有提供OIO的实现，天真的认为Netty仅仅是对NIO进行了封装。直到某一天使用到了OIO，想将OIO的线程模型和Netty中的实现类似：一个线程负责连接，别的线程负责IO。想了许久没有下文，机缘巧合看到了Netty的文档，其中有对OIO的封装，恰好和我心中想的一模一样，而且对外提供的API不变。这种抽象设计让我想深入其中，看看到底是如何做到的。我想借鉴其中的设计来实现一个比较简单对OIO的封装。

> EventLoopGroup which is used to handle OIO Channel's. Each Channel will be handled by its own EventLoop to not block others.

文档中的解释很简单，EventLoopGroup用来处理连接，每个连接由它自己的EventLoop处理。

这段解释不禁联想到了针对OIO的编程：每个连接由新开的线程处理，有多少个连接就有多少个线程。这里的Channel就是连接的抽象，EventLoop可以理解为线程。

### 初始化

```java
public OioEventLoopGroup(int maxChannels, ThreadFactory threadFactory) {
    super(maxChannels, threadFactory);
}

protected ThreadPerChannelEventLoopGroup(int maxChannels, ThreadFactory threadFactory, Object... args) {
    this(maxChannels, new ThreadPerTaskExecutor(threadFactory), args);
}


public final class ThreadPerTaskExecutor implements Executor {
    private final ThreadFactory threadFactory;

    public ThreadPerTaskExecutor(ThreadFactory threadFactory) {
        if (threadFactory == null) {
            throw new NullPointerException("threadFactory");
        }
        this.threadFactory = threadFactory;
    }

    @Override
    public void execute(Runnable command) {
        threadFactory.newThread(command).start();
    }
}
```
构造器中两个参数，maxChannels为最大连接数。注意，这个是比较讲究的，在Netty线程模型中有boss和worker线程之分。如果只指定一个，也就是boss就是worker那么这个值如果为1那么任何客户端无法连进来，如果为2则只允许1个客户端连进来，依次类推。默认为0，表示允许无限多客户端接入（理论上）。当有worker的时候，boss的maxChannels指定多少无所谓，worker中的maxChannels值为多少就意味着允许多少客户端接入，同理0代表无限多。第二个参数为线程工厂，默认使用的是JDK的默认实现：Executors.defaultThreadFactory()

```java
protected ThreadPerChannelEventLoopGroup(int maxChannels, Executor executor, Object... args) {
    if (maxChannels < 0) {
        throw new IllegalArgumentException(String.format(
                "maxChannels: %d (expected: >= 0)", maxChannels));
    }
    if (executor == null) {
        throw new NullPointerException("executor");
    }
    if (args == null) {
        childArgs = EmptyArrays.EMPTY_OBJECTS;
    } else {
        childArgs = args.clone();
    }
    this.maxChannels = maxChannels;
    this.executor = executor;

    tooManyChannels = ThrowableUtil.unknownStackTrace(
            new ChannelException("too many channels (max: " + maxChannels + ')'),
            ThreadPerChannelEventLoopGroup.class, "nextChild()");
}
```
其中的一些成员变量：

```java
public class ThreadPerChannelEventLoopGroup extends AbstractEventExecutorGroup implements EventLoopGroup {
    private final Object[] childArgs;
    private final int maxChannels;
    // 任务执行器
    final Executor executor;
    // 活跃的线程集合
    final Set<EventLoop> activeChildren =
            Collections.newSetFromMap(PlatformDependent.<EventLoop, Boolean>newConcurrentHashMap());
    // 空闲的线程集合
    final Queue<EventLoop> idleChildren = new ConcurrentLinkedQueue<EventLoop>();
    private final ChannelException tooManyChannels;

    private volatile boolean shuttingDown;
    private final Promise<?> terminationFuture = new DefaultPromise<Void>(GlobalEventExecutor.INSTANCE);
    private final FutureListener<Object> childTerminationListener = new FutureListener<Object>() {
        @Override
        public void operationComplete(Future<Object> future) throws Exception {
            // Inefficient, but works.
            if (isTerminated()) {
                terminationFuture.trySuccess(null);
            }
        }
    };
 }
```
EventLoopGroup的初始化就这样结束了。但是要探索其中的工作机制还得从一个Server 的启动开始。

### ServerBootstrap的启动


```java
try {
        ServerBootstrap b = new ServerBootstrap(); // (2)
        b.group(bossGroup, workerGroup)
         .channel(OioServerSocketChannel.class) // (3)
         .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
             @Override
             public void initChannel(SocketChannel ch) throws Exception {
                 ch.pipeline().addLast(new DiscardServerHandler());
             }
         })
         .option(ChannelOption.SO_BACKLOG, 128)          // (5)
         .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
    
        // Bind and start to accept incoming connections.
        ChannelFuture f = b.bind(port).sync(); // (7)
    
        // Wait until the server socket is closed.
        // In this example, this does not happen, but you can do that to gracefully
        // shut down your server.
        f.channel().closeFuture().sync();
    } finally {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
```
最核心的是bind方法。

```java
public ChannelFuture bind(SocketAddress localAddress) {
    validate();
    if (localAddress == null) {
        throw new NullPointerException("localAddress");
    }
    return doBind(localAddress);
}

private ChannelFuture doBind(final SocketAddress localAddress) {
	 // 核心是regFuture的获取，有了这个后面一切都好说
    final ChannelFuture regFuture = initAndRegister();
    final Channel channel = regFuture.channel();
    if (regFuture.cause() != null) {
        return regFuture;
    }
    if (regFuture.isDone()) {
        // At this point we know that the registration was complete and successful.
        ChannelPromise promise = channel.newPromise();
        doBind0(regFuture, channel, localAddress, promise);
        return promise;
    } else {
        // Registration future is almost always fulfilled already, but just in case it's not.
        final PendingRegistrationPromise promise = new PendingRegistrationPromise(channel);
        regFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Throwable cause = future.cause();
                if (cause != null) {
                    // Registration on the EventLoop failed so fail the ChannelPromise directly to not cause an
                    // IllegalStateException once we try to access the EventLoop of the Channel.
                    promise.setFailure(cause);
                } else {
                    // Registration was successful, so set the correct executor to use.
                    // See https://github.com/netty/netty/issues/2586
                    promise.registered();

                    doBind0(regFuture, channel, localAddress, promise);
                }
            }
        });
        return promise;
    }
}

final ChannelFuture initAndRegister() {
    Channel channel = null;
    try {
    	 // 实例化OioServerSocketChannel
        channel = channelFactory.newChannel();
        init(channel);
    } catch (Throwable t) {
        if (channel != null) {
            // channel can be null if newChannel crashed (eg SocketException("too many open files"))
            channel.unsafe().closeForcibly();
            // as the Channel is not registered yet we need to force the usage of the GlobalEventExecutor
            return new DefaultChannelPromise(channel, GlobalEventExecutor.INSTANCE).setFailure(t);
        }
        // as the Channel is not registered yet we need to force the usage of the GlobalEventExecutor
        return new DefaultChannelPromise(new FailedChannel(), GlobalEventExecutor.INSTANCE).setFailure(t);
    }
	 // 核心是这段逻辑config().group()返回的就是我们设置的boss：OioEventLoopGroup
    ChannelFuture regFuture = config().group().register(channel);
    if (regFuture.cause() != null) {
        if (channel.isRegistered()) {
            channel.close();
        } else {
            channel.unsafe().closeForcibly();
        }
    }
    return regFuture;
}
```

忽略掉无关的逻辑（实在是很复杂），关键点到了这个OioEventLoopGroup的register方法。

```java
@Override
public ChannelFuture register(Channel channel) {
    if (channel == null) {
        throw new NullPointerException("channel");
    }
    try {
        EventLoop l = nextChild();
        // 将channel包装了一下--> DefaultChannelPromise
        return l.register(new DefaultChannelPromise(channel, l));
    } catch (Throwable t) {
        return new FailedChannelFuture(channel, GlobalEventExecutor.INSTANCE, t);
    }
}
```

这个Channel就是OioServerSocketChannel，对应在OIO原生API中就是ServerSocket。根据代码的字面意可以这样解释：将OioServerSocketChannel注册到EventLoopGroup。

最终是通过EventLoop去注册的：

```java
private EventLoop nextChild() throws Exception {
    if (shuttingDown) {
        throw new RejectedExecutionException("shutting down");
    }
	 // 从空闲队列中取EventLoop
    EventLoop loop = idleChildren.poll();
    if (loop == null) {
        if (maxChannels > 0 && activeChildren.size() >= maxChannels) {
            throw tooManyChannels;
        }
        // 没有就新建一个 参数没用
        loop = newChild(childArgs);
        loop.terminationFuture().addListener(childTerminationListener);
    }
    // 新建的放到活跃队列中
    activeChildren.add(loop);
    return loop;
}
// 新建的一个EventLoop为ThreadPerChannelEventLoop实例，参数为EventLoopGroup 也就是说这个EventLoop说由哪个group产生的
protected EventLoop newChild(@SuppressWarnings("UnusedParameters") Object... args) throws Exception {
    return new ThreadPerChannelEventLoop(this);
}
```

这么一来，真正的注册逻辑就交给了ThreadPerChannelEventLoop去实现了。

```java
public ThreadPerChannelEventLoop(ThreadPerChannelEventLoopGroup parent) {
	 // 注意 这里的parent.executor为ThreadPerTaskExecutor实例
    super(parent, parent.executor, true);
    this.parent = parent;
}

public ChannelFuture register(ChannelPromise promise) {
    return super.register(promise).addListener(new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                ch = future.channel();
            } else {
                deregister();
            }
        }
    });
}
// 父类的
public ChannelFuture register(final ChannelPromise promise) {
    ObjectUtil.checkNotNull(promise, "promise");
    promise.channel().unsafe().register(this, promise);
    return promise;
}
```

终于要看到希望了，这一层一层的调用实在很繁琐，会把人看晕，建议多看几遍就不晕了😂。

promise.channel()返回的就是我们设置的OioServerSocketChannel。而unsafe方法则是继承自它的「太爷爷」。
其具体实现则是由它「爷爷」来实现。

```java
@Override
protected AbstractUnsafe newUnsafe() {
    return new DefaultOioUnsafe();
}
// 这是它的一个内部类
private final class DefaultOioUnsafe extends AbstractUnsafe {
    @Override
    public void connect(
            final SocketAddress remoteAddress,
            final SocketAddress localAddress, final ChannelPromise promise) {
        if (!promise.setUncancellable() || !ensureOpen(promise)) {
            return;
        }

        try {
            boolean wasActive = isActive();
            doConnect(remoteAddress, localAddress);

            // Get the state as trySuccess() may trigger an ChannelFutureListener that will close the Channel.
            // We still need to ensure we call fireChannelActive() in this case.
            boolean active = isActive();

            safeSetSuccess(promise);
            if (!wasActive && active) {
                pipeline().fireChannelActive();
            }
        } catch (Throwable t) {
            safeSetFailure(promise, annotateConnectException(t, remoteAddress));
            closeIfClosed();
        }
    }
}
```

虽然快看到希望的曙光了，但是眼前却依旧是一片黑暗。😫！！！亲爱的register你到底在哪里？

```java
// AbstractUnsafe 也是内部类 tmd netty真会折腾
public final void register(EventLoop eventLoop, final ChannelPromise promise) {
    if (eventLoop == null) {
        throw new NullPointerException("eventLoop");
    }
    if (isRegistered()) {
        promise.setFailure(new IllegalStateException("registered to an event loop already"));
        return;
    }
    if (!isCompatible(eventLoop)) {
        promise.setFailure(
                new IllegalStateException("incompatible event loop type: " + eventLoop.getClass().getName()));
        return;
    }

    AbstractChannel.this.eventLoop = eventLoop;
	 // 关键点 其他不管 这里一定是最后一步了
    if (eventLoop.inEventLoop()) {
        register0(promise);
    } else {
        try {
        	  // 这里大有玄机
            eventLoop.execute(new Runnable() {
                @Override
                public void run() {
                    register0(promise);
                }
            });
        } catch (Throwable t) {
            logger.warn(
                    "Force-closing a channel whose registration task was not accepted by an event loop: {}",
                    AbstractChannel.this, t);
            closeForcibly();
            closeFuture.setClosed();
            safeSetFailure(promise, t);
        }
    }
}
// 这段代码看不懂 先放这个地方 看懂了再来解读
private void register0(ChannelPromise promise) {
    try {
        // check if the channel is still open as it could be closed in the mean time when the register
        // call was outside of the eventLoop
        if (!promise.setUncancellable() || !ensureOpen(promise)) {
            return;
        }
        boolean firstRegistration = neverRegistered;
        // OIO版本中什么都不做
        doRegister();
        neverRegistered = false;
        registered = true;

        // Ensure we call handlerAdded(...) before we actually notify the promise. This is needed as the
        // user may already fire events through the pipeline in the ChannelFutureListener.
        pipeline.invokeHandlerAddedIfNeeded();

        safeSetSuccess(promise);
        pipeline.fireChannelRegistered();
        // Only fire a channelActive if the channel has never been registered. This prevents firing
        // multiple channel actives if the channel is deregistered and re-registered.
        if (isActive()) {
            if (firstRegistration) {
                pipeline.fireChannelActive();
            } else if (config().isAutoRead()) {
                // This channel was registered before and autoRead() is set. This means we need to begin read
                // again so that we process inbound data.
                //
                // See https://github.com/netty/netty/issues/4805
                beginRead();
            }
        }
    } catch (Throwable t) {
        // Close the channel directly to avoid FD leak.
        closeForcibly();
        closeFuture.setClosed();
        safeSetFailure(promise, t);
    }
}
```

eventLoop.execute方法中不仅仅只执行一个Runnable就完了，因为Netty这个狗逼没有使用默认实现 而是自己实现的

```java
// SingleThreadEventExecutor的实现
@Override
public void execute(Runnable task) {
    if (task == null) {
        throw new NullPointerException("task");
    }

    boolean inEventLoop = inEventLoop();
    // 这个task就是register0的具体逻辑 这个逻辑暂时不管（因为看不懂😂）
    if (inEventLoop) {
    	  // 放到队列中
        addTask(task);
    } else {
    	 // 终于露出马脚了 开线程了吧
        startThread();
        addTask(task);
        if (isShutdown() && removeTask(task)) {
            reject();
        }
    }

    if (!addTaskWakesUp && wakesUpForTask(task)) {
        wakeup(inEventLoop);
    }
}
// 开个线程都玩这么花
private void startThread() {
    if (state == ST_NOT_STARTED) {
        if (STATE_UPDATER.compareAndSet(this, ST_NOT_STARTED, ST_STARTED)) {
            try {
                doStartThread();
            } catch (Throwable cause) {
                STATE_UPDATER.set(this, ST_NOT_STARTED);
                PlatformDependent.throwException(cause);
            }
        }
    }
}

private void doStartThread() {
    assert thread == null;
    // 这个executor就是ThreadPerTaskExecutor 
    executor.execute(new Runnable() {
        @Override
        public void run() {
            thread = Thread.currentThread();
            if (interrupted) {
                thread.interrupt();
            }

            boolean success = false;
            updateLastExecutionTime();
            try {
            		// 这个狗逼玩的是真花 还去调别人的run 实际上是ThreadPerChannelEventLoop的实现
                SingleThreadEventExecutor.this.run();
                success = true;
            } catch (Throwable t) {
                logger.warn("Unexpected exception from an event executor: ", t);
            } finally {
            	// 太多 不看了          
            }
        }
    });
}

// ThreadPerChannelEventLoop
@Override
protected void run() {
	 // 死循环
    for (;;) {
    	 // 这里的task就是AbstractUnsafe#register0的逻辑 当然也有可能是其他的
        Runnable task = takeTask();
        if (task != null) {
            task.run();
            updateLastExecutionTime();
        }

        Channel ch = this.ch;
        if (isShuttingDown()) {
            if (ch != null) {
                ch.unsafe().close(ch.unsafe().voidPromise());
            }
            if (confirmShutdown()) {
                break;
            }
        } else {
            if (ch != null) {
                // Handle deregistration
                if (!ch.isRegistered()) {
                    runAllTasks();
                    deregister();
                }
            }
        }
    }
}
```

整了这么多，依旧没有搞明白这个register到底在做什么。但是明白了一件事：找到了启动入口。

### 注册逻辑
在原生OIO网络编程中，实现一个服务器需要做这几个步骤：

* 创建ServerSocket对象绑定监听端口。
* 通过accept()方法监听客户端的请求。
* 建立连接后，通过输入输出流读取客户端发送的请求信息。
* 通过输出流向客户端发送请求信息。
* 关闭相关资源。

```java
try{
    ServerSocket server=null;
    try{
        server=new ServerSocket(5209);
        //b)指定绑定的端口，并监听此端口。
        System.out.println("服务器启动成功");
        //创建一个ServerSocket在端口5209监听客户请求
    }catch(Exception e) {
            System.out.println("没有启动监听："+e);
            //出错，打印出错信息
    }
    Socket socket=null;
    try{
        socket=server.accept();
        //2、调用accept()方法开始监听，等待客户端的连接 
        //使用accept()阻塞等待客户请求，有客户
        //请求到来则产生一个Socket对象，并继续执行
    }catch(Exception e) {
        System.out.println("Error."+e);
        //出错，打印出错信息
    }
```
在Netty中的实现基本如此，只不过代码结构比较复杂罢了。这段代码在Netty中的的实现在OioServerSocketChannel中：

```java
@Override
protected void doBind(SocketAddress localAddress) throws Exception {
    socket.bind(localAddress, config.getBacklog());
}
@Override
protected int doReadMessages(List<Object> buf) throws Exception {
    if (socket.isClosed()) {
        return -1;
    }
    try {
        Socket s = socket.accept();
        try {
            buf.add(new OioSocketChannel(this, s));
            return 1;
        } catch (Throwable t) {
            logger.warn("Failed to create a new channel from an accepted socket.", t);
            try {
                s.close();
            } catch (Throwable t2) {
                logger.warn("Failed to close a socket.", t2);
            }
        }
    } catch (SocketTimeoutException e) {
        // Expected
    }
    return 0;
}
```
先绑定端口，再接受连接。这个接受连接是伪非阻塞的。因为用于连接的线程只有一个，没有客户端连进来的时候不能将其阻塞调。客户端连进来了就将这个「连接」交给别的线程处理，每个连接对应一个线程。这样就做到了连接和io处理不冲突。

当然，最后的执行肯定是到这一步，但是具体的执行调用过程可称得上困难重重。仔细回头看这个register0的处理逻辑，发现好像仅仅启动了一个线程，用于不断从队列中取任务执行的死循环而已。似乎没有直接表现出像绑定端口，接受连接的迹象。不能慌，这个老b隐藏得很深。回到最开始的地方，这个仅仅是register，姑且就到这里，先继续往下看，看到底又有什么新发现。

```java
private ChannelFuture doBind(final SocketAddress localAddress) {
    final ChannelFuture regFuture = initAndRegister();
    final Channel channel = regFuture.channel();
    if (regFuture.cause() != null) {
        return regFuture;
    }
    if (regFuture.isDone()) {
        // At this point we know that the registration was complete and successful.
        ChannelPromise promise = channel.newPromise();
        doBind0(regFuture, channel, localAddress, promise);
        return promise;
    } 
    // 省略。。。
}
```

initAndRegister方法经历千山万水终于启动了一个线程，目的就是返回一个ChannelFuture，先不管这个ChannelFuture到底是什么鬼，先将其理解为JDK中的Future的增强实现。一旦这个Future完成了，调用doBind0:

```java
private static void doBind0(
            final ChannelFuture regFuture, final Channel channel,
            final SocketAddress localAddress, final ChannelPromise promise) {
    // This method is invoked before channelRegistered() is triggered.  Give user handlers a chance to set up
    // the pipeline in its channelRegistered() implementation.
    channel.eventLoop().execute(new Runnable() {
        @Override
        public void run() {
            if (regFuture.isSuccess()) {
                channel.bind(localAddress, promise).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } else {
                promise.setFailure(regFuture.cause());
            }
        }
    });
}
```

看到了吧，这个鬼又向队列中添加了一个任务。这个任务核心就是去绑定。想都不用想，这个绑定一定是AbstractChannel中的方法：

```java
@Override
public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
    return pipeline.bind(localAddress, promise);
}
// pipeline的bind有是其默认实现类中的子类TailContext中的实现
@Override
public final ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
    return tail.bind(localAddress, promise);
}
@Override
public ChannelFuture bind(final SocketAddress localAddress, final ChannelPromise promise) {
    // 省略。。。
    final AbstractChannelHandlerContext next = findContextOutbound();
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
        next.invokeBind(localAddress, promise);
    } else {
        safeExecute(executor, new Runnable() {
            @Override
            public void run() {
                next.invokeBind(localAddress, promise);
            }
        }, promise, null);
    }
    return promise;
}
```
最后的bind是最终的核心逻辑。先找OutboundContext：

```java
private AbstractChannelHandlerContext findContextOutbound() {
    AbstractChannelHandlerContext ctx = this;
    do {
        ctx = ctx.prev;
    } while (!ctx.outbound);
    return ctx;
}

```
注意，调用这个方法的是tail，关于pipeline的结构有必要了解一下。![image](https://segmentfault.com/img/bVEPxn?w=2387&h=584)

我们在这个Server初始化的时候添加了handler，比如LoggingHandler等。这些handler都会被添加到tail和head之间。即使你不添加任何handler，netty也会把自己内部的handler添加进去。handler又分为in和out，分别代表入站和出站。这段代码就是找出站的(只有out的才有bind方法)，一直向head方向找（废话，自己都是tail了只能往前找，后面没有了）。找到一个就算数，直接返回这个context。接着就是调用invokeBind方法：

```java
private void invokeBind(SocketAddress localAddress, ChannelPromise promise) {
    if (invokeHandler()) {
        try {
            ((ChannelOutboundHandler) handler()).bind(this, localAddress, promise);
        } catch (Throwable t) {
            notifyOutboundHandlerException(t, promise);
        }
    } else {
        bind(localAddress, promise);
    }
}
```

最终的bind方法在if分支中。具体的执行逻辑为实现了out的handler，例如LoggingHandler：

```java
@Override
public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
    if (logger.isEnabled(internalLevel)) {
        logger.log(internalLevel, format(ctx, "BIND", localAddress));
    }
    ctx.bind(localAddress, promise);
}
```
显然这个handler仅仅只是来打印log的，完事之后又交给父类去执行。而父类依然是那段。因为之前是找到第一个实现out的handler就算数，这里又回到了这个pipeline中，继续往前找，最终会找到head（head不仅是out而且还是in，就是这么屌）。最终调用的是headcontext中的bind，而它的bind却是使用的是unsafe的bind：

```java
@Override
public void bind(
        ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise)
        throws Exception {
    unsafe.bind(localAddress, promise);
}

@Override
public final void bind(final SocketAddress localAddress, final ChannelPromise promise) {
    assertEventLoop();
    if (!promise.setUncancellable() || !ensureOpen(promise)) {
        return;
    }
    // See: https://github.com/netty/netty/issues/576
    if (Boolean.TRUE.equals(config().getOption(ChannelOption.SO_BROADCAST)) &&
        localAddress instanceof InetSocketAddress &&
        !((InetSocketAddress) localAddress).getAddress().isAnyLocalAddress() &&
        !PlatformDependent.isWindows() && !PlatformDependent.maybeSuperUser()) {
        // Warn a user about the fact that a non-root user can't receive a
        // broadcast packet on *nix if the socket is bound on non-wildcard address.
        logger.warn(
                "A non-root user can't receive a broadcast packet if the socket " +
                "is not bound to a wildcard address; binding to a non-wildcard " +
                "address (" + localAddress + ") anyway as requested.");
    }
    // 这个逻辑是有意思的 返回值为 !socket.isClosed()&& socket.isBound()
    // 没关且绑定了才为true 这里一定为false 因为肯定没绑定
    boolean wasActive = isActive();
    try {
    	 // 看到这行代码就够了 其他不管
        doBind(localAddress);
    } catch (Throwable t) {
        safeSetFailure(promise, t);
        closeIfClosed();
        return;
    }
	 // 绑定完了isActive()肯定为true
    if (!wasActive && isActive()) {
        // 这段代码也得看
        invokeLater(new Runnable() {
            @Override
            public void run() {
                pipeline.fireChannelActive();
            }
        });
    }

    safeSetSuccess(promise);
}
```
看到doBind就知道怎么回事了，这就是之前所说的OioServerSocketChannel的doBind。终于完成了第一步：绑定端口。
接下来就是监听客户端连接，在invokeLater中将其实现了，一探究竟：

```java
private void invokeLater(Runnable task) {
    try {
        eventLoop().execute(task);
    } catch (RejectedExecutionException e) {
        logger.warn("Can't invoke task later as EventLoop rejected it", e);
    }
}
```
果然，依旧把这个任务放到线程中去执行了。这个任务到底是什么，很重要。代码中只给了一段``pipeline.fireChannelActive()``.看看具体实现吧：

```java
@Override
public final ChannelPipeline fireChannelActive() {
    AbstractChannelHandlerContext.invokeChannelActive(head);
    return this;
}
// context为head 又交给了EventExecutor去执行
static void invokeChannelActive(final AbstractChannelHandlerContext next) {
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
        next.invokeChannelActive();
    } else {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                next.invokeChannelActive();
            }
        });
    }
}
// 调用的是head的实现
private void invokeChannelActive() {
    if (invokeHandler()) {
        try {
            ((ChannelInboundHandler) handler()).channelActive(this);
        } catch (Throwable t) {
            notifyHandlerException(t);
        }
    } else {
        fireChannelActive();
    }
}
// head的channelActive 这里的套路和之前的一样，先调用父类的 继续找pipeline中的handler只不过方向相反（从head到tail） 依次类推 如果某个handler不去调用ctx了，那么事件就到此为止不会传递下去了
@Override
public void channelActive(ChannelHandlerContext ctx) throws Exception {
    ctx.fireChannelActive();
	 // 这段代码是重点
    readIfIsAutoRead();
}
// 父类的fireChannelActive
@Override
public ChannelHandlerContext fireChannelActive() {
    invokeChannelActive(findContextInbound());
    return this;
}
```
最终，一定一定是要做我们在OIO原生编程中的第二步了：接受连接了。

```java
private void readIfIsAutoRead() {
    if (channel.config().isAutoRead()) {
        channel.read();
    }
}
// OioSocketChannel的read 实际上是父类的
@Override
public Channel read() {
    pipeline.read();
    return this;
}
// 调用的是pipeline的read
@Override
public final ChannelPipeline read() {
    tail.read();
    return this;
}
// tail的read
@Override
public ChannelHandlerContext read() {
    final AbstractChannelHandlerContext next = findContextOutbound();
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
        next.invokeRead();
    } else {
        Runnable task = next.invokeReadTask;
        if (task == null) {
            next.invokeReadTask = task = new Runnable() {
                @Override
                public void run() {
                    next.invokeRead();
                }
            };
        }
        executor.execute(task);
    }

    return this;
}
```

看到这里我又打脸了，还有这么多层的调用！但是不要慌，因为逻辑是类似的。都是在pipeline这条链上找handler来调用，爱调不调的思想。这里的顺序是从tail到head。如果这个链中有哪个不长眼的没有将事件传递下去，那么最终就到不了head。正常情况下是一定要到head的。

```java
@Override
public void read(ChannelHandlerContext ctx) {
    unsafe.beginRead();
}
// 什么都得考unsafe
@Override
public final void beginRead() {
    assertEventLoop();
    if (!isActive()) {
        return;
    }
    try {
        doBeginRead();
    } catch (final Exception e) {
        invokeLater(new Runnable() {
            @Override
            public void run() {
                pipeline.fireExceptionCaught(e);
            }
        });
        close(voidPromise());
    }
}
// 最终还是将其丢给了eventLoop去执行 readTask是核心
@Override
protected void doBeginRead() throws Exception {
    if (readPending) {
        return;
    }
    readPending = true;
    eventLoop().execute(readTask);
}
```
这个readTask先将其定义好了，没有直接使用匿名内部类。一股清流啊！

```java
private final Runnable readTask = new Runnable() {
    @Override
    public void run() {
        doRead();
    }
};

```

这个doRead有2个实现AbstractOioByteChannel和AbstractOioMessageChannel看名字都能知道区别，一个是读字节一个是读对象。最大的区别是OioByteStreamChannel是OioSocketChannel的父类而AbstractOioMessageChannel是OioServerSocketChannel的父类。这里使用的实现不用说也知道了。

```java
@Override
protected void doRead() {
    // 太多省略不看
    final ChannelConfig config = config();
    final ChannelPipeline pipeline = pipeline();
    final RecvByteBufAllocator.Handle allocHandle = unsafe().recvBufAllocHandle();
    allocHandle.reset(config);
    boolean closed = false;
    Throwable exception = null;
    try {
        do {
            // Perform a read. 关键点
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
    // 不看
}
```

最终这个doReadMessages就是OioServerSocketChannel的实现。将监听客户端连接也放到了任务队列中，让线程去轮询。至于怎么去把消息读出来以及这个过程是怎样的，这是以后的事情。因为这次基本上将整个netty的核心组件都接触到了。接下来的源码解读会稍微轻松点。

### 总结
Netty真屌，不接受反驳😂！