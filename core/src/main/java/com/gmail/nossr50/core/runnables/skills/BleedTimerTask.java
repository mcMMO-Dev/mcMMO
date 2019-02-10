package com.gmail.nossr50.core.runnables.skills;

import com.gmail.nossr50.core.config.AdvancedConfig;
import com.gmail.nossr50.core.datatypes.interactions.NotificationType;
import com.gmail.nossr50.core.mcmmo.entity.Living;
import com.gmail.nossr50.core.mcmmo.entity.Player;
import com.gmail.nossr50.core.util.MobHealthbarUtils;
import com.gmail.nossr50.core.util.player.NotificationManager;
import com.gmail.nossr50.core.util.skills.CombatUtils;
import com.gmail.nossr50.core.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.core.util.sounds.SoundManager;
import com.gmail.nossr50.core.util.sounds.SoundType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class BleedTimerTask extends BukkitRunnable {
    private static Map<UUID, BleedContainer> bleedList = new HashMap<UUID, BleedContainer>();

    public static BleedContainer copyContainer(BleedContainer container) {
        Living target = container.target;
        Living source = container.damageSource;
        int bleedTicks = container.bleedTicks;
        int bleedRank = container.bleedRank;

        BleedContainer newContainer = new BleedContainer(target, bleedTicks, bleedRank, source);
        return newContainer;
    }

    /**
     * Instantly Bleed out a Living
     *
     * @param entity Living to bleed out
     */
    public static void bleedOut(Living entity) {
        /*
         * Don't remove anything from the list outside of run()
         */

        if (bleedList.containsKey(entity)) {
            CombatUtils.dealNoInvulnerabilityTickDamage(entity, bleedList.get(entity).bleedTicks * 2, bleedList.get(entity).damageSource);
        }
    }

    /**
     * Add a Living to the bleedList if it is not in it.
     *
     * @param entity Living to add
     * @param ticks  Number of bleeding ticks
     */
    public static void add(Living entity, Living attacker, int ticks, int bleedRank) {
        BleedContainer newBleedContainer = new BleedContainer(entity, ticks, bleedRank, attacker);
        bleedList.put(entity, newBleedContainer);
    }

    public static boolean isBleeding(Living entity) {
        return bleedList.containsKey(entity);
    }

    @Override
    public void run() {
        Iterator<Entry<Living, BleedContainer>> bleedIterator = bleedList.entrySet().iterator();

        while (bleedIterator.hasNext()) {
            Entry<Living, BleedContainer> containerEntry = bleedIterator.next();
            Living target = containerEntry.getKey();

            int bleedTicks = containerEntry.getValue().bleedTicks;

            if (containerEntry.getValue().bleedTicks <= 0 || !target.isValid()) {
                bleedIterator.remove();
                continue;
            }

            double damage;

            if (target instanceof Player) {
                damage = AdvancedConfig.getInstance().getRuptureDamagePlayer();

                //Above Bleed Rank 3 deals 50% more damage
                if (containerEntry.getValue().bleedRank >= 3)
                    damage = damage * 1.5;

                Player player = (Player) target;

                if (!player.isOnline()) {
                    continue;
                }

                NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Swords.Combat.Bleeding.Stopped");
            } else {
                damage = AdvancedConfig.getInstance().getRuptureDamageMobs();

                //Above Bleed Rank 3 deals 50% more damage
                if (containerEntry.getValue().bleedRank >= 3)
                    damage = damage * 1.5;


                MobHealthbarUtils.handleMobHealthbars(target, damage, mcMMO.p); //Update health bars
            }

            CombatUtils.dealNoInvulnerabilityTickDamage(target, damage, containerEntry.getValue().damageSource);
            //Play Bleed Sound
            SoundManager.worldSendSound(target.getWorld(), target.getLocation(), SoundType.BLEED);

            ParticleEffectUtils.playBleedEffect(target);

            //Lower Bleed Ticks
            BleedContainer loweredBleedContainer = copyContainer(containerEntry.getValue());
            loweredBleedContainer.bleedTicks -= 1;
            containerEntry.setValue(loweredBleedContainer);
        }
    }
}
