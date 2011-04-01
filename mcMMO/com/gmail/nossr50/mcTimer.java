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
			if(player == null)
				continue;
			if(mcUsers.getProfile(player) == null)
	    		mcUsers.addUser(player);
			/*
			 * MONITOR SKILLS
			 */
			mcSkills.getInstance().monitorSkills(player);
			/*
			 * COOLDOWN MONITORING
			 */
			mcSkills.getInstance().decreaseCooldowns(player);
			
			/*
			 * PLAYER BLEED MONITORING
			 */
			if(thecount % 2 == 0 && player != null && mcUsers.getProfile(player).getBleedTicks() >= 1){
        		player.damage(2);
        		mcUsers.getProfile(player).decreaseBleedTicks();
        	}
			
			if(mcPermissions.getInstance().regeneration(player)){
				if(thecount == 10 || thecount == 20 || thecount == 30 || thecount == 40){
				    if(player != null &&
				    	player.getHealth() > 0 && player.getHealth() < 20 
				    	&& mcUsers.getProfile(player).getPowerLevel(player) >= 1000 
				    	&& mcUsers.getProfile(player).getRecentlyHurt() == 0){
				    	player.setHealth(mcm.getInstance().calculateHealth(player.getHealth(), 1));
				    }
				}
				if(thecount == 20 || thecount == 40){
			   		if(player != null &&
			   			player.getHealth() > 0 && player.getHealth() < 20 
			    		&& mcUsers.getProfile(player).getPowerLevel(player) >= 500 
			    		&& mcUsers.getProfile(player).getPowerLevel(player) < 1000  
			    		&& mcUsers.getProfile(player).getRecentlyHurt() == 0){
			    		player.setHealth(mcm.getInstance().calculateHealth(player.getHealth(), 1));
			    	}
				}
				if(thecount == 40){
			    	if(player != null &&
			    		player.getHealth() > 0 && player.getHealth() < 20  
			    		&& mcUsers.getProfile(player).getPowerLevel(player) < 500  
			    		&& mcUsers.getProfile(player).getRecentlyHurt() == 0){
			    		player.setHealth(mcm.getInstance().calculateHealth(player.getHealth(), 1));
			    	}
				}
				if(player != null && mcUsers.getProfile(player).getRecentlyHurt() >= 1){
					mcUsers.getProfile(player).decreaseLastHurt();
				}
			}
		}
		
		/*
		 * NON-PLAYER BLEED MONITORING
		 */
		if(thecount % 2 == 0)
			mcCombat.getInstance().bleedSimulate();
		
		if(thecount < 40){
			thecount++;
		} else {
			thecount = 1;
		}
	}
}
