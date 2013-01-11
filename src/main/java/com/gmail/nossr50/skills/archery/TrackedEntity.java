package com.gmail.nossr50.skills.archery;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitScheduler;

import com.gmail.nossr50.mcMMO;

public class TrackedEntity implements Runnable {
    private LivingEntity livingEntity;
    private int arrowCount;
    private int previousTicksLived;
    private int taskId;
    private BukkitScheduler scheduler;

    protected TrackedEntity(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
        this.scheduler = Bukkit.getScheduler();
        this.taskId = scheduler.scheduleSyncRepeatingTask(mcMMO.p, this, 12000, 12000);
    }

    //LivingEntity.isDead() isn't a reliable way to know if an entity is still active
    //This method must not be called more than once per server tick
    private boolean isActive() {
        int currentTicksLived = livingEntity.getTicksLived();

        if (currentTicksLived == previousTicksLived) {
            return false;
        }

        previousTicksLived = currentTicksLived;
        return true;
    }

    protected LivingEntity getLivingEntity() {
        return livingEntity;
    }

    protected int getArrowCount() {
        return arrowCount;
    }

    protected void incrementArrowCount() {
        arrowCount++;
    }

    @Override
    public void run() {
        if (!isActive()) {
            Archery.removeFromTracker(this);
            scheduler.cancelTask(taskId);
        }
    }
}
