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

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.Combat;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.skills.Acrobatics;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.skills.Taming;


public class mcEntityListener implements Listener 
{
	private final mcMMO plugin;

    public mcEntityListener(final mcMMO plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) 
    {
    	if(event.isCancelled())
    		return;
    	
    	//Check for world pvp flag
    	if(event instanceof EntityDamageByEntityEvent)
    	{
    		EntityDamageByEntityEvent eventb = (EntityDamageByEntityEvent)event;
    		if(eventb.getEntity() instanceof Player && eventb.getDamager() instanceof Player && !event.getEntity().getWorld().getPVP())
    			return;
    	}
    	
    	/*
    	 * CHECK FOR INVULNERABILITY
    	 */
    	if(event.getEntity() instanceof Player)
    	{
    		Player defender = (Player)event.getEntity();
    		PlayerProfile PPd = Users.getProfile(defender);
    		if(defender != null && PPd.getGodMode())
    			event.setCancelled(true);
    		if(PPd == null)
    			Users.addUser(defender);
    	}
    	
    	/*
    	 * Demolitions Expert
    	 
    	if(event.getCause() == DamageCause.BLOCK_EXPLOSION)
    	{
    		if(event.getEntity() instanceof Player)
    		{
    			Player player = (Player)event.getEntity();
    			BlastMining.demolitionsExpertise(player, event);
    		}
    	}
    	*/
    	
    	if(event.getEntity() instanceof LivingEntity)
    	{
    		//CraftEntity cEntity = (CraftEntity)event.getEntity();
	    	//if(cEntity.getHandle() instanceof EntityLiving)
	    	{
		    	LivingEntity entityliving = (LivingEntity)event.getEntity();
		    	if(entityliving.getNoDamageTicks() < entityliving.getMaximumNoDamageTicks()/2.0F)
		    	{
			    	Entity x = event.getEntity();
			    	DamageCause type = event.getCause();
			    	if(event.getEntity() instanceof Wolf && ((Wolf)event.getEntity()).isTamed() && Taming.getOwner(((Wolf)event.getEntity()), plugin) != null)
			    	{
			    		Wolf theWolf = (Wolf) event.getEntity();
				    	Player master = Taming.getOwner(theWolf, plugin);
				    	PlayerProfile PPo = Users.getProfile(master);
				    	if(master == null || PPo == null)
				    		return;
			    		//Environmentally Aware
						if((event.getCause() == DamageCause.CONTACT || event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.FIRE) && PPo.getSkillLevel(SkillType.TAMING) >= 100)
						{
							if(event.getDamage() < ((Wolf) event.getEntity()).getHealth())
							{
								event.getEntity().teleport(Taming.getOwner(theWolf, plugin).getLocation());
								master.sendMessage(mcLocale.getString("mcEntityListener.WolfComesBack")); //$NON-NLS-1$
								event.getEntity().setFireTicks(0);
							}
						}
						if(event.getCause() == DamageCause.FALL && PPo.getSkillLevel(SkillType.TAMING) >= 100)
						{
							event.setCancelled(true);
						}
						
						//Thick Fur
						if(event.getCause() == DamageCause.FIRE_TICK)
						{
							event.getEntity().setFireTicks(0);
						}
			    	}
			    	
			    	/*
			    	 * ACROBATICS
			    	 */
			    	if(x instanceof Player){
				    	Player player = (Player)x;
				    	if(type == DamageCause.FALL){
				    		Acrobatics.acrobaticsCheck(player, event);
				    	}
			    	}
			    	
			    	/*
			    	 * Entity Damage by Entity checks
			    	 */
			    	if(event instanceof EntityDamageByEntityEvent && !event.isCancelled())
			    	{
			    		EntityDamageByEntityEvent eventb = (EntityDamageByEntityEvent) event;
			    		Entity f = eventb.getDamager();
			    		Entity e = event.getEntity();
			    		/*
			    		 * PARTY CHECKS
			    		 */
			    		if(e instanceof Player && f instanceof Player)
			    		{
			        		Player defender = (Player)e;
			        		Player attacker = (Player)f;
			        		if(Party.getInstance().inSameParty(defender, attacker))
			        			event.setCancelled(true);
			    		}
			    		Combat.combatChecks(event, plugin);	
			        }
			    	/*
			    	 * Check to see if the defender took damage so we can apply recently hurt
			    	 */
			    	if(event.getEntity() instanceof Player)
			    	{
			    		Player herpderp = (Player)event.getEntity();
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
    	Entity x = event.getEntity();
    	x.setFireTicks(0);
    	
    	//Remove bleed track
    	if(plugin.misc.bleedTracker.contains((LivingEntity)x))
    		plugin.misc.addToBleedRemovalQue((LivingEntity)x);
    	
		Skills.arrowRetrievalCheck(x, plugin);
		/*
		if(Config.getInstance().isMobSpawnTracked(x)){
			Config.getInstance().removeMobSpawnTrack(x);
		}
		*/
    	if(x instanceof Player){
    		Player player = (Player)x;
    		Users.getProfile(player).setBleedTicks(0);
    	}
    	
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) 
    {
    	SpawnReason reason = event.getSpawnReason();
    	
    	if(reason == SpawnReason.SPAWNER && !LoadProperties.xpGainsMobSpawners)
    	{
    		plugin.misc.mobSpawnerList.add(event.getEntity());
    	}
    }
    
    /*
	@EventHandler (priority = EventPriority.LOW)
	public void onExplosionPrime(ExplosionPrimeEvent event)
	{
		if(event.getEntity() instanceof TNTPrimed)
		{
			Block block = event.getEntity().getLocation().getBlock();
			
			if(plugin.misc.tntTracker.get(block) != null)
			{
				int skillLevel = plugin.misc.tntTracker.get(block);
				BlastMining.biggerBombs(skillLevel, event);
			}
		}		
	}
	
	
	@EventHandler (priority = EventPriority.LOW)
	public void onEnitityExplode(EntityExplodeEvent event)
	{
		if(event.getEntity() instanceof TNTPrimed)
		{
			Block block = event.getLocation().getBlock();

			if(plugin.misc.tntTracker.get(block) != null)
			{
				int skillLevel = plugin.misc.tntTracker.get(block);
				BlastMining.dropProcessing(skillLevel, event, plugin);
			}
		}
	}
	*/
    
	public boolean isBow(ItemStack is){
		if (is.getTypeId() == 261){
			return true;
		} else {
			return false;
		}
	}
	public boolean isPlayer(Entity entity){
    	if (entity instanceof Player) {
    	    return true;
    	} else{
    		return false;
    	}
    }
}
