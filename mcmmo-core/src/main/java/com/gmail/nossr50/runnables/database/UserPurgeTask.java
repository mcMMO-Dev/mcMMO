package com.gmail.nossr50.runnables.database;

import com.gmail.nossr50.mcMMO;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.locks.ReentrantLock;

public class UserPurgeTask extends BukkitRunnable {
    private final mcMMO pluginRef;
    private ReentrantLock lock;

    public UserPurgeTask(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        lock = new ReentrantLock();
    }

    @Override
    public void run() {
        lock.lock();
        if (pluginRef.getDatabaseCleaningSettings().isPurgePowerlessUsers())
            pluginRef.getDatabaseManager().purgePowerlessUsers();

        if (pluginRef.getDatabaseCleaningSettings().isPurgeOldUsers()) {
            pluginRef.getDatabaseManager().purgeOldUsers();
        }
        lock.unlock();
    }
}
