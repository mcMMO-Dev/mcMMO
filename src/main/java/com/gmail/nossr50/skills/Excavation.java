package com.gmail.nossr50.skills;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.player.PlayerAnimationEvent;

import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.config.LoadTreasures;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;

import org.getspout.spoutapi.sound.SoundEffect;

public class Excavation
{
	public static boolean canBeGigaDrillBroken(Block block)
	{
		switch(block.getType()){
		case CLAY:
		case DIRT:
		case GRASS:
		case GRAVEL:
		case MYCEL:
		case SAND:
		case SOUL_SAND:
			return true;
		}
		return false;
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
    		for(ExcavationTreasure treasure : LoadTreasures.excavationFromDirt)
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
    		for(ExcavationTreasure treasure : LoadTreasures.excavationFromGrass)
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
    		for(ExcavationTreasure treasure : LoadTreasures.excavationFromSand)
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
    		for(ExcavationTreasure treasure : LoadTreasures.excavationFromGravel)
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
    		for(ExcavationTreasure treasure : LoadTreasures.excavationFromClay)
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
    		for(ExcavationTreasure treasure : LoadTreasures.excavationFromMycel)
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
    		for(ExcavationTreasure treasure : LoadTreasures.excavationFromSoulSand)
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
		Skills.abilityDurabilityLoss(player.getItemInHand(), LoadProperties.abilityDurabilityLoss);
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