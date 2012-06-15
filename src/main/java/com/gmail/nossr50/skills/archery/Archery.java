package com.gmail.nossr50.skills.archery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.util.Misc;

public class Archery {
    private static Random random = new Random();
    private static List<TrackedEntity> trackedEntities = new ArrayList<TrackedEntity>();

    public static final int ARROW_TRACKING_MAX_BONUS_LEVEL = 1000;

    public static final int BONUS_DAMAGE_INCREASE_LEVEL = 50;
    public static final double BONUS_DAMAGE_INCREASE_PERCENT = 0.1D;
    public static final double BONUS_DAMAGE_MAX_BONUS_PERCENTAGE = 2.0D;

    public static final int DAZE_MAX_BONUS_LEVEL = 1000;
    public static final int DAZE_MODIFIER = 4;

    protected static void incrementTrackerValue(LivingEntity livingEntity) {
        for (TrackedEntity trackedEntity : trackedEntities) {
            if (trackedEntity.getLivingEntity() == livingEntity) {
                trackedEntity.incrementArrowCount();
                return;
            }
        }

        //If the entity isn't tracked yet
        addToTracker(livingEntity);
    }

    protected static void addToTracker(LivingEntity livingEntity) {
        TrackedEntity trackedEntity = new TrackedEntity(livingEntity);

        trackedEntity.incrementArrowCount();
        trackedEntities.add(trackedEntity);
    }

    protected static void removeFromTracker(TrackedEntity trackedEntity) {
        trackedEntities.remove(trackedEntity);
    }

    /**
     * Check for arrow retrieval.
     *
     * @param entity The entity hit by the arrows
     */
    public static void arrowRetrievalCheck(LivingEntity livingEntity) {
        for (Iterator<TrackedEntity> it = trackedEntities.iterator() ; it.hasNext() ; ) {
            TrackedEntity trackedEntity = it.next();

            if (trackedEntity.getLivingEntity() == livingEntity) {
                Misc.dropItems(livingEntity.getLocation(), new ItemStack(Material.ARROW), trackedEntity.getArrowCount());
                it.remove();
                return;
            }
        }
    }

    protected static Random getRandom() {
        return random;
    }
}
