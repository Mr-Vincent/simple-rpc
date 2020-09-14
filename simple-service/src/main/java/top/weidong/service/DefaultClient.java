package top.weidong.service;

import top.weidong.common.util.ExceptionUtil;
import network.ConnectionWatcher;
import network.Directory;
import network.SClient;
import top.weidong.registry.RegistryService;
import top.weidong.registry.zk.ZookeeperRegistryService;

import java.io.IOException;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/22
 * Time: 11:41
 */
public class DefaultClient {

    private SClient client;

    private RegistryService registryService;

    public DefaultClient() {
        this.registryService = new ZookeeperRegistryService();
    }

    public ConnectionWatcher watchConnections(Directory directory) throws IOException {
        //订阅-这里指的仅仅是获取注册中心对应节点的数据而已 并没有实际上的订阅操作
        registryService.subscribe(directory);

        //从注册中心对应目录节点中获取服务地址，客户端连接这个地址
        String serverAddress = registryService.getServiceAddress();
        final String[] split = serverAddress.split(":");
        final boolean available = true;
        return new ConnectionWatcher() {
            @Override
            public boolean waitForAvailable(final long timeoutMillis) {
                try {
                    client.connect(split[0],Integer.valueOf(split[1]), new Long(timeoutMillis).intValue());
                } catch (IOException e) {
                    ExceptionUtil.throwException(e);
                    return false;
                }
                return available;
            }
        };
    }


    public void connectToRegistryServer(String connectString) {
        registryService.connectToRegistryServer(connectString);
    }
    /**
     * 添加客户端
     * @param client
     * @return
     */
    public DefaultClient withClient(SClient client){
        this.client = client;
        return this;
    }

    public Socket getSocket(){
        return client.getSocket();
    }

}
