package com.gmail.nossr50;

import org.bukkit.Location;
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
    	if(event instanceof EntityDamageByProjectileEvent){
    		EntityDamageByProjectileEvent c = (EntityDamageByProjectileEvent)event;
    		mcCombat.getInstance().archeryCheck(c);
    	}
    	/*
    	 * Entity Damage by Entity checks
    	 */
    	if(event instanceof EntityDamageByEntityEvent){
    		EntityDamageByEntityEvent eventb = (EntityDamageByEntityEvent)event;
    		Entity e = eventb.getEntity(); //Defender
        	Entity f = eventb.getDamager(); //Attacker
        	/*
        	 * IF DEFENDER IS PLAYER
        	 */
        	if(e instanceof Player){
        		Player defender = (Player)e;
        		if(mcConfig.getInstance().isGodModeToggled(defender.getName()))
        			event.setCancelled(true);
        		if(f instanceof Monster){
        			mcUsers.getProfile(defender).setRecentlyHurt(30);
        		}
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
        	}
        	if(f instanceof Player && e instanceof Player && !mcLoadProperties.pvp)
        		event.setCancelled(true);
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
    }
    public void onEntityDeath(EntityDeathEvent event) {
    	Entity x = event.getEntity();
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
