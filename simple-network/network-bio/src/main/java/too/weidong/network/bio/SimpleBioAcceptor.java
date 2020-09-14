package too.weidong.network.bio;

import com.google.common.collect.Lists;
import top.weidong.common.util.NamedThreadFactory;
import top.weidong.network.api.Acceptor;
import top.weidong.network.api.Channel;
import top.weidong.network.api.ProviderProcessor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author dongwei
 * @since 2020/09/14
 * Time: 12:05
 * BIO实现
 */
public class SimpleBioAcceptor implements Acceptor {

    private ServerSocket serverSocket;
    /**
     * 绑定的端口号
     */
    private Integer port;

    private List<Channel> allChannels = Lists.newArrayList();

    private static final Integer DEFAULT_PORT = 9999;

    private ExecutorService executor = new ThreadPoolExecutor(4,4,0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(100),new NamedThreadFactory("Acceptor"));

    private ProviderProcessor providerProcessor;

    public SimpleBioAcceptor(Integer port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket();
    }
    @Override
    public void start() throws IOException {
        serverSocket.bind(new InetSocketAddress(this.port),128);
        executor.execute(new AcceptorTask(serverSocket));
    }

    @Override
    public void processor(ProviderProcessor processor) {
        this.providerProcessor = providerProcessor;
    }

    @Override
    public List<Channel> allChannels() {
        return allChannels;
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }

    public static Acceptor create() throws IOException {
        return new SimpleBioAcceptor(DEFAULT_PORT);
    }

    class AcceptorTask implements Runnable{
        ServerSocket serverSocket;
        public AcceptorTask(ServerSocket serverSocket){
            this.serverSocket = serverSocket;
        }
        @Override
        public void run() {
            while (true){
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println(Thread.currentThread().getName());
                    allChannels.add(BioChannel.ofChannel(socket));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
