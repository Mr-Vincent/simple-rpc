package top.weidong.common.util.internal;

import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLoggerFactory;

import java.io.*;

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
        // 将标准错误输出缓存到buffer
        // 这样每次使用err输出的时候就不会输出到控制台了，而是输出到PrintStream用StringBuffer缓存起来了。
        final StringBuffer buf = new StringBuffer();
        final PrintStream err = System.err;
        try {
            PrintStream ps = new PrintStream(new OutputStream() {
                @Override
                public void write(int b) {
                    buf.append((char) b);
                }
            }, true, "US-ASCII");
            System.setErr(ps);
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
        // 这段字符串不会以err输出到控制台 而是缓存到StringBuffer了。
        System.err.println("hahahaah rinima");
        // 这段代码会产生err的输出 但是因为是静态代码 所以只会执行一次 而这一次却被屏蔽掉了
        try {
            if (LoggerFactory.getILoggerFactory() instanceof NOPLoggerFactory) {
                throw new NoClassDefFoundError(buf.toString());
            } else {
                // err没有被屏蔽，因为这是新的引用
                err.print(buf);
                err.flush();
            }
        } finally {
            // 将err还原 确保不影响的err能正常输出到控制台
            System.setErr(err);
        }
        // 必须先去调用System.err.print方法buffer才会填充内容 不然buf始终为空
//        System.err.println(buf.toString());
    }


    @Override
    protected InternalLogger newInstance(String name) {
        return new Slf4JLogger(LoggerFactory.getLogger(name));
    }
}
