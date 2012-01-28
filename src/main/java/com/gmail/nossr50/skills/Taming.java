/*
	This file is part of mcMMO.

    mcMMO is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    mcMMO is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with mcMMO.  If not, see <http://www.gnu.org/licenses/>.
*/
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
				if(x instanceof AnimalTamer && x.isOnline())
				{
					AnimalTamer tamer = (AnimalTamer)x;
					if(theWolf.getOwner() == tamer)
						return x;
				}
			}
			return null;
		}
		return null;
	}
	
	public static String getOwnerName(Wolf theWolf)
	{
		Player owner = null;
		
		if (theWolf.getOwner() instanceof Player)
		{
			owner = (Player)theWolf.getOwner();
		}
		
		if(owner != null)
		{
			return owner.getName();
		}
		else
			return "Offline Master";
	}
}
