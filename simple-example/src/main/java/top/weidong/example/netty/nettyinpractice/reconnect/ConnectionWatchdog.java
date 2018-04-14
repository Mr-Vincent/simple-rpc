package top.weidong.example.netty.nettyinpractice.reconnect;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description: 断线重连狗
 *
 * @author dongwei
 * @date 2018/04/12
 * Time: 16:01
 */
@ChannelHandler.Sharable
public abstract class ConnectionWatchdog extends ChannelInboundHandlerAdapter implements TimerTask, ChannelHandlerHolder {

    private final Bootstrap bootstrap;
    private final Timer timer;
    private final int port;
    private final String host;

    private volatile boolean reconnect = true;

    private int attempts;
    private static final int MAX_ATTEMPTS = 5;

    public ConnectionWatchdog(Bootstrap bootstrap, Timer timer, int port, String host, boolean reconnect, int attempts) {
        this.bootstrap = bootstrap;
        this.timer = timer;
        this.port = port;
        this.host = host;
        this.reconnect = reconnect;
        this.attempts = attempts;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("channel is active...");
        attempts = 0;
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel is inactive...");
        if (reconnect) {
            //需要重连
            System.out.println("attempt to reconnect...");
            if (attempts < MAX_ATTEMPTS) {
                attempts++;
                int timeout = 2 << attempts;
                System.out.println("第 [" + attempts + "] 次重连.重连间隔为：[" + timeout + "]秒");
                timer.newTimeout(this, timeout, TimeUnit.SECONDS);
            } else {
                System.out.println("重连次数太多了，服务端依旧没上线。还是放弃算了。😣");
                ctx.fireChannelActive();
            }
        }
        ctx.fireChannelActive();
    }

    @Override
    public void run(Timeout timeout) {
        ChannelFuture future;

        synchronized (bootstrap) {
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    // 这里将handler添加进来
                    ch.pipeline().addLast(addHandlers());
                }
            });
            future = bootstrap.connect(host, port);
        }

        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                //回调
                boolean success = future.isSuccess();
                if (!success) {
                    // 失败了将这个channel变为inactive状态，继续重连
                    System.out.println("reconnect fail...");
                    // 继续触发channelInactive事件 触发重连动作
                    future.channel().pipeline().fireChannelInactive();
                } else {
                    System.out.println("reconnect success...");
                }
            }
        });
    }
}
