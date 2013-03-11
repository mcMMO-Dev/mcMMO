package com.gmail.nossr50.database.queuemanager;

import com.gmail.nossr50.mcMMO;

public class Queue implements Runnable {
    private boolean running;

    public Queue() {
        this.running = true;
    }

    public void run() {
        while (running) {
            try {
                mcMMO.queueManager.queue.take().run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void kill() {
        this.running = false;
    }
}
