package top.weidong.server.bio.task;

import top.weidong.common.util.Preconditions;
import top.weidong.server.bio.enums.ProcessorType;
import top.weidong.server.bio.processor.RpcProcessor;
import top.weidong.server.bio.processor.StringProcessor;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description: 任务对象
 *
 * @author dongwei
 * @date 2018/03/21
 * Time: 17:09
 */
public class SimpleTask implements Runnable{

    private Socket client = null;

    private ProcessorType type;

    private StringProcessor stringProcessor;

    private RpcProcessor rpcProcessor;

    private Map<String,Class> serviceRegistry;

    public SimpleTask(Socket client,ProcessorType type,Map<String,Class> serviceRegistry){
        Preconditions.checkNotNull(type);
        this.serviceRegistry = serviceRegistry;
        this.client = client;
        this.type = type;
        stringProcessor = new StringProcessor();
        rpcProcessor = new RpcProcessor(serviceRegistry);
    }

    @Override
    public void run() {
        withProcessor(type);
    }

    public void withProcessor(ProcessorType type){
        if (ProcessorType.STRING.equals(type)) {
            try {
                stringProcessor.process(client.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (ProcessorType.RPC.equals(type)){
            try {
                rpcProcessor.process(client.getInputStream(),client.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            throw  new RuntimeException("无法找到可用的processor！");
        }
    }

}
