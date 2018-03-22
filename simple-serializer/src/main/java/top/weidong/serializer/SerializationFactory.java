package top.weidong.serializer;

import top.weidong.serializer.enums.SerializerType;

/**
 * Created with IntelliJ IDEA.
 * Description: 序列化工具
 *
 * @author dongwei
 * @date 2018/03/22
 * Time: 16:35
 */
public class SerializationFactory {

    private final static byte PROTO_STUFF = ((byte) 0x01);
    private final static byte HESSIAN = ((byte) 0x02);
    private final static byte KRYO = ((byte) 0x03);
    private final static byte JAVA = ((byte) 0x04);

    private SerializationFactory() {
    }

    /**
     * 获取默认产品 实际上也只有一个产品😄
     * @return
     */
    public static Serializer getDefaultSerializer() {
        return new JdkSerializer();
    }

    /**
     * 生产一个对象
     * @param type
     * @return
     */
    public static Serializer creator(SerializerType type) {
        int code = type.value();
        switch (code) {
            case JAVA:
                return new JdkSerializer();
            case KRYO:
                return null;
            case HESSIAN:
                return null;
            case PROTO_STUFF:
                return null;
            default:
                return new JdkSerializer();
        }

    }
}
