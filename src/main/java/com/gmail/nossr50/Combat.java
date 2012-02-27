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
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

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
		if(event.isCancelled() || event.getDamage() == 0 || event.getEntity().isDead())
			return;
		
		if(event instanceof EntityDamageByEntityEvent)
		{	
			//Declare Things
			EntityDamageByEntityEvent eEvent = (EntityDamageByEntityEvent) event;
			Entity damager = eEvent.getDamager();
			LivingEntity target = (LivingEntity) eEvent.getEntity();
			int damage = eEvent.getDamage();
			
			/*
			 * PLAYER VERSUS ENTITIES
			 */
			if(damager instanceof Player)
			{
				Player attacker = (Player) eEvent.getDamager();
				ItemStack itemInHand = attacker.getItemInHand();
				PlayerProfile PPa = Users.getProfile(attacker);
				
				//Damage modifiers and proc checks
				if(m.isSwords(itemInHand) && mcPermissions.getInstance().swords(attacker))
				{
					if(PPa.getSwordsPreparationMode())
						Swords.serratedStrikesActivationCheck(attacker, PPa);
					
					if(!pluginx.misc.bleedTracker.contains(target)) //Bleed
						Swords.bleedCheck(attacker, PPa.getSkillLevel(SkillType.SWORDS), target, pluginx);
					
					if (!(event instanceof FakeEntityDamageByEntityEvent) && PPa.getSerratedStrikesMode())
						Swords.applySerratedStrikes(attacker, target, damage, pluginx);
					
					if(target instanceof Player)
						PvPExperienceGain(attacker, PPa, (Player) target, damage, SkillType.SWORDS);
					else if(!pluginx.misc.mobSpawnerList.contains(target.getEntityId()))
						PvEExperienceGain(attacker, PPa, target, damage, SkillType.SWORDS); 
				}
				else if(m.isAxes(itemInHand) && mcPermissions.getInstance().axes(attacker))
				{
					if(PPa.getAxePreparationMode())
						Axes.skullSplitterActivationCheck(attacker, PPa);
					
					if(PPa.getSkillLevel(SkillType.AXES) >= 500)
						damage += 4;
					
					damage = Axes.axeCriticalCheck(attacker, PPa.getSkillLevel(SkillType.AXES), target, damage, pluginx); //Critical hit
					
					if (!(event instanceof FakeEntityDamageByEntityEvent) && PPa.getSkullSplitterMode())
						Axes.applyAoeDamage(attacker, target, damage, pluginx);
					
					if(target instanceof Player)
						PvPExperienceGain(attacker, PPa, (Player) target, damage, SkillType.AXES);
					else if(!pluginx.misc.mobSpawnerList.contains(target.getEntityId()))
						PvEExperienceGain(attacker, PPa, target, damage, SkillType.AXES);
				}
				else if(itemInHand.getTypeId() == 0 && mcPermissions.getInstance().unarmed(attacker)) //Unarmed
				{
					if(PPa.getFistsPreparationMode())
						Unarmed.berserkActivationCheck(attacker, PPa);
					
					damage += Unarmed.unarmedBonus(PPa.getSkillLevel(SkillType.UNARMED));
					
					if(PPa.getBerserkMode())
						damage *= 1.5;
					
					if(target instanceof Player)
						Unarmed.disarmProcCheck(attacker, PPa.getSkillLevel(SkillType.UNARMED), (Player) target); //Disarm
					
					if(target instanceof Player)
						PvPExperienceGain(attacker, PPa, (Player) target, damage, SkillType.UNARMED);
					else if(!pluginx.misc.mobSpawnerList.contains(target.getEntityId()))
						PvEExperienceGain(attacker, PPa, target, damage, SkillType.UNARMED);
				}
				
				//Player use bone on wolf.
				else if(target instanceof Wolf)
				{
					Wolf wolf = (Wolf) target;
				
					if(itemInHand.getTypeId() == 352 && mcPermissions.getInstance().taming(attacker))
					{
						event.setCancelled(true);
						if(wolf.isTamed())
							attacker.sendMessage(mcLocale.getString("Combat.BeastLore")+" "+
									mcLocale.getString("Combat.BeastLoreOwner", new Object[] {Taming.getOwnerName(wolf)})+" "+
									mcLocale.getString("Combat.BeastLoreHealthWolfTamed", new Object[] {wolf.getHealth()}));
						else
							attacker.sendMessage(mcLocale.getString("Combat.BeastLore")+" "+
									mcLocale.getString("Combat.BeastLoreHealthWolf", new Object[] {wolf.getHealth()}));
					}
				}
				
				eEvent.setDamage(damage);
			}
			
			/*
			 * TAMING (WOLVES VERSUS ENTITIES)
			 */
			else if(damager instanceof Wolf)
			{
				Wolf wolf = (Wolf) damager;
				
				if (wolf.isTamed() && Taming.ownerOnline(wolf, pluginx))
				{
					Player master = Taming.getOwner(wolf, pluginx);
					if (master == null) //Can it really happen?
						return;
					
					PlayerProfile PPo = Users.getProfile(master);
					if(mcPermissions.getInstance().taming(master))
					{
						//Fast Food Service
						Taming.fastFoodService(PPo, wolf, event);
						
						//Sharpened Claws
						Taming.sharpenedClaws(PPo, event);
						
						//Gore
						Taming.gore(PPo, event, master, pluginx);
						
						//Reward XP
						Taming.rewardXp(event, pluginx, master);
					}
				}
			}
			
			//Another offensive check for Archery
			else if(damager instanceof Arrow)
				archeryCheck((EntityDamageByEntityEvent)event, pluginx);
			
			/*
			 * DEFENSIVE CHECKS
			 */
			if(target instanceof Player)
			{
				Swords.counterAttackChecks(eEvent);
				Acrobatics.dodgeChecks(eEvent);
			}
			
			/*
			 * DEFENSIVE CHECKS FOR WOLVES
			 */
			else if(target instanceof Wolf)
			{
				Wolf wolf = (Wolf) target;
				if(wolf.isTamed() && Taming.ownerOnline(wolf, pluginx))
					Taming.preventDamage(eEvent, pluginx);
			}
		}
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
			int damage = event.getDamage();
			if(mcPermissions.getInstance().archery(attacker) && damage > 0)
			{
				Archery.trackArrows(pluginx, x, PPa);
				
				/*
				 * IGNITION
				 */
				Archery.ignitionCheck(x, attacker);
			/*
			 * Defender is Monster
			 */
			if(!pluginx.misc.mobSpawnerList.contains(x.getEntityId()))
			{
				int xp = getXp(x, damage);
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
					if(LoadProperties.pvpxp && ((PPd.getLastLogin()+5)*1000) < System.currentTimeMillis() && !attacker.getName().equals(defender.getName()))
					{
						int xp = (int) ((damage * 2) * 10);
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
	
	private static void PvPExperienceGain(Player attacker, PlayerProfile PPa, Player defender, int damage, SkillType skillType)
	{
		if (!LoadProperties.pvpxp)
			return;
		
	  	PlayerProfile PPd = Users.getProfile(defender);
	  	
   		if(System.currentTimeMillis() >= (PPd.getRespawnATS()*1000) + 5000 
  				&& ((PPd.getLastLogin()+5)*1000) < System.currentTimeMillis()
   				&& defender.getHealth() >= 1)
   		{
			//Prevent a ridiculous amount of XP being granted by capping it at the remaining health of the mob
			int hpLeft = defender.getHealth(), xpinc = 0;
		   			
			if(hpLeft < damage)
			{
				if(hpLeft > 0)
					xpinc = hpLeft;
				else
					xpinc = 0;
			} else
				xpinc = damage;
			
			int xp = (int) (xpinc * 2 * LoadProperties.pvpxprewardmodifier);
			PPa.addXP(skillType, xp*10, attacker);
			Skills.XpCheckSkill(skillType, attacker);
	  	}
	}
	
	private static void PvEExperienceGain(Player attacker, PlayerProfile PPa, LivingEntity target, int damage, SkillType skillType)
	{
   		int xp = getXp(target, damage);
		PPa.addXP(skillType, xp*10, attacker);
	}
	
	public static int getXp(Entity entity, int damage)
	{
		int xp = 0;
		if(entity instanceof LivingEntity)
		{
			LivingEntity le = (LivingEntity) entity;
			//Prevent a ridiculous amount of XP being granted by capping it at the remaining health of the entity
			int hpLeft = le.getHealth();
			int xpinc = 0;
			
			if(hpLeft < damage)
			{
				if(hpLeft > 0)
					xpinc = hpLeft;
				else
					xpinc = 0;
			} 
			else
				xpinc = damage;
			
			if(entity instanceof Animals)
				xp = (int) (xpinc * LoadProperties.animalXP);
			else
			{
				if(entity instanceof Enderman)
					xp = (int) (xpinc * LoadProperties.endermanXP);
				else if(entity instanceof Creeper)
					xp = (int) (xpinc * LoadProperties.creeperXP);
				else if(entity instanceof Silverfish)
					xp = (int) (xpinc * LoadProperties.silverfishXP);
				else if(entity instanceof CaveSpider)
					xp = (int) (xpinc * LoadProperties.cavespiderXP);
				else if(entity instanceof Spider)
					xp = (int) (xpinc * LoadProperties.spiderXP);
				else if(entity instanceof Skeleton)
					xp = (int) (xpinc * LoadProperties.skeletonXP);
				else if(entity instanceof Zombie)
					xp = (int) (xpinc * LoadProperties.zombieXP);
				else if(entity instanceof PigZombie)
					xp = (int) (xpinc * LoadProperties.pigzombieXP);
				else if(entity instanceof Slime)
					xp = (int) (xpinc * LoadProperties.slimeXP);
				else if(entity instanceof Ghast)
					xp = (int) (xpinc * LoadProperties.ghastXP);
				else if(entity instanceof Blaze)
					xp = (int) (xpinc * LoadProperties.blazeXP);
				else if(entity instanceof EnderDragon)
					xp = (int) (xpinc * LoadProperties.enderdragonXP);
				else if(entity instanceof MagmaCube)
					xp = (int) (xpinc * LoadProperties.magmacubeXP);
			}
		}
		return xp;
	}
}
