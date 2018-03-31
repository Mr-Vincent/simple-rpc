package top.weidong.benchmark;

import top.weidong.common.util.internal.logging.InternalLogger;
import top.weidong.common.util.internal.logging.InternalLoggerFactory;
import top.weidong.example.ITest;
import top.weidong.network.ConnectionWatcher;
import top.weidong.network.Directory;
import top.weidong.network.SClient;
import top.weidong.registry.RegisterMeta;
import top.weidong.service.DefaultClient;
import top.weidong.service.invoker.Invoker;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * Description: 客户端
 *
 * @author dongwei
 * @date 2018/03/23
 * Time: 16:13
 */
public class BenchmarkClient {
    private final static InternalLogger LOGGER = InternalLoggerFactory.getInstance(BenchmarkClient.class);


    public static void main(String[] args) throws Exception {
        Directory directory = RegisterMeta.fromClazz(ITest.class);
        DefaultClient client = new DefaultClient().withClient(new SClient());
        client.connectToRegistryServer("localhost:2181");
        ConnectionWatcher watcher = client.watchConnections(directory);
        if (!watcher.waitForAvailable(3000)) {
            throw new RuntimeException("连接超时！");
        }
        Invoker invoker = new Invoker(client);
        final ITest invoke = invoker.invoke(ITest.class);

//
        for (int i = 0; i < 1000; i++) {
            String str = invoke.say("Vincent");
            System.out.print(str);
        }

        final int t = 1000;
        final int step = 6;
        final int processors = Runtime.getRuntime().availableProcessors();
        long start = System.currentTimeMillis();
        // 64个线程
        final CountDownLatch latch = new CountDownLatch(processors << step);
        final AtomicLong count = new AtomicLong();
        for (int i = 0; i < (processors << step); i++) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    for (int i = 0; i < t; i++) {
                        try {
                            invoke.say("Vincent");
                            if (count.getAndIncrement() % 10000 == 0) {
                                LOGGER.warn("count=" + count.get());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    latch.countDown();
                }
            }).start();
        }
        try {
            latch.await();
            LOGGER.warn("count=" + count.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long second = (System.currentTimeMillis() - start) / 1000;
        LOGGER.warn("Request count: " + count.get() + ", time: " + second + " second, qps: " + count.get() / second);



    }

}
