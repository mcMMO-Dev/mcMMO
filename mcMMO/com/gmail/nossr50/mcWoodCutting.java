package com.gmail.nossr50;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.PlayerList.PlayerProfile;


public class mcWoodCutting {
	int w = 0;
	private boolean isdone = false;
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
    public void woodCuttingProcCheck(Player player, Block block){
    	PlayerProfile PP = mcUsers.getProfile(player.getName());
    	byte type = block.getData();
    	Material mat = Material.getMaterial(block.getTypeId());
    	if(player != null){
    		if(Math.random() * 1000 <= PP.getWoodCuttingInt()){
    			ItemStack item = new ItemStack(mat, 1, (short) 0, type);
    			block.getWorld().dropItemNaturally(block.getLocation(), item);
    		}
    	}
    }
    public void treeFellerCheck(Player player, Block block, Plugin pluginx){
    	PlayerProfile PP = mcUsers.getProfile(player.getName());
    	if(mcm.getInstance().isAxes(player.getItemInHand())){
    		if(block != null){
        		if(!mcm.getInstance().abilityBlockCheck(block))
        			return;
        	}
    		/*
    		 * CHECK FOR AXE PREP MODE
    		 */
    		if(PP.getAxePreparationMode()){
    			PP.setAxePreparationMode(false);
    		}
    		int ticks = 2;
    		int x = PP.getWoodCuttingInt();
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}

    		if(!PP.getTreeFellerMode() && mcSkills.getInstance().cooldownOver(player, PP.getTreeFellerDeactivatedTimeStamp(), mcLoadProperties.treeFellerCooldown)){
    			player.sendMessage(ChatColor.GREEN+"**TREE FELLING ACTIVATED**");
    			for(Player y : pluginx.getServer().getOnlinePlayers()){
	    			if(y != null && y != player && mcm.getInstance().getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(ChatColor.GREEN+player.getName()+ChatColor.DARK_GREEN+" has used "+ChatColor.RED+"Tree Feller!");
	    		}
    			PP.setTreeFellerTicks(ticks * 1000);
    			PP.setTreeFellerActivatedTimeStamp(System.currentTimeMillis());
    			PP.setTreeFellerMode(true);
    		}
    		if(!PP.getTreeFellerMode() && !mcSkills.getInstance().cooldownOver(player, PP.getTreeFellerDeactivatedTimeStamp(), mcLoadProperties.treeFellerCooldown)){
    			player.sendMessage(ChatColor.RED+"You are too tired to use that ability again."
    					+ChatColor.YELLOW+" ("+mcSkills.getInstance().calculateTimeLeft(player, PP.getTreeFellerDeactivatedTimeStamp(), mcLoadProperties.treeFellerCooldown)+"s)");
    		}
    	}
    }
    public void treeFeller(Block block, Player player){
    	PlayerProfile PP = mcUsers.getProfile(player.getName());
    	int radius = 1;
    	if(PP.getWoodCuttingGatherInt() >= 500)
    		radius++;
    	if(PP.getWoodCuttingGatherInt() >= 950)
    		radius++;
        ArrayList<Block> blocklist = new ArrayList<Block>();
        ArrayList<Block> toAdd = new ArrayList<Block>();
        if(block != null)
        	blocklist.add(block);
        while(isdone == false){
        	addBlocksToTreeFelling(blocklist, toAdd, radius);
        }
        //This needs to be a hashmap too!
        isdone = false;
        /*
         * Add blocks from the temporary 'toAdd' array list into the 'treeFeller' array list
         * We use this temporary list to prevent concurrent modification exceptions
         */
        for(Block x : toAdd){
        	if(!mcConfig.getInstance().isTreeFellerWatched(x))
        		mcConfig.getInstance().addTreeFeller(x);
        }
        toAdd.clear();
    }
    public void addBlocksToTreeFelling(ArrayList<Block> blocklist, ArrayList<Block> toAdd, Integer radius){
    	int u = 0;
    	for (Block x : blocklist){
    		u++;
    		if(toAdd.contains(x))
    			continue;
    		w = 0;
    		Location loc = x.getLocation();
    		int vx = x.getX();
            int vy = x.getY();
            int vz = x.getZ();
            
            /*
             * Run through the blocks around the broken block to see if they qualify to be 'felled'
             */
    		for (int cx = -radius; cx <= radius; cx++) {
	            for (int cy = -radius; cy <= radius; cy++) {
	                for (int cz = -radius; cz <= radius; cz++) {
	                    Block blocktarget = loc.getWorld().getBlockAt(vx + cx, vy + cy, vz + cz);
	                    if (!blocklist.contains(blocktarget) && !toAdd.contains(blocktarget) && (blocktarget.getTypeId() == 17 || blocktarget.getTypeId() == 18)) { 
	                        toAdd.add(blocktarget);
	                        w++;
	                    }
	                }
	            }
	        }
    	}
    	/*
		 * Add more blocks to blocklist so they can be 'felled'
		 */
		for(Block xx : toAdd){
    		if(!blocklist.contains(xx))
        	blocklist.add(xx);
        }
    	if(u >= blocklist.size()){
    		isdone = true;
    	} else {
    		isdone = false;
    	}
    }
}
