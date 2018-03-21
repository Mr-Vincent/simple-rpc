package top.weidong.common.util.internal;

import top.weidong.common.util.Loader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
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

        // 加载默认的配置文件 没有配置文件就报错
        URL resource = Loader.getResource("log.properties");
        if (resource != null) {
            System.setProperty("java.util.logging.config.file",resource.getPath());
        } else {
            throw new RuntimeException("logger配置文件不存在！");
        }
        Logger logger = Logger.getLogger(name);
        return new JdkLogger(logger);
    }



}
