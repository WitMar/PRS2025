package atomic;

import org.apache.log4j.Logger;

import java.util.stream.IntStream;

public class CounterSynchronized {

    static Logger logger = Logger.getLogger(CounterSynchronized.class);
    static Integer number = 0;

    public static void main(String[] args) throws InterruptedException {

        CounterThreadSynchronized counter = new CounterThreadSynchronized(number);

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

class CounterThreadSynchronized {

    Integer number;

    public CounterThreadSynchronized(Integer number) {
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
