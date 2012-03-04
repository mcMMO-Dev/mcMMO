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
package com.gmail.nossr50.skills;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.sound.SoundEffect;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerAnimationEvent;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;


public class Mining 
{	

	public static void blockProcSimulate(Block block, Player player)
	{
    	Location loc = block.getLocation();
    	Material type = block.getType();
		ItemStack item = new ItemStack(type, 1);
		
		//Drop natural block with Silk Touch
		if(player.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)){
			m.mcDropItem(loc, item);
			return;
		}
			
		switch (type){
			case GLOWSTONE:
				item = new ItemStack(Material.GLOWSTONE_DUST, 1);
				m.mcDropItems(loc, item, 2);
				m.mcRandomDropItems(loc, item, 50, 2);
				break;
			case GLOWING_REDSTONE_ORE:
			case REDSTONE_ORE:
				item = new ItemStack(Material.REDSTONE, 1);
				m.mcDropItems(loc, item, 4);
				m.mcRandomDropItem(loc, item, 50);
				break;
			case LAPIS_ORE:
				item = new ItemStack(Material.INK_SACK, 1, (byte)0, (byte)0x4);
				m.mcDropItems(loc, item, 4);
				m.mcRandomDropItems(loc, item, 50, 4);
				break;
			case DIAMOND_ORE:
				item = new ItemStack(Material.DIAMOND, 1);
				m.mcDropItem(loc, item);
				break;
			case STONE:
				item = new ItemStack(Material.COBBLESTONE, 1);
				m.mcDropItem(loc, item);
				break;
			case COAL_ORE:
				item = new ItemStack(Material.COAL, 1, (byte)0, (byte)0x0);
				m.mcDropItem(loc, item);
				break;
			default:
				m.mcDropItem(loc, item);
				break;
		}
    }

    public static void blockProcCheck(Block block, Player player)
    {
    	int skillLevel = Users.getProfile(player).getSkillLevel(SkillType.MINING);

    	if(skillLevel > 1000 || (Math.random() * 1000 <= skillLevel)) 
	    	blockProcSimulate(block, player);
	}
    
    public static void miningBlockCheck(Player player, Block block, mcMMO plugin)
    {
    	PlayerProfile PP = Users.getProfile(player);
    	if(plugin.misc.blockWatchList.contains(block) || block.getData() == (byte) 5)
    		return;
    	int xp = 0;
		Material type = block.getType();
		
		switch (type) {
			case STONE: 
				xp += LoadProperties.mstone;
				break;
			case SANDSTONE:
				xp += LoadProperties.msandstone;
				break;
			case OBSIDIAN:
				xp += LoadProperties.mobsidian;
				break;
			case NETHERRACK:
				xp += LoadProperties.mnetherrack;
				break;
			case GLOWSTONE:
				xp += LoadProperties.mglowstone;
				break;
			case COAL_ORE:
				xp += LoadProperties.mcoal;
				break;
			case GOLD_ORE:
				xp += LoadProperties.mgold;
				break;
			case DIAMOND_ORE:
				xp += LoadProperties.mdiamond;
				break;
			case IRON_ORE:
				xp += LoadProperties.miron;
				break;
			case GLOWING_REDSTONE_ORE:
			case REDSTONE_ORE:
				xp += LoadProperties.mredstone;
				break;
			case LAPIS_ORE:
				xp += LoadProperties.mlapis;
				break;
			case ENDER_STONE:
				xp += LoadProperties.mendstone;
				break;
			case MOSSY_COBBLESTONE:
				xp += LoadProperties.mmossstone;
				break;
		}
		if(canBeSuperBroken(block))
			blockProcCheck(block, player);
    	PP.addXP(SkillType.MINING, xp, player);
    	Skills.XpCheckSkill(SkillType.MINING, player);
    }
    
    /*
     * Handling SuperBreaker stuff
     */
    public static Boolean canBeSuperBroken(Block block)
    {
    	switch(block.getType()){
    	case COAL_ORE:
    	case DIAMOND_ORE:
    	case ENDER_STONE:
    	case GLOWING_REDSTONE_ORE:
    	case GLOWSTONE:
    	case GOLD_ORE:
    	case IRON_ORE:
    	case LAPIS_ORE:
    	case MOSSY_COBBLESTONE:
    	case NETHERRACK:
    	case OBSIDIAN:
    	case REDSTONE_ORE:
    	case SANDSTONE:
    	case STONE:
    		return true;
    	}
    	return false;
    }
    
    public static void SuperBreakerBlockCheck(Player player, Block block, mcMMO plugin)
    {
    	PlayerProfile PP = Users.getProfile(player);
    	Material type = block.getType();
    	
    	//Obsidian needs to do more damage than normal
    	if(type != Material.OBSIDIAN)
    	    Skills.abilityDurabilityLoss(player.getItemInHand(), LoadProperties.abilityDurabilityLoss);
    	else
    	    Skills.abilityDurabilityLoss(player.getItemInHand(), LoadProperties.abilityDurabilityLoss*5);
    	
    	//Pre-processing
    	int xp = 0;
		PlayerAnimationEvent armswing = new PlayerAnimationEvent(player);
		
    	if(type.equals(Material.STONE) && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
    		xp += LoadProperties.mstone;
    		blockProcCheck(block, player);
    		blockProcCheck(block, player);
    	}
    	else if(type.equals(Material.SANDSTONE) && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
   			xp += LoadProperties.msandstone;
   			blockProcCheck(block, player);
   			blockProcCheck(block, player);
    	}
    	else if(type.equals(Material.NETHERRACK) && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
   			xp += LoadProperties.mnetherrack;
   			blockProcCheck(block, player);
   			blockProcCheck(block, player);
    	}
    	else if(type.equals(Material.GLOWSTONE) && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
    		xp += LoadProperties.mglowstone;
    		blockProcCheck(block, player);
    		blockProcCheck(block, player); 
    	}
    	else if(type.equals(Material.COAL_ORE) && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
    		xp += LoadProperties.mcoal;
        	blockProcCheck(block, player);
        	blockProcCheck(block, player);
    	}
    	else if(type.equals(Material.GOLD_ORE) && m.getTier(player) >= 3 && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
    		xp += LoadProperties.mgold;
        	blockProcCheck(block, player);
        	blockProcCheck(block, player);
    	}
    	else if(type.equals(Material.OBSIDIAN) && m.getTier(player) >= 4 && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
    		xp += LoadProperties.mobsidian;
        	blockProcCheck(block, player);
        	blockProcCheck(block, player);
    	}
    	else if(type.equals(Material.DIAMOND_ORE) && m.getTier(player) >= 3 && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
    		xp += LoadProperties.mdiamond;
        	blockProcCheck(block, player);
        	blockProcCheck(block, player);
    	}
    	else if(type.equals(Material.IRON_ORE) && m.getTier(player) >= 2 && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
    		xp += LoadProperties.miron;
        	blockProcCheck(block, player);
        	blockProcCheck(block, player);
    	}
    	else if((type.equals(Material.GLOWING_REDSTONE_ORE) || type.equals(Material.REDSTONE_ORE)) && m.getTier(player) >= 3 && !plugin.misc.blockWatchList.contains(block))
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
    		xp += LoadProperties.mredstone;
        	blockProcCheck(block, player);
        	blockProcCheck(block, player);
    	}
    	else if(type.equals(Material.LAPIS_ORE) && m.getTier(player) >= 3 && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
   			xp += LoadProperties.mlapis;
       		blockProcCheck(block, player);
      		blockProcCheck(block, player);
    	}
    	else if(type.equals(Material.ENDER_STONE) && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
    		xp += LoadProperties.mendstone;
        	blockProcCheck(block, player);
        	blockProcCheck(block, player);
    	}
    	else if(type.equals(Material.MOSSY_COBBLESTONE) && block.getData() != (byte) 5)
    	{
    		Bukkit.getPluginManager().callEvent(armswing);
   			xp += LoadProperties.mmossstone;
       		blockProcCheck(block, player);
       		blockProcCheck(block, player);
    	}
    	if(!plugin.misc.blockWatchList.contains(block) && block.getData() != (byte) 5)
    		PP.addXP(SkillType.MINING, xp, player);
    	if(LoadProperties.spoutEnabled)
    		SpoutStuff.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
    	Skills.XpCheckSkill(SkillType.MINING, player);
    }
}
