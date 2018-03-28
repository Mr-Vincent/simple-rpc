package top.weidong.example;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.util.proxy.MethodHandler;
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
 * Time: 17:38
 */
public class JavaAssitTest {

    @Test
    public void genericTest() throws Exception {
        ClassPool cp = ClassPool.getDefault();
        CtClass cc = cp.get("top.weidong.example.impl.TestImpl");
        CtMethod m = cc.getDeclaredMethod("say");
        m.insertBefore("{ System.out.println(\"test.say():\"); }");
        Class c = cc.toClass();
        ITest h = (ITest)c.newInstance();
        String hahaha = h.say("hahaha");
        System.out.println(hahaha);
    }

    @Test
    public void genericASSITInvokeTest(){
        ITest test = Proxies.ASSIT.newProxy(ITest.class, new MethodHandler() {
            @Override
            public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
                return thisMethod.invoke(new TestImpl(), args);
            }
        });
        String oooo = test.say("oooo");
        System.out.println(oooo);
    }


}
