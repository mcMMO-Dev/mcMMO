package com.gmail.nossr50.skills.runnables;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.CombatTools;
import com.gmail.nossr50.util.ParticleEffectUtils;

public class BleedTimer implements Runnable {
    private final static int MAX_BLEED_TICKS = 10;
    private static Map<LivingEntity, Integer> bleedList = new HashMap<LivingEntity, Integer>();

    @Override
    public void run() {
        for (Iterator<Entry<LivingEntity, Integer>> bleedIterator = bleedList.entrySet().iterator(); bleedIterator.hasNext();) {
            Entry<LivingEntity, Integer> entry = bleedIterator.next();
            LivingEntity entity = entry.getKey();

            if (entry.getValue() <= 0 || !entity.isValid()) {
                bleedIterator.remove();
                continue;
            }

            if (entity instanceof Player) {
                Player player = (Player) entity;

                if (!player.isOnline()) {
                    continue;
                }

                // Never kill with Bleeding
                if (player.getHealth() - 1 > 0) {
                    CombatTools.dealDamage(player, 1);
                    ParticleEffectUtils.playBleedEffect(entity);
                }

                entry.setValue(entry.getValue() - 1);

                if (entry.getValue() <= 0) {
                    player.sendMessage(LocaleLoader.getString("Swords.Combat.Bleeding.Stopped"));
                }
            }
            else {
                CombatTools.dealDamage(entity, 2);
                ParticleEffectUtils.playBleedEffect(entity);
                entry.setValue(entry.getValue() - 1);
            }
        }
    }

    /**
     * Instantly Bleed out a LivingEntity
     *
     * @param entity LivingEntity to bleed out
     */
    public static void bleedOut(LivingEntity entity) {
        if (bleedList.containsKey(entity)) {
            CombatTools.dealDamage(entity, bleedList.get(entity) * 2);
            bleedList.remove(entity);
        }
    }

    /**
     * Remove a LivingEntity from the bleedList if it is in it
     *
     * @param entity LivingEntity to remove
     */
    public static void remove(LivingEntity entity) {
        if (bleedList.containsKey(entity)) {
            bleedList.remove(entity);
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

        if (bleedList.containsKey(entity)) {
            newTicks += bleedList.get(entity);
            bleedList.put(entity, Math.min(newTicks, MAX_BLEED_TICKS));
        }
        else {
            bleedList.put(entity, Math.min(newTicks, MAX_BLEED_TICKS));
        }
    }
}
