package com.gmail.nossr50;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.MobType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
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
    		mcm.getInstance().acrobaticsCheck(player, event, loc, xx, y, z);
    		}
    	}
    	/*
    	 * ARCHERY CHECKS
    	 */
    	if(event instanceof EntityDamageByProjectileEvent){
    		EntityDamageByProjectileEvent c = (EntityDamageByProjectileEvent)event;
    		mcm.getInstance().archeryCheck(c);
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
        		if(f instanceof Monster){
        			mcUsers.getProfile(defender).setRecentlyHurt(60);
        		}
        		/*
        		 * PARRYING CHECK, CHECK TO SEE IF ITS A SUCCESSFUL PARRY OR NOT
        		 */
        		mcm.getInstance().parryCheck(defender, eventb, f);
        		/*
        		 * PLAYER DEATH BY MONSTER MESSAGE CHECK, CHECKS TO SEE IF TO REPORT THE DEATH OR NOT
        		 */
        		//mcm.getInstance().playerDeathByMonsterMessageCheck(y, defender, plugin);
        		/*
        		 * CHECKS IF THE PLAYER DIES, IF SO DROP HIS SHIT BECAUSE OF THE DAMAGE MODIFIERS
        		 * MIGHT BE A BIT BUGGY, IT SEEMS TO WORK RIGHT NOW AT LEAST...
        		 */

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
        		mcm.getInstance().playerVersusMonsterChecks(eventb, attacker, e, typeid);
        		/*
        		 * Player versus Squid checks, this handles all skill damage modifiers and any procs.
        		 */
        		mcm.getInstance().playerVersusSquidChecks(eventb, attacker, e, typeid);
        		/*
        		 * Player versus Player checks, these checks make sure players are not in the same party, etc. They also check for any procs from skills and handle damage modifiers.
        		 */
        		if(mcm.getInstance().isPvpEnabled())
        		mcm.getInstance().playerVersusPlayerChecks(e, attacker, eventb, plugin);
        		/*
        		 * Player versus Animals checks, these checks handle any skill modifiers or procs
        		 */
        		mcm.getInstance().playerVersusAnimalsChecks(e, attacker, eventb, typeid);
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
