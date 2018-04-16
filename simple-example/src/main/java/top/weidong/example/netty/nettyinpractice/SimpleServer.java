package top.weidong.example.netty.nettyinpractice;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import top.weidong.example.netty.nettyinpractice.nio.AbstractNioServer;


/**
 * Created with IntelliJ IDEA.
 * Description: 最简单的server
 *
 * @author dongwei
 * @date 2018/04/12
 * Time: 11:21
 */
public class SimpleServer extends AbstractNioServer {

    private static final AttributeKey<String> NETTY_CHANNEL_KEY = AttributeKey.valueOf("netty.channel");


    public static void main(String[] args) throws InterruptedException {
        new SimpleServer().run(false);
    }

    @Override
    protected ChannelHandler[] addHandlers() {
        return new ChannelHandler[]{
                new FirstInboundEventHandler(),
                new SecondInboundEventHandler()
        };
    }

    /***
     * 继承自最简单的handler 无需手动释放资源
     * 会根据泛型参数去判断能否读取 不指定编码器的话 类型都为ByteBuf -> PooledUnsafeDirectByteBuf
     *
     * 仅仅处理in事件
     */
    private static class SimpleHandler extends SimpleChannelInboundHandler<ByteBuf> {


        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            System.out.println("received:" + msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

            cause.printStackTrace();
            ctx.close();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            Attribute<String> attr = ctx.attr(NETTY_CHANNEL_KEY);
            String s = attr.get();
            if (null == s) {
                s = attr.setIfAbsent("hahah");
            } else {
                System.out.println("attributeMap contains value");
                System.out.println("this value is " + s);
            }

            System.out.println("SimpleHandler channelActive");
            ctx.fireChannelActive();
        }
    }

    /**
     * 第一个inbound处理器
     */
    private static class FirstInboundEventHandler extends ChannelInboundHandlerAdapter{
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("FirstInboundEventHandler channelActive");

            Attribute<String> attr = ctx.attr(NETTY_CHANNEL_KEY);
            String s = attr.get();
            if (null == s) {
                s = attr.setIfAbsent("FirstInboundEventHandler");
            } else {
                System.out.println("attributeMap contains value");
                System.out.println("this value is " + s);
            }

            ctx.fireChannelActive();
        }
    }

    /**
     * 第二个inbound处理器
     */
    private static class SecondInboundEventHandler extends ChannelInboundHandlerAdapter{
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("SecondInboundEventHandler channelActive");
            Attribute<String> attr = ctx.attr(NETTY_CHANNEL_KEY);
            String s = attr.get();

            if (null == s) {
                s = attr.setIfAbsent("SecondInboundEventHandler");
            } else {
                System.out.println("attributeMap contains value");
                System.out.println("this value is " + s);
            }

            ctx.fireChannelActive();
        }
    }

}
