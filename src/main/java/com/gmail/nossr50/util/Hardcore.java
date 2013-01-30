package com.gmail.nossr50.util;

import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.SkillType;

public final class Hardcore {
    public static double statLossPercentage = Config.getInstance().getHardcoreDeathStatPenaltyPercentage();
    public static double vampirismStatLeechPercentage = Config.getInstance().getHardcoreVampirismStatLeechPercentage();
    public static boolean statLossEnabled = Config.getInstance().getHardcoreEnabled();
    public static boolean vampirismEnabled = Config.getInstance().getHardcoreVampirismEnabled();

    private Hardcore() {}

    public static void invokeStatPenalty(Player player) {
        if (statLossPercentage <= 0 || statLossPercentage > 100) {
            return;
        }

        PlayerProfile playerProfile = Users.getProfile(player);
        int totalLost = 0;

        for (SkillType skillType : SkillType.values()) {
            if (skillType.equals(SkillType.ALL) || skillType.isChildSkill()) {
                continue;
            }

            int playerSkillLevel = playerProfile.getSkillLevel(skillType);

            //Should we really care about negative skill levels?
            if (playerSkillLevel <= 0) {
                continue;
            }

            int levelsLost = (int) (playerSkillLevel * (statLossPercentage * 0.01D));
            totalLost += levelsLost;

            playerProfile.modifySkill(skillType, playerSkillLevel - levelsLost);
        }

        player.sendMessage(LocaleLoader.getString("Hardcore.Player.Loss", new Object[] {totalLost}));
    }

    public static void invokeVampirism(Player killer, Player victim) {
        if (vampirismStatLeechPercentage <= 0 || vampirismStatLeechPercentage > 100) {
            return;
        }

        PlayerProfile killerProfile = Users.getProfile(killer);
        PlayerProfile victimProfile = Users.getProfile(victim);
        int totalStolen = 0;

        for (SkillType skillType : SkillType.values()) {
            if (skillType.equals(SkillType.ALL) || skillType.isChildSkill()) {
                continue;
            }

            int killerSkillLevel = killerProfile.getSkillLevel(skillType);
            int victimSkillLevel = victimProfile.getSkillLevel(skillType);

            //Should we really care about negative skill levels?
            if (victimSkillLevel <= 0 || victimSkillLevel < killerSkillLevel / 2) {
                continue;
            }

            int levelsStolen = (int) (victimSkillLevel * (vampirismStatLeechPercentage * 0.01D));
            totalStolen += levelsStolen;

            killerProfile.modifySkill(skillType, killerSkillLevel + levelsStolen);
            victimProfile.modifySkill(skillType, victimSkillLevel - levelsStolen);
        }

        if (totalStolen > 0) {
            killer.sendMessage(LocaleLoader.getString("Vampirism.Killer.Success", new Object[] {totalStolen, victim.getName()} ));
            victim.sendMessage(LocaleLoader.getString("Vampirism.Victim.Success", new Object[] {killer.getName(), totalStolen} ));
        }
        else {
            killer.sendMessage(LocaleLoader.getString("Vampirism.Killer.Failure", new Object[] {victim.getName()} ));
            victim.sendMessage(LocaleLoader.getString("Vampirism.Victim.Failure", new Object[] {killer.getName()} ));
        }
    }
}