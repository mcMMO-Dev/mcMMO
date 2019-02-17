package com.gmail.nossr50.listeners;

import com.gmail.nossr50.config.MainConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.gmail.nossr50.worldguard.WorldGuardManager;
import com.gmail.nossr50.worldguard.WorldGuardUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SelfListener implements Listener {
    //Used in task scheduling and other things
    private final mcMMO plugin;

    public SelfListener(mcMMO plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLevelUp(McMMOPlayerLevelUpEvent event) {
        Player player = event.getPlayer();
        PrimarySkillType skill = event.getSkill();

        //Players can gain multiple levels especially during xprate events
        for(int i = 0; i < event.getLevelsGained(); i++)
        {
            int previousLevelGained = event.getSkillLevel() - i;
            //Send player skill unlock notifications
            UserManager.getPlayer(player).processUnlockNotifications(plugin, event.getSkill(), previousLevelGained);
        }

        if(MainConfig.getInstance().getScoreboardsEnabled())
            ScoreboardManager.handleLevelUp(player, skill);

        if (!MainConfig.getInstance().getLevelUpEffectsEnabled()) {
            return;
        }

        if ((event.getSkillLevel() % MainConfig.getInstance().getLevelUpEffectsTier()) == 0) {
            skill.celebrateLevelUp(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerXp(McMMOPlayerXpGainEvent event) {
        if(MainConfig.getInstance().getScoreboardsEnabled())
            ScoreboardManager.handleXp(event.getPlayer(), event.getSkill());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAbility(McMMOPlayerAbilityActivateEvent event) {
        if(MainConfig.getInstance().getScoreboardsEnabled())
            ScoreboardManager.cooldownUpdate(event.getPlayer(), event.getSkill());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerXpGain(McMMOPlayerXpGainEvent event) {
        Player player = event.getPlayer();
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        PrimarySkillType primarySkillType = event.getSkill();

        //WorldGuard XP Check
        if(event.getXpGainReason() == XPGainReason.PVE ||
                event.getXpGainReason() == XPGainReason.PVP ||
                event.getXpGainReason() == XPGainReason.SHARED_PVE ||
                event.getXpGainReason() == XPGainReason.SHARED_PVP)
        {
            if(WorldGuardUtils.isWorldGuardLoaded())
            {
                if(!WorldGuardManager.getInstance().hasXPFlag(player))
                {
                    event.setRawXpGained(0);
                    event.setCancelled(true);
                }
            }
        }

        if (event.getXpGainReason() == XPGainReason.COMMAND)
        {
            return;
        }

        int threshold = ExperienceConfig.getInstance().getDiminishedReturnsThreshold(primarySkillType);

        if (threshold <= 0 || !ExperienceConfig.getInstance().getDiminishedReturnsEnabled()) {
            // Diminished returns is turned off
            return;
        }

        final float rawXp = event.getRawXpGained();
        if (rawXp < 0) {
            // Don't calculate for XP subtraction
            return;
        }

        if (primarySkillType.isChildSkill()) {
            return;
        }

        float guaranteedMinimum = ExperienceConfig.getInstance().getDiminishedReturnsCap() * rawXp;

        float modifiedThreshold = (float) (threshold / primarySkillType.getXpModifier() * ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier());
        float difference = (mcMMOPlayer.getProfile().getRegisteredXpGain(primarySkillType) - modifiedThreshold) / modifiedThreshold;

        if (difference > 0) {
//            System.out.println("Total XP Earned: " + mcMMOPlayer.getProfile().getRegisteredXpGain(primarySkillType) + " / Threshold value: " + threshold);
//            System.out.println(difference * 100 + "% over the threshold!");
//            System.out.println("Previous: " + event.getRawXpGained());
//            System.out.println("Adjusted XP " + (event.getRawXpGained() - (event.getRawXpGained() * difference)));
            float newValue = rawXp - (rawXp * difference);

            /*
             * Make sure players get a guaranteed minimum of XP
             */
            //If there is no guaranteed minimum proceed, otherwise only proceed if newValue would be higher than our guaranteed minimum
            if(guaranteedMinimum <= 0 || newValue > guaranteedMinimum)
            {
                if (newValue > 0) {
                    event.setRawXpGained(newValue);
                }
                else {
                    event.setCancelled(true);
                }
            } else {
                event.setRawXpGained(guaranteedMinimum);
            }

        }
    }
}
