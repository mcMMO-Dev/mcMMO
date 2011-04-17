package com.gmail.nossr50;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.datatypes.PlayerProfile;


public class mcHerbalism {
	private static mcMMO plugin;
	public mcHerbalism(mcMMO instance) {
    	plugin = instance;
    }
	
	public static void greenTerraWheat(Player player, Block block, BlockBreakEvent event){
		if(block.getType() == Material.WHEAT && block.getData() == (byte) 0x07){
			event.setCancelled(true);
			PlayerProfile PP = mcUsers.getProfile(player);
			Material mat = Material.getMaterial(296);
			Location loc = block.getLocation();
			ItemStack is = new ItemStack(mat, 1, (byte)0, (byte)0);
			PP.addHerbalismXP(5 * mcLoadProperties.xpGainMultiplier);
	    	loc.getWorld().dropItemNaturally(loc, is);
	    	herbalismProcCheck(block, player, event);
	    	herbalismProcCheck(block, player, event);
			block.setData((byte) 0x03);
		}
	}
	public static void greenTerra(Player player, Block block){
		if(block.getType() == Material.COBBLESTONE || block.getType() == Material.DIRT){
			if(!hasSeeds(player))
				player.sendMessage("You need more seeds to spread Green Terra");
			if(hasSeeds(player) && block.getType() != Material.WHEAT){
				removeSeeds(player);	
			if(block.getType() == Material.DIRT)
				block.setType(Material.GRASS);
			if(block.getType() == Material.COBBLESTONE)
				block.setType(Material.MOSSY_COBBLESTONE);
			}
		}
	}
	public static Boolean canBeGreenTerra(Block block){
    	int t = block.getTypeId();
    	if(t == 4 || t == 3 || t == 59 || t == 81 || t == 83 || t == 91 || t == 86 || t == 39 || t == 46 || t == 37 || t == 38){
    		return true;
    	} else {
    		return false;
    	}
    }
	public static boolean hasSeeds(Player player){
    	ItemStack[] inventory = player.getInventory().getContents();
    	for(ItemStack x : inventory){
    		if(x != null && x.getTypeId() == 295){
    			return true;
    		}
    	}
    	return false;
    }
	public static void removeSeeds(Player player){
    	ItemStack[] inventory = player.getInventory().getContents();
    	for(ItemStack x : inventory){
    		if(x != null && x.getTypeId() == 295){
    			if(x.getAmount() == 1){
    				x.setTypeId(0);
    				x.setAmount(0);
    				player.getInventory().setContents(inventory);
    			} else{
    			x.setAmount(x.getAmount() - 1);
    			player.getInventory().setContents(inventory);
    			}
    			return;
    		}
    	}
    }
	public static void greenTerraCheck(Player player, Block block, Plugin pluginx){
		PlayerProfile PP = mcUsers.getProfile(player);
	    if(mcm.isHoe(player.getItemInHand())){
	    	if(block != null){
		    	if(!mcm.abilityBlockCheck(block))
		    		return;
	    	}
	    	if(PP.getHoePreparationMode()){
    			PP.setHoePreparationMode(false);
    		}
	    	int ticks = 2;
	    	int x = PP.getHerbalismInt();
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}
    		
	    	if(!PP.getGreenTerraMode() && mcSkills.cooldownOver(player, PP.getGreenTerraDeactivatedTimeStamp(), mcLoadProperties.greenTerraCooldown)){
	    		player.sendMessage(ChatColor.GREEN+"**GREEN TERRA ACTIVATED**");
	    		for(Player y : pluginx.getServer().getOnlinePlayers()){
	    			if(y != null && y != player && mcm.getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(ChatColor.GREEN+player.getName()+ChatColor.DARK_GREEN+" has used "+ChatColor.RED+"Green Terra!");
	    		}
	    		PP.setGreenTerraTicks(ticks * 1000);
	    		PP.setGreenTerraActivatedTimeStamp(System.currentTimeMillis());
	    		PP.setGreenTerraMode(true);
	    	}
	    	
	    }
	}
	public static void herbalismProcCheck(Block block, Player player, BlockBreakEvent event){
		PlayerProfile PP = mcUsers.getProfile(player);
    	int type = block.getTypeId();
    	Location loc = block.getLocation();
    	ItemStack is = null;
    	Material mat = null;
    	
    	if(mcConfig.getInstance().isBlockWatched(block)){
    		return;
    	}
    	if(type == 59 && block.getData() == (byte) 0x7){
    		mat = Material.getMaterial(296);
			is = new ItemStack(mat, 1, (byte)0, (byte)0);
    		PP.addHerbalismXP(5 * mcLoadProperties.xpGainMultiplier);
    		if(player != null){
	    		if(Math.random() * 1000 <= PP.getHerbalismInt()){
	    			loc.getWorld().dropItemNaturally(loc, is);
	    		}
    		}
    		//GREEN THUMB
    		if(Math.random() * 1500 <= PP.getHerbalismInt()){
    			event.setCancelled(true);
    			loc.getWorld().dropItemNaturally(loc, is);
    			
    			block.setData((byte) 0x1); //Change it to first stage
    			
    			//Setup the bonuses
    			int bonus = 0;
    			if(PP.getHerbalismInt() >= 200)
    				bonus++;
    			if(PP.getHerbalismInt() >= 400)
    				bonus++;
    			if(PP.getHerbalismInt() >= 600)
    				bonus++;
    			
    			//Change wheat to be whatever stage based on the bonus
    			if(bonus == 1)
    				block.setData((byte) 0x2);
    			if(bonus == 2)
    				block.setData((byte) 0x3);
    			if(bonus == 3)
    				block.setData((byte) 0x4);
    		}
    	}
    	/*
    	 * We need to check not-wheat stuff for if it was placed by the player or not
    	 */
    	if(block.getData() != (byte) 5){
    		//Cactus
	    	if(type == 81){
	    		mat = Material.getMaterial(block.getTypeId());
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
	    		if(player != null){
		    		if(Math.random() * 1000 <= PP.getHerbalismInt()){
		    			loc.getWorld().dropItemNaturally(loc, is);
		    		}
	    		}
	    		PP.addHerbalismXP(3 * mcLoadProperties.xpGainMultiplier);
	    	}
    		//Sugar Canes
	    	if(type == 83){
				is = new ItemStack(Material.SUGAR_CANE, 1, (byte)0, (byte)0);
	    		if(player != null){
		    		if(Math.random() * 1000 <= PP.getHerbalismInt()){
		    			loc.getWorld().dropItemNaturally(loc, is);
		    		}
	    		}
	    		PP.addHerbalismXP(3 * mcLoadProperties.xpGainMultiplier);
	    	}
    		//Pumpkins
	    	if((type == 91 || type == 86) && !mcConfig.getInstance().isBlockWatched(block)){
	    		mat = Material.getMaterial(block.getTypeId());
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
	    		if(player != null){
		    		if(Math.random() * 1000 <= PP.getHerbalismInt()){
		    			loc.getWorld().dropItemNaturally(loc, is);
		    		}
	    		}
	    		PP.addHerbalismXP(55 * mcLoadProperties.xpGainMultiplier);
	    	}
    		//Mushroom
	    	if(type == 39 || type == 40){
	    		mat = Material.getMaterial(block.getTypeId());
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
	    		if(player != null){
		    		if(Math.random() * 1000 <= PP.getHerbalismInt()){
		    			loc.getWorld().dropItemNaturally(loc, is);
		    		}
	    		}
	    		PP.addHerbalismXP(40 * mcLoadProperties.xpGainMultiplier);
	    	}
	    	//Flower
	    	if(type == 37 || type == 38){
	    		mat = Material.getMaterial(block.getTypeId());
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
	    		if(player != null){
		    		if(Math.random() * 1000 <= PP.getHerbalismInt()){
		    			loc.getWorld().dropItemNaturally(loc, is);
		    		}
	    		}
	    		PP.addHerbalismXP(10 * mcLoadProperties.xpGainMultiplier);
	    	}
    	}
    	mcSkills.XpCheck(player);
    }
	public static void breadCheck(Player player, ItemStack is){
		PlayerProfile PP = mcUsers.getProfile(player);
    	if(is.getTypeId() == 297){
    		if(PP.getHerbalismInt() >= 50 && PP.getHerbalismInt() < 150){
    			player.setHealth(player.getHealth() + 1);
    		} else if (PP.getHerbalismInt() >= 150 && PP.getHerbalismInt() < 250){
    			player.setHealth(player.getHealth() + 2);
    		} else if (PP.getHerbalismInt() >= 250 && PP.getHerbalismInt() < 350){
    			player.setHealth(player.getHealth() + 3);
    		} else if (PP.getHerbalismInt() >= 350 && PP.getHerbalismInt() < 450){
    			player.setHealth(player.getHealth() + 4);
    		} else if (PP.getHerbalismInt() >= 450 && PP.getHerbalismInt() < 550){
    			player.setHealth(player.getHealth() + 5);
    		} else if (PP.getHerbalismInt() >= 550 && PP.getHerbalismInt() < 650){
    			player.setHealth(player.getHealth() + 6);
    		} else if (PP.getHerbalismInt() >= 650 && PP.getHerbalismInt() < 750){
    			player.setHealth(player.getHealth() + 7);
    		} else if (PP.getHerbalismInt() >= 750){
    			player.setHealth(player.getHealth() + 8);
    		}
    	}
    }
    public static void stewCheck(Player player, ItemStack is){
    	PlayerProfile PP = mcUsers.getProfile(player);
    	if(is.getTypeId() == 282){
    		if(PP.getHerbalismInt() >= 50 && PP.getHerbalismInt() < 150){
    			player.setHealth(player.getHealth() + 1);
    		} else if (PP.getHerbalismInt() >= 150 && PP.getHerbalismInt() < 250){
    			player.setHealth(player.getHealth() + 2);
    		} else if (PP.getHerbalismInt() >= 250 && PP.getHerbalismInt() < 350){
    			player.setHealth(player.getHealth() + 3);
    		} else if (PP.getHerbalismInt() >= 350 && PP.getHerbalismInt() < 450){
    			player.setHealth(player.getHealth() + 4);
    		} else if (PP.getHerbalismInt() >= 450 && PP.getHerbalismInt() < 550){
    			player.setHealth(player.getHealth() + 5);
    		} else if (PP.getHerbalismInt() >= 550 && PP.getHerbalismInt() < 650){
    			player.setHealth(player.getHealth() + 6);
    		} else if (PP.getHerbalismInt() >= 650 && PP.getHerbalismInt() < 750){
    			player.setHealth(player.getHealth() + 7);
    		} else if (PP.getHerbalismInt() >= 750){
    			player.setHealth(player.getHealth() + 8);
    		}
    	}
    }
}
