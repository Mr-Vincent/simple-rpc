package top.weidong.common.util.internal;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * Description: JdkLog 工厂实现
 *
 * @author dongwei
 * @date 2018/03/13
 * Time: 22:59
 */
public class JdkLoggerFactory extends InternalLoggerFactory {
    public JdkLoggerFactory() {
    }

    @Override
    protected InternalLogger newInstance(String name) {
        File f = new File(JdkLogger.class.getClass().getResource("/").getPath()+"log.properties");
        try {
            System.setProperty("java.util.logging.config.file",f.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger logger = Logger.getLogger(name);
        return new JdkLogger(logger);
    }
}
