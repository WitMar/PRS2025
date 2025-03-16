package trylock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class TryLock {

    private static final Logger logger = LoggerFactory.getLogger(TryLock.class);
    Lock lock;
    Integer lastIt = 0;

    int ticketsAvailable;

    public TryLock(Lock lock) {
        this.lock = lock;
        ticketsAvailable = 20;
    }

    public void process() {
        IntStream.rangeClosed(1, 10).forEach(it -> {
            if (lock.tryLock()) {
                logger.info("Saving from " + lastIt + " to " + it);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lastIt = it + 1;
                lock.unlock();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("Process " + it);
            }
        });
        if (lastIt <= 10) {
            lock.lock();
            logger.info("Saving from " + lastIt + " to " + 10);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.unlock();
        }
    }

    public static void main(String args[]) {
        Lock lock = new ReentrantLock();
        TryLock obj = new TryLock(lock);
        TryLock obj2 = new TryLock(lock);
        TryLock obj3 = new TryLock(lock);

        Thread thread1 = new Thread(() -> obj.process());
        Thread thread2 = new Thread(() -> obj2.process());
        Thread thread3 = new Thread(() -> obj3.process());

        thread1.start();
        thread2.start();
        thread3.start();

        try {
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}