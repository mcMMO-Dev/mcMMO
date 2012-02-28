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

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
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
import com.gmail.nossr50.events.FakeBlockBreakEvent;

public class mcBlockListener implements Listener 
{
    private final mcMMO plugin;

    public mcBlockListener(final mcMMO plugin) 
    {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) 
    {
    	//Setup some basic variables
    	Block block;
    	Player player = event.getPlayer();
    	
    	//When blocks are placed on snow this event reports the wrong block.
    	if (event.getBlockReplacedState() != null && event.getBlockReplacedState().getTypeId() == 78) 
    		block = event.getBlockAgainst();
    	else 
    		block = event.getBlock();
    	
    	int id = block.getTypeId();
    	Material mat = block.getType();
    	
    	//TNT placement checks - needed for Blast Mining
    	if(id == 46 && mcPermissions.getInstance().blastmining(player))
    	{
    		int skill = Users.getProfile(player).getSkillLevel(SkillType.MINING);
    		plugin.misc.tntTracker.put(block, skill);
    	}
    	
    	//Check if the blocks placed should be monitored so they do not give out XP in the future
    	if(m.shouldBeWatched(mat))
    	{	
    		//Only needed for blocks that use their block data (wood, pumpkins, etc.)
    	    boolean shouldBeChanged = true;
    	    
    		switch(mat)
    		{
    		case CACTUS:
    		case GLOWING_REDSTONE_ORE:
    		case JACK_O_LANTERN:
    		case LOG:
    		case PUMPKIN:
    		case REDSTONE_ORE:
    		case SUGAR_CANE_BLOCK:
    		case VINE:
    		    shouldBeChanged = false; //We don't want these added to changeQueue
    			plugin.misc.blockWatchList.add(block);
    			break;
    		case BROWN_MUSHROOM:
    		case RED_MUSHROOM:
    		case RED_ROSE:
    		case YELLOW_FLOWER:
    		case WATER_LILY:
    			plugin.fastChangeQueue.push(block);
    			break;
    		}
    		
    		if(shouldBeChanged)
    		    plugin.changeQueue.push(block); 			
    	}
    	
    	if(id == LoadProperties.anvilID && LoadProperties.anvilmessages)
    	{
    		PlayerProfile PP = Users.getProfile(player);
    		if(!PP.getPlacedAnvil())
    		{
    			if(LoadProperties.spoutEnabled)
    			{
    				SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
    				if(sPlayer.isSpoutCraftEnabled())
    					sPlayer.sendNotification("[mcMMO] Anvil Placed", "Right click to repair!", Material.IRON_BLOCK);
	    		}
	    		else
	    			event.getPlayer().sendMessage(mcLocale.getString("mcBlockListener.PlacedAnvil")); //$NON-NLS-1$
    			
    			PP.togglePlacedAnvil();
    		}
    	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) 
    {
    	Player player = event.getPlayer();
    	PlayerProfile PP = Users.getProfile(player);
    	Block block = event.getBlock();
    	int id = block.getTypeId();
    	ItemStack inhand = player.getItemInHand();
    	
    	if(event instanceof FakeBlockBreakEvent) 
    		return;
    	
    	//Reset player spawn back to world spawn if their bed is destroyed.
    	if(block.getType().equals(Material.BED_BLOCK) && LoadProperties.enableMySpawn && PP.getMySpawn(player) != null)
    	{
    		if(PP.getMySpawn(player).getBlock().getType() != Material.BED_BLOCK)
    		{
	    		double x = Bukkit.getServer().getWorlds().get(0).getSpawnLocation().getX();
	    		double y = Bukkit.getServer().getWorlds().get(0).getSpawnLocation().getY();
	    		double z = Bukkit.getServer().getWorlds().get(0).getSpawnLocation().getZ();
	    		String worldname = Bukkit.getServer().getWorlds().get(0).getName();
	    		PP.setMySpawn(x, y, z, worldname);
    		}
    	}
    	/*
    	 * HERBALISM
    	 */
    	
    	//Green Terra
   		if(PP.getHoePreparationMode() && mcPermissions.getInstance().herbalismAbility(player) && ((id == 59 && block.getData() == (byte) 0x07) || Herbalism.canBeGreenTerra(block)))
   			Skills.abilityCheck(player, SkillType.HERBALISM);
   		
   		//Wheat && Triple drops
   		if(PP.getGreenTerraMode() && Herbalism.canBeGreenTerra(block))
   			Herbalism.herbalismProcCheck(block, player, event, plugin);
    	
    	if(mcPermissions.getInstance().herbalism(player) && block.getData() != (byte) 5)
			Herbalism.herbalismProcCheck(block, player, event, plugin);
    	
    	/*
    	 * MINING
    	 */
   		
    	//TNT removal checks - needed for Blast Mining
    	if(id == 46 && inhand.getTypeId() != 259 && mcPermissions.getInstance().blastmining(player))
    		plugin.misc.tntTracker.remove(block);
    	
    	if(mcPermissions.getInstance().mining(player))
    	{
    		if(LoadProperties.miningrequirespickaxe && m.isMiningPick(inhand))
    			Mining.miningBlockCheck(player, block, plugin);
    		else if(!LoadProperties.miningrequirespickaxe)
    			Mining.miningBlockCheck(player, block, plugin);
    	}
    	
    	/*
   		 * WOOD CUTTING
   		 */
    	
   		if(mcPermissions.getInstance().woodcutting(player) && id == 17)
   		{
   			if(LoadProperties.woodcuttingrequiresaxe && m.isAxes(inhand))
				WoodCutting.woodcuttingBlockCheck(player, block, plugin);
   			else if(!LoadProperties.woodcuttingrequiresaxe)
    			WoodCutting.woodcuttingBlockCheck(player, block, plugin);
   			
   			if(PP.getTreeFellerMode())
   			    WoodCutting.treeFeller(event, plugin);
    	}
   		
    	/*
    	 * EXCAVATION
    	 */
    	if(Excavation.canBeGigaDrillBroken(block) && mcPermissions.getInstance().excavation(player) && block.getData() != (byte) 5)
    	{
    		if(LoadProperties.excavationRequiresShovel && m.isShovel(inhand))
    			Excavation.excavationProcCheck(block, player);
    		else if(!LoadProperties.excavationRequiresShovel)
    			Excavation.excavationProcCheck(block, player);
    	}
    	
    	//Change the byte back when broken
    	if(block.getData() == 5 && m.shouldBeWatched(block.getType()))
    	{
    		block.setData((byte) 0);
    		if(plugin.misc.blockWatchList.contains(block))
    		{
    			plugin.misc.blockWatchList.remove(block);
    		}
    	}
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDamage(BlockDamageEvent event) 
    {
    	Player player = event.getPlayer();
    	PlayerProfile PP = Users.getProfile(player);
    	ItemStack inhand = player.getItemInHand();
    	Block block = event.getBlock();
    	int id = block.getTypeId();

    	/*
    	 * ABILITY PREPARATION CHECKS
    	 */
    	if(m.abilityBlockCheck(block))
    	{
	   		if(PP.getHoePreparationMode() && Herbalism.canBeGreenTerra(block))
	   			Skills.abilityCheck(player, SkillType.HERBALISM);
	    	if(PP.getAxePreparationMode() && id == 17 && mcPermissions.getInstance().woodCuttingAbility(player))
	    		Skills.abilityCheck(player, SkillType.WOODCUTTING);
	    	if(PP.getPickaxePreparationMode() && Mining.canBeSuperBroken(block))
	    		Skills.abilityCheck(player, SkillType.MINING);
	    	if(PP.getShovelPreparationMode() && Excavation.canBeGigaDrillBroken(block))
	    		Skills.abilityCheck(player, SkillType.EXCAVATION);
    	}
    	
    	if(PP.getFistsPreparationMode() && (Excavation.canBeGigaDrillBroken(block) || id == 78))
    		Skills.abilityCheck(player, SkillType.UNARMED);
    	
    	/*
    	 * TREE FELLER STUFF
    	 */
    	if(LoadProperties.spoutEnabled && id == 17 && PP.getTreeFellerMode())
    		SpoutStuff.playSoundForPlayer(SoundEffect.FIZZ, player, block.getLocation());
    	
    	/*
    	 * GREEN TERRA STUFF
    	 */
    	if(PP.getGreenTerraMode() && mcPermissions.getInstance().herbalismAbility(player))
   			Herbalism.greenTerra(player, block);
    	
    	/*
    	 * GIGA DRILL BREAKER CHECKS
    	 */
    	if(PP.getGigaDrillBreakerMode() && Excavation.canBeGigaDrillBroken(block) && m.blockBreakSimulate(block, player, true) && mcPermissions.getInstance().excavationAbility(player))
    	{	
    		if(LoadProperties.excavationRequiresShovel && m.isShovel(inhand))
    		{
    				event.setInstaBreak(true);
    				Excavation.gigaDrillBreaker(player, block);
    		} 
    		else if(!LoadProperties.excavationRequiresShovel){
    		    
    		    if(LoadProperties.toolsLoseDurabilityFromAbilities)
    	        {
    	            if(!inhand.containsEnchantment(Enchantment.DURABILITY))
    	            {
    	                short durability = inhand.getDurability();
    	                durability += (LoadProperties.abilityDurabilityLoss);
    	                inhand.setDurability(durability);
    	            }
    	        }
    		    
    			event.setInstaBreak(true);
    			Excavation.gigaDrillBreaker(player, block);
    		}
    	}
    	/*
    	 * BERSERK MODE CHECKS
    	 */
    	if(PP.getBerserkMode() 
    		&& m.blockBreakSimulate(block, player, true) 
    		&& player.getItemInHand().getTypeId() == 0 
    		&& (Excavation.canBeGigaDrillBroken(block) || id == 78)
    		&& mcPermissions.getInstance().unarmedAbility(player))
    	{
    		event.setInstaBreak(true);
			
    		if(LoadProperties.spoutEnabled)
    			SpoutStuff.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
    	}
    	
    	/*
    	 * SUPER BREAKER CHECKS
    	 */
    	if(PP.getSuperBreakerMode() 
    		&& Mining.canBeSuperBroken(block)
    		&& m.blockBreakSimulate(block, player, true)
    		&& mcPermissions.getInstance().miningAbility(player))
    	{
    		if(LoadProperties.miningrequirespickaxe)
    		{
    			if(m.isMiningPick(inhand)){
    			    
    				if(LoadProperties.toolsLoseDurabilityFromAbilities)
        	        {
        	            if(!inhand.containsEnchantment(Enchantment.DURABILITY))
        	            {
        	                short durability = inhand.getDurability();
        	                durability += (LoadProperties.abilityDurabilityLoss);
        	                inhand.setDurability(durability);
        	            }
        	        }
    			    
    				event.setInstaBreak(true);
    				Mining.SuperBreakerBlockCheck(player, block, plugin);
    			}
    		} else {
    			event.setInstaBreak(true);
    			Mining.SuperBreakerBlockCheck(player, block, plugin);
    		}
    	}
    	
    	/*
    	 * LEAF BLOWER CHECKS
    	 */
    	if(id == 18 
    		&& mcPermissions.getInstance().woodCuttingAbility(player) 
    		&& PP.getSkillLevel(SkillType.WOODCUTTING) >= 100 
    		&& m.blockBreakSimulate(block, player, true))
    	{	
    		if(LoadProperties.woodcuttingrequiresaxe)
    		{
    			if(m.isAxes(inhand)){
    				event.setInstaBreak(true);
    				WoodCutting.leafBlower(player, block);
    			}
    		}
    		else{
    			if(inhand.getTypeId() != 359){
	    			event.setInstaBreak(true);
	    			WoodCutting.leafBlower(player, block);
    			}
    		}
    		
    	}
    }
    
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) 
    {
        Block blockFrom = event.getBlock();
        Block blockTo = event.getToBlock();
        if(m.shouldBeWatched(blockFrom.getType()) && blockFrom.getData() == (byte)5)
        {
        	blockTo.setData((byte)5);
        }
    }    
}
