package com.gmail.nossr50.runnables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Combat;

public class BleedTimer implements Runnable {
    private final static int MAX_BLEED_TICKS = 10;

    private static Map<LivingEntity, Integer> bleedList = new HashMap<LivingEntity, Integer>();
    private static Map<LivingEntity, Integer> bleedAddList = new HashMap<LivingEntity, Integer>();
    private static List<LivingEntity> bleedRemoveList = new ArrayList<LivingEntity>();

    private static boolean lock = false;

    @Override
    public void run() {
        updateBleedList();
        bleedSimulate();
    }

    private void bleedSimulate() {
        lock = true;

        for (Entry<LivingEntity, Integer> entry : bleedList.entrySet()) {
            LivingEntity entity = entry.getKey();

            if (entry.getValue() <= 0 || entity.isDead()) {
                remove(entity);
                break;
            }

            // Player bleed simulation
            if (entity instanceof Player) {
                Player player = (Player) entity;

                if (!player.isOnline()) {
                    continue;
                }

                //Never kill with Bleeding
                if (player.getHealth() - 1 > 0) {
                    Combat.dealDamage(player, 1);
                }

                entry.setValue(entry.getValue() - 1);

                if (entry.getValue() <= 0) {
                    player.sendMessage(LocaleLoader.getString("Swords.Combat.Bleeding.Stopped"));
                }
            }
            // Bleed monsters/animals
            else {
                Combat.dealDamage(entity, 2);
                entry.setValue(entry.getValue() - 1);
            }
        }

        // Unlock list now that we are done
        lock = false;
    }

    private void updateBleedList() {
        if (lock) {
            mcMMO.p.getLogger().warning("mcBleedTimer attempted to update the bleedList but the list was locked!");
        }
        else {
            bleedList.keySet().removeAll(bleedRemoveList);
            bleedRemoveList.clear();

            bleedList.putAll(bleedAddList);
            bleedAddList.clear();
        }
    }

    /**
     * Instantly Bleed out a LivingEntity
     *
     * @param entity LivingEntity to bleed out
     */
    public static void bleedOut(LivingEntity entity) {
        if (bleedList.containsKey(entity)) {
            Combat.dealDamage(entity, bleedList.get(entity) * 2);
            bleedList.remove(entity);
       }
    }

    /**
     * Remove a LivingEntity from the bleedList if it is in it
     *
     * @param entity LivingEntity to remove
     */
    public static void remove(LivingEntity entity) {
        if (lock) {
            if (!bleedRemoveList.contains(entity)) {
                bleedRemoveList.add(entity);
            }
        }
        else {
            if (bleedList.containsKey(entity)) {
                bleedList.remove(entity);
            }
        }
    }

    /**
     * Add a LivingEntity to the bleedList if it is not in it.
     *
     * @param entity LivingEntity to add
     * @param ticks Number of bleeding ticks
     */
    public static void add(LivingEntity entity, int ticks) {
        int newTicks = ticks;

        if (lock) {
            if (bleedAddList.containsKey(entity)) {
                newTicks += bleedAddList.get(entity);

                if (newTicks > MAX_BLEED_TICKS) {
                    newTicks = MAX_BLEED_TICKS;
                }

                bleedAddList.put(entity, newTicks);
            }
            else {
                if (newTicks > MAX_BLEED_TICKS) {
                    newTicks = MAX_BLEED_TICKS;
                }

                bleedAddList.put(entity, newTicks);
            }
        }
        else {
            if (bleedList.containsKey(entity)) {
                newTicks += bleedList.get(entity);

                if (newTicks > MAX_BLEED_TICKS) {
                    newTicks = MAX_BLEED_TICKS;
                }

                bleedList.put(entity, newTicks);

                // Need to find a better way to ensure that the entity stays in bleedList
                // when some ticks are added but already marked for removal.
                // Suggestion: Why not use Iterator.remove() and drop the lock boolean?
                if (bleedRemoveList.contains(entity)) {
                    bleedRemoveList.remove(entity);
                }
            }
            else {
                if (newTicks > MAX_BLEED_TICKS) {
                    newTicks = MAX_BLEED_TICKS;
                }

                bleedList.put(entity, newTicks);
            }
        }
    }

    /**
     * Check to see if a LivingEntity is in the bleedList
     *
     * @param entity LivingEntity to check if in the bleedList
     * @return true if in the list, false if not
     */
    public static boolean contains(LivingEntity entity) {
        return (bleedList.containsKey(entity) || bleedAddList.containsKey(entity));
    }
}
