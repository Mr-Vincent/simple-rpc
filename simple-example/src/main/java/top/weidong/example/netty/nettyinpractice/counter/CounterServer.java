package top.weidong.example.netty.nettyinpractice.counter;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import top.weidong.example.netty.nettyinpractice.nio.AbstractNioServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: 统计连接的server demo
 *
 * 这种方式是不可取的，因为虽然给这个handler设置为共享的 实例是同一个实例
 * 但是每次客户端连接进来创建的ChannelHandlerContext是不一样的 自然Attribute也一定不一样
 *
 * 这种尝试仅仅只是为了探索Attribute到底怎么玩
 *
 * 具体实现在线人数统计的方法有很多 使用这种（handler2）方式需要注意的是不能设置为共享的handler
 * 因为共享了一个list变量 在并发情况下可能会出现问题
 *
 * @author dongwei
 * @date 2018/04/13
 * Time: 10:24
 */
public class CounterServer extends AbstractNioServer {

    private static final AttributeKey<Integer> COUNTER = AttributeKey.valueOf("counter");

    private CounterHandler2 counterHandler = new CounterHandler2();

    private static List<String> ALL_CHANNEL_IDS = new ArrayList<>();

    @Override
    protected ChannelHandler[] addHandlers() {
        return new ChannelHandler[]{
                new CounterHandler2()
        };
    }

    public static void main(String[] args) throws InterruptedException {
        new CounterServer().run(false);
    }

    @ChannelHandler.Sharable
    private static class CounterHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            //disconnect
            Attribute<Integer> attr = ctx.attr(COUNTER);
            Integer counter = attr.get();
            counter --;
            System.out.println("remote peer :" +ctx.channel().remoteAddress());
            System.out.println("current connection count is " + counter);
            ctx.fireChannelInactive();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            //connect
            Attribute<Integer> attr = ctx.attr(COUNTER);
            Integer counter = attr.get();
            if (null == counter) {
                counter = attr.setIfAbsent(1);
                counter = 1;
            } else {
                attr.set(counter++);
            }
            System.out.println("remote peer :" +ctx.channel().remoteAddress());
            System.out.println("current connection count is " + counter);
            ctx.fireChannelActive();
        }
    }


    private static class CounterHandler2 extends ChannelInboundHandlerAdapter {
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            //disconnect
            ALL_CHANNEL_IDS.remove(ctx.channel().id().asShortText());
            System.out.println("remote peer was disconnected:" +ctx.channel().remoteAddress());
            System.out.println("current connection count is " + ALL_CHANNEL_IDS.size());

            ctx.fireChannelInactive();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            //connect
            ALL_CHANNEL_IDS.add(ctx.channel().id().asShortText());
            System.out.println("remote peer was connected :" +ctx.channel().remoteAddress());
            System.out.println("current connection count is " + ALL_CHANNEL_IDS.size());
            ctx.fireChannelActive();
        }
    }
}
