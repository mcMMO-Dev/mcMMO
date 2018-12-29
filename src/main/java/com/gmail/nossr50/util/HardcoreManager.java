package com.gmail.nossr50.util;

import java.util.HashMap;

import com.gmail.nossr50.datatypes.skills.PrimarySkill;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.player.UserManager;

public final class HardcoreManager {
    private HardcoreManager() {}

    public static void invokeStatPenalty(Player player) {
        double statLossPercentage = Config.getInstance().getHardcoreDeathStatPenaltyPercentage();
        int levelThreshold = Config.getInstance().getHardcoreDeathStatPenaltyLevelThreshold();

        PlayerProfile playerProfile = UserManager.getPlayer(player).getProfile();
        int totalLevelsLost = 0;

        HashMap<String, Integer> levelChanged = new HashMap<String, Integer>();
        HashMap<String, Float> experienceChanged = new HashMap<String, Float>();

        for (PrimarySkill primarySkill : PrimarySkill.NON_CHILD_SKILLS) {
            if (!primarySkill.getHardcoreStatLossEnabled()) {
                levelChanged.put(primarySkill.toString(), 0);
                experienceChanged.put(primarySkill.toString(), 0F);
                continue;
            }

            int playerSkillLevel = playerProfile.getSkillLevel(primarySkill);
            int playerSkillXpLevel = playerProfile.getSkillXpLevel(primarySkill);

            if (playerSkillLevel <= 0 || playerSkillLevel <= levelThreshold) {
                levelChanged.put(primarySkill.toString(), 0);
                experienceChanged.put(primarySkill.toString(), 0F);
                continue;
            }

            double statsLost = playerSkillLevel * (statLossPercentage * 0.01D);
            int levelsLost = (int) statsLost;
            int xpLost = (int) Math.floor(playerSkillXpLevel * (statsLost - levelsLost));
            levelChanged.put(primarySkill.toString(), levelsLost);
            experienceChanged.put(primarySkill.toString(), (float) xpLost);

            totalLevelsLost += levelsLost;
        }

        if (!EventUtils.handleStatsLossEvent(player, levelChanged, experienceChanged)) {
            return;
        }

        player.sendMessage(LocaleLoader.getString("Hardcore.DeathStatLoss.PlayerDeath", totalLevelsLost));
    }

    public static void invokeVampirism(Player killer, Player victim) {
        double vampirismStatLeechPercentage = Config.getInstance().getHardcoreVampirismStatLeechPercentage();
        int levelThreshold = Config.getInstance().getHardcoreVampirismLevelThreshold();

        PlayerProfile killerProfile = UserManager.getPlayer(killer).getProfile();
        PlayerProfile victimProfile = UserManager.getPlayer(victim).getProfile();
        int totalLevelsStolen = 0;

        HashMap<String, Integer> levelChanged = new HashMap<String, Integer>();
        HashMap<String, Float> experienceChanged = new HashMap<String, Float>();

        for (PrimarySkill primarySkill : PrimarySkill.NON_CHILD_SKILLS) {
            if (!primarySkill.getHardcoreVampirismEnabled()) {
                levelChanged.put(primarySkill.toString(), 0);
                experienceChanged.put(primarySkill.toString(), 0F);
                continue;
            }

            int killerSkillLevel = killerProfile.getSkillLevel(primarySkill);
            int victimSkillLevel = victimProfile.getSkillLevel(primarySkill);

            if (victimSkillLevel <= 0 || victimSkillLevel < killerSkillLevel / 2 || victimSkillLevel <= levelThreshold) {
                levelChanged.put(primarySkill.toString(), 0);
                experienceChanged.put(primarySkill.toString(), 0F);
                continue;
            }

            int victimSkillXpLevel = victimProfile.getSkillXpLevel(primarySkill);

            double statsStolen = victimSkillLevel * (vampirismStatLeechPercentage * 0.01D);
            int levelsStolen = (int) statsStolen;
            int xpStolen = (int) Math.floor(victimSkillXpLevel * (statsStolen - levelsStolen));
            levelChanged.put(primarySkill.toString(), levelsStolen);
            experienceChanged.put(primarySkill.toString(), (float) xpStolen);

            totalLevelsStolen += levelsStolen;
        }

        if (!EventUtils.handleVampirismEvent(killer, victim, levelChanged, experienceChanged)) {
            return;
        }

        if (totalLevelsStolen > 0) {
            killer.sendMessage(LocaleLoader.getString("Hardcore.Vampirism.Killer.Success", totalLevelsStolen, victim.getName()));
            victim.sendMessage(LocaleLoader.getString("Hardcore.Vampirism.Victim.Success", killer.getName(), totalLevelsStolen));
        }
        else {
            killer.sendMessage(LocaleLoader.getString("Hardcore.Vampirism.Killer.Failure", victim.getName()));
            victim.sendMessage(LocaleLoader.getString("Hardcore.Vampirism.Victim.Failure", killer.getName()));
        }
    }

    /**
     * Check if Hardcore Stat Loss is enabled for one or more skill types
     *
     * @return true if Stat Loss is enabled for one or more skill types
     */
    public static boolean isStatLossEnabled() {
        boolean enabled = false;

        for (PrimarySkill primarySkill : PrimarySkill.NON_CHILD_SKILLS) {
            if (primarySkill.getHardcoreStatLossEnabled()) {
                enabled = true;
                break;
            }
        }

        return enabled;
    }

    /**
     * Check if Hardcore Vampirism is enabled for one or more skill types
     *
     * @return true if Vampirism is enabled for one or more skill types
     */
    public static boolean isVampirismEnabled() {
        boolean enabled = false;

        for (PrimarySkill primarySkill : PrimarySkill.NON_CHILD_SKILLS) {
            if (primarySkill.getHardcoreVampirismEnabled()) {
                enabled = true;
                break;
            }
        }

        return enabled;
    }
}
