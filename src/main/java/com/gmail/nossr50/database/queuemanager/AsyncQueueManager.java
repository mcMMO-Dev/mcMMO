package com.gmail.nossr50.database.queuemanager;

import java.util.concurrent.LinkedBlockingQueue;

public class AsyncQueueManager implements Runnable {
    private LinkedBlockingQueue<Queueable> queue;
    private boolean running;
    
    public AsyncQueueManager() {
        this.queue = new LinkedBlockingQueue<Queueable>();
        this.running = true;
    }

    @Override
    public void run() {
        while(running) {
            try {
                queue.take().run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean queue(Queueable task) {
        return queue.offer(task);
    }

    public boolean contains(String player) {
        return queue.contains(new EqualString(player));
    }

    public void disable() {
        running = false;
        // Throw one more Queueable into queue to unblock take()
        queue.add(new EndThread());
    }

    private class EqualString {
        private String player;

        public EqualString(String player) {
            this.player = player;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Queueable) {
                return ((Queueable)obj).getPlayer().equalsIgnoreCase(player);
            }

            return false;
        }
    }

    private class EndThread implements Queueable {
        @Override
        public void run() { }

        @Override
        public String getPlayer() { return null; }     
    }
}
