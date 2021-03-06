package too.weidong.network.bio.task;

import top.weidong.common.util.Preconditions;
import top.weidong.common.util.internal.logging.InternalLogger;
import top.weidong.common.util.internal.logging.InternalLoggerFactory;
import too.weidong.network.bio.processor.Processor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * Description: 任务对象 只关注任务是什么，具体怎么去执行这些细节不管
 *
 * @author dongwei
 * @date 2018/03/21
 * Time: 17:09
 */
public class SimpleTask implements Runnable {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(SimpleTask.class);

    private Socket client = null;

    private Processor processor;

    public SimpleTask(Socket client, Processor processor) {
        Preconditions.checkNotNull(processor);
        this.client = client;
        this.processor = processor;
    }

    @Override
    public void run() {
        String remoteClient = null;
        try {
            InetSocketAddress remoteSocketAddress = (InetSocketAddress) client.getRemoteSocketAddress();
            remoteClient = remoteSocketAddress.getHostName() +":"+ remoteSocketAddress.getPort();
            LOGGER.debug("远程客户端：[{}]",remoteClient);
            boolean flag = true;
            while (flag){
                flag = processor.process(client.getInputStream(), client.getOutputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
                boolean closed = client.isClosed();
                if (closed) {
                    LOGGER.debug("remote client:[{}] closed connection！",remoteClient);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
