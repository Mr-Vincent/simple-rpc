package top.weidong.example.test;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/26
 * Time: 16:57
 */
public class FutureTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ExecutorService executorService = Executors.newCachedThreadPool();
//        Future<Integer> future = executorService.submit(new MyCallable());

        Future<?> submit = executorService.submit(new MyRunnable());
        System.out.println("哈哈哈 我可以继续执行");
//        System.out.println(future.get());
        executorService.shutdown();


    }

    static class MyCallable implements Callable<Integer>{

        @Override
        public Integer call() throws Exception {
            Thread.sleep(10000L);
            return 10;
        }
    }

    static class MyRunnable implements Runnable{
        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(10);
                System.out.println("runnable");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
