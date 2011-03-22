package com.gmail.nossr50;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class mcItem {
	private static mcMMO plugin;
	public mcItem(mcMMO instance) {
    	plugin = instance;
    }
	private static volatile mcItem instance;
	public static mcItem getInstance() {
    	if (instance == null) {
    		instance = new mcItem(plugin);
    	}
    	return instance;
    	}
	public void itemChecks(Player player){
		ItemStack inhand = player.getItemInHand();
		if(inhand.getTypeId() == 288){
			chimaerawing(player);
		}
	}
	public void chimaerawing(Player player){
		ItemStack is = player.getItemInHand();
		Block block = player.getLocation().getBlock();
		if(mcPermissions.getInstance().chimaeraWing(player) && is.getTypeId() == 288 && mcm.getInstance().abilityBlockCheck(block)){
    		if(mcUsers.getProfile(player).getRecentlyHurt() == 0 && is.getAmount() >= mcLoadProperties.feathersConsumedByChimaeraWing){
    			Block derp = player.getLocation().getBlock();
    			int y = derp.getY();
    			ItemStack[] inventory = player.getInventory().getContents();
    	    	for(ItemStack x : inventory){
    	    		if(x.getTypeId() == 288){
    	    			if(x.getAmount() >= mcLoadProperties.feathersConsumedByChimaeraWing + 1){
    	    				x.setAmount(x.getAmount() - mcLoadProperties.feathersConsumedByChimaeraWing);
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
    			while(y < 127){
    				y++;
    				if(player != null){
    					if(player.getLocation().getWorld().getBlockAt(block.getX(), y, block.getZ()).getType() != Material.AIR){
	    					player.sendMessage("**CHIMAERA WING FAILED!**");
	    					player.teleportTo(player.getLocation().getWorld().getBlockAt(block.getX(), (y - 1), block.getZ()).getLocation());
	    					return;
    					}
    				}
    			}
    			if(mcUsers.getProfile(player).getMySpawn(player) != null){
    				player.teleportTo(mcUsers.getProfile(player).getMySpawn(player));
    			} else {
    				player.teleportTo(player.getWorld().getSpawnLocation());
    			}
    			player.sendMessage("**CHIMAERA WING**");
    		} else if (mcUsers.getProfile(player).getRecentlyHurt() >= 1 && is.getAmount() >= 10) {
    			player.sendMessage("You were injured recently and must wait to use this."
    					+ChatColor.YELLOW+" ("+(mcUsers.getProfile(player).getRecentlyHurt() * 2)+"s)");
    		} else if (is.getTypeId() == 288 && is.getAmount() <= 9){
    			player.sendMessage("You need more of that to use it");
    		}
    	}
	}
}
