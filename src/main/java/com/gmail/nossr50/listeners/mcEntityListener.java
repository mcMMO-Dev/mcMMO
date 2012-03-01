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

import org.bukkit.Location;
import org.bukkit.entity.Entity;
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
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
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
    public void onEntityDamage(EntityDamageEvent event) 
    {
    	Entity entity = event.getEntity();
    	DamageCause cause = event.getCause();
    	
    	//Check for world pvp flag
    	if(event instanceof EntityDamageByEntityEvent)
    	{
    		EntityDamageByEntityEvent eventb = (EntityDamageByEntityEvent)event;
    		if(eventb.getEntity() instanceof Player && eventb.getDamager() instanceof Player && !entity.getWorld().getPVP())
    			return;
    	}
    	
    	/*
    	 * CHECK FOR INVULNERABILITY
    	 */
    	if(entity instanceof Player)
    	{
    		Player defender = (Player)event.getEntity();
    		PlayerProfile PPd = Users.getProfile(defender);
    		if(defender != null && PPd.getGodMode())
    			event.setCancelled(true);
    		if(PPd == null)
    			Users.addUser(defender);
    	}
    	
    	if(entity instanceof LivingEntity)
    	{
	    	{
		    	LivingEntity entityliving = (LivingEntity)entity;
		    	if(entityliving.getNoDamageTicks() < entityliving.getMaximumNoDamageTicks()/2.0F)
		    	{
			    	if(entity instanceof Wolf && ((Wolf)entity).isTamed() && Taming.getOwner(((Wolf)entity), plugin) != null)
			    		Taming.preventDamage(event, plugin);
			    	
			    	if(entity instanceof Player){
				    	Player player = (Player)entity;
				    	/*
				    	 * ACROBATICS
				    	 */
				    	if(cause == DamageCause.FALL && mcPermissions.getInstance().acrobatics(player))
				    		Acrobatics.acrobaticsCheck(player, event);
				    	/*
				    	 * Demolitions Expert
				    	 */
		    			if(cause == DamageCause.BLOCK_EXPLOSION && mcPermissions.getInstance().blastMining(player))
		    				BlastMining.demolitionsExpertise(Users.getProfile(player).getSkillLevel(SkillType.MINING), event);
			    	}
			    	
			    	/*
			    	 * Entity Damage by Entity checks
			    	 */
			    	if(event instanceof EntityDamageByEntityEvent && !event.isCancelled())
			    	{
			    		EntityDamageByEntityEvent eventb = (EntityDamageByEntityEvent) event;
			    		Entity f = eventb.getDamager();
			    		/*
			    		 * PARTY CHECKS
			    		 */
			    		if(entity instanceof Player && f instanceof Player)
			    		{
			        		Player defender = (Player)entity;
			        		Player attacker = (Player)f;
			        		if(Party.getInstance().inSameParty(defender, attacker))
			        			event.setCancelled(true);
			    		}
			    		Combat.combatChecks(eventb, plugin);	
			        }
			    	/*
			    	 * Check to see if the defender took damage so we can apply recently hurt
			    	 */
			    	if(entity instanceof Player)
			    	{
			    		Player herpderp = (Player)entity;
			    		if(!event.isCancelled() && event.getDamage() >= 1)
			    		{
			    			Users.getProfile(herpderp).setRecentlyHurt(System.currentTimeMillis());
			    		}
			    	}
		    	}
	    	}
    	}
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) 
    {
    	LivingEntity x = event.getEntity();
    	x.setFireTicks(0);
    	
    	//Remove mob from mob spawner list
    	if(plugin.misc.mobSpawnerList.contains(x.getEntityId()))
    	{
    	    plugin.misc.mobSpawnerList.remove((Object)x.getEntityId());
    	}
    	
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
		if(event.getEntity() instanceof TNTPrimed)
		{
			Location location = event.getEntity().getLocation();
			System.out.println("TNT Primed.");
			
			//Ugly code to make it recognize the location
			location.setX(location.getBlockX()+1);
			location.setY(location.getBlockY());
			location.setZ(location.getBlockZ()+1);
			System.out.println(location.toString());
			
			if(plugin.misc.tntTracker.get(location) != null)
			{
				System.out.println("Being Tracked.");
				Player player = plugin.misc.tntTracker.get(location);
				
				BlastMining.biggerBombs(Users.getProfile(player).getSkillLevel(SkillType.MINING), event);
			}
		}		
	}
		
	@EventHandler (priority = EventPriority.LOW)
	public void onEnitityExplode(EntityExplodeEvent event)
	{
		if(event.getEntity() instanceof TNTPrimed)
		{
			Location location = event.getEntity().getLocation();
			System.out.println("TNT Explode.");
			
			//Ugly code to make it recognize the location
			location.setX(location.getBlockX()+1);
			location.setY(location.getBlockY());
			location.setZ(location.getBlockZ()+1);
			System.out.println(location.toString());

			if(plugin.misc.tntTracker.get(location) != null)
			{
				System.out.println("Being Tracked.");
				Player player = plugin.misc.tntTracker.get(location);
				BlastMining.dropProcessing(Users.getProfile(player).getSkillLevel(SkillType.MINING), event, plugin);
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
					int food = player.getItemInHand().getTypeId();
					int herbLevel = PP.getSkillLevel(SkillType.HERBALISM);
					int foodChange = newFoodLevel - currentFoodLevel;
					
					switch(food)
					{
					case 297:
					{
					    //BREAD (297) RESTORES 2 1/2 HUNGER
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
					case 357:
					{
					    //COOKIE (357) RESTORES 1/2 HUNGER
					    //RESTORES 2 HUNGER @ 1000
					    if(herbLevel >= 200 && herbLevel < 600)
                            foodChange = foodChange + 1;
                        else if(herbLevel >= 600 && herbLevel < 1000)
                            foodChange = foodChange + 2;
                        else if(herbLevel >= 1000)
                            foodChange = foodChange + 3;
                        break;
					}
					case 360:
					{
					    //MELON (360) RESTORES  1 HUNGER
					    //RESTORES 2 1/2 HUNGER @ 1000
                        if(herbLevel >= 200 && herbLevel < 600)
                            foodChange = foodChange + 1;
                        else if(herbLevel >= 600 && herbLevel < 1000)
                            foodChange = foodChange + 2;
                        else if(herbLevel >= 1000)
                            foodChange = foodChange + 3;
                        break;  
					}
					case 282:
					{
					    //STEW (282) RESTORES 4 HUNGER
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
	        if(event.getEntity() instanceof Wolf)
	        {
	        	PP.addXP(SkillType.TAMING, LoadProperties.mtameWolf, player);
	        	Skills.XpCheckSkill(SkillType.TAMING, player);
	        }
	    }
	}
}
