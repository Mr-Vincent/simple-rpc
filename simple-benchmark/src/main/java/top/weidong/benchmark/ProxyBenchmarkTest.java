package top.weidong.benchmark;

import javassist.util.proxy.MethodHandler;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import top.weidong.example.ITest;
import top.weidong.example.impl.TestImpl;
import top.weidong.service.proxy.Proxies;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description: 代理基准性能测试
 *
 * @author dongwei
 * @date 2018/03/28
 * Time: 19:09
 */

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@Measurement(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
@Threads(8)
@OutputTimeUnit(TimeUnit.SECONDS)
public class ProxyBenchmarkTest {

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(ProxyBenchmarkTest.class.getSimpleName())
                .forks(1).build();
        new Runner(options).run();
    }


    @Benchmark
    public void genericASSITInvokeTest(){
        ITest test = Proxies.ASSIT.newProxy(ITest.class, new MethodHandler() {
            @Override
            public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
                return thisMethod.invoke(new TestImpl(), args);
            }
        });
        test.say("11111");
    }


    @Benchmark
    public void genericJDKInvokeTest(){
        ITest test = Proxies.JDK_PROXY.newProxy(ITest.class, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return method.invoke(new TestImpl(), args);
            }
        });
        test.say("11111");
    }

    @Benchmark
    public void genericCGLIBInvokeTest(){
        ITest test = Proxies.CGLIB.newProxy(ITest.class, new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                return method.invoke(new TestImpl(),objects);
            }
        });
        test.say("11111");
    }
}
