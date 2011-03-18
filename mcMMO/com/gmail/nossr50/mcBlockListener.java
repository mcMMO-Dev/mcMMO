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
import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.inventory.ItemStack;

public class mcBlockListener extends BlockListener {
    private final mcMMO plugin;

    public mcBlockListener(final mcMMO plugin) {
        this.plugin = plugin;
    }
    public void onBlockPlace(BlockPlaceEvent event) {
    	Block block;
    	Player player = event.getPlayer();
    	if (event.getBlock() != null && event.getBlockReplacedState() != null && event.getBlockReplacedState().getTypeId() == 78) {
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
    public void onBlockRightClick(BlockRightClickEvent event) {
    	Block block = event.getBlock();
    	Player player = event.getPlayer();
    	ItemStack is = player.getItemInHand();
    	if(mcPermissions.getInstance().unarmed(player) && player.getItemInHand().getTypeId() == 0 && mcm.getInstance().abilityBlockCheck(block)){
    		mcSkills.getInstance().abilityActivationCheck(player, block);
    	}
    	if(block != null && player != null && mcPermissions.getInstance().repair(player) && event.getBlock().getTypeId() == 42){
        	mcRepair.getInstance().repairCheck(player, is, event.getBlock());
        	}
    }
    //put all Block related code here
    public void onBlockDamage(BlockDamageEvent event) {
    	if(event.isCancelled())
    		return;
    	//STARTED(0), DIGGING(1), BROKEN(3), STOPPED(2);
    	Player player = event.getPlayer();
    	ItemStack inhand = player.getItemInHand();
    	//player.sendMessage("mcMMO DEBUG: EVENT-OK DMG LEVEL ("+event.getDamageLevel().getLevel()+")");
    	Block block = event.getBlock();
    	Location loc = block.getLocation();
    	int dmg = event.getDamageLevel().getLevel();
    	/*
    	 * ABILITY PREPARATION CHECKS
    	 */
    	if(mcUsers.getProfile(player).getAxePreparationMode() && block.getTypeId() == 17)
    		mcWoodCutting.getInstance().treeFellerCheck(player, block);
    	if(mcUsers.getProfile(player).getPickaxePreparationMode())
    		mcMining.getInstance().superBreakerCheck(player, block);
    	if(mcUsers.getProfile(player).getShovelPreparationMode() && mcExcavation.getInstance().canBeGigaDrillBroken(block))
    		mcExcavation.getInstance().gigaDrillBreakerActivationCheck(player, block);
    	if(mcUsers.getProfile(player).getFistsPreparationMode() && mcExcavation.getInstance().canBeGigaDrillBroken(block))
    		mcSkills.getInstance().berserkActivationCheck(player);
    	/*
    	 * GIGA DRILL BREAKER CHECKS
    	 */
    	if(mcUsers.getProfile(player).getGigaDrillBreakerMode() && dmg == 0 && mcExcavation.getInstance().canBeGigaDrillBroken(block) && mcm.getInstance().isShovel(inhand)){
    		mcExcavation.getInstance().excavationProcCheck(block, player);
    		mcExcavation.getInstance().excavationProcCheck(block, player);
    		mcExcavation.getInstance().excavationProcCheck(block, player);
    		Material mat = Material.getMaterial(block.getTypeId());
    		if(block.getTypeId() == 2)
    			mat = Material.DIRT;
			byte type = block.getData();
			ItemStack item = new ItemStack(mat, 1, (byte)0, type);
			block.setType(Material.AIR);
			block.getLocation().getWorld().dropItemNaturally(block.getLocation(), item);
    	}
    	/*
    	 * BERSERK MODE CHECKS
    	 */
    	if(mcUsers.getProfile(player).getBerserkMode() && player.getItemInHand().getTypeId() == 0 && dmg == 0 && mcExcavation.getInstance().canBeGigaDrillBroken(block)){
    		Material mat = Material.getMaterial(block.getTypeId());
    		if(block.getTypeId() == 2)
    			mat = Material.DIRT;
			byte type = block.getData();
			ItemStack item = new ItemStack(mat, 1, (byte)0, type);
			block.setType(Material.AIR);
			block.getLocation().getWorld().dropItemNaturally(block.getLocation(), item);
    	}
    	
    	/*
    	 * SUPER BREAKER CHECKS
    	 */
    	if(mcUsers.getProfile(player).getSuperBreakerMode() && dmg == 0 && mcMining.getInstance().canBeSuperBroken(block)){
    		if(mcLoadProperties.miningrequirespickaxe){
    			if(mcm.getInstance().isMiningPick(inhand))
    				mcMining.getInstance().SuperBreakerBlockCheck(player, block);
    		} else {
    			mcMining.getInstance().SuperBreakerBlockCheck(player, block);
    		}
    	}
    	
    	/*
    	 * HERBALISM
    	 */
    	if(dmg == 3){
        	if(mcPermissions.getInstance().herbalism(player))
       		mcHerbalism.getInstance().herbalismProcCheck(block, player);
    	}
    	if(player != null && dmg == 2 && !mcConfig.getInstance().isBlockWatched(block)){
    		/*
        	 * MINING
        	 */
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
	    			mcSkills.getInstance().XpCheck(player);
	    			/*
	    			 * IF PLAYER IS USING TREEFELLER
	   				 */
	   				if(mcPermissions.getInstance().woodCuttingAbility(player) && mcUsers.getProfile(player).getTreeFellerMode() && block.getTypeId() == 17){
	    				mcWoodCutting.getInstance().treeFeller(block, player);
	    				for(Block blockx : mcConfig.getInstance().getTreeFeller()){
	    					if(blockx != null){
	    						Material mat = Material.getMaterial(block.getTypeId());
	    						byte type = 0;
	    						if(block.getTypeId() == 17)
	    							type = block.getData();
	    						ItemStack item = new ItemStack(mat, 1, (byte)0, type);
	    						if(blockx.getTypeId() == 17){
	    							blockx.getLocation().getWorld().dropItemNaturally(blockx.getLocation(), item);
	    							mcWoodCutting.getInstance().woodCuttingProcCheck(player, blockx, blockx.getLocation());
	    							mcUsers.getProfile(player).addWoodcuttingGather(7);
	    						}
	    						if(blockx.getTypeId() == 18){
	    							mat = Material.getMaterial(6);
	    							item = new ItemStack(mat, 1, (byte)0, type);
	    							if(Math.random() * 10 > 8)
	    								blockx.getLocation().getWorld().dropItemNaturally(blockx.getLocation(), item);
	    						}
	    						blockx.setType(Material.AIR);
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