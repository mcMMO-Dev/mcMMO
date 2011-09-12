package com.gmail.nossr50.runnables;
import org.bukkit.entity.*;

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
        		player.damage(2);
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
