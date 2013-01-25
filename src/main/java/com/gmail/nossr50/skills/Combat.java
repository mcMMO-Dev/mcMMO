package com.gmail.nossr50.skills;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.events.fake.FakeEntityDamageByEntityEvent;
import com.gmail.nossr50.events.fake.FakeEntityDamageEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.runnables.BleedTimer;
import com.gmail.nossr50.runnables.GainXp;
import com.gmail.nossr50.skills.acrobatics.Acrobatics;
import com.gmail.nossr50.skills.acrobatics.AcrobaticsManager;
import com.gmail.nossr50.skills.archery.Archery;
import com.gmail.nossr50.skills.archery.ArcheryManager;
import com.gmail.nossr50.skills.axes.AxeManager;
import com.gmail.nossr50.skills.axes.Axes;
import com.gmail.nossr50.skills.swords.Swords;
import com.gmail.nossr50.skills.swords.SwordsManager;
import com.gmail.nossr50.skills.taming.Taming;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.unarmed.Unarmed;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;
import com.gmail.nossr50.util.ItemChecks;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Users;

public class Combat {
    private static Config configInstance = Config.getInstance();

    /**
     * Apply combat modifiers and process and XP gain.
     *
     * @param event The event to run the combat checks on.
     */
    public static void combatChecks(EntityDamageByEntityEvent event, Entity damager, LivingEntity target) {
        boolean targetIsPlayer = (target.getType() == EntityType.PLAYER);
        boolean targetIsTamedPet = (target instanceof Tameable) ? ((Tameable) target).isTamed() : false;

        switch (event.getDamager().getType()) {
        case PLAYER:
            Player attacker = (Player) event.getDamager();

            if (Misc.isNPCPlayer(attacker)) {
                return;
            }

            ItemStack heldItem = attacker.getItemInHand();
            Material heldItemType = heldItem.getType();

            if (ItemChecks.isSword(heldItem)) {
                if (targetIsPlayer || targetIsTamedPet) {
                    if (!Swords.pvpEnabled) {
                        return;
                    }
                }
                else if (!Swords.pveEnabled) {
                    return;
                }

                Skills.abilityCheck(attacker, SkillType.SWORDS);

                SwordsManager swordsManager = new SwordsManager(attacker);
                swordsManager.bleedCheck(target);
                swordsManager.serratedStrikes(target, event.getDamage());

                startGainXp(attacker, swordsManager.getProfile(), target, SkillType.SWORDS);
            }
            else if (ItemChecks.isAxe(heldItem)) {
                if (targetIsPlayer || targetIsTamedPet) {
                    if (!Axes.pvpEnabled) {
                        return;
                    }
                }
                else if (!Axes.pveEnabled) {
                    return;
                }

                Skills.abilityCheck(attacker, SkillType.AXES);

                AxeManager axeManager = new AxeManager(attacker);
                axeManager.bonusDamage(event);
                axeManager.criticalHitCheck(event, target);
                axeManager.impact(event, target);
                axeManager.skullSplitter(target, event.getDamage());

                startGainXp(attacker, axeManager.getProfile(), target, SkillType.AXES);
            }
            else if (heldItemType == Material.AIR) {
                if (targetIsPlayer || targetIsTamedPet) {
                    if (!configInstance.getUnarmedPVP()) {
                        return;
                    }
                }
                else if (!configInstance.getUnarmedPVE()) {
                    return;
                }

                Skills.abilityCheck(attacker, SkillType.UNARMED);

                UnarmedManager unarmedManager = new UnarmedManager(attacker);
                unarmedManager.bonusDamage(event);
                unarmedManager.berserkDamage(event);
                unarmedManager.disarmCheck(target);

                startGainXp(attacker, unarmedManager.getProfile(), target, SkillType.UNARMED);
            }
            else if (heldItemType == Material.BONE && target instanceof Tameable) {
                TamingManager tamingManager = new TamingManager(attacker);
                tamingManager.beastLore(target);
                event.setCancelled(true);
            }

            break;

        case WOLF:
            Wolf wolf = (Wolf) event.getDamager();

            if (wolf.isTamed() && wolf.getOwner() instanceof Player) {
                Player master = (Player) wolf.getOwner();

                if (targetIsPlayer || targetIsTamedPet) {
                    if (!Taming.pvpEnabled) {
                        return;
                    }
                }
                else if (!Taming.pveEnabled) {
                    return;
                }

                TamingManager tamingManager = new TamingManager(master);
                tamingManager.fastFoodService(wolf, event.getDamage());
                tamingManager.sharpenedClaws(event);
                tamingManager.gore(event);

                startGainXp(master, tamingManager.getProfile(), target, SkillType.TAMING);
            }

            break;

        case ARROW:
            LivingEntity shooter = ((Arrow) event.getDamager()).getShooter();

            //TODO: Is there a reason we're breaking here instead of returning?
            if (shooter == null || shooter.getType() != EntityType.PLAYER) {
                break;
            }

            if (targetIsPlayer || targetIsTamedPet) {
                if (!Archery.pvpEnabled) {
                    return;
                }
            }
            else if (!Archery.pveEnabled) {
                return;
            }

            archeryCheck((Player) shooter, target, event);
            break;

        default:
            break;
        }

        if (targetIsPlayer) {
            Player player = (Player) target;

            if (event.getDamager() instanceof Player) {
                if (Swords.pvpEnabled) {
                    SwordsManager swordsManager = new SwordsManager(player);
                    swordsManager.counterAttackChecks((LivingEntity) event.getDamager(), event.getDamage());
                }

                if (Acrobatics.pvpEnabled) {
                    AcrobaticsManager acrobaticsManager = new AcrobaticsManager(player);
                    acrobaticsManager.dodgeCheck(event);
                }

                if (Unarmed.pvpEnabled && player.getItemInHand().getType() == Material.AIR) {
                    UnarmedManager unarmedManager = new UnarmedManager(player);
                    unarmedManager.deflectCheck(event);
                }
            }
            else {
                if (Swords.pveEnabled && event.getDamager() instanceof LivingEntity) {
                    SwordsManager swordsManager = new SwordsManager(player);
                    swordsManager.counterAttackChecks((LivingEntity) event.getDamager(), event.getDamage());
                }

                if (Acrobatics.pveEnabled) {
                    if (!(event.getDamager() instanceof LightningStrike && Acrobatics.dodgeLightningDisabled)) {
                        AcrobaticsManager acrobaticsManager = new AcrobaticsManager(player);
                        acrobaticsManager.dodgeCheck(event);
                    }
                }
            }
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
        if (Misc.isNPCPlayer(shooter)) {
            return;
        }

        ArcheryManager archeryManager = new ArcheryManager(shooter);
        archeryManager.skillShot(event);

        if (target instanceof Player) {
            archeryManager.dazeCheck((Player) target, event);
        }

        if (!(shooter.getItemInHand().containsEnchantment(Enchantment.ARROW_INFINITE))) {
            archeryManager.trackArrows(target);
        }

        if (target != shooter) {
            startGainXp(shooter, archeryManager.getProfile(), target, SkillType.ARCHERY);
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
    public static void applyAbilityAoE(Player attacker, LivingEntity target, int damage, SkillType type) {
        int numberOfTargets = Misc.getTier(attacker.getItemInHand()); //The higher the weapon tier, the more targets you hit
        int damageAmount = damage;

        if (damageAmount < 1) {
            damageAmount = 1;
        }

        for (Entity entity : target.getNearbyEntities(2.5, 2.5, 2.5)) {
            if ((entity instanceof Player && Misc.isNPCPlayer((Player) entity)) || !(entity instanceof LivingEntity) || !shouldBeAffected(attacker, entity)) {
                continue;
            }

            if (numberOfTargets <= 0) {
                break;
            }

            PlayerAnimationEvent armswing = new PlayerAnimationEvent(attacker);
            mcMMO.p.getServer().getPluginManager().callEvent(armswing);

            switch (type) {
            case SWORDS:
                if (entity instanceof Player) {
                    ((Player) entity).sendMessage(LocaleLoader.getString("Swords.Combat.SS.Struck"));
                }

                BleedTimer.add((LivingEntity) entity, Swords.serratedStrikesBleedTicks);

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
     * @param profile The player's PlayerProfile
     * @param target The defending entity
     * @param skillType The skill being used
     */
    public static void startGainXp(Player attacker, PlayerProfile profile, LivingEntity target, SkillType skillType) {
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
        else if (!mcMMO.placeStore.isSpawnedMob(target)) {
            if (target instanceof Animals && !mcMMO.placeStore.isSpawnedPet(target)) {
                baseXP = configInstance.getAnimalsXP();
            }
            else {
                EntityType type = target.getType();

                switch (type) {
                case BAT:
                    baseXP = configInstance.getAnimalsXP();
                    break;

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

                case GIANT:
                    baseXP = configInstance.getGiantXP();
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
                    switch(((Skeleton) target).getSkeletonType()) {
                    case WITHER:
                        baseXP = configInstance.getWitherSkeletonXP();
                        break;
                    default:
                        baseXP = configInstance.getSkeletonXP();
                        break;
                    }
                case SLIME:
                    baseXP = configInstance.getSlimeXP();
                    break;

                case SPIDER:
                    baseXP = configInstance.getSpiderXP();
                    break;

                case WITCH:
                    baseXP = configInstance.getWitchXP();
                    break;

                case WITHER:
                    baseXP = configInstance.getWitherXP();
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
            mcMMO.p.getServer().getScheduler().scheduleSyncDelayedTask(mcMMO.p, new GainXp(attacker, profile, skillType, baseXP, target), 0);
        }
    }

    /**
     * Check to see if the given LivingEntity should be affected by a combat ability.
     *
     * @param player The attacking Player
     * @param livingEntity The defending LivingEntity
     * @return true if the Entity should be damaged, false otherwise.
     */
    public static boolean shouldBeAffected(Player player, Entity entity) {
        if (entity instanceof Player) {
            Player defender = (Player) entity;

            if (!defender.getWorld().getPVP() || defender == player || PartyManager.getInstance().inSameParty(player, defender) || Users.getProfile(defender).getGodMode()) {
                return false;
            }

            //It may seem a bit redundant but we need a check here to prevent bleed from being applied in applyAbilityAoE()
            EntityDamageEvent ede = new FakeEntityDamageByEntityEvent(player, entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1);
            mcMMO.p.getServer().getPluginManager().callEvent(ede);

            if (ede.isCancelled()) {
                return false;
            }
        }
        else if (entity instanceof Tameable) {
            if (Misc.isFriendlyPet(player, (Tameable) entity)) {
                return false;
            }
        }

        return true;
    }
}
