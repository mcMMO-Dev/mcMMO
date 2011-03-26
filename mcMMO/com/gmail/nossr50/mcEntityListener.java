package com.gmail.nossr50;

import net.minecraft.server.EntityLiving;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class mcEntityListener extends EntityListener {
	private final mcMMO plugin;

    public mcEntityListener(final mcMMO plugin) {
        this.plugin = plugin;
    }
    public boolean isBow(ItemStack is){
    	if (is.getTypeId() == 261){
    		return true;
    	} else {
    		return false;
    	}
    }
    public void onCreatureSpawn(CreatureSpawnEvent event) {
    	Location loc = event.getLocation();
    	Entity spawnee = event.getEntity();
    	if(mcm.getInstance().isBlockAround(loc, 5, 52)){
    		mcConfig.getInstance().addMobSpawnTrack(spawnee);
    	}
    }
    public void onEntityDamage(EntityDamageEvent event) {
    	/*
    	 * CHECK FOR INVULNERABILITY
    	 */
    	if(event.getEntity() instanceof CraftEntity){
    	CraftEntity cEntity = (CraftEntity)event.getEntity();
    	if(cEntity.getHandle() instanceof EntityLiving){
    	EntityLiving entityliving = (EntityLiving)cEntity.getHandle();
    	if(entityliving.noDamageTicks < entityliving.maxNoDamageTicks/2.0F){
    	Entity x = event.getEntity();
    	DamageCause type = event.getCause();
    	/*
    	 * ACROBATICS
    	 */
    	if(x instanceof Player){
    	Player player = (Player)x;
    	Location loc = player.getLocation();
    	int xx = loc.getBlockX();
    	int y = loc.getBlockY();
    	int z = loc.getBlockZ();
    	if(type == DamageCause.FALL){
    		mcAcrobatics.getInstance().acrobaticsCheck(player, event, loc, xx, y, z);
    		}
    	}
    	
    	/*
    	 * ARCHERY CHECKS
    	 */
    	if(event instanceof EntityDamageByProjectileEvent && event.getDamage() >= 1){
    		EntityDamageByProjectileEvent c = (EntityDamageByProjectileEvent)event;
    		mcCombat.getInstance().archeryCheck(c);
    	}
    	
    	/*
    	 * Entity Damage by Entity checks
    	 */
    	if(event instanceof EntityDamageByEntityEvent && event.getDamage() >= 1){
    		if(event.isCancelled()){
    			return;
    		}
    		EntityDamageByEntityEvent eventb = (EntityDamageByEntityEvent)event;
    		Entity e = eventb.getEntity(); //Defender
        	Entity f = eventb.getDamager(); //Attacker
        	/*
        	 * CHECK FOR PVP INTERACTIONS
        	 */
        	if(f instanceof Player && e instanceof Player && !mcLoadProperties.pvp)
        		event.setCancelled(true);
        	/*
        	 * IF DEFENDER IS PLAYER
        	 */
        	if(e instanceof Player){
        		Player defender = (Player)e;
        		if(defender != null && mcConfig.getInstance().isGodModeToggled(defender.getName()))
        			event.setCancelled(true);
        		/*
        		 * PARRYING CHECK, CHECK TO SEE IF ITS A SUCCESSFUL PARRY OR NOT
        		 */
        		mcCombat.getInstance().parryCheck(defender, eventb, f);
        	}
        	/*
        	 * IF ATTACKER IS PLAYER
        	 */
        	if(f instanceof Player){
        		//((Player) f).sendMessage("DEBUG: EntityDamageByEntity cast correctly!");
        		int typeid = ((Player) f).getItemInHand().getTypeId();
        		Player attacker = (Player)f;
        		if(mcUsers.getProfile(attacker).getAxePreparationMode())
        			mcSkills.getInstance().skullSplitterCheck(attacker);
        		if(mcUsers.getProfile(attacker).getSwordsPreparationMode())
        			mcSkills.getInstance().serratedStrikesActivationCheck(attacker);
        		if(mcUsers.getProfile(attacker).getFistsPreparationMode())
        			mcSkills.getInstance().berserkActivationCheck(attacker);
        		/*
        		 * BERSERK DAMAGE MODIFIER
        		 */
        		if(mcUsers.getProfile(attacker).getBerserkMode())
        			event.setDamage(event.getDamage() + (event.getDamage() / 2));
        		/*
        		 * Player versus Monster checks, this handles all skill damage modifiers and any procs.
        		 */
        		mcCombat.getInstance().playerVersusMonsterChecks(eventb, attacker, e, typeid);
        		/*
        		 * Player versus Squid checks, this handles all skill damage modifiers and any procs.
        		 */
        		mcCombat.getInstance().playerVersusSquidChecks(eventb, attacker, e, typeid);
        		/*
        		 * Player versus Player checks, these checks make sure players are not in the same party, etc. They also check for any procs from skills and handle damage modifiers.
        		 */
        		if(mcm.getInstance().isPvpEnabled())
        		mcCombat.getInstance().playerVersusPlayerChecks(e, attacker, eventb);
        		/*
        		 * Player versus Animals checks, these checks handle any skill modifiers or procs
        		 */
        		mcCombat.getInstance().playerVersusAnimalsChecks(e, attacker, eventb, typeid);
        		/*
        		 * This will do AOE damage from the axes ability
        		 */
        		if(!event.isCancelled() && mcUsers.getProfile(attacker).getSkullSplitterMode() && mcm.getInstance().isAxes(attacker.getItemInHand()))
            		mcCombat.getInstance().applyAoeDamage(attacker, eventb, x);
        		if(!event.isCancelled() && mcUsers.getProfile(attacker).getSerratedStrikesMode() && mcm.getInstance().isSwords(attacker.getItemInHand()))
            		mcCombat.getInstance().applyAoeDamage(attacker, eventb, x);
        	}
        	
        	if(e instanceof Monster || e instanceof Animals){
        		if(e instanceof Monster){
        			Monster monster = (Monster)e;
        			if(monster.getHealth() <= 0){
        				mcConfig.getInstance().removeBleedTrack(e);
        			}
        		}
        		if(e instanceof Animals){
        			Animals animals = (Animals)e;
        			if(animals.getHealth() <= 0){
        				mcConfig.getInstance().removeBleedTrack(e);
        			}
        		}
        	}
    	}
    	
    	/*
    	 * Check to see if the defender took damage so we can apply recently hurt
    	 */
    	if(x instanceof Player && !event.isCancelled()){
    		Player herpderp = (Player)x;
    		mcUsers.getProfile(herpderp).setRecentlyHurt(30);
    	}
    	}
    	}
    	}
    }
    public void onEntityDeath(EntityDeathEvent event) {
    	Entity x = event.getEntity();
		mcSkills.getInstance().arrowRetrievalCheck(x);
		if(mcConfig.getInstance().isMobSpawnTracked(x)){
			mcConfig.getInstance().removeMobSpawnTrack(x);
		}
    	if(x instanceof Player){
    		Player player = (Player)x;
    		if(mcUsers.getProfile(player).isDead()){
    			 mcUsers.getProfile(player).setDead(false);
    			 return;
    		}
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
