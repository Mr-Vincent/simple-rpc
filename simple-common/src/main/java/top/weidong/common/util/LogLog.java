package top.weidong.common.util;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * 内部log实现 用于输出调试信息
 * copy from log4j
 *
 * @author dongwei
 * @date 2018/03/21
 * Time: 15:31
 */
public class LogLog {
    public static final String DEBUG_KEY = "debug";
    protected static boolean debugEnabled = false;
    private static final String PREFIX = "Debug Log: ";

    static {
        String key = System.getProperty(DEBUG_KEY, "false");
        if (key != null) {
            debugEnabled = toBoolean(key, false);
        }
    }

    public static void setInternalDebugging(boolean enabled) {
        debugEnabled = enabled;
    }


    public static void debug(String msg) {
        if (debugEnabled) {
            System.out.println(PREFIX + msg);
        }
    }

    /**
     * info输出 无需判断开光
     * @param msg
     */
    public static void info(String msg) {
        System.out.println(PREFIX + msg);
    }

    public static void error(String msg) {
        System.err.println(PREFIX + msg);
    }

    /**
     * 不尔转化
     *
     * @param value   true / false
     * @param dEfault default value
     * @return
     */
    public static boolean toBoolean(String value, boolean dEfault) {
        if (value == null) {
            return dEfault;
        }
        String trimmedVal = value.trim();
        if ("true".equalsIgnoreCase(trimmedVal)) {
            return true;
        }
        if ("false".equalsIgnoreCase(trimmedVal)) {
            return false;
        }
        return dEfault;
    }
}
