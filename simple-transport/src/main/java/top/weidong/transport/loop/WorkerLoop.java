package top.weidong.transport.loop;

import top.weidong.transport.handler.Handler;
import top.weidong.transport.loop.BaseLoop;

import java.net.Socket;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/04/28
 * Time: 12:52
 */
public class WorkerLoop extends BaseLoop {

    private ThreadFactory threadFactory;

    public void processIO(List<Socket> buf, final List<Handler> handlers) {
        for (final Socket s: buf) {
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    doProcessIO(s,handlers);
                }
            };
            execute(task);
        }
    }

    private void doProcessIO(Socket socket,List<Handler> handlers) {
        for (Handler handler : handlers) {
            handler.process(socket);
        }
    }

    public WorkerLoop(ThreadFactory threadFactory) {
        this(16);
        this.threadFactory = threadFactory;
    }

    public WorkerLoop() {
        this(Executors.defaultThreadFactory());
    }

    public WorkerLoop(int maxPendingTasks) {
        super(maxPendingTasks);
    }

    @Override
    protected void run() {
        threadFactory.newThread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    Runnable task = takeTask();
                    if (task != null) {
                        task.run();
                    }
                }
            }
        }).start();
    }
}
