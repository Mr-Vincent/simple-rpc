package top.weidong.example;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.Test;
import top.weidong.example.impl.TestImpl;
import top.weidong.service.proxy.Proxies;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/28
 * Time: 19:40
 */
public class CglibTest {

    @Test
    public void cglibProxyTest(){
       ITest test = Proxies.CGLIB.newProxy(ITest.class, new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                return method.invoke(new TestImpl(),objects);
            }
        });
        System.out.println(test.say("hahahah"));
    }
}
