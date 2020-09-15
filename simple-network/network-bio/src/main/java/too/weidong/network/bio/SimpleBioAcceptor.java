package too.weidong.network.bio;

import com.google.common.collect.Lists;
import top.weidong.common.util.ListenableArrayList;
import top.weidong.common.util.NamedThreadFactory;
import top.weidong.network.api.Acceptor;
import top.weidong.network.api.Channel;
import top.weidong.network.api.ProviderProcessor;
import top.weidong.network.api.ServerSocketFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author dongwei
 * @since 2020/09/14
 * Time: 12:05
 * BIO实现
 */
public class SimpleBioAcceptor implements Acceptor {

    private ServerSocket serverSocket;

    private ServerSocketFactory ssf;

    private boolean running;
    /**
     * 绑定的端口号
     */
    private Integer port;

    private ListenableArrayList<Channel> allChannels = new ListenableArrayList<>();

    private static final Integer DEFAULT_PORT = 9999;

    private ExecutorService executor = new ThreadPoolExecutor(1,1,0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),new NamedThreadFactory("Acceptor"));

    private ProviderProcessor providerProcessor;

    public SimpleBioAcceptor(Integer port) throws IOException {
        this.port = port;
        ssf = new DefaultServerSocketFactory();
    }
    @Override
    public void start() throws IOException {
        running = true;
        serverSocket = ssf.createSocket(this.port, 128);
        executor.execute(new AcceptorTask());
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
        public AcceptorTask(){
        }
        @Override
        public void run() {
            while (running){
                try {
                    Socket socket = ssf.acceptSocket(serverSocket);
                    allChannels.add(BioChannel.ofChannel(socket),new ListenableArrayList.ElementAddListener<Channel>(){
                        @Override
                        public void success(Channel o) {
                            System.out.println("add success: " + o.toString());
                        }
                        @Override
                        public void fail(Channel o, Throwable throwable) {
                            System.out.println("add fail");
                            throwable.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
