package com.gmail.nossr50;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.PlayerList.PlayerProfile;


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
	public void gigaDrillBreakerActivationCheck(Player player, Block block, Plugin pluginx){
		PlayerProfile PP = mcUsers.getProfile(player.getName());
		if(mcm.getInstance().isShovel(player.getItemInHand())){
	    	if(block != null){
		    	if(!mcm.getInstance().abilityBlockCheck(block))
		    		return;
	    	}
	    	if(PP.getShovelPreparationMode()){
    			PP.setShovelPreparationMode(false);
    		}
	    	int ticks = 2;
	    	int x = PP.getExcavationInt();
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}
    		
	    	if(!PP.getGigaDrillBreakerMode() && PP.getGigaDrillBreakerCooldown() == 0){
	    		player.sendMessage(ChatColor.GREEN+"**GIGA DRILL BREAKER ACTIVATED**");
	    		for(Player y : pluginx.getServer().getOnlinePlayers()){
	    			if(y != null && y != player && mcm.getInstance().getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(ChatColor.GREEN+player.getName()+ChatColor.DARK_GREEN+" has used "+ChatColor.RED+"Giga Drill Breaker!");
	    		}
	    		PP.setGigaDrillBreakerTicks(ticks * 1000);
	    		PP.setGigaDrillBreakerActivatedTimeStamp(System.currentTimeMillis());
	    		PP.setGigaDrillBreakerMode(true);
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
		PlayerProfile PP = mcUsers.getProfile(player.getName());
    	int type = block.getTypeId();
    	Location loc = block.getLocation();
    	ItemStack is = null;
    	Material mat = null;
    	if(block.getData() == (byte) 5){
    		return;
    	}
    	if(type == 2){
    		if(PP.getExcavationInt() > 250){
	    		//CHANCE TO GET EGGS
	    		if(mcLoadProperties.eggs == true && Math.random() * 100 > 99){
	    			PP.addExcavationXP(10 * mcLoadProperties.xpGainMultiplier);
					mat = Material.getMaterial(344);
					is = new ItemStack(mat, 1, (byte)0, (byte)0);
					loc.getWorld().dropItemNaturally(loc, is);
	    		}
	    		//CHANCE TO GET APPLES
	    		if(mcLoadProperties.apples == true && Math.random() * 100 > 99){
	    			PP.addExcavationXP(10 * mcLoadProperties.xpGainMultiplier);
	    			mat = Material.getMaterial(260);
					is = new ItemStack(mat, 1, (byte)0, (byte)0);
					loc.getWorld().dropItemNaturally(loc, is);
	    		}
    		}
    	}
    	//DIRT SAND OR GRAVEL
    	if(type == 3 || type == 13 || type == 2 || type == 12){
    			PP.addExcavationXP(4 * mcLoadProperties.xpGainMultiplier);
    		if(PP.getExcavationInt() > 750){
    			//CHANCE TO GET CAKE
    			if(mcLoadProperties.cake == true && Math.random() * 2000 > 1999){
    				PP.addExcavationXP(300 * mcLoadProperties.xpGainMultiplier);
    				mat = Material.getMaterial(354);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
    			}
    		}
    		if(PP.getExcavationInt() > 350){
    			//CHANCE TO GET DIAMOND
    			if(mcLoadProperties.diamond == true && Math.random() * 750 > 749){
    				PP.addExcavationXP(100 * mcLoadProperties.xpGainMultiplier);
        				mat = Material.getMaterial(264);
        				is = new ItemStack(mat, 1, (byte)0, (byte)0);
        				loc.getWorld().dropItemNaturally(loc, is);
    			}
    		}
    		if(PP.getExcavationInt() > 250){
    			//CHANCE TO GET YELLOW MUSIC
    			if(mcLoadProperties.music == true && Math.random() * 2000 > 1999){
    				PP.addExcavationXP(300 * mcLoadProperties.xpGainMultiplier);
    				mat = Material.getMaterial(2256);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
    			}
    			
    		}
    		if(PP.getExcavationInt() > 350){
    			//CHANCE TO GET GREEN MUSIC
    			if(mcLoadProperties.music == true && Math.random() * 2000 > 1999){
    				PP.addExcavationXP(300 * mcLoadProperties.xpGainMultiplier);
    				mat = Material.getMaterial(2257);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
    			}
    		}
    	}
    	//SAND
    	if(type == 12){
    		//CHANCE TO GET GLOWSTONE
    		if(mcLoadProperties.glowstone == true && PP.getExcavationInt() > 50 && Math.random() * 100 > 95){
    			PP.addExcavationXP(8 * mcLoadProperties.xpGainMultiplier);
				mat = Material.getMaterial(348);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    		//CHANCE TO GET SLOWSAND
    		if(mcLoadProperties.slowsand == true && PP.getExcavationInt() > 650 && Math.random() * 200 > 199){
    			PP.addExcavationXP(8 * mcLoadProperties.xpGainMultiplier);
				mat = Material.getMaterial(88);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    	}
    	//GRASS OR DIRT
    	if(type == 2 || type == 3){
    		if(PP.getExcavationInt() > 50){
    			//CHANCE FOR COCOA BEANS
    			if(mcLoadProperties.eggs == true && Math.random() * 75 > 74){
	    			PP.addExcavationXP(10 * mcLoadProperties.xpGainMultiplier);
					mat = Material.getMaterial(351);
					is = new ItemStack(mat, 1, (byte)0, (byte)0);
					is.setDurability((byte) 3); //COCOA
					loc.getWorld().dropItemNaturally(loc, is);
    			}
    		}
    		//CHANCE FOR SHROOMS
    		if(mcLoadProperties.mushrooms == true && PP.getExcavationInt() > 500 && Math.random() * 200 > 199){
    			PP.addExcavationXP(8 * mcLoadProperties.xpGainMultiplier);
    			if(Math.random() * 10 > 5){
    				mat = Material.getMaterial(39);
    			} else {
    				mat = Material.getMaterial(40);
    			}
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    		//CHANCE TO GET GLOWSTONE
    		if(mcLoadProperties.glowstone == true && PP.getExcavationInt() > 25 && Math.random() * 100 > 95){
    			PP.addExcavationXP(8 * mcLoadProperties.xpGainMultiplier);
    			mat = Material.getMaterial(348);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    	}
    	//GRAVEL
    	if(type == 13){
    		//CHANCE TO GET NETHERRACK
    		if(mcLoadProperties.netherrack == true && PP.getExcavationInt() > 850 && Math.random() * 200 > 199){
    			PP.addExcavationXP(3 * mcLoadProperties.xpGainMultiplier);
				mat = Material.getMaterial(87);
				is = new ItemStack(mat, 1, (byte)0, (byte)0);
				loc.getWorld().dropItemNaturally(loc, is);
    		}
    		//CHANCE TO GET SULPHUR
    		if(mcLoadProperties.sulphur == true && PP.getExcavationInt() > 75){
	    		if(Math.random() * 10 > 9){
	    			PP.addExcavationXP(3 * mcLoadProperties.xpGainMultiplier);
	    			mat = Material.getMaterial(289);
					is = new ItemStack(mat, 1, (byte)0, (byte)0);
					loc.getWorld().dropItemNaturally(loc, is);
	    		}
    		}
    		//CHANCE TO GET BONES
    		if(mcLoadProperties.bones == true && PP.getExcavationInt() > 175){
        		if(Math.random() * 10 > 9){
        			PP.addExcavationXP(3 * mcLoadProperties.xpGainMultiplier);
        			mat = Material.getMaterial(352);
    				is = new ItemStack(mat, 1, (byte)0, (byte)0);
    				loc.getWorld().dropItemNaturally(loc, is);
        		}
        	}
    	}
    	mcSkills.getInstance().XpCheck(player);
    }
}
