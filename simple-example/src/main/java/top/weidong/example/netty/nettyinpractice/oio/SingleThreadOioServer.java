package top.weidong.example.netty.nettyinpractice.oio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.oio.OioEventLoopGroup;
import top.weidong.example.netty.nettyinpractice.handlers.BlockedEventHandler;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * <p>
 * 本质上而言 oio没有单线程这一说
 *
 * 在设置了worker的情况下boss的值大于1没意义 worker的值如果不指定默认为0表示无限制
 * 这种情况下有一个线程负责客户端连接 这个线程是不会阻塞的，在oio中的实现是对accept设置了超时时间1s（不然这个线程会阻塞住），然后不断轮询将请求派发给io线程处理
 * io线程没有做池化，每个连接绑定一个线程，nio中不是这个机制，nio中使用了池化。
 * 当连接断开，对应的线程会变为wait状态，而nio中依旧是running，这也是因为使用了池化。
 *
 * worker设置为多少就表示最多能有几个客户端连接到server 超过这个数就会抛too many channels异常。
 *
 * 在没设置worker的情况下（boss就是worker），构造器参数maxChannel指的是最大连接数，也不全是。因为自己也需要一个channel。
 * 这就是为什么设置为1的时候客户端连接会报too many channels异常。这个就是处理连接的线程。如果设置为2 那么就只能允许一个客户端连接到server，依次类推。
 * 这种情况下不会出现一个连接处理阻塞的任务其他连接会等着的现象。因为一个连接对应一个线程，相互不会影响。oio这种弊端就是不能复用线程。
 * 思考一下为什么不能像nio一样复用线程？
 * 其实更深层原因是oio的底层机制：读写操作是阻塞的。具体可以debug一下nio的源码。
 *
 * 但是这种将连接和处理io线程独立出来本身就是一件很高明的设计了。
 *
 * @author dongwei
 * @date 2018/04/16
 * Time: 14:43
 */
public class SingleThreadOioServer extends AbstractOioServer {
    @Override
    protected ChannelHandler[] handlers() {
        return new ChannelHandler[]{
                new BlockedEventHandler()
        };
    }

    @Override
    protected ServerBootstrap eventLoopGroupMode(boolean single) {
        boss = new OioEventLoopGroup(1);
        if (single) {
            boss = new OioEventLoopGroup(3);
            return this.serverBootstrap.group(boss);
        } else {
            worker = new OioEventLoopGroup(1);
            return this.serverBootstrap.group(boss, worker);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new SingleThreadOioServer().run(false);
    }

}
