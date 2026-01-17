package com.gmail.nossr50.util.skills;

import static com.gmail.nossr50.datatypes.experience.XPGainReason.PVP;
import static com.gmail.nossr50.util.AttributeMapper.MAPPED_MOVEMENT_SPEED;
import static com.gmail.nossr50.util.ItemUtils.isSpear;
import static com.gmail.nossr50.util.MobMetadataUtils.hasMobFlag;
import static com.gmail.nossr50.util.Permissions.canUseSubSkill;
import static com.gmail.nossr50.util.skills.ProjectileUtils.isCrossbowProjectile;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.meta.OldName;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.metadata.MobMetaFlagType;
import com.gmail.nossr50.runnables.skills.AwardCombatXpTask;
import com.gmail.nossr50.skills.acrobatics.AcrobaticsManager;
import com.gmail.nossr50.skills.archery.ArcheryManager;
import com.gmail.nossr50.skills.axes.AxesManager;
import com.gmail.nossr50.skills.maces.MacesManager;
import com.gmail.nossr50.skills.spears.SpearsManager;
import com.gmail.nossr50.skills.swords.SwordsManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.tridents.TridentsManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.MobHealthbarUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Animals;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Trident;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CombatUtils {

    private static final ThreadLocal<Boolean> IN_MCMMO_DAMAGE
            = ThreadLocal.withInitial(() -> false);


    public static void safeDealDamage(@NotNull LivingEntity target, double amount) {
        safeDealDamage(target, amount, null);
    }

    /**
     * Safely deals damage to a target entity, preventing recursive mcMMO damage calls.
     *
     * @param target The {@link LivingEntity} to damage.
     * @param amount The amount of damage to deal.
     * @param attacker The {@link Entity} responsible for the damage, or null if none.
     */
    public static void safeDealDamage(@NotNull LivingEntity target, double amount,
            @Nullable Entity attacker) {
        boolean prev = IN_MCMMO_DAMAGE.get();

        if (prev || target.isDead()) {
            return;
        }

        try {
            IN_MCMMO_DAMAGE.set(true);
            if (!hasIgnoreDamageMetadata(target)) {
                applyIgnoreDamageMetadata(target);
            }

            if (attacker != null) {
                target.damage(amount, attacker);
            } else {
                target.damage(amount);
            }
        } finally {
            IN_MCMMO_DAMAGE.set(false);
            if (hasIgnoreDamageMetadata(target)) {
                removeIgnoreDamageMetadata(target);
            }
        }
    }

    private CombatUtils() {
    }

    @Deprecated(forRemoval = true, since = "2.2.039")
    public static boolean isDamageLikelyFromNormalCombat(@NotNull DamageCause damageCause) {
        return switch (damageCause) {
            case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK, PROJECTILE -> true;
            default -> false;
        };
    }

    @Deprecated(forRemoval = true, since = "2.2.039")
    public static boolean hasWeakenedDamage(@NotNull LivingEntity livingEntity) {
        return livingEntity.hasPotionEffect(PotionEffectType.WEAKNESS);
    }

    private static void processSwordCombat(@NotNull LivingEntity target, @NotNull Player player,
            @NotNull EntityDamageByEntityEvent event) {
        if (event.getCause() == DamageCause.THORNS) {
            return;
        }

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        //Make sure the profiles been loaded
        if (mmoPlayer == null) {
            return;
        }

        // Hack to avoid other combat abilities applying to off-hand spear attacks
        if (isSpear(player.getInventory().getItemInOffHand()) && isNotSwinging(mmoPlayer)) {
            return;
        }

        SwordsManager swordsManager = mmoPlayer.getSwordsManager();
        double boostedDamage = event.getDamage();

        if (swordsManager.canActivateAbility()) {
            mmoPlayer.checkAbilityActivation(PrimarySkillType.SWORDS);
        }

        //Add Stab Damage
        if (swordsManager.canUseStab()) {
            boostedDamage += (swordsManager.getStabDamage() * mmoPlayer.getAttackStrength());
        }

        if (swordsManager.canUseSerratedStrike()) {
            swordsManager.serratedStrikes(target, event.getDamage());
        }

        if (canUseLimitBreak(player, target, SubSkillType.SWORDS_SWORDS_LIMIT_BREAK)) {
            boostedDamage += (getLimitBreakDamage
                    (player, target, SubSkillType.SWORDS_SWORDS_LIMIT_BREAK)
                    * mmoPlayer.getAttackStrength());
        }

        event.setDamage(boostedDamage);

        if (target.getHealth() - event.getFinalDamage() > 0) {
            swordsManager.processRupture(target);
        }

        processCombatXP(mmoPlayer, target, PrimarySkillType.SWORDS);

        printFinalDamageDebug(player, event, mmoPlayer);
    }

    private static void printFinalDamageDebug(@NotNull Player player,
            @NotNull EntityDamageByEntityEvent event,
            @NotNull McMMOPlayer mmoPlayer,
            @Nullable String @Nullable ... extraInfoLines) {
        if (mmoPlayer.isDebugMode()) {
            player.sendMessage(
                    "Final Damage value after mcMMO modifiers: " + event.getFinalDamage());
            player.sendMessage("Your current attack strength: " + player.getAttackCooldown());
            if (extraInfoLines != null) {
                for (String str : extraInfoLines) {
                    if (str != null) {
                        player.sendMessage(str);
                    }
                }
            }
        }
    }

    private static void processTridentCombatMelee(@NotNull LivingEntity target,
            @NotNull Player player, @NotNull EntityDamageByEntityEvent event) {
        if (event.getCause() == DamageCause.THORNS) {
            return;
        }

        double boostedDamage = event.getDamage();

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        //Make sure the profiles been loaded
        if (mmoPlayer == null) {
            return;
        }

        // Hack to avoid other combat abilities applying to off-hand spear attacks
        if (isSpear(player.getInventory().getItemInOffHand()) && isNotSwinging(mmoPlayer)) {
            return;
        }

        final TridentsManager tridentsManager = mmoPlayer.getTridentsManager();

//        if (tridentsManager.canActivateAbility()) {
//            mmoPlayer.checkAbilityActivation(PrimarySkillType.TRIDENTS);
//        }

        if (SkillUtils.canUseSubskill(player, SubSkillType.TRIDENTS_IMPALE)) {
            boostedDamage += (tridentsManager.impaleDamageBonus() * mmoPlayer.getAttackStrength());
        }

        if (canUseLimitBreak(player, target, SubSkillType.TRIDENTS_TRIDENTS_LIMIT_BREAK)) {
            boostedDamage += (getLimitBreakDamage(
                    player, target, SubSkillType.TRIDENTS_TRIDENTS_LIMIT_BREAK)
                    * mmoPlayer.getAttackStrength());
        }

        event.setDamage(boostedDamage);
        processCombatXP(mmoPlayer, target, PrimarySkillType.TRIDENTS);

        printFinalDamageDebug(player, event, mmoPlayer);
    }

    private static void processTridentCombatRanged(@NotNull Trident trident,
            @NotNull LivingEntity target,
            @NotNull Player player,
            @NotNull EntityDamageByEntityEvent event) {
        if (event.getCause() == DamageCause.THORNS) {
            return;
        }

        double boostedDamage = event.getDamage();

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        //Make sure the profiles been loaded
        if (mmoPlayer == null) {
            return;
        }

        final TridentsManager tridentsManager = mmoPlayer.getTridentsManager();

        if (SkillUtils.canUseSubskill(player, SubSkillType.TRIDENTS_IMPALE)) {
            boostedDamage += (tridentsManager.impaleDamageBonus());
        }

        if (canUseLimitBreak(player, target, SubSkillType.TRIDENTS_TRIDENTS_LIMIT_BREAK)) {
            boostedDamage += (getLimitBreakDamage(player, target,
                    SubSkillType.TRIDENTS_TRIDENTS_LIMIT_BREAK));
        }

        event.setDamage(boostedDamage);
        processCombatXP(mmoPlayer, target, PrimarySkillType.TRIDENTS);

        printFinalDamageDebug(player, event, mmoPlayer);
    }

    private static void processCrossbowsCombat(@NotNull LivingEntity target, @NotNull Player player,
            @NotNull EntityDamageByEntityEvent event, @NotNull AbstractArrow arrow) {
        double initialDamage = event.getDamage();

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        //Make sure the profiles been loaded
        if (mmoPlayer == null) {
            delayArrowMetaCleanup(arrow);
            return;
        }

        double boostedDamage = event.getDamage();

        if (SkillUtils.canUseSubskill(player, SubSkillType.CROSSBOWS_POWERED_SHOT)) {
            //Not Additive
            boostedDamage = mmoPlayer.getCrossbowsManager().poweredShot(initialDamage);
        }

        if (canUseLimitBreak(player, target, SubSkillType.CROSSBOWS_CROSSBOWS_LIMIT_BREAK)) {
            boostedDamage += getLimitBreakDamage(player, target,
                    SubSkillType.CROSSBOWS_CROSSBOWS_LIMIT_BREAK);
        }

        double distanceMultiplier = ArcheryManager.distanceXpBonusMultiplier(target, arrow);
        double forceMultiplier = 1.0;

        event.setDamage(boostedDamage);
        processCombatXP(mmoPlayer, target, PrimarySkillType.CROSSBOWS,
                forceMultiplier * distanceMultiplier);

        printFinalDamageDebug(player, event, mmoPlayer,
                "Distance Multiplier: " + distanceMultiplier,
                "Force Multiplier: " + forceMultiplier,
                "Initial Damage: " + initialDamage,
                "Final Damage: " + boostedDamage);

        //Clean data
        delayArrowMetaCleanup(arrow);
    }

    private static void processMacesCombat(@NotNull LivingEntity target,
            @NotNull Player player,
            @NotNull EntityDamageByEntityEvent event) {
        if (event.getCause() == DamageCause.THORNS) {
            return;
        }

        double boostedDamage = event.getDamage();

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        //Make sure the profiles been loaded
        if (mmoPlayer == null) {
            return;
        }

        // Hack to avoid other combat abilities applying to off-hand spear attacks
        if (isSpear(player.getInventory().getItemInOffHand()) && isNotSwinging(mmoPlayer)) {
            return;
        }

        final MacesManager macesManager = mmoPlayer.getMacesManager();

        // Apply Limit Break DMG
        if (canUseLimitBreak(player, target, SubSkillType.MACES_MACES_LIMIT_BREAK)) {
            boostedDamage += (getLimitBreakDamage(
                    player, target, SubSkillType.MACES_MACES_LIMIT_BREAK)
                    * mmoPlayer.getAttackStrength());
        }

        // Apply Crush DMG
        boostedDamage += (macesManager.getCrushDamage() * mmoPlayer.getAttackStrength());

        event.setDamage(boostedDamage);

        // Apply Cripple
        if (target.getHealth() - event.getFinalDamage() > 0) {
            macesManager.processCripple(target);
        }

        processCombatXP(mmoPlayer, target, PrimarySkillType.MACES);
        printFinalDamageDebug(player, event, mmoPlayer);
    }

    private static void processSpearsCombat(@NotNull LivingEntity target,
            @NotNull Player player,
            @NotNull EntityDamageByEntityEvent event) {
        if (event.getCause() == DamageCause.THORNS) {
            return;
        }

        double boostedDamage = event.getDamage();

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        //Make sure the profiles been loaded
        if (mmoPlayer == null) {
            return;
        }

        final SpearsManager spearsManager = mmoPlayer.getSpearsManager();

        if (canUseSubSkill(player, SubSkillType.SPEARS_SPEAR_MASTERY)) {
            boostedDamage += spearsManager.getSpearMasteryBonusDamage()
                    * mmoPlayer.getAttackStrength();
        }

        // Apply Limit Break DMG
        if (canUseLimitBreak(player, target, SubSkillType.SPEARS_SPEARS_LIMIT_BREAK)) {
            boostedDamage += (getLimitBreakDamage(
                    player, target, SubSkillType.SPEARS_SPEARS_LIMIT_BREAK)
                    * mmoPlayer.getAttackStrength());
        }


        event.setDamage(boostedDamage);

        // Apply any non-damage effects here
        spearsManager.potentiallyApplyMomentum();

        processCombatXP(mmoPlayer, target, PrimarySkillType.SPEARS);
        printFinalDamageDebug(player, event, mmoPlayer);
    }

    private static void processAxeCombat(@NotNull LivingEntity target, @NotNull Player player,
            @NotNull EntityDamageByEntityEvent event) {
        if (event.getCause() == DamageCause.THORNS) {
            return;
        }

        double boostedDamage = event.getDamage();

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        //Make sure the profiles been loaded
        if (mmoPlayer == null) {
            return;
        }

        // Hack to avoid other combat abilities applying to off-hand spear attacks
        if (isSpear(player.getInventory().getItemInOffHand()) && isNotSwinging(mmoPlayer)) {
            return;
        }

        final AxesManager axesManager = mmoPlayer.getAxesManager();

        if (axesManager.canActivateAbility()) {
            mmoPlayer.checkAbilityActivation(PrimarySkillType.AXES);
        }

        if (axesManager.canUseAxeMastery()) {
            boostedDamage += axesManager.axeMastery() * mmoPlayer.getAttackStrength();
        }

        if (axesManager.canImpact(target)) {
            axesManager.impactCheck(target);
        } else if (axesManager.canGreaterImpact(target)) {
            boostedDamage += axesManager.greaterImpact(target) * mmoPlayer.getAttackStrength();
        }

        if (axesManager.canUseSkullSplitter(target)) {
            axesManager.skullSplitterCheck(target, event.getDamage());
        }

        if (axesManager.canCriticalHit(target)) {
            boostedDamage += (axesManager.criticalHit(target, boostedDamage)
                    * mmoPlayer.getAttackStrength());
        }

        if (canUseLimitBreak(player, target, SubSkillType.AXES_AXES_LIMIT_BREAK)) {
            boostedDamage += (
                    getLimitBreakDamage(player, target, SubSkillType.AXES_AXES_LIMIT_BREAK)
                            * mmoPlayer.getAttackStrength());
        }

        event.setDamage(boostedDamage);
        processCombatXP(mmoPlayer, target, PrimarySkillType.AXES);

        printFinalDamageDebug(player, event, mmoPlayer);
    }

    private static void processUnarmedCombat(@NotNull LivingEntity target, @NotNull Player player,
            @NotNull EntityDamageByEntityEvent event) {
        if (event.getCause() == DamageCause.THORNS) {
            return;
        }

        double boostedDamage = event.getDamage();

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        //Make sure the profiles been loaded
        if (mmoPlayer == null) {
            return;
        }

        // Hack to avoid other combat abilities applying to off-hand spear attacks
        if (isSpear(player.getInventory().getItemInOffHand()) && isNotSwinging(mmoPlayer)) {
            return;
        }

        final UnarmedManager unarmedManager = mmoPlayer.getUnarmedManager();

        if (unarmedManager.canActivateAbility()) {
            mmoPlayer.checkAbilityActivation(PrimarySkillType.UNARMED);
        }

        if (unarmedManager.canUseSteelArm()) {
            boostedDamage += (unarmedManager.calculateSteelArmStyleDamage()
                    * mmoPlayer.getAttackStrength());
        }

        if (unarmedManager.canUseBerserk()) {
            boostedDamage += (unarmedManager.berserkDamage(boostedDamage)
                    * mmoPlayer.getAttackStrength());
        }

        if (unarmedManager.canDisarm(target)) {
            unarmedManager.disarmCheck((Player) target);
        }

        if (canUseLimitBreak(player, target, SubSkillType.UNARMED_UNARMED_LIMIT_BREAK)) {
            boostedDamage += (getLimitBreakDamage(
                    player, target, SubSkillType.UNARMED_UNARMED_LIMIT_BREAK)
                    * mmoPlayer.getAttackStrength());
        }

        event.setDamage(boostedDamage);
        processCombatXP(mmoPlayer, target, PrimarySkillType.UNARMED);

        printFinalDamageDebug(player, event, mmoPlayer);
    }

    private static void processTamingCombat(@NotNull LivingEntity target, @Nullable Player master,
            @NotNull Wolf wolf, @NotNull EntityDamageByEntityEvent event) {
        double initialDamage = event.getDamage();
        double boostedDamage = initialDamage;

        if (master != null && master.isOnline() && master.isValid()) {
            final McMMOPlayer mmoPlayer = UserManager.getPlayer(master);

            //Make sure the profiles been loaded
            if (mmoPlayer == null) {
                return;
            }

            final TamingManager tamingManager = mmoPlayer.getTamingManager();

            if (tamingManager.canUseFastFoodService()) {
                tamingManager.fastFoodService(wolf, event.getDamage());
            }

            tamingManager.pummel(target, wolf);

            if (tamingManager.canUseSharpenedClaws()) {
                boostedDamage += tamingManager.sharpenedClaws();
            }

            if (tamingManager.canUseGore()) {
                boostedDamage += tamingManager.gore(target, initialDamage);
            }

            event.setDamage(boostedDamage);
            processCombatXP(mmoPlayer, target, PrimarySkillType.TAMING, 3);
        }

    }

    private static void processArcheryCombat(@NotNull LivingEntity target, @NotNull Player player,
            @NotNull EntityDamageByEntityEvent event, @NotNull AbstractArrow arrow) {
        double initialDamage = event.getDamage();

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        //Make sure the profiles been loaded
        if (mmoPlayer == null) {
            delayArrowMetaCleanup(arrow);
            return;
        }

        final ArcheryManager archeryManager = mmoPlayer.getArcheryManager();

        double boostedDamage = event.getDamage();

        if (archeryManager.canSkillShot()) {
            //Not Additive
            boostedDamage = archeryManager.skillShot(initialDamage);
        }

        if (archeryManager.canDaze(target)) {
            boostedDamage += archeryManager.daze(
                    (Player) target); //the cast is checked by the if condition
        }

        if (!arrow.hasMetadata(MetadataConstants.METADATA_KEY_INF_ARROW)
                && archeryManager.canRetrieveArrows()) {
            archeryManager.retrieveArrows(target, arrow);
        }

        if (canUseLimitBreak(player, target, SubSkillType.ARCHERY_ARCHERY_LIMIT_BREAK)) {
            boostedDamage += getLimitBreakDamage(player, target,
                    SubSkillType.ARCHERY_ARCHERY_LIMIT_BREAK);
        }

        double distanceMultiplier = ArcheryManager.distanceXpBonusMultiplier(target, arrow);
        double forceMultiplier = 1.0; //Hacky Fix - some plugins spawn arrows and assign them to players after the ProjectileLaunchEvent fires

        if (arrow.hasMetadata(MetadataConstants.METADATA_KEY_BOW_FORCE)) {
            forceMultiplier = arrow.getMetadata(MetadataConstants.METADATA_KEY_BOW_FORCE).get(0)
                    .asDouble();
        }

        event.setDamage(boostedDamage);
        processCombatXP(mmoPlayer, target, PrimarySkillType.ARCHERY,
                forceMultiplier * distanceMultiplier);

        printFinalDamageDebug(player, event, mmoPlayer,
                "Distance Multiplier: " + distanceMultiplier,
                "Force Multiplier: " + forceMultiplier,
                "Initial Damage: " + initialDamage,
                "Final Damage: " + boostedDamage);
        //Clean data
        delayArrowMetaCleanup(arrow);
    }

    /**
     * Apply combat modifiers and process and XP gain.
     *
     * @param event The event to run the combat checks on.
     */
    public static void processCombatAttack(@NotNull EntityDamageByEntityEvent event,
            @NotNull Entity painSourceRoot,
            @NotNull LivingEntity target) {
        Entity painSource = event.getDamager();
        EntityType entityType = painSource.getType();

        if (target instanceof ArmorStand) {
            return;
        }

        if (target instanceof Player player) {
            if (ExperienceConfig.getInstance().isNPCInteractionPrevented()) {
                if (Misc.isNPCEntityExcludingVillagers(target)) {
                    return;
                }
            }

            if (!UserManager.hasPlayerDataKey(player)) {
                return;
            }

            final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
            final AcrobaticsManager acrobaticsManager = mmoPlayer.getAcrobaticsManager();

            if (acrobaticsManager.canDodge(target)) {
                event.setDamage(acrobaticsManager.dodgeCheck(painSourceRoot, event.getDamage()));
            }

            if (ItemUtils.isSword(player.getInventory().getItemInMainHand())) {
                if (!mcMMO.p.getSkillTools()
                        .canCombatSkillsTrigger(PrimarySkillType.SWORDS, target)) {
                    return;
                }

                final SwordsManager swordsManager = mmoPlayer.getSwordsManager();

                if (swordsManager.canUseCounterAttack(painSource)) {
                    swordsManager.counterAttackChecks((LivingEntity) painSource, event.getDamage());
                }
            }
        }

        if (painSourceRoot instanceof Player player && entityType == EntityType.PLAYER) {
            if (!UserManager.hasPlayerDataKey(player)) {
                return;
            }

            ItemStack heldItem = player.getInventory().getItemInMainHand();

            if (target instanceof Tameable) {
                if (heldItem.getType() == Material.BONE) {
                    TamingManager tamingManager = UserManager.getPlayer(player).getTamingManager();

                    if (tamingManager.canUseBeastLore()) {
                        tamingManager.beastLore(target);
                        event.setCancelled(true);
                        return;
                    }
                }

                if (isFriendlyPet(player, (Tameable) target)) {
                    return;
                }
            }

            if (ItemUtils.isSword(heldItem)) {
                if (!mcMMO.p.getSkillTools()
                        .canCombatSkillsTrigger(PrimarySkillType.SWORDS, target)) {
                    return;
                }

                if (mcMMO.p.getSkillTools()
                        .doesPlayerHaveSkillPermission(player, PrimarySkillType.SWORDS)) {
                    processSwordCombat(target, player, event);
                }
            } else if (ItemUtils.isAxe(heldItem)) {
                if (!mcMMO.p.getSkillTools()
                        .canCombatSkillsTrigger(PrimarySkillType.AXES, target)) {
                    return;
                }

                if (mcMMO.p.getSkillTools()
                        .doesPlayerHaveSkillPermission(player, PrimarySkillType.AXES)) {
                    processAxeCombat(target, player, event);
                }
            } else if (ItemUtils.isUnarmed(heldItem)) {
                if (!mcMMO.p.getSkillTools()
                        .canCombatSkillsTrigger(PrimarySkillType.UNARMED, target)) {
                    return;
                }

                if (mcMMO.p.getSkillTools()
                        .doesPlayerHaveSkillPermission(player, PrimarySkillType.UNARMED)) {
                    processUnarmedCombat(target, player, event);
                }
            } else if (ItemUtils.isTrident(heldItem)) {
                if (!mcMMO.p.getSkillTools()
                        .canCombatSkillsTrigger(PrimarySkillType.TRIDENTS, target)) {
                    return;
                }

                if (mcMMO.p.getSkillTools()
                        .doesPlayerHaveSkillPermission(player, PrimarySkillType.TRIDENTS)) {
                    processTridentCombatMelee(target, player, event);
                }
            } else if (ItemUtils.isMace(heldItem)) {
                if (!mcMMO.p.getSkillTools()
                        .canCombatSkillsTrigger(PrimarySkillType.MACES, target)) {
                    return;
                }

                if (mcMMO.p.getSkillTools()
                        .doesPlayerHaveSkillPermission(player, PrimarySkillType.MACES)) {
                    processMacesCombat(target, player, event);
                }
            } else if (isSpear(heldItem)) {
                if (!mcMMO.p.getSkillTools()
                        .canCombatSkillsTrigger(PrimarySkillType.SPEARS, target)) {
                    return;
                }
                if (mcMMO.p.getSkillTools()
                        .doesPlayerHaveSkillPermission(player, PrimarySkillType.SPEARS)) {
                    processSpearsCombat(target, player, event);
                }
            }
        } else if (entityType == EntityType.WOLF) {
            Wolf wolf = (Wolf) painSource;
            AnimalTamer tamer = wolf.getOwner();

            if (tamer instanceof Player master && mcMMO.p.getSkillTools()
                    .canCombatSkillsTrigger(PrimarySkillType.TAMING, target)) {

                if (!Misc.isNPCEntityExcludingVillagers(master) && mcMMO.p.getSkillTools()
                        .doesPlayerHaveSkillPermission(master, PrimarySkillType.TAMING)) {
                    processTamingCombat(target, master, wolf, event);
                }
            }
        } else if (painSource instanceof Trident trident) {
            ProjectileSource projectileSource = trident.getShooter();

            if (projectileSource instanceof Player player) {
                if (!Misc.isNPCEntityExcludingVillagers(player)) {
                    if (mcMMO.p.getSkillTools()
                            .canCombatSkillsTrigger(PrimarySkillType.TRIDENTS, target)) {
                        processTridentCombatRanged(trident, target, player, event);
                    }
                }
            }
        } else if (painSource instanceof AbstractArrow arrow) {
            ProjectileSource projectileSource = arrow.getShooter();
            boolean isCrossbow = isCrossbowProjectile(arrow);
            if (projectileSource instanceof Player player) {
                if (!Misc.isNPCEntityExcludingVillagers(player)) {
                    if (!isCrossbow && mcMMO.p.getSkillTools()
                            .canCombatSkillsTrigger(PrimarySkillType.ARCHERY, target)) {
                        processArcheryCombat(target, player, event, arrow);
                    } else if (isCrossbow && mcMMO.p.getSkillTools()
                            .canCombatSkillsTrigger(PrimarySkillType.CROSSBOWS, target)) {
                        processCrossbowsCombat(target, player, event, arrow);
                    }
                } else {
                    //Cleanup Arrow
                    delayArrowMetaCleanup(arrow);
                }

                if (target.getType() != EntityType.CREEPER
                        && !Misc.isNPCEntityExcludingVillagers(player)
                        && mcMMO.p.getSkillTools()
                        .doesPlayerHaveSkillPermission(player, PrimarySkillType.TAMING)) {
                    final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

                    if (mmoPlayer == null) {
                        return;
                    }

                    TamingManager tamingManager = mmoPlayer.getTamingManager();
                    tamingManager.attackTarget(target);
                }
            }
        }
    }

    /**
     * This cleans up names from displaying in chat as hearts
     *
     * @param entity target entity
     */
    public static void fixNames(@NotNull LivingEntity entity) {
        List<MetadataValue> metadataValue = entity.getMetadata(
                MetadataConstants.METADATA_KEY_OLD_NAME_KEY);

        if (metadataValue.size() <= 0) {
            return;
        }

        OldName oldName = (OldName) metadataValue.get(0);
        entity.setCustomName(oldName.asString());
        entity.setCustomNameVisible(false);

        entity.removeMetadata(MetadataConstants.METADATA_KEY_OLD_NAME_KEY, mcMMO.p);
    }

    /**
     * Calculate and return the RAW damage bonus from Limit Break before reductions
     *
     * @param attacker attacking player
     * @param defender defending living entity
     * @param subSkillType the specific limit break skill for calculations
     * @return the RAW damage bonus from Limit Break which is applied before reductions
     */
    public static int getLimitBreakDamage(@NotNull Player attacker, @NotNull LivingEntity defender,
            @NotNull SubSkillType subSkillType) {
        if (defender instanceof Player playerDefender) {
            return getLimitBreakDamageAgainstQuality(attacker, subSkillType,
                    getArmorQualityLevel(playerDefender));
        } else {
            return getLimitBreakDamageAgainstQuality(attacker, subSkillType, 1000);
        }
    }

    /**
     * Calculate the RAW daamge value of limit break based on the armor quality of the target PVE
     * mobs are passed in with a value of 1000 for armor quality, hacky... I'll change it later
     *
     * @param attacker Living entity attacker
     * @param subSkillType Target limit break
     * @param armorQualityLevel Armor quality level
     * @return the RAW damage boost after its been mutated by armor quality
     */
    public static int getLimitBreakDamageAgainstQuality(@NotNull Player attacker,
            @NotNull SubSkillType subSkillType,
            int armorQualityLevel) {
        float rawDamageBoost = RankUtils.getRank(attacker, subSkillType);

        if (armorQualityLevel <= 4) {
            rawDamageBoost *= .25F; //75% Nerf
        } else if (armorQualityLevel <= 8) {
            rawDamageBoost *= .50F; //50% Nerf
        } else if (armorQualityLevel <= 12) {
            rawDamageBoost *= .75F; //25% Nerf
        }

        return (int) rawDamageBoost;
    }

    /**
     * Get the quality level of the armor of a player used for Limit Break calculations
     *
     * @param defender target defending player
     * @return the armor quality of the defending player
     */
    public static int getArmorQualityLevel(@NotNull Player defender) {
        int armorQualityLevel = 0;

        for (ItemStack itemStack : defender.getInventory().getArmorContents()) {
            if (itemStack != null) {
                armorQualityLevel += getArmorQuality(itemStack);
            }
        }

        return armorQualityLevel;
    }

    /**
     * Get the armor quality for a specific item used for Limit Break calculations
     *
     * @param itemStack target item stack
     * @return the armor quality of a specific Item Stack
     */
    private static int getArmorQuality(@NotNull ItemStack itemStack) {
        return mcMMO.getMaterialMapStore().getTier(itemStack.getType().getKey().getKey());
    }

    /**
     * Checks if player has access to their weapons limit break
     *
     * @param player target entity
     * @return true if the player has access to the limit break
     */
    public static boolean canUseLimitBreak(@NotNull Player player, LivingEntity target,
            @NotNull SubSkillType subSkillType) {
        if (target instanceof Player || mcMMO.p.getAdvancedConfig().canApplyLimitBreakPVE()) {
            return RankUtils.hasUnlockedSubskill(player, subSkillType)
                    && Permissions.isSubSkillEnabled(player, subSkillType);
        } else {
            return false;
        }
    }

    /**
     * Attempt to damage target for value dmg with reason CUSTOM
     *
     * @param target LivingEntity which to attempt to damage
     * @param damage Amount of damage to attempt to do
     * @deprecated use {@link #safeDealDamage(LivingEntity, double, Entity)} instead
     */
    @Deprecated(since = "2.2.039")
    public static void dealDamage(@NotNull LivingEntity target, double damage) {
        safeDealDamage(target, damage, null);
    }

    /**
     * Attempt to damage target for value dmg with reason ENTITY_ATTACK with damager attacker
     *
     * @param target the entity to attempt to damage
     * @param damage Amount of damage to attempt to do
     * @param attacker the responsible entity (nullable)
     * @deprecated use {@link #safeDealDamage(LivingEntity, double, Entity)} instead
     */
    @Deprecated(since = "2.2.039")
    public static void dealDamage(@NotNull LivingEntity target, double damage,
            @Nullable Entity attacker) {
        safeDealDamage(target, damage, attacker);
    }

    public static void removeIgnoreDamageMetadata(@NotNull LivingEntity target) {
        target.removeMetadata(MetadataConstants.METADATA_KEY_CUSTOM_DAMAGE, mcMMO.p);
    }

    public static void applyIgnoreDamageMetadata(@NotNull LivingEntity target) {
        target.setMetadata(MetadataConstants.METADATA_KEY_CUSTOM_DAMAGE,
                MetadataConstants.MCMMO_METADATA_VALUE);
    }

    public static boolean hasIgnoreDamageMetadata(@NotNull LivingEntity target) {
        return target.hasMetadata(MetadataConstants.METADATA_KEY_CUSTOM_DAMAGE);
    }

    /**
     * Apply Area-of-Effect ability actions.
     *
     * @param attacker The attacking player
     * @param target The defending entity
     * @param damage The initial damage amount
     * @param type The type of skill being used
     */
    public static void applyAbilityAoE(@NotNull Player attacker, @NotNull LivingEntity target,
            double damage, @NotNull PrimarySkillType type) {
        int numberOfTargets = getTier(attacker.getInventory()
                .getItemInMainHand()); // The higher the weapon tier, the more targets you hit
        double damageAmount = Math.max(damage, 1);

        for (Entity entity : target.getNearbyEntities(2.5, 2.5, 2.5)) {
            if (numberOfTargets <= 0) {
                break;
            }

            if ((ExperienceConfig.getInstance().isNPCInteractionPrevented()
                    && Misc.isNPCEntityExcludingVillagers(entity))
                    || !(entity instanceof LivingEntity livingEntity) || !shouldBeAffected(attacker,
                    entity)) {
                continue;
            }

            switch (type) {
                case SWORDS:
                    if (entity instanceof Player) {
                        NotificationManager.sendPlayerInformation((Player) entity,
                                NotificationType.SUBSKILL_MESSAGE,
                                "Swords.Combat.SS.Struck");
                    }

                    final McMMOPlayer mmoAttacker = UserManager.getPlayer(attacker);

                    if (mmoAttacker != null) {
                        mmoAttacker.getSwordsManager().processRupture(livingEntity);
                    }

                    break;

                case AXES:
                    if (entity instanceof Player) {
                        NotificationManager.sendPlayerInformation((Player) entity,
                                NotificationType.SUBSKILL_MESSAGE,
                                "Axes.Combat.SS.Struck");
                    }
                    break;

                default:
                    break;
            }

            safeDealDamage(livingEntity, damageAmount, attacker);
            numberOfTargets--;
        }
    }

    /**
     * Start the task that gives combat XP.
     *
     * @param mmoPlayer The attacking player
     * @param target The defending entity
     * @param primarySkillType The skill being used
     */
    public static void processCombatXP(@NotNull McMMOPlayer mmoPlayer, @NotNull LivingEntity target,
            @NotNull PrimarySkillType primarySkillType) {
        processCombatXP(mmoPlayer, target, primarySkillType, 1.0);
    }

    /**
     * Start the task that gives combat XP.
     *
     * @param mmoPlayer The attacking player
     * @param target The defending entity
     * @param primarySkillType The skill being used
     * @param multiplier final XP result will be multiplied by this
     */
    public static void processCombatXP(@NotNull McMMOPlayer mmoPlayer,
            @NotNull LivingEntity target,
            @NotNull PrimarySkillType primarySkillType,
            double multiplier) {
        double baseXP = 0;
        XPGainReason xpGainReason;

        if (target instanceof Player defender) {
            if (defender.equals(mmoPlayer.getPlayer())
                    || !ExperienceConfig.getInstance().getExperienceGainsPlayerVersusPlayerEnabled()
                    ||
                    (mcMMO.p.getPartyConfig().isPartyEnabled()
                            && mcMMO.p.getPartyManager()
                            .inSameParty(mmoPlayer.getPlayer(), defender))) {
                return;
            }

            xpGainReason = PVP;

            if (defender.isOnline()
                    && SkillUtils.cooldownExpired(mmoPlayer.getRespawnATS(),
                    Misc.PLAYER_RESPAWN_COOLDOWN_SECONDS)) {
                baseXP = 20 * ExperienceConfig.getInstance().getPlayerVersusPlayerXP();
            }
        } else {
            if (target instanceof Animals) {
                EntityType type = target.getType();
                baseXP = ExperienceConfig.getInstance().getAnimalsXP(type);
            } else if (target instanceof Monster) {
                EntityType type = target.getType();
                baseXP = ExperienceConfig.getInstance().getCombatXP(type);
            } else {
                EntityType type = target.getType();

                if (ExperienceConfig.getInstance().hasCombatXP(type)) {
                    if (type == EntityType.IRON_GOLEM && target instanceof IronGolem ironGolem) {
                        if (!ironGolem.isPlayerCreated()) {
                            baseXP = ExperienceConfig.getInstance().getCombatXP(type);
                        }
                    } else {
                        baseXP = ExperienceConfig.getInstance().getCombatXP(type);
                    }
                } else {
                    baseXP = 1.0;
                }
            }

            if (hasMobFlag(MobMetaFlagType.COTW_SUMMONED_MOB, target)) {
                baseXP = 0;
            } else if (hasMobFlag(MobMetaFlagType.MOB_SPAWNER_MOB, target) || target.hasMetadata(
                    "ES")) {
                baseXP *= ExperienceConfig.getInstance().getSpawnedMobXpMultiplier();
            } else if (hasMobFlag(MobMetaFlagType.NETHER_PORTAL_MOB, target)) {
                baseXP *= ExperienceConfig.getInstance().getNetherPortalXpMultiplier();
            } else if (hasMobFlag(MobMetaFlagType.EGG_MOB, target)) {
                baseXP *= ExperienceConfig.getInstance().getEggXpMultiplier();
            } else if (hasMobFlag(MobMetaFlagType.PLAYER_BRED_MOB, target)) {
                baseXP *= ExperienceConfig.getInstance().getBredMobXpMultiplier();
            } else if (hasMobFlag(MobMetaFlagType.PLAYER_TAMED_MOB, target)) {
                baseXP *= ExperienceConfig.getInstance().getTamedMobXpMultiplier();
            }

            baseXP *= 10;
            xpGainReason = XPGainReason.PVE;
        }

        baseXP *= multiplier;

        if (baseXP > 0) {
            mcMMO.p.getFoliaLib().getScheduler().runAtEntity(target,
                    new AwardCombatXpTask(mmoPlayer, primarySkillType, baseXP, target,
                            xpGainReason));
        }
    }

    /**
     * Check to see if the given LivingEntity should be affected by a combat ability.
     *
     * @param player The attacking Player
     * @param entity The defending Entity
     * @return true if the Entity should be damaged, false otherwise.
     */
    private static boolean shouldBeAffected(@NotNull Player player, @NotNull Entity entity) {
        if (entity instanceof Player defender) {
            //TODO: NPC Interaction?
            if (UserManager.getPlayer(defender) == null) {
                return true;
            }

            if (!defender.getWorld().getPVP() || defender == player || UserManager.getPlayer(
                    defender).getGodMode()) {
                return false;
            }

            if (mcMMO.p.getPartyConfig().isPartyEnabled()) {
                if ((mcMMO.p.getPartyManager().inSameParty(player, defender)
                        || mcMMO.p.getPartyManager().areAllies(player, defender))
                        && !(Permissions.friendlyFire(player) && Permissions.friendlyFire(
                        defender))) {
                    return false;
                }
            }

            // Vanished players should not be able to get hit by AoE effects
            if (!player.canSee(defender)) {
                return false;
            }

            // Spectators should not be affected 
            return defender.getGameMode() != GameMode.SPECTATOR;
        } else if (entity instanceof Tameable tameableEntity) {
            if (isFriendlyPet(player, tameableEntity)) {
                // isFriendlyPet ensures that the Tameable is: Tamed, owned by a player, and the owner is in the same party
                // So we can make some assumptions here, about our casting and our check
                Player owner = (Player) tameableEntity.getOwner();
                return Permissions.friendlyFire(player) && Permissions.friendlyFire(owner);
            }
        }

        return true;
    }

    /**
     * Checks to see if an entity is currently invincible.
     *
     * @param entity The {@link LivingEntity} to check
     * @param eventDamage The damage from the event the entity is involved in
     * @return true if the entity is invincible, false otherwise
     */
    public static boolean isInvincible(@NotNull LivingEntity entity, double eventDamage) {
        /*
         * So apparently if you do more damage to a LivingEntity than its last damage int you bypass the invincibility.
         * So yeah, this is for that.
         */
        return (entity.getNoDamageTicks() > entity.getMaximumNoDamageTicks() / 2.0F) && (eventDamage
                <= entity.getLastDamage());
    }

    /**
     * Checks to see if an entity is currently friendly toward a given player.
     *
     * @param attacker The player to check.
     * @param pet The entity to check.
     * @return true if the entity is friendly, false otherwise
     */
    public static boolean isFriendlyPet(@NotNull Player attacker, @NotNull Tameable pet) {
        if (pet.isTamed()) {
            AnimalTamer tamer = pet.getOwner();

            if (tamer instanceof Player owner) {

                return (owner == attacker || (mcMMO.p.getPartyConfig().isPartyEnabled()
                        && (mcMMO.p.getPartyManager().inSameParty(attacker, owner)
                        || mcMMO.p.getPartyManager().areAllies(attacker, owner))));
            }
        }

        return false;
    }

    /**
     * Get the upgrade tier of the item in hand.
     *
     * @param inHand The item to check the tier of
     * @return the tier of the item
     */
    private static int getTier(@NotNull ItemStack inHand) {
        int tier = 0;

        if (ItemUtils.isWoodTool(inHand)) {
            tier = 1;
        } else if (ItemUtils.isStoneTool(inHand)) {
            tier = 2;
        } else if (ItemUtils.isIronTool(inHand)) {
            tier = 3;
        } else if (ItemUtils.isGoldTool(inHand)) {
            tier = 1;
        } else if (ItemUtils.isDiamondTool(inHand)) {
            tier = 4;
        } else if (ItemUtils.isNetheriteTool(inHand)) {
            tier = 5;
        }

        return tier;
    }

    public static void handleHealthbars(@NotNull Entity attacker, @NotNull LivingEntity target,
            double damage, @NotNull mcMMO plugin) {
        if (!(attacker instanceof Player player)) {
            return;
        }

        if (Misc.isNPCEntityExcludingVillagers(player) || Misc.isNPCEntityExcludingVillagers(
                target)) {
            return;
        }

        if (!player.hasMetadata(MetadataConstants.METADATA_KEY_PLAYER_DATA)) {
            return;
        }

        MobHealthbarUtils.handleMobHealthbars(target, damage, plugin);
    }

    @Deprecated(forRemoval = true, since = "2.2.039")
    public static void modifyMoveSpeed(@NotNull LivingEntity livingEntity, double multiplier) {
        AttributeInstance attributeInstance = livingEntity.getAttribute(MAPPED_MOVEMENT_SPEED);

        if (attributeInstance != null) {
            double normalSpeed = attributeInstance.getBaseValue();
            attributeInstance.setBaseValue(normalSpeed * multiplier);
        }
    }

    /**
     * Clean up metadata from a projectile after a minute has passed
     *
     * @param arrow the projectile
     */
    public static void delayArrowMetaCleanup(@NotNull AbstractArrow arrow) {
        mcMMO.p.getFoliaLib().getScheduler()
                .runLater(() -> ProjectileUtils.cleanupProjectileMetadata(arrow), 20 * 120);
    }

    public static boolean isNotSwinging(McMMOPlayer mmoPlayer) {
        // If player has swung in the last second, it's extremely unlikely the damage originates
        // from an off-hand spear charge attack
        return mmoPlayer.getLastSwingTimestamp() + 500L < System.currentTimeMillis();
    }
}
