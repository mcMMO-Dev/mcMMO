package com.gmail.nossr50;
import java.util.TimerTask;

import org.bukkit.entity.*;

import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.skills.Swords;


public class mcTimer extends TimerTask
{
	private final mcMMO plugin;
	int thecount = 1;

    public mcTimer(final mcMMO plugin) 
    {
        this.plugin = plugin;
    }
    
	public void run() 
	{
		long before = System.currentTimeMillis();
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
			 * MANA MONITORING
			 */
			/*
			if(mcPermissions.getInstance().sorcery(player) && thecount == 20 && PP.getCurrentMana() < PP.getMaxMana())
			{
				PP.setMana(PP.getCurrentMana()+PP.getMaxMana()/5);
				
				//MAKE SURE THE MANA IS NOT ABOVE MAXIMUM
				if(PP.getMaxMana() < PP.getCurrentMana())
					PP.setMana(PP.getMaxMana());
				
				if(PP.getMaxMana() != PP.getCurrentMana())
					player.sendMessage(Messages.getString("Sorcery.Current_Mana")+" "+ChatColor.GREEN+PP.getCurrentMana()+"/"+PP.getMaxMana());
			}
			*/
			
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
				if(thecount == 10 || thecount == 20 || thecount == 30 || thecount == 40){
				    if(player != null &&
				    	player.getHealth() > 0 && player.getHealth() < 20 
				    	&& m.getPowerLevel(player) >= 1000){
				    	player.setHealth(m.calculateHealth(player.getHealth(), 1));
				    }
				}
				if(thecount == 20 || thecount == 40){
			   		if(player != null &&
			   			player.getHealth() > 0 && player.getHealth() < 20 
			    		&& m.getPowerLevel(player) >= 500 
			    		&& m.getPowerLevel(player) < 1000){
			    		player.setHealth(m.calculateHealth(player.getHealth(), 1));
			    	}
				}
				if(thecount == 40)
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
		if(thecount >= 41)
			thecount = 1;
		
		
		if(LoadProperties.print_reports)
		{
			long after = System.currentTimeMillis();
			plugin.mcTimerx+=(after-before);
			
			if(thecount == 40)
				plugin.printDelays();
		}
	}
}
