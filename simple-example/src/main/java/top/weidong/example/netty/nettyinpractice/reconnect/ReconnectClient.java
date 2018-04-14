package top.weidong.example.netty.nettyinpractice.reconnect;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;
import io.netty.util.ReferenceCountUtil;
import top.weidong.example.netty.nettyinpractice.nio.AbstractNioClient;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description: 重连客户端
 *
 * @author dongwei
 * @date 2018/04/12
 * Time: 16:15
 */
public class ReconnectClient extends AbstractNioClient {

    private Bootstrap bootstrap;

    protected final HashedWheelTimer timer = new HashedWheelTimer();

    public static void main(String[] args) throws InterruptedException {
        new ReconnectClient().connect(9999,"localhost");
    }

    @Override
    protected ChannelHandler[] addHandlers() {
        return new ChannelHandler[] {

        };
    }

    @Override
    protected void connect(int port, String host) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(NioSocketChannel.class);

        final ConnectionWatchdog watchdog = new ConnectionWatchdog(bootstrap,timer,port,host,true,12) {
            @Override
            public ChannelHandler[] addHandlers() {
                return new ChannelHandler[] {
                        this,
                        new HeartBeatsHandler(),
                        new IdleStateHandler(0,4,0, TimeUnit.SECONDS),
                        new StringDecoder(),
                        new StringEncoder(),
                        new ConnectorIdleStateTrigger()

                };
            }
        };
        ChannelFuture future;

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(watchdog.addHandlers());
            }
        });

        future = bootstrap.connect(host,port);

        future.sync();

    }

    private static class HeartBeatsHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("HeartBeatsHandler channelActive : " + new Date());
            ctx.fireChannelActive();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("HeartBeatsHandler channelInactive : [" + new Date() + "] 链路关闭！");
            ctx.close();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            String message = (String) msg;
            try {
                if (message.equals("beats")) {
                    System.out.println("this message is a heartbeats...");
                    return;
                }
                System.out.println("has read message from server : " + message);
            } finally {
                ReferenceCountUtil.release(msg);
            }
        }
    }
}
