package com.gmail.nossr50.skills.archery;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import com.gmail.nossr50.mcMMO;

public class TrackedEntity {
    private LivingEntity livingEntity;
    private int arrowCount;
    private int previousTicksLived;

    public TrackedEntity(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
        new CheckTrackedEntityExistence(this);
    }

    //LivingEntity.isDead() isn't a reliable way to know if an entity is still active
    //This method must not be called more than once per server tick
    public boolean isActive() {
        int currentTicksLived = livingEntity.getTicksLived();

        if (currentTicksLived == previousTicksLived) {
            return false;
        }
        else {
            previousTicksLived = currentTicksLived;
            return true;
        }
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

    private class CheckTrackedEntityExistence implements Runnable {
        private TrackedEntity trackedEntity;
        private int taskId;

        public CheckTrackedEntityExistence(TrackedEntity trackedEntity) {
            this.trackedEntity = trackedEntity;
            
            //Check if the entity is still active every 10 minutes
            taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(mcMMO.p, this, 20, 20);
        }

        @Override
        public void run() {
            if (!trackedEntity.isActive()) {
                Archery.removeFromTracker(trackedEntity);
                Bukkit.getScheduler().cancelTask(taskId);
            }
        }
    }
}
