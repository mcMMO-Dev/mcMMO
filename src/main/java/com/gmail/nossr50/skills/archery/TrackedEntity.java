package com.gmail.nossr50.skills.archery;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import com.gmail.nossr50.mcMMO;

public class TrackedEntity implements Runnable {
    private LivingEntity livingEntity;
    private int arrowCount;
    private int previousTicksLived;
    private int taskId;

    public TrackedEntity(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(mcMMO.p, this, 12000, 12000);
    }

    //LivingEntity.isDead() isn't a reliable way to know if an entity is still active
    //This method must not be called more than once per server tick
    public boolean isActive() {
        int currentTicksLived = livingEntity.getTicksLived();

        if (currentTicksLived == previousTicksLived) {
            return false;
        }

        previousTicksLived = currentTicksLived;
        return true;
    }

    public LivingEntity getLivingEntity() {
        return livingEntity;
    }

    public int getArrowCount() {
        return arrowCount;
    }

    public void incrementArrowCount() {
        arrowCount++;
    }

    @Override
    public void run() {
        if (!isActive()) {
            Archery.removeFromTracker(this);
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }
}
