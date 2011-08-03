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
	
	@SuppressWarnings("deprecation")
	public static void chimaerawing(Player player, Plugin plugin)
	{
		PlayerProfile PP = Users.getProfile(player);
		ItemStack is = player.getItemInHand();
		Block block = player.getLocation().getBlock();
		if(mcPermissions.getInstance().chimaeraWing(player) && is.getTypeId() == LoadProperties.chimaeraId)
		{
    		if(Skills.cooldownOver(player, PP.getRecentlyHurt(), 60) && is.getAmount() >= LoadProperties.feathersConsumedByChimaeraWing)
    		{
    			Block derp = player.getLocation().getBlock();
    			int y = derp.getY();
    			ItemStack[] inventory = player.getInventory().getContents();
    	    	for(ItemStack x : inventory){
    	    		if(x != null && x.getTypeId() == LoadProperties.chimaeraId){
    	    			if(x.getAmount() >= LoadProperties.feathersConsumedByChimaeraWing + 1)
    	    			{
    	    				x.setAmount(x.getAmount() - LoadProperties.feathersConsumedByChimaeraWing);
    	    				player.getInventory().setContents(inventory);
        	    			player.updateInventory();
        	    			break;
    	    			} else {
    	    				x.setAmount(0);
    	    				x.setTypeId(0);
    	    				player.getInventory().setContents(inventory);
        	    			player.updateInventory();
        	    			break;
    	    			}
    	    		}
    	    	}
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
    				if(mySpawn != null && plugin.getServer().getWorld(PP.getMySpawnWorld(plugin)) != null)
    					mySpawn.setWorld(plugin.getServer().getWorld(PP.getMySpawnWorld(plugin)));
    				if(mySpawn != null){
	    				player.teleport(mySpawn);//Do it twice to prevent weird stuff
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
