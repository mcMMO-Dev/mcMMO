package com.gmail.nossr50.runnables;

import org.bukkit.entity.*;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.skills.Skills;

public class mcTimer implements Runnable
{
	private final mcMMO plugin;

    public mcTimer(final mcMMO plugin) 
    {
        this.plugin = plugin;
    }
    
	public void run() 
	{
		long curTime = System.currentTimeMillis();
		for(Player player : plugin.getServer().getOnlinePlayers())
		{
			if(player == null)
				continue;
			PlayerProfile PP = Users.getProfile(player);
			
			if(PP == null)
				continue;
			
			/*
			 * MONITOR SKILLS
			 */
			Skills.monitorSkill(player, PP, curTime, SkillType.AXES);
			Skills.monitorSkill(player, PP, curTime, SkillType.EXCAVATION);
			Skills.monitorSkill(player, PP, curTime, SkillType.HERBALISM);
			Skills.monitorSkill(player, PP, curTime, SkillType.MINING);
			Skills.monitorSkill(player, PP, curTime, SkillType.SWORDS);
			Skills.monitorSkill(player, PP, curTime, SkillType.UNARMED);
			Skills.monitorSkill(player, PP, curTime, SkillType.WOODCUTTING);
			
			/*
			 * COOLDOWN MONITORING
			 */
			Skills.watchCooldown(player, PP, curTime, AbilityType.SKULL_SPLIITER);
			Skills.watchCooldown(player, PP, curTime, AbilityType.GIGA_DRILL_BREAKER);
			Skills.watchCooldown(player, PP, curTime, AbilityType.GREEN_TERRA);
			Skills.watchCooldown(player, PP, curTime, AbilityType.SUPER_BREAKER);
			Skills.watchCooldown(player, PP, curTime, AbilityType.SERRATED_STRIKES);
			Skills.watchCooldown(player, PP, curTime, AbilityType.BERSERK);
			Skills.watchCooldown(player, PP, curTime, AbilityType.TREE_FELLER);
			Skills.watchCooldown(player, PP, curTime, AbilityType.BLAST_MINING);
		}
	}
}
