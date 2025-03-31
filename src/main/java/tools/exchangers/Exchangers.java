package tools.exchangers;

import org.slf4j.LoggerFactory;
import threadgroup.Groups;

import java.util.Random;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Exchangers {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Groups.class);

    public static void main(String[] args) {

        Exchanger<Integer> ex = new Exchanger<Integer>();

        ExecutorService producers = Executors.newFixedThreadPool(1);
        ExecutorService consumers = Executors.newFixedThreadPool(1);

        consumers.submit(() -> {
            IntStream.rangeClosed(1, 100).forEach(it -> {
                logger.info("Consuming");
                Integer num = null;
                try {
                    num = ex.exchange(num);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("Consumed " + num);
            });
        });


        producers.submit(() -> {
            IntStream.rangeClosed(1, 100).forEach(it -> {
                int r = new Random().nextInt();
                logger.info("Producing " + r);
                try {
                    Thread.sleep(1000);
                    ex.exchange(r);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });

        consumers.shutdown();
        producers.shutdown();
    }
}
