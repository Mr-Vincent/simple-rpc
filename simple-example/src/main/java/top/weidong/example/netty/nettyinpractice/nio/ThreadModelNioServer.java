package top.weidong.example.netty.nettyinpractice.nio;

import io.netty.channel.ChannelHandler;

/**
 * Created with IntelliJ IDEA.
 * Description: NIO线程模型
 *
 * 和oio一样 boss数量超过1是没意义的除非你要绑定的端口不止一个
 *
 * 如果仅设置boss 那么worker就是boss 也就是这个线程不仅处理连接也要处理io
 *
 * 如果仅设置boss 且值设置为1的话，是允许多个客户端连接的，但是前提是如果有处理一个客户端的阻塞读事件的话 其他连接请求就会被hold住直到这个阻塞事件被处理完
 *
 * 如果设置了worker worker的数量也就是线程池的数量，不给参数默认为0 但实际大小为cpu*2 每一个客户端连接到server都会创建一个thread直到最大数量。
 * 和oio相比这种模式就不会让线程无限增加。而连接断开后线程也不会wait依旧running。
 *
 * 举个例子，当worker设置为1的时候，客户端的io处理就得排队，一个一个来。如果worker有多个客户端的io就可以从中取出可用的（空闲）线程去处理任务。
 *
 * 因此耗时任务最好不要在worker这里处理，这里主要负责的是读和写。最好另开线程去处理耗时，将这些宝贵的io线程让给最需要的连接来发送/读取数据。
 *
 *
 * @author dongwei
 * @date 2018/04/17
 * Time: 13:40
 */
public class ThreadModelNioServer extends AbstractNioServer{
    @Override
    protected ChannelHandler[] addHandlers() {
        return new ChannelHandler[]{
        };
    }

    public static void main(String[] args) throws InterruptedException {
        new ThreadModelNioServer().bossAndWorkerCntSettings(1,1).run(false);
    }
}
