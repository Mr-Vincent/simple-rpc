package top.weidong.example.juc;

import top.weidong.common.util.NamedThreadFactory;

import java.util.Date;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * 更加实用的demo 使用线程来具体化任务对象 分离职责 让过期任务和具体业务逻辑分离
 * 这种实现类似于定时器的实现
 * <p>
 * DelayQueue内部维护的是一个优先队列 PriorityQueue
 * PriorityQueue的实现是采用堆作为数据结构来存储的 和Timer中的TaskQueue是类似的 我觉得老爷子Doug Lea借鉴了Timer的实现，后来发现原来作者是他们俩😂 这个包并不属于JUC
 *
 * @author dongwei
 * @date 2018/04/23
 * Time: 11:43
 */
public class DelayQueueTest2 {

    private static void run0() throws InterruptedException {

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory("DelayQueueTestThread"));


        DelayQueue<MyTask<MyRunnable>> myTasks = new DelayQueue<>();

        for (int i = 0; i < 20; i++) {
            myTasks.put(new MyTask<>(i + 1, new MyRunnable(i + 1)));
        }

        // 取出任务 并执行

        while (!myTasks.isEmpty()) {
            MyTask<MyRunnable> task = myTasks.take();
            MyRunnable runnable = task.getTask();
            if (runnable != null) {
                threadPoolExecutor.execute(runnable);
            }
        }

        threadPoolExecutor.shutdown();

    }

    public static void main(String[] args) throws InterruptedException {
        run0();
    }


    /**
     * 具体要执行的任务
     */
    private static class MyRunnable implements Runnable {
        private int i;

        public MyRunnable(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            System.out.println("this task index is " + i);
            System.out.println("current time is " + new Date());
            System.out.println("current thread is " + Thread.currentThread().getName());
        }
    }

    /**
     * 针对山歌版本的改进 让其真正的有执行任务
     *
     * @param <R> 持有的任务
     */
    private static class MyTask<R extends Runnable> implements Delayed {

        private R task;

        public MyTask(long timeout, R task) {
            // 目标延迟时间为秒，将其转化为纳秒
            long convert = TimeUnit.NANOSECONDS.convert(timeout, TimeUnit.SECONDS);
            this.timeout = System.nanoTime() + convert;
            this.task = task;
        }

        private long timeout;

        /**
         * 时间到了就将这个任务取出来
         *
         * @return
         */
        public R getTask() {
            return task;
        }


        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(timeout - System.nanoTime(), java.util.concurrent.TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            MyTask that = (MyTask) o;
            if (this.timeout > that.timeout)
                return 1;
            if (this.timeout < that.timeout)
                return -1;
            return 0;
        }

        @Override
        public String toString() {
            return "MyTask{" +
                    "timeout=" + timeout +
                    '}';
        }
    }
}
