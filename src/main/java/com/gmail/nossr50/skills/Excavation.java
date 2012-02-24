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

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerAnimationEvent;

import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;

import org.getspout.spoutapi.sound.SoundEffect;

public class Excavation
{
	public static void gigaDrillBreakerActivationCheck(Player player)
	{
		PlayerProfile PP = Users.getProfile(player);
		if(m.isShovel(player.getItemInHand()))
		{
	    	if(PP.getShovelPreparationMode())
    			PP.setShovelPreparationMode(false);
	    	
	    	int ticks = 2;
	    	int x = PP.getSkillLevel(SkillType.EXCAVATION);
    		while(x >= 50)
    		{
    			x-=50;
    			ticks++;
    		}
    		
	    	if(!PP.getGigaDrillBreakerMode() && PP.getSkillDATS(AbilityType.GIGA_DRILL_BREAKER) < System.currentTimeMillis())
	    	{
	    		player.sendMessage(mcLocale.getString("Skills.GigaDrillBreakerOn"));
	    		for(Player y : player.getWorld().getPlayers())
	    		{
	    			if(y != null && y != player && m.getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(mcLocale.getString("Skills.GigaDrillBreakerPlayer", new Object[] {player.getName()}));
	    		}
	    		PP.setSkillDATS(AbilityType.GIGA_DRILL_BREAKER, System.currentTimeMillis()+(ticks*1000));
	    		PP.setGigaDrillBreakerMode(true);
	    	}
	    	
	    }
	}
	public static boolean canBeGigaDrillBroken(Block block)
	{
		Material t = block.getType();
		return t == Material.DIRT || t == Material.GRASS || t == Material.SAND || t == Material.GRAVEL || t == Material.CLAY || t == Material.MYCEL || t == Material.SOUL_SAND;
	}
	public static void excavationProcCheck(Block block, Player player)
	{
		Material type = block.getType();
		Location loc = block.getLocation();
		
		PlayerProfile PP = Users.getProfile(player);
		int skillLevel = PP.getSkillLevel(SkillType.EXCAVATION);
    	ArrayList<ItemStack> is = new ArrayList<ItemStack>();
    	int xp = LoadProperties.mbase;
    	
    	switch(type)
    	{
    	case DIRT:
    		for(ExcavationTreasure treasure : LoadProperties.excavationFromDirt)
    		{
    			if(skillLevel >= treasure.getDropLevel())
    			{
    				if(Math.random() * 100 > (100.00 - treasure.getDropChance()))
    				{
    					xp += treasure.getXp();
    					is.add(treasure.getDrop());
    				}
    			}
    		}
    		break;
    	case GRASS:
    		for(ExcavationTreasure treasure : LoadProperties.excavationFromGrass)
    		{
    			if(skillLevel >= treasure.getDropLevel())
    			{
    				if(Math.random() * 100 > (100.00 - treasure.getDropChance()))
    				{
    					xp += treasure.getXp();
    					is.add(treasure.getDrop());
    				}
    			}
    		}
    		break;
    	case SAND:
    		for(ExcavationTreasure treasure : LoadProperties.excavationFromSand)
    		{
    			if(skillLevel >= treasure.getDropLevel())
    			{
    				if(Math.random() * 100 > (100.00 - treasure.getDropChance()))
    				{
    					xp += treasure.getXp();
    					is.add(treasure.getDrop());
    				}
    			}
    		}
    		break;
    	case GRAVEL:
    		for(ExcavationTreasure treasure : LoadProperties.excavationFromGravel)
    		{
    			if(skillLevel >= treasure.getDropLevel())
    			{
    				if(Math.random() * 100 > (100.00 - treasure.getDropChance()))
    				{
    					xp += treasure.getXp();
    					is.add(treasure.getDrop());
    				}
    			}
    		}
    		break;
    	case CLAY:
    		for(ExcavationTreasure treasure : LoadProperties.excavationFromClay)
    		{
    			if(skillLevel >= treasure.getDropLevel())
    			{
    				if(Math.random() * 100 > (100.00 - treasure.getDropChance()))
    				{
    					xp += treasure.getXp();
    					is.add(treasure.getDrop());
    				}
    			}
    		}
    		break;
    	case MYCEL:
    		for(ExcavationTreasure treasure : LoadProperties.excavationFromMycel)
    		{
    			if(skillLevel >= treasure.getDropLevel())
    			{
    				if(Math.random() * 100 > (100.00 - treasure.getDropChance()))
    				{
    					xp += treasure.getXp();
    					is.add(treasure.getDrop());
    				}
    			}
    		}
    		break;
    	case SOUL_SAND:
    		for(ExcavationTreasure treasure : LoadProperties.excavationFromSoulSand)
    		{
    			if(skillLevel >= treasure.getDropLevel())
    			{
    				if(Math.random() * 100 > (100.00 - treasure.getDropChance()))
    				{
    					xp += treasure.getXp();
    					is.add(treasure.getDrop());
    				}
    			}
    		}
    		break;
    	}
    	
    	//Drop items
    	for(ItemStack x : is)
    	{
    		if(x != null)
    			m.mcDropItem(loc, x);
    	}
    	
    	//Handle XP related tasks
    	PP.addXP(SkillType.EXCAVATION, xp, player);
    	Skills.XpCheckSkill(SkillType.EXCAVATION, player);
    }
	
	public static void gigaDrillBreaker(Player player, Block block)
	{
		if(LoadProperties.toolsLoseDurabilityFromAbilities)
    	{
			if(!player.getItemInHand().containsEnchantment(Enchantment.DURABILITY))
			{
				short durability = player.getItemInHand().getDurability();
				durability += LoadProperties.abilityDurabilityLoss;
				player.getItemInHand().setDurability(durability);
			}
    	}
		
		if(block.getData() != (byte)5)
		{
			PlayerAnimationEvent armswing = new PlayerAnimationEvent(player);
			Bukkit.getPluginManager().callEvent(armswing);
			Excavation.excavationProcCheck(block, player);	
			Excavation.excavationProcCheck(block, player);
			Excavation.excavationProcCheck(block, player);
		}
		
		if(LoadProperties.spoutEnabled)
			SpoutStuff.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
	}
}