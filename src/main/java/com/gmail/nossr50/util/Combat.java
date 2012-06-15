package com.gmail.nossr50.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.datatypes.ToolType;
import com.gmail.nossr50.events.fake.FakeEntityDamageByEntityEvent;
import com.gmail.nossr50.events.fake.FakeEntityDamageEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.runnables.BleedTimer;
import com.gmail.nossr50.runnables.GainXp;
import com.gmail.nossr50.skills.acrobatics.AcrobaticsManager;
import com.gmail.nossr50.skills.archery.ArcheryManager;
import com.gmail.nossr50.skills.combat.Axes;
import com.gmail.nossr50.skills.combat.Swords;
import com.gmail.nossr50.skills.combat.Unarmed;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;

public class Combat {
    private static Config configInstance = Config.getInstance();
    private static Permissions permInstance = Permissions.getInstance();

    /**
     * Apply combat modifiers and process and XP gain.
     *
     * @param event The event to run the combat checks on.
     */
    public static void combatChecks(EntityDamageByEntityEvent event) {
        if (event.getDamage() == 0 || event.getEntity().isDead()) {
            return;
        }

        Entity damager = event.getDamager();
        LivingEntity target = (LivingEntity) event.getEntity();

        boolean targetIsPlayer = target instanceof Player;
        boolean targetIsTamedPet = (target instanceof Tameable) ? ((Tameable) target).isTamed() : false;

        if (damager instanceof Player) {
            Player attacker = (Player) event.getDamager();
            ItemStack itemInHand = attacker.getItemInHand();
            PlayerProfile PPa = Users.getProfile(attacker);

            combatAbilityChecks(attacker);

            if (ItemChecks.isSword(itemInHand) && permInstance.swords(attacker)) {
                if (!configInstance.getSwordsPVP()) {
                    if (targetIsPlayer || targetIsTamedPet) {
                        return;
                    }
                }

                if (!configInstance.getSwordsPVE()) {
                    if (!targetIsPlayer || !targetIsTamedPet) {
                        return;
                    }
                }

                if (permInstance.swordsBleed(attacker)) {
                    Swords.bleedCheck(attacker, target);
                }

                if (PPa.getAbilityMode(AbilityType.SERRATED_STRIKES) && permInstance.serratedStrikes(attacker)) {
                    applyAbilityAoE(attacker, target, event.getDamage() / 4, SkillType.SWORDS);
                    BleedTimer.add(target, 5);
                }

                startGainXp(attacker, PPa, target, SkillType.SWORDS);
            }
            else if (ItemChecks.isAxe(itemInHand) && permInstance.axes(attacker)) {
                if (!configInstance.getAxesPVP()) {
                    if (targetIsPlayer || targetIsTamedPet) {
                        return;
                    }
                }

                if (!configInstance.getAxesPVE()) {
                    if (!targetIsPlayer || !targetIsTamedPet) {
                        return;
                    }
                }

                if (permInstance.axeBonus(attacker)) {
                    Axes.axesBonus(attacker, event);
                }

                if (permInstance.criticalHit(attacker)) {
                    Axes.axeCriticalCheck(attacker, event);
                }

                if (permInstance.impact(attacker)) {
                    Axes.impact(attacker, target, event);
                }

                if (PPa.getAbilityMode(AbilityType.SKULL_SPLIITER) && permInstance.skullSplitter(attacker)) {
                    applyAbilityAoE(attacker, target, event.getDamage() / 2, SkillType.AXES);
                }

                startGainXp(attacker, PPa, target, SkillType.AXES);
            }
            else if (itemInHand.getType().equals(Material.AIR) && permInstance.unarmed(attacker)) {
                if (!configInstance.getUnarmedPVP()) {
                    if (targetIsPlayer || targetIsTamedPet) {
                        return;
                    }
                }

                if (!configInstance.getUnarmedPVE()) {
                    if (!targetIsPlayer || !targetIsTamedPet) {
                        return;
                    }
                }

                UnarmedManager unarmedManager = new UnarmedManager(attacker);

                if (permInstance.unarmedBonus(attacker)) {
                    Unarmed.unarmedBonus(PPa, event);
                }

                if (PPa.getAbilityMode(AbilityType.BERSERK) && permInstance.berserk(attacker)) {
                    event.setDamage((int) (event.getDamage() * 1.5));
                }

                if (targetIsPlayer) {
                    unarmedManager.disarmCheck((Player) target);
                }

                startGainXp(attacker, PPa, target, SkillType.UNARMED);
            }
            else if (itemInHand.getType() == Material.BONE && target instanceof Tameable) {
                TamingManager tamingManager = new TamingManager(attacker);
                tamingManager.beastLore(target);
                event.setCancelled(true);
            }
        }
        else if (damager instanceof Wolf) {
            Wolf wolf = (Wolf) damager;

            if (wolf.isTamed() && wolf.getOwner() instanceof Player) {
                Player master = (Player) wolf.getOwner();

                if (!configInstance.getTamingPVP()) {
                    if (targetIsPlayer || targetIsTamedPet) {
                        return;
                    }
                }

                if (!configInstance.getTamingPVE()) {
                    if (!targetIsPlayer || !targetIsTamedPet) {
                        return;
                    }
                }

                TamingManager tamingManager = new TamingManager(master);

                tamingManager.fastFoodService(wolf, event.getDamage());
                tamingManager.sharpenedClaws(event);
                tamingManager.gore(event);

                startGainXp(master, Users.getProfile(master), target, SkillType.TAMING);
            }
        }
        else if (damager instanceof Arrow) {
            LivingEntity shooter = ((Arrow) damager).getShooter();

            if (shooter.getType() != EntityType.PLAYER) {
                return;
            }

            if (targetIsPlayer || targetIsTamedPet) {
                if (!configInstance.getArcheryPVP()) {
                    return;
                }
            }
            else if (!configInstance.getArcheryPVE()) {
                return;
            }

            archeryCheck((Player) shooter, target, event);
        }

        if (target instanceof Player) {
            AcrobaticsManager acroManager = new AcrobaticsManager((Player) target);

            if (configInstance.getSwordsPVP() && damager instanceof Player) {
                Swords.counterAttackChecks(damager, (Player) target, event.getDamage());
            }

            if (configInstance.getSwordsPVE() && !(damager instanceof Player)) {
                Swords.counterAttackChecks(damager, (Player) target, event.getDamage());
            }

            if (configInstance.getAcrobaticsPVP() && damager instanceof Player) {
                acroManager.dodgeCheck(event);
            }

            if (configInstance.getAcrobaticsPVE() && !(damager instanceof Player)) {
                acroManager.dodgeCheck(event);
            }
        }
    }

