package top.weidong.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description: 简单的基准测试demo
 *
 * Warmup：预热 iterations：预热轮数
 *
 * Measurement：基本的测试参数 iterations：进行测试的轮次 time：每轮进行的时长
 *
 * Threads：线程数
 *
 * OutputTimeUnit：基准测试结果的时间类型
 *
 * @author dongwei
 * @date 2018/03/22
 * Time: 14:02
 */

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@Measurement(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
@Threads(8)
@OutputTimeUnit(TimeUnit.SECONDS)
public class BenchmarkTest1 {

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(BenchmarkTest1.class.getSimpleName())
                .forks(2).build();
        new Runner(options).run();
    }


    @Benchmark
    public void testStringAdd() {
        String a = "";
        for (int i = 0; i < 10; i++) {
            a += i;
        }
        print(a);
    }

    @Benchmark
    public void testStringBuilderAdd() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(i);
        }
        print(sb.toString());
    }

    private void print(String a) {
    }
}
