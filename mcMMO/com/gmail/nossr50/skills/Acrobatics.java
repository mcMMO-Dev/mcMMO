package com.gmail.nossr50.skills;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;


public class Acrobatics {
	public static void acrobaticsCheck(Player player, EntityDamageEvent event)
	{
    	if(player != null && mcPermissions.getInstance().acrobatics(player))
    	{
    		PlayerProfile PP = Users.getProfile(player);
    		int acrovar = PP.getSkillLevel(SkillType.ACROBATICS);
    		
    		if(player.isSneaking())
    			acrovar = acrovar * 2;
    		
			if(Math.random() * 1000 <= acrovar && !event.isCancelled())
			{
				int threshold = 7;
				if(player.isSneaking())
					threshold = 14;
				
				int newDamage = event.getDamage() - threshold;
				if(newDamage < 0)
					newDamage = 0;
				/*
				 * Check for death
				 */
				if(player.getHealth() - newDamage >= 1){
					if(!event.isCancelled())
						PP.addXP(SkillType.ACROBATICS, (event.getDamage() * 8)*10);
					Skills.XpCheckSkill(SkillType.ACROBATICS, player);
					event.setDamage(newDamage);
					if(event.getDamage() <= 0)
						event.setCancelled(true);
					if(player.isSneaking()){
						player.sendMessage(ChatColor.GREEN+"**GRACEFUL ROLL**");
					} else {
						player.sendMessage("**ROLL**");
					}
				}
			} else if (!event.isCancelled()){
				if(player.getHealth() - event.getDamage() >= 1){
					PP.addXP(SkillType.ACROBATICS, (event.getDamage() * 12)*10);
					Skills.XpCheckSkill(SkillType.ACROBATICS, player);
				}
			}
    	}
    }
	public static void dodgeChecks(EntityDamageByEntityEvent event){
		Player defender = (Player) event.getEntity();
		PlayerProfile PPd = Users.getProfile(defender);
		
		if(mcPermissions.getInstance().acrobatics(defender)){
			if(PPd.getSkillLevel(SkillType.ACROBATICS) <= 800){
	    		if(Math.random() * 4000 <= PPd.getSkillLevel(SkillType.ACROBATICS)){
	    			defender.sendMessage(ChatColor.GREEN+"**DODGE**");
	    			if(System.currentTimeMillis() >= 5000 + PPd.getRespawnATS() && defender.getHealth() >= 1){
	    				PPd.addXP(SkillType.ACROBATICS, (event.getDamage() * 12)*1);
	    				Skills.XpCheckSkill(SkillType.ACROBATICS, defender);
	    			}
	    			event.setDamage(event.getDamage() / 2);
	    			//Needs to do minimal damage
	    			if(event.getDamage() <= 0)
	    				event.setDamage(1);
	    		}
			} else if(Math.random() * 4000 <= 800) {
				defender.sendMessage(ChatColor.GREEN+"**DODGE**");
				if(System.currentTimeMillis() >= 5000 + PPd.getRespawnATS() && defender.getHealth() >= 1){
					PPd.addXP(SkillType.ACROBATICS, (event.getDamage() * 12)*10);
					Skills.XpCheckSkill(SkillType.ACROBATICS, defender);
				}
				event.setDamage(event.getDamage() / 2);
				//Needs to deal minimal damage
				if(event.getDamage() <= 0)
					event.setDamage(1);
			}
		}
	}
	
}
