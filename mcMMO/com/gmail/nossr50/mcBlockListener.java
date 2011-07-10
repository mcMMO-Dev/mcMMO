package com.gmail.nossr50;

import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.skills.*;
import com.gmail.nossr50.datatypes.FakeBlockBreakEvent;


public class mcBlockListener extends BlockListener {
    private final mcMMO plugin;

    public mcBlockListener(final mcMMO plugin) {
        this.plugin = plugin;
    }
    
    public void onBlockPlace(BlockPlaceEvent event) 
    {
    	long before = System.currentTimeMillis();
    	Block block;
    	Player player = event.getPlayer();
    	if (event.getBlock() != null && event.getBlockReplacedState() != null && event.getBlockReplacedState().getTypeId() == 78) 
    	{
    			block = event.getBlockAgainst();
    		}
    		else 
    		{
    			block = event.getBlock();
    		}
    	if(player != null && m.shouldBeWatched(block))
    	{
    		if(block.getTypeId() != 17 && block.getTypeId() != 39 && block.getTypeId() != 40 && block.getTypeId() != 91 && block.getTypeId() != 86)
    			block.setData((byte) 5); //Change the byte
    		if(block.getTypeId() == 17 || block.getTypeId() == 39 || block.getTypeId() == 40 || block.getTypeId() == 91 || block.getTypeId() == 86)
    			plugin.misc.blockWatchList.add(block);
    	}
    	if(block.getTypeId() == 42 && LoadProperties.anvilmessages)
    		event.getPlayer().sendMessage(Messages.getString("mcBlockListener.PlacedAnvil")); //$NON-NLS-1$
    	
    	long after = System.currentTimeMillis();
    	if(LoadProperties.print_reports)
		{
    		plugin.onBlockPlace+=(after-before);
		}
    }
    
