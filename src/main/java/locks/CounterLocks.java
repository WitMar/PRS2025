package locks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import synchronization.Counter;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class CounterLocks {

    static Logger logger = LoggerFactory.getLogger(Counter.class);
    static Integer number = 0;
    static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {

        CounterThreadLocks counter = new CounterThreadLocks(number, lock);

        Thread t1 = new Thread(() -> {
            IntStream.rangeClosed(1, 10000).forEach(num -> {
                counter.increment();
            });
        });

        Thread t2 = new Thread(() -> {
            IntStream.rangeClosed(1, 10000).forEach(num -> {
                counter.decrement();
            });
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        logger.info("Number " + counter.getNumber());

    }
}

class CounterThreadLocks {

    Integer number;
    ReentrantLock lock;

    public CounterThreadLocks(Integer number, ReentrantLock lock) {
        this.number = number;
        this.lock = lock;
    }

    public void increment() {
        lock.lock();
        number++;
        lock.unlock();
    }


    public void decrement() {
        lock.lock();
        number--;
        lock.unlock();
    }

    public Integer getNumber() {
        lock.lock();
        Integer num = number;
        lock.unlock();
        return num;
    }
}
