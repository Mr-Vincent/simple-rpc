package top.weidong.benchmark;

import javassist.util.proxy.MethodHandler;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import top.weidong.service.proxy.Proxies;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description: 代理基准性能测试
 * 带业务处理：性能差距不大
 * Benchmark                                       Mode  Cnt  Score   Error   Units
 * ProxyBenchmarkTest.genericASSITInvokeTest      thrpt   10  2.830 ± 0.315  ops/us
 * ProxyBenchmarkTest.genericBYTEBUDDYInvokeTest  thrpt   10  3.038 ± 0.074  ops/us
 * ProxyBenchmarkTest.genericJDKInvokeTest        thrpt   10  3.062 ± 0.053  ops/us
 *
 * 不带业务处理：bytebuddy性能比其他都高出一倍左右
 * Benchmark                                       Mode  Cnt     Score    Error   Units
 * ProxyBenchmarkTest.genericASSITInvokeTest      thrpt   10   509.003 ± 11.863  ops/us
 * ProxyBenchmarkTest.genericBYTEBUDDYInvokeTest  thrpt   10  1118.753 ± 24.387  ops/us
 * ProxyBenchmarkTest.genericJDKInvokeTest        thrpt   10   743.625 ± 22.115  ops/us
 *
 *
 * @author dongwei
 * @date 2018/03/28
 * Time: 19:09
 */
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ProxyBenchmarkTest {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ProxyBenchmarkTest.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }

    static TestInterface testImpl = new TestImlp();

    static class ByteBuddyProxyHandler {

        @SuppressWarnings("UnusedParameters")
        @RuntimeType
        public Object invoke(@Origin Method method, @AllArguments @RuntimeType Object[] args) throws Throwable {
            return method.invoke(testImpl,args);
        }
    }

    static TestInterface assitTest = Proxies.ASSIT.newProxy(TestInterface.class, new MethodHandler() {
        @Override
        public Object invoke(Object self, Method method, Method proceed, Object[] args) throws Throwable {
            return method.invoke(testImpl,args);
        }
    });

    static TestInterface jdkTest = Proxies.JDK_PROXY.newProxy(TestInterface.class, new InvocationHandler() {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(testImpl,args);
        }
    });

    static TestInterface byteBuddyTest = Proxies.BYTE_BUDDY_PROXY.newProxy(TestInterface.class,  new ByteBuddyProxyHandler());

    @Benchmark
    public static void genericASSITInvokeTest(){
        assitTest.say("11111");
    }


    @Benchmark
    public static void genericJDKInvokeTest(){
        jdkTest.say("11111");
    }


    @Benchmark
    public static void genericBYTEBUDDYInvokeTest(){
        byteBuddyTest.say("11111");
    }
}

interface TestInterface {
    String say(String arg);
}

class TestImlp implements TestInterface{
    @Override
    public String say(String arg) {
        return "haha" +arg;
    }
}