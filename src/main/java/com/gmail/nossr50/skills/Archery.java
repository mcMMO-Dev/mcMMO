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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;

public class Archery 
{
	public static void trackArrows(mcMMO pluginx, Entity x, EntityDamageByEntityEvent event, Player attacker)
	{
		PlayerProfile PPa = Users.getProfile(attacker);
		if(!pluginx.misc.arrowTracker.containsKey(x) && event.getDamage() > 0)
		{
			pluginx.misc.arrowTracker.put(x, 0);
			if(attacker != null)
			{
				if( (Math.random() * 1000 <= PPa.getSkillLevel(SkillType.ARCHERY)) || PPa.getSkillLevel(SkillType.ARCHERY) > 1000)
				{
					pluginx.misc.arrowTracker.put(x, 1);
				}
			}
		} else 
		{
			if(event.getDamage() > 0)
			{
				if(attacker != null)
				{
					if( (Math.random() * 1000 <= PPa.getSkillLevel(SkillType.ARCHERY)) || PPa.getSkillLevel(SkillType.ARCHERY) > 1000)
					{
						pluginx.misc.arrowTracker.put(x, 1);
					}
				}
			}
		}
	}
	public static void ignitionCheck(Entity x, EntityDamageByEntityEvent event, Player attacker)
	{
		//Check to see if PVP for this world is disabled before executing
		if(!x.getWorld().getPVP())
			return;
		
		PlayerProfile PPa = Users.getProfile(attacker);
		if(Math.random() * 100 >= 75)
		{
			
			int ignition = 20;	
			if(PPa.getSkillLevel(SkillType.ARCHERY) >= 200)
				ignition+=20;
			if(PPa.getSkillLevel(SkillType.ARCHERY) >= 400)
				ignition+=20;
			if(PPa.getSkillLevel(SkillType.ARCHERY) >= 600)
				ignition+=20;
			if(PPa.getSkillLevel(SkillType.ARCHERY) >= 800)
				ignition+=20;
			if(PPa.getSkillLevel(SkillType.ARCHERY) >= 1000)
				ignition+=20;
			
			if(x instanceof Player)
			{
				Player Defender = (Player)x;
				if(!Party.getInstance().inSameParty(attacker, Defender))
				{
					event.getEntity().setFireTicks(ignition);
					attacker.sendMessage(mcLocale.getString("Combat.Ignition")); //$NON-NLS-1$
					Defender.sendMessage(mcLocale.getString("Combat.BurningArrowHit")); //$NON-NLS-1$
				}
			} else {
			event.getEntity().setFireTicks(ignition);
			attacker.sendMessage(mcLocale.getString("Combat.Ignition")); //$NON-NLS-1$
			}
		}
	}
	public static void dazeCheck(Player defender, Player attacker)
	{
		PlayerProfile PPa = Users.getProfile(attacker);
		
		Location loc = defender.getLocation();
		if(Math.random() * 10 > 5)
		{
		loc.setPitch(90);
		} else {
			loc.setPitch(-90);
		}
		if(PPa.getSkillLevel(SkillType.ARCHERY) >= 1000){
			if(Math.random() * 1000 <= 500){
				defender.teleport(loc);
				defender.sendMessage(mcLocale.getString("Combat.TouchedFuzzy")); //$NON-NLS-1$
				attacker.sendMessage(mcLocale.getString("Combat.TargetDazed")); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else if(Math.random() * 2000 <= PPa.getSkillLevel(SkillType.ARCHERY)){
			defender.teleport(loc);
			defender.sendMessage(mcLocale.getString("Combat.TouchedFuzzy")); //$NON-NLS-1$
			attacker.sendMessage(mcLocale.getString("Combat.TargetDazed")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
