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
	public void gigaDrillBreakerActivationCheck(Player player, Block block){
		if(mcm.getInstance().isShovel(player.getItemInHand())){
	    	if(block != null){
		    	if(!mcm.getInstance().abilityBlockCheck(block))
		    		return;
	    	}
	    	if(mcUsers.getProfile(player).getShovelPreparationMode()){
    			mcUsers.getProfile(player).setShovelPreparationMode(false);
    			mcUsers.getProfile(player).setShovelPreparationTicks(0);
    		}
	    	int ticks = 2;
    		if(mcUsers.getProfile(player).getExcavationInt() >= 50)
    			ticks++;
    		if(mcUsers.getProfile(player).getExcavationInt() >= 150)
    			ticks++;
    		if(mcUsers.getProfile(player).getExcavationInt() >= 250)
    			ticks++;
    		if(mcUsers.getProfile(player).getExcavationInt() >= 350)
    			ticks++;
    		if(mcUsers.getProfile(player).getExcavationInt() >= 450)
    			ticks++;
    		if(mcUsers.getProfile(player).getExcavationInt() >= 550)
    			ticks++;
    		if(mcUsers.getProfile(player).getExcavationInt() >= 650)
    			ticks++;
    		if(mcUsers.getProfile(player).getExcavationInt() >= 750)
    			ticks++;
    		
	    	if(!mcUsers.getProfile(player).getGigaDrillBreakerMode() && mcUsers.getProfile(player).getGigaDrillBreakerCooldown() == 0){
	    		player.sendMessage(ChatColor.GREEN+"**GIGA DRILL BREAKER ACTIVATED**");
	    		mcUsers.getProfile(player).setGigaDrillBreakerTicks(ticks * 2);
	    		mcUsers.getProfile(player).setGigaDrillBreakerMode(true);
	    	}
	    	
	    }
	}
	public boolean canBeGigaDrillBroken(Block block){
		int i = block.getTypeId();
		if(i == 2||i == 3||i == 12||i == 13){
			return true;
		} else {
			return false;
		}
	}
	public void excavationProcCheck(Block block, Player player){
    	int type = block.getTypeId();
    	Location loc = block.getLocation();
    	ItemStack is = null;
    	Material mat = null;
    	if(type == 2){
    		if(mcUsers.getProfile(player).getExcavationInt() > 250){
	    		//CHANCE TO GET EGGS
	    		if(mcLoadProperties.eggs == true && Math.random() * 100 > 99){
	    			mcUsers.getProfile(player).addExcavationGather(10 * mcLoadProperties.xpGainMultiplier);
					mat = Material.getMaterial(344);
					is = new ItemStack(mat, 1, (byte)0, (byte)0);
					loc.getWorld().dropItemNaturally(loc, is);
	    		}
	    		//CHANCE TO GET APPLES
	    		if(mcLoadProperties.apples == true && Math.random() * 100 > 99){
	    			mcUsers.getProfile(player).addExcavationGather(10 * mcLoadProperties.xpGainMultiplier);
	    			mat = Material.getMaterial(260);
					is = new ItemStack(mat, 1, (byte)0, (byte)0);
					loc.getWorld().dropItemNaturally(loc, is);
	    		}
    		}
    	}
    	//DIRT SAND OR GRAVEL
    	if(type == 3 || type == 13 || type == 2 || type == 12){
    			mcUsers.getProfile(player).addExcavationGather(4);
    		if(mcUsers.getProfile(player).getExcavationInt() > 750){
    			//CHANCE TO GET CAKE
    			if(mcLoadProperties.cake == true && Math.random() * 2000 > 1999){
    				mcUsers.getProfile(player).addExcavationGather(300 * mcLoadProperties.xpGainMultiplier);
    				mat = Material.getMaterial(354);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
    			}
    		}
    		if(mcUsers.getProfile(player).getExcavationInt() > 350){
    			//CHANCE TO GET DIAMOND
    			if(mcLoadProperties.diamond == true && Math.random() * 750 > 749){
    				mcUsers.getProfile(player).addExcavationGather(100 * mcLoadProperties.xpGainMultiplier);
        				mat = Material.getMaterial(264);
        				is = new ItemStack(mat, 1, (byte)0, (byte)0);
        				loc.getWorld().dropItemNaturally(loc, is);
    			}
    		}
    		if(mcUsers.getProfile(player).getExcavationInt() > 250){
    			//CHANCE TO GET YELLOW MUSIC
    			if(mcLoadProperties.music == true && Math.random() * 2000 > 1999){
    				mcUsers.getProfile(player).addExcavationGather(300 * mcLoadProperties.xpGainMultiplier);
    				mat = Material.getMaterial(2256);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
    			}
    			
    		}
    		if(mcUsers.getProfile(player).getExcavationInt() > 350){
    			//CHANCE TO GET GREEN MUSIC
    			if(mcLoadProperties.music == true && Math.random() * 2000 > 1999){
    				mcUsers.getProfile(player).addExcavationGather(300 * mcLoadProperties.xpGainMultiplier);
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
    			mcUsers.getProfile(player).addExcavationGather(8 * mcLoadProperties.xpGainMultiplier);
				mat = Material.getMaterial(348);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    		//CHANCE TO GET SLOWSAND
    		if(mcLoadProperties.slowsand == true && mcUsers.getProfile(player).getExcavationInt() > 650 && Math.random() * 200 > 199){
    			mcUsers.getProfile(player).addExcavationGather(8 * mcLoadProperties.xpGainMultiplier);
				mat = Material.getMaterial(88);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    	}
    	//GRASS OR DIRT
    	if(type == 2 || type == 3){
    		//CHANCE FOR SHROOMS
    		if(mcLoadProperties.mushrooms == true && mcUsers.getProfile(player).getExcavationInt() > 500 && Math.random() * 200 > 199){
    			mcUsers.getProfile(player).addExcavationGather(8 * mcLoadProperties.xpGainMultiplier);
    			if(Math.random() * 10 > 5){
    				mat = Material.getMaterial(39);
    			} else {
    				mat = Material.getMaterial(40);
    			}
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    		//CHANCE TO GET GLOWSTONE
    		if(mcLoadProperties.glowstone == true && mcUsers.getProfile(player).getExcavationInt() > 25 && Math.random() * 100 > 95){
    			mcUsers.getProfile(player).addExcavationGather(8 * mcLoadProperties.xpGainMultiplier);
    			mat = Material.getMaterial(348);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    	}
    	//GRAVEL
    	if(type == 13){
    		//CHANCE TO GET NETHERRACK
    		if(mcLoadProperties.netherrack == true && mcUsers.getProfile(player).getExcavationInt() > 850 && Math.random() * 200 > 199){
    			mcUsers.getProfile(player).addExcavationGather(3 * mcLoadProperties.xpGainMultiplier);
				mat = Material.getMaterial(87);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    		//CHANCE TO GET SULPHUR
    		if(mcLoadProperties.sulphur == true && mcUsers.getProfile(player).getExcavationInt() > 75){
	    		if(Math.random() * 10 > 9){
	    			mcUsers.getProfile(player).addExcavationGather(3 * mcLoadProperties.xpGainMultiplier);
	    			mat = Material.getMaterial(289);
					is = new ItemStack(mat, 1, (byte)0, (byte)0);
					loc.getWorld().dropItemNaturally(loc, is);
	    		}
    		}
    		//CHANCE TO GET BONES
    		if(mcLoadProperties.bones == true && mcUsers.getProfile(player).getExcavationInt() > 175){
        		if(Math.random() * 10 > 9){
        			mcUsers.getProfile(player).addExcavationGather(3 * mcLoadProperties.xpGainMultiplier);
        			mat = Material.getMaterial(352);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
        		}
        	}
    	}
    	mcSkills.getInstance().XpCheck(player);
    }
}
