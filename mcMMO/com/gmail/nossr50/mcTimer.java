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
			 * MONITOR SKILLS
			 */
			mcSkills.getInstance().monitorSkills(player);
			/*
			 * COOLDOWN MONITORING
			 */
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
