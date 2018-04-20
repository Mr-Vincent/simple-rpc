package top.weidong.example.netty.nettyinpractice.timer;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

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

    public static void main(String[] args) {

        Timer timer = new Timer("timer-demo");

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("task executed");
                System.out.println(new Date());
            }
        },5*1000,3*1000);

        System.out.println("timer started at: "+new Date());

    }
}
