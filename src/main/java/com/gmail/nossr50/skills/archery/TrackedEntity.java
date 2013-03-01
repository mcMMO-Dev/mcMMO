package com.gmail.nossr50.skills.archery;

import java.util.UUID;

import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitScheduler;

import com.gmail.nossr50.mcMMO;

public class TrackedEntity implements Runnable {
    private LivingEntity livingEntity;
    private UUID id;
    private int arrowCount;
    private int taskId;
    private BukkitScheduler scheduler;

    protected TrackedEntity(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
        this.id = livingEntity.getUniqueId();
        this.scheduler = mcMMO.p.getServer().getScheduler();
        this.taskId = scheduler.scheduleSyncRepeatingTask(mcMMO.p, this, 12000, 12000);
    }

    @Override
    public void run() {
        if (!livingEntity.isValid()) {
            Archery.removeFromTracker(this);
            scheduler.cancelTask(taskId);
        }
    }

    protected LivingEntity getLivingEntity() {
        return livingEntity;
    }

    protected UUID getID() {
        return id;
    }

    protected int getArrowCount() {
        return arrowCount;
    }

    protected void incrementArrowCount() {
        arrowCount++;
    }
}
