package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.MobHealthbarUtils;
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
    private static Map<LivingEntity, BleedContainer> bleedList = new HashMap<LivingEntity, BleedContainer>();

    @Override
    public void run() {
        Iterator<Entry<LivingEntity, BleedContainer>> bleedIterator = bleedList.entrySet().iterator();

        while (bleedIterator.hasNext()) {
            Entry<LivingEntity, BleedContainer> containerEntry = bleedIterator.next();
            LivingEntity target = containerEntry.getKey();

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

    public static BleedContainer copyContainer(BleedContainer container)
    {
        LivingEntity target = container.target;
        LivingEntity source = container.damageSource;
        int bleedTicks = container.bleedTicks;
        int bleedRank = container.bleedRank;

        BleedContainer newContainer = new BleedContainer(target, bleedTicks, bleedRank, source);
        return newContainer;
    }

    /**
     * Instantly Bleed out a LivingEntity
     *
     * @param entity LivingEntity to bleed out
     */
    public static void bleedOut(LivingEntity entity) {
        /*
         * Don't remove anything from the list outside of run()
         */

        if (bleedList.containsKey(entity)) {
            CombatUtils.dealNoInvulnerabilityTickDamage(entity, bleedList.get(entity).bleedTicks * 2, bleedList.get(entity).damageSource);
        }
    }

    /**
     * Add a LivingEntity to the bleedList if it is not in it.
     *
     * @param entity LivingEntity to add
     * @param ticks Number of bleeding ticks
     */
    public static void add(LivingEntity entity, LivingEntity attacker, int ticks, int bleedRank) {
        BleedContainer newBleedContainer = new BleedContainer(entity, ticks, bleedRank, attacker);
        bleedList.put(entity, newBleedContainer);
    }

    public static boolean isBleeding(LivingEntity entity) {
        return bleedList.containsKey(entity);
    }
}
