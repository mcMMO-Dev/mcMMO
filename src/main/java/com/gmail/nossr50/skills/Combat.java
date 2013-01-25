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
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class Combat {
    private static Config configInstance = Config.getInstance();

    /**
     * Apply combat modifiers and process and XP gain.
     *
     * @param event The event to run the combat checks on.
     */
    public static void combatChecks(EntityDamageByEntityEvent event, Entity attacker, LivingEntity target) {
        boolean targetIsPlayer = (target.getType() == EntityType.PLAYER);
        boolean targetIsTamedPet = (target instanceof Tameable) ? ((Tameable) target).isTamed() : false;

        if (attacker instanceof Player) {
            Player player = (Player) attacker;

            if (Misc.isNPCPlayer(player)) {
                return;
            }

            ItemStack heldItem = player.getItemInHand();
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

                if (Permissions.swords(player)) {
                    SwordsManager swordsManager = new SwordsManager(player);
                    PlayerProfile profile = swordsManager.getProfile();
                    boolean canSerratedStrike = Permissions.serratedStrikes(player); //So we don't have to check the same permission twice

                    if (profile.getToolPreparationMode(ToolType.SWORD) && canSerratedStrike) {
                        Skills.abilityCheck(player, SkillType.SWORDS);
                    }

                    if (Permissions.swordsBleed(player) && shouldBeAffected(player, target)) {
                        swordsManager.bleedCheck(target);
                    }

                    if (profile.getAbilityMode(AbilityType.SERRATED_STRIKES) && canSerratedStrike) {
                        swordsManager.serratedStrikes(target, event.getDamage());
                    }

                    startGainXp(player, profile, target, SkillType.SWORDS);
                }
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

                if (Permissions.axes(player)) {
                    AxeManager axeManager = new AxeManager(player);
                    PlayerProfile profile = axeManager.getProfile();
                    boolean canSkullSplit = Permissions.skullSplitter(player); //So we don't have to check the same permission twice
                    if (profile.getToolPreparationMode(ToolType.AXE) && canSkullSplit) {
                        Skills.abilityCheck(player, SkillType.AXES);
                    }

                    if (Permissions.axeBonus(player)) {
                        axeManager.bonusDamage(event);
                    }

                    if (!target.isDead() && Permissions.criticalHit(player) && shouldBeAffected(player, target)) {
                        axeManager.criticalHitCheck(event, target);
                    }

                    if (!target.isDead() && Permissions.impact(player)) {
                        axeManager.impact(event, target);
                    }

                    if (!target.isDead() && profile.getAbilityMode(AbilityType.SKULL_SPLIITER) && canSkullSplit) {
                        axeManager.skullSplitter(target, event.getDamage());
                    }

                    startGainXp(player, profile, target, SkillType.AXES);
                }
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

                if (Permissions.unarmed(player)) {
                    UnarmedManager unarmedManager = new UnarmedManager(player);
                    PlayerProfile profile = unarmedManager.getProfile();
                    boolean canBerserk = Permissions.berserk(player); //So we don't have to check the same permission twice

                    if (profile.getToolPreparationMode(ToolType.FISTS) && canBerserk) {
                        Skills.abilityCheck(player, SkillType.UNARMED);
                    }

                    if (Permissions.unarmedBonus(player)) {
                        unarmedManager.bonusDamage(event);
                    }

                    if (profile.getAbilityMode(AbilityType.BERSERK) && canBerserk) {
                        unarmedManager.berserkDamage(event);
                    }

                    if (target instanceof Player && Permissions.disarm(player)) {
                        unarmedManager.disarmCheck(target);
                    }

                    startGainXp(player, unarmedManager.getProfile(), target, SkillType.UNARMED);
                }
            }
            else if (heldItemType == Material.BONE && target instanceof Tameable && Permissions.beastLore(player)) {
                TamingManager tamingManager = new TamingManager(player);
                tamingManager.beastLore(target);
                event.setCancelled(true);
            }
        }

        Entity damager = event.getDamager();

        switch (damager.getType()) {
        case WOLF:
            Wolf wolf = (Wolf) damager;

            if (wolf.isTamed() && wolf.getOwner() instanceof Player) {
                Player master = (Player) wolf.getOwner();

                if (Misc.isNPCPlayer(master)) {
                    return;
                }

                if (targetIsPlayer || targetIsTamedPet) {
                    if (!Taming.pvpEnabled) {
                        return;
                    }
                }
                else if (!Taming.pveEnabled) {
                    return;
                }

                if (Permissions.taming(master)) {
                    TamingManager tamingManager = new TamingManager(master);
                    int skillLevel = tamingManager.getSkillLevel();

                    if (skillLevel >= Taming.fastFoodServiceUnlockLevel && Permissions.fastFoodService(master)) {
                        tamingManager.fastFoodService(wolf, event.getDamage());
                    }

                    if (skillLevel >= Taming.sharpenedClawsUnlockLevel && Permissions.sharpenedClaws(master)) {
                        tamingManager.sharpenedClaws(event);
                    }

                    if (Permissions.gore(master)) {
                        tamingManager.gore(event);
                    }

                    if (target != master) {
                        startGainXp(master, tamingManager.getProfile(), target, SkillType.TAMING);
                    }
                }
            }

            break;

        case ARROW:
            LivingEntity shooter = ((Arrow) damager).getShooter();

            /* Break instead of return due to Dodge/Counter/Deflect abilities */
            if (shooter == null || !(shooter instanceof Player)) {
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

            if (Misc.isNPCPlayer(player)) {
                return;
            }

            ItemStack heldItem = player.getItemInHand();

            if (damager instanceof Player) {
                if (Swords.pvpEnabled && ItemChecks.isSword(heldItem) && Permissions.counterAttack(player)) {
                    SwordsManager swordsManager = new SwordsManager(player);
                    swordsManager.counterAttackChecks((LivingEntity) damager, event.getDamage());
                }

                if (Acrobatics.pvpEnabled && Permissions.dodge(player)) {
                    AcrobaticsManager acrobaticsManager = new AcrobaticsManager(player);
                    acrobaticsManager.dodgeCheck(event);
                }

                if (Unarmed.pvpEnabled && heldItem.getType() == Material.AIR && Permissions.deflect(player)) {
                    UnarmedManager unarmedManager = new UnarmedManager(player);
                    unarmedManager.deflectCheck(event);
                }
            }
            else {
                if (Swords.pveEnabled && damager instanceof LivingEntity && ItemChecks.isSword(heldItem) && Permissions.counterAttack(player)) {
                    SwordsManager swordsManager = new SwordsManager(player);
                    swordsManager.counterAttackChecks((LivingEntity) damager, event.getDamage());
                }

                if (Acrobatics.pveEnabled && !(damager instanceof LightningStrike && Acrobatics.dodgeLightningDisabled) && Permissions.dodge(player)) {
                    AcrobaticsManager acrobaticsManager = new AcrobaticsManager(player);
                    acrobaticsManager.dodgeCheck(event);
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
    private static void archeryCheck(Player shooter, LivingEntity target, EntityDamageByEntityEvent event) {
        if (Misc.isNPCPlayer(shooter)) {
            return;
        }

        if (Permissions.archery(shooter)) {
            ArcheryManager archeryManager = new ArcheryManager(shooter);
            archeryManager.skillShot(event);

            if (target instanceof Player && Permissions.daze(shooter)) {
                archeryManager.dazeCheck((Player) target, event);
            }

            if (!(shooter.getItemInHand().containsEnchantment(Enchantment.ARROW_INFINITE)) && Permissions.trackArrows(shooter)) {
                archeryManager.trackArrows(target);
            }

            if (target != shooter) {
                startGainXp(shooter, archeryManager.getProfile(), target, SkillType.ARCHERY);
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
