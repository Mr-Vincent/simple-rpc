package top.weidong.example.netty.nettyinpractice.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Created with IntelliJ IDEA.
 * Description: 通用的handler 避免每次都写一个静态内部类 烦死人😡
 *
 * @author dongwei
 * @date 2018/04/13
 * Time: 16:24
 */
public class GenericHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        printState(ctx, EventType.ACTIVE);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        printState(ctx, EventType.INACTIVE);
    }

    /**
     * 如果不实现这个方法 默认会将消息丢掉
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        printMsg(ctx,msg,EventType.READ);
        ReferenceCountUtil.release(msg);
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 打印消息
     * @param ctx
     * @param msg
     * @param type
     * @throws Exception
     */
    private void printMsg(ChannelHandlerContext ctx, Object msg,EventType type) throws Exception {
        printState(ctx,type);
        System.out.println("read message from remote peer :" + (ByteBuf)msg);
    }

    /**
     * 打印事件
     * @param ctx
     * @param type
     * @throws Exception
     */
    private void printState(ChannelHandlerContext ctx, EventType type) throws Exception{
        switch (type) {
            case ACTIVE:
                System.out.println("this channel connect");
                break;
            case INACTIVE:
                System.out.println("this channel disconnect");
                break;
            case READ:
                System.out.println("this channel read");
                break;
            default:
                throw new Exception("unknown type");
        }
        System.out.println("channel info : " + ctx.channel());

    }

    enum EventType {
        ACTIVE,
        INACTIVE,
        READ
    }
}
