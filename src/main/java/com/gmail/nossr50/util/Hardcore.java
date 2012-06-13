package com.gmail.nossr50.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;

public class Hardcore {
    public static void invokeStatPenalty(Player player) {
        if(Config.getInstance().getHardcoreDeathStatPenaltyPercentage() <= 0)
            return;

        PlayerProfile PP = Users.getProfile(player);

        int totalCount = 0;

        for(SkillType st : SkillType.values()) {

            if(st.equals(SkillType.ALL))
                continue;

            int newValue = (int) (PP.getSkillLevel(st) - (PP.getSkillLevel(st) * (Config.getInstance().getHardcoreDeathStatPenaltyPercentage() * 0.01D)));

            if(newValue < 0)
                newValue = 0;

            totalCount+=PP.getSkillLevel(st)-newValue;

            PP.modifySkill(st, newValue);
        }

        player.sendMessage(ChatColor.GOLD+"[mcMMO] "+ChatColor.DARK_RED+"You've lost "+ChatColor.BLUE+totalCount+ChatColor.DARK_RED+" from death.");
    }

    public static void invokeVampirism(Player killer, Player defender) {
        if(Config.getInstance().getHardcoreVampirismStatLeechPercentage() <= 0)
            return;

        PlayerProfile PPk = Users.getProfile(killer);
        PlayerProfile PPd = Users.getProfile(defender);

        int totalCount = 0;

        for(SkillType st : SkillType.values()) {
            if(st.equals(SkillType.ALL))
                continue;

            if(PPd.getSkillLevel(st) <= 0 || PPd.getSkillLevel(st) < (PPk.getSkillLevel(st)/2))
                continue;

            int newValue = (int) (PPd.getSkillLevel(st) * (Config.getInstance().getHardcoreVampirismStatLeechPercentage() * 0.01D));

            if(newValue <= 0)
                newValue = 1;

            totalCount += newValue;

            PPk.modifySkill(st, newValue+PPk.getSkillLevel(st));
            PPd.modifySkill(st, PPd.getSkillLevel(st)-newValue);
        }

        if(totalCount >= 1) {
            killer.sendMessage(ChatColor.GOLD+"[mcMMO] "+ChatColor.DARK_AQUA+"You've stolen "+ChatColor.BLUE+totalCount+ChatColor.DARK_AQUA+" levels from that player.");
            defender.sendMessage(ChatColor.GOLD+"[mcMMO] "+ChatColor.YELLOW+killer.getName()+ChatColor.DARK_RED+" has stolen "+ChatColor.BLUE+totalCount+ChatColor.DARK_RED+" levels from you!");
        } else {
            killer.sendMessage(ChatColor.GOLD+"[mcMMO] "+ChatColor.GRAY+"That player was too unskilled to grant you any knowledge.");
            defender.sendMessage(ChatColor.GOLD+"[mcMMO] "+ChatColor.YELLOW+killer.getName()+ChatColor.GRAY+" was unable to steal knowledge from you!");
        }
    }
}