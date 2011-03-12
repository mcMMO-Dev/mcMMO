package com.gmail.nossr50;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class mcBlockListener extends BlockListener {
    private final mcMMO plugin;

    public mcBlockListener(final mcMMO plugin) {
        this.plugin = plugin;
    }
    public void onBlockPlace(BlockPlaceEvent event) {
    	Block block;
    	Player player = event.getPlayer();
    	if (event.getBlockReplacedState().getTypeId() == 78) {
    		block = event.getBlockAgainst();
    		}
    		else {
    		block = event.getBlock();
    		}
    	if(player != null && mcm.getInstance().shouldBeWatched(block))
    	mcConfig.getInstance().addBlockWatch(block);
    	if(block.getTypeId() == 42 && mcLoadProperties.anvilmessages)
    		event.getPlayer().sendMessage(ChatColor.DARK_RED+"You have placed an anvil, anvils can repair tools and armor.");
    }
    //put all Block related code here
    public void onBlockDamage(BlockDamageEvent event) {
    		//STARTED(0), DIGGING(1), BROKEN(3), STOPPED(2);
    		Player player = event.getPlayer();
    		ItemStack inhand = player.getItemInHand();
    		//player.sendMessage("mcMMO DEBUG: EVENT-OK DMG LEVEL ("+event.getDamageLevel().getLevel()+")");
    		Block block = event.getBlock();
    		Location loc = block.getLocation();
    		int dmg = event.getDamageLevel().getLevel();
    		/*
    		 * HERBALISM
    		 */
    		if(dmg == 3){
        		if(mcPermissions.getInstance().herbalism(player))
        		mcHerbalism.getInstance().herbalismProcCheck(block, player);
    		}
    		/*
    		 * MINING
    		 */
    		if(player != null && dmg == 2 && !mcConfig.getInstance().isBlockWatched(block)){
	    		if(mcPermissions.getInstance().mining(player)){
	    			if(mcLoadProperties.miningrequirespickaxe){
	    				if(mcm.getInstance().isMiningPick(inhand))
		    			mcMining.getInstance().miningBlockCheck(player, block);
	    			} else {
	    				mcMining.getInstance().miningBlockCheck(player, block);
	    			}
	    		}
	    		/*
	    		 * WOOD CUTTING
	    		 */
	    		if(player != null && block.getTypeId() == 17 && mcPermissions.getInstance().woodcutting(player)){
	    				if(mcLoadProperties.woodcuttingrequiresaxe){
	    					if(mcm.getInstance().isAxes(inhand)){
	    						mcWoodCutting.getInstance().woodCuttingProcCheck(player, block, loc);
	    						mcUsers.getProfile(player).addWoodcuttingGather(7);
	    					}
	    				} else {
	    					mcWoodCutting.getInstance().woodCuttingProcCheck(player, block, loc);
    						mcUsers.getProfile(player).addWoodcuttingGather(7);	
	    				}
	    				/*
	    				 * IF PLAYER IS USING TREEFELLER
	    				 */
	    				if(mcPermissions.getInstance().woodcuttingability(player) && mcUsers.getProfile(player).getTreeFellerMode() && block.getTypeId() == 17){
	    					mcWoodCutting.getInstance().treeFeller(block, player);
	    					for(Block blockx : mcConfig.getInstance().getTreeFeller()){
	    						if(blockx != null){
	    							Material mat = Material.getMaterial(blockx.getTypeId());
	    							byte damage = 0;
	    							ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
	    							blockx.setTypeId(0);
	    							if(item.getTypeId() == 17){
	    							blockx.getLocation().getWorld().dropItemNaturally(blockx.getLocation(), item);
	    							mcWoodCutting.getInstance().woodCuttingProcCheck(player, blockx, blockx.getLocation());
	    							mcUsers.getProfile(player).addWoodcuttingGather(7);
	    							}
	    							if(item.getTypeId() == 18){
	    								mat = Material.getMaterial(6);
	    								item = new ItemStack(mat, 1, (byte)0, damage);
	    								if(Math.random() * 10 > 8)
	    								blockx.getLocation().getWorld().dropItemNaturally(blockx.getLocation(), item);
	    							}
	    						}
	    					}
	    					/*
	    					 * NOTE TO SELF
	    					 * I NEED TO REMOVE TREE FELL BLOCKS FROM BEING WATCHED AFTER THIS CODE IS EXECUTED
	    					 * OR ELSE IT COULD BE A MEMORY LEAK SITUATION
	    					 */
	    					mcConfig.getInstance().clearTreeFeller();
	    				}
	    		}
	    		/*
	    		 * EXCAVATION
	    		 */
	    		if(mcPermissions.getInstance().excavation(player) && block != null && player != null)
	    		mcExcavation.getInstance().excavationProcCheck(block, player);
	    		/*
	    		 * EXPLOIT COUNTERMEASURES
	    		 */
	    		mcConfig.getInstance().addBlockWatch(block);
	    		if(player != null && mcUsers.getProfile(player).getWoodCuttingGatherInt() >= mcUsers.getProfile(player).getXpToLevel("woodcutting")){
	    			int skillups = 0;
	    			while(mcUsers.getProfile(player).getWoodCuttingGatherInt() >= mcUsers.getProfile(player).getXpToLevel("woodcutting")){
	    				skillups++;
	    				mcUsers.getProfile(player).removeWoodCuttingGather(mcUsers.getProfile(player).getXpToLevel("woodcutting"));
	    				mcUsers.getProfile(player).skillUpWoodCutting(1);
	    			}
	    			player.sendMessage(ChatColor.YELLOW+"WoodCutting skill increased by "+skillups+"."+" Total ("+mcUsers.getProfile(player).getWoodCutting()+")");	
	    		}
    		}
    }
    
    
    public void onBlockFlow(BlockFromToEvent event) {
    	//Code borrowed from WorldGuard by sk89q
        World world = event.getBlock().getWorld();
        int radius = 1;
        Block blockFrom = event.getBlock();
        Block blockTo = event.getToBlock();
        
        boolean isWater = blockFrom.getTypeId() == 8 || blockFrom.getTypeId() == 9;

            int ox = blockTo.getX();
            int oy = blockTo.getY();
            int oz = blockTo.getZ();

            if(blockTo.getTypeId() == 9 || blockTo.getTypeId() == 8){
            	return;
            }

            for (int cx = -radius; cx <= radius; cx++) {
                for (int cy = -radius; cy <= radius; cy++) {
                    for (int cz = -radius; cz <= radius; cz++) {
                        Block block = world.getBlockAt(ox + cx, oy + cy, oz + cz);
                        //If block is block
                        if (isWater == true &&
                        		block.getTypeId() == 13 && mcLoadProperties.clay) {
                        	//Change
                        	block.setTypeId(82);
                            return;
                        }
                    }
                }
            }
    }
}