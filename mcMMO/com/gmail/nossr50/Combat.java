package com.gmail.nossr50;

import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.skills.Acrobatics;
import com.gmail.nossr50.skills.Archery;
import com.gmail.nossr50.skills.Axes;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.skills.Swords;
import com.gmail.nossr50.skills.Taming;
import com.gmail.nossr50.skills.Unarmed;

public class Combat 
{
	public static void combatChecks(EntityDamageEvent event, mcMMO pluginx)
	{
		if(event.isCancelled() || event.getDamage() == 0)
			return;
		
		if(event instanceof EntityDamageByEntityEvent)
		{	
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
				if(m.isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker) && PPa.getSkillLevel(SkillType.AXES) >= 500)
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
				    				PPa.addXP(SkillType.AXES, (event.getDamage() * 2) * LoadProperties.pvpxprewardmodifier);
				    			if(m.isSwords(attacker.getItemInHand()) && mcPermissions.getInstance().swords(attacker))
				    				PPa.addXP(SkillType.SWORDS, (event.getDamage() * 2) * LoadProperties.pvpxprewardmodifier);
				    			if(attacker.getItemInHand().getTypeId() == 0 && mcPermissions.getInstance().unarmed(attacker))
				    				PPa.addXP(SkillType.UNARMED, (event.getDamage() * 2) * LoadProperties.pvpxprewardmodifier);
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

						if(m.isSwords(attacker.getItemInHand()) && mcPermissions.getInstance().swords(attacker))
							PPa.addXP(SkillType.SWORDS, xp);
						else if(m.isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker))
							PPa.addXP(SkillType.AXES, xp);
						else if(attacker.getItemInHand().getTypeId() == 0 && mcPermissions.getInstance().unarmed(attacker))
							PPa.addXP(SkillType.UNARMED, xp);
		      		}
		      		Skills.XpCheckAll(attacker);
		      		
		      		if(event.getEntity() instanceof Wolf)
		      		{
		      			Wolf theWolf = (Wolf)event.getEntity();
		      			
		      			if(attacker.getItemInHand().getTypeId() == 352 && mcPermissions.getInstance().taming(attacker))
		      			{
		      				event.setCancelled(true);
		      				if(theWolf.isTamed())
		      				{
		      				attacker.sendMessage(mcLocale.getString("Combat.BeastLore")+" "+
		      						mcLocale.getString("Combat.BeastLoreOwner", new Object[] {Taming.getOwnerName(theWolf)})+" "+
		      						mcLocale.getString("Combat.BeastLoreHealthWolfTamed", new Object[] {theWolf.getHealth()}));
		      				} 
		      				else
		      				{
		      					attacker.sendMessage(mcLocale.getString("Combat.BeastLore")+" "+
		      							mcLocale.getString("Combat.BeastLoreHealthWolf", new Object[] {theWolf.getHealth()}));
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
			if(theWolf.isTamed() && Taming.ownerOnline(theWolf, pluginx))
			{
				if(Taming.getOwner(theWolf, pluginx) == null)
					return;
				Player master = Taming.getOwner(theWolf, pluginx);
				PlayerProfile PPo = Users.getProfile(master);
				
				if(mcPermissions.getInstance().taming(master))
				{
					//Sharpened Claws
					if(PPo.getSkillLevel(SkillType.TAMING) >= 750)
					{
						event.setDamage(event.getDamage() + 2);
					}
					
					//Gore
					if(Math.random() * 1000 <= PPo.getSkillLevel(SkillType.TAMING))
					{
						event.setDamage(event.getDamage() * 2);
						
						if(event.getEntity() instanceof Player)
						{
							Player target = (Player)event.getEntity();
							target.sendMessage(mcLocale.getString("Combat.StruckByGore")); //$NON-NLS-1$
							Users.getProfile(target).setBleedTicks(2);
						}
						else
							pluginx.misc.addToBleedQue((LivingEntity) event.getEntity());
						
						master.sendMessage(mcLocale.getString("Combat.Gore")); //$NON-NLS-1$
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
							Users.getProfile(master).addXP(SkillType.TAMING, xp);
						}
						if(event.getEntity() instanceof Player)
						{
							xp = (event.getDamage() * 2) * LoadProperties.xpGainMultiplier;
							Users.getProfile(master).addXP(SkillType.TAMING, xp);
						}
						Skills.XpCheckSkill(SkillType.TAMING, master);
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
			
			if(theWolf.isTamed() && Taming.ownerOnline(theWolf, pluginx))
			{
				if(Taming.getOwner(theWolf, pluginx) == null)
					return;
				
				Player master = Taming.getOwner(theWolf, pluginx);
				PlayerProfile PPo = Users.getProfile(master);
				if(mcPermissions.getInstance().taming(master))
				{				
					//Shock-Proof
					if((event.getCause() == DamageCause.ENTITY_EXPLOSION || event.getCause() == DamageCause.BLOCK_EXPLOSION) && PPo.getSkillLevel(SkillType.TAMING) >= 500)
					{
						event.setDamage(2);
					}
					
					//Thick Fur
					if(PPo.getSkillLevel(SkillType.TAMING) >= 250)
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
    	{
    		Player defender = (Player)x;
    		PlayerProfile PPd = Users.getProfile(defender);
    		if(PPd == null)
    			Users.addUser(defender);
    		if(mcPermissions.getInstance().unarmed(defender) && defender.getItemInHand().getTypeId() == 0)
    		{
	    		if(defender != null && PPd.getSkillLevel(SkillType.UNARMED) >= 1000)
	    		{
	    			if(Math.random() * 1000 <= 500)
	    			{
	    				event.setCancelled(true);
	    				defender.sendMessage(mcLocale.getString("Combat.ArrowDeflect")); //$NON-NLS-1$
	    				return;
	    			}
	    		} else if(defender != null && Math.random() * 1000 <= (PPd.getSkillLevel(SkillType.UNARMED) / 2))
	    		{
	    			event.setCancelled(true);
	    			defender.sendMessage(mcLocale.getString("Combat.ArrowDeflect")); //$NON-NLS-1$
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
    		{
    			Archery.trackArrows(pluginx, x, event, attacker);
    			/*
    			 * DAMAGE MODIFIER
    			 */
    			if(PPa.getSkillLevel(SkillType.ARCHERY) >= 50 && PPa.getSkillLevel(SkillType.ARCHERY) < 250)
    				event.setDamage(event.getDamage()+1);
    			if(PPa.getSkillLevel(SkillType.ARCHERY) >= 250 && PPa.getSkillLevel(SkillType.ARCHERY) < 575)
    				event.setDamage(event.getDamage()+2);
    			if(PPa.getSkillLevel(SkillType.ARCHERY) >= 575 && PPa.getSkillLevel(SkillType.ARCHERY) < 725)
    				event.setDamage(event.getDamage()+3);
    			if(PPa.getSkillLevel(SkillType.ARCHERY) >= 725 && PPa.getSkillLevel(SkillType.ARCHERY) < 1000)
    				event.setDamage(event.getDamage()+4);
    			if(PPa.getSkillLevel(SkillType.ARCHERY) >= 1000)
    				event.setDamage(event.getDamage()+5);
    			
    			/*
    			 * IGNITION
    			 */
    			Archery.ignitionCheck(x, event, attacker);
    		/*
    		 * Defender is Monster
    		 */
    		if(!pluginx.misc.mobSpawnerList.contains(x) && x instanceof Monster)
    		{
    			//XP
    			if(x instanceof Creeper)
    				PPa.addXP(SkillType.ARCHERY, (event.getDamage() * 4) * LoadProperties.xpGainMultiplier);
				if(x instanceof Spider)
					PPa.addXP(SkillType.ARCHERY, (event.getDamage() * 3) * LoadProperties.xpGainMultiplier);
				if(x instanceof Skeleton)
					PPa.addXP(SkillType.ARCHERY, (event.getDamage() * 2) * LoadProperties.xpGainMultiplier);
				if(x instanceof Zombie)
					PPa.addXP(SkillType.ARCHERY, (event.getDamage() * 2) * LoadProperties.xpGainMultiplier);
				if(x instanceof PigZombie)
					PPa.addXP(SkillType.ARCHERY, (event.getDamage() * 3) * LoadProperties.xpGainMultiplier);
				if(x instanceof Slime)
					PPa.addXP(SkillType.ARCHERY, (event.getDamage() * 3) * LoadProperties.xpGainMultiplier);
				if(x instanceof Ghast)
					PPa.addXP(SkillType.ARCHERY, (event.getDamage() * 3) * LoadProperties.xpGainMultiplier);
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
    	    		if(PPa.inParty() && PPd.inParty())
    	    		{
    					if(Party.getInstance().inSameParty(defender, attacker))
    					{
    						event.setCancelled(true);
    						return;
    					}
    	    		}
    	    		/*
    	    		 * PVP XP
    	    		 */
    	    		if(LoadProperties.pvpxp && !Party.getInstance().inSameParty(attacker, defender) 
    	    				&& ((PPd.getLastLogin()+5)*1000) < System.currentTimeMillis())
    	    		{
    	    			PPa.addXP(SkillType.ARCHERY, (event.getDamage() * 3) * LoadProperties.pvpxprewardmodifier);
    	    		}
    				/*
    				 * DAZE PROC
    				 */
    	    		Archery.dazeCheck(defender, attacker);
    			}
    		}
    		Skills.XpCheckSkill(SkillType.ARCHERY, attacker);
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
