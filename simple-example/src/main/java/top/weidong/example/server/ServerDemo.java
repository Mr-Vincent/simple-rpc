package top.weidong.example.server;

import top.weidong.example.ITest;
import top.weidong.example.impl.TestImpl;
import top.weidong.network.SServer;
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
        DefaultServer server = new DefaultServer().withServer(new SServer());
        server.publish(ITest.class.getName(), TestImpl.class);
        server.start();
    }
}
