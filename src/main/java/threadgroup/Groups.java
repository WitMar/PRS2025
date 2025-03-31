package threadgroup;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Groups {

    private static final Logger logger = LoggerFactory.getLogger(Groups.class);

    public static void main(String[] args) {
        logger.info(String.valueOf(Runtime.getRuntime().availableProcessors()));

        logger.info(Thread.currentThread().getThreadGroup().getName());
        logger.info(Thread.currentThread().getThreadGroup().getParent().getName());

        ThreadGroup group = new ThreadGroup("moja grupa");
        // tworzymy podgrupę
        ThreadGroup group_sec = new ThreadGroup(group, "moja druga grupa");
        Thread thread1 = new Thread(group_sec, () -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread thread2 = new Thread(group_sec, () -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread1.start();
        thread2.start();

        group_sec.list();

        logger.info(String.valueOf(group.activeCount()));
        logger.info(String.valueOf(group_sec.activeCount()));

    }
}
