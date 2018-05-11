package top.weidong.example.juc.schedual;

import top.weidong.common.util.NamedThreadFactory;

import java.util.Date;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * JUC中的定时器
 *
 * ScheduledExecutorService API中提供了四个接口 ：
 *
 * schedule(Runnable command, long delay, TimeUnit unit) 提交一个Runnable任务，在delay单位时间后执行，有返回值，不过返回值为null。
 *
 * schedule(Callable<V> callable, long delay, TimeUnit unit) 提交一个Callable，在delay单位时间后执行，有返回值，返回类型由任务参数决定。
 *
 * scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) 在initialDelay单位时间后执行，周期为period反复执行，有返回值null。
 *
 * scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) 在initialDelay单位时间后执行，任务完成后的delay单位时间之后继续执行，有返回值null。
 *
 * scheduleAtFixedRate和scheduleWithFixedDelay区别：
 *
 * scheduleWithFixedDelay任务执行间隔是一定的，也就是说上个任务执行完后的delay单位时长后才会执行下个任务
 * 如果任务耗时要比间隔还长，那么就按照任务耗时时间来作为执行间隔
 *
 * scheduleAtFixedRate每个任务开始执行的时间间隔是一定的，第一个任务在initialDelay后执行，第二个在initialDelay+period，第三个在initialDelay + 2 * period...
 * 而不管这个任务到底耗时多久
 *
 * @author dongwei
 * @date 2018/05/11
 * Time: 09:24
 */
public class ScheduleThreadDemo {

    private static ThreadFactory factory = new NamedThreadFactory("SchedualThreadDemo");

    private static ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(4, factory);


    private static void scheduleRun() throws ExecutionException, InterruptedException {
        // 使用Runnable作为任务提交是没有返回值的
        ScheduledFuture<?> schedule = scheduler.schedule(new MyRunnable(true), 10L, TimeUnit.SECONDS);
        scheduler.schedule(new MyRunnable(true), 40L, TimeUnit.SECONDS);
        Object o = schedule.get();
        System.out.println("future get:" + o);
    }

    private static void scheduleCall() throws ExecutionException, InterruptedException {
        ScheduledFuture<String> schedule = scheduler.schedule(new MyCallable(), 1L, TimeUnit.SECONDS);
        String ret = schedule.get();
        System.out.println("future get:" + ret);

    }


    private static void scheduleAtFixedRate() throws ExecutionException, InterruptedException {
        // 间隔为1s 任务耗时为2s 最终间隔为2s
        scheduler.scheduleAtFixedRate(new MyRunnable(true), 1L,1L, TimeUnit.SECONDS);
    }


    private static void scheduleWithFixedDelay() throws ExecutionException, InterruptedException {
        // 间隔为1s 任务耗时2s 最终间隔为3s
        scheduler.scheduleWithFixedDelay(new MyRunnable(true), 1L,1L, TimeUnit.SECONDS);
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        scheduleCall();
        scheduleRun();
//        scheduleAtFixedRate();
//        scheduleWithFixedDelay();
    }

    private static class MyRunnable implements Runnable {
        boolean doWork;
        MyRunnable(boolean doWork){
            this.doWork = doWork;
        }
        @Override
        public void run() {
            if (doWork) {
                Date date = new Date();
                date.setTime(System.currentTimeMillis() + 20 * 1000);
                while (true) {
                    if (System.currentTimeMillis() >= date.getTime()) {
                        System.out.println("cycle end :" + new Date());
                        break;
                    }
                }
            }
            System.out.println(Thread.currentThread().getName());
            System.out.println(new Date());
            System.out.println("MyRunnable");
        }
    }

    private static class MyCallable implements Callable<String> {
        @Override
        public String call() throws Exception {
            System.out.println(Thread.currentThread().getName());
            System.out.println("MyCallable");
            return "return MyCallable";
        }
    }
}
