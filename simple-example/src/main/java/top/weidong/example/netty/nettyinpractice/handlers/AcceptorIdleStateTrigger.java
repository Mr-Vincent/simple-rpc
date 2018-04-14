package top.weidong.example.netty.nettyinpractice.handlers;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import top.weidong.common.util.Signal;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/04/14
 * Time: 12:35
 */


@ChannelHandler.Sharable
public class AcceptorIdleStateTrigger extends ChannelInboundHandlerAdapter {

    /**
     * Read idle 链路检测
     */
    private static final Signal READER_IDLE = Signal.valueOf(AcceptorIdleStateTrigger.class, "READER_IDLE");

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                throw READER_IDLE;
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
