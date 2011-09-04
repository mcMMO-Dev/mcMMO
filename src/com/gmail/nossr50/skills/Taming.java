package com.gmail.nossr50.skills;

import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.plugin.Plugin;

public class Taming 
{
	public static boolean ownerOnline(Wolf theWolf, Plugin pluginx)
	{
		for(Player x : pluginx.getServer().getOnlinePlayers())
		{
			if(x instanceof AnimalTamer)
			{
				AnimalTamer tamer = (AnimalTamer)x;
				if(theWolf.getOwner() == tamer)
					return true;
			}
		}
		return false;
	}
	
	public static Player getOwner(Entity wolf, Plugin pluginx)
	{
		if(wolf instanceof Wolf)
		{
			Wolf theWolf = (Wolf)wolf;
			for(Player x : pluginx.getServer().getOnlinePlayers())
			{
				if(x instanceof AnimalTamer)
				{
					AnimalTamer tamer = (AnimalTamer)x;
					if(theWolf.getOwner() == tamer)
						return x;
				}
			}
		}
		return null;
	}
	
	public static String getOwnerName(Wolf theWolf)
	{
		Player owner = (Player)theWolf.getOwner();
		if(owner != null)
		{
		return owner.getName();
		}
		else
			return "Offline Master";
	}
}
