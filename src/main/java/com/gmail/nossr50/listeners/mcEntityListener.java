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
package com.gmail.nossr50.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import com.gmail.nossr50.Combat;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.events.FakeEntityDamageByEntityEvent;
import com.gmail.nossr50.events.FakeEntityDamageEvent;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.skills.Acrobatics;
import com.gmail.nossr50.skills.Archery;
import com.gmail.nossr50.skills.BlastMining;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.skills.Taming;

public class mcEntityListener implements Listener 
{
	private final mcMMO plugin;

    public mcEntityListener(final mcMMO plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
    {
        //Lets just put this here...
        if(event instanceof FakeEntityDamageByEntityEvent)
            return;
        
    	Entity defender = event.getEntity();
		Entity attacker = event.getDamager();
		
		if(attacker instanceof Player && defender instanceof Player)
		{
			if(!defender.getWorld().getPVP())
				return;
			if(Party.getInstance().inSameParty((Player)defender, (Player)attacker))
				event.setCancelled(true);
		}
		
		//Make sure defender is not invincible
		if(defender instanceof LivingEntity)
		{
		    LivingEntity livingDefender = (LivingEntity)defender;
		    if(!m.isInvincible(livingDefender, event))
		        Combat.combatChecks(event, plugin);
		}
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) 
    {
        if(event instanceof FakeEntityDamageEvent)
            return;
        
    	Entity entity = event.getEntity();
    	EntityType type = entity.getType();
    	DamageCause cause = event.getCause();
    	
    	switch(type)
    	{
    	case PLAYER:
    		/*
    		 * CHECK FOR INVULNERABILITY
    		 */
    		Player player = (Player) entity;
    		PlayerProfile PP = Users.getProfile(player);
    		if(PP.getGodMode())
    			event.setCancelled(true);
    		
    		if(!m.isInvincible(player, event))
    		{
    			if(cause == DamageCause.FALL && mcPermissions.getInstance().acrobatics(player))
    				Acrobatics.acrobaticsCheck(player, event);
    			if(cause == DamageCause.BLOCK_EXPLOSION && mcPermissions.getInstance().blastMining(player))
    				BlastMining.demolitionsExpertise(player, event);
    			if(event.getDamage() >= 1)
    				PP.setRecentlyHurt(System.currentTimeMillis());
    		}
    		break;
    	case WOLF:
    		Wolf wolf = (Wolf) entity;
    		if((!m.isInvincible(wolf, event)) && wolf.isTamed() && (wolf.getOwner() instanceof Player))
    			Taming.preventDamage(event, plugin);
    		break;
    	}
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) 
    {
    	LivingEntity x = event.getEntity();
    	x.setFireTicks(0);
    	
    	//Remove mob from mob spawner list
    	if(plugin.misc.mobSpawnerList.contains(x.getEntityId()))
    	    plugin.misc.mobSpawnerList.remove((Object)x.getEntityId());
    	
    	//Remove bleed track
    	if(plugin.misc.bleedTracker.contains(x))
    		plugin.misc.addToBleedRemovalQue(x);
    	
		Archery.arrowRetrievalCheck(x, plugin);

    	if(x instanceof Player)
    		Users.getProfile((Player)x).setBleedTicks(0);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) 
    {
    	SpawnReason reason = event.getSpawnReason();
    	
    	if(reason == SpawnReason.SPAWNER && !LoadProperties.xpGainsMobSpawners)
    		plugin.misc.mobSpawnerList.add(event.getEntity().getEntityId());
    }
    
	@EventHandler (priority = EventPriority.LOW)
	public void onExplosionPrime(ExplosionPrimeEvent event)
	{
		Entity entity = event.getEntity();
		if(entity instanceof TNTPrimed)
		{
			int id = entity.getEntityId();
			if(plugin.misc.tntTracker.containsKey(id))
			{
				Player player = plugin.misc.tntTracker.get(id);
				BlastMining.biggerBombs(player, event);
			}
		}		
	}
		
	@EventHandler (priority = EventPriority.LOW)
	public void onEnitityExplode(EntityExplodeEvent event)
	{
		Entity entity = event.getEntity();
		if(event.getEntity() instanceof TNTPrimed)
		{
			int id = entity.getEntityId();
			if(plugin.misc.tntTracker.containsKey(id))
			{
				Player player = plugin.misc.tntTracker.get(id);
				BlastMining.dropProcessing(player, event, plugin);
				plugin.misc.tntTracker.remove(id);
			}
		}
	}
    
	@EventHandler (priority = EventPriority.LOW)
	public void onFoodLevelChange(FoodLevelChangeEvent event)
	{
		if(LoadProperties.herbalismHungerBonus)
		{
			if(event.getEntity() instanceof Player)
			{
				Player player = (Player) event.getEntity();
				PlayerProfile PP = Users.getProfile(player);
				int currentFoodLevel = player.getFoodLevel();
				int newFoodLevel = event.getFoodLevel();
				
				//Some foods have 3 ranks
				//Some foods have 5 ranks
				//The number of ranks is based on how 'common' the item is
				//We can adjust this quite easily if we find something is giving too much of a bonus
				
				if(newFoodLevel > currentFoodLevel)
				{
					Material food = player.getItemInHand().getType();
					int herbLevel = PP.getSkillLevel(SkillType.HERBALISM);
					int foodChange = newFoodLevel - currentFoodLevel;
					
					switch(food)
					{
					case BREAD:
					{
					    //BREAD RESTORES 2 1/2 HUNGER
					    //Restores 5 HUNGER @ 1000
					    if(herbLevel >= 200 && herbLevel < 400)
                            foodChange = foodChange + 1;
					    else if(herbLevel >= 400 && herbLevel < 600)
                            foodChange = foodChange + 2;
					    else if(herbLevel >= 600 && herbLevel < 800)
                            foodChange = foodChange + 3;
					    else if(herbLevel >= 800 && herbLevel < 1000)
                            foodChange = foodChange + 4;
					    else if(herbLevel >= 1000)
                            foodChange = foodChange + 5;
                        break;
					}
					case COOKIE:
					{
					    //COOKIE RESTORES 1/2 HUNGER
					    //RESTORES 2 HUNGER @ 1000
					    if(herbLevel >= 200 && herbLevel < 600)
                            foodChange = foodChange + 1;
                        else if(herbLevel >= 600 && herbLevel < 1000)
                            foodChange = foodChange + 2;
                        else if(herbLevel >= 1000)
                            foodChange = foodChange + 3;
                        break;
					}
					case MELON:
					{
					    //MELON RESTORES  1 HUNGER
					    //RESTORES 2 1/2 HUNGER @ 1000
                        if(herbLevel >= 200 && herbLevel < 600)
                            foodChange = foodChange + 1;
                        else if(herbLevel >= 600 && herbLevel < 1000)
                            foodChange = foodChange + 2;
                        else if(herbLevel >= 1000)
                            foodChange = foodChange + 3;
                        break;  
					}
					case MUSHROOM_SOUP:
					{
					    //MUSHROOM SOUP RESTORES 4 HUNGER
					    //RESTORES 6 1/2 HUNGER @ 1000
					    if(herbLevel >= 200 && herbLevel < 400)
                            foodChange = foodChange + 1;
                        else if(herbLevel >= 400 && herbLevel < 600)
                            foodChange = foodChange + 2;
                        else if(herbLevel >= 600 && herbLevel < 800)
                            foodChange = foodChange + 3;
                        else if(herbLevel >= 800 && herbLevel < 1000)
                            foodChange = foodChange + 4;
                        else if(herbLevel >= 1000)
                            foodChange = foodChange + 5;
                        break;
					}
					}
					
					//Make sure we don't go over the max value
					newFoodLevel = currentFoodLevel + foodChange;
                    if(newFoodLevel > 20)
                        event.setFoodLevel(20);
                    if(newFoodLevel <= 20)
                        event.setFoodLevel(newFoodLevel);
				}
			}
		}
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onEntityTame(EntityTameEvent event)
	{
		Player player = (Player) event.getOwner();
	    if(mcPermissions.getInstance().taming(player))
	    {
	        PlayerProfile PP = Users.getProfile(player);
	        EntityType type = event.getEntityType();
	        int xp = 0;
	        
	        switch(type)
	        {
	        case WOLF:
	        	xp = LoadProperties.mtameWolf;
	        	break;
	        case OCELOT:
	        	xp = LoadProperties.mtameOcelot;
	        	break;
	        }
	        
	        PP.addXP(SkillType.TAMING, xp, player);
	        Skills.XpCheckSkill(SkillType.TAMING, player);
	    }
	}
}
