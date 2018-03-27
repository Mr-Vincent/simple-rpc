package top.weidong.common.util.lock;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/26
 * Time: 23:21
 */
public class LockTest {

    public static void main(String[] args) {
        final MutexLock mutexLock = new MutexLock();
        // ---------------------------------Task one:
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    mutexLock.lock();
                    try {
                        System.out.println(Thread.currentThread().getName() + " acquired successfully!");
                        TimeUnit.SECONDS.sleep(2);
                        System.out.println(Thread.currentThread().getName() + " done!");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        mutexLock.unlock();
                    }
                    break;
                }
            }
        }, "Task one").start();
        // --------------------------------- Task two:
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    mutexLock.lock();
                    try {
                        System.out.println(Thread.currentThread().getName() + " acquired successfully!");
                        TimeUnit.SECONDS.sleep(30);
                        System.out.println(Thread.currentThread().getName() + " done!");

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        mutexLock.unlock();
                    }
                    break;
                }
            }
        }, "Task two").start();
    }
}
