package top.weidong.example;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import org.junit.Test;
import top.weidong.example.impl.TestImpl;
import top.weidong.service.proxy.Proxies;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/29
 * Time: 11:58
 */
public class BytebuddyTest {

    @Test
    public void genericTest(){
        ITest test = Proxies.BYTE_BUDDY_PROXY.newProxy(ITest.class, new Object() {
            @RuntimeType
            public Object byteBuddyInvoke(@This Object proxy, @Origin Method method, @AllArguments @RuntimeType Object[] args) throws Throwable {
                return method.invoke(new TestImpl(), args);
            }
        });
        System.out.println(test.say("hahaah"));
    }
}
