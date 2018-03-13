package top.weidong.common.util.internal;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/13
 * Time: 22:26
 */
public interface InternalLogger {
    /**
     * Return the name of this {@link InternalLogger} instance.
     *
     * @return name of this logger instance
     */
    String name();

    /*================switch defination start=============*/

    /**
     * Is the logger instance enabled for the TRACE level?
     *
     * @return True if this Logger is enabled for the TRACE level,
     * false otherwise.
     */
    boolean isTraceEnabled();

    /**
     * Is the logger instance enabled for the DEBUG level?
     *
     * @return True if this Logger is enabled for the DEBUG level,
     * false otherwise.
     */
    boolean isDebugEnabled();

    /**
     * Is the logger instance enabled for the INFO level?
     *
     * @return True if this Logger is enabled for the INFO level,
     * false otherwise.
     */
    boolean isInfoEnabled();

    /**
     * Is the logger instance enabled for the WARN level?
     *
     * @return True if this Logger is enabled for the WARN level,
     * false otherwise.
     */
    boolean isWarnEnabled();

    /**
     * Is the logger instance enabled for the ERROR level?
     *
     * @return True if this Logger is enabled for the ERROR level,
     * false otherwise.
     */
    boolean isErrorEnabled();

    /**
     * Is the logger instance enabled for the specified {@code level}?
     *
     * @return True if this Logger is enabled for the specified {@code level},
     * false otherwise.
     */
    boolean isEnabled(InternalLogLevel level);

    /*================switch defination end=============*/

    /*=================log level defination start=================*/

    void trace(String msg);

    void trace(String format, Object arg);

    void trace(String format, Object argA, Object argB);

    void trace(String format, Object... arguments);

    void trace(String msg, Throwable t);

    void trace(Throwable t);


    void debug(String msg);

    void debug(String format, Object arg);

    void debug(String format, Object argA, Object argB);

    void debug(String format, Object... arguments);

    void debug(String msg, Throwable t);

    void debug(Throwable t);


    void info(String msg);

    void info(String format, Object arg);

    void info(String format, Object argA, Object argB);

    void info(String format, Object... arguments);

    void info(String msg, Throwable t);

    void info(Throwable t);


    void warn(String msg);

    void warn(String format, Object arg);

    void warn(String format, Object argA, Object argB);

    void warn(String format, Object... arguments);

    void warn(String msg, Throwable t);

    void warn(Throwable t);


    void error(String msg);

    void error(String format, Object arg);

    void error(String format, Object argA, Object argB);

    void error(String format, Object... arguments);

    void error(String msg, Throwable t);

    void error(Throwable t);

    /*=================log level defination end=================*/

    /*========more universal methods defination========*/

    void log(InternalLogLevel level, String msg);

    void log(InternalLogLevel level, String format, Object arg);

    void log(InternalLogLevel level, String format, Object argA, Object argB);

    void log(InternalLogLevel level, String format, Object... arguments);

    void log(InternalLogLevel level, String msg, Throwable t);

    void log(InternalLogLevel level, Throwable t);


}
