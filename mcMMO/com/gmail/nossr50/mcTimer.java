package com.gmail.nossr50;
import java.awt.Color;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.bukkit.entity.*;

import com.gmail.nossr50.PlayerList.PlayerProfile;


public class mcTimer extends TimerTask{
	private final mcMMO plugin;
	int thecount = 1;

    public mcTimer(final mcMMO plugin) {
        this.plugin = plugin;
    }
    
	public void run() {
		Player[] playerlist = plugin.getServer().getOnlinePlayers();
		for(Player player : playerlist){
			PlayerProfile PP = mcUsers.getProfile(player.getName());
			if(player == null)
				continue;
			if(PP == null)
	    		mcUsers.addUser(player);
			/*
			 * MONITOR SKILLS
			 */
			mcSkills.monitorSkills(player);
			/*
			 * COOLDOWN MONITORING
			 */
			mcSkills.watchCooldowns(player);
			
			/*
			 * PLAYER BLEED MONITORING
			 */
			if(thecount % 2 == 0 && player != null && PP.getBleedTicks() >= 1){
        		player.damage(2);
        		PP.decreaseBleedTicks();
        	}
			
			if(mcPermissions.getInstance().regeneration(player) && System.currentTimeMillis() >= PP.getRecentlyHurt() + 60000){
				if(thecount == 10 || thecount == 20 || thecount == 30 || thecount == 40){
				    if(player != null &&
				    	player.getHealth() > 0 && player.getHealth() < 20 
				    	&& mcm.getPowerLevel(player) >= 1000){
				    	player.setHealth(mcm.calculateHealth(player.getHealth(), 1));
				    }
				}
				if(thecount == 20 || thecount == 40){
			   		if(player != null &&
			   			player.getHealth() > 0 && player.getHealth() < 20 
			    		&& mcm.getPowerLevel(player) >= 500 
			    		&& mcm.getPowerLevel(player) < 1000){
			    		player.setHealth(mcm.calculateHealth(player.getHealth(), 1));
			    	}
				}
				if(thecount == 40){
			    	if(player != null &&
			    		player.getHealth() > 0 && player.getHealth() < 20  
			    		&& mcm.getPowerLevel(player) < 500){
			    		player.setHealth(mcm.calculateHealth(player.getHealth(), 1));
			    	}
				}
			}
		}
		
		/*
		 * NON-PLAYER BLEED MONITORING
		 */
		if(thecount % 2 == 0)
			mcCombat.bleedSimulate();
		
		if(thecount < 40){
			thecount++;
		} else {
			thecount = 1;
		}
	}
}
