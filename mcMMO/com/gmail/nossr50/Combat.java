package com.gmail.nossr50;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.skills.Acrobatics;
import com.gmail.nossr50.skills.Axes;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.skills.Swords;
import com.gmail.nossr50.skills.Taming;
import com.gmail.nossr50.skills.Unarmed;

public class Combat {
	private static mcMMO plugin;
	public Combat(mcMMO instance) {
    	plugin = instance;
    }
	public static void combatChecks(EntityDamageEvent event, Plugin pluginx){
		if(event.isCancelled() || event.getDamage() == 0)
			return;
		/*
		 * OFFENSIVE CHECKS FOR PLAYERS VERSUS ENTITIES
		 */
		if(event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) event).getDamager() instanceof Player)
		{
			//Declare Things
			EntityDamageByEntityEvent eventb = (EntityDamageByEntityEvent) event;
			Player attacker = (Player)((EntityDamageByEntityEvent) event).getDamager();
			PlayerProfile PPa = Users.getProfile(attacker);
			
			//Damage modifiers
			if(mcPermissions.getInstance().unarmed(attacker) && attacker.getItemInHand().getTypeId() == 0) //Unarmed
				Unarmed.unarmedBonus(attacker, eventb);
			if(m.isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker) && PPa.getAxesInt() >= 500)
					event.setDamage(event.getDamage()+4);
			
			//If there are any abilities to activate
	    	combatAbilityChecks(attacker, PPa);
	    	
	    	//Check for offensive procs
	    	Axes.axeCriticalCheck(attacker, eventb); //Axe Criticals
	    	if(!Config.getInstance().isBleedTracked(event.getEntity())) //Swords Bleed
    			Swords.bleedCheck(attacker, event.getEntity());
	    	
	    	//Modify the event damage if Attacker is Berserk
	    	if(PPa.getBerserkMode())
	    		event.setDamage(event.getDamage() + (event.getDamage() / 2));
       	
	   		//Handle Ability Interactions
	   		if(PPa.getSkullSplitterMode() && m.isAxes(attacker.getItemInHand()))
       			Axes.applyAoeDamage(attacker, eventb);
      		if(PPa.getSerratedStrikesMode() && m.isSwords(attacker.getItemInHand()))
       			Swords.applySerratedStrikes(attacker, eventb);
      		
      		//Experience
      		if(event.getEntity() instanceof Player)
      		{
      			Player defender = (Player)event.getEntity();
      			PlayerProfile PPd = Users.getProfile(defender);
	    		if(attacker != null && defender != null && LoadProperties.pvpxp)
	    		{
	    			if(System.currentTimeMillis() >= PPd.getRespawnATS() + 5000 && defender.getHealth() >= 1)
	    			{
		    			if(m.isAxes(attacker.getItemInHand()))
		    				PPa.addAxesXP((event.getDamage() * 3) * LoadProperties.pvpxprewardmodifier);
		    			if(m.isSwords(attacker.getItemInHand()))
		    				PPa.addSwordsXP((event.getDamage() * 3) * LoadProperties.pvpxprewardmodifier);
		    			if(attacker.getItemInHand().getTypeId() == 0)
		    				PPa.addUnarmedXP((event.getDamage() * 3) * LoadProperties.pvpxprewardmodifier);
	    			}
	    		}
      		}
      		
      		if(event.getEntity() instanceof Monster)
      		{
      			int xp = 0;
      			if(event.getEntity() instanceof Creeper)
					xp = (event.getDamage() * 4) * LoadProperties.xpGainMultiplier;
				if(event.getEntity() instanceof Spider)
					xp = (event.getDamage() * 3) * LoadProperties.xpGainMultiplier;
				if(event.getEntity() instanceof Skeleton)
					xp = (event.getDamage() * 2) * LoadProperties.xpGainMultiplier;
				if(event.getEntity() instanceof Zombie)
					xp = (event.getDamage() * 2) * LoadProperties.xpGainMultiplier;
				if(event.getEntity() instanceof PigZombie)
					xp = (event.getDamage() * 3) * LoadProperties.xpGainMultiplier;
				
				if(m.isSwords(attacker.getItemInHand()) && mcPermissions.getInstance().swords(attacker))
					PPa.addSwordsXP(xp);
				if(m.isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker))
					PPa.addAxesXP(xp);
				if(attacker.getItemInHand().getTypeId() == 0 && mcPermissions.getInstance().unarmed(attacker))
					PPa.addUnarmedXP(xp);
      		}
      		Skills.XpCheck(attacker);
      		
      		//Taming Debug Stuff
      		if(event.getEntity() instanceof Wolf)
      		{
      			attacker.sendMessage("mcMMO Debug: Wolf Owner Name "+Taming.getOwnerName(event.getEntity()));
      			event.setCancelled(true);
      		}
		}
		/*
		 * OFFENSIVE CHECKS FOR WOLVES VERSUS ENTITIES
		 */
		if(event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) event).getDamager() instanceof Wolf){
			EntityDamageByEntityEvent eventb = (EntityDamageByEntityEvent) event;
			if(Taming.hasOwner(eventb.getDamager(), pluginx)){
				Player master = Taming.getOwner(eventb.getDamager(), pluginx);
				PlayerProfile PPo = Users.getProfile(master);
				
				//Sharpened Claws
				if(PPo.getTamingInt() >= 750)
				{
					event.setDamage(event.getDamage() + 2);
				}
				
				//Gore
				if(Math.random() * 1000 <= PPo.getTamingInt())
				{
					event.setDamage(event.getDamage() * 2);
					
					if(event.getEntity() instanceof Player)
					{
						Player target = (Player)event.getEntity();
						target.sendMessage(ChatColor.RED+"**STRUCK BY GORE**");
						Users.getProfile(target).setBleedTicks(2);
					}
					else
						Config.getInstance().addToBleedQue(event.getEntity());
					
					master.sendMessage(ChatColor.GREEN+"**GORE**");
				}
				PPo.addTamingXP(event.getDamage() * 4);
				master.sendMessage("mcMMO Debug: Event Damage "+event.getDamage());
				Skills.XpCheck(master);
			}
		}
		//Another offensive check for Archery
		if(event instanceof EntityDamageByProjectileEvent)
			archeryCheck((EntityDamageByProjectileEvent) event);
			
		/*
		 * DEFENSIVE CHECKS
		 */
		if(event instanceof EntityDamageByEntityEvent && event.getEntity() instanceof Player)
		{
			Player defender = (Player)event.getEntity();
			Swords.parryCheck((EntityDamageByEntityEvent) event, defender);
			Swords.counterAttackChecks(event);
			Acrobatics.dodgeChecks((EntityDamageByEntityEvent)event);
		}
		/*
		 * DEFENSIVE CHECKS FOR WOLVES
		 */
		if(event.getEntity() instanceof Wolf){
			if(Taming.hasOwner(event.getEntity(), pluginx))
			{
				Player master = Taming.getOwner(event.getEntity(), pluginx);
				PlayerProfile PPo = Users.getProfile(master);
				
				//Shock-Proof
				if((event.getCause() == DamageCause.ENTITY_EXPLOSION || event.getCause() == DamageCause.BLOCK_EXPLOSION) && PPo.getTamingInt() >= 500)
				{
					event.setDamage(event.getDamage() / 6);
				}
				
				//Thick Fur
				if(PPo.getTamingInt() >= 250)
					event.setDamage(event.getDamage() / 2);
				master.sendMessage("mcMMO Debug: Wolf Damage Taken "+event.getDamage());
			}
		}
	}
	
	public static void combatAbilityChecks(Player attacker, PlayerProfile PPa){
		//Check to see if any abilities need to be activated
		if(PPa.getAxePreparationMode())
			Skills.skullSplitterCheck(attacker, plugin);
		if(PPa.getSwordsPreparationMode())
			Skills.serratedStrikesActivationCheck(attacker, plugin);
		if(PPa.getFistsPreparationMode())
			Skills.berserkActivationCheck(attacker, plugin);
	}
	public static void archeryCheck(EntityDamageByProjectileEvent event){
    	Entity y = event.getDamager();
    	Entity x = event.getEntity();
    	if(event.getProjectile().toString().equals("CraftArrow") && x instanceof Player){
    		Player defender = (Player)x;
    		PlayerProfile PPd = Users.getProfile(defender);
    		if(PPd == null)
    			Users.addUser(defender);
    		if(mcPermissions.getInstance().unarmed(defender) && defender.getItemInHand().getTypeId() == 0){
	    		if(defender != null && PPd.getUnarmedInt() >= 1000){
	    			if(Math.random() * 1000 <= 500){
	    				event.setCancelled(true);
	    				defender.sendMessage(ChatColor.WHITE+"**ARROW DEFLECT**");
	    				return;
	    			}
	    		} else if(defender != null && Math.random() * 1000 <= (PPd.getUnarmedInt() / 2)){
	    			event.setCancelled(true);
	    			defender.sendMessage(ChatColor.WHITE+"**ARROW DEFLECT**");
	    			return;
	    		}
    		}
    	}
    	/*
    	 * If attacker is player
    	 */
    	if(y instanceof Player){
    		Player attacker = (Player)y;
    		PlayerProfile PPa = Users.getProfile(attacker);
    		if(event.getProjectile().toString().equals("CraftArrow") && mcPermissions.getInstance().archery(attacker)){
    			if(!Config.getInstance().isTracked(x) && event.getDamage() > 0){
    				Config.getInstance().addArrowTrack(x, 0);
    				if(attacker != null){
    					if(Math.random() * 1000 <= PPa.getArcheryInt()){
    						Config.getInstance().addArrowCount(x, 1);
    					}
    				}
    			} else {
    				if(event.getDamage() > 0){
    					if(attacker != null){
        					if(Math.random() * 1000 <= PPa.getArcheryInt()){
        						Config.getInstance().addArrowCount(x, 1);
        					}
        				}
    				}
    			}
    			/*
    			 * DAMAGE MODIFIER
    			 */
    			if(PPa.getArcheryInt() >= 50 && PPa.getArcheryInt() < 250)
    				event.setDamage(event.getDamage()+1);
    			if(PPa.getArcheryInt() >= 250 && PPa.getArcheryInt() < 575)
    				event.setDamage(event.getDamage()+2);
    			if(PPa.getArcheryInt() >= 575 && PPa.getArcheryInt() < 725)
    				event.setDamage(event.getDamage()+3);
    			if(PPa.getArcheryInt() >= 725 && PPa.getArcheryInt() < 1000)
    				event.setDamage(event.getDamage()+4);
    			if(PPa.getArcheryInt() >= 1000)
    				event.setDamage(event.getDamage()+5);
    			
    			/*
    			 * IGNITION
    			 */
    			if(Math.random() * 100 >= 75){
    				
    				int ignition = 20;	
    				if(PPa.getArcheryInt() >= 200)
    					ignition+=20;
    				if(PPa.getArcheryInt() >= 400)
    					ignition+=20;
    				if(PPa.getArcheryInt() >= 600)
    					ignition+=20;
    				if(PPa.getArcheryInt() >= 800)
    					ignition+=20;
    				if(PPa.getArcheryInt() >= 1000)
    					ignition+=20;
    				
        			if(x instanceof Player){
        				Player Defender = (Player)x;
        				if(!Party.getInstance().inSameParty(attacker, Defender)){
        					event.getEntity().setFireTicks(ignition);
        					attacker.sendMessage(ChatColor.RED+"**IGNITION**");
        					Defender.sendMessage(ChatColor.DARK_RED+"You were struck by a burning arrow!");
        				}
        			} else {
        			event.getEntity().setFireTicks(ignition);
        			attacker.sendMessage(ChatColor.RED+"**IGNITION**");
        			}
        		}
    		/*
    		 * Defender is Monster
    		 */
    		if(x instanceof Monster){
    			//XP
    			if(!Config.getInstance().isMobSpawnTracked(x)){
    				if(x instanceof Creeper)
					PPa.addArcheryXP((event.getDamage() * 4) * LoadProperties.xpGainMultiplier);
					if(x instanceof Spider)
						PPa.addArcheryXP((event.getDamage() * 3) * LoadProperties.xpGainMultiplier);
					if(x instanceof Skeleton)
						PPa.addArcheryXP((event.getDamage() * 2) * LoadProperties.xpGainMultiplier);
					if(x instanceof Zombie)
						PPa.addArcheryXP((event.getDamage() * 2) * LoadProperties.xpGainMultiplier);
					if(x instanceof PigZombie)
						PPa.addArcheryXP((event.getDamage() * 3) * LoadProperties.xpGainMultiplier);
    			}
    		}
    		/*
    		 * Attacker is Player
    		 */
    		if(x instanceof Player){
    			if(LoadProperties.pvp == false){
    				event.setCancelled(true);
    				return;
    			}
    			Player defender = (Player)x;
    			PlayerProfile PPd = Users.getProfile(defender);
    			/*
    			 * Stuff for the daze proc
    			 */
    	    		if(PPa.inParty() && PPd.inParty()){
    					if(Party.getInstance().inSameParty(defender, attacker)){
    						event.setCancelled(true);
    						return;
    					}
    	    		}
    	    		/*
    	    		 * PVP XP
    	    		 */
    	    		if(LoadProperties.pvpxp && !Party.getInstance().inSameParty(attacker, defender)){
    	    			PPa.addArcheryXP((event.getDamage() * 3) * LoadProperties.pvpxprewardmodifier);
    	    		}
    				/*
    				 * DAZE PROC
    				 */
    	    		Location loc = defender.getLocation();
    				if(Math.random() * 10 > 5){
					loc.setPitch(90);
					} else {
						loc.setPitch(-90);
					}
    				if(PPa.getArcheryInt() >= 1000){
    	    			if(Math.random() * 1000 <= 500){
    	    				defender.teleportTo(loc);
    	    				defender.sendMessage(ChatColor.DARK_RED+"Touched Fuzzy. Felt Dizzy.");
    	    				attacker.sendMessage("Target was "+ChatColor.DARK_RED+"Dazed");
    	    			}
    	    		} else if(Math.random() * 2000 <= PPa.getArcheryInt()){
    	    			defender.teleportTo(loc);
	    				defender.sendMessage(ChatColor.DARK_RED+"Touched Fuzzy. Felt Dizzy.");
	    				attacker.sendMessage("Target was "+ChatColor.DARK_RED+"Dazed");
    	    		}
    			}
    		}
    		Skills.XpCheck(attacker);
    	}
    }
    public static void dealDamage(Entity target, int dmg){
    	if(target instanceof Player){
    		((Player) target).damage(dmg);
    	}
    	if(target instanceof Animals){
    		((Animals) target).damage(dmg);
    	}
    	if(target instanceof Monster){
    		((Monster) target).damage(dmg);
    	}
    }
}
