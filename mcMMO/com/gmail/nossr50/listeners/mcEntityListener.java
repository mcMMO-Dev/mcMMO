package com.gmail.nossr50.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.Combat;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.skills.Acrobatics;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.skills.Taming;


public class mcEntityListener extends EntityListener 
{
	private final mcMMO plugin;

    public mcEntityListener(final mcMMO plugin) {
        this.plugin = plugin;
    }
    
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
						if((event.getCause() == DamageCause.CONTACT || event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.FIRE) && PPo.getSkill("taming") >= 100)
						{
							if(event.getDamage() < ((Wolf) event.getEntity()).getHealth())
							{
								event.getEntity().teleport(Taming.getOwner(theWolf, plugin).getLocation());
								master.sendMessage(mcLocale.getString("mcEntityListener.WolfComesBack")); //$NON-NLS-1$
								event.getEntity().setFireTicks(0);
							}
						}
						if(event.getCause() == DamageCause.FALL && PPo.getSkill("taming") >= 100)
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
			    		if(event.getEntity() instanceof Player && f instanceof Player)
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
			    	if(event.getEntity() instanceof Player && !event.isCancelled() && event.getDamage() >= 1)
			    	{
			    		Player herpderp = (Player)event.getEntity();
			    		Users.getProfile(herpderp).setRecentlyHurt(System.currentTimeMillis());
			    	}
		    	}
	    	}
    	}
    }
    
    public void onEntityDeath(EntityDeathEvent event) 
    {
    	
    	Entity x = event.getEntity();
    	x.setFireTicks(0);
    	
    	//cleanup mob diff
    	if(plugin.mob.mobDiff.containsKey(event.getEntity().getEntityId()))
    			plugin.mob.mobDiff.remove(event.getEntity().getEntityId());
    	
    	
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
    
    public void onCreatureSpawn(CreatureSpawnEvent event) 
    {
    	
    	SpawnReason reason = event.getSpawnReason();
    	
    	if(reason == SpawnReason.SPAWNER && !LoadProperties.xpGainsMobSpawners)
    	{
    		plugin.misc.mobSpawnerList.add(event.getEntity());
    	} else 
    	{
    		if(event.getEntity() instanceof Monster && !plugin.mob.mobDiff.containsKey(event.getEntity().getEntityId()))
        		plugin.mob.assignDifficulty(event.getEntity());
    	}
    }
    
    public void onEntityTarget(EntityTargetEvent event) 
	{
    	
		int type = event.getEntity().getEntityId();
		//Make 3+ non-aggressive
		if(event.getEntity() instanceof Monster 
				&& plugin.mob.mobDiff.containsKey(type)
				&& plugin.mob.isAggressive.containsKey(type))
		{
			if(plugin.mob.mobDiff.get(type) >= 2 && plugin.mob.isAggressive.get(type) == false)
			{
				event.setCancelled(true);
				event.setTarget(null);
			}
		}
	}
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
