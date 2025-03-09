package locks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import synchronization.Counter;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WaitNotifyLocks<condition> {

    Logger log = LoggerFactory.getLogger(WaitNotifyLocks.class);
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

    Logger logger = LoggerFactory.getLogger(WaitNotifyLocks.class);

    Lock lock;
    Condition condition;

    public ProducerConsumer(ReentrantLock lock, Condition condition) {
        this.lock = lock;
        this.condition = condition;
    }

    public void produce() throws InterruptedException {
        lock.lock();
        logger.info("Start produce");
        condition.signal();
        Thread.sleep(1000L);
        logger.info("Finish produce");
        lock.unlock();
    }


    public void consume() throws InterruptedException {
        lock.lock();
        logger.info("Start consume");
        condition.await();
        logger.info("Finish consume");
        lock.unlock();
    }

}
