package top.weidong.example.netty.nettyinpractice.reconnect;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/04/12
 * Time: 16:26
 */
@ChannelHandler.Sharable
public class ConnectorIdleStateTrigger extends ChannelInboundHandlerAdapter{
    private final static ByteBuf HEARTBEATS = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("beats", CharsetUtil.UTF_8));
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent)evt;
            if(event.state() == IdleState.WRITER_IDLE) {
                // 检测到写空闲 向服务端发送心跳❤️
                ctx.writeAndFlush(HEARTBEATS.duplicate());
            }
        } else {
            super.userEventTriggered(ctx,evt);
        }
    }
}
