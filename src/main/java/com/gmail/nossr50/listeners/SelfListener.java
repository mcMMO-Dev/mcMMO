package com.gmail.nossr50.listeners;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.PlayerLevelUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.worldguard.WorldGuardUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SelfListener implements Listener {
    //Used in task scheduling and other things
    private final mcMMO pluginRef;

    public SelfListener(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLevelUp(McMMOPlayerLevelUpEvent event) {
        Player player = event.getPlayer();
        PrimarySkillType skill = event.getSkill();

        //Players can gain multiple levels especially during xprate events
        for (int i = 0; i < event.getLevelsGained(); i++) {
            int previousLevelGained = event.getSkillLevel() - i;
            //Send player skill unlock notifications
            pluginRef.getUserManager().getPlayer(player).processUnlockNotifications(pluginRef, event.getSkill(), previousLevelGained);
        }

        //Reset the delay timer
        RankUtils.resetUnlockDelayTimer();

        if (pluginRef.getScoreboardSettings().getScoreboardsEnabled())
            pluginRef.getScoreboardManager().handleLevelUp(player, skill);

        /*if ((event.getSkillLevel() % Config.getInstance().getLevelUpEffectsTier()) == 0) {
            skill.celebrateLevelUp(player);
        }*/
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerXp(McMMOPlayerXpGainEvent event) {
        if (pluginRef.getScoreboardSettings().getScoreboardsEnabled())
            pluginRef.getScoreboardManager().handleXp(event.getPlayer(), event.getSkill());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAbility(McMMOPlayerAbilityActivateEvent event) {
        if (pluginRef.getScoreboardSettings().getScoreboardsEnabled())
            pluginRef.getScoreboardManager().cooldownUpdate(event.getPlayer(), event.getSkill());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerXpGain(McMMOPlayerXpGainEvent event) {
        Player player = event.getPlayer();
        McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);
        PrimarySkillType primarySkillType = event.getSkill();

        //WorldGuard XP Check
        if (event.getXpGainReason() == XPGainReason.PVE ||
                event.getXpGainReason() == XPGainReason.PVP ||
                event.getXpGainReason() == XPGainReason.SHARED_PVE ||
                event.getXpGainReason() == XPGainReason.SHARED_PVP) {
            if (WorldGuardUtils.isWorldGuardLoaded()) {
                if (!pluginRef.getWorldGuardManager().hasXPFlag(player)) {
                    event.setRawXpGained(0);
                    event.setCancelled(true);
                }
            }
        }

        if (event.getXpGainReason() == XPGainReason.COMMAND) {
            return;
        }

        if (pluginRef.getConfigManager().getConfigLeveling().isEnableEarlyGameBoost()) {

            int earlyGameBonusXP = 0;

            //Give some bonus XP for low levels
            if(PlayerLevelUtils.qualifiesForEarlyGameBoost(mcMMOPlayer, primarySkillType))
            {
                earlyGameBonusXP += (mcMMOPlayer.getXpToLevel(primarySkillType) * 0.05);
                event.setRawXpGained(event.getRawXpGained() + earlyGameBonusXP);
            }
        }

        int threshold = pluginRef.getConfigManager().getConfigLeveling().getSkillThreshold(primarySkillType);

        if (threshold <= 0 || !pluginRef.getConfigManager().getConfigLeveling().getConfigLevelingDiminishedReturns().isDiminishedReturnsEnabled()) {
            // Diminished returns is turned off
            return;
        }

        if (event.getRawXpGained() <= 0) {
            // Don't calculate for XP subtraction
            return;
        }

        if (primarySkillType.isChildSkill()) {
            return;
        }

        final double rawXp = event.getRawXpGained();

        double guaranteedMinimum = pluginRef.getConfigManager().getConfigLeveling().getGuaranteedMinimums() * rawXp;

        double modifiedThreshold = (double) (threshold / primarySkillType.getXpModifier() * pluginRef.getDynamicSettingsManager().getExperienceManager().getGlobalXpMult());
        double difference = (mcMMOPlayer.getProfile().getRegisteredXpGain(primarySkillType) - modifiedThreshold) / modifiedThreshold;

        if (difference > 0) {
//            System.out.println("Total XP Earned: " + mcMMOPlayer.getProfile().getRegisteredXpGain(primarySkillType) + " / Threshold value: " + threshold);
//            System.out.println(difference * 100 + "% over the threshold!");
//            System.out.println("Previous: " + event.getRawXpGained());
//            System.out.println("Adjusted XP " + (event.getRawXpGained() - (event.getRawXpGained() * difference)));
            double newValue = rawXp - (rawXp * difference);

            /*
             * Make sure players get a guaranteed minimum of XP
             */
            //If there is no guaranteed minimum proceed, otherwise only proceed if newValue would be higher than our guaranteed minimum
            if (guaranteedMinimum <= 0 || newValue > guaranteedMinimum) {
                if (newValue > 0) {
                    event.setRawXpGained(newValue);
                } else {
                    event.setCancelled(true);
                }
            } else {
                event.setRawXpGained(guaranteedMinimum);
            }

        }
    }


}
