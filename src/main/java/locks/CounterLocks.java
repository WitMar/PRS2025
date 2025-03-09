package locks;

import org.apache.log4j.Logger;

import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class CounterLocks {

    static Logger logger = Logger.getLogger(CounterLocks.class);
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
        number++;
    }


    public void decrement() {
        number--;
    }

    public Integer getNumber() {
        return number;
    }
}
