package com.gmail.nossr50.core.mcmmo.tasks;

/**
 * Represents a schedules task
 * Bukkit and Sponge both have systems for this
 */
public interface PluginTask {
    /**
     * Kills the current task
     */
    void killTask();

    /**
     * Schedule a delayed task in n ticks
     * @param ticks ticks until the task should start
     */
    void scheduleTask(int ticks);

    /**
     * Schedule this task (begins ASAP)
     */
    void scheduleTask();

    /**
     * Get the ID of this task
     * @return the id of this task
     */
    int getTaskId();
}
