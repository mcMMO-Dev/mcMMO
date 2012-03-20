package com.gmail.nossr50;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
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
import com.gmail.nossr50.runnables.GainXp;
import com.gmail.nossr50.runnables.mcBleedTimer;
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
     * @param plugin mcMMO plugin instance
     */
    public static void combatChecks(EntityDamageByEntityEvent event, mcMMO plugin) {
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
                if (!mcBleedTimer.contains(target) && mcPermissions.getInstance().swordsBleed(attacker)) {
                    Swords.bleedCheck(attacker, target, plugin);
                }

                if (PPa.getSerratedStrikesMode()) {
                    applyAbilityAoE(attacker, target, damage, plugin, SkillType.SWORDS);
                }

                startGainXp(attacker, PPa, target, SkillType.SWORDS, plugin);
            }
            else if (ItemChecks.isAxe(itemInHand) && mcPermissions.getInstance().axes(attacker)) {
                if (mcPermissions.getInstance().axeBonus(attacker)) {
                    Axes.axesBonus(attacker, event);
                }

                if (mcPermissions.getInstance().criticalHit(attacker)) {
                    Axes.axeCriticalCheck(attacker, event);
                }

                if (mcPermissions.getInstance().impact(attacker)) {
                    Axes.impact(attacker, target, event);
                }

                if (PPa.getSkullSplitterMode()) {
                    applyAbilityAoE(attacker, target, damage, plugin, SkillType.AXES);
                }

                startGainXp(attacker, PPa, target, SkillType.AXES, plugin);
            }
            else if (itemInHand.getType().equals(Material.AIR) && mcPermissions.getInstance().unarmed(attacker)) {
                if (mcPermissions.getInstance().unarmedBonus(attacker)) {
                    Unarmed.unarmedBonus(PPa, event);
                }

                if (PPa.getBerserkMode() && mcPermissions.getInstance().berserk(attacker)) {
                    event.setDamage((int) (damage * 1.5));
                }

                if (targetType.equals(EntityType.PLAYER) && mcPermissions.getInstance().disarm(attacker)) {
                    Unarmed.disarmProcCheck(PPa, (Player) target);
                }

                startGainXp(attacker, PPa, target, SkillType.UNARMED, plugin);
            }
            else if (itemInHand.getType().equals(Material.BONE) && mcPermissions.getInstance().beastLore(attacker)) {
                Taming.beastLore(event, target, attacker);
            }
            break;

        case WOLF:
            Wolf wolf = (Wolf) damager;

            if (wolf.isTamed() && wolf.getOwner() instanceof Player) {
                Player master = (Player) wolf.getOwner();
                PlayerProfile PPo = Users.getProfile(master);

                if (mcPermissions.getInstance().taming(master)) {
                    if (mcPermissions.getInstance().fastFoodService(master)) {
                        Taming.fastFoodService(PPo, wolf, event);
                    }

                    if (mcPermissions.getInstance().sharpenedclaws(master)) {
                        Taming.sharpenedClaws(PPo, event);
                    }

                    if (mcPermissions.getInstance().gore(master)) {
                        Taming.gore(PPo, event, master, plugin);
                    }

                    startGainXp(master, PPo, target, SkillType.TAMING, plugin);
                }
            }
            break;

        case ARROW:
            archeryCheck((EntityDamageByEntityEvent) event, plugin);
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
        LivingEntity target = (LivingEntity) event.getEntity();

        if (target instanceof Player) {
            Player defender = (Player) target;

            if (mcPermissions.getInstance().unarmed(defender) && defender.getItemInHand().getType().equals(Material.AIR)) {
                Unarmed.deflectCheck(defender, event);
            }
        }

        if (shooter instanceof Player) {
            Player attacker = (Player) shooter;
            PlayerProfile PPa = Users.getProfile(attacker);
            int damage = event.getDamage();

            if (mcPermissions.getInstance().archery(attacker) && damage > 0) {
                if (mcPermissions.getInstance().trackArrows(attacker)) {
                    Archery.trackArrows(pluginx, target, PPa);
                }

                if (mcPermissions.getInstance().ignition(attacker)) {
                    Archery.ignitionCheck(target, attacker);
                }

                startGainXp(attacker, PPa, target, SkillType.ARCHERY, pluginx);

                if (target instanceof Player) {
                    Player defender = (Player) target;
                    PlayerProfile PPd = Users.getProfile(defender);

                    if (PPa.inParty() && PPd.inParty() && Party.getInstance().inSameParty(defender, attacker)) {
                        event.setCancelled(true);
                        return;
                    }

                    Archery.dazeCheck(defender, attacker);
                }
            }
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
    private static void dealDamage(LivingEntity target, int dmg, DamageCause cause) {
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
    private static void dealDamage(LivingEntity target, int dmg, Player attacker) {
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

                    if (owner.equals(attacker) || Party.getInstance().inSameParty(attacker, owner)) {
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

                    if (type.equals(SkillType.SWORDS)) {
                        mcBleedTimer.add(livingEntity);
                    }

                    dealDamage(livingEntity, damageAmount, attacker);
                    numberOfTargets--;
                }
            }
        }
    }

    /**
     * Start the task that gives combat XP.
     *
     * @param attacker The attacking player
     * @param PP The player's PlayerProfile
     * @param target The defending entity
     * @param skillType The skill being used
     * @param plugin mcMMO plugin instance
     */
    public static void startGainXp(Player attacker, PlayerProfile PP, LivingEntity target, SkillType skillType, mcMMO pluginx) {
        double baseXP = 0;

        if (target instanceof Player) {
            if (!LoadProperties.pvpxp) {
                return;
            }

            Player defender = (Player) target;
            PlayerProfile PPd = Users.getProfile(defender);

            if (System.currentTimeMillis() >= (PPd.getRespawnATS() * 1000) + 5000 && ((PPd.getLastLogin() + 5) * 1000) < System.currentTimeMillis() && defender.getHealth() >= 1) {
                baseXP = 20 * LoadProperties.pvpxprewardmodifier;
            }
        }
        else if (!target.hasMetadata("mcmmoFromMobSpawner")) {
            if (target instanceof Animals && !target.hasMetadata("mcmmoSummoned")) {
                baseXP = LoadProperties.animalXP;
            }
            else {
                EntityType type = target.getType();

                switch (type) {
                case BLAZE:
                    baseXP = LoadProperties.blazeXP;
                    break;

                case CAVE_SPIDER:
                    baseXP = LoadProperties.cavespiderXP;
                    break;

                case CREEPER:
                    baseXP = LoadProperties.creeperXP;
                    break;

                case ENDER_DRAGON:
                    baseXP = LoadProperties.enderdragonXP;
                    break;

                case ENDERMAN:
                    baseXP = LoadProperties.endermanXP;
                    break;

                case GHAST:
                    baseXP = LoadProperties.ghastXP;
                    break;

                case MAGMA_CUBE:
                    baseXP = LoadProperties.magmacubeXP;
                    break;

                case PIG_ZOMBIE:
                    baseXP = LoadProperties.pigzombieXP;
                    break;

                case SILVERFISH:
                    baseXP = LoadProperties.silverfishXP;
                    break;

                case SKELETON:
                    baseXP = LoadProperties.skeletonXP;
                    break;

                case SLIME:
                    baseXP = LoadProperties.slimeXP;
                    break;

                case SPIDER:
                    baseXP = LoadProperties.spiderXP;
                    break;

                case ZOMBIE:
                    baseXP = LoadProperties.zombieXP;
                    break;

                default:
                    break;
                }
            }

            baseXP *= 10;
        }

        if (baseXP != 0) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(pluginx, new GainXp(attacker, PP, skillType, baseXP, target), 0);
        }
    }
}
