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
    	if(mcUsers.getProfile(player).getAcrobaticsInt() >= 50 
				&& mcUsers.getProfile(player).getAcrobaticsInt() < 250
				&& mcPermissions.getInstance().acrobatics(player)){
			if(Math.random() * 10 > 8){
				event.setCancelled(true);
				player.sendMessage("**ROLLED**");
				return;
			}
		}
		if(mcUsers.getProfile(player).getAcrobaticsInt() >= 250 
				&& mcUsers.getProfile(player).getAcrobaticsInt() < 450 
				&& mcPermissions.getInstance().acrobatics(player)){
			if(Math.random() * 10 > 6){
				event.setCancelled(true);
				player.sendMessage("**ROLLED**");
				return;
			}
		}
		if(mcUsers.getProfile(player).getAcrobaticsInt() >= 450 
				&& mcUsers.getProfile(player).getAcrobaticsInt() < 750 
				&& mcPermissions.getInstance().acrobatics(player)){
			if(Math.random() * 10 > 4){
				event.setCancelled(true);
				player.sendMessage("**ROLLED**");
				return;
			}
		}
		if(mcUsers.getProfile(player).getAcrobaticsInt() >= 750 
				&& mcUsers.getProfile(player).getAcrobaticsInt() < 950 
				&& mcPermissions.getInstance().acrobatics(player)){
			if(Math.random() * 10 > 2){
				event.setCancelled(true);
				player.sendMessage("**BARREL ROLLED**");
				return;
			}
		}
		if(mcUsers.getProfile(player).getAcrobaticsInt() >= 950
				&& mcPermissions.getInstance().acrobatics(player)){
				event.setCancelled(true);
				player.sendMessage("**ROLLED... LIKE A BOSS**");
				return;
			}
		if(player.getHealth() - event.getDamage() <= 0)
			return;
		if(!mcConfig.getInstance().isBlockWatched(loc.getWorld().getBlockAt(xx, y, z)) 
				&& mcPermissions.getInstance().acrobatics(player)){
		mcUsers.getProfile(player).addAcrobaticsGather(event.getDamage() * 3);
		if(mcUsers.getProfile(player).getAcrobaticsGatherInt() >= mcUsers.getProfile(player).getXpToLevel("acrobatics")){
			int skillups = 0;
			while(mcUsers.getProfile(player).getAcrobaticsGatherInt() >= mcUsers.getProfile(player).getXpToLevel("acrobatics")){
				skillups++;
				mcUsers.getProfile(player).removeAcrobaticsGather(mcUsers.getProfile(player).getXpToLevel("acrobatics"));
				mcUsers.getProfile(player).skillUpAcrobatics(1);
			}
			player.sendMessage(ChatColor.YELLOW+"Acrobatics skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(player).getAcrobatics()+")");	
		}
		mcConfig.getInstance().addBlockWatch(loc.getWorld().getBlockAt(xx, y, z));
		if(player.getHealth() - event.getDamage() <= 0){
			if(mcUsers.getProfile(player).isDead())
    			return;
			mcUsers.getProfile(player).setDead(true);
		}
		}
    }
	
}
