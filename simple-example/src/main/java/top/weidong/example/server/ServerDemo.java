package top.weidong.example.server;

import top.weidong.example.ITest;
import top.weidong.example.impl.TestImpl;
import top.weidong.network.SServer;
import top.weidong.registry.RegistryService;
import top.weidong.registry.zk.ZookeeperRegistryService;
import top.weidong.service.DefaultServer;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/21
 * Time: 16:14
 */
public class ServerDemo {

    public static void main(String[] args) throws IOException {
        int port = 18866;
        RegistryService registryService = new ZookeeperRegistryService();
        registryService.connectToRegistryServer("localhost:2181");
        DefaultServer server = new DefaultServer(registryService,port).withServer(new SServer(port));
        server.publish(ITest.class,new TestImpl());
        server.start();
    }
}
