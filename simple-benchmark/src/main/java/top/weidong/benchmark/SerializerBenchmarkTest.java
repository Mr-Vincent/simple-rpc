package top.weidong.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import top.weidong.example.pojo.Person;
import top.weidong.serializer.SerializationFactory;
import top.weidong.serializer.Serializer;
import top.weidong.serializer.enums.SerializerType;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/27
 * Time: 15:36
 */

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@Measurement(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
@Threads(8)
@OutputTimeUnit(TimeUnit.SECONDS)
public class SerializerBenchmarkTest {

    private static Serializer proto = SerializationFactory.create(SerializerType.PROTO_STUFF);
    private static Serializer jdk = SerializationFactory.create(SerializerType.JAVA);
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(SerializerBenchmarkTest.class.getSimpleName())
                .forks(2).build();
        new Runner(options).run();
    }


    @Benchmark
    public void testProtoStuff() {
        for (int i=0;i<10;i++){
            Person person = new Person("dongwei",25);
            byte[] bytes = proto.writeObject(person);
            Person readObject = proto.readObject(bytes, Person.class);
        }
    }

    @Benchmark
    public void testJdk() {
        for (int i=0;i<10;i++){
            Person person = new Person("dongwei",25);
            byte[] bytes = jdk.writeObject(person);
            Person readObject = jdk.readObject(bytes, Person.class);
        }
    }


}
