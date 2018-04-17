package top.weidong.example.netty.nettyinpractice.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * 模拟一个读阻塞的handler
 * @author dongwei
 * @date 2018/04/17
 * Time: 13:42
 */
public class BlockedEventHandler extends ChannelInboundHandlerAdapter {
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
