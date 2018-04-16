package top.weidong.example.netty.nettyinpractice.nio;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description: reactor单线程模型
 * 验证单线程模型中处理连接的线程和处理io的线程是同一个线程
 *
 * 方法：启动server 使用客户端工具（telnet/netcat）连到server 发送数据后再开一个客户端连接到server
 *
 * 在客户端1发送数据的时候，客户端2是没法连接上的 因为server读数据的时候得花20秒 线程被阻塞在这里
 * 等server读事件结束后客户端2才能连接到server
 *
 * 这个模型在Oio下不成立
 *
 * @author dongwei
 * @date 2018/04/16
 * Time: 15:09
 */
public class SingleThreadNioServer extends AbstractNioServer {
    @Override
    protected ChannelHandler[] addHandlers() {
        return new ChannelHandler[]{
                new BlockedEventHandler()
        };
    }

    public static void main(String[] args) throws InterruptedException {
        new SingleThreadNioServer().run(true);
    }

    /**
     * 模拟一个阻塞handler
     */
    private static class BlockedEventHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
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
