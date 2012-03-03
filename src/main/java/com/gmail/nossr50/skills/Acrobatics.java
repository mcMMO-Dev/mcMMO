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
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;


public class Acrobatics {
	public static void acrobaticsCheck(Player player, EntityDamageEvent event)
	{
		PlayerProfile PP = Users.getProfile(player);
		int acrovar = PP.getSkillLevel(SkillType.ACROBATICS);
		
		if(player.isSneaking())
			acrovar = acrovar * 2;
		
		if(Math.random() * 1000 <= acrovar)
		{
			int threshold = 7;
			
			if(player.isSneaking())
				threshold = 14;
			
			int newDamage = event.getDamage() - threshold;
			
			if(newDamage < 0)
				newDamage = 0;
			
			/*
			 * Check for death
			 */
			if(player.getHealth() - newDamage >= 1)
			{
				PP.addXP(SkillType.ACROBATICS, (event.getDamage() * 8)*10, player);
				Skills.XpCheckSkill(SkillType.ACROBATICS, player);
				event.setDamage(newDamage);
				if(event.getDamage() <= 0)
					event.setCancelled(true);
				if(player.isSneaking()){
					player.sendMessage(ChatColor.GREEN+"**GRACEFUL ROLL**");
				} else {
					player.sendMessage("**ROLL**");
				}
			}
		} 
		else if(player.getHealth() - event.getDamage() >= 1)
		{
			PP.addXP(SkillType.ACROBATICS, (event.getDamage() * 12)*10, player);
			Skills.XpCheckSkill(SkillType.ACROBATICS, player);
		}
    }
	public static void dodgeChecks(EntityDamageByEntityEvent event){
		Player defender = (Player) event.getEntity();
		PlayerProfile PPd = Users.getProfile(defender);
		
		if(mcPermissions.getInstance().acrobatics(defender)){
			if(PPd.getSkillLevel(SkillType.ACROBATICS) <= 800){
	    		if(Math.random() * 4000 <= PPd.getSkillLevel(SkillType.ACROBATICS)){
	    			defender.sendMessage(ChatColor.GREEN+"**DODGE**");
	    			if(System.currentTimeMillis() >= 5000 + PPd.getRespawnATS() && defender.getHealth() >= 1){
	    				PPd.addXP(SkillType.ACROBATICS, (event.getDamage() * 12)*1, defender);
	    				Skills.XpCheckSkill(SkillType.ACROBATICS, defender);
	    			}
	    			event.setDamage(event.getDamage() / 2);
	    			//Needs to do minimal damage
	    			if(event.getDamage() <= 0)
	    				event.setDamage(1);
	    		}
			} else if(Math.random() * 4000 <= 800) {
				defender.sendMessage(ChatColor.GREEN+"**DODGE**");
				if(System.currentTimeMillis() >= 5000 + PPd.getRespawnATS() && defender.getHealth() >= 1){
					PPd.addXP(SkillType.ACROBATICS, (event.getDamage() * 12)*10, defender);
					Skills.XpCheckSkill(SkillType.ACROBATICS, defender);
				}
				event.setDamage(event.getDamage() / 2);
				//Needs to deal minimal damage
				if(event.getDamage() <= 0)
					event.setDamage(1);
			}
		}
	}
	
}
