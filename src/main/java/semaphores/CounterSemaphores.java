package semaphores;

import org.apache.log4j.Logger;

import java.util.concurrent.Semaphore;
import java.util.stream.IntStream;

public class CounterSemaphores {

    static Logger logger = Logger.getLogger(CounterSemaphores.class);
    static Integer number = 0;
    static Semaphore semaphore = new Semaphore(1);

    public static void main(String[] args) throws InterruptedException {

        CounterThreadSemaphores counter = new CounterThreadSemaphores(number, semaphore);

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

class CounterThreadSemaphores {

    Integer number;
    Semaphore semaphore;

    public CounterThreadSemaphores(Integer number, Semaphore semaphore) {
        this.number = number;
        this.semaphore = semaphore;
    }

    public void increment() throws InterruptedException {
        semaphore.acquire();
        number++;
        semaphore.release();
    }


    public void decrement() throws InterruptedException {
        semaphore.acquire();
        number--;
        semaphore.release();
    }

    public Integer getNumber() {
        return number;
    }
}
