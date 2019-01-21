package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class BleedTimerTask extends BukkitRunnable {
    private final static int MAX_BLEED_TICKS = 10;
    private static Map<LivingEntity, Integer> bleedList = new HashMap<LivingEntity, Integer>();
    private static Map<LivingEntity, Integer> bleedDamage = new HashMap<LivingEntity, Integer>();

    @Override
    public void run() {
        for (Iterator<Entry<LivingEntity, Integer>> bleedIterator = bleedList.entrySet().iterator(); bleedIterator.hasNext(); ) {
            Entry<LivingEntity, Integer> entry = bleedIterator.next();
            LivingEntity entity = entry.getKey();

            if (entry.getValue() <= 0 || !entity.isValid()) {
                bleedIterator.remove();
                continue;
            }

            double damage;

            //Play Bleed Sound
            SoundManager.worldSendSound(entity.getWorld(), entity.getLocation(), SoundType.BLEED);

            if (entity instanceof Player) {
                damage = AdvancedConfig.getInstance().getRuptureDamagePlayer();

                //Above Bleed Rank 3 deals 50% more damage
                if(bleedDamage.get(entity) >= 3)
                    damage = damage * 1.5;

                Player player = (Player) entity;

                if (!player.isOnline()) {
                    continue;
                }

                // Never kill with Bleeding
                if (player.getHealth() - damage > 0) {
                    CombatUtils.dealDamage(player, damage);
                    ParticleEffectUtils.playBleedEffect(entity);
                }

                entry.setValue(entry.getValue() - 1);

                if (entry.getValue() <= 0) {
                    NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Swords.Combat.Bleeding.Stopped");
                }
            }
            else {
                damage = AdvancedConfig.getInstance().getRuptureDamageMobs();

                // Anticipate the entity's death to prevent CME because of our EntityDeathEvent listener
                if (entity.getHealth() - damage > 0) {
                    entry.setValue(entry.getValue() - 1);
                }
                else {
                    bleedIterator.remove();
                }

                CombatUtils.dealDamage(entity, damage);
                ParticleEffectUtils.playBleedEffect(entity);
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
            CombatUtils.dealNoInvulnerabilityTickDamage(entity, bleedList.get(entity) * 2, null);
            bleedList.remove(entity);
            bleedDamage.remove(entity);
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
            bleedDamage.remove(entity);
        }
    }

    /**
     * Add a LivingEntity to the bleedList if it is not in it.
     *
     * @param entity LivingEntity to add
     * @param ticks Number of bleeding ticks
     */
    public static void add(LivingEntity entity, int ticks, int bleedRank) {
        int newTicks = ticks;

        if (bleedList.containsKey(entity)) {
            newTicks += bleedList.get(entity);
            bleedList.put(entity, Math.min(newTicks, MAX_BLEED_TICKS));

            //Override the current bleed rank only if this one is higher
            if(bleedDamage.get(entity) < bleedRank)
                bleedDamage.put(entity, bleedRank);
        }
        else {
            bleedList.put(entity, Math.min(newTicks, MAX_BLEED_TICKS));
            bleedDamage.put(entity, bleedRank);
        }
    }

    public static boolean isBleeding(LivingEntity entity) {
        return bleedList.containsKey(entity);
    }
}
