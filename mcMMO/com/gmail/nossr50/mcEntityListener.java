package com.gmail.nossr50;

import net.minecraft.server.EntityLiving;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.skills.Acrobatics;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.skills.Taming;


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
    	if(m.isBlockAround(loc, 5, 52)){
    		Config.getInstance().addMobSpawnTrack(spawnee);
    	}
    }
    public void onEntityDamage(EntityDamageEvent event) {
    	if(event.isCancelled())
    		return;
    	/*
    	 * CHECK FOR mcMMO PVP FLAG
    	 */
    	if(event instanceof EntityDamageByEntityEvent)
    	{
    		EntityDamageByEntityEvent eventb = (EntityDamageByEntityEvent)event;
    		if(eventb.getEntity() instanceof Player && eventb.getDamager() instanceof Player && !LoadProperties.pvp)
    			return;
    	}
    	/*
    	 * CHECK FOR INVULNERABILITY
    	 */
    	if(event.getEntity() instanceof Player){
    		Player defender = (Player)event.getEntity();
    		PlayerProfile PPd = Users.getProfile(defender);
    		if(defender != null && Config.getInstance().isGodModeToggled(defender.getName()))
    			event.setCancelled(true);
    		if(PPd == null)
    			Users.addUser(defender);
    	}
    	
    	if(event.getEntity() instanceof CraftEntity){
    		CraftEntity cEntity = (CraftEntity)event.getEntity();
	    	if(cEntity.getHandle() instanceof EntityLiving)
	    	{
		    	EntityLiving entityliving = (EntityLiving)cEntity.getHandle();
		    	if(entityliving.noDamageTicks < entityliving.maxNoDamageTicks/2.0F)
		    	{
			    	Entity x = event.getEntity();
			    	DamageCause type = event.getCause();
			    	if(event.getEntity() instanceof Wolf)
			    	{
				    	Player master = Taming.getOwner(event.getEntity(), plugin);
				    	PlayerProfile PPo = Users.getProfile(master);
				    	if(master == null || PPo == null)
				    		return;
			    		//Environmentally Aware
						if((event.getCause() == DamageCause.CONTACT || event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.FIRE) && PPo.getTamingInt() >= 100)
						{
							if(event.getDamage() < ((Wolf) event.getEntity()).getHealth())
							{
								event.getEntity().teleport(Taming.getOwner(event.getEntity(), plugin).getLocation());
								master.sendMessage(ChatColor.DARK_GRAY+"Your wolf scurries back to you...");
								event.getEntity().setFireTicks(0);
							}
						}
						if(event.getCause() == DamageCause.FALL && PPo.getTamingInt() >= 100)
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
    
    public void onEntityDeath(EntityDeathEvent event) {
    	Entity x = event.getEntity();
    	x.setFireTicks(0);
    	
    	//Remove bleed track
    	if(Config.getInstance().isBleedTracked(x))
    		Config.getInstance().addToBleedRemovalQue(x);
    	
		Skills.arrowRetrievalCheck(x);
		if(Config.getInstance().isMobSpawnTracked(x)){
			Config.getInstance().removeMobSpawnTrack(x);
		}
    	if(x instanceof Player){
    		Player player = (Player)x;
    		Users.getProfile(player).setBleedTicks(0);
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
