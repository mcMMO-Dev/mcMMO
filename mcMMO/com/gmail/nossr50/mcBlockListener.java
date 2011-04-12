package com.gmail.nossr50;

import org.bukkit.ChatColor;
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

import com.gmail.nossr50.PlayerList.PlayerProfile;


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
    	if(player != null && mcm.getInstance().shouldBeWatched(block)){
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
    	PlayerProfile PP = mcUsers.getProfile(player.getName());
    	Block block = event.getBlock();
    	ItemStack inhand = player.getItemInHand();
    	if(event.isCancelled())
    		return;
    	if (event instanceof FakeBlockBreakEvent) 
    		return;
    	/*
		* Check if the Timer is doing its job
		*/
   		mcSkills.getInstance().monitorSkills(player);
    	
   		/*
   		 * HERBALISM
   		 */
   		if(mcPermissions.getInstance().herbalismAbility(player)&& PP.getGreenTerraMode() && block.getTypeId() == 59 && block.getData() == (byte) 0x07){
   			mcHerbalism.getInstance().greenTerraCheck(player, block, plugin);
   			mcHerbalism.getInstance().greenTerraWheat(player, block, event);
   		}
   		
   		
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
					if(!mcConfig.getInstance().isBlockWatched(block)){
	    				mcWoodCutting.getInstance().woodCuttingProcCheck(player, block);
	    				PP.addWoodcuttingGather(7 * mcLoadProperties.xpGainMultiplier);
					}
    			}
    		} else {
    			if(block.getData() != 5){
	    			mcWoodCutting.getInstance().woodCuttingProcCheck(player, block);
					PP.addWoodcuttingGather(7 * mcLoadProperties.xpGainMultiplier);	
    			}
   			}
    		mcSkills.getInstance().XpCheck(player);
    			
    		/*
    		 * IF PLAYER IS USING TREEFELLER
    		 */
   			if(mcPermissions.getInstance().woodCuttingAbility(player) 
   					&& PP.getTreeFellerMode() 
   					&& block.getTypeId() == 17
   					&& mcm.getInstance().blockBreakSimulate(block, player, plugin)){
   				
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
    						//XP WOODCUTTING
    						if(!mcConfig.getInstance().isBlockWatched(block)){
	    						mcWoodCutting.getInstance().woodCuttingProcCheck(player, blockx);
	    						PP.addWoodcuttingGather(7);
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
    		    	mcm.getInstance().damageTool(player, (short) mcLoadProperties.abilityDurabilityLoss);
    				mcConfig.getInstance().clearTreeFeller();
    		}
    	}
    	/*
    	 * EXCAVATION
    	 */
    	if(mcPermissions.getInstance().excavation(player) && block.getData() != (byte) 5)
    		mcExcavation.getInstance().excavationProcCheck(block, player);
    	/*
    	 * HERBALISM
    	 */
    	if(mcPermissions.getInstance().herbalism(player) && mcHerbalism.getInstance().canBeGreenTerra(block)){
    		mcHerbalism.getInstance().greenTerraCheck(player, block, plugin);
    		if(block.getData() != (byte) 5)
    			mcHerbalism.getInstance().herbalismProcCheck(block, player, event);
    	}
    	//Change the byte back when broken
    	if(block.getData() == 5)
    		block.setData((byte) 0);
    }
    public void onBlockDamage(BlockDamageEvent event) {
    	if(event.isCancelled())
    		return;
    	Player player = event.getPlayer();
    	PlayerProfile PP = mcUsers.getProfile(player.getName());
    	ItemStack inhand = player.getItemInHand();
    	Block block = event.getBlock();
    	/*
		* Check if the Timer is doing its job
		*/
   		mcSkills.getInstance().monitorSkills(player);
    	/*
    	 * ABILITY PREPARATION CHECKS
    	 */
   		if(PP.getHoePreparationMode() && mcHerbalism.getInstance().canBeGreenTerra(block))
    		mcHerbalism.getInstance().greenTerraCheck(player, block, plugin);
    	if(PP.getAxePreparationMode() && block.getTypeId() == 17)
    		mcWoodCutting.getInstance().treeFellerCheck(player, block, plugin);
    	if(PP.getPickaxePreparationMode())
    		mcMining.getInstance().superBreakerCheck(player, block, plugin);
    	if(PP.getShovelPreparationMode() && mcExcavation.getInstance().canBeGigaDrillBroken(block))
    		mcExcavation.getInstance().gigaDrillBreakerActivationCheck(player, block, plugin);
    	if(PP.getFistsPreparationMode() && mcExcavation.getInstance().canBeGigaDrillBroken(block))
    		mcSkills.getInstance().berserkActivationCheck(player, plugin);
    	/*
    	 * GREEN TERRA STUFF
    	 */
    	if(PP.getGreenTerraMode() && mcPermissions.getInstance().herbalismAbility(player) && PP.getGreenTerraMode()){
   			mcHerbalism.getInstance().greenTerra(player, block);
   		}
    	
    	/*
    	 * GIGA DRILL BREAKER CHECKS
    	 */
    	if(PP.getGigaDrillBreakerMode() 
    			&& mcm.getInstance().blockBreakSimulate(block, player, plugin) 
    			&& mcExcavation.getInstance().canBeGigaDrillBroken(block) 
    			&& mcm.getInstance().isShovel(inhand)){
    		
    		if(mcm.getInstance().getTier(player) >= 2)
    			mcExcavation.getInstance().excavationProcCheck(block, player);
    		if(mcm.getInstance().getTier(player) >= 3)
    			mcExcavation.getInstance().excavationProcCheck(block, player);
    		if(mcm.getInstance().getTier(player) >= 4)
    			mcExcavation.getInstance().excavationProcCheck(block, player);
    		Material mat = Material.getMaterial(block.getTypeId());
    		if(block.getTypeId() == 2)
    			mat = Material.DIRT;
			byte type = block.getData();
			ItemStack item = new ItemStack(mat, 1, (byte)0, type);
			block.setType(Material.AIR);
			if(mcLoadProperties.toolsLoseDurabilityFromAbilities)
	    		mcm.getInstance().damageTool(player, (short) mcLoadProperties.abilityDurabilityLoss);
			block.getLocation().getWorld().dropItemNaturally(block.getLocation(), item);
    	}
    	/*
    	 * BERSERK MODE CHECKS
    	 */
    	if(PP.getBerserkMode() 
    		&& mcm.getInstance().blockBreakSimulate(block, player, plugin) 
    		&& player.getItemInHand().getTypeId() == 0 
    		&& mcExcavation.getInstance().canBeGigaDrillBroken(block)){
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
    			&& mcMining.getInstance().canBeSuperBroken(block)
    			&& mcm.getInstance().blockBreakSimulate(block, player, plugin)){
    		
    		if(mcLoadProperties.miningrequirespickaxe){
    			if(mcm.getInstance().isMiningPick(inhand))
    				mcMining.getInstance().SuperBreakerBlockCheck(player, block);
    		} else {
    			mcMining.getInstance().SuperBreakerBlockCheck(player, block);
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