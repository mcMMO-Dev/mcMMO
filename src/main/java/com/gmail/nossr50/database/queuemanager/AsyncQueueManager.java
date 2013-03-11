package com.gmail.nossr50.database.queuemanager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.scheduler.BukkitScheduler;

import com.gmail.nossr50.mcMMO;

public class AsyncQueueManager {

    private List<Queue> queues;
    protected LinkedBlockingQueue<Queueable> queue;;

    public AsyncQueueManager(BukkitScheduler scheduler, int number) {
        this.queues = new ArrayList<Queue>();

        for (int i = 1; i <= number; i++) {
            Queue queue = new Queue();
            scheduler.runTaskAsynchronously(mcMMO.p, queue);
            this.queues.add(queue);
        }

        this.queue = new LinkedBlockingQueue<Queueable>();
    }

    public boolean queue(Queueable task) {
        return queue.offer(task);
    }

    public boolean contains(String player) {
        return queue.contains(new EqualString(player));
    }

    private class EqualString {
        private String player;

        public EqualString(String player) {
            this.player = player;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Queueable) {
                return ((Queueable) obj).getPlayer().equalsIgnoreCase(player);
            }
            return false;
        }
    }

    public void disable() {
        for (Queue queueThread : queues) {
            queueThread.kill();
        }

        for (int i = 0; i < queues.size(); i++) {
            queue.offer(new KillQueue());
        }
    }

    public class KillQueue implements Queueable {
        @Override
        public void run() {
        }

        @Override
        public String getPlayer() {
            return null;
        }
    }
}
