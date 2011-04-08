package com.gmail.nossr50;

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
    	if(player != null && mcPermissions.getInstance().acrobatics(player)&& event.getDamage() < 21){
			if(Math.random() * 1000 <= mcUsers.getProfile(player).getAcrobaticsInt()){
				player.sendMessage("**ROLLED**");
				if(!mcConfig.getInstance().isBlockWatched(loc.getWorld().getBlockAt(xx, y, z))){
					if(!event.isCancelled())
						mcUsers.getProfile(player).addAcrobaticsGather((event.getDamage() * 8) * mcLoadProperties.xpGainMultiplier);
					mcSkills.getInstance().XpCheck(player);
					event.setCancelled(true);
				}
			} else if (!mcConfig.getInstance().isBlockWatched(loc.getWorld().getBlockAt(xx, y, z)) && !event.isCancelled()){
				mcUsers.getProfile(player).addAcrobaticsGather((event.getDamage() * 12) * mcLoadProperties.xpGainMultiplier);
				mcSkills.getInstance().XpCheck(player);
			}
    	}
    }
	
}
