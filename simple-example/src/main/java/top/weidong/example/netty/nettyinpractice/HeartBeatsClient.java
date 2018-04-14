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
import top.weidong.example.netty.nettyinpractice.nio.AbstractNioClient;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/04/12
 * Time: 14:24
 */
public class HeartBeatsClient extends AbstractNioClient {
    @Override
    protected ChannelHandler[] addHandlers() {
        return new ChannelHandler[]{
                // 客户端和服务端端编解码器顺序是相反的
                new StringDecoder(),
                new StringEncoder(),
                // 写超时4秒  因为服务端设置的读超时为5秒 客户端4秒内没有检测到写数据 就发送一个心跳包💓给服务端 服务端收到💓会返回一个响应
                new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS),
                new HeartBeatsHandler()
        };
    }

    public static void main(String[] args) throws InterruptedException {
        new HeartBeatsClient().connect(9999, "localhost");
    }


    private static class HeartBeatsHandler extends ChannelInboundHandlerAdapter {

        private int retryTimes = 0;

        /** 心跳包最大发送次数 发送心跳超过这个次数不再发心跳，主动关闭这个连接*/
        private static final int MAX_TRY_TIMES = 3;

        private final static ByteBuf HEARTBEATS = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("beats", CharsetUtil.UTF_8));

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channelActive : " + new Date());
            ctx.fireChannelActive();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channelInactive : " + new Date());
        }

        // 每隔4s触发一次 对当前事件进行检测
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            System.out.println("userEventTriggered : " + new Date());
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                //相对于客户端就是写超时
                if (event.state() == IdleState.WRITER_IDLE) {
                    if (retryTimes <= MAX_TRY_TIMES) {
                        System.out.println("检测到写超时，这是第 " + retryTimes + "次检测到");
                        retryTimes++;
                        ctx.channel().writeAndFlush(HEARTBEATS.duplicate());
                    }
                }
            }
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            String message = (String) msg;
            if (message.equals("beats")) {
                System.out.println("has read message from server");
            }
            ReferenceCountUtil.release(msg);
        }
    }
}
