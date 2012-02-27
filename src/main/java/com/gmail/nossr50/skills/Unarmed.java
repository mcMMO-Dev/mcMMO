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

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.gmail.nossr50.m;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;

public class Unarmed
{
	public static void berserkActivationCheck(Player player, PlayerProfile PPa)
	{
		PPa.setFistsPreparationMode(false);
		
		int ticks = 2;
		int x = PPa.getSkillLevel(SkillType.UNARMED); 	
		while(x >= 50)
		{
			x-=50;
			ticks++;
		}
			
		if(!PPa.getBerserkMode() && Skills.cooldownOver(player, PPa.getSkillDATS(AbilityType.BERSERK), LoadProperties.berserkCooldown))
		{
			player.sendMessage(mcLocale.getString("Skills.BerserkOn"));
			for(Player y : player.getWorld().getPlayers())
			{
				if(y != null && y != player && m.getDistance(player.getLocation(), y.getLocation()) < 10)
					y.sendMessage(mcLocale.getString("Skills.BerserkPlayer", new Object[] {player.getName()}));
			}
			PPa.setSkillDATS(AbilityType.BERSERK, System.currentTimeMillis()+(ticks*1000));
			//System.out.println("getSkillDATS(): "+PPa.getSkillDATS(AbilityType.BERSERK));
			PPa.setBerserkMode(true);
		}
	}
	
	public static int unarmedBonus(int skillLevel)
	{
		int bonus = 3;
		
		//Add 1 DMG for every 50 skill levels
		bonus += skillLevel / 50;
		
		if(bonus > 8)
			bonus = 8;
		
		return bonus;
	}
	
	public static void disarmProcCheck(Player attacker, int skillLevel, Player defender)
	{
		double random = Math.random() * 4000;
		
		if(skillLevel >= 1000)
		{
	   		if(random <= 1000)
	   		{
	   			Location loc = defender.getLocation();
	   			ItemStack itemInHand = defender.getItemInHand();
	   			
	   			if(itemInHand != null && itemInHand.getTypeId() != 0)
	   			{
	   				defender.sendMessage(mcLocale.getString("Skills.Disarmed"));
					m.mcDropItem(loc, itemInHand);
					defender.setItemInHand(null);
	   			}
	   		}
	   	}
		else
	   	{
	   		if(random <= skillLevel)
	   		{
	   			Location loc = defender.getLocation();
	   			ItemStack itemInHand = defender.getItemInHand();
	   			
	   			if(itemInHand != null && itemInHand.getTypeId() != 0)
	   			{
	   				defender.sendMessage(mcLocale.getString("Skills.Disarmed"));
					m.mcDropItem(loc, itemInHand);
					defender.setItemInHand(null);
	   			}
	   		}
		}
	}
}
