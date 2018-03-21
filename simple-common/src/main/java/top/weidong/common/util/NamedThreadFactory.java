package top.weidong.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * Description: 能取名字的线程工厂
 *
 * @author dongwei
 * @date 2018/03/21
 * Time: 16:45
 */
public class NamedThreadFactory implements ThreadFactory{
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private String name;
    private final ThreadGroup group;
    public NamedThreadFactory(String name){
        String defName = "pool-" + poolNumber.getAndIncrement() + "-thread-";
        this.name = StringUtils.isEmpty(name) ? defName : name +"#" +threadNumber.getAndIncrement();
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();

    }
    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                this.name + threadNumber.getAndIncrement(),
                0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY){
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
