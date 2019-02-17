package com.gmail.nossr50.core.mcmmo.tasks;

/**
 * Schedules tasks for a platform
 */
public interface TaskScheduler {
    /**
     * Schedules the specified task
     * @param pluginTask the task to schedule
     */
    PluginTask scheduleTask(PluginTask pluginTask);

    /**
     * Schedules the specified task
     * @param runnable the runnable to schedule
     */
    PluginTask scheduleTask(Runnable runnable);

    /**
     * Schedules the specified task
     * @param runnable the runnable to schedule
     * @param tickDelay the delay for this task in ticks
     */
    PluginTask scheduleTask(Runnable runnable, int tickDelay);
}
