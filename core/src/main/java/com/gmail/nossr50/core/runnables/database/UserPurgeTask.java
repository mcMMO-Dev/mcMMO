package com.gmail.nossr50.core.runnables.database;

import com.gmail.nossr50.core.config.Config;
import com.gmail.nossr50.mcMMO;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.locks.ReentrantLock;

public class UserPurgeTask extends BukkitRunnable {
    private ReentrantLock lock = new ReentrantLock();

    @Override
    public void run() {
        lock.lock();
        mcMMO.getDatabaseManager().purgePowerlessUsers();

        if (Config.getInstance().getOldUsersCutoff() != -1) {
            mcMMO.getDatabaseManager().purgeOldUsers();
        }
        lock.unlock();
    }
}