    /**
     * Process combat abilities based on weapon preparation modes.
     *
     * @param attacker The player attacking
     */
    public static void combatAbilityChecks(Player attacker) {
        PlayerProfile PPa = Users.getProfile(attacker);

        if (PPa.getToolPreparationMode(ToolType.AXE)) {
            Skills.abilityCheck(attacker, SkillType.AXES);
        }
        else if (PPa.getToolPreparationMode(ToolType.SWORD)) {
            Skills.abilityCheck(attacker, SkillType.SWORDS);
        }
        else if (PPa.getToolPreparationMode(ToolType.FISTS)) {
            Skills.abilityCheck(attacker, SkillType.UNARMED);
        }
    }

    /**
     * Process archery abilities.
     *
     * @param shooter The player shooting
     * @param target The defending entity
     * @param event The event to run the archery checks on.
     */
    public static void archeryCheck(Player shooter, LivingEntity target, EntityDamageByEntityEvent event) {
        if (target instanceof Player) {
            Player defender = (Player) target;

            if (defender.getItemInHand().getType().equals(Material.AIR)) {
                if (configInstance.getUnarmedPVP()) {
                    Unarmed.deflectCheck(defender, event);
                }
            }
        }

        ArcheryManager archeryManager = new ArcheryManager(shooter);

        archeryManager.bonusDamage(event);

        if (target instanceof Player) {
            archeryManager.dazeCheck((Player) target, event);
        }

        if (!(shooter.getItemInHand().containsEnchantment(Enchantment.ARROW_INFINITE))) {
            archeryManager.trackArrows(target);
        }

        if (target != shooter) {
            PlayerProfile PP = Users.getProfile(shooter);
            startGainXp(shooter, PP, target, SkillType.ARCHERY);
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
        if (configInstance.getEventCallbackEnabled()) {
            EntityDamageEvent ede = new FakeEntityDamageEvent(target, cause, dmg);
            mcMMO.p.getServer().getPluginManager().callEvent(ede);

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
        if (configInstance.getEventCallbackEnabled()) {
            EntityDamageEvent ede = new FakeEntityDamageByEntityEvent(attacker, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, dmg);
            mcMMO.p.getServer().getPluginManager().callEvent(ede);

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
     * @param type The type of skill being used
     */
    private static void applyAbilityAoE(Player attacker, LivingEntity target, int damage, SkillType type) {
        ItemStack inHand = attacker.getItemInHand();

        if (ModChecks.isCustomTool(inHand) && !ModChecks.getToolFromItemStack(inHand).isAbilityEnabled()) {
            return;
        }

        int numberOfTargets = Misc.getTier(attacker.getItemInHand()); //The higher the weapon tier, the more targets you hit
        int damageAmount = damage;

        if (damageAmount < 1) {
            damageAmount = 1;
        }

        for (Entity entity : target.getNearbyEntities(2.5, 2.5, 2.5)) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }

            if (numberOfTargets <= 0) {
                break;
            }

            PlayerAnimationEvent armswing = new PlayerAnimationEvent(attacker);
            mcMMO.p.getServer().getPluginManager().callEvent(armswing);

            if (entity instanceof Player) {
                Player defender = (Player) entity;

                if (!target.getWorld().getPVP()) {
                    continue;
                }

                if (defender.getName().equals(attacker.getName())) {
                    continue;
                }

                if (PartyManager.getInstance().inSameParty(attacker, defender)) {
                    continue;
                }

                PlayerProfile playerProfile = Users.getProfile((Player) entity);

                if (playerProfile.getGodMode()) {
                    continue;
                }
            }
            else if (entity instanceof Tameable) {
                AnimalTamer tamer = ((Tameable) entity).getOwner();

                if (tamer instanceof Player) {
                    if (tamer.equals(attacker) || PartyManager.getInstance().inSameParty(attacker, (Player) tamer)) {
                        continue;
                    }
                }
            }

            switch (type) {
            case SWORDS:
                if (entity instanceof Player) {
                    ((Player) entity).sendMessage(LocaleLoader.getString("Swords.Combat.SS.Struck"));
                }

                BleedTimer.add((LivingEntity) entity, 5);

                break;

            case AXES:
                if (entity instanceof Player) {
                    ((Player) entity).sendMessage(LocaleLoader.getString("Axes.Combat.Cleave.Struck"));
                }

                break;

            default:
                break;
            }

            dealDamage((LivingEntity) entity, damageAmount, attacker);
            numberOfTargets--;
        }
    }

    /**
     * Start the task that gives combat XP.
     *
     * @param attacker The attacking player
     * @param PP The player's PlayerProfile
     * @param target The defending entity
     * @param skillType The skill being used
     */
    public static void startGainXp(Player attacker, PlayerProfile PP, LivingEntity target, SkillType skillType) {
        double baseXP = 0;

        if (target instanceof Player) {
            if (!configInstance.getExperienceGainsPlayerVersusPlayerEnabled()) {
                return;
            }

            Player defender = (Player) target;

            if (System.currentTimeMillis() >= Users.getProfile(defender).getRespawnATS() + 5) {
                baseXP = 20 * configInstance.getPlayerVersusPlayerXP();
            }
        }
        else if (!target.hasMetadata("mcmmoFromMobSpawner")) {
            if (target instanceof Animals && !target.hasMetadata("mcmmoSummoned")) {
                baseXP = configInstance.getAnimalsXP();
            }
            else {
                EntityType type = target.getType();

                switch (type) {
                case BLAZE:
                    baseXP = configInstance.getBlazeXP();
                    break;

                case CAVE_SPIDER:
                    baseXP = configInstance.getCaveSpiderXP();
                    break;

                case CREEPER:
                    baseXP = configInstance.getCreeperXP();
                    break;

                case ENDER_DRAGON:
                    baseXP = configInstance.getEnderDragonXP();
                    break;

                case ENDERMAN:
                    baseXP = configInstance.getEndermanXP();
                    break;

                case GHAST:
                    baseXP = configInstance.getGhastXP();
                    break;

                case MAGMA_CUBE:
                    baseXP = configInstance.getMagmaCubeXP();
                    break;

                case IRON_GOLEM:
                    if (!((IronGolem) target).isPlayerCreated()) {
                        baseXP = configInstance.getIronGolemXP();
                    }

                    break;

                case PIG_ZOMBIE:
                    baseXP = configInstance.getPigZombieXP();
                    break;

                case SILVERFISH:
                    baseXP = configInstance.getSilverfishXP();
                    break;

                case SKELETON:
                    baseXP = configInstance.getSkeletonXP();
                    break;

                case SLIME:
                    baseXP = configInstance.getSlimeXP();
                    break;

                case SPIDER:
                    baseXP = configInstance.getSpiderXP();
                    break;

                case ZOMBIE:
                    baseXP = configInstance.getZombieXP();
                    break;

                default:
                    break;
                }
            }

            baseXP *= 10;
        }

        if (baseXP != 0) {
            mcMMO.p.getServer().getScheduler().scheduleSyncDelayedTask(mcMMO.p, new GainXp(attacker, PP, skillType, baseXP, target), 0);
        }
    }
}
