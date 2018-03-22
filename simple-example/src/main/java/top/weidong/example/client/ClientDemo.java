package top.weidong.example.client;


import top.weidong.example.ITest;
import top.weidong.network.SClient;
import top.weidong.service.DefaultClient;
import top.weidong.service.invoker.Invoker;

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
        DefaultClient client = new DefaultClient().withClient(new SClient().connect("localhost",9999));
        Invoker invoker = new Invoker(client);
        ITest invoke = invoker.invoke(ITest.class);
        System.out.println(invoke.say("hahah"));

    }
}
