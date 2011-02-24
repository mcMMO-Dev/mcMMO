package com.gmail.nossr50;

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
    	if (event.getBlockReplacedState().getTypeId() == 78) {
    		block = event.getBlockAgainst();
    		}
    		else {
    		block = event.getBlock();
    		}
    	int x = block.getX();
    	int y = block.getY();
    	int z = block.getZ();
    	String xyz = x+","+y+","+z;
    	mcConfig.getInstance().addBlockWatch(block);
    	mcConfig.getInstance().addCoordsWatch(xyz);
    	if(block.getTypeId() == 42 && mcLoadProperties.anvilmessages)
    		event.getPlayer().sendMessage(ChatColor.DARK_RED+"You have placed an anvil, anvils can repair tools and armor.");
    }
    //put all Block related code here
    public void onBlockDamage(BlockDamageEvent event) {
    		//STARTED(0), DIGGING(1), BROKEN(3), STOPPED(2);
    		Player player = event.getPlayer();
    		//player.sendMessage("mcMMO DEBUG: EVENT-OK DMG LEVEL ("+event.getDamageLevel().getLevel()+")");
    		Block block = event.getBlock();
    		int x = block.getX();
        	int y = block.getY();
        	int z = block.getZ();
        	String xyz = x+","+y+","+z;
    		int type = block.getTypeId();
    		Location loc = block.getLocation();
    		int dmg = event.getDamageLevel().getLevel();
    		/*
    		 * HERBALISM
    		 */
    		if(dmg == 3){
        		if(mcPermissions.getInstance().herbalism(player))
        		mcm.getInstance().herbalismProcCheck(block, player);
    		}
    		/*
    		 * MINING
    		 */
    		if(dmg == 2 && !mcConfig.getInstance().isBlockWatched(block) && !mcConfig.getInstance().isCoordsWatched(xyz)){
    		if(mcPermissions.getInstance().mining(player))
    		mcm.getInstance().miningBlockCheck(player, block);
    		/*
    		 * WOOD CUTTING
    		 */
    		if(block.getTypeId() == 17 && mcPermissions.getInstance().woodcutting(player)){    		
    				mcm.getInstance().woodCuttingProcCheck(player, block, loc);
    				mcUsers.getProfile(player).addWoodcuttingGather(2);
    		}
    		/*
    		 * EXCAVATION
    		 */
    		if(mcPermissions.getInstance().excavation(player) && block != null && player != null)
    		mcm.getInstance().excavationProcCheck(block, player);
    		/*
    		 * EXPLOIT COUNTERMEASURES
    		 */
    		mcConfig.getInstance().addCoordsWatch(xyz);
    		mcConfig.getInstance().addBlockWatch(block);
    		if(mcUsers.getProfile(player).getWoodCuttingGatherInt() >= (mcUsers.getProfile(player).getWoodCuttingInt() + 5) * mcLoadProperties.xpmodifier){
    			int skillups = 0;
    			while(mcUsers.getProfile(player).getWoodCuttingGatherInt() >= (mcUsers.getProfile(player).getWoodCuttingInt() +5) * mcLoadProperties.xpmodifier){
    				skillups++;
    				mcUsers.getProfile(player).removeWoodCuttingGather((mcUsers.getProfile(player).getWoodCuttingInt() + 5) * mcLoadProperties.xpmodifier);
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