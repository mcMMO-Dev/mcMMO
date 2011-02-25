package com.gmail.nossr50;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

public class mcTimer extends TimerTask{
	private final mcMMO plugin;
	int thecount = 1;

    public mcTimer(final mcMMO plugin) {
        this.plugin = plugin;
    }
    public Integer calculateHealth(Integer health, Integer newvalue){
    	if((health + newvalue) > 20){
    		return 20;
    	} else {
    		return health+newvalue;
    	}
    }
    public Integer calculateMinusHealth(Integer health, Integer newvalue){
    	if((health - newvalue) < 1){
    		return 0;
    	} else {
    		return health-newvalue;
    	}
    }
    public Integer getHealth(Entity entity){
    	if(entity instanceof Monster){
    		Monster monster = (Monster)entity;
    		return monster.getHealth();
    	} else if (entity instanceof Animals){
    		Animals animals = (Animals)entity;
    		return animals.getHealth();
    	} else if (entity instanceof Player){
    		Player player = (Player)entity;
    		return player.getHealth();
    	} else {
    		return 0;
    	}
    }
	public void run() {
		for(World world : plugin.getServer().getWorlds()){
			for(Entity entity : world.getEntities()){
				if(entity == null || getHealth(entity) <= 0)
					return;
				if(mcConfig.getInstance().getBleedCount(entity) < 1)
					return;
				if(mcConfig.getInstance().isBleedTracked(entity)){
					if(entity instanceof Player){
						Player player = (Player)entity;
						if(player.getHealth() >= 1){
						player.setHealth(calculateMinusHealth(player.getHealth(), 1));
						player.sendMessage(ChatColor.RED+"**BLEED**");
						if(player.getHealth() <= 0){
							for(ItemStack items : player.getInventory().getContents()){
								if(items.getTypeId() != 0)
								player.getLocation().getWorld().dropItemNaturally(player.getLocation(), items);
							}
						}
					}
					}
					if(entity instanceof Animals){
						Animals animals = (Animals)entity;
						if(animals.getHealth() >= 1){
						animals.setHealth(calculateMinusHealth(animals.getHealth(), 1));
						if(animals.getHealth() <= 0){
							mcm.getInstance().simulateNaturalDrops(entity);
						}
						}
					}
					if(entity instanceof Monster){
						Monster monster = (Monster)entity;
						if(monster.getHealth() >= 1){
						monster.setHealth(calculateMinusHealth(monster.getHealth(), 1));
						if(monster.getHealth() <= 0){
							mcm.getInstance().simulateNaturalDrops(entity);
						}
						}
					}
				}
			}
		}
		for(World world : plugin.getServer().getWorlds()){
			for(Entity entity : world.getEntities()){
				if(mcConfig.getInstance().isBleedTracked(entity)){
					if(mcConfig.getInstance().getBleedCount(entity) >= 2){
						mcConfig.getInstance().removeBleedCount(entity, 1);
					} else if(mcConfig.getInstance().getBleedCount(entity) == 1){
						mcConfig.getInstance().removeBleedTrack(entity);
					}
				}
			}
		}
		if(thecount == 10 || thecount == 20 || thecount == 30 || thecount == 40){
		for(Player player : plugin.getServer().getOnlinePlayers()){
			if(player != null && mcUsers.getProfile(player).getRecentlyHurt() >= 1)
				mcUsers.getProfile(player).decreaseLastHurt();
		}
		}

		for(Player player : plugin.getServer().getOnlinePlayers()){
	    	if(player != null &&
	    			player.getHealth() > 0 && player.getHealth() < 20 
	    			&& mcUsers.getProfile(player).getPowerLevel() >= 1000 
	    			&& mcUsers.getProfile(player).getRecentlyHurt() == 0 
	    			&& mcPermissions.getInstance().regeneration(player)){
	    		player.setHealth(calculateHealth(player.getHealth(), 1));
	    	}
	    }
		if(thecount == 20 || thecount == 40){
		for(Player player : plugin.getServer().getOnlinePlayers()){
    		if(player != null &&
    				player.getHealth() > 0 && player.getHealth() < 20 
    				&& mcUsers.getProfile(player).getPowerLevel() >= 500 
    				&& mcUsers.getProfile(player).getPowerLevel() < 1000  
    				&& mcUsers.getProfile(player).getRecentlyHurt() == 0 
    				&& mcPermissions.getInstance().regeneration(player)){
    			player.setHealth(calculateHealth(player.getHealth(), 1));
    		}
    	}
		}
		if(thecount == 40){
			for(Player player : plugin.getServer().getOnlinePlayers()){
	    		if(player != null &&
	    				player.getHealth() > 0 && player.getHealth() < 20 
	    				&& mcUsers.getProfile(player).getPowerLevel() >= 100 
	    				&& mcUsers.getProfile(player).getPowerLevel() < 500  
	    				&& mcUsers.getProfile(player).getRecentlyHurt() == 0 
	    				&& mcPermissions.getInstance().regeneration(player)){
	    			player.setHealth(calculateHealth(player.getHealth(), 1));
	    		}
	    	}
		}
		/*
		 * RESET THE COUNT
		 */
		if(thecount < 40){
		thecount++;
		} else {
		thecount = 1;
		}
	}
}
