package top.weidong.example.netty.nettyinpractice.reconnect;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import top.weidong.example.netty.nettyinpractice.HeartBeatsServer;
import top.weidong.example.netty.nettyinpractice.handlers.AcceptorIdleStateTrigger;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/04/12
 * Time: 15:53
 */
public class ReconnectServer extends HeartBeatsServer {


    public static void main(String[] args) throws InterruptedException {
        new ReconnectServer().run(false);
    }

    @Override
    protected ChannelHandler[] addHandlers() {
        return new ChannelHandler[] {
                new StringEncoder(),
                new StringDecoder(),
                // 读超时5秒 5秒内没有读事件就触发读超时事件
                new IdleStateHandler(5,0,0, TimeUnit.SECONDS),
                new AcceptorIdleStateTrigger(),
                new HeartBeatsHandler()
        };
    }



    private static class HeartBeatsHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("channelRead");
            System.out.println(ctx.channel().remoteAddress() + " -> from server message【"+ new Date()+"】:  " +msg.toString() );
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channelInactive. remote endpoint " + ctx.channel().remoteAddress() +" was disconnected ");
        }
    }

}
