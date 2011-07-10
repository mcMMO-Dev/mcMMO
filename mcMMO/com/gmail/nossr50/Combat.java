package com.gmail.nossr50;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.Plugin;

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
	public static void combatChecks(EntityDamageEvent event, mcMMO pluginx)
	{
		if(event.isCancelled() || event.getDamage() == 0)
			return;
		/*
		 * CHANGE DAMAGE BASED ON DIFFICULTY
		 */
		if(event instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent eventx = (EntityDamageByEntityEvent) event;
			/*
			 * MOB DIFFICULTY STUFF
			 */
			
			/*
			if(eventx.getDamager() instanceof Monster)
			{
				Integer theMob = eventx.getDamager().getEntityId();
				//System.out.println("FIRST STAGE");
				if(pluginx.mob.mobDiff.containsKey(theMob))
				{
					//System.out.println("STAGE TWO");
					if(eventx.getEntity() instanceof Player)
					{
						Player player = (Player) eventx.getEntity();
						player.sendMessage("Mob Difficulty: "+((int) pluginx.mob.mobDiff.get(theMob)+1));
						//System.out.println("STAGE THREE");
						
						//SETUP DMG
						event.setDamage((pluginx.mob.mobDiff.get(theMob)+1) * event.getDamage());
					}
				} else
				{
					pluginx.mob.assignDifficulty(eventx.getDamager());
				}
			}
			*/
			
			/*
			 * OFFENSIVE CHECKS FOR PLAYERS VERSUS ENTITIES
			 */
			if(((EntityDamageByEntityEvent) event).getDamager() instanceof Player)
			{
				//Declare Things
				EntityDamageByEntityEvent eventb = (EntityDamageByEntityEvent) event;
				Player attacker = (Player)((EntityDamageByEntityEvent) event).getDamager();
				PlayerProfile PPa = Users.getProfile(attacker);
				
				//Damage modifiers
				if(mcPermissions.getInstance().unarmed(attacker) && attacker.getItemInHand().getTypeId() == 0) //Unarmed
					Unarmed.unarmedBonus(attacker, eventb);
				if(m.isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker) && PPa.getSkill("axes") >= 500)
						event.setDamage(event.getDamage()+4);
				
				//If there are any abilities to activate
		    	combatAbilityChecks(attacker, PPa, pluginx);
		    	
		    	//Check for offensive procs
		    	if(!(event instanceof EntityDamageByProjectileEvent))
		    	{
			    	if(mcPermissions.getInstance().axes(attacker))
			    		Axes.axeCriticalCheck(attacker, eventb, pluginx); //Axe Criticals
			    	
			    	if(!pluginx.misc.bleedTracker.contains((LivingEntity) event.getEntity())) //Swords Bleed
			   			Swords.bleedCheck(attacker, (LivingEntity)event.getEntity(), pluginx);
			    	
				   	if(event.getEntity() instanceof Player && mcPermissions.getInstance().unarmed(attacker))
				   	{
				   		Player defender = (Player)event.getEntity();
				   		Unarmed.disarmProcCheck(attacker, defender);
				    }
			    	
			    	
			    	
			    	//Modify the event damage if Attacker is Berserk
			    	if(PPa.getBerserkMode())
			    		event.setDamage(event.getDamage() + (event.getDamage() / 2));
		       	
			   		//Handle Ability Interactions
			   		if(PPa.getSkullSplitterMode() && m.isAxes(attacker.getItemInHand()))
		       			Axes.applyAoeDamage(attacker, eventb, pluginx);
		      		if(PPa.getSerratedStrikesMode() && m.isSwords(attacker.getItemInHand()))
		       			Swords.applySerratedStrikes(attacker, eventb, pluginx);
		      		
		      		//Experience
		      		if(event.getEntity() instanceof Player)
		      		{
		      			Player defender = (Player)event.getEntity();
		      			PlayerProfile PPd = Users.getProfile(defender);
			    		if(attacker != null && defender != null && LoadProperties.pvpxp)
			    		{
			    			if(System.currentTimeMillis() >= PPd.getRespawnATS() + 5000 
			    					&& ((PPd.getLastLogin()+5)*1000) < System.currentTimeMillis()
			    					&& defender.getHealth() >= 1)
			    			{
				    			if(m.isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker))
				    				PPa.addAxesXP((event.getDamage() * 2) * LoadProperties.pvpxprewardmodifier);
				    			if(m.isSwords(attacker.getItemInHand()) && mcPermissions.getInstance().swords(attacker))
				    				PPa.addSwordsXP((event.getDamage() * 2) * LoadProperties.pvpxprewardmodifier);
				    			if(attacker.getItemInHand().getTypeId() == 0 && mcPermissions.getInstance().unarmed(attacker))
				    				PPa.addUnarmedXP((event.getDamage() * 2) * LoadProperties.pvpxprewardmodifier);
			    			}
			    		}
		      		}
		      		
		      		if(event.getEntity() instanceof Monster && !pluginx.misc.mobSpawnerList.contains(event.getEntity()))
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
						if(event.getEntity() instanceof Slime)
							xp = (event.getDamage() * 3) * LoadProperties.xpGainMultiplier;
						if(event.getEntity() instanceof Ghast)
							xp = (event.getDamage() * 3) * LoadProperties.xpGainMultiplier;
						
						if(pluginx.mob.mobDiff.containsKey(event.getEntity().getEntityId()))
		      			{
		      				pluginx.mob.isAggressive.put(event.getEntity().getEntityId(), true);
		      				//ARMOR REDUCTION BASED ON ENEMY RANKING
		      				int modifiedDmg = event.getDamage() / (pluginx.mob.mobDiff.get(event.getEntity().getEntityId())+1);
		      				if(modifiedDmg < 1)
		      					modifiedDmg = 1;
		      				event.setDamage(modifiedDmg);
		      			}
						
						//ADJUST TO DIFFICULTY
						if(pluginx.mob.mobDiff.containsKey(event.getEntity().getEntityId()))
		      			{
							xp = xp * (pluginx.mob.mobDiff.get(event.getEntity().getEntityId())+1);
		      			}
						
						if(m.isSwords(attacker.getItemInHand()) && mcPermissions.getInstance().swords(attacker))
							PPa.addSwordsXP(xp);
						if(m.isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker))
							PPa.addAxesXP(xp);
						if(attacker.getItemInHand().getTypeId() == 0 && mcPermissions.getInstance().unarmed(attacker))
							PPa.addUnarmedXP(xp);
		      		}
		      		Skills.XpCheck(attacker);
		      		
		      		if(event.getEntity() instanceof Wolf)
		      		{
		      			Wolf theWolf = (Wolf)event.getEntity();
		      			
		      			if(attacker.getItemInHand().getTypeId() == 352 && mcPermissions.getInstance().taming(attacker))
		      			{
		      				event.setCancelled(true);
		      				if(theWolf.isTamed())
		      				{
		      				attacker.sendMessage(Messages.getString("Combat.BeastLore")+" "+
		      						Messages.getString("Combat.BeastLoreOwner", new Object[] {Taming.getOwnerName(theWolf)})+" "+
		      						Messages.getString("Combat.BeastLoreHealthWolfTamed", new Object[] {theWolf.getHealth()}));
		      				} 
		      				else
		      				{
		      					attacker.sendMessage(Messages.getString("Combat.BeastLore")+" "+
		      							Messages.getString("Combat.BeastLoreHealthWolf", new Object[] {theWolf.getHealth()}));
		      				}
		      			}
		      		}
				}
			}
		}
		
		/*
		 * OFFENSIVE CHECKS FOR WOLVES VERSUS ENTITIES
		 */
		if(event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) event).getDamager() instanceof Wolf)
		{
			EntityDamageByEntityEvent eventb = (EntityDamageByEntityEvent) event;
			Wolf theWolf = (Wolf) eventb.getDamager();
			if(Taming.ownerOnline(theWolf, pluginx))
			{
				Player master = Taming.getOwner(theWolf, pluginx);
				PlayerProfile PPo = Users.getProfile(master);
				
				if(mcPermissions.getInstance().taming(master))
				{
					//Sharpened Claws
					if(PPo.getSkill("taming") >= 750)
					{
						event.setDamage(event.getDamage() + 2);
					}
					
					//Gore
					if(Math.random() * 1000 <= PPo.getSkill("taming"))
					{
						event.setDamage(event.getDamage() * 2);
						
						if(event.getEntity() instanceof Player)
						{
							Player target = (Player)event.getEntity();
							target.sendMessage(Messages.getString("Combat.StruckByGore")); //$NON-NLS-1$
							Users.getProfile(target).setBleedTicks(2);
						}
						else
							pluginx.misc.addToBleedQue((LivingEntity) event.getEntity());
						
						master.sendMessage(Messages.getString("Combat.Gore")); //$NON-NLS-1$
					}
					if(!event.getEntity().isDead() && !pluginx.misc.mobSpawnerList.contains(event.getEntity()))
					{
						int xp = 0;
						if(event.getEntity() instanceof Monster)
						{
			      			if(event.getEntity() instanceof Creeper)
								xp = (event.getDamage() * 6) * LoadProperties.xpGainMultiplier;
							if(event.getEntity() instanceof Spider)
								xp = (event.getDamage() * 5) * LoadProperties.xpGainMultiplier;
							if(event.getEntity() instanceof Skeleton)
								xp = (event.getDamage() * 3) * LoadProperties.xpGainMultiplier;
							if(event.getEntity() instanceof Zombie)
								xp = (event.getDamage() * 3) * LoadProperties.xpGainMultiplier;
							if(event.getEntity() instanceof PigZombie)
								xp = (event.getDamage() * 4) * LoadProperties.xpGainMultiplier;
							if(event.getEntity() instanceof Slime)
								xp = (event.getDamage() * 4) * LoadProperties.xpGainMultiplier;
							if(event.getEntity() instanceof Ghast)
								xp = (event.getDamage() * 4) * LoadProperties.xpGainMultiplier;
							Users.getProfile(master).addTamingXP(xp);
						}
						if(event.getEntity() instanceof Player)
						{
							xp = (event.getDamage() * 2) * LoadProperties.xpGainMultiplier;
							Users.getProfile(master).addTamingXP(xp);
						}
						Skills.XpCheck(master);
					}
				}
			}
		}
		//Another offensive check for Archery
		if(event instanceof EntityDamageByProjectileEvent)
			archeryCheck((EntityDamageByProjectileEvent) event, pluginx);
			
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
		
		if(event.getEntity() instanceof Wolf)
		{
			Wolf theWolf = (Wolf) event.getEntity();
			
			if(Taming.ownerOnline(theWolf, pluginx))
			{
				Player master = Taming.getOwner(theWolf, pluginx);
				PlayerProfile PPo = Users.getProfile(master);
				if(mcPermissions.getInstance().taming(master))
				{
					/*
					 * TEMPORARY FIX AS WOLVES AREN'T TRIGGERING DAMAGE EVENTS WHEN ATTACKING NON PLAYERS AT THE TIME OF WRITING
					 */
					if(!event.isCancelled() && event.getCause() != DamageCause.LIGHTNING)
					{
						PPo.addTamingXP(event.getDamage() * 3);
						Skills.XpCheck(master);
					}
					
					//Shock-Proof
					if((event.getCause() == DamageCause.ENTITY_EXPLOSION || event.getCause() == DamageCause.BLOCK_EXPLOSION) && PPo.getSkill("taming") >= 500)
					{
						event.setDamage(2);
					}
					
					//Thick Fur
					if(PPo.getSkill("taming") >= 250)
						event.setDamage(event.getDamage() / 2);
				}
			}
		}
	}
	
	public static void combatAbilityChecks(Player attacker, PlayerProfile PPa, Plugin pluginx)
	{
		//Check to see if any abilities need to be activated
		if(PPa.getAxePreparationMode())
			Axes.skullSplitterCheck(attacker, pluginx);
		if(PPa.getSwordsPreparationMode())
			Swords.serratedStrikesActivationCheck(attacker, pluginx);
		if(PPa.getFistsPreparationMode())
			Unarmed.berserkActivationCheck(attacker, pluginx);
	}
	public static void archeryCheck(EntityDamageByProjectileEvent event, mcMMO pluginx)
	{
    	Entity y = event.getDamager();
    	Entity x = event.getEntity();
    	Projectile projectile = event.getProjectile();
    	if(projectile.toString().equals("CraftArrow") && x instanceof Player)
    	{ //$NON-NLS-1$
    		Player defender = (Player)x;
    		PlayerProfile PPd = Users.getProfile(defender);
    		if(PPd == null)
    			Users.addUser(defender);
    		if(mcPermissions.getInstance().unarmed(defender) && defender.getItemInHand().getTypeId() == 0)
    		{
	    		if(defender != null && PPd.getSkill("unarmed") >= 1000)
	    		{
	    			if(Math.random() * 1000 <= 500)
	    			{
	    				event.setCancelled(true);
	    				defender.sendMessage(Messages.getString("Combat.ArrowDeflect")); //$NON-NLS-1$
	    				return;
	    			}
	    		} else if(defender != null && Math.random() * 1000 <= (PPd.getSkill("unarmed") / 2))
	    		{
	    			event.setCancelled(true);
	    			defender.sendMessage(Messages.getString("Combat.ArrowDeflect")); //$NON-NLS-1$
	    			return;
	    		}
    		}
    	}
    	/*
    	 * If attacker is player
    	 */
    	if(y instanceof Player)
    	{
    		Player attacker = (Player)y;
    		PlayerProfile PPa = Users.getProfile(attacker);
    		if(projectile.toString().equals("CraftArrow") && mcPermissions.getInstance().archery(attacker))
    		{ //$NON-NLS-1$
    			if(!pluginx.misc.arrowTracker.containsKey(x) && event.getDamage() > 0)
    			{
    				pluginx.misc.arrowTracker.put(x, 0);
    				if(attacker != null)
    				{
    					if(Math.random() * 1000 <= PPa.getSkill("archery"))
    					{
    						pluginx.misc.arrowTracker.put(x, 1);
    					}
    				}
    			} else 
    			{
    				if(event.getDamage() > 0)
    				{
    					if(attacker != null){
        					if(Math.random() * 1000 <= PPa.getSkill("archery"))
        					{
        						pluginx.misc.arrowTracker.put(x, 1);
        					}
        				}
    				}
    			}
    			/*
    			 * DAMAGE MODIFIER
    			 */
    			if(PPa.getSkill("archery") >= 50 && PPa.getSkill("archery") < 250)
    				event.setDamage(event.getDamage()+1);
    			if(PPa.getSkill("archery") >= 250 && PPa.getSkill("archery") < 575)
    				event.setDamage(event.getDamage()+2);
    			if(PPa.getSkill("archery") >= 575 && PPa.getSkill("archery") < 725)
    				event.setDamage(event.getDamage()+3);
    			if(PPa.getSkill("archery") >= 725 && PPa.getSkill("archery") < 1000)
    				event.setDamage(event.getDamage()+4);
    			if(PPa.getSkill("archery") >= 1000)
    				event.setDamage(event.getDamage()+5);
    			
    			/*
    			 * IGNITION
    			 */
    			if(Math.random() * 100 >= 75){
    				
    				int ignition = 20;	
    				if(PPa.getSkill("archery") >= 200)
    					ignition+=20;
    				if(PPa.getSkill("archery") >= 400)
    					ignition+=20;
    				if(PPa.getSkill("archery") >= 600)
    					ignition+=20;
    				if(PPa.getSkill("archery") >= 800)
    					ignition+=20;
    				if(PPa.getSkill("archery") >= 1000)
    					ignition+=20;
    				
        			if(x instanceof Player){
        				Player Defender = (Player)x;
        				if(!Party.getInstance().inSameParty(attacker, Defender)){
        					event.getEntity().setFireTicks(ignition);
        					attacker.sendMessage(Messages.getString("Combat.Ignition")); //$NON-NLS-1$
        					Defender.sendMessage(Messages.getString("Combat.BurningArrowHit")); //$NON-NLS-1$
        				}
        			} else {
        			event.getEntity().setFireTicks(ignition);
        			attacker.sendMessage(Messages.getString("Combat.Ignition")); //$NON-NLS-1$
        			}
        		}
    		/*
    		 * Defender is Monster
    		 */
    		if(x instanceof Monster){
    			//XP
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
				if(x instanceof Slime)
					PPa.addArcheryXP((event.getDamage() * 3) * LoadProperties.xpGainMultiplier);
				if(x instanceof Ghast)
					PPa.addArcheryXP((event.getDamage() * 3) * LoadProperties.xpGainMultiplier);
    		}
    		/*
    		 * Attacker is Player
    		 */
    		if(x instanceof Player){
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
    	    		if(LoadProperties.pvpxp && !Party.getInstance().inSameParty(attacker, defender) 
    	    				&& ((PPd.getLastLogin()+5)*1000) < System.currentTimeMillis()){
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
    				if(PPa.getSkill("archery") >= 1000){
    	    			if(Math.random() * 1000 <= 500){
    	    				defender.teleport(loc);
    	    				defender.sendMessage(Messages.getString("Combat.TouchedFuzzy")); //$NON-NLS-1$
    	    				attacker.sendMessage(Messages.getString("Combat.TargetDazed")); //$NON-NLS-1$ //$NON-NLS-2$
    	    			}
    	    		} else if(Math.random() * 2000 <= PPa.getSkill("archery")){
    	    			defender.teleport(loc);
	    				defender.sendMessage(Messages.getString("Combat.TouchedFuzzy")); //$NON-NLS-1$
	    				attacker.sendMessage(Messages.getString("Combat.TargetDazed")); //$NON-NLS-1$ //$NON-NLS-2$
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
    public static boolean pvpAllowed(EntityDamageByEntityEvent event, World world)
    {
    	if(!event.getEntity().getWorld().getPVP())
    		return false;
    	//If it made it this far, pvp is enabled
    	return true;
    }
}
