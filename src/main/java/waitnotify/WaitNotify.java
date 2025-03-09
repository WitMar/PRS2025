package waitnotify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitNotify {

    Logger log = LoggerFactory.getLogger(WaitNotify.class);

    public static void main(String[] args) throws InterruptedException {
        ProducerConsumer producer = new ProducerConsumer();

        Thread t1 = new Thread(() -> {
            try {
                producer.produce();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                producer.consume();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t2.start();
        Thread.sleep(100);
        t1.start();

        t1.join();
        t2.join();

    }
}

class ProducerConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ProducerConsumer.class);

    public void produce() throws InterruptedException {
        synchronized (this) {
            logger.info("Start produce");
            notify();
            Thread.sleep(1000L);
            logger.info("Finish produce");
        }
    }


    public void consume() throws InterruptedException {
        synchronized (this) {
            logger.info("Start consume");
            wait();
            logger.info("Finish consume");
        }
    }

}
