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

        //Check if the entity is still active every 10 minutes
        Bukkit.getScheduler().scheduleSyncRepeatingTask(mcMMO.p, new CheckTrackedEntityExistence(this), 12000, 12000);
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

        public CheckTrackedEntityExistence(TrackedEntity trackedEntity) {
            this.trackedEntity = trackedEntity;
        }

        @Override
        public void run() {
            if (!trackedEntity.isActive()) {
                Archery.removeFromTracker(trackedEntity);
            }
        }
    }
}
