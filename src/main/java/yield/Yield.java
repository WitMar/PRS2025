package yield;

import org.apache.log4j.Logger;

public class Yield {

    public static Logger log = Logger.getLogger(Yield.class);

    public static void main(String[] args) {
        Thread thread = new Thread(() ->
        {
            for (int i = 0; i < 3; ++i) {
                log.info(Thread.currentThread().getName());
                Thread.yield(); // By calling this method, MyThread stop its execution and giving a chance to a main thread
            }
            log.info("Thread ended:" + Thread.currentThread().getName());
        }
        );

        thread.start();

        for (int i = 0; i < 3; ++i) {
            log.info(Thread.currentThread().getName());
        }

        log.info("Thread ended:" + Thread.currentThread().getName());
    }
}