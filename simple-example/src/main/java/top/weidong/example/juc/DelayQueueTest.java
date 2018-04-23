package top.weidong.example.juc;

import java.util.Date;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * juc之delay queue实现 demo
 *
 * 此demo展示了一个最简单的延迟队列的基本使用：
 * 创建20个任务对象，任务的过期时间依次增加，第一个1秒后到期 第二个2秒后到期依次类推
 * 将20个任务取出来，会发现取出的顺序是按照过期时间的长短进行的，最早过期的先取出
 *
 * tips：任务必须实现Delayed接口 实现其中的getDelay和compareTo方法，前者设置过期时间后者设置比较规则。
 *
 * take方法会阻塞：时间没到就等 到了就返回 poll不会阻塞 没到期就直接返回
 *
 * @author dongwei
 * @date 2018/04/23
 * Time: 11:02
 */
public class DelayQueueTest {


    private static void run0() throws InterruptedException {

        DelayQueue<MyTask> myTasks = new DelayQueue<MyTask>();

        for (int i = 0; i < 20; i++) {
            myTasks.add(new MyTask(i+1));
        }


        for (int i = 0; i < 20; i++) {
            System.out.println("current time: " + new Date());
            System.out.println("current data: "+ myTasks.take());
        }


    }

    public static void main(String[] args) throws InterruptedException {
        run0();
    }

    private static class MyTask implements Delayed{

        public MyTask(long trigger) {
            // 目标延迟时间为秒，将其转化为纳秒
            long convert = TimeUnit.NANOSECONDS.convert(trigger, TimeUnit.SECONDS);
            this.trigger = System.nanoTime() + convert;
        }

        private long trigger;


        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(trigger-System.nanoTime(), java.util.concurrent.TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            MyTask that=(MyTask)o;
            if(this.trigger>that.trigger)
                return 1;
            if(this.trigger<that.trigger)
                return -1;
            return 0;
        }

        @Override
        public String toString() {
            return "MyTask{" +
                    "trigger=" + trigger +
                    '}';
        }
    }

}
