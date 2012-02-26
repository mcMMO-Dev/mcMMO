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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import com.gmail.nossr50.Combat;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;

public class Swords 
{
	public static void serratedStrikesActivationCheck(Player player, PlayerProfile PPa)
	{
		PPa.setSwordsPreparationMode(false);
			
		int ticks = 2;
		int x = PPa.getSkillLevel(SkillType.SWORDS);
		while(x >= 50)
		{
			x-=50;
			ticks++;
		}
			
		if(!PPa.getSerratedStrikesMode() && PPa.getSkillDATS(AbilityType.SERRATED_STRIKES) < System.currentTimeMillis())
		{
			player.sendMessage(mcLocale.getString("Skills.SerratedStrikesOn"));
			for(Player y : player.getWorld().getPlayers())
			{
				if(y != null && y != player && m.getDistance(player.getLocation(), y.getLocation()) < 10)
					y.sendMessage(mcLocale.getString("Skills.SerratedStrikesPlayer", new Object[] {player.getName()}));
			}
			PPa.setSkillDATS(AbilityType.SERRATED_STRIKES, System.currentTimeMillis()+(ticks*1000));
			PPa.setSerratedStrikesMode(true);
		}
	}

	public static void bleedCheck(Player attacker, int skillLevel, LivingEntity target, mcMMO pluginx)
	{
		if(target instanceof Wolf)
		{
			if(Taming.isFriendlyWolf(attacker, (Wolf) target, pluginx))
				return;
		}
		
		double random = Math.random() * 1000;

		if(skillLevel >= 750)
		{
			if(random <= 750)
			{
				if(!(target instanceof Player))
					pluginx.misc.addToBleedQue(target);
				else if(target instanceof Player)
					Users.getProfile((Player) target).addBleedTicks(3);
				attacker.sendMessage(ChatColor.GREEN+"**ENEMY BLEEDING**");
			}
		} 
		else if(random <= skillLevel)
		{
			if(!(target instanceof Player))
				pluginx.misc.addToBleedQue(target);
			if(target instanceof Player)
				Users.getProfile((Player) target).addBleedTicks(2);
			attacker.sendMessage(ChatColor.GREEN+"**ENEMY BLEEDING**");
		}
	}
	
	public static void applySerratedStrikes(Player attacker, LivingEntity target, int damage, mcMMO pluginx)
	{
		int targets = 0;
		
		int dmgAmount = damage / 4;
		
		//Setup minimum damage
		if(dmgAmount < 1)
			dmgAmount = 1;
		
		targets = m.getTier(attacker);
			
		for(Entity derp : target.getNearbyEntities(2.5, 2.5, 2.5))
		{
			//Make sure the Wolf is not friendly
			if(derp instanceof Wolf)
			{
				if (Taming.isFriendlyWolf(attacker, (Wolf) target, pluginx))
					continue;
			}
			
			//Damage nearby LivingEntities
			if(derp instanceof LivingEntity && targets >= 1)
			{
				if(derp instanceof Player)
				{
					Player nearbyPlayer = (Player) derp;
						
					if(nearbyPlayer.getName().equals(attacker.getName()))
						continue;
						
					if(Users.getProfile(nearbyPlayer).getGodMode())
						continue;
						
					if(Party.getInstance().inSameParty(attacker, nearbyPlayer))
						continue;
					
					if(targets >= 1 && nearbyPlayer.getWorld().getPVP())
					{
						Combat.dealDamage(nearbyPlayer, dmgAmount, attacker);
						nearbyPlayer.sendMessage(ChatColor.DARK_RED+"Struck by Serrated Strikes!");
						Users.getProfile(nearbyPlayer).addBleedTicks(5);
						targets--;
					}
				}
				else
				{
					LivingEntity neabyLivingEntity = (LivingEntity) derp;
					
					if(!pluginx.misc.bleedTracker.contains(neabyLivingEntity))
						pluginx.misc.addToBleedQue(neabyLivingEntity);
					
					Combat.dealDamage(neabyLivingEntity, dmgAmount, attacker);
					targets--;
				}
			}
		}
	}
	
	public static void counterAttackChecks(EntityDamageByEntityEvent event)
	{
		//Don't want to counter attack stuff not alive
		
		if(!(event.getDamager() instanceof LivingEntity))
			return;

		if(event instanceof EntityDamageByEntityEvent)
		{
			Entity f = ((EntityDamageByEntityEvent) event).getDamager();
		   	if(event.getEntity() instanceof Player)
		   	{
		   		Player defender = (Player)event.getEntity();
		   		PlayerProfile PPd = Users.getProfile(defender);
		   		if(m.isSwords(defender.getItemInHand()) && mcPermissions.getInstance().swords(defender))
		   		{
					if(PPd.getSkillLevel(SkillType.SWORDS) >= 600)
					{
						if(Math.random() * 2000 <= 600)
						{
							Combat.dealDamage((LivingEntity) f, event.getDamage() / 2);
							defender.sendMessage(ChatColor.GREEN+"**COUNTER-ATTACKED**");
							if(f instanceof Player)
							((Player) f).sendMessage(ChatColor.DARK_RED+"Hit with counterattack!");
						}
					}
					else if (Math.random() * 2000 <= PPd.getSkillLevel(SkillType.SWORDS))
					{
						Combat.dealDamage((LivingEntity) f, event.getDamage() / 2);
						defender.sendMessage(ChatColor.GREEN+"**COUNTER-ATTACKED**");
						if(f instanceof Player)
							((Player) f).sendMessage(ChatColor.DARK_RED+"Hit with counterattack!");
					}
		   		}
			}
		}
	}
	
	public static void bleedSimulate(mcMMO plugin)
	{
		//Add items from Que list to BleedTrack list
		
		for(LivingEntity x : plugin.misc.bleedQue)
		{
			plugin.misc.bleedTracker.add(x);
		}
		
		//Clear list
		plugin.misc.bleedQue = new LivingEntity[plugin.misc.bleedQue.length];
		plugin.misc.bleedQuePos = 0;
		
		//Cleanup any dead entities from the list
		for(LivingEntity x : plugin.misc.bleedRemovalQue)
		{
			plugin.misc.bleedTracker.remove(x);
		}
		
		//Clear bleed removal list
		plugin.misc.bleedRemovalQue = new LivingEntity[plugin.misc.bleedRemovalQue.length];
		plugin.misc.bleedRemovalQuePos = 0;
		
		//Bleed monsters/animals
		for(LivingEntity x : plugin.misc.bleedTracker)
		{
			if(x == null){continue;}
			
			if(x.getHealth() <= 0)
			{
				plugin.misc.addToBleedRemovalQue(x);
				continue;
			}
			else
			{
				Combat.dealDamage(x, 2);
			}
		}
	}
}
