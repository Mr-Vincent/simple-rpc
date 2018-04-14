package top.weidong.example.netty.nettyinpractice.oio;

import io.netty.channel.ChannelHandler;
import top.weidong.example.netty.nettyinpractice.handlers.GenericHandler;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/04/13
 * Time: 16:23
 */
public class OioServer extends AbstractOioServer {
    @Override
    protected ChannelHandler[] handlers() {
        return new ChannelHandler[] {
            new GenericHandler()
        };
    }

    public static void main(String[] args) throws InterruptedException {
        new OioServer().run();
    }
}
