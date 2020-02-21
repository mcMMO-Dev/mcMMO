package com.gmail.nossr50.mcmmo.bukkit.platform.scheduler;

import com.gmail.nossr50.mcmmo.api.platform.scheduler.PlatformScheduler;
import com.gmail.nossr50.mcmmo.api.platform.scheduler.Task;
import com.gmail.nossr50.mcmmo.bukkit.BukkitBoostrap;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class BukkitPlatformScheduler implements PlatformScheduler {

    private final BukkitBoostrap bukkitBoostrap;

    public BukkitPlatformScheduler(BukkitBoostrap bukkitBoostrap) {
        this.bukkitBoostrap = bukkitBoostrap;
    }

    @Override
    public TaskBuilder getTaskBuilder() {
        return new TaskBuilder();
    }

    @Override
    public Task scheduleTask(TaskBuilder taskBuilder) {
        final Integer repeatTime = taskBuilder.getRepeatTime();
        final Integer delay = taskBuilder.getDelay();
        final boolean isAsync = taskBuilder.isAsync();
        final Consumer<Task> taskConsumer = taskBuilder.getTask();

        final MMOBukkitTask task = new MMOBukkitTask(taskConsumer);
        final BukkitScheduler bukkitScheduler = Bukkit.getScheduler();

        final BukkitTask bukkitTask;
        if (!isAsync) {
            if (delay == null && repeatTime == null) {
                bukkitTask = bukkitScheduler.runTask(bukkitBoostrap, task);
            } else if (delay != null && repeatTime == null) {
                bukkitTask = bukkitScheduler.runTaskLater(bukkitBoostrap, task, delay);
            } else {
                bukkitTask = bukkitScheduler.runTaskTimer(bukkitBoostrap, task, delay != null ? delay : 0, repeatTime);
            }
        } else {
            if (delay == null && repeatTime == null) {
                bukkitTask = bukkitScheduler.runTaskAsynchronously(bukkitBoostrap, task);
            } else if (delay != null && repeatTime == null) {
                bukkitTask = bukkitScheduler.runTaskLaterAsynchronously(bukkitBoostrap, task, delay);
            } else {
                bukkitTask = bukkitScheduler.runTaskTimerAsynchronously(bukkitBoostrap, task, delay != null ? delay : 0, repeatTime);
            }
        }

        task.setBukkitTask(bukkitTask);

        return task;
    }
}
