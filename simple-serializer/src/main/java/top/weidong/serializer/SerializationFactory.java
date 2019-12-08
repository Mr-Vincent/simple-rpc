package top.weidong.serializer;

import top.weidong.common.util.Maps;
import top.weidong.common.util.internal.logging.InternalLogger;
import top.weidong.common.util.internal.logging.InternalLoggerFactory;
import top.weidong.serializer.enums.SerializerType;

import java.util.Map;
import java.util.ServiceLoader;

/**
 * Created with IntelliJ IDEA.
 * Description: 序列化工具
 *
 * @author dongwei
 * @date 2018/03/22
 * Time: 16:35
 */
public class SerializationFactory {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(SerializationFactory.class);

    private static final Map<Byte, Serializer> serializers = Maps.newConcurrentMap();

    static {
        Iterable<Serializer> all = ServiceLoader.load(Serializer.class);
        for (Serializer s : all) {
            serializers.put(s.code(), s);
        }
        LOGGER.info("Supported serializers: {}.", serializers);
    }

    private SerializationFactory() {
    }

    /**
     * 获取默认产品
     *
     * @return
     */
    public static Serializer getDefaultSerializer() {
        return serializers.get(SerializerType.KRYO.value());
    }

    /**
     * 生产一个对象
     *
     * @param type
     * @return
     */
    public static Serializer create(SerializerType type) {
        Serializer serializer = serializers.get(type.value());
        if (serializer == null) {
            throw new IllegalArgumentException("unsupported serializer type with code: " + type.name());
        }
        return serializer;
    }

}
