package com.gmail.nossr50.listeners;

import com.gmail.nossr50.commands.levelup.LevelUpCommandManager;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.PlayerLevelUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillTools;
import com.gmail.nossr50.worldguard.WorldGuardManager;
import com.gmail.nossr50.worldguard.WorldGuardUtils;
import java.util.LinkedHashSet;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

public class SelfListener implements Listener {
    //Used in task scheduling and other things
    private final mcMMO plugin;

    public SelfListener(mcMMO plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerLevelUp(McMMOPlayerLevelUpEvent event) {
        final Player player = event.getPlayer();
        final PrimarySkillType skill = event.getSkill();

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        //TODO: Handle proper validation at the event level
        if (mmoPlayer == null || !mmoPlayer.getProfile().isLoaded()) {
            return;
        }

        if (player.isOnline()) {
            //Players can gain multiple levels especially during xprate events
            for (int i = 0; i < event.getLevelsGained(); i++) {
                int previousLevelGained = event.getSkillLevel() - i;
                //Send player skill unlock notifications
                mmoPlayer.processUnlockNotifications(plugin, event.getSkill(),
                        previousLevelGained);
            }

            //Reset the delay timer
            RankUtils.resetUnlockDelayTimer();

            if (mcMMO.p.getGeneralConfig().getScoreboardsEnabled()) {
                ScoreboardManager.handleLevelUp(player, skill);
            }
        }

        final LevelUpCommandManager levelUpCommandManager = plugin.getLevelUpCommandManager();
        if (!levelUpCommandManager.hasRegistrations()) {
            return;
        }

        final Set<Integer> levelsGained = new LinkedHashSet<>();
        final Set<Integer> powerLevelsGained = new LinkedHashSet<>();
        final int startingLevel = event.getSkillLevel() - event.getLevelsGained();
        final int startingPowerLevel = mmoPlayer.getPowerLevel() - event.getLevelsGained();
        for (int i = 1; i <= event.getLevelsGained(); i++) {
            levelsGained.add(startingLevel + i);
            powerLevelsGained.add(startingPowerLevel + i);
        }

        levelUpCommandManager.applyLevelUp(mmoPlayer, skill, levelsGained, powerLevelsGained);
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin() == plugin) {
            // mcMMO shutting down is handled in onDisable
            return;
        }

        plugin.getLevelUpCommandManager().clearPluginRegistrations(event.getPlugin());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerXp(McMMOPlayerXpGainEvent event) {
        Player player = event.getPlayer();

        if (player.isOnline()) {
            if (mcMMO.p.getGeneralConfig().getScoreboardsEnabled()) {
                ScoreboardManager.handleXp(player, event.getSkill());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAbility(McMMOPlayerAbilityActivateEvent event) {
        Player player = event.getPlayer();
        if (player.isOnline()) {
            if (mcMMO.p.getGeneralConfig().getScoreboardsEnabled()) {
                ScoreboardManager.cooldownUpdate(event.getPlayer(), event.getSkill());
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerXpGain(McMMOPlayerXpGainEvent event) {
        Player player = event.getPlayer();
        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        //TODO: Handle proper validation at the event level
        if (mmoPlayer == null || !mmoPlayer.getProfile().isLoaded()) {
            return;
        }

        PrimarySkillType primarySkillType = event.getSkill();

        if (mmoPlayer.isDebugMode()) {
            mmoPlayer.getPlayer().sendMessage(event.getSkill().toString() + " XP Gained");
            mmoPlayer.getPlayer().sendMessage("Incoming Raw XP: " + event.getRawXpGained());
        }

        //WorldGuard XP Check
        if (event.getXpGainReason() == XPGainReason.PVE ||
                event.getXpGainReason() == XPGainReason.PVP ||
                event.getXpGainReason() == XPGainReason.SHARED_PVE ||
                event.getXpGainReason() == XPGainReason.SHARED_PVP) {
            if (WorldGuardUtils.isWorldGuardLoaded()) {
                if (!WorldGuardManager.getInstance().hasXPFlag(player)) {
                    event.setRawXpGained(0);
                    event.setCancelled(true);

                    if (mmoPlayer.isDebugMode()) {
                        mmoPlayer.getPlayer().sendMessage(
                                "No WG XP Flag - New Raw XP: " + event.getRawXpGained());
                    }

                    return;
                }
            }
        }

        if (event.getXpGainReason() == XPGainReason.COMMAND) {
            return;
        }

        if (ExperienceConfig.getInstance().isEarlyGameBoostEnabled()) {

            int earlyGameBonusXP = 0;

            //Give some bonus XP for low levels
            if (PlayerLevelUtils.qualifiesForEarlyGameBoost(mmoPlayer, primarySkillType)) {
                earlyGameBonusXP += (mmoPlayer.getXpToLevel(primarySkillType) * 0.05);
                event.setRawXpGained(event.getRawXpGained() + earlyGameBonusXP);
            }
        }

        int threshold = ExperienceConfig.getInstance()
                .getDiminishedReturnsThreshold(primarySkillType);

        if (threshold <= 0 || !ExperienceConfig.getInstance().getDiminishedReturnsEnabled()) {
            if (mmoPlayer.isDebugMode()) {
                mmoPlayer.getPlayer().sendMessage("Final Raw XP: " + event.getRawXpGained());
            }
            // Diminished returns is turned off
            return;
        }

        if (event.getRawXpGained() <= 0) {
            // Don't calculate for XP subtraction
            return;
        }

        if (SkillTools.isChildSkill(primarySkillType)) {
            return;
        }

        final DiminishedReturns.Result result = DiminishedReturns.apply(
                event.getRawXpGained(),
                mmoPlayer.getProfile().getRegisteredXpGain(primarySkillType),
                threshold,
                ExperienceConfig.getInstance().getFormulaSkillModifier(primarySkillType),
                ExperienceConfig.getInstance().getExperienceGainsMultiplier(primarySkillType),
                ExperienceConfig.getInstance().getDiminishedReturnsCap());

        if (result.cancelled()) {
            event.setCancelled(true);
        } else if (result.changed()) {
            event.setRawXpGained(result.rawXp());
        }

        if (mmoPlayer.isDebugMode()) {
            mmoPlayer.getPlayer().sendMessage("Final Raw XP: " + event.getRawXpGained());
        }
    }


}
