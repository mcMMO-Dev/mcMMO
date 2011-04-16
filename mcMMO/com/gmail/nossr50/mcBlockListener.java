package com.gmail.nossr50;

import org.bukkit.ChatColor;
import com.gmail.nossr50.datatypes.PlayerProfile;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.datatypes.FakeBlockBreakEvent;


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
    	if(player != null && mcm.shouldBeWatched(block)){
    		if(block.getTypeId() != 17)
    			block.setData((byte) 5); //Change the byte
    		if(block.getTypeId() == 17)
    			mcConfig.getInstance().addBlockWatch(block);
    	}
    	if(block.getTypeId() == 42 && mcLoadProperties.anvilmessages)
    		event.getPlayer().sendMessage(ChatColor.DARK_RED+"You have placed an anvil, anvils can repair tools and armor.");
    }
    
    public void onBlockBreak(BlockBreakEvent event) {
    	Player player = event.getPlayer();
    	PlayerProfile PP = mcUsers.getProfile(player);
    	Block block = event.getBlock();
    	ItemStack inhand = player.getItemInHand();
    	if(event.isCancelled())
    		return;
    	if (event instanceof FakeBlockBreakEvent) 
    		return;
    	/*
		* Check if the Timer is doing its job
		*/
   		mcSkills.monitorSkills(player);
    	
   		/*
   		 * HERBALISM
   		 */
   		if(PP.getHoePreparationMode() && mcPermissions.getInstance().herbalismAbility(player) && block.getTypeId() == 59 && block.getData() == (byte) 0x07){
   			mcHerbalism.greenTerraCheck(player, block, plugin);
   		}
   		//Wheat && Triple drops
   		if(PP.getGreenTerraMode() && mcHerbalism.canBeGreenTerra(block)){
   			mcHerbalism.herbalismProcCheck(block, player, event);
   			mcHerbalism.greenTerraWheat(player, block, event);
   		}
   		
   		
    	/*
    	 * MINING
    	 */
    	if(mcPermissions.getInstance().mining(player)){
    		if(mcLoadProperties.miningrequirespickaxe){
    			if(mcm.isMiningPick(inhand))
    				mcMining.miningBlockCheck(player, block);
    		} else {
    			mcMining.miningBlockCheck(player, block);
    		}
    	}
    	/*
   		 * WOOD CUTTING
   		 */
    	
   		if(player != null && block.getTypeId() == 17 && mcPermissions.getInstance().woodcutting(player)){
   			if(mcLoadProperties.woodcuttingrequiresaxe){
				if(mcm.isAxes(inhand)){
					if(!mcConfig.getInstance().isBlockWatched(block)){
	    				mcWoodCutting.woodCuttingProcCheck(player, block);
	    				PP.addWoodcuttingXP(7 * mcLoadProperties.xpGainMultiplier);
					}
    			}
    		} else {
    			if(block.getData() != 5){
	    			mcWoodCutting.woodCuttingProcCheck(player, block);
					PP.addWoodcuttingXP(7 * mcLoadProperties.xpGainMultiplier);	
    			}
   			}
    		mcSkills.XpCheck(player);
    			
    		/*
    		 * IF PLAYER IS USING TREEFELLER
    		 */
   			if(mcPermissions.getInstance().woodCuttingAbility(player) 
   					&& PP.getTreeFellerMode() 
   					&& block.getTypeId() == 17
   					&& mcm.blockBreakSimulate(block, player, plugin)){
   				
    			mcWoodCutting.treeFeller(block, player);
    			for(Block blockx : mcConfig.getInstance().getTreeFeller()){
    				if(blockx != null){
    					Material mat = Material.getMaterial(block.getTypeId());
    					byte type = 0;
    					if(block.getTypeId() == 17)
    						type = block.getData();
    					ItemStack item = new ItemStack(mat, 1, (byte)0, type);
    					if(blockx.getTypeId() == 17){
    						blockx.getLocation().getWorld().dropItemNaturally(blockx.getLocation(), item);
    						//XP WOODCUTTING
    						if(!mcConfig.getInstance().isBlockWatched(block)){
	    						mcWoodCutting.woodCuttingProcCheck(player, blockx);
	    						PP.addWoodcuttingXP(7);
    						}
    					}
    					if(blockx.getTypeId() == 18){
    						mat = Material.getMaterial(6);
    						item = new ItemStack(mat, 1, (byte)0, (byte) 0);
    						if(Math.random() * 10 > 8)
    							blockx.getLocation().getWorld().dropItemNaturally(blockx.getLocation(), item);
    					}
    					blockx.setType(Material.AIR);
    				}
    			}
    			if(mcLoadProperties.toolsLoseDurabilityFromAbilities)
    		    	mcm.damageTool(player, (short) mcLoadProperties.abilityDurabilityLoss);
    				mcConfig.getInstance().clearTreeFeller();
    		}
    	}
    	/*
    	 * EXCAVATION
    	 */
    	if(mcPermissions.getInstance().excavation(player) && block.getData() != (byte) 5)
    		mcExcavation.excavationProcCheck(block, player);
    	/*
    	 * HERBALISM
    	 */
    	if(PP.getHoePreparationMode() && mcPermissions.getInstance().herbalism(player) && mcHerbalism.canBeGreenTerra(block)){
    		mcHerbalism.greenTerraCheck(player, block, plugin);
    	}
    	if(mcPermissions.getInstance().herbalism(player) && block.getData() != (byte) 5)
			mcHerbalism.herbalismProcCheck(block, player, event);
    	
    	//Change the byte back when broken
    	if(block.getData() == 5)
    		block.setData((byte) 0);
    }
    public void onBlockDamage(BlockDamageEvent event) {
    	if(event.isCancelled())
    		return;
    	Player player = event.getPlayer();
    	PlayerProfile PP = mcUsers.getProfile(player);
    	ItemStack inhand = player.getItemInHand();
    	Block block = event.getBlock();
    	/*
		* Check if the Timer is doing its job
		*/
   		mcSkills.monitorSkills(player);
    	/*
    	 * ABILITY PREPARATION CHECKS
    	 */
   		if(PP.getHoePreparationMode() && mcHerbalism.canBeGreenTerra(block))
    		mcHerbalism.greenTerraCheck(player, block, plugin);
    	if(PP.getAxePreparationMode() && block.getTypeId() == 17)
    		mcWoodCutting.treeFellerCheck(player, block, plugin);
    	if(PP.getPickaxePreparationMode())
    		mcMining.superBreakerCheck(player, block, plugin);
    	if(PP.getShovelPreparationMode() && mcExcavation.canBeGigaDrillBroken(block))
    		mcExcavation.gigaDrillBreakerActivationCheck(player, block, plugin);
    	if(PP.getFistsPreparationMode() && mcExcavation.canBeGigaDrillBroken(block))
    		mcSkills.berserkActivationCheck(player, plugin);
    	/*
    	 * GREEN TERRA STUFF
    	 */
    	if(PP.getGreenTerraMode() && mcPermissions.getInstance().herbalismAbility(player) && PP.getGreenTerraMode()){
   			mcHerbalism.greenTerra(player, block);
   		}
    	
    	/*
    	 * GIGA DRILL BREAKER CHECKS
    	 */
    	if(PP.getGigaDrillBreakerMode() 
    			&& mcm.blockBreakSimulate(block, player, plugin) 
    			&& mcExcavation.canBeGigaDrillBroken(block) 
    			&& mcm.isShovel(inhand)){
    		
    		if(mcm.getTier(player) >= 2)
    			mcExcavation.excavationProcCheck(block, player);
    		if(mcm.getTier(player) >= 3)
    			mcExcavation.excavationProcCheck(block, player);
    		if(mcm.getTier(player) >= 4)
    			mcExcavation.excavationProcCheck(block, player);
    		Material mat = Material.getMaterial(block.getTypeId());
    		if(block.getTypeId() == 2)
    			mat = Material.DIRT;
			byte type = block.getData();
			ItemStack item = new ItemStack(mat, 1, (byte)0, type);
			block.setType(Material.AIR);
			if(mcLoadProperties.toolsLoseDurabilityFromAbilities)
	    		mcm.damageTool(player, (short) mcLoadProperties.abilityDurabilityLoss);
			block.getLocation().getWorld().dropItemNaturally(block.getLocation(), item);
    	}
    	/*
    	 * BERSERK MODE CHECKS
    	 */
    	if(PP.getBerserkMode() 
    		&& mcm.blockBreakSimulate(block, player, plugin) 
    		&& player.getItemInHand().getTypeId() == 0 
    		&& mcExcavation.canBeGigaDrillBroken(block)){
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
    	if(PP.getSuperBreakerMode() 
    			&& mcMining.canBeSuperBroken(block)
    			&& mcm.blockBreakSimulate(block, player, plugin)){
    		
    		if(mcLoadProperties.miningrequirespickaxe){
    			if(mcm.isMiningPick(inhand))
    				mcMining.SuperBreakerBlockCheck(player, block);
    		} else {
    			mcMining.SuperBreakerBlockCheck(player, block);
    		}
    	}
    	
    }
    
    public void onBlockFromTo(BlockFromToEvent event) {
    	//Code borrowed from WorldGuard by sk89q
        World world = event.getBlock().getWorld();
        int radius = 1;
        Block blockFrom = event.getBlock();
        Block blockTo = event.getToBlock();
        
        boolean isWater = blockFrom.getTypeId() == 8 || blockFrom.getTypeId() == 9;

            int ox = blockTo.getX();
            int oy = blockTo.getY();
            int oz = blockTo.getZ();

            if(blockTo.getTypeId() == 9 || blockTo.getTypeId() == 8)
            	return;
            
            if(mcLoadProperties.clay){
	            for (int cx = -radius; cx <= radius; cx++) {
	                for (int cy = -radius; cy <= radius; cy++) {
	                    for (int cz = -radius; cz <= radius; cz++) {
	                        Block block = world.getBlockAt(ox + cx, oy + cy, oz + cz);
	                        //If block is block
	                        if (isWater == true && block.getType() == Material.GRAVEL){
	                        	//Change
	                        	block.setType(Material.CLAY);
	                            return;
	                        }
	                    }
	                }
	            }
            }
    }
}