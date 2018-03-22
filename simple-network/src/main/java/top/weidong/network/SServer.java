package top.weidong.network;

import top.weidong.common.util.NamedThreadFactory;
import top.weidong.network.processor.Processor;
import top.weidong.network.task.SimpleTask;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static top.weidong.common.util.Preconditions.checkArgument;

/**
 * Created with IntelliJ IDEA.
 * Description: simple server 关注接收连接 怎么处理交给task完成
 *
 * @author dongwei
 * @date 2018/03/22
 * Time: 09:31
 */
public class SServer {

    /** 监听端口（默认）*/
    private static final int PORT = 9999;

    /** 工作线程数*/
    private final static int WORKER_COUNT = Runtime.getRuntime().availableProcessors() * 2;

    /** 线程池*/
    private static ExecutorService executor =
            new ThreadPoolExecutor(WORKER_COUNT,
                    WORKER_COUNT,
                    0L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(),new NamedThreadFactory("ServerWorkerThreadPool"));


    private ServerSocket ss;
    private Socket client;

    /** 处理器类型*/
//    private ProcessorType processorType;

    /** 处理器*/
    private Processor processor;

    public SServer() {
        try {
            this.ss = new ServerSocket(PORT);
        } catch (IOException e) {
            throw new RuntimeException("服务初始化异常，请检查端口是否被占用");
        }
    }

    public SServer(int port) {
        // check port is valid
        checkArgument(port>0,"端口号必须大于0");
        try {
            this.ss = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException("服务初始化异常，请检查端口是否被占用");
        }
    }

    public void start() throws IOException {
        while (true){
            try {
                client = ss.accept();
                executor.execute(new SimpleTask(client,processor));
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }
    }


    public void close() throws IOException {
        client.close();
    }


    public Socket getClient() {
        return client;
    }

    public Processor getProcessor() {
        return processor;
    }

    public void withProcessor(Processor processor) {
        this.processor = processor;
    }
}