    public void onBlockBreak(BlockBreakEvent event) 
    {
    	long before = System.currentTimeMillis();
    	
    	Player player = event.getPlayer();
    	PlayerProfile PP = Users.getProfile(player);
    	Block block = event.getBlock();
    	ItemStack inhand = player.getItemInHand();
    	if(event.isCancelled())
    		return;
    	if (event instanceof FakeBlockBreakEvent) 
    		return;
    	
   		/*
   		 * HERBALISM
   		 */
   		if(PP.getHoePreparationMode() && mcPermissions.getInstance().herbalismAbility(player) && block.getTypeId() == 59 && block.getData() == (byte) 0x07)
   		{
   			Herbalism.greenTerraCheck(player, block, plugin);
   		}
   		//Wheat && Triple drops
   		if(PP.getGreenTerraMode() && Herbalism.canBeGreenTerra(block))
   		{
   			Herbalism.herbalismProcCheck(block, player, event, plugin);
   			Herbalism.greenTerraWheat(player, block, event, plugin);
   		}
   		
   		
    	/*
    	 * MINING
    	 */
    	if(mcPermissions.getInstance().mining(player)){
    		if(LoadProperties.miningrequirespickaxe){
    			if(m.isMiningPick(inhand))
    				Mining.miningBlockCheck(player, block, plugin);
    		} else {
    			Mining.miningBlockCheck(player, block, plugin);
    		}
    	}
    	/*
   		 * WOOD CUTTING
   		 */
    	
   		if(player != null && block.getTypeId() == 17 && mcPermissions.getInstance().woodcutting(player))
   		{
   			if(LoadProperties.woodcuttingrequiresaxe)
   			{
				if(m.isAxes(inhand))
				{
					if(!plugin.misc.blockWatchList.contains(block))
					{
	    				WoodCutting.woodCuttingProcCheck(player, block);
	    				//Default
	    				if(block.getData() == (byte)0)
	    					PP.addWoodcuttingXP(LoadProperties.mpine * LoadProperties.xpGainMultiplier);
	    				//Spruce
	    				if(block.getData() == (byte)1)
	    					PP.addWoodcuttingXP(LoadProperties.mspruce * LoadProperties.xpGainMultiplier);
	    				//Birch
	    				if(block.getData() == (byte)2)
	    					PP.addWoodcuttingXP(LoadProperties.mbirch * LoadProperties.xpGainMultiplier);
					}
    			}
    		} else 
    		{
    			if(!plugin.misc.blockWatchList.contains(block))
    			{
	    			WoodCutting.woodCuttingProcCheck(player, block);
	    			//Default
    				if(block.getData() == (byte)0)
    					PP.addWoodcuttingXP(LoadProperties.mpine * LoadProperties.xpGainMultiplier);
    				//Spruce
    				if(block.getData() == (byte)1)
    					PP.addWoodcuttingXP(LoadProperties.mspruce * LoadProperties.xpGainMultiplier);
    				//Birch
    				if(block.getData() == (byte)2)
    					PP.addWoodcuttingXP(LoadProperties.mbirch * LoadProperties.xpGainMultiplier);
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
   				
    			WoodCutting.treeFeller(block, player, plugin);
    			for(Block blockx : plugin.misc.treeFeller)
    			{
    				if(blockx != null){
    					Material mat = Material.getMaterial(block.getTypeId());
    					byte type = 0;
    					if(block.getTypeId() == 17)
    						type = block.getData();
    					ItemStack item = new ItemStack(mat, 1, (byte)0, type);
    					if(blockx.getTypeId() == 17){
    						blockx.getLocation().getWorld().dropItemNaturally(blockx.getLocation(), item);
    						//XP WOODCUTTING
    						if(!plugin.misc.blockWatchList.contains(block))
    						{
	    						WoodCutting.woodCuttingProcCheck(player, blockx);
	    						PP.addWoodcuttingXP(LoadProperties.mpine);
    						}
    					}
    					if(blockx.getTypeId() == 18)
    					{
    						mat = Material.SAPLING;
    						
    						item = new ItemStack(mat, 1, (short)0, blockx.getData());
    						
    						if(Math.random() * 10 > 9)
    							blockx.getLocation().getWorld().dropItemNaturally(blockx.getLocation(), item);
    					}
    					if(blockx.getType() != Material.AIR)
    						player.incrementStatistic(Statistic.MINE_BLOCK, event.getBlock().getType());
    					blockx.setType(Material.AIR);
    				}
    			}
    			if(LoadProperties.toolsLoseDurabilityFromAbilities)
    		    	m.damageTool(player, (short) LoadProperties.abilityDurabilityLoss);
    			plugin.misc.treeFeller.clear();
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
    	if(PP.getHoePreparationMode() && mcPermissions.getInstance().herbalism(player) && Herbalism.canBeGreenTerra(block))
    	{
    		Herbalism.greenTerraCheck(player, block, plugin);
    	}
    	if(mcPermissions.getInstance().herbalism(player) && block.getData() != (byte) 5)
			Herbalism.herbalismProcCheck(block, player, event, plugin);
    	
    	//Change the byte back when broken
    	if(block.getData() == 5 && m.shouldBeWatched(block))
    	{
    		block.setData((byte) 0);
    		if(plugin.misc.blockWatchList.contains(block))
    		{
    			plugin.misc.blockWatchList.remove(block);
    		}
    	}
    	
    	long after = System.currentTimeMillis();
    	if(LoadProperties.print_reports)
		{
    		plugin.onBlockBreak+=(after-before);
		}
    }
    public void onBlockDamage(BlockDamageEvent event) 
    {
    	long before = System.currentTimeMillis();
    	
    	if(event.isCancelled())
    		return;
    	Player player = event.getPlayer();
    	PlayerProfile PP = Users.getProfile(player);
    	ItemStack inhand = player.getItemInHand();
    	Block block = event.getBlock();
    	
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
    	if(PP.getFistsPreparationMode() && (Excavation.canBeGigaDrillBroken(block) || block.getTypeId() == 78))
    		Unarmed.berserkActivationCheck(player, plugin);
    	
    	/*
    	 * GREEN TERRA STUFF
    	 */
    	if(PP.getGreenTerraMode() && mcPermissions.getInstance().herbalismAbility(player) && PP.getGreenTerraMode()){
   			Herbalism.greenTerra(player, block);
   		}
    	
    	/*
    	 * GIGA DRILL BREAKER CHECKS
    	 */
    	if(PP.getGigaDrillBreakerMode() && m.blockBreakSimulate(block, player, plugin) && Excavation.canBeGigaDrillBroken(block) && m.isShovel(inhand)){
    		
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
			player.incrementStatistic(Statistic.MINE_BLOCK, event.getBlock().getType());
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
    		&& (Excavation.canBeGigaDrillBroken(block) || block.getTypeId() == 78)){
		   	Material mat = Material.getMaterial(block.getTypeId());
		   	if(block.getTypeId() == 2)
		   		mat = Material.DIRT;
		   	if(block.getTypeId() == 78)
		   		mat = Material.SNOW_BALL;
			byte type = block.getData();
			ItemStack item = new ItemStack(mat, 1, (byte)0, type);
			player.incrementStatistic(Statistic.MINE_BLOCK, event.getBlock().getType());
			block.setType(Material.AIR);
			block.getLocation().getWorld().dropItemNaturally(block.getLocation(), item);
    	}
    	
    	/*
    	 * SUPER BREAKER CHECKS
    	 */
    	if(PP.getSuperBreakerMode() 
    			&& Mining.canBeSuperBroken(block)
    			&& m.blockBreakSimulate(block, player, plugin))
    	{
    		
    		if(LoadProperties.miningrequirespickaxe)
    		{
    			if(m.isMiningPick(inhand))
    				Mining.SuperBreakerBlockCheck(player, block, plugin);
    		} else {
    			Mining.SuperBreakerBlockCheck(player, block, plugin);
    		}
    	}
    	
    	/*
    	 * LEAF BLOWER
    	 */
    	if(block.getTypeId() == 18 && mcPermissions.getInstance().woodcutting(player) && PP.getSkill("woodcutting") >= 100 && m.isAxes(player.getItemInHand()) && m.blockBreakSimulate(block, player, plugin))
    	{
    		m.damageTool(player, (short)1);
    		if(Math.random() * 10 > 9)
    		{
    			ItemStack x = new ItemStack(Material.SAPLING, 1, (short)0, block.getData());
    			block.getLocation().getWorld().dropItemNaturally(block.getLocation(), x);
    		}
    		block.setType(Material.AIR);
    		player.incrementStatistic(Statistic.MINE_BLOCK, event.getBlock().getType());
    	}
    	if(block.getType() == Material.AIR && plugin.misc.blockWatchList.contains(block))
    	{
    		plugin.misc.blockWatchList.remove(block);
    	}
    	long after = System.currentTimeMillis();
    	if(LoadProperties.print_reports)
		{
    		plugin.onBlockDamage+=(after-before);
		}
    }
    
    public void onBlockFromTo(BlockFromToEvent event) 
    {
    	long before = System.currentTimeMillis();
    	
        Block blockFrom = event.getBlock();
        Block blockTo = event.getToBlock();
        if(m.shouldBeWatched(blockFrom) && blockFrom.getData() == (byte)5)
        {
        	blockTo.setData((byte)5);
        }
        long after = System.currentTimeMillis();
        if(LoadProperties.print_reports)
		{
        	plugin.onBlockFromTo+=(after-before);
		}
    }
}