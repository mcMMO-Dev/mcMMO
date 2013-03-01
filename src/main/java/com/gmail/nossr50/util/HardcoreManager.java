package com.gmail.nossr50.util;

import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.player.UserManager;

public final class HardcoreManager {
    private HardcoreManager() {}

    public static void invokeStatPenalty(Player player) {
        double statLossPercentage = Config.getInstance().getHardcoreDeathStatPenaltyPercentage();

        if (statLossPercentage <= 0 || statLossPercentage > 100) {
            return;
        }

        PlayerProfile playerProfile = UserManager.getPlayer(player).getProfile();
        int totalLost = 0;

        for (SkillType skillType : SkillType.values()) {
            if (skillType.isChildSkill()) {
                continue;
            }

            int playerSkillLevel = playerProfile.getSkillLevel(skillType);

            if (playerSkillLevel <= 0) {
                continue;
            }

            int levelsLost = (int) (playerSkillLevel * (statLossPercentage * 0.01D));
            totalLost += levelsLost;

            playerProfile.modifySkill(skillType, playerSkillLevel - levelsLost);
        }

        player.sendMessage(LocaleLoader.getString("Hardcore.Player.Loss", totalLost));
    }

    public static void invokeVampirism(Player killer, Player victim) {
        double vampirismStatLeechPercentage = Config.getInstance().getHardcoreVampirismStatLeechPercentage();

        if (vampirismStatLeechPercentage <= 0 || vampirismStatLeechPercentage > 100) {
            return;
        }

        PlayerProfile killerProfile = UserManager.getPlayer(killer).getProfile();
        PlayerProfile victimProfile = UserManager.getPlayer(victim).getProfile();
        int totalStolen = 0;

        for (SkillType skillType : SkillType.values()) {
            if (skillType.isChildSkill()) {
                continue;
            }

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
            killer.sendMessage(LocaleLoader.getString("Vampirism.Killer.Success", totalStolen, victim.getName()));
            victim.sendMessage(LocaleLoader.getString("Vampirism.Victim.Success", killer.getName(), totalStolen));
        }
        else {
            killer.sendMessage(LocaleLoader.getString("Vampirism.Killer.Failure", victim.getName()));
            victim.sendMessage(LocaleLoader.getString("Vampirism.Victim.Failure", killer.getName()));
        }
    }
}
