package com.gmail.nossr50;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class mcWoodCutting {
	private static mcMMO plugin;
	public mcWoodCutting(mcMMO instance) {
    	plugin = instance;
    }
	private static volatile mcWoodCutting instance;
	public static mcWoodCutting getInstance() {
    	if (instance == null) {
    	instance = new mcWoodCutting(plugin);
    	}
    	return instance;
    	}
    public void woodCuttingProcCheck(Player player, Block block, Location loc){
    	byte type = block.getData();
    	Material mat = Material.getMaterial(block.getTypeId());
    	byte damage = 0;
    	if(mcUsers.getProfile(player).getWoodCuttingInt() > 1000){
			ItemStack item = new ItemStack(mat, 1, type, damage);
			loc.getWorld().dropItemNaturally(loc, item);
			return;
    	}
    	if(mcUsers.getProfile(player).getWoodCuttingInt() > 750){
			if((Math.random() * 10) > 2){
				ItemStack item = new ItemStack(mat, 1, type, damage);
				loc.getWorld().dropItemNaturally(loc, item);
				return;
			}
    	}
	if(mcUsers.getProfile(player).getWoodCuttingInt() > 300){
		if((Math.random() * 10) > 4){
			ItemStack item = new ItemStack(mat, 1, type, damage);
			loc.getWorld().dropItemNaturally(loc, item);
			return;
		}
	}
	if(mcUsers.getProfile(player).getWoodCuttingInt() > 100){
		if((Math.random() * 10) > 6){
			ItemStack item = new ItemStack(mat, 1, type, damage);
			loc.getWorld().dropItemNaturally(loc, item);
			return;
		}
	}
	if(mcUsers.getProfile(player).getWoodCuttingInt() > 10){
		if((Math.random() * 10) > 8){
			ItemStack item = new ItemStack(mat, 1, type, damage);
			loc.getWorld().dropItemNaturally(loc, item);
			return;
		}
	}
    }
}
