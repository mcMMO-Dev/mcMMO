package com.gmail.nossr50.core.runnables.database;

import com.gmail.nossr50.core.McmmoCore;
import com.gmail.nossr50.core.config.MainConfig;

import java.util.concurrent.locks.ReentrantLock;

public class UserPurgeTask implements Runnable {
    private ReentrantLock lock = new ReentrantLock();

    @Override
    public void run() {
        lock.lock();
        McmmoCore.getDatabaseManager().purgePowerlessUsers();

        if (MainConfig.getInstance().getOldUsersCutoff() != -1) {
            McmmoCore.getDatabaseManager().purgeOldUsers();
        }
        lock.unlock();
    }
}
