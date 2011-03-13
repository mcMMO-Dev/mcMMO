package com.gmail.nossr50;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class mcAcrobatics {
	private static volatile mcAcrobatics instance;
	public static mcAcrobatics getInstance() {
    	if (instance == null) {
    	instance = new mcAcrobatics();
    	}
    	return instance;
    	}
	public void acrobaticsCheck(Player player, EntityDamageEvent event, Location loc, int xx, int y, int z){
    	if(player != null){
			if(Math.random() * 1000 <= mcUsers.getProfile(player).getAcrobaticsInt()){
				event.setCancelled(true);
				player.sendMessage("**ROLLED**");
				if(!mcConfig.getInstance().isBlockWatched(loc.getWorld().getBlockAt(xx, y, z)) 
						&& mcPermissions.getInstance().acrobatics(player)){
				mcUsers.getProfile(player).addAcrobaticsGather(event.getDamage() * 8);
				mcSkills.getInstance().XpCheck(player);
				}
				return;
			}
		}
		if(player != null && player.getHealth() - event.getDamage() <= 0)
			return;
		if(!mcConfig.getInstance().isBlockWatched(loc.getWorld().getBlockAt(xx, y, z)) 
				&& mcPermissions.getInstance().acrobatics(player)){
		mcUsers.getProfile(player).addAcrobaticsGather(event.getDamage() * 12);
		mcSkills.getInstance().XpCheck(player);
		mcConfig.getInstance().addBlockWatch(loc.getWorld().getBlockAt(xx, y, z));
		if(player.getHealth() - event.getDamage() <= 0){
			if(mcUsers.getProfile(player).isDead())
    			return;
			mcUsers.getProfile(player).setDead(true);
		}
		}
    }
	
}
