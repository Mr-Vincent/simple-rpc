package top.weidong.example.netty.nettyinpractice;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created with IntelliJ IDEA.
 * Description: demo级别的短线重连 看看就好
 *
 * @author dongwei
 * @date 2018/04/12
 * Time: 15:38
 */
public class ReconnectClient extends HeartBeatsClient{

    @Override
    protected void connect(int port, String host) throws InterruptedException {
        ChannelFuture f = null;
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(addHandlers());
                        }
                    });

            f = bootstrap.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } finally {
            // 遇到异常程序不结束掉
            if (null != f) {
                if (f.channel() != null && f.channel().isOpen()) {
                    f.channel().close();
                }
            }
            System.out.println("因为某些原因连接断开，准备重连");
            // 无限递归 有bug
            connect(port, host);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new ReconnectClient().connect(9999,"localhost");
    }
}
