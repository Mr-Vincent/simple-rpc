package top.weidong.common.util.internal.logging;


import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * jdk 原生log实现 默认只输出info级别的 也就是>=800
 *
 * @author dongwei
 * @date 2018/03/13
 * Time: 23:01
 */
public class JdkLogger extends AbstractInternalLogger {

    final transient Logger logger;

    public JdkLogger(Logger logger) {
        super(logger.getName());
        this.logger = logger;


    }


    @Override
    public boolean isTraceEnabled() {
        return logger.isLoggable(Level.FINEST);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isLoggable(Level.WARNING);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    @Override
    public void trace(String msg) {
        if (isTraceEnabled()) {
            log(SELF, Level.FINEST, msg, null);
        }
    }

    @Override
    public void trace(String format, Object arg) {
        if (isTraceEnabled()) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            log(SELF, Level.FINEST, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public void trace(String format, Object argA, Object argB) {
        if (isTraceEnabled()) {
            FormattingTuple ft = MessageFormatter.format(format, argA,argB);
            log(SELF, Level.FINEST, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (isTraceEnabled()) {
            FormattingTuple ft = MessageFormatter.format(format, arguments);
            log(SELF, Level.FINEST, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (isTraceEnabled()) {
            log(SELF, Level.FINE, msg, t);
        }
    }

    @Override
    public void debug(String msg) {
        if (isDebugEnabled()) {
            log(SELF, Level.FINE, msg, null);
        }
    }

    @Override
    public void debug(String format, Object arg) {
        if (isDebugEnabled()) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            log(SELF, Level.FINE, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public void debug(String format, Object argA, Object argB) {
        if (isDebugEnabled()) {
            FormattingTuple ft = MessageFormatter.format(format, argA,argB);
            log(SELF, Level.FINE, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (isDebugEnabled()) {
            FormattingTuple ft = MessageFormatter.format(format, arguments);
            log(SELF, Level.FINE, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (isDebugEnabled()) {
            log(SELF, Level.FINE, msg, t);
        }
    }

    @Override
    public void info(String msg) {
        if (isInfoEnabled()) {
            log(SELF, Level.INFO, msg, null);
        }
    }

    @Override
    public void info(String format, Object arg) {
        if (isInfoEnabled()) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            log(SELF, Level.INFO, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public void info(String format, Object argA, Object argB) {
        if (isInfoEnabled()) {
            FormattingTuple ft = MessageFormatter.format(format, argA);
            log(SELF, Level.INFO, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public void info(String format, Object... arguments) {
        if (isInfoEnabled()) {
            FormattingTuple ft = MessageFormatter.format(format, arguments);
            log(SELF, Level.INFO, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public void info(String msg, Throwable t) {
        if (isInfoEnabled()) {
            log(SELF, Level.INFO, msg, t);
        }
    }

    @Override
    public void warn(String msg) {
        if (isWarnEnabled()) {
            log(SELF, Level.WARNING, msg, null);
        }
    }

    @Override
    public void warn(String format, Object arg) {
        if (isWarnEnabled()) {
            FormattingTuple tf = MessageFormatter.format(format,arg);
            log(SELF, Level.WARNING, tf.getMessage(), tf.getThrowable());
        }
    }

    @Override
    public void warn(String format, Object argA, Object argB) {
        if (isWarnEnabled()) {
            FormattingTuple tf = MessageFormatter.format(format,argA,argB);
            log(SELF, Level.WARNING, tf.getMessage(), tf.getThrowable());
        }
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (isWarnEnabled()) {
            FormattingTuple tf = MessageFormatter.format(format,arguments);
            log(SELF, Level.WARNING, tf.getMessage(), tf.getThrowable());
        }
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (isWarnEnabled()) {
            log(SELF, Level.WARNING, msg, t);
        }
    }

    @Override
    public void error(String msg) {
        if (isErrorEnabled()) {
            log(SELF, Level.SEVERE, msg, null);
        }
    }

    @Override
    public void error(String format, Object arg) {
        if (isErrorEnabled()) {
            FormattingTuple tf = MessageFormatter.format(format,arg);
            log(SELF, Level.SEVERE, tf.getMessage(), tf.getThrowable());
        }
    }

    @Override
    public void error(String format, Object argA, Object argB) {
        if (isErrorEnabled()) {
            FormattingTuple tf = MessageFormatter.format(format,argA,argB);
            log(SELF, Level.SEVERE, tf.getMessage(), tf.getThrowable());
        }
    }

    @Override
    public void error(String format, Object... arguments) {
        if (isErrorEnabled()) {
            FormattingTuple tf = MessageFormatter.format(format,arguments);
            log(SELF, Level.SEVERE, tf.getMessage(), tf.getThrowable());
        }
    }

    @Override
    public void error(String msg, Throwable t) {
        if (isErrorEnabled()) {
            log(SELF, Level.SEVERE, msg, t);
        }
    }

    static final String SELF = JdkLogger.class.getName();
    static final String SUPER = AbstractInternalLogger.class.getName();


    /**
     * Log the message at the specified level with the specified throwable if any.
     * This method creates a LogRecord and fills in caller date before calling
     * this instance's JDK14 logger.
     * <p/>
     * See bug report #13 for more details.
     */
    private void log(String callerFQCN, Level level, String msg, Throwable t) {
        // millis and thread are filled by the constructor
        LogRecord record = new LogRecord(level, msg);
        record.setLoggerName(name());
        record.setThrown(t);
        fillCallerData(callerFQCN, record);
        logger.log(record);
    }

    /**
     * Fill in caller data if possible.
     *
     * @param record The record to update
     */
    private static void fillCallerData(String callerFQCN, LogRecord record) {
        StackTraceElement[] steArray = new Throwable().getStackTrace();

        int selfIndex = -1;
        for (int i = 0; i < steArray.length; i++) {
            final String className = steArray[i].getClassName();
            if (className.equals(callerFQCN) || className.equals(SUPER)) {
                selfIndex = i;
                break;
            }
        }

        int found = -1;
        for (int i = selfIndex + 1; i < steArray.length; i++) {
            final String className = steArray[i].getClassName();
            if (!(className.equals(callerFQCN) || className.equals(SUPER))) {
                found = i;
                break;
            }
        }

        if (found != -1) {
            StackTraceElement ste = steArray[found];
            // setting the class name has the side effect of setting
            // the needToInferCaller variable to false.
            record.setSourceClassName(ste.getClassName());
            record.setSourceMethodName(ste.getMethodName());
        }
    }
}
