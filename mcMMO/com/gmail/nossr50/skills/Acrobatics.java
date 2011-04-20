package com.gmail.nossr50.skills;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;


public class Acrobatics {
	public static void acrobaticsCheck(Player player, EntityDamageEvent event){
    	if(player != null && mcPermissions.getInstance().acrobatics(player)){
    		PlayerProfile PP = Users.getProfile(player);
    		int acrovar = PP.getAcrobaticsInt();
    		if(player.isSneaking())
    			acrovar = acrovar * 2;
			if(Math.random() * 1000 <= acrovar && !event.isCancelled()){
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
						PP.addAcrobaticsXP((event.getDamage() * 8) * LoadProperties.xpGainMultiplier);
					Skills.XpCheck(player);
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
					PP.addAcrobaticsXP((event.getDamage() * 12) * LoadProperties.xpGainMultiplier);
					Skills.XpCheck(player);
				}
			}
    	}
    }
	public static void dodgeChecks(EntityDamageByEntityEvent event){
		Player defender = (Player) event.getEntity();
		PlayerProfile PPd = Users.getProfile(defender);
		
		if(mcPermissions.getInstance().acrobatics(defender)){
			if(PPd.getAcrobaticsInt() <= 800){
	    		if(Math.random() * 4000 <= PPd.getAcrobaticsInt()){
	    			defender.sendMessage(ChatColor.GREEN+"**DODGE**");
	    			if(System.currentTimeMillis() >= 5000 + PPd.getRespawnATS() && defender.getHealth() >= 1){
	    				PPd.addAcrobaticsXP(event.getDamage() * 12);
	    				Skills.XpCheck(defender);
	    			}
	    			event.setDamage(event.getDamage() / 2);
	    			//Needs to do minimal damage
	    			if(event.getDamage() <= 0)
	    				event.setDamage(1);
	    		}
			} else if(Math.random() * 4000 <= 800) {
				defender.sendMessage(ChatColor.GREEN+"**DODGE**");
				if(System.currentTimeMillis() >= 5000 + PPd.getRespawnATS() && defender.getHealth() >= 1){
					PPd.addAcrobaticsXP(event.getDamage() * 12);
					Skills.XpCheck(defender);
				}
				event.setDamage(event.getDamage() / 2);
				//Needs to deal minimal damage
				if(event.getDamage() <= 0)
					event.setDamage(1);
			}
		}
	}
	
}
