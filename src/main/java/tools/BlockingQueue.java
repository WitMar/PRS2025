package tools;

import org.slf4j.LoggerFactory;
import threadgroup.Groups;

import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class BlockingQueue {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Groups.class);

    public static void main(String[] args) {
        LinkedBlockingQueue<Integer> sharedQ = new LinkedBlockingQueue<Integer>();

        CyclicBarrier barrier = new CyclicBarrier(6);

        ExecutorService producers = Executors.newFixedThreadPool(1);
        ExecutorService consumers = Executors.newFixedThreadPool(5);

        IntStream.rangeClosed(1, 5).forEach(itt -> {
            consumers.submit(() -> {
                while (barrier.getNumberWaiting()<1) {
                    logger.info("Consuming");
                    Integer num = null;
                    try {
                        num = sharedQ.poll(1, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    logger.info("Consumed " + num);
                }
                logger.info("Finished");
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        });

        IntStream.rangeClosed(1, 1).forEach(itt -> {
            producers.submit(() -> {
                IntStream.rangeClosed(1, 10).forEach(it -> {
                    int r = new Random().nextInt();
                    logger.info("Producing " + r);
                    try {
                        Thread.sleep(100);
                        sharedQ.put(r);
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                logger.info("Finished");
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        });

        consumers.shutdown();
        producers.shutdown();
    }
}
