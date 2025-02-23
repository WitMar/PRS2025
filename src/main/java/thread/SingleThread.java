package thread;

import introduction.HelloWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleThread {
    public static void main(String[] args) { //main is a function in java that is run, only classes with main method can be executed in Java

        Logger logger = LoggerFactory.getLogger(HelloWorld.class);

        logger.info("Current thread name: " + Thread.currentThread().getName());

        for (byte i = 1; i <= 5; i++) {

            // sleep stops thread for given ms
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }

            // write elements to screen
            logger.info("Loop 1, Iteration: " + i);
        }

        for (int i = 1; i <= 5; i++) {

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
            logger.info("Loop 2, Iteration: " + i);
        }
    }
}
