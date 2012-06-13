package com.gmail.nossr50.skills.archery;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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

    public static final int DAZE_MAX_BONUS_LEVEL = 1000;
    public static final int DAZE_MODIFIER = 4;

//    protected static Set<Entry<Entity, Integer>> getEntitySet() {
//        return arrowTracker.entrySet();
//    }

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
        for (Iterator<Entry<LivingEntity, Integer>> it = arrowTracker.entrySet().iterator() ; it.hasNext() ; ) { //This is a wee bit confusing...
            Entry<LivingEntity, Integer> entry = it.next();

            if (entry.getKey() == entity) { //Shouldn't we be using .equals() here?
                Misc.dropItems(entity.getLocation(), new ItemStack(Material.ARROW), entry.getValue());
                it.remove();
                return;
            }
        }
    }

    protected static Random getRandom() {
        return random;
    }
}
