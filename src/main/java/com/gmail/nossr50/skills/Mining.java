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

	public static void miningDrops(Block block)
	{
		Location loc = block.getLocation();
    	Material type = block.getType();
		ItemStack item = new ItemStack(type, 1);
		
		switch (type)
		{
		case COAL_ORE:
			item = new ItemStack(Material.COAL, 1, (byte)0, (byte)0x0);
			m.mcDropItem(loc, item);
			break;
		case DIAMOND_ORE:
			item = new ItemStack(Material.DIAMOND, 1);
			m.mcDropItem(loc, item);
			break;
		case GLOWING_REDSTONE_ORE:
		case REDSTONE_ORE:
			item = new ItemStack(Material.REDSTONE, 1);
			m.mcDropItems(loc, item, 4);
			m.mcRandomDropItem(loc, item, 50);
			break;
		case GLOWSTONE:
			item = new ItemStack(Material.GLOWSTONE_DUST, 1);
			m.mcDropItems(loc, item, 2);
			m.mcRandomDropItems(loc, item, 50, 2);
			break;
		case LAPIS_ORE:
			item = new ItemStack(Material.INK_SACK, 1, (byte)0, (byte)0x4);
			m.mcDropItems(loc, item, 4);
			m.mcRandomDropItems(loc, item, 50, 4);
			break;
		case STONE:
			item = new ItemStack(Material.COBBLESTONE, 1);
			m.mcDropItem(loc, item);
			break;
		default:
			m.mcDropItem(loc, item);
			break;
		}
	}
	
	public static void miningXP(Player player, Block block)
	{
		PlayerProfile PP = Users.getProfile(player);
		Material type = block.getType();
		int xp = 0;
		
		switch (type)
		{
		case COAL_ORE:
			xp += LoadProperties.mcoal;
			break;
		case DIAMOND_ORE:
			xp += LoadProperties.mdiamond;
			break;
		case ENDER_STONE:
			xp += LoadProperties.mendstone;
			break;
		case GLOWING_REDSTONE_ORE:
		case REDSTONE_ORE:
			xp += LoadProperties.mredstone;
			break;
		case GLOWSTONE:
			xp += LoadProperties.mglowstone;
			break;
		case GOLD_ORE:
			xp += LoadProperties.mgold;
			break;
		case IRON_ORE:
			xp += LoadProperties.miron;
			break;
		case LAPIS_ORE:
			xp += LoadProperties.mlapis;
			break;
		case MOSSY_COBBLESTONE:
			xp += LoadProperties.mmossstone;
			break;
		case NETHERRACK:
			xp += LoadProperties.mnetherrack;
			break;
		case OBSIDIAN:
			xp += LoadProperties.mobsidian;
			break;
		case SANDSTONE:
			xp += LoadProperties.msandstone;
			break;
		case STONE:
			xp += LoadProperties.mstone;
			break;
		}
		
		PP.addXP(SkillType.MINING, xp, player);
    	Skills.XpCheckSkill(SkillType.MINING, player);
	}

	public static void blockProcSimulate(Block block, Player player)
	{
		//Drop natural block with Silk Touch
		if(player.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH))
			m.mcDropItem(block.getLocation(), new ItemStack(block.getType(), 1));
		else
			miningDrops(block);
    }

    public static void blockProcCheck(Block block, Player player)
    {
    	int skillLevel = Users.getProfile(player).getSkillLevel(SkillType.MINING);

    	if(skillLevel > 1000 || (Math.random() * 1000 <= skillLevel))
	    	blockProcSimulate(block, player);
	}
    
    public static void miningBlockCheck(Player player, Block block, mcMMO plugin)
    {
    	if(plugin.misc.blockWatchList.contains(block) || block.getData() == (byte) 5)
    		return;
    	miningXP(player, block);
		if(canBeSuperBroken(block))
			blockProcCheck(block, player);
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
    	Material type = block.getType();
    	int tier = m.getTier(player.getItemInHand());
    	int durabilityLoss = LoadProperties.abilityDurabilityLoss;
    	PlayerAnimationEvent armswing = new PlayerAnimationEvent(player);
		
		switch(type)
		{
		case OBSIDIAN:
			if(tier < 4)
				return;
			durabilityLoss = durabilityLoss * 5; //Obsidian needs to do more damage than normal
		case DIAMOND_ORE:
		case GLOWING_REDSTONE_ORE:
		case GOLD_ORE:
		case LAPIS_ORE:
		case REDSTONE_ORE:
			if(tier < 3)
				return;
		case IRON_ORE:
			if(tier < 2)
				return;
		case COAL_ORE:
		case ENDER_STONE:
		case GLOWSTONE:
		case MOSSY_COBBLESTONE:
		case NETHERRACK:
		case SANDSTONE:
		case STONE:
			if((block.getData() == (byte) 5) || plugin.misc.blockWatchList.contains(block))
				return;
			Bukkit.getPluginManager().callEvent(armswing);
			Skills.abilityDurabilityLoss(player.getItemInHand(), durabilityLoss);
			blockProcCheck(block, player);
    		blockProcCheck(block, player);
    		if(!plugin.misc.blockWatchList.contains(block) && block.getData() != (byte) 5)
        		miningXP(player, block);
        	if(LoadProperties.spoutEnabled)
        		SpoutStuff.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
		}
    }
}
