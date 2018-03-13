package top.weidong.common.util.internal;

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
        return new JdkLogger(Logger.getLogger(name));
    }
}
