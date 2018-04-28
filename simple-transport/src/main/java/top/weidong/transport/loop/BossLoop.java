package top.weidong.transport.loop;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * 处理socket连接的loop
 *
 * @author dongwei
 * @date 2018/04/28
 * Time: 11:48
 */
public class BossLoop extends BaseLoop{

    private ThreadFactory threadFactory;

    public BossLoop() {
        this(16, Executors.defaultThreadFactory());
    }

    public BossLoop(int maxPendingTasks,ThreadFactory factory) {
        super(maxPendingTasks);
        this.threadFactory = factory;
    }

    public BossLoop(int maxPendingTasks) {
        super(maxPendingTasks);
    }



    @Override
    protected void run() {
        threadFactory.newThread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    Runnable task = takeTask();
                    if (task != null) {
                        task.run();
                    }
                }
            }
        }).start();

    }

    public void doWork(final Runnable task){
        execute(task);
    }




}
