package semaphores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import synchronization.Counter;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class CounterSynchronized {

    static Logger logger = LoggerFactory.getLogger(CounterSemaphores.class);
    static AtomicInteger number = new AtomicInteger(0);
    static Semaphore semaphore = new Semaphore(3);

    public static void main(String[] args) throws InterruptedException {

        CounterThreadAtomicSemaphores counter = new CounterThreadAtomicSemaphores(number, semaphore);

        Thread t1 = new Thread(() -> {
            IntStream.rangeClosed(1, 10000).forEach(num -> {
                try {
                    counter.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });

        Thread t2 = new Thread(() -> {
            IntStream.rangeClosed(1, 10000).forEach(num -> {
                try {
                    counter.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        logger.info("Number " + counter.getNumber());

    }
}

class CounterThreadAtomicSemaphores {

    AtomicInteger number;
    Semaphore semaphore;

    public CounterThreadAtomicSemaphores(AtomicInteger number, Semaphore semaphore) {
        this.number = number;
        this.semaphore = semaphore;
    }

    public void increment() throws InterruptedException {
        semaphore.acquire();
        number.getAndIncrement();
        semaphore.release();
    }


    public void decrement() throws InterruptedException {
        semaphore.acquire();
        number.getAndDecrement();
        semaphore.release();
    }

    public AtomicInteger getNumber() {
        return number;
    }
}
