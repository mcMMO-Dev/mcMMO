package com.gmail.nossr50;
import java.awt.Color;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.bukkit.entity.*;

public class mcTimer extends TimerTask{
	private final mcMMO plugin;
	int thecount = 1;

    public mcTimer(final mcMMO plugin) {
        this.plugin = plugin;
    }
    
	public void run() {
		Player[] playerlist = plugin.getServer().getOnlinePlayers();
		for(Player player : playerlist){
			if(mcPermissions.getInstance().regeneration(player)){
				if(thecount == 5 || thecount == 10 || thecount == 15 || thecount == 20){
				    if(player != null &&
				    	player.getHealth() > 0 && player.getHealth() < 20 
				    	&& mcUsers.getProfile(player).getPowerLevel() >= 1000 
				    	&& mcUsers.getProfile(player).getRecentlyHurt() == 0){
				    	player.setHealth(mcm.getInstance().calculateHealth(player.getHealth(), 1));
				    }
				}
				if(thecount == 10 || thecount == 20){
			   		if(player != null &&
			   			player.getHealth() > 0 && player.getHealth() < 20 
			    		&& mcUsers.getProfile(player).getPowerLevel() >= 500 
			    		&& mcUsers.getProfile(player).getPowerLevel() < 1000  
			    		&& mcUsers.getProfile(player).getRecentlyHurt() == 0){
			    		player.setHealth(mcm.getInstance().calculateHealth(player.getHealth(), 1));
			    	}
				}
				if(thecount == 20){
			    	if(player != null &&
			    		player.getHealth() > 0 && player.getHealth() < 20  
			    		&& mcUsers.getProfile(player).getPowerLevel() < 500  
			    		&& mcUsers.getProfile(player).getRecentlyHurt() == 0){
			    		player.setHealth(mcm.getInstance().calculateHealth(player.getHealth(), 1));
			    	}
				}
				if(player != null && mcUsers.getProfile(player).getRecentlyHurt() >= 1){
					mcUsers.getProfile(player).decreaseLastHurt();
				}
			}
			/*
			 * WOODCUTTING ABILITY
			 */
			if(mcPermissions.getInstance().woodcuttingability(player)){
				//Monitor the length of TreeFeller mode
				if(mcUsers.getProfile(player).getTreeFellerMode()){
					mcUsers.getProfile(player).decreaseTreeFellerTicks();
					if(mcUsers.getProfile(player).getTreeFellerTicks() <= 0){
						mcUsers.getProfile(player).setTreeFellerMode(false);
						mcUsers.getProfile(player).setTreeFellerCooldown(120);
						player.sendMessage(ChatColor.GRAY+"**You feel strength leaving you**");
					}
				}
			}
			/*
			 * MINING ABILITY
			 */
			if(mcPermissions.getInstance().miningability(player)){
				//Monitor the length of SuperBreaker mode
				if(mcUsers.getProfile(player).getSuperBreakerMode()){
					mcUsers.getProfile(player).decreaseSuperBreakerTicks();
					if(mcUsers.getProfile(player).getSuperBreakerTicks() <= 0){
						mcUsers.getProfile(player).setSuperBreakerMode(false);
						mcUsers.getProfile(player).setSuperBreakerCooldown(120);
						player.sendMessage(ChatColor.GRAY+"**You feel strength leaving you**");
					}
				}
			}
			/*
			 * COOLDOWN MONITORING
			 */
			if(mcUsers.getProfile(player).hasCooldowns())
				mcSkills.getInstance().decreaseCooldowns(player);
		}
		if(thecount < 20){
			thecount++;
		} else {
			thecount = 1;
		}
		/*
		 * BLEED MONITORING
		 */
		mcCombat.getInstance().bleedSimulate();
	}
}
