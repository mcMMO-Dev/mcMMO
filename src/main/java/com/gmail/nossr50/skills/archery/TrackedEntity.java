package com.gmail.nossr50.skills.archery;

import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitScheduler;

import com.gmail.nossr50.mcMMO;

public class TrackedEntity implements Runnable {
    private LivingEntity livingEntity;
    private int arrowCount;
    private int taskId;
    private BukkitScheduler scheduler;

    protected TrackedEntity(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
        this.scheduler = mcMMO.p.getServer().getScheduler();
        this.taskId = scheduler.scheduleSyncRepeatingTask(mcMMO.p, this, 12000, 12000);
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
        if (!livingEntity.isValid()) {
            Archery.removeFromTracker(this);
            scheduler.cancelTask(taskId);
        }
    }
}
