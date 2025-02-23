package thread;

import introduction.HelloWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example of threads run in parallel, competing for processor access
 */
public class TwoThreads {
    public static void main(String[] args) {

        Concurrency thread1 = new Concurrency(1); // create an object of class Concurrent which is a thread
        Concurrency thread2 = new Concurrency(2);

        thread1.start();
        thread2.start();
    }

    static class Concurrency extends Thread { // this class is a thread in Java, elements in run method will be run on separate Thread

        private final int loopNum;
        public Logger logger = LoggerFactory.getLogger(HelloWorld.class);


        Concurrency(int loopNum) {
            this.loopNum = loopNum;
        }

        @Override // override method from superclass
        public void run() {
            for (int i = 1; i <= 500; i++) {
                logger.info("Loop " + this.loopNum + ", Iteration: " + i);
            }
        }
    }

}

