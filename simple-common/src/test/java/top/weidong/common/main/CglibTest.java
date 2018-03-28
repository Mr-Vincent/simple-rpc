package top.weidong.common.main;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/28
 * Time: 19:30
 */
public class CglibTest {

    @Test
    public void cglibGenericTest(){
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(new CglibInterceptor(new TestImpl()));
        enhancer.setInterfaces(new Class[] { ITest.class });
        ITest test = (ITest) enhancer.create();
        System.out.println(test.say("hahaha"));
    }


    @Test
    public void cglibProxyTest(){

    }


    private static class CglibInterceptor implements MethodInterceptor {

        final Object delegate;

        CglibInterceptor(Object delegate) {
            this.delegate = delegate;
        }

        public Object intercept(Object object, Method method, Object[] objects,
                                MethodProxy methodProxy) throws Throwable {
            return methodProxy.invoke(delegate, objects);
        }
    }
}
