package top.weidong.example.server;

import top.weidong.network.SServer;
import top.weidong.network.enums.ProcessorType;
import top.weidong.service.DefaultServer;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/22
 * Time: 18:53
 */
public class EchoServer {
    public static void main(String[] args) throws IOException {
        DefaultServer server = new DefaultServer(ProcessorType.CONSOLE).withServer(new SServer());
        server.start();
    }
}
