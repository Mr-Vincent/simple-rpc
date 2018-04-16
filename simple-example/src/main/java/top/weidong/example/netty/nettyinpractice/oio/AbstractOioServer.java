package top.weidong.example.netty.nettyinpractice.oio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created with IntelliJ IDEA.
 * Description: oio server 抽象实现
 *
 * @author dongwei
 * @date 2018/04/13
 * Time: 16:17
 */
public abstract class AbstractOioServer {

    protected final ServerBootstrap serverBootstrap = new ServerBootstrap();


    protected EventLoopGroup boss;
    protected EventLoopGroup worker;


    protected abstract ChannelHandler[] handlers();

    protected ServerBootstrap eventLoopGroupMode(boolean single){
        boss = new OioEventLoopGroup();
        if (single) {
            boss = new OioEventLoopGroup(1);
            return this.serverBootstrap.group(boss);
        } else {
            worker = new OioEventLoopGroup();
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
        // 注意到这个OioEventLoopGroup的构造参数表达的含义和NioEventLoopGroup的参数表达含义是不一样的
        // 这个入参代表最大的channel数量 也就是最大连接数 但是，如果填1任何客户端都连不上 会报异常 必须填客户端数量+1
        try {
            this.eventLoopGroupMode(single)
                    .channel(OioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(handlers());
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
