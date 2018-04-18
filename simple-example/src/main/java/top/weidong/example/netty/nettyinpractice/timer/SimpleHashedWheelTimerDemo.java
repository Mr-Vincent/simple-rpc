package top.weidong.example.netty.nettyinpractice.timer;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description: netty中的时间轮定时器
 *
 * @author dongwei
 * @date 2018/04/18
 * Time: 09:27
 */
public class SimpleHashedWheelTimerDemo {

    private static final HashedWheelTimer TIMER = new HashedWheelTimer();

    private static final HashedWheelTimer CUSTOMIZED_TIMER = new HashedWheelTimer(Executors.defaultThreadFactory(), 5L, TimeUnit.SECONDS, 12);

    private static void run0(Timer timer) {
//        Timeout timeout = timer.newTimeout(new TimerTask() {
//            @Override
//            public void run(Timeout timeout) throws Exception {
//                // process a Time-consuming task
//                Date date = new Date();
//                System.out.println("cycle start :" + date);
//                date.setTime(System.currentTimeMillis() + 20 * 1000);
//                while (true) {
//                    if (System.currentTimeMillis() >= date.getTime()) {
//                        System.out.println("cycle end :" + new Date());
//                        break;
//                    }
//                }
//                System.out.println("timeout 13");
//            }
//        }, 13L, TimeUnit.SECONDS);
//
//        System.out.println(timeout);

//        timer.newTimeout(new TimerTask() {
//            @Override
//            public void run(Timeout timeout) throws Exception {
//                System.out.println("timeout 100");
//            }
//        }, 100L, TimeUnit.SECONDS);



        //timer.stop();
    }

    private static void run1(Timer timer){

        timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {

            }
        }, 1000L, TimeUnit.SECONDS);
//
//        timer.newTimeout(new TimerTask() {
//            @Override
//            public void run(Timeout timeout) throws Exception {
//                System.out.println("task1 will spend 3s ");
//                TimeUnit.SECONDS.sleep(3L);
//                System.out.println("task1:" + new Date());
//            }
//        }, 3L, TimeUnit.SECONDS);
//
//        timer.newTimeout(new TimerTask() {
//            @Override
//            public void run(Timeout timeout) throws Exception {
//                System.out.println("task2 will spend  approximated 0s");
//                System.out.println("task2:" + new Date());
//            }
//        }, 4L, TimeUnit.SECONDS);
    }


    public static void main(String[] args) throws InterruptedException {
//        run0(CUSTOMIZED_TIMER);
        run1(CUSTOMIZED_TIMER);
        System.out.println(Thread.currentThread().getName());
        TimeUnit.SECONDS.sleep(3L);
    }
}
