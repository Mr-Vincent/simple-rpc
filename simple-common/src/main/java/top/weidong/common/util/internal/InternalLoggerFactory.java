package top.weidong.common.util.internal;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/13
 * Time: 22:25
 */
public abstract class InternalLoggerFactory {

    private static volatile InternalLoggerFactory defaultFactory =
            newDefaultFactory(InternalLoggerFactory.class.getName());

    /**
     * 优先使用slf4j的日志实现 没有实现那就算了 再用jdk的日志实现
     * @param name
     * @return
     */
    private static InternalLoggerFactory newDefaultFactory(String name) {
        InternalLoggerFactory f;
        try {
            f = new Slf4JLoggerFactory(true);
            f.newInstance(name).debug("Using SLF4J as the default logging framework");
        } catch (Throwable t) {
            f = new JdkLoggerFactory();
            f.newInstance(name).debug("Using java.util.logging as the default logging framework");
        }
        return f;
    }

    public static InternalLogger getInstance(String name) {
        return getDefaultFactory().newInstance(name);
    }

    public static InternalLogger getInstance(Class<?> clazz) {
        return getInstance(clazz.getName());
    }

    public static InternalLoggerFactory getDefaultFactory() {
        return defaultFactory;
    }

    protected abstract InternalLogger newInstance(String name);
}
