package top.weidong.example.metrics;

import com.codahale.metrics.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * @author dongwei
 * @since 2019/12/06
 * Time: 10:35
 */
public class MetricsDemo {

    static final MetricRegistry metrics = new MetricRegistry();

    private static final Counter pendingJobs = metrics.counter(name(QueueManager.class, "pending-jobs"));

    private static final Histogram responseSizes = metrics.histogram(name(QueueManager.class, "response-sizes"));


    public static void main(String[] args) throws InterruptedException, NoSuchAlgorithmException {
//        meters();
//        gauge();
//        counter();
        histogram();
    }

    public static void histogram() throws NoSuchAlgorithmException, InterruptedException {
        startReport();
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        for (int i = 0; i < 100; i++) {
            responseSizes.update(random.nextInt(100));
            TimeUnit.MILLISECONDS.sleep(200);
        }
        wait5Seconds();
    }

    public static void gauge() throws InterruptedException {
        startReport();
        QueueManager queueManager = new QueueManager(metrics, "job");
        for (int i = 0; i < 100; i++) {
            queueManager.getQueue().add(Integer.valueOf(i));
            TimeUnit.MILLISECONDS.sleep(200);
        }
        wait5Seconds();
    }

    public static void counter() throws InterruptedException {
        startReport();
        for (int i = 0; i < 100; i++) {
            pendingJobs.inc();
            TimeUnit.MILLISECONDS.sleep(200);
        }
        wait5Seconds();
    }
    public static void meters() throws InterruptedException {
        startReport();
        Meter requests = metrics.meter("requests");
        for (int i=0;i<100;i++) {
            requests.mark();
            TimeUnit.MILLISECONDS.sleep(200);
        }
        wait5Seconds();
    }

    static void startReport() {
        ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(2, TimeUnit.SECONDS);
    }

    static void wait5Seconds() {
        try {
            Thread.sleep(5*1000);
        }
        catch(InterruptedException e) {}
    }
}
