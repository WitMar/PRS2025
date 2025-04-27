package example;

import java.util.concurrent.ThreadLocalRandom;

public class VirtualThreadsExample {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            Thread.startVirtualThread(() -> {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));
                    System.out.println("Task completed by " + Thread.currentThread());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        Thread.sleep(2000);
    }
}