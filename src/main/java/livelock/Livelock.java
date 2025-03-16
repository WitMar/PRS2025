package livelock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import race.TicketBooking;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Livelock {

    private static final Logger logger = LoggerFactory.getLogger(Livelock.class);
    private Lock lock1 = new ReentrantLock(true);
    private Lock lock2 = new ReentrantLock(true);

    public static void main(String[] args) {
        Livelock livelock = new Livelock();
        Thread thread1 = new Thread(() -> livelock.operation1());
        Thread thread2 = new Thread(() -> livelock.operation2());

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void operation1() {
        while (true) {
            try {
                lock1.tryLock(50, TimeUnit.MILLISECONDS);
                logger.info("lock1 acquired, trying to acquire lock2.");
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (lock2.tryLock()) {
                logger.info("lock2 acquired.");
            } else {
                logger.info("cannot acquire lock2, releasing lock1.");
                lock1.unlock();
                continue;
            }

            logger.info("executing first operation.");
            break;
        }
        lock2.unlock();
        lock1.unlock();
    }

    public void operation2() {
        while (true) {
            try {
                lock2.tryLock(50, TimeUnit.MILLISECONDS);
                logger.info("lock2 acquired, trying to acquire lock1.");
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (lock1.tryLock()) {
                logger.info("lock1 acquired.");
            } else {
                logger.info("cannot acquire lock1, releasing lock2.");
                lock2.unlock();
                continue;
            }

            logger.info("executing second operation.");
            break;
        }
        lock1.unlock();
        lock2.unlock();
    }

    // helper methods

}
