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
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import com.gmail.nossr50.Combat;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;

public class Axes
{
	public static int axesBonus(Player attacker, PlayerProfile PP)
	{
		//Add 1 DMG for every 50 skill levels
		int bonus = PP.getSkillLevel(SkillType.AXES) / 50;
		
		if(bonus > 4)
			bonus = 4;
		
		return bonus;
	}
	
	public static double axeCriticalBonus(Player attacker, int skillLevel, LivingEntity target, mcMMO pluginx)
	{
		if(target instanceof Wolf)
		{
			if(Taming.isFriendlyWolf(attacker, (Wolf) target, pluginx))
				return 1.0;
		}
		
		double criticalBonus = 0.0;
		
		if(skillLevel >= 750)
		{
			if(Math.random() * 1000 <= 750)
			{
				if(target instanceof Player)
				{
					criticalBonus = 1.5;
					((Player) target).sendMessage(ChatColor.DARK_RED + "You were CRITICALLY hit!");
				}
				else
					criticalBonus = 2.0;
				attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
			}
		}
		else if(Math.random() * 1000 <= skillLevel)
		{
			if(target instanceof Player)
			{
				criticalBonus = 1.5;
				((Player) target).sendMessage(ChatColor.DARK_RED + "You were CRITICALLY hit!");
			}
			else
				criticalBonus = 2.0;
			attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
		}
		
		return criticalBonus;
	}
	
	public static void impact(Player attacker, LivingEntity target)
	{
	    //TODO: Finish this skill, the idea is you will greatly damage an opponents armor and when they are armor less you have a proc that will stun them and deal additional damage.
	    boolean didImpact = false;
	    
	    if(target instanceof Player)
	    {
	        Player targetPlayer = (Player) target;
	        int emptySlots = 0;
	        short durDmg = 5; //Start with 5 durability dmg
	        
	        durDmg+=Users.getProfile(attacker).getSkillLevel(SkillType.AXES)/30; //Every 30 Skill Levels you gain 1 durability dmg
	        
	        System.out.println(durDmg);
	        
	        for(ItemStack x : targetPlayer.getInventory().getArmorContents())
	        {
	            if(x.getType() == Material.AIR)
	            {
	                emptySlots++;
	            } else {
	                x.setDurability((short) (x.getDurability()+durDmg)); //Damage armor piece
	            }
	        }
	        
	        if(emptySlots == 4)
	        {
	            didImpact = applyImpact(attacker, target);
	            if(didImpact)
	                targetPlayer.sendMessage(mcLocale.getString("Axes.GreaterImpactOnSelf"));
	        }
	    } else {
	        //Since mobs are technically unarmored this will always trigger
	        didImpact = applyImpact(attacker, target);
	    }
	    
	    if(didImpact)
	    {
	        attacker.sendMessage(mcLocale.getString("Axes.GreaterImpactOnEnemy"));
	    }
	}
	
	public static boolean applyImpact(Player attacker, LivingEntity target)
	{
	    if(Math.random() * 100 > 75)
        {
            Combat.dealDamage(target, 2, attacker);
            target.setVelocity(attacker.getLocation().getDirection().normalize().multiply(1.5D));
            return true;
        }
	    return false;
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
			else if(derp instanceof LivingEntity && targets >= 1)
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
