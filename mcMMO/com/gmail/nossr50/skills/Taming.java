package com.gmail.nossr50.skills;

import net.minecraft.server.EntityWolf;

import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Taming {
	public static String getOwnerName(Entity theWolf){
		CraftWolf cWolf = (CraftWolf)theWolf;
		EntityWolf eWolf = (EntityWolf)cWolf.getHandle();
		
		String playerName = eWolf.x();
		return playerName;
	}
	public static boolean hasOwner(Entity theWolf, Plugin pluginx){
		for(Player x : pluginx.getServer().getOnlinePlayers()){
			if(x != null && x.getName().equals(getOwnerName(theWolf))){
				return true;
			}
		}
		return false;
	}
	public static Player getOwner(Entity theWolf, Plugin pluginx){
		for(Player x : pluginx.getServer().getOnlinePlayers()){
			if(x != null && x.getName().equals(getOwnerName(theWolf))){
				return x;
			}
		}
		return null;
	}
}
