package race;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class TicketBooking {


    private static final Logger logger = LoggerFactory.getLogger(TicketBooking.class);
    Lock lock;

    int ticketsAvailable;

    public TicketBooking(Lock lock) {
        this.lock = lock;
        ticketsAvailable = 20;
    }

    public void book() {
        logger.info("Waiting to book ticket for : " + Thread.currentThread().getName());
        //lock.lock();
        if (ticketsAvailable > 0) {
            logger.info("Booking ticket for : " + Thread.currentThread().getName());
            try {
                Thread.sleep(1000); // simulate booking operation
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ticketsAvailable--;
            logger.info("Ticket BOOKED for : " + Thread.currentThread().getName());
            logger.info("currently ticketsAvailable = " + ticketsAvailable);
            //lock.unlock();
        } else {
            //lock.lock();
            logger.info("Ticket NOT BOOKED for : " + Thread.currentThread().getName());
        }
    }

    public static void main(String args[]) {
        Lock lock = new ReentrantLock();
        TicketBooking obj = new TicketBooking(lock);

        Thread thread1 = new Thread(() -> IntStream.rangeClosed(1, 6).forEach(it -> obj.book()));
        Thread thread2 = new Thread(() -> IntStream.rangeClosed(1, 6).forEach(it -> obj.book()));
        Thread thread3 = new Thread(() -> IntStream.rangeClosed(1, 6).forEach(it -> obj.book()));
        Thread thread4 = new Thread(() -> IntStream.rangeClosed(1, 6).forEach(it -> obj.book()));

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
