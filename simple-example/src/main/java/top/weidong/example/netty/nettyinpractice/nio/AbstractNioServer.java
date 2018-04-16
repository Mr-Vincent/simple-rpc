package top.weidong.example.netty.nettyinpractice.nio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/04/12
 * Time: 13:51
 */
public abstract class AbstractNioServer {

    private final ServerBootstrap serverBootstrap = new ServerBootstrap();

    private EventLoopGroup boss;
    private EventLoopGroup worker;

    protected abstract ChannelHandler[] addHandlers();

    protected ServerBootstrap eventLoopGroupMode(boolean single){
        boss = new NioEventLoopGroup();
        if (single) {
            boss = new NioEventLoopGroup(1);
            return this.serverBootstrap.group(boss);
        } else {
            worker = new NioEventLoopGroup();
            return this.serverBootstrap.group(boss,worker);
        }
    }

    protected void shutdownGracefully(boolean single){
        if (!single) {
            worker.shutdownGracefully();
        }
        boss.shutdownGracefully();
    }

    protected void run(boolean single) throws InterruptedException {
        try {
            this.eventLoopGroupMode(single)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(addHandlers());
                        }
                    }).option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            ChannelFuture sync = serverBootstrap.bind(9999).sync();
            sync.channel().closeFuture().sync();
        } finally {
            shutdownGracefully(single);
        }
    }
}
