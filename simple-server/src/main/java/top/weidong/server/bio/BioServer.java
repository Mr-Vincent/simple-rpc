package top.weidong.server.bio;

import top.weidong.common.util.NamedThreadFactory;
import top.weidong.server.bio.enums.ProcessorType;
import top.weidong.server.bio.processor.StringProcessor;
import top.weidong.server.bio.task.SimpleTask;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.*;

import static top.weidong.common.util.Preconditions.*;
/**
 * Created with IntelliJ IDEA.
 * Description:
 * BIO server 简单的基于传统io的服务端
 *
 * @author dongwei
 * @date 2018/03/12
 * Time: 21:33
 */
public class BioServer {

    /** 监听端口*/
    private static final int PORT = 8090;

    private final static int WORKER_COUNT = Runtime.getRuntime().availableProcessors();

    private static ExecutorService executor =
            new ThreadPoolExecutor(WORKER_COUNT,
                    WORKER_COUNT,
            0L,
                    TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(),new NamedThreadFactory("ServerWorkerThreadPool"));


    private ServerSocket ss;
    private Socket client;

    private ProcessorType processorType;

    private static final HashMap<String, Class> serviceRegistry = new HashMap<String, Class>();


    public BioServer() {
        try {
            this.ss = new ServerSocket(PORT);
        } catch (IOException e) {
            throw new RuntimeException("服务初始化异常，请检查端口是否被占用");
        }
    }

    public BioServer(int port) {
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
                executor.execute(new SimpleTask(client,processorType,serviceRegistry));
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }
    }

    public void setProcessorType(ProcessorType processorType){
        this.processorType = processorType;
    }


    public void close() throws IOException {
        client.close();
    }

    /**
     * 注册服务 本地保存
     * @param name
     * @param clazz
     */
    public void regisiter(String name,Class clazz){
        serviceRegistry.put(name,clazz);
    }

    public Socket getClient() {
        return client;
    }


}
