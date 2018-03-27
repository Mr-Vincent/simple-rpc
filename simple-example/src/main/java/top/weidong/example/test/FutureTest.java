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

        final Thread mainThread = Thread.currentThread();
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    System.in.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LockSupport.unpark(mainThread);
            }
        }).start();
        LockSupport.park();
        System.out.println("======");
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<Integer> future = executorService.submit(new MyCallable());
        System.out.println(future.get());
        executorService.shutdown();


    }

    static class MyCallable implements Callable<Integer>{

        @Override
        public Integer call() throws Exception {
            Thread.sleep(100000L);
            return 10;
        }
    }
}
