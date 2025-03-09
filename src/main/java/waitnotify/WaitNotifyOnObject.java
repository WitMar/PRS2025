package waitnotify;

import org.apache.log4j.Logger;

public class WaitNotifyOnObject {

    Logger logger = Logger.getLogger(WaitNotify.class);
    static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        ProducerConsumerObject producer = new ProducerConsumerObject(lock);

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

class ProducerConsumerObject {

    Object lock;

    public ProducerConsumerObject(Object lock) {
        this.lock = lock;
    }

    Logger logger = Logger.getLogger(WaitNotify.class);

    public void produce() throws InterruptedException {
        synchronized (lock) {
            logger.info("Start produce");
            lock.notify();
            Thread.sleep(1000L);
            logger.info("Finish produce");
        }
    }


    public void consume() throws InterruptedException {
        synchronized (lock) {
            logger.info("Start consume");
            lock.wait();
            logger.info("Finish consume");
        }
    }

}
