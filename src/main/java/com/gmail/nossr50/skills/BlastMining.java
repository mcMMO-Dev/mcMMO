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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;

public class BlastMining{
	
	public static void explosionBlockDrops(Block block, Location loc)
	{
    	int id = block.getTypeId();
    	Material mat = Material.getMaterial(id);
		byte damage = 0;
		ItemStack item = new ItemStack(mat, 1, (byte)0, damage);
		
		if(id != 89 && id != 73 && id != 74 && id != 56 && id != 21 && id != 1 && id != 16 && id != 112 && id != 121 && id != 48)
		{
			m.mcDropItem(loc, item);
			return;
		}
			
		switch (id)
		{
		//GLOWSTONE
		case 89:
			mat = Material.getMaterial(348);
			item = new ItemStack(mat, 1, (byte)0, damage);
			m.mcDropItem(loc, item);
			m.mcDropItem(loc, item);
			if(Math.random() * 10 > 5)
				m.mcDropItem(loc, item);
			if(Math.random() * 10 > 5)
				m.mcDropItem(loc, item);
			break;
		//REDSTONE
		case 73:
			mat = Material.getMaterial(331);
			item = new ItemStack(mat, 1, (byte)0, damage);
			m.mcDropItem(loc, item);
			m.mcDropItem(loc, item);
			m.mcDropItem(loc, item);
			m.mcDropItem(loc, item);
			if(Math.random() * 10 > 5)
				m.mcDropItem(loc, item);
			break;
		case 74:
			mat = Material.getMaterial(331);
			item = new ItemStack(mat, 1, (byte)0, damage);
			m.mcDropItem(loc, item);
			m.mcDropItem(loc, item);
			m.mcDropItem(loc, item);
			m.mcDropItem(loc, item);
			if(Math.random() * 10 > 5)
				m.mcDropItem(loc, item);
			break;
		//LAPIS
		case 21:
			mat = Material.getMaterial(351);
			item = new ItemStack(mat, 1, (byte)0,(byte)0x4);
			m.mcDropItem(loc, item);
			m.mcDropItem(loc, item);
			m.mcDropItem(loc, item);
			m.mcDropItem(loc, item);
			if(Math.random() * 10 > 5)
				m.mcDropItem(loc, item);
			if(Math.random() * 10 > 5)
				m.mcDropItem(loc, item);
			if(Math.random() * 10 > 5)
				m.mcDropItem(loc, item);
			if(Math.random() * 10 > 5)
				m.mcDropItem(loc, item);
			break;
		//DIAMOND
		case 56:
			mat = Material.getMaterial(264);
			item = new ItemStack(mat, 1, (byte)0, damage);
			m.mcDropItem(loc, item);
			break;
		//STONE
		case 1:
			mat = Material.getMaterial(4);
			item = new ItemStack(mat, 1, (byte)0, damage);
			m.mcDropItem(loc, item);
			break;
		//COAL
		case 16:
			mat = Material.getMaterial(263);
			item = new ItemStack(mat, 1, (byte)0, damage);
			m.mcDropItem(loc, item);
			break;
		}
	}
	
	/*
	 * Process the drops from the explosion
	 */
	public static void dropProcessing(int skillLevel, EntityExplodeEvent event, mcMMO plugin)
	{
		float yield = event.getYield(); 
		List<Block> blocks = event.blockList();
		Iterator<Block> iterator = blocks.iterator();
		
		List<Block> ores = new ArrayList<Block>();
		List<Block> debris = new ArrayList<Block>();
		
		while(iterator.hasNext())
		{
			Block temp = iterator.next();
			int id = temp.getTypeId();
			if(id == 14 || id == 15 || id == 16 || id == 21 || id == 56 || id == 73 || id == 74)
			{
				ores.add(temp);
			}
			else
			{
				debris.add(temp);
			}
		}
		
		//Normal explosion
		if(skillLevel < 125)
			return;
		
		//Drop 10% more ores
		if(skillLevel >= 125 && skillLevel < 250)
		{
			event.setYield(0);
			Iterator<Block> iterator2 = ores.iterator();
			Iterator<Block> iterator3 = debris.iterator();
			while(iterator2.hasNext())
			{
				Block temp = iterator2.next();
				if(Math.random() * 100 < (yield + 10))
				{
					explosionBlockDrops(temp, event.getLocation());
				}
			}
			while(iterator3.hasNext())
			{
				Block temp = iterator3.next();
				if(Math.random() * 100 < yield)
				{
					explosionBlockDrops(temp, event.getLocation());
				}
			}
		}
		
		//Drop 20% more ores
		if(skillLevel >= 250 && skillLevel < 375)
		{
			event.setYield(0);
			Iterator<Block> iterator2 = ores.iterator();
			Iterator<Block> iterator3 = debris.iterator();
			while(iterator2.hasNext())
			{
				Block temp = iterator2.next();
				if(Math.random() * 100 < (yield + 20))
				{
					explosionBlockDrops(temp, event.getLocation());
				}
			}
			while(iterator3.hasNext())
			{
				Block temp = iterator3.next();
				if(Math.random() * 100 < yield)
				{
					explosionBlockDrops(temp, event.getLocation());
				}
			}
		}
		
		//No debris
		if(skillLevel >= 375 && skillLevel < 625)
		{
			event.setYield(0);
			Iterator<Block> iterator2 = ores.iterator();

			while(iterator2.hasNext())
			{
				Block temp = iterator2.next();
				if(Math.random() * 100 < yield + 20)
				{
					explosionBlockDrops(temp, event.getLocation());
				}
			}
		}
		
		//Double Drops
		if(skillLevel >= 625 && skillLevel < 875)
		{
			event.setYield(0);
			Iterator<Block> iterator2 = ores.iterator();

			while(iterator2.hasNext())
			{
				Block temp = iterator2.next();
				if(Math.random() * 100 < yield + 20)
				{
					explosionBlockDrops(temp, event.getLocation());
					if(Math.random() * 1000 <= skillLevel) 
						explosionBlockDrops(temp, event.getLocation());
				}
			}
		}
		
		//Triple Drops
		if(skillLevel >= 875)
		{
			event.setYield(0);
			Iterator<Block> iterator2 = ores.iterator();

			while(iterator2.hasNext())
			{
				Block temp = iterator2.next();
				if(Math.random() * 100 < yield + 20)
				{
					explosionBlockDrops(temp, event.getLocation());
					if(Math.random() * 1000 <= skillLevel || skillLevel > 1000) 
						explosionBlockDrops(temp, event.getLocation());
					if(Math.random() * 1000 <= skillLevel || skillLevel > 1000) 
						explosionBlockDrops(temp, event.getLocation());
				}
			}
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
	 * Reduces explosion damage to 1/2 of normal at 1000.
	 */
	public static void demolitionsExpertise(Player player, EntityDamageEvent event)
	{
		PlayerProfile PP = Users.getProfile(player);
		int skill = PP.getSkillLevel(SkillType.MINING);
		int damage = event.getDamage();
		if(skill < 500)
			return;
		if(skill >= 500 && skill < 1000)
			damage = damage/4;
		if(skill >= 1000)
			damage = damage/2;
		
		event.setDamage(damage);
	}

}