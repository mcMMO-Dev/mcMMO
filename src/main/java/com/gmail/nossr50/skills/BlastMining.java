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
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;

public class BlastMining{
	
	public static Block explosionBlockDrops(Block block, Location loc)
	{
    	int id = block.getTypeId();
		ItemStack item = new ItemStack(id, 1);
			
		switch (id){
		//GLOWSTONE
		case 89:
			item = new ItemStack(348, 1);
			m.mcDropItems(loc, item, 2);
			m.mcRandomDropItems(loc, item, 50, 2);
			break;
		//REDSTONE
		case 73:
			item = new ItemStack(331, 1);
			m.mcDropItems(loc, item, 4);
			m.mcRandomDropItem(loc, item, 50);
			break;
		case 74:
			item = new ItemStack(331, 1);
			m.mcDropItems(loc, item, 4);
			m.mcRandomDropItem(loc, item, 50);
			break;
		//LAPIS
		case 21:
			item = new ItemStack(351, 1, (byte)0,(byte)0x4);
			m.mcDropItems(loc, item, 4);
			m.mcRandomDropItems(loc, item, 50, 4);
			break;
		//DIAMOND
		case 56:
			item = new ItemStack(264, 1);
			m.mcDropItem(loc, item);
			break;
		//STONE
		case 1:
			item = new ItemStack(4, 1);
			m.mcDropItem(loc, item);
			break;
		//COAL
		case 16:
			item = new ItemStack(263, 1);
			m.mcDropItem(loc, item);
			break;
		default:
			m.mcDropItem(loc, item);
			break;	
		}
		return block;
	}
	
	public static List<Block> explosionYields(List<Block> ores, List<Block> debris, float yield, float oreBonus, float debrisReduction, Location location, int extraDrops)
	{
		Iterator<Block> iterator2 = ores.iterator();
		List<Block> blocksDropped = new ArrayList<Block>();
		while(iterator2.hasNext())
		{
			Block temp = iterator2.next();
			if((float)Math.random() < (yield + oreBonus))
			{
				blocksDropped.add(temp);
				explosionBlockDrops(temp, location);
				if(extraDrops == 2)
					blocksDropped.add(temp);
					explosionBlockDrops(temp, location);
				if(extraDrops == 3)
					blocksDropped.add(temp);
					explosionBlockDrops(temp, location);
			}
		}
		
		if(yield - debrisReduction != 0)
		{
			Iterator<Block> iterator3 = debris.iterator();
			while(iterator3.hasNext())
			{
				Block temp = iterator3.next();
				if((float)Math.random() < (yield - debrisReduction))
					explosionBlockDrops(temp, location);
			}
		}
		return blocksDropped;
	}
	
	/*
	 * Process the drops from the explosion
	 */
	public static void dropProcessing(Player player, EntityExplodeEvent event, mcMMO plugin)
	{
		int skillLevel = Users.getProfile(player).getSkillLevel(SkillType.MINING);
		float yield = event.getYield(); 
		Location location = event.getLocation();
		List<Block> blocks = event.blockList();
		Iterator<Block> iterator = blocks.iterator();
		
		List<Block> ores = new ArrayList<Block>();
		List<Block> debris = new ArrayList<Block>();
		
		List<Block> xp = new ArrayList<Block>();
		
		while(iterator.hasNext())
		{
			Block temp = iterator.next();
			if(temp.getData() != 5 && !plugin.misc.blockWatchList.contains(temp))
			{
				if(m.isOre(temp))
					ores.add(temp);
				else
					debris.add(temp);
			}
		}
		
		//Normal explosion
		if(skillLevel < 125)
			return;
		
		event.setYield(0);
		//+35% ores, -10% debris
		if(skillLevel >= 125 && skillLevel < 250)
			xp = explosionYields(ores, debris, yield, .35f, .10f, location, 1);
		
		//+40% ores, -20% debris
		if(skillLevel >= 250 && skillLevel < 375)
			xp = explosionYields(ores, debris, yield, .40f, .20f, location, 1);
		
		//No debris, +45% ores
		if(skillLevel >= 375 && skillLevel < 500)
			xp = explosionYields(ores, debris, yield, .45f, .30f, location, 1);
		
		//No debris, +50% ores
		if(skillLevel >= 500 && skillLevel < 625)
			xp = explosionYields(ores, debris, yield, .50f, .30f, location, 1);
		
		//Double Drops, No Debris, +55% ores
		if(skillLevel >= 625 && skillLevel < 750)
			xp = explosionYields(ores, debris, yield, .55f, .30f, location, 2);
		
		//Double Drops, No Debris, +60% ores
		if(skillLevel >= 750 && skillLevel < 875)
			xp = explosionYields(ores, debris, yield, .60f, .30f, location, 2);
				
		//Triple Drops, No debris, +65% ores
		if(skillLevel >= 875 && skillLevel < 1000)
			xp = explosionYields(ores, debris, yield, .65f, .30f, location, 3);

		//Triple Drops, No debris, +70% ores
		if(skillLevel >= 1000)
			xp = explosionYields(ores, debris, yield, .70f, .30f, location, 3);
		
		Iterator<Block> xpGain = xp.iterator();
		
		while(xpGain.hasNext())
		{
			blastMiningXP(player, xpGain.next(), plugin);
		}
	}
	
	/*
	 * Bigger Bombs (Unlocked at Mining 250)
	 * 
	 * Increases radius of explosion by 1 at 250.
	 * Increases radius of explosion by 2 at 500.
	 * Increases radius of explosion by 3 at 750.
	 * Increases radius of explosion by 4 at 1000.
	 */
	public static void biggerBombs(int skillLevel, ExplosionPrimeEvent event)
	{
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
	
	/*
	 * Demolitions Expertise (Unlocked at Mining 500) 
	 * 
	 * Reduces explosion damage to 1/4 of normal at 500.
	 * Reduces explosion damage to 1/2 of normal at 750.
	 * Reduces explosion damage to 0 at 1000.
	 */
	public static void demolitionsExpertise(int skill, EntityDamageEvent event)
	{
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

	public static void blastMiningXP(Player player, Block block, mcMMO plugin)
    {
    	PlayerProfile PP = Users.getProfile(player);
    	if(plugin.misc.blockWatchList.contains(block) || block.getData() == (byte) 5)
    		return;
    	int xp = 0;
		int id = block.getTypeId();
		
		switch (id) {
			//COAL
			case 16:
				xp += LoadProperties.mcoal;
				break;
			//GOLD
			case 14:
				xp += LoadProperties.mgold;
				break;
			//DIAMOND
			case 56:
				xp += LoadProperties.mdiamond;
				break;
			//IRON
			case 15:
				xp += LoadProperties.miron;
				break;
			//REDSTONE
			case 73:
				xp += LoadProperties.mredstone;
				break;
			case 74:
				xp += LoadProperties.mredstone;
				break;
			//LAPIS
			case 21:
				xp += LoadProperties.mlapis;
				break;
		}
		
    	PP.addXP(SkillType.MINING, xp, player);
    	Skills.XpCheckSkill(SkillType.MINING, player);
    }
	
}