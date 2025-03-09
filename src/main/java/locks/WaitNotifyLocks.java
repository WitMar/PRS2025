package locks;

import org.apache.log4j.Logger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WaitNotifyLocks<condition> {

    Logger logger = Logger.getLogger(WaitNotifyLocks.class);
    static ReentrantLock lock = new ReentrantLock();
    static Condition condition = lock.newCondition();

    public static void main(String[] args) throws InterruptedException {
        ProducerConsumer producer = new ProducerConsumer(lock, condition);

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

    Logger logger = Logger.getLogger(WaitNotifyLocks.class);

    Lock lock;
    Condition condition;

    public ProducerConsumer(ReentrantLock lock, Condition condition) {
        this.lock = lock;
        this.condition = condition;
    }

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
