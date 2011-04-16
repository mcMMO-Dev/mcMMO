package com.gmail.nossr50;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import com.gmail.nossr50.datatypes.PlayerProfile;


public class mcAcrobatics {
	private static volatile mcAcrobatics instance;
	public static mcAcrobatics getInstance() {
    	if (instance == null) {
    	instance = new mcAcrobatics();
    	}
    	return instance;
    	}
	public void acrobaticsCheck(Player player, EntityDamageEvent event, Location loc, int xx, int y, int z){
    	if(player != null && mcPermissions.getInstance().acrobatics(player)){
    		PlayerProfile PP = mcUsers.getProfile(player);
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
						PP.addAcrobaticsXP((event.getDamage() * 8) * mcLoadProperties.xpGainMultiplier);
					mcSkills.XpCheck(player);
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
					PP.addAcrobaticsXP((event.getDamage() * 12) * mcLoadProperties.xpGainMultiplier);
					mcSkills.XpCheck(player);
				}
			}
    	}
    }
	
}
