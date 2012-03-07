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

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import com.gmail.nossr50.Combat;
import com.gmail.nossr50.ItemChecks;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;

public class Swords 
{
	public static void bleedCheck(Player attacker, LivingEntity x, mcMMO pluginx)
	{
    	PlayerProfile PPa = Users.getProfile(attacker);
    	
    	if(x instanceof Wolf)
    	{
    		Wolf wolf = (Wolf)x;
    		if(wolf.getOwner() instanceof Player)
    		{
    			Player owner = (Player) wolf.getOwner();
	    		if(owner == attacker)
	    			return;
	    		if(Party.getInstance().inSameParty(attacker, owner))
	    			return;
    		}
    	}
    	if(mcPermissions.getInstance().swords(attacker) && ItemChecks.isSword(attacker.getItemInHand())){
			if(PPa.getSkillLevel(SkillType.SWORDS) >= 750)
			{
				if(Math.random() * 1000 <= 750)
				{
					if(!(x instanceof Player))
						pluginx.misc.addToBleedQue(x);
					if(x instanceof Player)
					{
						Player target = (Player)x;
						Users.getProfile(target).addBleedTicks(3);
					}
					attacker.sendMessage(mcLocale.getString("Swords.EnemyBleeding"));
				}
			} 
			else if (Math.random() * 1000 <= PPa.getSkillLevel(SkillType.SWORDS))
			{
				if(!(x instanceof Player))
					pluginx.misc.addToBleedQue(x);
				if(x instanceof Player)
				{
					Player target = (Player)x;
					Users.getProfile(target).addBleedTicks(2);
				}
				attacker.sendMessage(mcLocale.getString("Swords.EnemyBleeding"));
			}
		}
    }
    
    public static void counterAttackChecks(EntityDamageByEntityEvent event)
    {
    	//Don't want to counter attack stuff not alive
    	
    	if(!(event.getDamager() instanceof LivingEntity))
    		return;

	    if(event instanceof EntityDamageByEntityEvent)
	    {
	    	Entity f = ((EntityDamageByEntityEvent) event).getDamager();
		   	if(event.getEntity() instanceof Player)
		   	{
		   		Player defender = (Player)event.getEntity();
		   		PlayerProfile PPd = Users.getProfile(defender);
		   		if(ItemChecks.isSword(defender.getItemInHand()) && mcPermissions.getInstance().swords(defender))
		   		{
		    		if(PPd.getSkillLevel(SkillType.SWORDS) >= 600)
		    		{
		    			if(Math.random() * 2000 <= 600)
		    			{
			    			Combat.dealDamage((LivingEntity) f, event.getDamage() / 2);
			    			defender.sendMessage(mcLocale.getString("Swords.CounterAttacked"));
			    			if(f instanceof Player)
		    				((Player) f).sendMessage(mcLocale.getString("Swords.HitByCounterAttack"));
		    			}
		    		}
		    		else if (Math.random() * 2000 <= PPd.getSkillLevel(SkillType.SWORDS))
		    		{
			    		Combat.dealDamage((LivingEntity) f, event.getDamage() / 2);
			    		defender.sendMessage(mcLocale.getString("Swords.CounterAttacked"));
		    			if(f instanceof Player)
		    				((Player) f).sendMessage(mcLocale.getString("Swords.HitByCounterAttack"));
		    		}
		   		}
		    }
    	}
    }
    
    public static void bleedSimulate(mcMMO plugin)
    {
    	//Add items from Que list to BleedTrack list
    	
    	for(LivingEntity x : plugin.misc.bleedQue)
    	{
    		plugin.misc.bleedTracker.add(x);
    	}
    	
    	//Clear list
    	plugin.misc.bleedQue = new LivingEntity[plugin.misc.bleedQue.length];
    	plugin.misc.bleedQuePos = 0;
    	
    	//Cleanup any dead entities from the list
    	for(LivingEntity x : plugin.misc.bleedRemovalQue)
    	{
    		plugin.misc.bleedTracker.remove(x);
    	}
    	
    	//Clear bleed removal list
    	plugin.misc.bleedRemovalQue = new LivingEntity[plugin.misc.bleedRemovalQue.length];
    	plugin.misc.bleedRemovalQuePos = 0;
    	
    	//Bleed monsters/animals
        for(LivingEntity x : plugin.misc.bleedTracker)
        {
        	if(x == null || x.isDead())
        	{
        		plugin.misc.addToBleedRemovalQue(x);
        		continue;
        	}
        	else
        	{
				Combat.dealDamage(x, 2);
        	}
        }
    }
}
