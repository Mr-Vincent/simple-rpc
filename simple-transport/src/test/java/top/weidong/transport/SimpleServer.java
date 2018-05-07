package top.weidong.transport;

import org.junit.Test;
import top.weidong.common.util.NamedThreadFactory;
import top.weidong.transport.channel.ServerAcceptorChannel;
import top.weidong.transport.handler.Handler;
import top.weidong.transport.handler.LoggerHandler;
import top.weidong.transport.loop.BossLoop;
import top.weidong.transport.loop.WorkerLoop;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/04/28
 * Time: 11:55
 */
public class SimpleServer {

    @Test
    public void test(){
        System.out.println("====");
    }


    @Test
    public void simpleServerTest() throws Exception {

        NamedThreadFactory factory = new NamedThreadFactory("IO processor");


        ServerAcceptorChannel acceptor = new ServerAcceptorChannel();
        acceptor.bind(9999);

        BossLoop bossLoop = new BossLoop();
        WorkerLoop workerLoop = new WorkerLoop(factory);
        acceptor.handlers(new Handler[]{new LoggerHandler()});
        acceptor.startWithLoop(bossLoop,workerLoop);

        synchronized (acceptor) {
            acceptor.wait();
        }



    }
}
