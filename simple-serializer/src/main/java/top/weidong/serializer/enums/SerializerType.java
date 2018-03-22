package top.weidong.serializer.enums;

/**
 * Created with IntelliJ IDEA.
 * Description: 序列化类型枚举
 *
 * @author dongwei
 * @date 2018/03/22
 * Time: 16:11
 */
public enum SerializerType {
    PROTO_STUFF ((byte) 0x01),
    HESSIAN     ((byte) 0x02),
    KRYO        ((byte) 0x03),
    JAVA        ((byte) 0x04)
    // ...
    ;

    SerializerType(byte value) {
        if (0x00 < value && value < 0x10) {
            this.value = value;
        } else {
            throw new IllegalArgumentException("out of range(0x01 ~ 0x0f): " + value);
        }
    }

    private final byte value;

    public byte value() {
        return value;
    }

    public static SerializerType parse(String name) {
        for (SerializerType s : values()) {
            if (s.name().equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }

    public static SerializerType parse(byte value) {
        for (SerializerType s : values()) {
            if (s.value() == value) {
                return s;
            }
        }
        return null;
    }

    public static SerializerType getDefault() {
        return PROTO_STUFF;
    }
}
