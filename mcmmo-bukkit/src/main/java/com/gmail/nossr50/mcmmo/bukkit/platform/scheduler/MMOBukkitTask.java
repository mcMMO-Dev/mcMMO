package com.gmail.nossr50.mcmmo.bukkit.platform.scheduler;

import com.gmail.nossr50.mcmmo.api.platform.scheduler.Task;
import com.google.common.base.Preconditions;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class MMOBukkitTask implements Task, Runnable {
    Consumer<Task> task;
    private BukkitTask bukkitTask;

    public MMOBukkitTask(Consumer<Task> task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        Preconditions.checkState(bukkitTask != null, "Cannot cancel an an unscheduled task!");
        bukkitTask.cancel();
    }

    @Override
    public void run() {
        task.accept(this);
    }

    public void setBukkitTask(BukkitTask bukkitTask) {
        this.bukkitTask = bukkitTask;
    }
}
