package com.gmail.nossr50;

import org.bukkit.ChatColor;

import com.gmail.nossr50.config.LoadProperties;
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

import com.gmail.nossr50.config.*;
import com.gmail.nossr50.datatypes.*;
import com.gmail.nossr50.skills.*;
import com.gmail.nossr50.party.*;

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
    	if(player != null && m.shouldBeWatched(block)){
    		if(block.getTypeId() != 17)
    			block.setData((byte) 5); //Change the byte
    		if(block.getTypeId() == 17 || block.getTypeId() == 91 || block.getTypeId() == 86)
    			Config.getInstance().addBlockWatch(block);
    	}
    	if(block.getTypeId() == 42 && LoadProperties.anvilmessages)
    		event.getPlayer().sendMessage(ChatColor.DARK_RED+"You have placed an anvil, anvils can repair tools and armor.");
    }
    
    public void onBlockBreak(BlockBreakEvent event) {
    	Player player = event.getPlayer();
    	PlayerProfile PP = Users.getProfile(player);
    	Block block = event.getBlock();
    	ItemStack inhand = player.getItemInHand();
    	if(event.isCancelled())
    		return;
    	if (event instanceof FakeBlockBreakEvent) 
    		return;
    	/*
		* Check if the Timer is doing its job
		*/
   		Skills.monitorSkills(player);
    	
   		/*
   		 * HERBALISM
   		 */
   		if(PP.getHoePreparationMode() && mcPermissions.getInstance().herbalismAbility(player) && block.getTypeId() == 59 && block.getData() == (byte) 0x07){
   			Herbalism.greenTerraCheck(player, block, plugin);
   		}
   		//Wheat && Triple drops
   		if(PP.getGreenTerraMode() && Herbalism.canBeGreenTerra(block)){
   			Herbalism.herbalismProcCheck(block, player, event);
   			Herbalism.greenTerraWheat(player, block, event);
   		}
   		
   		
    	/*
    	 * MINING
    	 */
    	if(mcPermissions.getInstance().mining(player)){
    		if(LoadProperties.miningrequirespickaxe){
    			if(m.isMiningPick(inhand))
    				Mining.miningBlockCheck(player, block);
    		} else {
    			Mining.miningBlockCheck(player, block);
    		}
    	}
    	/*
   		 * WOOD CUTTING
   		 */
    	
   		if(player != null && block.getTypeId() == 17 && mcPermissions.getInstance().woodcutting(player)){
   			if(LoadProperties.woodcuttingrequiresaxe){
				if(m.isAxes(inhand)){
					if(!Config.getInstance().isBlockWatched(block)){
	    				WoodCutting.woodCuttingProcCheck(player, block);
	    				//Default
	    				if(block.getData() == (byte)0)
	    					PP.addWoodcuttingXP(7 * LoadProperties.xpGainMultiplier);
	    				//Spruce
	    				if(block.getData() == (byte)1)
	    					PP.addWoodcuttingXP(8 * LoadProperties.xpGainMultiplier);
	    				//Birch
	    				if(block.getData() == (byte)2)
	    					PP.addWoodcuttingXP(9 * LoadProperties.xpGainMultiplier);
					}
    			}
    		} else {
    			if(!Config.getInstance().isBlockWatched(block)){
	    			WoodCutting.woodCuttingProcCheck(player, block);
	    			//Default
    				if(block.getData() == (byte)0)
    					PP.addWoodcuttingXP(7 * LoadProperties.xpGainMultiplier);
    				//Spruce
    				if(block.getData() == (byte)1)
    					PP.addWoodcuttingXP(8 * LoadProperties.xpGainMultiplier);
    				//Birch
    				if(block.getData() == (byte)2)
    					PP.addWoodcuttingXP(9 * LoadProperties.xpGainMultiplier);
    			}
   			}
    		Skills.XpCheck(player);
    			
    		/*
    		 * IF PLAYER IS USING TREEFELLER
    		 */
   			if(mcPermissions.getInstance().woodCuttingAbility(player) 
   					&& PP.getTreeFellerMode() 
   					&& block.getTypeId() == 17
   					&& m.blockBreakSimulate(block, player, plugin)){
   				
    			WoodCutting.treeFeller(block, player);
    			for(Block blockx : Config.getInstance().getTreeFeller()){
    				if(blockx != null){
    					Material mat = Material.getMaterial(block.getTypeId());
    					byte type = 0;
    					if(block.getTypeId() == 17)
    						type = block.getData();
    					ItemStack item = new ItemStack(mat, 1, (byte)0, type);
    					if(blockx.getTypeId() == 17){
    						blockx.getLocation().getWorld().dropItemNaturally(blockx.getLocation(), item);
    						//XP WOODCUTTING
    						if(!Config.getInstance().isBlockWatched(block)){
	    						WoodCutting.woodCuttingProcCheck(player, blockx);
	    						PP.addWoodcuttingXP(7);
    						}
    					}
    					if(blockx.getTypeId() == 18){
    						mat = Material.SAPLING;
    						
    						item = new ItemStack(mat, 1, (short)0, blockx.getData());
    						
    						if(Math.random() * 10 > 9)
    							blockx.getLocation().getWorld().dropItemNaturally(blockx.getLocation(), item);
    					}
    					blockx.setType(Material.AIR);
    				}
    			}
    			if(LoadProperties.toolsLoseDurabilityFromAbilities)
    		    	m.damageTool(player, (short) LoadProperties.abilityDurabilityLoss);
    				Config.getInstance().clearTreeFeller();
    		}
    	}
    	/*
    	 * EXCAVATION
    	 */
    	if(mcPermissions.getInstance().excavation(player) && block.getData() != (byte) 5)
    		Excavation.excavationProcCheck(block, player);
    	/*
    	 * HERBALISM
    	 */
    	if(PP.getHoePreparationMode() && mcPermissions.getInstance().herbalism(player) && Herbalism.canBeGreenTerra(block)){
    		Herbalism.greenTerraCheck(player, block, plugin);
    	}
    	if(mcPermissions.getInstance().herbalism(player) && block.getData() != (byte) 5)
			Herbalism.herbalismProcCheck(block, player, event);
    	
    	//Change the byte back when broken
    	if(block.getData() == 5 && m.shouldBeWatched(block))
    		block.setData((byte) 0);
    }
    public void onBlockDamage(BlockDamageEvent event) {
    	if(event.isCancelled())
    		return;
    	Player player = event.getPlayer();
    	PlayerProfile PP = Users.getProfile(player);
    	ItemStack inhand = player.getItemInHand();
    	Block block = event.getBlock();
    	/*
		* Check if the Timer is doing its job
		*/
   		Skills.monitorSkills(player);
    	/*
    	 * ABILITY PREPARATION CHECKS
    	 */
   		if(PP.getHoePreparationMode() && Herbalism.canBeGreenTerra(block))
    		Herbalism.greenTerraCheck(player, block, plugin);
    	if(PP.getAxePreparationMode() && block.getTypeId() == 17)
    		WoodCutting.treeFellerCheck(player, block, plugin);
    	if(PP.getPickaxePreparationMode())
    		Mining.superBreakerCheck(player, block, plugin);
    	if(PP.getShovelPreparationMode() && Excavation.canBeGigaDrillBroken(block))
    		Excavation.gigaDrillBreakerActivationCheck(player, block, plugin);
    	if(PP.getFistsPreparationMode() && Excavation.canBeGigaDrillBroken(block))
    		Skills.berserkActivationCheck(player, plugin);
    	/*
    	 * GREEN TERRA STUFF
    	 */
    	if(PP.getGreenTerraMode() && mcPermissions.getInstance().herbalismAbility(player) && PP.getGreenTerraMode()){
   			Herbalism.greenTerra(player, block);
   		}
    	
    	/*
    	 * GIGA DRILL BREAKER CHECKS
    	 */
    	if(PP.getGigaDrillBreakerMode() 
    			&& m.blockBreakSimulate(block, player, plugin) 
    			&& Excavation.canBeGigaDrillBroken(block) 
    			&& m.isShovel(inhand)){
    		
    		if(m.getTier(player) >= 2)
    			Excavation.excavationProcCheck(block, player);
    		if(m.getTier(player) >= 3)
    			Excavation.excavationProcCheck(block, player);
    		if(m.getTier(player) >= 4)
    			Excavation.excavationProcCheck(block, player);
    		Material mat = Material.getMaterial(block.getTypeId());
    		if(block.getTypeId() == 2)
    			mat = Material.DIRT;
			byte type = block.getData();
			ItemStack item = new ItemStack(mat, 1, (byte)0, type);
			block.setType(Material.AIR);
			if(LoadProperties.toolsLoseDurabilityFromAbilities)
	    		m.damageTool(player, (short) LoadProperties.abilityDurabilityLoss);
			block.getLocation().getWorld().dropItemNaturally(block.getLocation(), item);
    	}
    	/*
    	 * BERSERK MODE CHECKS
    	 */
    	if(PP.getBerserkMode() 
    		&& m.blockBreakSimulate(block, player, plugin) 
    		&& player.getItemInHand().getTypeId() == 0 
    		&& Excavation.canBeGigaDrillBroken(block)){
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
    			&& Mining.canBeSuperBroken(block)
    			&& m.blockBreakSimulate(block, player, plugin)){
    		
    		if(LoadProperties.miningrequirespickaxe){
    			if(m.isMiningPick(inhand))
    				Mining.SuperBreakerBlockCheck(player, block);
    		} else {
    			Mining.SuperBreakerBlockCheck(player, block);
    		}
    	}
    	
    	/*
    	 * LEAF BLOWER
    	 */
    	if(block.getTypeId() == 18 && mcPermissions.getInstance().woodcutting(player) && PP.getWoodCuttingInt() >= 100 && m.isAxes(player.getItemInHand()) && m.blockBreakSimulate(block, player, plugin))
    	{
    		m.damageTool(player, (short)1);
    		if(Math.random() * 10 > 9)
    		{
    			ItemStack x = new ItemStack(Material.SAPLING, 1, (short)0, block.getData());
    			block.getLocation().getWorld().dropItemNaturally(block.getLocation(), x);
    		}
    		block.setType(Material.AIR);
    	}
    }
    
    public void onBlockFromTo(BlockFromToEvent event) {
        Block blockFrom = event.getBlock();
        Block blockTo = event.getToBlock();
        if(m.shouldBeWatched(blockFrom) && blockFrom.getData() == (byte)5){
        	blockTo.setData((byte)5);
        }
    }
}