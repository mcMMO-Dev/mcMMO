package com.gmail.nossr50.util;

import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.hardcore.McMMOPlayerDeathPenaltyEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.player.UserManager;

public final class HardcoreManager {
    private HardcoreManager() {}

    public static void invokeStatPenalty(Player player) {
        double statLossPercentage = Config.getInstance().getHardcoreDeathStatPenaltyPercentage();

        McMMOPlayerDeathPenaltyEvent eventToFire = new McMMOPlayerDeathPenaltyEvent(player);
        mcMMO.p.getServer().getPluginManager().callEvent(eventToFire);

        if (eventToFire.isCancelled()) {
            return;
        }

        PlayerProfile playerProfile = UserManager.getPlayer(player).getProfile();
        int totalLost = 0;

        for (SkillType skillType : SkillType.NON_CHILD_SKILLS) {
            int playerSkillLevel = playerProfile.getSkillLevel(skillType);

            if (playerSkillLevel <= 0) {
                continue;
            }

            int levelsLost = (int) (playerSkillLevel * (statLossPercentage * 0.01D));
            totalLost += levelsLost;

            playerProfile.modifySkill(skillType, playerSkillLevel - levelsLost);
        }

        player.sendMessage(LocaleLoader.getString("Hardcore.DeathStatLoss.PlayerDeath", totalLost));
    }

    public static void invokeVampirism(Player killer, Player victim) {
        double vampirismStatLeechPercentage = Config.getInstance().getHardcoreVampirismStatLeechPercentage();

        McMMOPlayerDeathPenaltyEvent eventToFire = new McMMOPlayerDeathPenaltyEvent(victim);
        mcMMO.p.getServer().getPluginManager().callEvent(eventToFire);

        if (eventToFire.isCancelled()) {
            return;
        }

        PlayerProfile killerProfile = UserManager.getPlayer(killer).getProfile();
        PlayerProfile victimProfile = UserManager.getPlayer(victim).getProfile();
        int totalStolen = 0;

        for (SkillType skillType : SkillType.NON_CHILD_SKILLS) {
            int killerSkillLevel = killerProfile.getSkillLevel(skillType);
            int victimSkillLevel = victimProfile.getSkillLevel(skillType);

            if (victimSkillLevel <= 0 || victimSkillLevel < killerSkillLevel / 2) {
                continue;
            }

            int levelsStolen = (int) (victimSkillLevel * (vampirismStatLeechPercentage * 0.01D));
            totalStolen += levelsStolen;

            killerProfile.modifySkill(skillType, killerSkillLevel + levelsStolen);
            victimProfile.modifySkill(skillType, victimSkillLevel - levelsStolen);
        }

        if (totalStolen > 0) {
            killer.sendMessage(LocaleLoader.getString("Hardcore.Vampirism.Killer.Success", totalStolen, victim.getName()));
            victim.sendMessage(LocaleLoader.getString("Hardcore.Vampirism.Victim.Success", killer.getName(), totalStolen));
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

        for (SkillType skillType : SkillType.NON_CHILD_SKILLS) {
            if (skillType.getHardcoreStatLossEnabled()) {
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

        for (SkillType skillType : SkillType.NON_CHILD_SKILLS) {
            if (skillType.getHardcoreVampirismEnabled()) {
                enabled = true;
                break;
            }
        }

        return enabled;
    }
}
