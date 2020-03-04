package com.gmail.nossr50.runnables.database;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcmmo.api.platform.scheduler.Task;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class UserPurgeTask implements Consumer<Task> {
    private final mcMMO pluginRef;
    private ReentrantLock lock;

    public UserPurgeTask(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        lock = new ReentrantLock();
    }

    @Override
    public void accept(Task task) {
        lock.lock();
        if (pluginRef.getDatabaseCleaningSettings().isPurgePowerlessUsers())
            pluginRef.getDatabaseManager().purgePowerlessUsers();

        if (pluginRef.getDatabaseCleaningSettings().isPurgeOldUsers()) {
            pluginRef.getDatabaseManager().purgeOldUsers();
        }
        lock.unlock();
    }
}
