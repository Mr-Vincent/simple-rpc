package top.weidong.example.metrics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author dongwei
 * @since 2019/12/06
 * Time: 10:57
 */
public class QueueManager {
    private final Queue queue;

    public Queue getQueue() {
        return queue;
    }

    public QueueManager(MetricRegistry metrics, String name) {
        this.queue = new LinkedBlockingQueue();
        metrics.register(MetricRegistry.name(QueueManager.class, name, "size"),
                new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return queue.size();
                    }
                });
    }
}
