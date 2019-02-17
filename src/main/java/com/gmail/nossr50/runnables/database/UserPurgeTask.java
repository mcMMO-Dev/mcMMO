package com.gmail.nossr50.runnables.database;

import com.gmail.nossr50.config.MainConfig;
import com.gmail.nossr50.mcMMO;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.locks.ReentrantLock;

public class UserPurgeTask extends BukkitRunnable {
    private ReentrantLock lock = new ReentrantLock();
    @Override
    public void run() {
        lock.lock();
        mcMMO.getDatabaseManager().purgePowerlessUsers();

        if (MainConfig.getInstance().getOldUsersCutoff() != -1) {
            mcMMO.getDatabaseManager().purgeOldUsers();
        }
        lock.unlock();
    }
}
