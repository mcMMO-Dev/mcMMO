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
    	if(block.getTypeId() == 42)
    		event.getPlayer().sendMessage(ChatColor.DARK_RED+"You have placed an anvil, anvils can repair tools and armor.");
    }
    //put all Block related code here
    public void onBlockDamage(BlockDamageEvent event) {
    		//STARTED(0), DIGGING(1), BROKEN(3), STOPPED(2);
    		Player player = event.getPlayer();
    		Block block = event.getBlock();
    		int x = block.getX();
        	int y = block.getY();
        	int z = block.getZ();
        	String xyz = x+","+y+","+z;
    		int type = block.getTypeId();
    		Location loc = block.getLocation();
    		int dmg = event.getDamageLevel().getLevel();
    		/*
    		 * MINING
    		 */
    		if(dmg == 3 && !mcConfig.getInstance().isBlockWatched(block) && !mcConfig.getInstance().isCoordsWatched(xyz)){
    		if(mcPermissions.getInstance().mining(player))
    		mcm.getInstance().miningBlockCheck(player, block);
    		/*
    		 * WOOD CUTTING
    		 */
    		if(block.getTypeId() == 17 
    				&& mcPermissions.getInstance().woodcutting(player)){
    		mcUsers.getProfile(player).addwgather(1);    		
    			mcm.getInstance().woodCuttingProcCheck(player, block, loc);
    		}
    		mcm.getInstance().simulateSkillUp(player);
    		/*
    		 * EXCAVATION
    		 */
    		if(mcPermissions.getInstance().excavation(player))
    		mcm.getInstance().excavationProcCheck(block, player);
    		/*
    		 * HERBALISM
    		 */
    		if(!(type == 39 || type == 40 || type == 37 || type == 38) 
    				&& mcPermissions.getInstance().herbalism(player));
    		mcm.getInstance().herbalismProcCheck(block, player); //You place the blocks so we wont check if they are being watched
    		/*
    		 * EXPLOIT COUNTERMEASURES
    		 */
    		mcConfig.getInstance().addCoordsWatch(xyz);
    		mcConfig.getInstance().addBlockWatch(block);
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
                        Block dirt = world.getBlockAt(ox + cx, oy + cy, oz + cz);
                        //If block is dirt
                        if (isWater == true &&
                        		dirt.getTypeId() == 13) {
                        	//Change
                        	dirt.setTypeId(82);
                            return;
                        }
                    }
                }
            }
    }
}