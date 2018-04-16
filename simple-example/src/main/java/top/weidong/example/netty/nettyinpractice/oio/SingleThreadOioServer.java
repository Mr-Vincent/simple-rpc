package top.weidong.example.netty.nettyinpractice.oio;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created with IntelliJ IDEA.
 * Description:
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

    public static void main(String[] args) throws InterruptedException {
        new SingleThreadOioServer().run();
    }

    private static class BlockedHandler extends ChannelInboundHandlerAdapter{
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


        }
    }
}
