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
    	if(player != null){
    		if(Math.random() * 1000 <= mcUsers.getProfile(player).getWoodCuttingInt()){
			ItemStack item = new ItemStack(mat, 1, type, damage);
			loc.getWorld().dropItemNaturally(loc, item);
			return;
    		}
    	}
    }
    public void treeFeller(Block block){
    	Location loc = block.getLocation();
    	int radius = 1;
    	int typeid = 17;
    	if(mcm.getInstance().isBlockAround(loc, radius, typeid)){
    		
    	}
    }
}
