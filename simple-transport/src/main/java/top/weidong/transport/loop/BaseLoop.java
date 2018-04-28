package top.weidong.transport.loop;

import java.util.Queue;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/04/28
 * Time: 14:02
 */
public abstract class BaseLoop {

    private final Queue<Runnable> taskQueue;

    private final int maxPendingTasks;

    private Thread thread;

    private LoopRejectedExecutionHandler defaultHandler = new LoopRejectedExecutionHandler(){
        @Override
        public void rejectedExecution(Runnable r, BaseLoop executor) {
            throw new RejectedExecutionException("Task " + r.toString() +
                    " rejected from " +
                    executor.toString());
        }
    };

    public BaseLoop(int maxPendingTasks) {
        this.maxPendingTasks = Math.max(16, maxPendingTasks);
        this.taskQueue = newTaskQueue(this.maxPendingTasks);
    }

    protected abstract void run();

    protected void startThread() {
        run();
    }

    protected void addTask(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        if (!offerTask(task)) {
            reject(task);
        }
    }

    final boolean offerTask(Runnable task) {
        return taskQueue.offer(task);
    }

    protected final void reject(Runnable task) {
        defaultHandler.rejectedExecution(task,this);
    }


    public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        boolean inEventLoop = inEventLoop();
        // 在同一个线程中 将任务方到队列中 不开启额外线程
        if (inEventLoop) {
            addTask(task);
        } else {
            // 开启单独的线程
            startThread();
            addTask(task);
        }
    }

    /**
     * 取任务 队列中不为空才返回
     * @return
     */
    protected Runnable takeTask() {
        if (!(taskQueue instanceof BlockingQueue)) {
            throw new UnsupportedOperationException();
        }
        BlockingQueue<Runnable> taskQueue = (BlockingQueue<Runnable>) this.taskQueue;
        for (; ; ) {
            Runnable task = taskQueue.poll();
            if (task != null) {
                return task;
            }
        }
    }

    public boolean inEventLoop() {
        return inEventLoop(Thread.currentThread());
    }

    public boolean inEventLoop(Thread thread) {
        return thread == this.thread;
    }

    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return new LinkedBlockingQueue<Runnable>(maxPendingTasks);
    }

    interface LoopRejectedExecutionHandler {
        void rejectedExecution(Runnable r,BaseLoop baseLoop);
    }
}
