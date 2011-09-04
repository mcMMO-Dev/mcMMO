package com.gmail.nossr50;
import org.bukkit.entity.*;

import com.gmail.nossr50.config.LoadProperties;
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
			
			if(LoadProperties.enableRegen && mcPermissions.getInstance().regeneration(player) && System.currentTimeMillis() >= PP.getRecentlyHurt() + 60000)
			{
				if(thecount == 20 || thecount == 40 || thecount == 60 || thecount == 80){
				    if(player != null &&
				    	player.getHealth() > 0 && player.getHealth() < 20 
				    	&& m.getPowerLevel(player) >= 1000){
				    	player.setHealth(m.calculateHealth(player.getHealth(), 1));
				    }
				}
				if(thecount == 40 || thecount == 80){
			   		if(player != null &&
			   			player.getHealth() > 0 && player.getHealth() < 20 
			    		&& m.getPowerLevel(player) >= 500 
			    		&& m.getPowerLevel(player) < 1000){
			    		player.setHealth(m.calculateHealth(player.getHealth(), 1));
			    	}
				}
				if(thecount == 80)
				{
			    	if(player != null &&
			    		player.getHealth() > 0 && player.getHealth() < 20  
			    		&& m.getPowerLevel(player) < 500){
			    		player.setHealth(m.calculateHealth(player.getHealth(), 1));
			    	}
				}
			}
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
