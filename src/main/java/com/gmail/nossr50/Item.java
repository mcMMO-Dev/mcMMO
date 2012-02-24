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
package com.gmail.nossr50;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import com.gmail.nossr50.config.*;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.skills.*;

import com.gmail.nossr50.datatypes.PlayerProfile;

public class Item {
	
	public static void itemchecks(Player player, Plugin plugin)
	{
		ItemStack inhand = player.getItemInHand();
		if(LoadProperties.chimaeraWingEnable && inhand.getTypeId() == LoadProperties.chimaeraId)
		{
			chimaerawing(player, plugin);
		}
	}
	
	public static void chimaerawing(Player player, Plugin plugin)
	{
		PlayerProfile PP = Users.getProfile(player);
		ItemStack is = player.getItemInHand();
		Block block = player.getLocation().getBlock();
		int chimaeraID = LoadProperties.chimaeraId;
		int itemsUsed = LoadProperties.feathersConsumedByChimaeraWing;
		if(mcPermissions.getInstance().chimaeraWing(player) && is.getTypeId() == chimaeraID)
		{
    		if(Skills.cooldownOver(player, PP.getRecentlyHurt(), 60) && is.getAmount() >= itemsUsed)
    		{
    			Block derp = player.getLocation().getBlock();
    			int y = derp.getY();
    			player.setItemInHand(new ItemStack(chimaeraID, is.getAmount() - itemsUsed));
    
    			while(y < 127)
    			{
    				y++;
    				if(player != null)
    				{
    					if(player.getLocation().getWorld().getBlockAt(block.getX(), y, block.getZ()).getType() != Material.AIR)
    					{
	    					player.sendMessage(mcLocale.getString("Item.ChimaeraWingFail")); //$NON-NLS-1$
	    					player.teleport(player.getLocation().getWorld().getBlockAt(block.getX(), (y - 1), block.getZ()).getLocation());
	    					return;
    					}
    				}
    			}
    			if(PP.getMySpawn(player) != null)
    			{
    				Location mySpawn = PP.getMySpawn(player);
    				if(mySpawn != null){
	    				player.teleport(mySpawn); //Do it twice to prevent weird stuff
	    				player.teleport(mySpawn);
    				}
    			} else {
    				player.teleport(player.getWorld().getSpawnLocation());
    			}
    			player.sendMessage(mcLocale.getString("Item.ChimaeraWingPass")); //$NON-NLS-1$
    		} else if (!Skills.cooldownOver(player, PP.getRecentlyHurt(), 60) && is.getAmount() >= 10) 
    		{
    			player.sendMessage(mcLocale.getString("Item.InjuredWait", new Object[] {Skills.calculateTimeLeft(player, PP.getRecentlyHurt(), 60)})); //$NON-NLS-1$
    		} else if (is.getTypeId() == LoadProperties.chimaeraId && is.getAmount() <= 9){
    			player.sendMessage(mcLocale.getString("Item.NeedFeathers")); //$NON-NLS-1$
    		}
    	}
	}
}
