package com.gmail.nossr50.mcmmo.bukkit.platform.scheduler;

import com.gmail.nossr50.mcmmo.api.platform.scheduler.PlatformScheduler;
import com.gmail.nossr50.mcmmo.api.platform.scheduler.Task;
import com.gmail.nossr50.mcmmo.bukkit.BukkitBootstrap;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class BukkitPlatformScheduler implements PlatformScheduler {

    private final BukkitBootstrap bukkitBootstrap;

    public BukkitPlatformScheduler(BukkitBootstrap bukkitBootstrap) {
        this.bukkitBootstrap = bukkitBootstrap;
    }

    @Override
    public TaskBuilder getTaskBuilder() {
        return new TaskBuilder() {
            @Override
            public Task schedule() {
                return BukkitPlatformScheduler.this.scheduleTask(this);
            }
        };
    }

    @Override
    public Task scheduleTask(TaskBuilder taskBuilder) {
        final Long repeatTime = taskBuilder.getRepeatTime();
        final Long delay = taskBuilder.getDelay();
        final boolean isAsync = taskBuilder.isAsync();
        final Consumer<Task> taskConsumer = taskBuilder.getTask();

        final MMOBukkitTask task = new MMOBukkitTask(taskConsumer);
        final BukkitScheduler bukkitScheduler = Bukkit.getScheduler();

        final BukkitTask bukkitTask;
        if (!isAsync) {
            if (delay == null && repeatTime == null) {
                bukkitTask = bukkitScheduler.runTask(bukkitBootstrap, task);
            } else if (delay != null && repeatTime == null) {
                bukkitTask = bukkitScheduler.runTaskLater(bukkitBootstrap, task, delay);
            } else {
                bukkitTask = bukkitScheduler.runTaskTimer(bukkitBootstrap, task, delay != null ? delay : 0, repeatTime);
            }
        } else {
            if (delay == null && repeatTime == null) {
                bukkitTask = bukkitScheduler.runTaskAsynchronously(bukkitBootstrap, task);
            } else if (delay != null && repeatTime == null) {
                bukkitTask = bukkitScheduler.runTaskLaterAsynchronously(bukkitBootstrap, task, delay);
            } else {
                bukkitTask = bukkitScheduler.runTaskTimerAsynchronously(bukkitBootstrap, task, delay != null ? delay : 0, repeatTime);
            }
        }

        task.setBukkitTask(bukkitTask);

        return task;
    }
}
