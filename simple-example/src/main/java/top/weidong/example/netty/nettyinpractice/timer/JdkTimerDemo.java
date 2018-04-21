package top.weidong.example.netty.nettyinpractice.timer;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * jdk中的定时器
 * 比hashwheeltimer简单很多
 *
 * @author dongwei
 * @date 2018/04/20
 * Time: 17:30
 */
public class JdkTimerDemo {

    public static void run(){

        Timer timer = new Timer("timer-demo");

        // 任务执行的时间超过了给定的时间间隔，以实际执行的时间为间隔时间
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    System.out.println("task was started at " + new Date());
                    TimeUnit.SECONDS.sleep(6);
                    System.out.println("task was finished at " + new Date());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },5*1000,4*1000);


        System.out.println("timer started at: " + new Date());

    }


    public static void run1() throws IOException {

        Timer timer = new Timer("timer-demo");

        // 无周期 到点就执行 不重复
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("======first task added=====");
            }
        },10*1000);

        // 第一个任务执行完任务队列中就空了 timer线程处于等待状态 再添加一个任务 看timer能不能被唤醒继续执行
        // 实时上一定是可以的 因为这段代码
        // if (queue.getMin() == task)
        //   queue.notify();
        // 如果队列中没有任务了，那么新添加的一定是当前任务 然后将等待的timer唤醒 如果新加的任务到期时间是最近的，也会将当前等待到期的任务唤醒
        System.in.read();

        // 再添加一个任务
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("======second task added=====");
            }
        },5*1000);


        System.out.println("timer started at: " + new Date());


    }

    /**
     * 任务执行完不手动取消 强制触发gc看TimerThread能否结束
     * 方法：在任务跑完之后 attach到jprofile中 点击run gc 可以看到jvm直接退出了 说明jdk的小聪明还是起到效果了
     * @throws IOException
     */
    public static void run2() throws IOException {

        Timer timer = new Timer("timer-demo");

        // 无周期 到点就执行 不重复
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("======first task added=====");
            }
        }, 10 * 1000);


    }

    public static void run0(){
        Timer timer = new Timer("timer-demo");

        MyTask[] tasks = new MyTask[20];

        for (int i = 0; i < tasks.length; i++) {
            tasks[i] = new MyTask(i);
            System.out.println("time no " + i + " task start at " + new Date());
            timer.schedule(tasks[i], 2 * 1000, 3 * 1000);
        }


        System.out.println("timer started at: " + new Date());
    }

    public static void main(String[] args) throws IOException {
        run0();
    }

    private static class MyTask extends TimerTask {
        int timeNo;

        public MyTask(int timeNo) {
            this.timeNo = timeNo;
        }

        /**
         * 如果任务出现异常不被捕获，其他任务不会被执行
         */
        @Override
        public void run() {
            if (timeNo == 8) {
                throw new RuntimeException("boom");
            }
            System.out.println("time no " + timeNo);
        }
    }

}
