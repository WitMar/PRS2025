package threadgroup;

import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ScheduledThreadExecutor {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Groups.class);

    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(5);

        service.scheduleWithFixedDelay(() -> {
            logger.info("Working " + Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 100, 100, MILLISECONDS);

        Thread.sleep(10000);
        service.shutdown();
    }

}
