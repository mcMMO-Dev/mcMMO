package com.gmail.nossr50;

import net.minecraft.server.EntityWolf;

import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class mcTaming {
	private static mcMMO plugin;
	public mcTaming(mcMMO instance) {
    	plugin = instance;
    }
	
	private static volatile mcTaming instance;
	
	public static mcTaming getInstance() {
    	if (instance == null) {
    		instance = new mcTaming(plugin);
    	}
    	return instance;
    }
	public String getOwnerName(Entity theWolf){
		CraftWolf cWolf = (CraftWolf)theWolf;
		EntityWolf eWolf = (EntityWolf)cWolf.getHandle();

		String playerName = eWolf.v();
		return playerName;
	}
	public boolean hasOwner(Entity theWolf, Plugin pluginx){
		for(Player x : pluginx.getServer().getOnlinePlayers()){
			if(x != null && x.getName().equals(getOwnerName(theWolf))){
				return true;
			}
		}
		return false;
	}
	public Player getOwner(Entity theWolf, Plugin pluginx){
		for(Player x : pluginx.getServer().getOnlinePlayers()){
			if(x != null && x.getName().equals(getOwnerName(theWolf))){
				return x;
			}
		}
		return null;
	}
}
