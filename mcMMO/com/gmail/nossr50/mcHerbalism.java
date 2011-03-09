package com.gmail.nossr50;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class mcHerbalism {
	private static mcMMO plugin;
	public mcHerbalism(mcMMO instance) {
    	plugin = instance;
    }
	private static volatile mcHerbalism instance;
	public static mcHerbalism getInstance() {
    	if (instance == null) {
    	instance = new mcHerbalism(plugin);
    	}
    	return instance;
    	}
	public void herbalismProcCheck(Block block, Player player){
    	int type = block.getTypeId();
    	Location loc = block.getLocation();
    	ItemStack is = null;
    	Material mat = null;
    	if(type == 59 && block.getData() == (byte) 0x7){
    		mat = Material.getMaterial(296);
			is = new ItemStack(mat, 1, (byte)0, (byte)0);
    		mcUsers.getProfile(player).addHerbalismGather(5);
    		if(player != null){
	    		if(Math.random() * 1000 <= mcUsers.getProfile(player).getHerbalismInt()){
	    			loc.getWorld().dropItemNaturally(loc, is);
	    		}
    		}
    	}
    	/*
    	 * We need to check not-wheat stuff for if it was placed by the player or not
    	 */
    	if(!mcConfig.getInstance().isBlockWatched(block)){
    	if(type == 39 || type == 40){
    			mcUsers.getProfile(player).addHerbalismGather(10);
    		}
    	if(type == 37 || type == 38){
    			mcUsers.getProfile(player).addHerbalismGather(3);
    	}
    	}
    	if(mcUsers.getProfile(player).getHerbalismGatherInt() >= mcUsers.getProfile(player).getXpToLevel("herbalism")){
			int skillups = 0;
			while(mcUsers.getProfile(player).getHerbalismGatherInt() >= mcUsers.getProfile(player).getXpToLevel("herbalism")){
				skillups++;
				mcUsers.getProfile(player).removeHerbalismGather(mcUsers.getProfile(player).getXpToLevel("herbalism"));
				mcUsers.getProfile(player).skillUpHerbalism(1);
			}
			player.sendMessage(ChatColor.YELLOW+"Herbalism skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(player).getHerbalism()+")");	
		}
    }
	public void breadCheck(Player player, ItemStack is){
    	if(is.getTypeId() == 297){
    		if(mcUsers.getProfile(player).getHerbalismInt() >= 50 && mcUsers.getProfile(player).getHerbalismInt() < 150){
    			player.setHealth(player.getHealth() + 1);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 150 && mcUsers.getProfile(player).getHerbalismInt() < 250){
    			player.setHealth(player.getHealth() + 2);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 250 && mcUsers.getProfile(player).getHerbalismInt() < 350){
    			player.setHealth(player.getHealth() + 3);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 350 && mcUsers.getProfile(player).getHerbalismInt() < 450){
    			player.setHealth(player.getHealth() + 4);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 450 && mcUsers.getProfile(player).getHerbalismInt() < 550){
    			player.setHealth(player.getHealth() + 5);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 550 && mcUsers.getProfile(player).getHerbalismInt() < 650){
    			player.setHealth(player.getHealth() + 6);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 650 && mcUsers.getProfile(player).getHerbalismInt() < 750){
    			player.setHealth(player.getHealth() + 7);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 750){
    			player.setHealth(player.getHealth() + 8);
    		}
    	}
    }
    public void stewCheck(Player player, ItemStack is){
    	if(is.getTypeId() == 282){
    		if(mcUsers.getProfile(player).getHerbalismInt() >= 50 && mcUsers.getProfile(player).getHerbalismInt() < 150){
    			player.setHealth(player.getHealth() + 1);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 150 && mcUsers.getProfile(player).getHerbalismInt() < 250){
    			player.setHealth(player.getHealth() + 2);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 250 && mcUsers.getProfile(player).getHerbalismInt() < 350){
    			player.setHealth(player.getHealth() + 3);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 350 && mcUsers.getProfile(player).getHerbalismInt() < 450){
    			player.setHealth(player.getHealth() + 4);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 450 && mcUsers.getProfile(player).getHerbalismInt() < 550){
    			player.setHealth(player.getHealth() + 5);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 550 && mcUsers.getProfile(player).getHerbalismInt() < 650){
    			player.setHealth(player.getHealth() + 6);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 650 && mcUsers.getProfile(player).getHerbalismInt() < 750){
    			player.setHealth(player.getHealth() + 7);
    		} else if (mcUsers.getProfile(player).getHerbalismInt() >= 750){
    			player.setHealth(player.getHealth() + 8);
    		}
    	}
    }
}
