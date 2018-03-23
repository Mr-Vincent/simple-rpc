package top.weidong.benchmark;

import top.weidong.example.ITest;
import top.weidong.example.impl.TestImpl;
import top.weidong.network.SServer;
import top.weidong.service.DefaultServer;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Description: 服务端
 *
 * @author dongwei
 * @date 2018/03/23
 * Time: 16:11
 */
public class BenchmarkProviderTest {

    public static void main(String[] args) throws IOException {
        DefaultServer server = new DefaultServer().withServer(new SServer());
        server.publish(ITest.class.getName(), TestImpl.class);
        server.start();
    }
}
