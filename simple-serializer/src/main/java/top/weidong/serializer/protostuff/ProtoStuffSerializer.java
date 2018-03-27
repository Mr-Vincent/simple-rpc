package top.weidong.serializer.protostuff;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import top.weidong.common.util.ExceptionUtil;
import top.weidong.common.util.Maps;
import top.weidong.serializer.Serializer;
import top.weidong.serializer.enums.SerializerType;

import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 * Description: protostuff序列化实现
 *
 * @author dongwei
 * @date 2018/03/27
 * Time: 15:00
 */
public class ProtoStuffSerializer extends Serializer {

    private static final Objenesis objenesis = new ObjenesisStd(true);

    private static final ConcurrentMap<Class<?>, Schema<?>> schemaCache = Maps.newConcurrentMap();

    /** 目的是复用 ByteArrayOutputStream 中的 byte[]*/
    private static final ThreadLocal<LinkedBuffer> bufThreadLocal = new ThreadLocal<LinkedBuffer>() {
        @Override
        protected LinkedBuffer initialValue() {
            return LinkedBuffer.allocate(DEFAULT_BUF_SIZE);
        }
    };

    @Override
    public byte code() {
        return SerializerType.PROTO_STUFF.value();
    }

    @Override
    public <T> byte[] writeObject(T obj) {
        Schema<T> schema = getSchema((Class<T>) obj.getClass());
        LinkedBuffer buf = bufThreadLocal.get();
        try {
            return ProtostuffIOUtil.toByteArray(obj, schema, buf);
        } finally {
            buf.clear(); // for reuse
        }
    }

    @Override
    public <T> T readObject(byte[] bytes, int offset, int length, Class<T> clazz) {
        T msg = objenesis.newInstance(clazz);
        Schema<T> schema = getSchema(clazz);
        ProtostuffIOUtil.mergeFrom(bytes, offset, length, msg, schema);
        return msg;
    }


    @SuppressWarnings("unchecked")
    private <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) schemaCache.get(clazz);
        if (schema == null) {
            Schema<T> newSchema = RuntimeSchema.createFrom(clazz);
            schema = (Schema<T>) schemaCache.putIfAbsent(clazz, newSchema);
            if (schema == null) {
                schema = newSchema;
            }
        }
        return schema;
    }

    @Override
    public String toString() {
        return "proto_stuff:(code=" + code() + ")";
    }
}
