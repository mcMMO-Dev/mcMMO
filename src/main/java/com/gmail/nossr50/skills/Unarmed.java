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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.gmail.nossr50.m;
import com.gmail.nossr50.locale.mcLocale;

public class Unarmed
{
	public static int unarmedBonus(int skillLevel)
	{
		//Add 1 DMG for every 50 skill levels
		int bonus = skillLevel / 50;
		
		if(bonus > 8)
			bonus = 8;
		
		return bonus;
	}
	
	public static void disarmProcCheck(Player attacker, int skillLevel, Player defender)
	{
		if(defender.getItemInHand() != null && defender.getItemInHand().getType() != Material.AIR)
		{
			if(skillLevel >= 1000)
			{
				if(Math.random() * 3000 <= 1000)
				{
					ItemStack item = defender.getItemInHand();
					defender.sendMessage(mcLocale.getString("Skills.Disarmed"));
					m.mcDropItem(defender.getLocation(), item);
					defender.setItemInHand(null);
				}
			}
			else
			{
				if(Math.random() * 3000 <= skillLevel)
				{
					ItemStack item = defender.getItemInHand();
					defender.sendMessage(mcLocale.getString("Skills.Disarmed"));
					m.mcDropItem(defender.getLocation(), item);
					defender.setItemInHand(null);
				}
			}
		}
	}
}
