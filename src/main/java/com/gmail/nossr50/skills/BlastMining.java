package com.gmail.nossr50.skills;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

import com.gmail.nossr50.BlockChecks;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.SkillType;

public class BlastMining{
	
	/**
	 * Handler for what blocks drop from the explosion.
	 * 
	 * @param ores List of ore blocks destroyed by the explosion
	 * @param debris List of non-ore blocks destroyed by the explosion
	 * @param yield Percentage of blocks to drop
	 * @param oreBonus Percentage bonus for ore drops
	 * @param debrisReduction Percentage reduction for non-ore drops
	 * @param extraDrops Number of times to drop each block
	 * @param plugin mcMMO plugin instance
	 * @return A list of blocks dropped from the explosion
	 */
	private static List<Block> explosionYields(List<Block> ores, List<Block> debris, float yield, float oreBonus, float debrisReduction, int extraDrops, mcMMO plugin)
	{
		Iterator<Block> iterator2 = ores.iterator();
		List<Block> blocksDropped = new ArrayList<Block>();
		while(iterator2.hasNext())
		{
			Block temp = iterator2.next();
			if((float)Math.random() < (yield + oreBonus))
			{
				blocksDropped.add(temp);
				Mining.miningDrops(temp);
				if(temp.getData() != (byte)5 && !plugin.misc.blockWatchList.contains(temp))
				{
					if(extraDrops == 2)
					{
						blocksDropped.add(temp);
						Mining.miningDrops(temp);
					}
					if(extraDrops == 3)
					{
						blocksDropped.add(temp);
						Mining.miningDrops(temp);
					}
				}
			}
		}
		
		if(yield - debrisReduction != 0)
		{
			Iterator<Block> iterator3 = debris.iterator();
			while(iterator3.hasNext())
			{
				Block temp = iterator3.next();
				if((float)Math.random() < (yield - debrisReduction))
					Mining.miningDrops(temp);
			}
		}
		return blocksDropped;
	}
	
	/**
	 * Handler for explosion drops and XP gain.
	 * @param player Player triggering the explosion
	 * @param event Event whose explosion is being processed
	 * @param plugin mcMMO plugin instance
	 */
	public static void dropProcessing(Player player, EntityExplodeEvent event, mcMMO plugin)
	{
		int skillLevel = Users.getProfile(player).getSkillLevel(SkillType.MINING);
		float yield = event.getYield(); 
		List<Block> blocks = event.blockList();
		Iterator<Block> iterator = blocks.iterator();
		
		List<Block> ores = new ArrayList<Block>();
		List<Block> debris = new ArrayList<Block>();
		List<Block> xp = new ArrayList<Block>();

		while(iterator.hasNext())
		{
			Block temp = iterator.next();
			if(BlockChecks.isOre(temp.getType()))
				ores.add(temp);
			else
				debris.add(temp);
		}
		
		//Normal explosion
		if(skillLevel < 125)
			return;
		
		event.setYield(0);
		
		//+35% ores, -10% debris
		if(skillLevel >= 125 && skillLevel < 250)
			xp = explosionYields(ores, debris, yield, .35f, .10f, 1, plugin);
		
		//+40% ores, -20% debris
		if(skillLevel >= 250 && skillLevel < 375)
			xp = explosionYields(ores, debris, yield, .40f, .20f, 1, plugin);
		
		//No debris, +45% ores
		if(skillLevel >= 375 && skillLevel < 500)
			xp = explosionYields(ores, debris, yield, .45f, .30f, 1, plugin);
		
		//No debris, +50% ores
		if(skillLevel >= 500 && skillLevel < 625)
			xp = explosionYields(ores, debris, yield, .50f, .30f, 1, plugin);
		
		//Double Drops, No Debris, +55% ores
		if(skillLevel >= 625 && skillLevel < 750)
			xp = explosionYields(ores, debris, yield, .55f, .30f, 2, plugin);
		
		//Double Drops, No Debris, +60% ores
		if(skillLevel >= 750 && skillLevel < 875)
			xp = explosionYields(ores, debris, yield, .60f, .30f, 2, plugin);
				
		//Triple Drops, No debris, +65% ores
		if(skillLevel >= 875 && skillLevel < 1000)
			xp = explosionYields(ores, debris, yield, .65f, .30f, 3, plugin);

		//Triple Drops, No debris, +70% ores
		if(skillLevel >= 1000)
			xp = explosionYields(ores, debris, yield, .70f, .30f, 3, plugin);
		
		for(Block block : xp)
		{
			if(block.getData() != (byte)5 && !plugin.misc.blockWatchList.contains(block))
				Mining.miningXP(player, block);
		}
			
	}
	
	/**
	 * Increases the blast radius of the explosion.
	 * 
	 * @param player Player triggering the explosion
	 * @param event Event whose explosion radius is being changed
	 */
	public static void biggerBombs(Player player, ExplosionPrimeEvent event)
	{
		int skillLevel = Users.getProfile(player).getSkillLevel(SkillType.MINING);
		float radius = event.getRadius();
		if(skillLevel < 250)
			return;
		if(skillLevel >= 250)
			radius++;
		if(skillLevel >= 500)
			radius++;
		if(skillLevel >= 750)
			radius++;
		if(skillLevel >= 1000)
			radius++;	
		event.setRadius(radius);
	}
	
	/**
	 * Decreases damage dealt by the explosion.
	 * 
	 * @param player Player triggering the explosion
	 * @param event Event whose explosion damage is being reduced
	 */
	public static void demolitionsExpertise(Player player, EntityDamageEvent event)
	{
		int skill = Users.getProfile(player).getSkillLevel(SkillType.MINING);
		int damage = event.getDamage();
		if(skill < 500)
			return;
		if(skill >= 500 && skill < 750)
			damage = damage/4;
		if(skill >= 750 && skill < 1000)
			damage = damage/2;
		if(skill >= 1000)
			damage = 0;
		event.setDamage(damage);
	}

	
}
