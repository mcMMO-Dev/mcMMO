/*
	This file is part of mcMMO.

    mcMMO is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    mcMMO is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with mcMMO.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.gmail.nossr50.listeners;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundEffect;

import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.skills.*;
import com.gmail.nossr50.datatypes.FakeBlockBreakEvent;

import net.minecraft.server.Enchantment;

public class mcBlockListener implements Listener 
{
    private final mcMMO plugin;

    public mcBlockListener(final mcMMO plugin) 
    {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) 
    {
    	//Setup some basic vars
    	Block block;
    	Player player = event.getPlayer();
    	
    	//When blocks are placed on snow this event reports the wrong block.
    	if (event.getBlockReplacedState() != null && event.getBlockReplacedState().getTypeId() == 78) 
    	{
    		block = event.getBlockAgainst();
    	}
    	else 
    	{
    		block = event.getBlock();
    	}
    	
    	//Check if the blocks placed should be monitored so they do not give out XP in the future
    	if(m.shouldBeWatched(block))
    	{
    		int id = block.getTypeId();
    		
    		//Only needed for blocks that use their block data (wood, pumpkins, etc.)
    		if (id == 17 || id == 73 || id == 74 || id == 81 || id == 83 || id == 86 || id == 91 || id == 106 || id == 98)
    			plugin.misc.blockWatchList.add(block);
    		else {
    			//block.setData((byte) 5); //Change the byte
    			//The following is a method to get around a breakage in 1.1-R2 and onward
    			//it should be removed as soon as functionality to change a block
    			//in this event returns.
    			plugin.changeQueue.push(block);
    		}
    	}
    	
    	if(block.getTypeId() == 42 && LoadProperties.anvilmessages)
    	{
    		PlayerProfile PP = Users.getProfile(player);
    		if(LoadProperties.spoutEnabled)
    		{
    			SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
	    		if(sPlayer.isSpoutCraftEnabled())
	    		{
	    			if(!PP.getPlacedAnvil())
	    			{
	    				sPlayer.sendNotification("[mcMMO] Anvil Placed", "Right click to repair!", Material.IRON_BLOCK);
	    				PP.togglePlacedAnvil();
	    			}
	    		}
	    		else
	    		{
	    			if(!PP.getPlacedAnvil())
	    			{
	    				event.getPlayer().sendMessage(mcLocale.getString("mcBlockListener.PlacedAnvil")); //$NON-NLS-1$
	    				PP.togglePlacedAnvil();
	    			}
	    		}
    		}
    		else
    		{
    			if(!PP.getPlacedAnvil())
    			{
    				event.getPlayer().sendMessage(mcLocale.getString("mcBlockListener.PlacedAnvil")); //$NON-NLS-1$
    				PP.togglePlacedAnvil();
    			}
    		}
    	}
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) 
    {
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
    	
    	//Green Terra
   		if(PP.getHoePreparationMode() && mcPermissions.getInstance().herbalismAbility(player) && block.getTypeId() == 59 && block.getData() == (byte) 0x07)
   		{
   			Herbalism.greenTerraCheck(player, block);
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
   		
    	if(mcPermissions.getInstance().mining(player))
    	{
    		if(LoadProperties.miningrequirespickaxe)
    		{
    			if(m.isMiningPick(inhand))
    			{
    				Mining.miningBlockCheck(player, block, plugin);
    			}
    		} else 
    		{
    			Mining.miningBlockCheck(player, block, plugin);
    		}
    	}
    	
    	
    	/*
   		 * WOOD CUTTING
   		 */
    	
   		if(mcPermissions.getInstance().woodcutting(player))
   		{
   			if(LoadProperties.woodcuttingrequiresaxe)
   			{
				if(m.isAxes(inhand))
				{
					WoodCutting.woodcuttingBlockCheck(player, block, plugin);
    			}
    		} else 
    		{
    			WoodCutting.woodcuttingBlockCheck(player, block, plugin);
   			}
    			
    		/*
    		 * IF PLAYER IS USING TREEFELLER
    		 */
   			if(mcPermissions.getInstance().woodCuttingAbility(player) 
   					&& PP.getTreeFellerMode() 
   					&& block.getTypeId() == 17
   					&& m.blockBreakSimulate(block, player))
   			{
   				if(LoadProperties.spoutEnabled)
   					SpoutStuff.playSoundForPlayer(SoundEffect.EXPLODE, player, block.getLocation());
   				
    			WoodCutting.treeFeller(block, player, plugin);
    			for(Block blockx : plugin.misc.treeFeller)
    			{
    				if(blockx != null)
    				{
    					Material mat = Material.getMaterial(block.getTypeId());
    					byte type = 0;
    					if(block.getTypeId() == 17)
    						type = block.getData();
    					ItemStack item = new ItemStack(mat, 1, (byte)0, type);
    					if(blockx.getTypeId() == 17)
    					{
    						m.mcDropItem(blockx.getLocation(), item);
    						//XP WOODCUTTING
    						if(!plugin.misc.blockWatchList.contains(block))
    						{
	    						WoodCutting.woodCuttingProcCheck(player, blockx);
	    						PP.addXP(SkillType.WOODCUTTING, LoadProperties.mpine, player);
    						}
    					}
    					if(blockx.getTypeId() == 18)
    					{
    						mat = Material.SAPLING;
    						
    						item = new ItemStack(mat, 1, (short)0, (byte)(blockx.getData()-8));
    						
    						if(Math.random() * 10 > 9)
    							m.mcDropItem(blockx.getLocation(), item);
    					}
    					if(blockx.getType() != Material.AIR)
    						player.incrementStatistic(Statistic.MINE_BLOCK, event.getBlock().getType());
    					blockx.setType(Material.AIR);
    				}
    			}
    			if(LoadProperties.toolsLoseDurabilityFromAbilities)
    	    	{
    	    		if(inhand.getEnchantments().containsKey(Enchantment.DURABILITY))
    	    			m.damageTool(player, (short) LoadProperties.abilityDurabilityLoss);
    	    	}
    			plugin.misc.treeFeller.clear();
    		}
    	}
    	/*
    	 * EXCAVATION
    	 */
    	if(Excavation.canBeGigaDrillBroken(block) && mcPermissions.getInstance().excavation(player) && block.getData() != (byte) 5)
    		Excavation.excavationProcCheck(block.getData(), block.getType(), block.getLocation(), player);
    	/*
    	 * HERBALISM
    	 */
    	if(PP.getHoePreparationMode() && mcPermissions.getInstance().herbalism(player) && Herbalism.canBeGreenTerra(block))
    	{
    		Herbalism.greenTerraCheck(player, block);
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
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockDamage(BlockDamageEvent event) 
    {
    	if(event.isCancelled())
    		return;
    	Player player = event.getPlayer();
    	PlayerProfile PP = Users.getProfile(player);
    	ItemStack inhand = player.getItemInHand();
    	Block block = event.getBlock();
    	
    	Skills.monitorSkills(player, PP);

    	/*
    	 * ABILITY PREPARATION CHECKS
    	 */
   		if(PP.getHoePreparationMode() && Herbalism.canBeGreenTerra(block))
    		Herbalism.greenTerraCheck(player, block);
    	if(PP.getAxePreparationMode() && block.getTypeId() == 17)
    		WoodCutting.treeFellerCheck(player, block);
    	if(PP.getPickaxePreparationMode() && Mining.canBeSuperBroken(block))
    		Mining.superBreakerCheck(player, block);
    	if(PP.getShovelPreparationMode() && Excavation.canBeGigaDrillBroken(block))
    		Excavation.gigaDrillBreakerActivationCheck(player, block);
    	if(PP.getFistsPreparationMode() && (Excavation.canBeGigaDrillBroken(block) || block.getTypeId() == 78))
    		Unarmed.berserkActivationCheck(player);
    	
    	/*
    	 * TREE FELLAN STUFF
    	 */
    	if(LoadProperties.spoutEnabled && block.getTypeId() == 17 && Users.getProfile(player).getTreeFellerMode())
    		SpoutStuff.playSoundForPlayer(SoundEffect.FIZZ, player, block.getLocation());
    	
    	/*
    	 * GREEN TERRA STUFF
    	 */
    	if(PP.getGreenTerraMode() && mcPermissions.getInstance().herbalismAbility(player) && PP.getGreenTerraMode())
    	{
   			Herbalism.greenTerra(player, block);
   		}
    	
    	/*
    	 * GIGA DRILL BREAKER CHECKS
    	 */
    	if(PP.getGigaDrillBreakerMode() && m.blockBreakSimulate(block, player) 
    			&& Excavation.canBeGigaDrillBroken(block) && m.isShovel(inhand))
    	{
    		int x = 0;
    		
    		while(x < 3)
    		{
    			if(block.getData() != (byte)5)
    				Excavation.excavationProcCheck(block.getData(), block.getType(), block.getLocation(), player);
    			x++;
    		}
    		
    		Material mat = Material.getMaterial(block.getTypeId());
    		
    		if(block.getType() == Material.GRASS)
    			mat = Material.DIRT;
    		if(block.getType() == Material.CLAY)
    			mat = Material.CLAY_BALL;
    		if(block.getType() == Material.MYCEL)
    			mat = Material.DIRT;
    		
			byte type = block.getData();
			ItemStack item = new ItemStack(mat, 1, (byte)0, type);
			
			block.setType(Material.AIR);
			
			player.incrementStatistic(Statistic.MINE_BLOCK, event.getBlock().getType());
			
			if(LoadProperties.toolsLoseDurabilityFromAbilities)
	    	{
	    		if(inhand.getEnchantments().containsKey(Enchantment.DURABILITY))
	    			m.damageTool(player, (short) LoadProperties.abilityDurabilityLoss);
	    	}
			
			if(item.getType() == Material.CLAY_BALL)
			{
				m.mcDropItem(block.getLocation(), item);
				m.mcDropItem(block.getLocation(), item);
				m.mcDropItem(block.getLocation(), item);
				m.mcDropItem(block.getLocation(), item);
			} else
			{
				m.mcDropItem(block.getLocation(), item);
			}
			
			//Spout stuff
			if(LoadProperties.spoutEnabled)
				SpoutStuff.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
    	}
    	/*
    	 * BERSERK MODE CHECKS
    	 */
    	if(PP.getBerserkMode() 
    		&& m.blockBreakSimulate(block, player) 
    		&& player.getItemInHand().getTypeId() == 0 
    		&& (Excavation.canBeGigaDrillBroken(block) || block.getTypeId() == 78))
    	{
		   	Material mat = Material.getMaterial(block.getTypeId());
		   	
		   	if(block.getTypeId() == 2)
		   		mat = Material.DIRT;
		   	if(block.getTypeId() == 78)
		   		mat = Material.SNOW_BALL;
		   	if(block.getTypeId() == 82)
		   		mat = Material.CLAY_BALL;
		   	if(block.getTypeId() == 110)
		   		mat = Material.DIRT;
		   	
			byte type = block.getData();
			
			ItemStack item = new ItemStack(mat, 1, (byte)0, type);
			player.incrementStatistic(Statistic.MINE_BLOCK, event.getBlock().getType());
			
			block.setType(Material.AIR);
			
			if(item.getType() == Material.CLAY_BALL)
			{
				m.mcDropItem(block.getLocation(), item);
				m.mcDropItem(block.getLocation(), item);
				m.mcDropItem(block.getLocation(), item);
				m.mcDropItem(block.getLocation(), item);
			} else
			{
				m.mcDropItem(block.getLocation(), item);
			}
			
			if(LoadProperties.spoutEnabled)
				SpoutStuff.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
    	}
    	
    	/*
    	 * SUPER BREAKER CHECKS
    	 */
    	if(PP.getSuperBreakerMode() 
    			&& Mining.canBeSuperBroken(block)
    			&& m.blockBreakSimulate(block, player))
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
    	if(block.getTypeId() == 18 && mcPermissions.getInstance().woodcutting(player) && PP.getSkillLevel(SkillType.WOODCUTTING) >= 100 && m.isAxes(player.getItemInHand()) && m.blockBreakSimulate(block, player))
    	{	
    		
    		if(LoadProperties.toolsLoseDurabilityFromAbilities)
	    	{
	    		if(inhand.getEnchantments().containsKey(Enchantment.DURABILITY))
	    			m.damageTool(player, (short) LoadProperties.abilityDurabilityLoss);
	    	}
    		
    		if(Math.random() * 10 > 9)
    		{
    			ItemStack x = new ItemStack(Material.SAPLING, 1, (short)0, (byte)(block.getData()-8));
    			m.mcDropItem(block.getLocation(), x);
    		}
    		block.setType(Material.AIR);
    		player.incrementStatistic(Statistic.MINE_BLOCK, event.getBlock().getType());
    		if(LoadProperties.spoutEnabled)
    			SpoutStuff.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
    	}
    	if(block.getType() == Material.AIR && plugin.misc.blockWatchList.contains(block))
    	{
    		plugin.misc.blockWatchList.remove(block);
    	}
    }
    
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) 
    {
        Block blockFrom = event.getBlock();
        Block blockTo = event.getToBlock();
        if(m.shouldBeWatched(blockFrom) && blockFrom.getData() == (byte)5)
        {
        	blockTo.setData((byte)5);
        }
    }
}