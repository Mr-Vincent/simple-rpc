package top.weidong.common.util.internal;

import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * Description: slf4j 工厂实现
 *
 * @author dongwei
 * @date 2018/03/13
 * Time: 22:55
 */
public class Slf4JLoggerFactory extends InternalLoggerFactory{
    public Slf4JLoggerFactory() {
    }

    public Slf4JLoggerFactory(boolean arg) {
    }


    @Override
    protected InternalLogger newInstance(String name) {
        return new Slf4JLogger(LoggerFactory.getLogger(name));
    }
}
