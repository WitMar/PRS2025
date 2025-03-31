package tools.phaser;

import tools.model.TimeAndId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Phaser;

public class PhaserExample {

    public static void main(String[] args) {
        List<TimeAndId> timeList = Collections.synchronizedList(new ArrayList<TimeAndId>());

        Phaser phaser = new Phaser();
        phaser.register();
        int currentPhase;

        System.out.println("Starting");

        new MyThread(phaser, "A", timeList);
        new MyThread(phaser, "B", timeList);
        new MyThread(phaser, "C", timeList);

        // Wait for all threads to complete phase Zero.
        currentPhase = phaser.getPhase();
        phaser.arriveAndAwaitAdvance();
        System.out.println("Phase " + currentPhase
                + " Complete");
        System.out.println("Phase Zero Ended");

        // Wait for all threads to complete phase One.
        currentPhase = phaser.getPhase();
        phaser.arriveAndAwaitAdvance();
        System.out.println("Phase " + currentPhase
                + " Complete");
        System.out.println("Phase One Ended");

        currentPhase = phaser.getPhase();
        phaser.arriveAndAwaitAdvance();
        System.out.println("Phase " + currentPhase
                + " Complete");
        System.out.println("Phase Two Ended");

        // Deregister the main thread.
        phaser.arriveAndDeregister();
        if (phaser.isTerminated()) {
            System.out.println("Phaser is terminated");
        }
    }
}

class MyThread {

    Phaser phaser;
    String title;
    List<TimeAndId> timeList;

    public MyThread(Phaser phaser, String title, List<TimeAndId> timeList) {
        this.phaser = phaser;
        this.title = title;
        this.timeList = timeList;

        new Thread(() -> {
            phaser.register();
            System.out.println("Thread: " + title
                    + " Phase Zero Started");
            try {
                Thread.sleep(new Random().nextInt() % 1000 + 2000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            timeList.add(TimeAndId.builder().time(Instant.now()).id(title).build());
            if (timeList.size() > 2 && timeList.get(2).getId() == title) {
                System.out.println("Thread: " + title
                        + " Lost");
                phaser.arriveAndDeregister();
                return;
            } else {
                phaser.arriveAndAwaitAdvance();
            }

            // Stop execution to prevent jumbled output
            try {
                Thread.sleep(new Random().nextInt() % 1000 + 2000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }

            System.out.println("Thread: " + title
                    + " Phase One Started");
            timeList.add(TimeAndId.builder().time(Instant.now()).id(title).build());
            if (timeList.size() > 4 && timeList.get(4).getId() == title) {
                System.out.println("Thread: " + title
                        + " Lost");
                phaser.arriveAndDeregister();
                return;
            } else {
                phaser.arriveAndAwaitAdvance();
            }

            // Stop execution to prevent jumbled output
            try {
                Thread.sleep(new Random().nextInt() % 1000 + 2000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }

            System.out.println("Thread: " + title
                    + " Phase Two Started");
            phaser.arriveAndDeregister();
        }).

                start();
    }


}
