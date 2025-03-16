package starvation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

class StarvationWaitNotify {

    private static final Logger logger = LoggerFactory.getLogger(StarvationWaitNotify.class);
    static Integer count = 0;

    public StarvationWaitNotify(Integer count) {
        this.count = count;
    }

    public void notify(int id) {
        IntStream.rangeClosed(1, 10).forEach(it -> {
            synchronized (this) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                notify();
            }
            Thread.yield();
            logger.info("Notify");
        });
    }

    public void run(int id) {
        IntStream.rangeClosed(1, 2).forEach(it -> {
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count++;
                logger.info("Thread execution" + " " + id);
            }
        });

    }

    public static void main(String[] args) throws InterruptedException {
        logger.info("Parent thread execution starts");
        StarvationWaitNotify producerConsumer = new StarvationWaitNotify(count);

        /* Priority of each thread given. */
        /* Thread 1 with priority 7. */
        Thread thread1 = new Thread(() -> producerConsumer.run(1));
        thread1.setPriority(10);
        /* Thread 2 with priority 6. */
        Thread thread2 = new Thread(() -> producerConsumer.run(2));
        thread2.setPriority(8);
        /* Thread 3 with priority 5. */
        Thread thread3 = new Thread(() -> producerConsumer.run(3));
        thread3.setPriority(6);
        /* Thread 4 with priority 4. */
        Thread thread4 = new Thread(() -> producerConsumer.run(4));
        thread4.setPriority(4);
        /* Thread 5 with priority 3. */
        Thread thread5 = new Thread(() -> producerConsumer.run(5));
        thread5.setPriority(1);

        Thread notifier = new Thread(() -> producerConsumer.notify(6));
        notifier.setPriority(10);

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        notifier.start();

        logger.info("Parent thread execution completes");

        thread1.join();
        thread2.join();
        thread3.join();
        thread4.join();
        thread5.join();
        notifier.join();
    }
}



