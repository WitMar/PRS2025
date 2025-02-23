package thread;

import introduction.HelloWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeparateThread {

    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(HelloWorld.class);

        Concurrency thread1 = new Concurrency(1); // create an object of class Concurrent which is a thread

        thread1.start();

        logger.info(Thread.currentThread().getName());
    }

    static class Concurrency extends Thread { // this class is a thread in Java, elements in run method will be run on separate Thread

        public Logger logger = LoggerFactory.getLogger(HelloWorld.class);

        private int loopNum;

        Concurrency(int loopNum) {
            this.loopNum = loopNum;
        }

        @Override // override method from superclass
        public void run() {

            for (int i = 1; i <= 5; i++) {

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }
                logger.info("Loop " + this.loopNum + ", Iteration: " + i + Thread.currentThread().getName());
            }
        }
    }
}


