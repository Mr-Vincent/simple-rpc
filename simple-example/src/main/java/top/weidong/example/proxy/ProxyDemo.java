package top.weidong.example.proxy;

import top.weidong.example.ITest;
import top.weidong.example.impl.TestImpl;

import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created with IntelliJ IDEA.
 * Description: proxy demo
 *
 * @author dongwei
 * @date 2018/03/21
 * Time: 15:45
 */
public class ProxyDemo {

    public static void main(String[] args) {
        InvocationHandler handler = new MyInvocationHandler(new TestImpl());
        ITest test = (ITest) Proxy.newProxyInstance(ITest.class.getClassLoader(),
                new Class[] { ITest.class },
                handler);
        String world = test.say("world");
        System.out.println(world);
    }

    static class MyInvocationHandler implements InvocationHandler{
        private Object target;
        MyInvocationHandler(Object target){
            this.target = target;
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result = method.invoke(target, args);
            return result;
        }
    }
}
