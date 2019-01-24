package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.MobHealthbarUtils;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BleedTimerTask extends BukkitRunnable {
    private final static int MAX_BLEED_TICKS = 100; //The cap has been raised :)
    private static Map<LivingEntity, Integer> bleedList = new HashMap<LivingEntity, Integer>();
    private static Map<LivingEntity, Integer> bleedDamage = new HashMap<LivingEntity, Integer>();
    private static Map<LivingEntity, LivingEntity> attackerMap = new HashMap<>();
    private static ArrayList<LivingEntity> cleanupList = new ArrayList<>();
    private static ArrayList<LivingEntity> lowerList = new ArrayList<>();

    @Override
    public void run() {
        lowerBleedTicks(); //Lower bleed ticks
        cleanEntities(); //Remove unwanted entities

        for(LivingEntity target : bleedList.keySet())
        {
            //mcMMO.p.getServer().broadcastMessage("Entity "+target.getName()+" has "+bleedList.get(target)+" ticks of bleed left");

            if (bleedList.get(target) <= 0 || !target.isValid()) {
                cleanupList.add(target);
                continue;
            }

            double damage;

            if (target instanceof Player) {
                damage = AdvancedConfig.getInstance().getRuptureDamagePlayer();

                //Above Bleed Rank 3 deals 50% more damage
                if(bleedDamage.get(target) >= 3)
                    damage = damage * 1.5;

                Player player = (Player) target;

                if (!player.isOnline()) {
                    cleanupList.add(target);
                    continue;
                }

                /*if (bleedList.get(target) <= 0) {
                    NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Swords.Combat.Bleeding.Stopped");
                }*/
            }
            else {
                damage = AdvancedConfig.getInstance().getRuptureDamageMobs();
                MobHealthbarUtils.handleMobHealthbars(target, damage, mcMMO.p); //Update health bars
            }


            CombatUtils.dealNoInvulnerabilityTickDamage(target, damage, attackerMap.get(target));
            //Play Bleed Sound
            SoundManager.worldSendSound(target.getWorld(), target.getLocation(), SoundType.BLEED);

            ParticleEffectUtils.playBleedEffect(target);
            lowerBleedDurationTicks(target);
        }
    }

    private void lowerBleedTicks() {
        for(LivingEntity lower : lowerList)
        {
            if(bleedList.containsKey(lower))
                bleedList.put(lower, bleedList.get(lower) - 1);
        }

        lowerList.clear();
    }

    private void cleanEntities() {
        for(LivingEntity cleanTarget : cleanupList)
        {
            if(bleedList.containsKey(cleanTarget))
            {
                remove(cleanTarget);
            }
        }

        cleanupList.clear(); //Reset List
    }

    private void lowerBleedDurationTicks(LivingEntity target) {
        if(bleedList.get(target) != null)
            lowerList.add(target);
    }

    /**
     * Instantly Bleed out a LivingEntity
     *
     * @param entity LivingEntity to bleed out
     */
    public static void bleedOut(LivingEntity entity) {
        if (bleedList.containsKey(entity)) {
            CombatUtils.dealNoInvulnerabilityTickDamage(entity, bleedList.get(entity) * 2, attackerMap.get(entity));
            bleedList.remove(entity);
            bleedDamage.remove(entity);
            attackerMap.remove(entity);
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
            attackerMap.remove(entity);
        }
    }

    /**
     * Add a LivingEntity to the bleedList if it is not in it.
     *
     * @param entity LivingEntity to add
     * @param ticks Number of bleeding ticks
     */
    public static void add(LivingEntity entity, LivingEntity attacker, int ticks, int bleedRank) {
        int newTicks = ticks;

        if (bleedList.containsKey(entity)) {
            newTicks += bleedList.get(entity);
            bleedList.put(entity, Math.min(MAX_BLEED_TICKS, newTicks));

            //Override the current bleed rank only if this one is higher
            if(bleedDamage.get(entity) < bleedRank)
                bleedDamage.put(entity, bleedRank);
        }
        else {
            bleedList.put(entity, Math.min(MAX_BLEED_TICKS, newTicks));
            bleedDamage.put(entity, bleedRank);
            attackerMap.put(entity, attacker);
        }
    }

    public static boolean isBleeding(LivingEntity entity) {
        return bleedList.containsKey(entity);
    }
}
