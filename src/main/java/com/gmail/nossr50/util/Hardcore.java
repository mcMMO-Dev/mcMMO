package com.gmail.nossr50.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;

public class Hardcore {
    public static void invokeStatPenalty(Player player) {
        PlayerProfile PP = Users.getProfile(player);
        
        for(SkillType st : SkillType.values()) {
            if(st.equals(SkillType.ALL))
                continue;
            int newValue = (int) (PP.getSkillLevel(st) / Config.getInstance().getHardcoreDeathStatPenaltyPercentage());
            
            if(newValue < 0)
                newValue = 0;
            
            PP.modifySkill(st, newValue);
        }
        
        player.sendMessage(ChatColor.GOLD+"[mcMMO] "+ChatColor.DARK_RED+"You've suffered a penalty to your knowledge from death.");
    }
}