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
        
        for(SkillType st : SkillType.values()) {
            
            if(st.equals(SkillType.ALL))
                continue;
            
            int newValue = (int) (PP.getSkillLevel(st) - (PP.getSkillLevel(st) * (Config.getInstance().getHardcoreDeathStatPenaltyPercentage() * 0.01D)));
            
            if(newValue < 0)
                newValue = 0;
            
            PP.modifySkill(st, newValue);
        }
        
        player.sendMessage(ChatColor.GOLD+"[mcMMO] "+ChatColor.DARK_RED+"You've suffered a penalty to skills from death.");
    }
    
    public static void invokeVampirism(Player killer, Player defender) {
        if(Config.getInstance().getHardcoreVampirismStatLeechPercentage() <= 0)
            return;
        
        PlayerProfile PPk = Users.getProfile(killer);
        PlayerProfile PPd = Users.getProfile(defender);
        
        for(SkillType st : SkillType.values()) {
            if(st.equals(SkillType.ALL))
                continue;
            
            if(PPd.getSkillLevel(st) <= 0)
                continue;
            
            int newValue = (int) (PPd.getSkillLevel(st) * (Config.getInstance().getHardcoreVampirismStatLeechPercentage() * 0.01D));
            
            if(newValue <= 0)
                newValue = 1;
            
            PPk.modifySkill(st, newValue+PPk.getSkillLevel(st));
            PPd.modifySkill(st, PPd.getSkillLevel(st)-newValue);
        }
        
        killer.sendMessage(ChatColor.GOLD+"[mcMMO] "+ChatColor.DARK_AQUA+"You've stolen knowledge from that player.");
        defender.sendMessage(ChatColor.GOLD+"[mcMMO] "+ChatColor.YELLOW+killer.getName()+ChatColor.DARK_AQUA+" has stolen knowledge from you!");
    }
}