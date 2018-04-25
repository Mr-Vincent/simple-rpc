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
                // 这里又是一个死循环 轮询客户端的连接 让阻塞操作变为非阻塞（设置了一个超时时间）
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
    	 // 这里的task就是AbstractUnsafe#register0的逻辑
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