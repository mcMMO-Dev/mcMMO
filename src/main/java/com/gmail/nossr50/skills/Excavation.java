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
import org.getspout.spoutapi.sound.SoundEffect;

public class Excavation
{
	public static void gigaDrillBreakerActivationCheck(Player player, Block block)
	{
		PlayerProfile PP = Users.getProfile(player);
		if(m.isShovel(player.getItemInHand()))
		{
	    	if(block != null)
	    	{
		    	if(!m.abilityBlockCheck(block))
		    		return;
	    	}
	    	if(PP.getShovelPreparationMode())
	    	{
    			PP.setShovelPreparationMode(false);
    		}
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
	    		PP.setSkillATS(AbilityType.GIGA_DRILL_BREAKER, ticks);
	    		PP.setGigaDrillBreakerMode(true);
	    	}
	    	
	    }
	}
	public static boolean canBeGigaDrillBroken(Block block)
	{
		Material t = block.getType();
		return t == Material.DIRT || t == Material.GRASS || t == Material.SAND || t == Material.GRAVEL || t == Material.CLAY || t == Material.MYCEL || t == Material.SOUL_SAND;
	}
	public static void excavationProcCheck(Material type, Location loc, Player player)
	{
		if(LoadProperties.excavationRequiresShovel && !m.isShovel(player.getItemInHand()))
			return;
		
		PlayerProfile PP = Users.getProfile(player);
		int skillLevel = PP.getSkillLevel(SkillType.EXCAVATION);
    	ArrayList<ItemStack> is = new ArrayList<ItemStack>();
    	int xp = 0;
    	
    	switch(type)
    	{
    	case GRASS:
    		if(skillLevel >= 250)
    		{
	    		//CHANCE TO GET EGGS
	    		if(LoadProperties.eggs && Math.random() * 100 > 99)
	    		{
	    			xp+= LoadProperties.meggs;
					is.add(new ItemStack(Material.EGG, 1, (byte)0, (byte)0));
	    		}
	    		//CHANCE TO GET APPLES
	    		if(LoadProperties.apples && Math.random() * 100 > 99)
	    		{
	    			xp+= LoadProperties.mapple;
					is.add(new ItemStack(Material.APPLE, 1, (byte)0, (byte)0));
	    		}
    		}
    		break;
    	case GRAVEL:
    		//CHANCE TO GET NETHERRACK
    		if(LoadProperties.netherrack && skillLevel >= 850 && Math.random() * 200 > 199)
    		{
    			xp+= LoadProperties.mnetherrack;
				is.add(new ItemStack(Material.NETHERRACK, 1, (byte)0, (byte)0));
				
    		}
    		//CHANCE TO GET SULPHUR
    		if(LoadProperties.sulphur && skillLevel >= 75 && Math.random() * 10 > 9)
    		{
    			xp+= LoadProperties.msulphur;
				is.add(new ItemStack(Material.SULPHUR, 1, (byte)0, (byte)0));
    		}
    		//CHANCE TO GET BONES
    		if(LoadProperties.bones && skillLevel >= 175 && Math.random() * 10 > 9)
    		{
        		xp+= LoadProperties.mbones;
    			is.add(new ItemStack(Material.BONE, 1, (byte)0, (byte)0));
        	}
    		break;
    	case SAND:
    		//CHANCE TO GET GLOWSTONE
    		if(LoadProperties.glowstone && skillLevel >= 50 && Math.random() * 100 > 95)
    		{
    			xp+= LoadProperties.mglowstone2;
				is.add(new ItemStack(Material.GLOWSTONE_DUST, 1, (byte)0, (byte)0));
				
    		}
    		//CHANCE TO GET SOUL SAND
    		if(LoadProperties.slowsand && skillLevel >= 650 && Math.random() * 200 > 199)
    		{
    			xp+= LoadProperties.mslowsand;
				is.add(new ItemStack(Material.SOUL_SAND, 1, (byte)0, (byte)0));
    		}
    		break;
    	case CLAY:
    		//CHANCE TO GET SLIMEBALLS
    		if(LoadProperties.slimeballs && skillLevel >= 50 && Math.random() * 20 > 19)
    		{
   				xp+= LoadProperties.mslimeballs;
   				is.add(new ItemStack(Material.SLIME_BALL, 1, (byte)0, (byte)0));
      		}
    		//CHANCE TO GET STRING
    		if(LoadProperties.string && skillLevel >= 250 && Math.random() * 20 > 19)
    		{
    			xp+= LoadProperties.mstring;
    			is.add(new ItemStack(Material.STRING, 1, (byte)0, (byte)0));
    		}
    		if(skillLevel >= 500)
    		{
    			//CHANCE TO GET CLOCK
        		if(LoadProperties.watch && Math.random() * 100 > 99)
        		{
        			xp+= LoadProperties.mwatch;
        			is.add(new ItemStack(Material.WATCH, 1, (byte)0));
        		
        		}
        		//CHANCE TO GET BUCKET
        		if(LoadProperties.bucket && Math.random() * 100 > 99)
        		{
       				xp+= LoadProperties.mbucket;
       				is.add(new ItemStack(Material.BUCKET, 1, (byte)0, (byte)0));
        		}
    		}
    		//CHANCE TO GET COBWEB
    		if(LoadProperties.web && skillLevel >= 750 && Math.random() * 20 > 19)
    		{
   				xp+= LoadProperties.mweb;
   				is.add(new ItemStack(Material.WEB, 1, (byte)0, (byte)0));
    		}
    		break;
    	}
    	
    	//ALL MATERIALS
    	if(type == Material.GRASS || type == Material.DIRT || type == Material.GRAVEL || type == Material.SAND || type == Material.CLAY || type == Material.MYCEL || type == Material.SOUL_SAND)
    	{
    		xp+= LoadProperties.mbase;
			//CHANCE TO GET CAKE
			if(LoadProperties.cake && skillLevel >= 750 && Math.random() * 2000 > 1999)
			{
				xp+= LoadProperties.mcake;
				is.add(new ItemStack(Material.CAKE, 1, (byte)0, (byte)0));
			}
    		if(skillLevel >= 350)
    		{
    			//CHANCE TO GET DIAMOND
    			if(LoadProperties.diamond && Math.random() * 750 > 749)
    			{
    				xp+= LoadProperties.mdiamond2;
        			is.add(new ItemStack(Material.DIAMOND, 1, (byte)0, (byte)0));
    			}
    			//CHANCE TO GET GREEN MUSIC
    			if(LoadProperties.music && Math.random() * 2000 > 1999)
    			{
    				xp+= LoadProperties.mmusic;
    				is.add(new ItemStack(Material.GREEN_RECORD, 1, (byte)0, (byte)0));
    			}
    		}
			//CHANCE TO GET YELLOW MUSIC
			if(LoadProperties.music && skillLevel >= 250 && Math.random() * 2000 > 1999)
			{
				xp+= LoadProperties.mmusic;
				is.add(new ItemStack(Material.GOLD_RECORD, 1, (byte)0, (byte)0));
			}
    	}
    	
    	//GRASS OR DIRT OR MYCEL
    	if(type == Material.DIRT || type == Material.GRASS || type == Material.MYCEL)
    	{
    		//CHANCE FOR COCOA BEANS
    		if(LoadProperties.cocoabeans && skillLevel >= 50 && Math.random() * 75 > 74)
    		{
    			xp+= LoadProperties.mcocoa;
				is.add(new ItemStack(Material.getMaterial(351), 1, (byte)0, (byte)3));
    		}
    		//CHANCE FOR SHROOMS
    		if(LoadProperties.mushrooms && skillLevel >= 500 && Math.random() * 200 > 199)
    		{
    			xp+= LoadProperties.mmushroom2;
    			switch((int)(Math.random() * 2))
    			{
    			case 0:
    				is.add(new ItemStack(Material.BROWN_MUSHROOM, 1, (byte)0, (byte)0));
    				break;
    			case 1:
    				is.add(new ItemStack(Material.RED_MUSHROOM, 1, (byte)0, (byte)0));
    				break;
    			}
				
    		}
    		//CHANCE TO GET GLOWSTONE
    		if(LoadProperties.glowstone && skillLevel >= 25 && Math.random() * 100 > 95)
    		{
    			xp+= LoadProperties.mglowstone2;
				is.add(new ItemStack(Material.GLOWSTONE_DUST, 1, (byte)0, (byte)0));
    		}
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
    			m.damageTool(player, (short) LoadProperties.abilityDurabilityLoss);
    	}
		
		if(block.getData() != (byte)5)
		{
			PlayerAnimationEvent armswing = new PlayerAnimationEvent(player);
			Bukkit.getPluginManager().callEvent(armswing);
			Excavation.excavationProcCheck(block.getType(), block.getLocation(), player);	
			Excavation.excavationProcCheck(block.getType(), block.getLocation(), player);
			Excavation.excavationProcCheck(block.getType(), block.getLocation(), player);
		}
		
		if(LoadProperties.spoutEnabled)
			SpoutStuff.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
	}
}