package top.weidong.example.server;

import top.weidong.example.ITest;
import top.weidong.example.impl.TestImpl;
import top.weidong.server.bio.BioServer;
import top.weidong.server.bio.enums.ProcessorType;

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
        BioServer server = new BioServer(9999);
        server.regisiter(ITest.class.getName(), TestImpl.class);
        server.setProcessorType(ProcessorType.RPC);
        server.start();

    }
}
