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

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.Combat;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.party.Party;

public class Axes
{
	public static void skullSplitterActivationCheck(Player player, PlayerProfile PPa)
	{
   		PPa.setAxePreparationMode(false);
   		
		int ticks = 2;
		int x = PPa.getSkillLevel(SkillType.AXES);
		while(x >= 50){
			x-=50;
			ticks++;
		}
		
		if (!PPa.getSkullSplitterMode())
		{
			if (Skills.cooldownOver(player, (PPa.getSkillDATS(AbilityType.SKULL_SPLIITER)*1000), LoadProperties.skullSplitterCooldown))
			{
				player.sendMessage(mcLocale.getString("Skills.SkullSplitterOn"));
				for(Player y : player.getWorld().getPlayers()){
					if(y != null && y != player && m.getDistance(player.getLocation(), y.getLocation()) < 10)
						y.sendMessage(mcLocale.getString("Skills.SkullSplitterPlayer", new Object[] {player.getName()}));
				}
				PPa.setSkillDATS(AbilityType.SKULL_SPLIITER, System.currentTimeMillis()+(ticks*1000));
				PPa.setSkullSplitterMode(true);
			}
			
			else
				player.sendMessage(mcLocale.getString("Skills.TooTired")
						+ChatColor.YELLOW+" ("+Skills.calculateTimeLeft(player, (PPa.getSkillDATS(AbilityType.SKULL_SPLIITER)*1000), LoadProperties.skullSplitterCooldown)+"s)");
		}
	}
	
	public static int axeCriticalCheck(Player attacker, int skillLevel, LivingEntity target, int damage, mcMMO pluginx)
	{
		if(target instanceof Wolf)
		{
			if (Taming.isFriendlyWolf(attacker, (Wolf) target, pluginx))
				return damage;
		}
		
		double random = Math.random() * 1000;
		int newDamage = damage;
		
		if(skillLevel >= 750)
		{
			if(random <= 750)
			{
				if(target instanceof Player)
				{
					newDamage *= 1.5;
					((Player) target).sendMessage(ChatColor.DARK_RED + "You were CRITICALLY hit!");
				}
				else
					newDamage *= 2;
				attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
			}
		}
		else if(random <= skillLevel)
		{
			if(target instanceof Player)
			{
				newDamage *= 1.5;
				((Player) target).sendMessage(ChatColor.DARK_RED + "You were CRITICALLY hit!");
			}
			else
				newDamage *= 2;
			attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
		}
		
		return newDamage;
	}
	
	public static void applyAoeDamage(Player attacker, LivingEntity target, int damage, mcMMO pluginx)
	{
		int targets = 0;
		
		int dmgAmount = damage / 2;
		
		//Setup minimum damage
		if(dmgAmount < 1)
			dmgAmount = 1;
		
		targets = m.getTier(attacker);
			
		for(Entity derp : target.getNearbyEntities(2.5, 2.5, 2.5))
		{
			//Make sure the Wolf is not friendly
			if(derp instanceof Wolf)
			{
				if (Taming.isFriendlyWolf(attacker, (Wolf) derp, pluginx))
					continue;
			}
				
			//Damage nearby LivingEntities
			if(derp instanceof LivingEntity && targets >= 1)
			{
				if(derp instanceof Player)
				{
					Player nearbyPlayer = (Player) derp;
						
					if(Users.getProfile(nearbyPlayer).getGodMode())
						continue;

					if(nearbyPlayer.getName().equals(attacker.getName()))
						continue;
						
					if(Party.getInstance().inSameParty(attacker, nearbyPlayer))
						continue;
						
					if(target.isDead())
						continue;
						
					if(targets >= 1 && nearbyPlayer.getWorld().getPVP())
					{
						Combat.dealDamage(nearbyPlayer, dmgAmount, attacker);
						nearbyPlayer.sendMessage(ChatColor.DARK_RED+"Struck by CLEAVE!");
						targets--;
					}
				}
				else
				{			
					LivingEntity nearbyLivingEntity = (LivingEntity) derp;
					Combat.dealDamage(nearbyLivingEntity, dmgAmount, attacker);
					targets--;
				}
			}
		}
	}
}
