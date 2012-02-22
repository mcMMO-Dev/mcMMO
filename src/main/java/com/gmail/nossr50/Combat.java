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
package com.gmail.nossr50;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.events.FakeEntityDamageByEntityEvent;
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
				if(m.isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker) && Users.getProfile(attacker).getSkillLevel(SkillType.AXES) >= 500)
				{
					int damage = event.getDamage()+4;
				    event.setDamage(damage);
				}
				
				//If there are any abilities to activate
		    	combatAbilityChecks(attacker, PPa, pluginx);
		    	
		    	//Check for offensive procs
		    	if(!(((EntityDamageByEntityEvent) event).getDamager() instanceof Arrow))
		    	{
			    	if(mcPermissions.getInstance().axes(attacker))
			    		Axes.axeCriticalCheck(attacker, eventb, pluginx); //Axe Critical Checks
			    	
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
			    	if(!(event instanceof FakeEntityDamageByEntityEvent)) {
			    		if(PPa.getSkullSplitterMode() && m.isAxes(attacker.getItemInHand()))
			   				Axes.applyAoeDamage(attacker, eventb, pluginx);
			   			if(PPa.getSerratedStrikesMode() && m.isSwords(attacker.getItemInHand()))
		      				Swords.applySerratedStrikes(attacker, eventb, pluginx);
			    	}
		      		
		      		//Experience
		      		if(event.getEntity() instanceof Player)
		      		{
		      			Player defender = (Player)event.getEntity();
		      			PlayerProfile PPd = Users.getProfile(defender);
			    		if(attacker != null && defender != null && LoadProperties.pvpxp)
			    		{
			    			if(System.currentTimeMillis() >= (PPd.getRespawnATS()*1000) + 5000 
			    					&& ((PPd.getLastLogin()+5)*1000) < System.currentTimeMillis()
			    					&& defender.getHealth() >= 1)
			    			{
			    				//Prevent a ridiculous amount of XP being granted by capping it at the remaining health of the mob
				      			int hpLeft = defender.getHealth(), xpinc = 0;
				      			
				      			if(hpLeft < event.getDamage())
				      			{
				      			    if(hpLeft > 0)
				      			        xpinc = hpLeft;
				      			    else
				      			        xpinc = 0;
				      			} else
				      				xpinc = event.getDamage();
				      			
			    				int xp = (int) (xpinc * 2 * LoadProperties.pvpxprewardmodifier);
			    				
				    			if(m.isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker))
				    				PPa.addXP(SkillType.AXES, xp*10, attacker);
				    			if(m.isSwords(attacker.getItemInHand()) && mcPermissions.getInstance().swords(attacker))
				    				PPa.addXP(SkillType.SWORDS, xp*10, attacker);
				    			if(attacker.getItemInHand().getTypeId() == 0 && mcPermissions.getInstance().unarmed(attacker))
				    				PPa.addXP(SkillType.UNARMED, xp*10, attacker);
			    			}
			    		}
		      		}
		      		
		      		if(!pluginx.misc.mobSpawnerList.contains(event.getEntity().getEntityId()))
		      		{
		      			int xp = getXp(event.getEntity(), event);

						if(m.isSwords(attacker.getItemInHand()) && mcPermissions.getInstance().swords(attacker))
							PPa.addXP(SkillType.SWORDS, xp*10, attacker);
						else if(m.isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker))
							PPa.addXP(SkillType.AXES, xp*10, attacker);
						else if(attacker.getItemInHand().getTypeId() == 0 && mcPermissions.getInstance().unarmed(attacker))
							PPa.addXP(SkillType.UNARMED, xp*10, attacker);
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
				    //Fast Food Service
				    if(PPo.getSkillLevel(SkillType.TAMING) >= 50)
                    {
                        if(theWolf.getHealth() < theWolf.getMaxHealth())
                        {
                            if(Math.random() * 10 > 5)
                            {
                            	if(theWolf.getHealth() + event.getDamage() <= 20)
                            		theWolf.setHealth(theWolf.getHealth()+event.getDamage());
                            	else
                            		theWolf.setHealth(theWolf.getMaxHealth());
                            }
                        }
                    }
				    
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
					if(!event.getEntity().isDead() && !pluginx.misc.mobSpawnerList.contains(event.getEntity().getEntityId()))
					{
						int xp = getXp(event.getEntity(), event);
						Users.getProfile(master).addXP(SkillType.TAMING, xp*10, master);
						
						if(event.getEntity() instanceof Player)
						{
							xp = (event.getDamage() * 2);
							Users.getProfile(master).addXP(SkillType.TAMING, (int)((xp*10)*1.5), master);
						}
						Skills.XpCheckSkill(SkillType.TAMING, master);
					}
				}
			}
		}
		//Another offensive check for Archery
		if(event instanceof EntityDamageByEntityEvent && event.getCause() == DamageCause.PROJECTILE && ((EntityDamageByEntityEvent) event).getDamager() instanceof Arrow)
			archeryCheck((EntityDamageByEntityEvent)event, pluginx);
			
		/*
		 * DEFENSIVE CHECKS
		 */
		if(event instanceof EntityDamageByEntityEvent && event.getEntity() instanceof Player)
		{
			Swords.counterAttackChecks((EntityDamageByEntityEvent)event);
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
			Axes.skullSplitterCheck(attacker);
		if(PPa.getSwordsPreparationMode())
			Swords.serratedStrikesActivationCheck(attacker);
		if(PPa.getFistsPreparationMode())
			Unarmed.berserkActivationCheck(attacker);
	}
	public static void archeryCheck(EntityDamageByEntityEvent event, mcMMO pluginx)
	{
		Arrow arrow = (Arrow)event.getDamager();
    	Entity y = arrow.getShooter();
    	Entity x = event.getEntity();
    	if(x instanceof Player)
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
    		if(mcPermissions.getInstance().archery(attacker))
    		{
    			Archery.trackArrows(pluginx, x, event, attacker);
    			
    			/*
    			 * IGNITION
    			 */
    			Archery.ignitionCheck(x, event, attacker);
    		/*
    		 * Defender is Monster
    		 */
    		if(!pluginx.misc.mobSpawnerList.contains(x.getEntityId()))
    		{
    			int xp = getXp(event.getEntity(), event);
				PPa.addXP(SkillType.ARCHERY, xp*10, attacker);
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
    	    				&& ((PPd.getLastLogin()+5)*1000) < System.currentTimeMillis() && !attacker.getName().equals(defender.getName()))
    	    		{
    	    			int xp = (int) ((event.getDamage() * 2) * 10);
    	    			PPa.addXP(SkillType.ARCHERY, xp, attacker);
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
	
    /**
     * Attempt to damage target for value dmg with reason CUSTOM
     * 
     * @param target LivingEntity which to attempt to damage
     * @param dmg Amount of damage to attempt to do
     */
    public static void dealDamage(LivingEntity target, int dmg){
    	dealDamage(target, dmg, EntityDamageEvent.DamageCause.CUSTOM);
    }
    
    /**
     * Attempt to damage target for value dmg with reason cause
     * 
     * @param target LivingEntity which to attempt to damage
     * @param dmg Amount of damage to attempt to do
     * @param cause DamageCause to pass to damage event
     */
    public static void dealDamage(LivingEntity target, int dmg, DamageCause cause) {
    	if(LoadProperties.eventCallback) {
    		EntityDamageEvent ede = new EntityDamageEvent(target, cause, dmg);
    		Bukkit.getPluginManager().callEvent(ede);
    		if(ede.isCancelled()) return;
        	
        	target.damage(ede.getDamage());
    	} else {
    		target.damage(dmg);
    	}
    }
    
    /**
     * Attempt to damage target for value dmg with reason ENTITY_ATTACK with damager attacker
     * 
     * @param target LivingEntity which to attempt to damage
     * @param dmg Amount of damage to attempt to do
     * @param attacker Player to pass to event as damager
     */
    public static void dealDamage(LivingEntity target, int dmg, Player attacker) {
    	if(LoadProperties.eventCallback) {
    		EntityDamageEvent ede = (EntityDamageByEntityEvent) new FakeEntityDamageByEntityEvent(attacker, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, dmg);
    		Bukkit.getPluginManager().callEvent(ede);
    		if(ede.isCancelled()) return;

    		target.damage(ede.getDamage());
    	} else {
			target.damage(dmg);
		}
    }
    
    public static boolean pvpAllowed(EntityDamageByEntityEvent event, World world)
    {
    	if(!event.getEntity().getWorld().getPVP())
    		return false;
    	//If it made it this far, pvp is enabled
    	return true;
    }
    public static int getXp(Entity entity, EntityDamageEvent event)
    {
    	int xp = 0;
    	if(entity instanceof LivingEntity)
    	{
    		LivingEntity le = (LivingEntity)entity;
	    	//Prevent a ridiculous amount of XP being granted by capping it at the remaining health of the entity
			int hpLeft = le.getHealth(), xpinc = 0;
				
			if(hpLeft < event.getDamage())
            {
			    if(hpLeft > 0)
			        xpinc = hpLeft;
                else
                    xpinc = 0;
            } 
			else
			    xpinc = event.getDamage();
			
	    	if(entity instanceof Animals)
	    	{
		    	xp = (int) (xpinc * 1);
	    	} else
	    	{
	    		if(entity instanceof Enderman)
					xp = (xpinc * 2);
		    	else if(entity instanceof Creeper)
					xp = (xpinc * 4);
		    	else if(entity instanceof Silverfish)
					xp = (xpinc * 3);
		    	else if(entity instanceof CaveSpider)
					xp = (xpinc * 3);
		    	else if(entity instanceof Spider)
					xp = (xpinc * 3);
		    	else if(entity instanceof Skeleton)
					xp = (xpinc * 2);
		    	else if(entity instanceof Zombie)
					xp = (xpinc * 2);
		    	else if(entity instanceof PigZombie)
					xp = (xpinc * 3);
		    	else if(entity instanceof Slime)
					xp = (xpinc * 2);
		    	else if(entity instanceof Ghast)
					xp = (xpinc * 3);
		    	else if(entity instanceof Blaze)
		    		xp = (xpinc * 3);
		    	else if(entity instanceof EnderDragon)
		    		xp = (xpinc * 8);
		    	else if(entity instanceof MagmaCube)
					xp = (xpinc * 2);
	    	}
    	}
    	return xp;
    }
}
