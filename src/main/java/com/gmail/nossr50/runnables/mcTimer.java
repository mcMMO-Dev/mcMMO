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
package com.gmail.nossr50.runnables;
import org.bukkit.entity.*;

import com.gmail.nossr50.Combat;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.skills.Swords;


public class mcTimer implements Runnable
{
	private final mcMMO plugin;
	int thecount = 1;

    public mcTimer(final mcMMO plugin) 
    {
        this.plugin = plugin;
    }
    
	public void run() 
	{
		for(Player player : plugin.getServer().getOnlinePlayers())
		{
			if(player == null)
				continue;
			PlayerProfile PP = Users.getProfile(player);
			
			if(PP == null)
				continue;
			
			/*
			 * MONITOR SKILLS
			 */
			Skills.monitorSkills(player);
			
			/*
			 * COOLDOWN MONITORING
			 */
			Skills.watchCooldowns(player);
			
			/*
			 * PLAYER BLEED MONITORING
			 */
			if(thecount % 2 == 0 && PP.getBleedTicks() >= 1)
			{
        		Combat.dealDamage(player, 2);
        		PP.decreaseBleedTicks();
        	}
		
			/*
			 * NON-PLAYER BLEED MONITORING
			 */
			
			if(thecount % 2 == 0)
				Swords.bleedSimulate(plugin);
			
			//SETUP FOR HP REGEN/BLEED
			thecount++;
			if(thecount >= 81)
				thecount = 1;
		}
	}
}
