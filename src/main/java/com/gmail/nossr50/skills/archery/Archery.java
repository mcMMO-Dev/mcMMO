package com.gmail.nossr50.skills.archery;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.util.Misc;

public class Archery {
    private static Random random = new Random();
    private static Map<LivingEntity, Integer> arrowTracker = new HashMap<LivingEntity, Integer>();

    public static final int ARROW_TRACKING_MAX_BONUS_LEVEL = 1000;

    public static final int BONUS_DAMAGE_INCREASE_LEVEL = 50;
    public static final double BONUS_DAMAGE_INCREASE_PERCENT = 0.1D;
    public static final double BONUS_DAMAGE_MAX_BONUS_PERCENTAGE = 2.0D;

    public static final int DAZE_MAX_BONUS_LEVEL = 1000;
    public static final int DAZE_MODIFIER = 4;

    protected static boolean arrowTrackerContains(LivingEntity livingEntity) {
        return arrowTracker.containsKey(livingEntity);
    }

    protected static void incrementTrackerValue(LivingEntity livingEntity) {
        arrowTracker.put(livingEntity, arrowTracker.get(livingEntity) + 1);
    }

    protected static void addToTracker(LivingEntity livingEntity) {
        arrowTracker.put(livingEntity, 1);
    }

    /**
     * Check for arrow retrieval.
     *
     * @param entity The entity hit by the arrows
     */
    public static void arrowRetrievalCheck(Entity entity) {
        Integer quantity = arrowTracker.remove(entity);

        if (quantity != null) {
            Misc.dropItems(entity.getLocation(), new ItemStack(Material.ARROW), quantity);
            arrowTracker.remove(entity);
        }
    }

    protected static Random getRandom() {
        return random;
    }
}
