package top.weidong.common.util.internal.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Created with IntelliJ IDEA.
 * Description: 日志格式化
 *
 * @author dongwei
 * @date 2018/03/29
 * Time: 16:51
 */
public class JdkSimpleFormatter extends Formatter {
    /**时间格式*/
    private static final String DATE_PATTERN_FULL = "MM-dd HH:mm:ss:SSS";

    private static final String format = "[%1$s - %2$s ]-> [%3$s#%4$s] %5$s%n";

    private static final Date dat = new Date();

    static Map<String,String> levelMapping = new HashMap<>();

    static {
        levelMapping.put("FINEST","TRACE");
        levelMapping.put("FINE","DEBUG");
        levelMapping.put("INFO","INFO");
        levelMapping.put("WARNING","WARN");
        levelMapping.put("SEVERE","ERROR");
    }

    @Override
    public String format(LogRecord record) {
        dat.setTime(record.getMillis());
        String message = formatMessage(record);
        String throwable = "";
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }
        return String.format(format,
                getCurrentDateStr(DATE_PATTERN_FULL),
                levelMapping.get(record.getLevel().getName()),
                record.getSourceClassName(),
                record.getSourceMethodName(),
                message,
                throwable);
    }

    private static String getCurrentDateStr(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(dat);
    }
}
