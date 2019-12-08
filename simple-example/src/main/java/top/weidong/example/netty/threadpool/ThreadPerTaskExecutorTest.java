package top.weidong.example.netty.threadpool;

import io.netty.util.concurrent.ThreadPerTaskExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/04/14
 * Time: 14:45
 */
public class ThreadPerTaskExecutorTest {

    public static void main(String[] args) {
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        ThreadPerTaskExecutor threadPerTaskExecutor = new ThreadPerTaskExecutor(threadFactory);
        for (int i=0;i<10;i++) {
            threadPerTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("ThreadPerTaskExecutorTest" + Thread.currentThread().getName());
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

    }
}
