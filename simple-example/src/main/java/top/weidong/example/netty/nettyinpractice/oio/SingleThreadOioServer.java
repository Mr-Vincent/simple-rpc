package top.weidong.example.netty.nettyinpractice.oio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.oio.OioEventLoopGroup;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * <p>
 * 本质上而言 oio没有单线程这一说
 *
 * @author dongwei
 * @date 2018/04/16
 * Time: 14:43
 */
public class SingleThreadOioServer extends AbstractOioServer {
    @Override
    protected ChannelHandler[] handlers() {
        return new ChannelHandler[]{
                new BlockedHandler()
        };
    }

    @Override
    protected ServerBootstrap eventLoopGroupMode(boolean single) {
        boss = new OioEventLoopGroup(1);
        if (single) {
            boss = new OioEventLoopGroup(3);
            return this.serverBootstrap.group(boss);
        } else {
            worker = new OioEventLoopGroup();
            return this.serverBootstrap.group(boss, worker);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new SingleThreadOioServer().run(true);
    }

    private static class BlockedHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("current thread name ：" + Thread.currentThread().getName());
            System.out.println("channelRead and remote address is :" + ctx.channel().remoteAddress());
            // start blocking 空转20秒
            Date date = new Date();
            System.out.println("cycle start :" + date);
            date.setTime(System.currentTimeMillis() + 20 * 1000);
            while (true) {
                if (System.currentTimeMillis() >= date.getTime()) {
                    System.out.println("cycle end :" + new Date());
                    break;
                }
            }
            System.out.println("cycle finished");
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channelActive and remote address is :" + ctx.channel().remoteAddress());
        }
    }
}
