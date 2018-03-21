package top.weidong.example.client;

import top.weidong.example.ITest;
import top.weidong.server.bio.BioClient;
import top.weidong.server.bio.invoker.InvokeProxy;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Description: 客户端demo
 *
 * @author dongwei
 * @date 2018/03/21
 * Time: 18:07
 */
public class ClientDemo {
    public static void main(String[] args) throws IOException {
        BioClient bioClient = new BioClient();
        bioClient.connect("localhost",9999);
        InvokeProxy invokeProxy = new InvokeProxy(bioClient);
        ITest invoke = invokeProxy.invoke(ITest.class);
        String hello = invoke.say("hello");
        System.out.println(hello);
    }
}
