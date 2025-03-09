package join;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Join {

    public static Logger log = LoggerFactory.getLogger(Join.class);

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 5; ++i) {
                log.info(Thread.currentThread().getName() + " " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.info("Thread ended:" + Thread.currentThread().getName());
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; ++i) {
                log.info(Thread.currentThread().getName() + " " + i);
                try {
                    Thread.sleep(1010);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.info("Thread ended:" + Thread.currentThread().getName());
        });

        Thread thread3 = new Thread(() -> {
            for (int i = 0; i < 5; ++i) {
                log.info(Thread.currentThread().getName() + " " + i);
                try {
                    Thread.sleep(1100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.info("Thread ended:" + Thread.currentThread().getName());
        });

        thread.start();
        thread2.start();
        thread3.start();

        thread.join();

        log.info("Main ended:" + Thread.currentThread().getName());
    }
}