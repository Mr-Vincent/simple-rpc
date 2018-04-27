package top.weidong.example.netty.nettyinpractice.eventloop;

import io.netty.channel.oio.OioEventLoopGroup;

import java.util.concurrent.ThreadFactory;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/04/27
 * Time: 10:51
 */
public class MyOioEventLoop extends OioEventLoopGroup {

    public MyOioEventLoop(ThreadFactory threadFactory) {
        super(0, threadFactory);
    }

    public MyOioEventLoop() {
        super(0);
    }

    public MyOioEventLoop(int maxChannels) {
        super(maxChannels);
    }
}
