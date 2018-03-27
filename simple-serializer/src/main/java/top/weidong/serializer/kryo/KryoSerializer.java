package top.weidong.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import io.protostuff.LinkedBuffer;
import org.objenesis.strategy.StdInstantiatorStrategy;
import top.weidong.serializer.Serializer;
import top.weidong.serializer.enums.SerializerType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/27
 * Time: 17:32
 */
public class KryoSerializer extends Serializer{
    private static ConcurrentMap<Class<?>,Object> useJavaSerializerTypes = new ConcurrentHashMap();

    private static final ThreadLocal<Kryo> kryoThreadLocal = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            for (Class<?> type : useJavaSerializerTypes.keySet()) {
                kryo.addDefaultSerializer(type, JavaSerializer.class);
            }
            kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
            kryo.setRegistrationRequired(false);
            kryo.setReferences(false);
            return kryo;
        }
    };

    // 目的是复用 Output 中的 byte[]
    private static final ThreadLocal<Output> outputThreadLocal = new ThreadLocal<Output>() {
        @Override
        protected Output initialValue() {
            return new Output(DEFAULT_BUF_SIZE, -1);
        }
    };

    @Override
    public byte code() {
        return SerializerType.KRYO.value();
    }

    @Override
    public <T> byte[] writeObject(T obj) {
        Output output = outputThreadLocal.get();
        try {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, obj);
            return output.toBytes();
        } finally {
            output.clear();
            // 防止hold过大的内存块一直不释放
            if (output.getBuffer().length > MAX_CACHED_BUF_SIZE) {
                output.setBuffer(new byte[DEFAULT_BUF_SIZE], -1);
            }
        }
    }

    @Override
    public <T> T readObject(byte[] bytes, int offset, int length, Class<T> clazz) {
        Input input = new Input(bytes, offset, length);
        Kryo kryo = kryoThreadLocal.get();
        return kryo.readObject(input, clazz);
    }

    @Override
    public String toString() {
        return "kryo:(code=" + code() + ")";
    }
}
