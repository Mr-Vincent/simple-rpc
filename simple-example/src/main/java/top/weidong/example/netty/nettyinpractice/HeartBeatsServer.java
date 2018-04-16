package top.weidong.example.netty.nettyinpractice;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import top.weidong.example.netty.nettyinpractice.nio.AbstractNioServer;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description: 心跳
 *
 * @author dongwei
 * @date 2018/04/12
 * Time: 13:50
 */
public class HeartBeatsServer extends AbstractNioServer {
    @Override
    protected ChannelHandler[] addHandlers() {
        return new ChannelHandler[]{
                new StringEncoder(),
                new StringDecoder(),
                // 读超时5秒 5秒内没有读事件就触发读超时事件
                new IdleStateHandler(5,0,0, TimeUnit.SECONDS),
                new HeartBeatsHandler()
        };

    }

    public static void main(String[] args) throws InterruptedException {
        new HeartBeatsServer().run(false);
    }


    private static class HeartBeatsHandler extends ChannelInboundHandlerAdapter{
        private int loss_connect_time = 0;
        private final static ByteBuf HEARTBEATS = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("beats", CharsetUtil.UTF_8));


        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent)evt;
                if(event.state() == IdleState.READER_IDLE) {
                    loss_connect_time++;
                    System.out.println("5S 内没有收到客户端消息了");
                    if (loss_connect_time > 2) {
                        System.out.println("超过2次超时，把这个连接关闭掉！");
                        ctx.close();
                    }
                }
            } else {
                super.userEventTriggered(ctx,evt);
            }
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("channelRead");
            System.out.println(ctx.channel().remoteAddress() + "-> from server message:  " +msg.toString() );

            // 判断是否是客户端的心跳 是就返回一个响应
            String message = (String) msg;
            if (message.equals("beats")) {
                System.out.println("这是心跳💓信息 ：" + message);
                ctx.channel().writeAndFlush(HEARTBEATS.duplicate());
            }
            ReferenceCountUtil.release(msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

            cause.printStackTrace();
            ctx.close();
        }
    }
}
