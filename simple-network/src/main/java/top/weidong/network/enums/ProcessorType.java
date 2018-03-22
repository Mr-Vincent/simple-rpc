package top.weidong.network.enums;

/**
 * Created with IntelliJ IDEA.
 * Description: 枚举
 *
 * @author dongwei
 * @date 2018/03/21
 * Time: 17:49
 */
public enum ProcessorType {
    CONSOLE,
    ECHO,
    RPC;

    public static ProcessorType parse(String name) {
        for (ProcessorType s : values()) {
            if (s.name().equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }

    public static ProcessorType getDefault() {
        return RPC;
    }
}
