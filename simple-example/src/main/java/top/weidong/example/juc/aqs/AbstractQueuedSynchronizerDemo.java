package top.weidong.example.juc.aqs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * AQS实现原理
 * java.util.concurrent.locks这个包下有3个接口：Condition Lock和ReadWriteLock
 *
 * @author dongwei
 * @date 2018/05/14
 * Time: 10:56
 */
public class AbstractQueuedSynchronizerDemo {
    private static Lock lock = new ReentrantLock(true);
    private static ExecutorService executor = Executors.newFixedThreadPool(5);
    private static int sum = 100;

    public static void sellTicket() {
        for (int i = 0; i < 10; i++) {
            executor.execute(new Task(true));
        }
    }
    public static void main(String[] args) {
        sellTicket();
    }

    private static class Task implements Runnable {
        boolean useLock;
        Task(boolean useLock) {
            this.useLock = useLock;
        }
        @Override
        public void run() {
            if (useLock) {
                lockVer();
            } else {
                unLockVer();
            }
        }
        private void lockVer() {
            while (true) {
                lock.lock();
                try {
                    if (sum == 0) {
                        System.out.println(Thread.currentThread().getName()+" has quit!");
                        break;
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + " sum = " + sum--);
                    System.out.println(lock);
                } finally {
                    lock.unlock();
                }
            }
        }
        private void unLockVer() {
            while (true) {
                if (sum == 0) {
                    System.out.println(Thread.currentThread().getName()+" has quit!");
                    break;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " sum = " + sum--);
            }
        }
    }
}