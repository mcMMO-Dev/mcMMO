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

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.Combat;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;

public class Taming 
{
	public static void rewardXp(EntityDamageEvent event, mcMMO pluginx, Player master)
	{
		Entity entity = event.getEntity();
		if(!pluginx.misc.mobSpawnerList.contains(entity.getEntityId()))
		{
			int xp = Combat.getXp(entity, event.getDamage());
			Users.getProfile(master).addXP(SkillType.TAMING, xp*10, master);
			
			if(entity instanceof Player)
			{
				xp = (event.getDamage() * 2);
				Users.getProfile(master).addXP(SkillType.TAMING, (int)((xp*10)*1.5), master);
			}
			Skills.XpCheckSkill(SkillType.TAMING, master);
		}
	}
	
	public static void fastFoodService(PlayerProfile PPo, Wolf theWolf, EntityDamageEvent event)
	{
		int health = theWolf.getHealth();
		int maxHealth = theWolf.getMaxHealth();
		int damage = event.getDamage();
		if(PPo.getSkillLevel(SkillType.TAMING) >= 50)
		{
			if(health < maxHealth)
			{
				if(Math.random() * 10 > 5)
				{
					if(health + damage <= maxHealth)
						theWolf.setHealth(health + damage);
					else
						theWolf.setHealth(maxHealth);
				}
			}
		}
	}
	
	public static void sharpenedClaws(PlayerProfile PPo, EntityDamageEvent event)
	{
		if(PPo.getSkillLevel(SkillType.TAMING) >= 750)
		{
			event.setDamage(event.getDamage() + 2);
		}
	}
	
	public static void gore(PlayerProfile PPo, EntityDamageEvent event, Player master, mcMMO pluginx)
	{
		if(Math.random() * 1000 <= PPo.getSkillLevel(SkillType.TAMING))
		{
			Entity entity = event.getEntity();
			event.setDamage(event.getDamage() * 2);
			
			if(entity instanceof Player)
			{
				Player target = (Player)entity;
				target.sendMessage(mcLocale.getString("Combat.StruckByGore")); //$NON-NLS-1$
				Users.getProfile(target).setBleedTicks(2);
			}
			else
				pluginx.misc.addToBleedQue((LivingEntity)entity);
			
			master.sendMessage(mcLocale.getString("Combat.Gore")); //$NON-NLS-1$
		}
	}
	
	public static String getOwnerName(Wolf theWolf)
	{
		Player owner = null;
		
		if (theWolf.getOwner() instanceof Player)
		{
			owner = (Player)theWolf.getOwner();
			return owner.getName();
		}
		else
			return "Offline Master";
	}
	
	public static void preventDamage(EntityDamageEvent event, mcMMO plugin)
	{
		DamageCause cause = event.getCause();
		Wolf wolf = (Wolf) event.getEntity();
		Player master = (Player) wolf.getOwner();
		int skillLevel = Users.getProfile(master).getSkillLevel(SkillType.TAMING);
		
		switch(cause)
		{
		//Environmentally Aware
		case CONTACT:
		case LAVA:
		case FIRE:
			if(skillLevel >= 100)
			{
				if(event.getDamage() >= wolf.getHealth())
					return;
				
				wolf.teleport(master.getLocation());
				master.sendMessage(mcLocale.getString("mcEntityListener.WolfComesBack")); //$NON-NLS-1$
			}
			break;
		case FALL:
			if(skillLevel >= 100)
				event.setCancelled(true);
			break;
			
		//Thick Fur
		case FIRE_TICK:
			if(skillLevel >= 250)
				wolf.setFireTicks(0);
			break;
		case ENTITY_ATTACK:
		case PROJECTILE:
			if(skillLevel >= 250)
				event.setDamage(event.getDamage() / 2);
			break;
			
		//Shock Proof
		case ENTITY_EXPLOSION:
		case BLOCK_EXPLOSION:
			if(skillLevel >= 500)
				event.setDamage(event.getDamage() / 6);
			break;
		}
	}
	
	public static void animalSummon(EntityType type, Player player)
	{
		ItemStack item = player.getItemInHand();
		Material summonItem = null;
		int summonAmount = 0;
		
		switch(type)
		{
		case WOLF:
			summonItem = Material.BONE;
			summonAmount = LoadProperties.bonesConsumedByCOTW;
		case OCELOT:
			summonItem = Material.RAW_FISH;
			summonAmount = LoadProperties.fishConsumedByCOTW;
		}
		
		if(item.getType().equals(summonItem) && item.getAmount() >= summonAmount)
		{
			for(Entity x : player.getNearbyEntities(40, 40, 40))
			{
				if(x.getType().equals(type))
				{
					player.sendMessage(mcLocale.getString("m.TamingSummonFailed"));
					return;
				}
			}
			
			World world = player.getWorld();
			world.spawnCreature(player.getLocation(), type);
			
			int amount = item.getAmount();
			amount =- summonAmount;
			player.setItemInHand(new ItemStack(summonItem, amount));
	    	player.sendMessage(mcLocale.getString("m.TamingSummon"));
		}
	}
}
