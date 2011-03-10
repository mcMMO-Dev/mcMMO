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
		/*
		if(thecount == 5 || thecount == 10 || thecount == 15 || thecount == 20){
			for(Player player : playerlist){
		    	if(player != null &&
		    			player.getHealth() > 0 && player.getHealth() < 20 
		    			&& mcUsers.getProfile(player).getPowerLevel() >= 1000 
		    			&& mcUsers.getProfile(player).getRecentlyHurt() == 0 
		    			&& mcPermissions.getInstance().regeneration(player)){
		    		player.setHealth(mcm.getInstance().calculateHealth(player.getHealth(), 1));
		    	}
		    }
		}
		if(thecount == 10 || thecount == 20){
			for(Player player : playerlist){
	    		if(player != null &&
	    				player.getHealth() > 0 && player.getHealth() < 20 
	    				&& mcUsers.getProfile(player).getPowerLevel() >= 500 
	    				&& mcUsers.getProfile(player).getPowerLevel() < 1000  
	    				&& mcUsers.getProfile(player).getRecentlyHurt() == 0 
	    				&& mcPermissions.getInstance().regeneration(player)){
	    			player.setHealth(mcm.getInstance().calculateHealth(player.getHealth(), 1));
	    		}
	    	}
		}
		if(thecount == 20){
			for(Player player : playerlist){
	    		if(player != null &&
	    				player.getHealth() > 0 && player.getHealth() < 20  
	    				&& mcUsers.getProfile(player).getPowerLevel() < 500  
	    				&& mcUsers.getProfile(player).getRecentlyHurt() == 0 
	    				&& mcPermissions.getInstance().regeneration(player)){
	    			player.setHealth(mcm.getInstance().calculateHealth(player.getHealth(), 1));
	    		}
	    	}
		}
		for(Player player : playerlist){
			if(player != null && mcUsers.getProfile(player).getRecentlyHurt() >= 1){
				mcUsers.getProfile(player).decreaseLastHurt();
			}
		}
		if(thecount < 20){
		thecount++;
		} else {
		thecount = 1;
		}
		mcCombat.getInstance().bleedSimulate();
		*/
		/*
		 * TREE FELLER INTERACTIONS
		 */
		for(Player player : playerlist){
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
				//Monitor the cooldown
				if(!mcUsers.getProfile(player).getTreeFellerMode() && mcUsers.getProfile(player).getTreeFellerCooldown() >= 1){
					mcUsers.getProfile(player).decreaseTreeFellerCooldown();
					if(mcUsers.getProfile(player).getTreeFellerCooldown() == 0){
						player.sendMessage(ChatColor.GREEN+"Your Tree Felling ability is refreshed!");
					}
				}
			}
		}
	}
}
