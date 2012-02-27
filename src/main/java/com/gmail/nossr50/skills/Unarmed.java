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

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;

public class Unarmed {
	public static void berserkActivationCheck(Player player)
	{
    	PlayerProfile PP = Users.getProfile(player);
    	AbilityType ability = AbilityType.BERSERK;
		if(player.getItemInHand() == null)
		{
			if(PP.getFistsPreparationMode())
    			PP.setFistsPreparationMode(false);
			
	    	int ticks = 2;
	    	int x = PP.getSkillLevel(SkillType.UNARMED);
	    	
    		while(x >= 50)
    		{
    			x-=50;
    			ticks++;
    		}
    		
	    	if(!PP.getBerserkMode() && Skills.cooldownOver(player, PP.getSkillDATS(ability), LoadProperties.berserkCooldown))
	    	{
	    		
	    		player.sendMessage(mcLocale.getString("Skills.BerserkOn"));
	    		for(Player y : player.getWorld().getPlayers())
	    		{
	    			if(y != null && y != player && m.getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(mcLocale.getString("Skills.BerserkPlayer", new Object[] {player.getName()}));
	    		}
	    		PP.setSkillDATS(ability, System.currentTimeMillis()+(ticks*1000));
	    		PP.setBerserkMode(true);
	    	}
	    }
	}
	
	public static void unarmedBonus(Player attacker, EntityDamageByEntityEvent event)
	{
		int bonus = 3;
		
		//Add 1 DMG for every 50 skill levels
		bonus += Users.getProfile(attacker).getSkillLevel(SkillType.UNARMED)/50;
		
		if(bonus > 8)
		    bonus = 8;
        
		event.setDamage(event.getDamage() + bonus);
	}
	
	public static void disarmProcCheck(Player attacker, Player defender)
	{
		int skillLevel = Users.getProfile(attacker).getSkillLevel(SkillType.UNARMED);
		if(attacker.getItemInHand() == null)
		{
			if(skillLevel >= 1000)
			{
				if(Math.random() * 4000 <= 1000)
				{
	    			ItemStack item = defender.getItemInHand();
	    			if(item != null)
	    			{
	    				defender.sendMessage(mcLocale.getString("Skills.Disarmed"));
		    			m.mcDropItem(defender.getLocation(), item);
		    			defender.setItemInHand(null);
	    			}
				}
	    	} 
			else
    		{
				if(Math.random() * 4000 <= skillLevel)
				{
	    			ItemStack item = defender.getItemInHand();
	    			if(item != null)
	    			{
	    				defender.sendMessage(mcLocale.getString("Skills.Disarmed"));
		    			m.mcDropItem(defender.getLocation(), item);
		    			defender.setItemInHand(null);
	    			}
				}
    		}
		}
	}
}
