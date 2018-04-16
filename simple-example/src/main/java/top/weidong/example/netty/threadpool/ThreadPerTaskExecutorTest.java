package top.weidong.example.netty.threadpool;

import io.netty.util.concurrent.ThreadPerTaskExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

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
        threadPerTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("ThreadPerTaskExecutorTest");
            }
        });

    }
}
