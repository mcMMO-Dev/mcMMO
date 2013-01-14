package com.gmail.nossr50.runnables;

import java.lang.Runnable;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import com.gmail.nossr50.mcMMO;

public class MobStoreCleaner implements Runnable
{
    private int taskID;

    public MobStoreCleaner()
    {
        taskID = -1;
        start();
    }

    public void start()
    {
        if (taskID >= 0)
            return;

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        taskID = scheduler.scheduleSyncRepeatingTask(mcMMO.p, this, 12000, 12000);
    }

    public void stop()
    {
        if(taskID < 0)
            return;

        Bukkit.getServer().getScheduler().cancelTask(taskID);
        taskID = -1;
    }

    public void run()
    {
        mcMMO.placeStore.cleanMobLists();
    }
}