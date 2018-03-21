package top.weidong.example.proxy;

import top.weidong.common.util.LogLog;
import top.weidong.example.ITest;
import top.weidong.example.impl.TestImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created with IntelliJ IDEA.
 * Description: proxy demo with SimpleProxy
 *
 * @author dongwei
 * @date 2018/03/21
 * Time: 15:45
 */
public class SimpleProxyDemo {

    public static void main(String[] args) {
        ITest test = SimpleProxy.getProxy(ITest.class,new TestImpl());
        String msg = test.say("worldd");
        LogLog.info(msg);
    }


}
