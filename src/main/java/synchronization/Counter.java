package synchronization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

public class Counter {

    static Logger logger = LoggerFactory.getLogger(Counter.class);
    static Integer number = 0;

    public static void main(String[] args) throws InterruptedException {

        CounterThread counter = new CounterThread(number);

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

class CounterThread {

    Integer number;

    public CounterThread(Integer number) {
        this.number = number;
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
