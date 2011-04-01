package com.gmail.nossr50;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class mcExcavation {
	private static mcMMO plugin;
	public mcExcavation(mcMMO instance) {
    	plugin = instance;
    }
	private static volatile mcExcavation instance;
	public static mcExcavation getInstance() {
    	if (instance == null) {
    	instance = new mcExcavation(plugin);
    	}
    	return instance;
    	}
	
	public void excavationProcCheck(Block block, Player player){
    	int type = block.getTypeId();
    	Location loc = block.getLocation();
    	ItemStack is = null;
    	Material mat = null;
    	if(type == 2 && mcUsers.getProfile(player).getExcavationInt() > 250){
    		//CHANCE TO GET EGGS
    		if(mcLoadProperties.eggs == true && Math.random() * 100 > 99){
    			mcUsers.getProfile(player).addExcavationGather(10);
				mat = Material.getMaterial(344);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    		//CHANCE TO GET APPLES
    		if(mcLoadProperties.apples == true && Math.random() * 100 > 99){
    			mcUsers.getProfile(player).addExcavationGather(10);
    			mat = Material.getMaterial(260);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    	}
    	//DIRT SAND OR GRAVEL
    	if(type == 3 || type == 13 || type == 2 || type == 12){
    			mcUsers.getProfile(player).addExcavationGather(3);
    		if(mcUsers.getProfile(player).getExcavationInt() > 750){
    			//CHANCE TO GET CAKE
    			if(mcLoadProperties.cake == true && Math.random() * 2000 > 1999){
    				mcUsers.getProfile(player).addExcavationGather(300);
    				mat = Material.getMaterial(354);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
    			}
    		}
    		if(mcUsers.getProfile(player).getExcavationInt() > 150){
    			//CHANCE TO GET MUSIC
    			if(mcLoadProperties.music == true && Math.random() * 2000 > 1999){
    				mcUsers.getProfile(player).addExcavationGather(300);
    				mat = Material.getMaterial(2256);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
    			}
    			
    		}
    		if(mcUsers.getProfile(player).getExcavationInt() > 350){
    			//CHANCE TO GET DIAMOND
    			if(mcLoadProperties.diamond == true && Math.random() * 500 > 499){
    				mcUsers.getProfile(player).addExcavationGather(100);
        				mat = Material.getMaterial(264);
        				is = new ItemStack(mat, 1, (byte)0, (byte)0);
        				loc.getWorld().dropItemNaturally(loc, is);
    			}
    		}
    		if(mcUsers.getProfile(player).getExcavationInt() > 250){
    			//CHANCE TO GET MUSIC
    			if(mcLoadProperties.music == true && Math.random() * 2000 > 1999){
    				mcUsers.getProfile(player).addExcavationGather(300);
    				mat = Material.getMaterial(2257);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
    			}
    		}
    	}
    	//SAND
    	if(type == 12){
    		//CHANCE TO GET GLOWSTONE
    		if(mcLoadProperties.glowstone == true && mcUsers.getProfile(player).getExcavationInt() > 50 && Math.random() * 100 > 95){
    			mcUsers.getProfile(player).addExcavationGather(3);
				mat = Material.getMaterial(348);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    		//CHANCE TO GET SLOWSAND
    		if(mcLoadProperties.slowsand == true && mcUsers.getProfile(player).getExcavationInt() > 650 && Math.random() * 200 > 199){
    			mcUsers.getProfile(player).addExcavationGather(5);
				mat = Material.getMaterial(88);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    		//CHANCE TO GET DIAMOND
    		if(mcLoadProperties.diamond == true && mcUsers.getProfile(player).getExcavationInt() > 500 && Math.random() * 500 > 499){
    			mcUsers.getProfile(player).addExcavationGather(100);
				mat = Material.getMaterial(264);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    	}
    	//GRASS OR DIRT
    	if((type == 2 || type == 3) && mcUsers.getProfile(player).getExcavationInt() > 25){
    		//CHANCE TO GET GLOWSTONE
    		if(mcLoadProperties.glowstone == true && Math.random() * 100 > 95){
    			mcUsers.getProfile(player).addExcavationGather(5);
    			mat = Material.getMaterial(348);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    	}
    	//GRAVEL
    	if(type == 13){
    		//CHANCE TO GET NETHERRACK
    		if(mcLoadProperties.netherrack == true && mcUsers.getProfile(player).getExcavationInt() > 850 && Math.random() * 200 > 199){
    			mcUsers.getProfile(player).addExcavationGather(3);
				mat = Material.getMaterial(87);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    		//CHANCE TO GET SULPHUR
    		if(mcLoadProperties.sulphur == true && mcUsers.getProfile(player).getExcavationInt() > 75){
    		if(Math.random() * 10 > 9){
    			mcUsers.getProfile(player).addExcavationGather(3);
    			mat = Material.getMaterial(289);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    		}
    		//CHANCE TO GET BONES
    		if(mcLoadProperties.bones == true && mcUsers.getProfile(player).getExcavationInt() > 175){
        		if(Math.random() * 10 > 6){
        			mcUsers.getProfile(player).addExcavationGather(3);
        			mat = Material.getMaterial(352);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
        		}
        		}
    	}
    	if(mcUsers.getProfile(player).getExcavationGatherInt() >= mcUsers.getProfile(player).getXpToLevel("excavation")){
			int skillups = 0;
			while(mcUsers.getProfile(player).getExcavationGatherInt() >= mcUsers.getProfile(player).getXpToLevel("excavation")){
				skillups++;
				mcUsers.getProfile(player).removeExcavationGather(mcUsers.getProfile(player).getXpToLevel("excavation"));
				mcUsers.getProfile(player).skillUpExcavation(1);
			}
			player.sendMessage(ChatColor.YELLOW+"Excavation skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(player).getExcavation()+")");	
		}
    }
}
