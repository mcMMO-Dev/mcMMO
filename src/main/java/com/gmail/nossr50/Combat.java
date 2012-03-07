/*
 * Copyright (C) 2012 Matt 'The Yeti' Burnett & mcMMO Development
 * Copyright (C) 2010-2011 'nossr50'
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.gmail.nossr50;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.events.FakeEntityDamageByEntityEvent;
import com.gmail.nossr50.events.FakeEntityDamageEvent;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.skills.Acrobatics;
import com.gmail.nossr50.skills.Archery;
import com.gmail.nossr50.skills.Axes;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.skills.Swords;
import com.gmail.nossr50.skills.Taming;
import com.gmail.nossr50.skills.Unarmed;

public class Combat {

    /**
     * Apply combat modifiers and process and XP gain.
     *
     * @param event The event to run the combat checks on.
     * @param pluginx mcMMO plugin instance
     */
    public static void combatChecks(EntityDamageByEntityEvent event, mcMMO pluginx) {
        if (event.getDamage() == 0 || event.getEntity().isDead()) {
            return;
        }

        Entity damager = event.getDamager();
        LivingEntity target = (LivingEntity) event.getEntity();
        int damage = event.getDamage();
        EntityType damagerType = damager.getType();
        EntityType targetType = target.getType();

        switch (damagerType) {
        case PLAYER:
            Player attacker = (Player) event.getDamager();
            ItemStack itemInHand = attacker.getItemInHand();
            PlayerProfile PPa = Users.getProfile(attacker);

            combatAbilityChecks(attacker);
            
            if (ItemChecks.isSword(itemInHand) && mcPermissions.getInstance().swords(attacker)) {
                if (!pluginx.misc.bleedTracker.contains(target)) {
                    Swords.bleedCheck(attacker, target, pluginx);
                }

                if (PPa.getSerratedStrikesMode()) {
                    applyAbilityAoE(attacker, target, damage, pluginx, SkillType.SWORDS);
                }

                if (targetType.equals(EntityType.PLAYER)) {
                    PvPExperienceGain(attacker, PPa, (Player) target, damage, SkillType.SWORDS);
                }
                else if (!pluginx.misc.mobSpawnerList.contains(target.getEntityId())){
                    PvEExperienceGain(attacker, PPa, target, damage, SkillType.SWORDS);
                }
            }
            else if (ItemChecks.isAxe(itemInHand) && mcPermissions.getInstance().axes(attacker)) {
                Axes.axesBonus(attacker, event);
                Axes.axeCriticalCheck(attacker, event);
                Axes.impact(attacker, target, event);

                if (PPa.getSkullSplitterMode()) {
                    applyAbilityAoE(attacker, target, damage, pluginx, SkillType.AXES);
                }
                
                if (targetType.equals(EntityType.PLAYER)) {
                    PvPExperienceGain(attacker, PPa, (Player) target, event.getDamage(), SkillType.AXES); //use getDamage because damage is modified in earlier functions
                }
                else if (!pluginx.misc.mobSpawnerList.contains(target.getEntityId())) {
                    PvEExperienceGain(attacker, PPa, target, event.getDamage(), SkillType.AXES); //use getDamage because damage is modified in earlier functions
                }
            }
            else if (itemInHand.getType().equals(Material.AIR) && mcPermissions.getInstance().unarmed(attacker)) {
                Unarmed.unarmedBonus(attacker, event);
                
                if (PPa.getBerserkMode()) {
                    event.setDamage(damage + (damage / 2));
                }

                if (targetType.equals(EntityType.PLAYER)) {
                    Unarmed.disarmProcCheck(attacker, (Player) target);
                    PvPExperienceGain(attacker, PPa, (Player) target, event.getDamage(), SkillType.UNARMED); //use getDamage because damage is modified in earlier functions
                }
                else if (!pluginx.misc.mobSpawnerList.contains(target.getEntityId())) {
                    PvEExperienceGain(attacker, PPa, target, event.getDamage(), SkillType.UNARMED); //use getDamage because damage is modified in earlier functions
                }
            }
            else if (itemInHand.getType().equals(Material.BONE) && mcPermissions.getInstance().taming(attacker) && targetType.equals(EntityType.WOLF)) {
                Wolf wolf = (Wolf) target;
                String message = "Combat.BeastLore" + " ";
                int health = wolf.getHealth();
                event.setCancelled(true);

                if (wolf.isTamed()) {
                    message.concat(mcLocale.getString("Combat.BeastLoreOwner", new Object[] {Taming.getOwnerName(wolf)}) + " ");
                    message.concat(mcLocale.getString("Combat.BeastLoreHealthWolfTamed", new Object[] {health}));
                }
                else {
                    message.concat(mcLocale.getString("Combat.BeastLoreHealthWolf", new Object[] {health}));
                }

                attacker.sendMessage(message);
            }
            break;

        case WOLF:
            Wolf wolf = (Wolf) damager;

            if (wolf.isTamed() && wolf.getOwner() instanceof Player) {
                Player master = (Player) wolf.getOwner();
                PlayerProfile PPo = Users.getProfile(master);

                if (mcPermissions.getInstance().taming(master)) {
                    Taming.fastFoodService(PPo, wolf, event);
                    Taming.sharpenedClaws(PPo, event);
                    Taming.gore(PPo, event, master, pluginx);
                    Taming.rewardXp(event, pluginx, master);
                }
            }
            break;

        case ARROW:
            archeryCheck((EntityDamageByEntityEvent)event, pluginx);
            break;

        default:
            break;
        }

        if (targetType.equals(EntityType.PLAYER)) {
            Swords.counterAttackChecks(event);
            Acrobatics.dodgeChecks(event);
        }
    }

    /**
     * Process combat abilities based on weapon preparation modes.
     *
     * @param attacker The player attacking
     */
    public static void combatAbilityChecks(Player attacker) {
        PlayerProfile PPa = Users.getProfile(attacker);

        if (PPa.getAxePreparationMode()) {
            Skills.abilityCheck(attacker, SkillType.AXES);
        }
        else if (PPa.getSwordsPreparationMode()) {
            Skills.abilityCheck(attacker, SkillType.SWORDS);
        }
        else if (PPa.getFistsPreparationMode()) {
            Skills.abilityCheck(attacker, SkillType.UNARMED);
        }
    }

    /**
     * Process archery abilities.
     *
     * @param event The event to run the archery checks on.
     * @param pluginx mcMMO plugin instance
     */
    public static void archeryCheck(EntityDamageByEntityEvent event, mcMMO pluginx) {
        Arrow arrow = (Arrow) event.getDamager();
        LivingEntity shooter = arrow.getShooter();
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            Player defender = (Player) entity;
            PlayerProfile PPd = Users.getProfile(defender);
            boolean deflect = false;

            if (mcPermissions.getInstance().unarmed(defender) && defender.getItemInHand().getType().equals(Material.AIR)) {
                if (PPd.getSkillLevel(SkillType.UNARMED) >= 1000 && (Math.random() * 1000 <= 500)) {
                    deflect = true;
                }
                else if (Math.random() * 1000 <= (PPd.getSkillLevel(SkillType.UNARMED) / 2)) {
                    deflect = true;
                }
                
                if (deflect) {
                    event.setCancelled(true);
                    defender.sendMessage(mcLocale.getString("Combat.ArrowDeflect"));
                    return;
                }
            }
        }

        if (shooter instanceof Player) {
            Player attacker = (Player) shooter;
            PlayerProfile PPa = Users.getProfile(attacker);
            int damage = event.getDamage();

            if (mcPermissions.getInstance().archery(attacker) && damage > 0) {
                Archery.trackArrows(pluginx, entity, PPa);
                Archery.ignitionCheck(entity, attacker);

                if (!pluginx.misc.mobSpawnerList.contains(entity.getEntityId())) {
                    int xp = getXp((LivingEntity) entity, damage);
                    PPa.addXP(SkillType.ARCHERY, xp*10, attacker);
                }

                if (entity instanceof Player) {
                    Player defender = (Player) entity;
                    PlayerProfile PPd = Users.getProfile(defender);

                    if (PPa.inParty() && PPd.inParty() && Party.getInstance().inSameParty(defender, attacker)) {
                        event.setCancelled(true);
                        return;
                    }

                    if (LoadProperties.pvpxp && (((PPd.getLastLogin() + 5) * 1000) < System.currentTimeMillis()) && !attacker.getName().equals(defender.getName())) {
                        int xp = (damage * 2) * 10; //What's the 2 for? Should this be a multiplier from file instead?
                        PPa.addXP(SkillType.ARCHERY, xp, attacker);
                    }

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
    public static void dealDamage(LivingEntity target, int dmg) {
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
        if (LoadProperties.eventCallback) {
            EntityDamageEvent ede = (EntityDamageEvent) new FakeEntityDamageEvent(target, cause, dmg);
            Bukkit.getPluginManager().callEvent(ede);

            if (ede.isCancelled()) {
                return;
            }

            target.damage(ede.getDamage());
        }
        else {
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
        if (LoadProperties.eventCallback) {
            EntityDamageEvent ede = (EntityDamageByEntityEvent) new FakeEntityDamageByEntityEvent(attacker, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, dmg);
            Bukkit.getPluginManager().callEvent(ede);

            if (ede.isCancelled()) {
                return;
            }

            target.damage(ede.getDamage());
        }
        else {
            target.damage(dmg);
        }
    }

    /**
     * Process PVP experience gain.
     *
     * @param attacker The attacking player
     * @param PPa The profile of the attacking player
     * @param defender The defending player
     * @param damage The initial damage amount
     * @param skillType The skill being used
     */
    private static void PvPExperienceGain(Player attacker, PlayerProfile PPa, Player defender, int damage, SkillType skillType) {
        if (!LoadProperties.pvpxp) {
            return;
        }

        PlayerProfile PPd = Users.getProfile(defender);
        int health = defender.getHealth();

        if ((System.currentTimeMillis() >= (PPd.getRespawnATS()*1000) + 5000) && (((PPd.getLastLogin()+5)*1000) < System.currentTimeMillis()) && health >= 1) {
            int xp = capXP(health, damage);

            xp = (int) (xp * 2 * LoadProperties.pvpxprewardmodifier);
            PPa.addXP(skillType, xp * 10, attacker);
            Skills.XpCheckSkill(skillType, attacker);
          }
    }

    /**
     * Process PVE experience gain.
     *
     * @param attacker The attacking player
     * @param PPa The profile of the attacking player
     * @param target The defending entity
     * @param damage The initial damage amount
     * @param skillType The skill being used
     */
    private static void PvEExperienceGain(Player attacker, PlayerProfile PPa, LivingEntity target, int damage, SkillType skillType) {
        int xp = getXp(target, damage);

        PPa.addXP(skillType, xp * 10, attacker);
        Skills.XpCheckSkill(skillType, attacker);
    }

    /**
     * Cap the XP based on the remaining health of an entity.
     *
     * @param hpLeft Amount of HP remaining
     * @param damage Amount of damage being dealt
     * @return the modified XP amount
     */
    private static int capXP(int hpLeft, int damage) {
        int xp;

        if (hpLeft < damage) {
            if (hpLeft > 0) {
                xp = hpLeft;
            }
            else {
                xp = 0;
            }
        }
        else {
            xp = damage;
        }

        return xp;
    }

    /**
     * Get the XP gained from damaging a non-player mob
     *
     * @param entity Entity being damaged
     * @param damage Damage to be dealt to the mob
     * @return XP gained
     */
    public static int getXp(LivingEntity entity, int damage) {
        int xp = capXP(entity.getHealth(), damage);

        if (entity instanceof Animals) {
            xp = (int) (xp * LoadProperties.animalXP);
        }
        else {
            EntityType type = entity.getType();

            switch (type) {
            case BLAZE:
                xp = (int) (xp * LoadProperties.blazeXP);
                break;

            case CAVE_SPIDER:
                xp = (int) (xp * LoadProperties.cavespiderXP);
                break;

            case CREEPER:
                xp = (int) (xp * LoadProperties.creeperXP);
                break;

            case ENDER_DRAGON:
                xp = (int) (xp * LoadProperties.enderdragonXP);
                break;

            case ENDERMAN:
                xp = (int) (xp * LoadProperties.endermanXP);
                break;

            case GHAST:
                xp = (int) (xp * LoadProperties.ghastXP);
                break;

            case MAGMA_CUBE:
                xp = (int) (xp * LoadProperties.magmacubeXP);
                break;

            case PIG_ZOMBIE:
                xp = (int) (xp * LoadProperties.pigzombieXP);
                break;

            case SILVERFISH:
                xp = (int) (xp * LoadProperties.silverfishXP);
                break;

            case SKELETON:
                xp = (int) (xp * LoadProperties.skeletonXP);
                break;

            case SLIME:
                xp = (int) (xp * LoadProperties.slimeXP);
                break;

            case SPIDER:
                xp = (int) (xp * LoadProperties.spiderXP);
                break;

            case ZOMBIE:
                xp = (int) (xp * LoadProperties.zombieXP);
                break;

            default:
                break;
            }
        }
        return xp;
    }

    /**
     * Apply Area-of-Effect ability actions.
     *
     * @param attacker The attacking player
     * @param target The defending entity
     * @param damage The initial damage amount
     * @param plugin mcMMO plugin instance
     * @param type The type of skill being used
     */
    private static void applyAbilityAoE(Player attacker, LivingEntity target, int damage, mcMMO plugin, SkillType type) {
        int numberOfTargets = m.getTier(attacker.getItemInHand()); //The higher the weapon tier, the more targets you hit
        int damageAmount = 0;

        if (type.equals(SkillType.AXES)) {
            damageAmount = damage / 2;
        }
        else if (type.equals(SkillType.SWORDS)) {
            damageAmount = damage / 4;
        }

        if (damageAmount < 1) {
            damageAmount = 1;
        }

        for (Entity entity : target.getNearbyEntities(2.5, 2.5, 2.5)) {
            EntityType entityType = entity.getType();

            if (entityType.equals(EntityType.WOLF)) {
                Wolf wolf = (Wolf) entity;
                AnimalTamer tamer = wolf.getOwner();

                if (tamer instanceof Player) {
                    Player owner = (Player) tamer;

                    //Reasons why the target shouldn't be hit
                    if (owner.equals(attacker)) {
                        continue;
                    }

                    if (Party.getInstance().inSameParty(attacker, owner)) {
                        continue;
                    }
                }
            }

            if (entity instanceof LivingEntity && numberOfTargets >= 1) {
                if (entityType.equals(EntityType.PLAYER)) {
                    Player defender = (Player) entity;
                    PlayerProfile PP = Users.getProfile(defender);

                    //Reasons why the target shouldn't be hit
                    if (PP.getGodMode()) {
                        continue;
                    }

                    if (defender.getName().equals(attacker.getName())) { //Is this even possible?
                        continue;
                    }

                    if (Party.getInstance().inSameParty(attacker, defender)) {
                        continue;
                    }

                    if (defender.isDead()) {
                        continue;
                    }

                    //Apply effect to players only if PVP is enabled
                    if (target.getWorld().getPVP()) {
                        String message = "";

                        if (type.equals(SkillType.AXES)) {
                            message = mcLocale.getString("Axes.HitByCleave");
                        }
                        else if (type.equals(SkillType.SWORDS)) {
                            message = mcLocale.getString("Swords.HitBySerratedStrikes");
                        }

                        dealDamage(defender, damageAmount, attacker);
                        defender.sendMessage(message);

                        if (type.equals(SkillType.SWORDS)) {
                            PP.addBleedTicks(5);
                        }

                        numberOfTargets--;
                    }
                }
                else {
                    LivingEntity livingEntity = (LivingEntity) entity;

                    if (type.equals(SkillType.SWORDS) && !plugin.misc.bleedTracker.contains(entity)) {
                        plugin.misc.addToBleedQue(livingEntity);
                    }

                    dealDamage(livingEntity, damageAmount, attacker);
                    numberOfTargets--;
                }
            }
        }
    }
}
