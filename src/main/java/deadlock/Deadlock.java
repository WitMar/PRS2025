package deadlock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deadlock {

    private static final Logger logger = LoggerFactory.getLogger(Deadlock.class);

    public static void main(String[] args) throws InterruptedException {
        // creating one object
        Object s1 = new Object();

        // creating second object
        Object s2 = new Object();

        // creating first thread and starting it
        Thread t1 = new Thread(() -> {
            synchronized (s1)
            {
                logger.info("test1-s1-begin");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("test1-s1-end");
                // second synchronized method
                synchronized (s2)
                {
                    logger.info("test1-s2-begin");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    logger.info("test1-s2-end");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (s2)
            {
                logger.info("test2-s2-begin");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("test2-s2-end");
                // second synchronized method
                synchronized (s1)
                {
                    logger.info("test2-s1-begin");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    logger.info("test2-s1-end");
                }
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }
}

