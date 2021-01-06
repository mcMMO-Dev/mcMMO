package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.meta.OldName;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.events.fake.FakeEntityDamageByEntityEvent;
import com.gmail.nossr50.events.fake.FakeEntityDamageEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.skills.AwardCombatXpTask;
import com.gmail.nossr50.skills.acrobatics.AcrobaticsManager;
import com.gmail.nossr50.skills.archery.ArcheryManager;
import com.gmail.nossr50.skills.axes.AxesManager;
import com.gmail.nossr50.skills.crossbows.CrossbowsManager;
import com.gmail.nossr50.skills.swords.SwordsManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.tridents.TridentsManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;
import com.gmail.nossr50.util.*;
import com.gmail.nossr50.util.compat.layers.persistentdata.AbstractPersistentDataLayer;
import com.gmail.nossr50.util.compat.layers.persistentdata.MobMetaFlagType;
import com.gmail.nossr50.util.player.NotificationManager;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CombatUtils {

    private CombatUtils() {}

    private static @NotNull AbstractPersistentDataLayer getPersistentData() {
        return mcMMO.getCompatibilityManager().getPersistentDataLayer();
    }

    //Likely.. because who knows what plugins are throwing around
    public static boolean isDamageLikelyFromNormalCombat(@NotNull DamageCause damageCause) {
        switch (damageCause) {
            case ENTITY_ATTACK:
            case ENTITY_SWEEP_ATTACK:
            case PROJECTILE:
                return true;
            default:
                return false;
        }
    }

    public static boolean hasWeakenedDamage(@NotNull LivingEntity livingEntity) {
        return livingEntity.hasPotionEffect(PotionEffectType.WEAKNESS);
    }

    private static void processSwordCombat(@NotNull LivingEntity target, @NotNull Player player, @NotNull EntityDamageByEntityEvent event) {
        if (event.getCause() == DamageCause.THORNS) {
            return;
        }

        OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(player);

        //Make sure the profiles been loaded
        if(mmoPlayer == null) {
            return;
        }

        SwordsManager swordsManager = ((McMMOPlayer) (mmoPlayer)).getSwordsManager();
        double initialDamage = event.getDamage();
        double finalDamage = initialDamage;

        Map<DamageModifier, Double> modifiers = getModifiers(event);

        if (swordsManager.canActivateAbility()) {
            mmoPlayer.getSuperAbilityManager().checkAbilityActivation(PrimarySkillType.SWORDS);
        }

        if(target.getHealth() - event.getFinalDamage() >= 1)
        {
            if (swordsManager.canUseRupture()) {
                swordsManager.ruptureCheck(target);
            }
        }

        //Add Stab Damage
        if(swordsManager.canUseStab())
        {
            finalDamage+=(swordsManager.getStabDamage());
        }

        if (swordsManager.canUseSerratedStrike()) {
            swordsManager.serratedStrikes(target, initialDamage, modifiers);
        }

        if(canUseLimitBreak(mmoPlayer, target, SubSkillType.SWORDS_SWORDS_LIMIT_BREAK))
        {
            finalDamage+=(getLimitBreakDamage(mmoPlayer, target, SubSkillType.SWORDS_SWORDS_LIMIT_BREAK));
        }

        applyScaledModifiers(initialDamage, finalDamage, event);
        processCombatXP(mmoPlayer, target, PrimarySkillType.SWORDS);

        printFinalDamageDebug(player, event, mmoPlayer);
    }

    public static void processTridentCombat(LivingEntity target, Player player, EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if (entityDamageByEntityEvent.getCause() == DamageCause.THORNS) {
            return;
        }

        OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(player);

        //Make sure the profiles been loaded
        if(mmoPlayer == null) {
            return;
        }

        TridentsManager tridentsManager = ((McMMOPlayer) (mmoPlayer)).getTridentManager();
//        double initialDamage = entityDamageByEntityEvent.getDamage();
//        double finalDamage = initialDamage;

        processCombatXP(mmoPlayer, target, PrimarySkillType.TRIDENTS);
    }


    private static void printFinalDamageDebug(@NotNull Player player, @NotNull EntityDamageByEntityEvent event, @NotNull OnlineMMOPlayer mcMMOPlayer, @Nullable String @Nullable ... extraInfoLines) {
        if(mcMMOPlayer.isDebugMode()) {
            player.sendMessage("Final Damage value after mcMMO modifiers: "+ event.getFinalDamage());
            if(extraInfoLines != null) {
                for(String str : extraInfoLines) {
                    if(str != null)
                        player.sendMessage(str);
                }
            }
        }
    }

    private static void processAxeCombat(@NotNull LivingEntity target, @NotNull Player player, @NotNull EntityDamageByEntityEvent event) {
        if (event.getCause() == DamageCause.THORNS) {
            return;
        }
        
        double initialDamage = event.getDamage();
        double finalDamage = initialDamage;
        Map<DamageModifier, Double> modifiers = getModifiers(event);

        OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(player);

        //Make sure the profiles been loaded
        if(mmoPlayer == null) {
            return;
        }

        AxesManager axesManager = ((McMMOPlayer) (mmoPlayer)).getArcheryManager();

        if (axesManager.canActivateAbility()) {
            mmoPlayer.getSuperAbilityManager().checkAbilityActivation(PrimarySkillType.AXES);
        }

        if (axesManager.canUseAxeMastery()) {
            finalDamage+=axesManager.axeMastery();
        }

        if (axesManager.canImpact(target)) {
            axesManager.impactCheck(target);
        }
        else if (axesManager.canGreaterImpact(target)) {
            finalDamage+=axesManager.greaterImpact(target);
        }

        if (axesManager.canUseSkullSplitter(target)) {
            axesManager.skullSplitterCheck(target, initialDamage, modifiers);
        }

        if (axesManager.canCriticalHit(target)) {
            finalDamage+=(axesManager.criticalHit(target, finalDamage));
        }

        if(canUseLimitBreak(mmoPlayer, target, SubSkillType.AXES_AXES_LIMIT_BREAK))
        {
            finalDamage+=(getLimitBreakDamage(mmoPlayer, target, SubSkillType.AXES_AXES_LIMIT_BREAK));
        }

        applyScaledModifiers(initialDamage, finalDamage, event);
        processCombatXP(mmoPlayer, target, PrimarySkillType.AXES);

        printFinalDamageDebug(player, event, mmoPlayer);
    }

    private static void processUnarmedCombat(@NotNull LivingEntity target, @NotNull Player player, @NotNull EntityDamageByEntityEvent event) {
        if (event.getCause() == DamageCause.THORNS) {
            return;
        }

        double initialDamage = event.getDamage();
        double finalDamage = initialDamage;

        OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(player);

        //Make sure the profiles been loaded
        if(mmoPlayer == null) {
            return;
        }

        UnarmedManager unarmedManager = ((McMMOPlayer) (mmoPlayer)).getUnarmedManager();

        if (unarmedManager.canActivateAbility()) {
            mmoPlayer.getSuperAbilityManager().checkAbilityActivation(PrimarySkillType.UNARMED);
        }

        if (unarmedManager.canUseSteelArm()) {
            finalDamage+=(unarmedManager.calculateSteelArmStyleDamage());
        }

        if (unarmedManager.canUseBerserk()) {
            finalDamage+=(unarmedManager.berserkDamage(finalDamage));
        }

        if (unarmedManager.canDisarm(target)) {
            unarmedManager.disarmCheck((Player) target);
        }

        if(canUseLimitBreak(mmoPlayer, target, SubSkillType.UNARMED_UNARMED_LIMIT_BREAK))
        {
            finalDamage+=(getLimitBreakDamage(mmoPlayer, target, SubSkillType.UNARMED_UNARMED_LIMIT_BREAK));
        }

        applyScaledModifiers(initialDamage, finalDamage, event);
        processCombatXP(mmoPlayer, target, PrimarySkillType.UNARMED);

        printFinalDamageDebug(player, event, mmoPlayer);
    }

    private static void processTamingCombat(@NotNull LivingEntity target, @Nullable Player master, @NotNull Wolf wolf, @NotNull EntityDamageByEntityEvent event) {
        double initialDamage = event.getDamage();
        double finalDamage = initialDamage;

        if(master != null && master.isOnline() && master.isValid()) {
            OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(master);

            //Make sure the profiles been loaded
            if(mmoPlayer == null) {
                return;
            }

            TamingManager tamingManager = ((McMMOPlayer) (mmoPlayer)).getTamingManager();

            if (tamingManager.canUseFastFoodService()) {
                tamingManager.fastFoodService(wolf, event.getDamage());
            }

            tamingManager.pummel(target, wolf);

            if (tamingManager.canUseSharpenedClaws()) {
                finalDamage+=tamingManager.sharpenedClaws();
            }

            if (tamingManager.canUseGore()) {
                finalDamage+=tamingManager.gore(target, initialDamage);
            }

            applyScaledModifiers(initialDamage, finalDamage, event);
            processCombatXP(mmoPlayer, target, PrimarySkillType.TAMING);
        }

    }

    private static void processArcheryCombat(@NotNull LivingEntity target, @NotNull Player player, @NotNull EntityDamageByEntityEvent event, @NotNull Projectile arrow) {
        double initialDamage = event.getDamage();

        OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(player);

        //Make sure the profiles been loaded
        if(mmoPlayer == null) {
            cleanupArrowMetadata(arrow);
            return;
        }

        ArcheryManager archeryManager = ((McMMOPlayer) (mmoPlayer)).getArcheryManager();
        
        double finalDamage = event.getDamage();

        if (archeryManager.canSkillShot()) {
            //Not Additive
            finalDamage = archeryManager.skillShot(initialDamage);
        }

        if (archeryManager.canDaze(target)) {
            finalDamage+=archeryManager.daze((Player) target); //the cast is checked by the if condition
        }

        if (!arrow.hasMetadata(mcMMO.infiniteArrowKey) && archeryManager.canRetrieveArrows()) {
            archeryManager.retrieveArrows(target, arrow);
        }

        if(canUseLimitBreak(mmoPlayer, target, SubSkillType.ARCHERY_ARCHERY_LIMIT_BREAK))
        {
            finalDamage+=getLimitBreakDamage(mmoPlayer, target, SubSkillType.ARCHERY_ARCHERY_LIMIT_BREAK);
        }

        double distanceMultiplier = archeryManager.distanceXpBonusMultiplier(target, arrow);
        double forceMultiplier = 1.0; //Hacky Fix - some plugins spawn arrows and assign them to players after the ProjectileLaunchEvent fires

        if(arrow.hasMetadata(mcMMO.bowForceKey))
            forceMultiplier = arrow.getMetadata(mcMMO.bowForceKey).get(0).asDouble();

        applyScaledModifiers(initialDamage, finalDamage, event);

        printFinalDamageDebug(player, event, mmoPlayer,
                "Distance Multiplier: "+distanceMultiplier,
                "Force Multiplier: "+forceMultiplier,
                "Initial Damage: "+initialDamage,
                "Final Damage: "+finalDamage);
        processCombatXP(mmoPlayer, target, PrimarySkillType.ARCHERY, distanceMultiplier);

        //Clean data
        cleanupArrowMetadata(arrow);
    }

    private static void processCrossbowCombat(LivingEntity target, Player player, EntityDamageByEntityEvent event, Projectile arrow) {
        double initialDamage = event.getDamage();

        OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(player);

        //Make sure the profiles been loaded
        if(mmoPlayer == null) {
            return;
        }

        CrossbowsManager crossbowsManager = ((McMMOPlayer) (mmoPlayer)).getCrossbowManager();

        double finalDamage = event.getDamage();

        if (target instanceof Player && PrimarySkillType.UNARMED.getPVPEnabled()) {
            UnarmedManager unarmedManager = mcMMO.getUserManager().queryPlayer((Player) target).getUnarmedManager();

            if (unarmedManager.canDeflect()) {
                event.setCancelled(unarmedManager.deflectCheck());

                if (event.isCancelled()) {
                    return;
                }
            }
        }

        if(canUseLimitBreak(mmoPlayer, target, SubSkillType.CROSSBOWS_CROSSBOWS_LIMIT_BREAK))
        {
            finalDamage+=getLimitBreakDamage(mmoPlayer, target, SubSkillType.CROSSBOWS_CROSSBOWS_LIMIT_BREAK);
        }

        double distanceMultiplier = crossbowsManager.distanceXpBonusMultiplier(target, arrow);
        applyScaledModifiers(initialDamage, finalDamage, event);


//        printFinalDamageDebug(player, event, mcMMOPlayer,
//                "Distance Multiplier: "+distanceMultiplier,
//                "Force Multiplier: "+forceMultiplier,
//                "Initial Damage: "+initialDamage,
//                "Final Damage: "+finalDamage);
        processCombatXP(mmoPlayer, target, PrimarySkillType.CROSSBOWS, distanceMultiplier);
    }

    /**
     * Apply combat modifiers and process and XP gain.
     *
     * @param event The event to run the combat checks on.
     */
    public static void processCombatAttack(@NotNull EntityDamageByEntityEvent event, @NotNull Entity painSourceRoot, @NotNull LivingEntity target) {
        Entity painSource = event.getDamager();
        EntityType entityType = painSource.getType();

        if (target instanceof Player) {
            if(ExperienceConfig.getInstance().isNPCInteractionPrevented()) {
                if (Misc.isNPCEntityExcludingVillagers(target)) {
                    return;
                }
            }

            Player player = (Player) target;
            if (!mcMMO.getUserManager().hasPlayerDataKey(player)) {
                return;
            }

            OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(player);
            AcrobaticsManager acrobaticsManager = ((McMMOPlayer) (mmoPlayer)).getAcrobaticsManager();

            if (acrobaticsManager.canDodge(target)) {
                event.setDamage(acrobaticsManager.dodgeCheck(painSourceRoot, event.getDamage()));
            }

            if (ItemUtils.isSword(player.getInventory().getItemInMainHand())) {
                if (!PrimarySkillType.SWORDS.shouldProcess(target)) {
                    return;
                }

                SwordsManager swordsManager = ((McMMOPlayer) (mmoPlayer)).getSwordsManager();

                if (swordsManager.canUseCounterAttack(painSource)) {
                    swordsManager.counterAttackChecks((LivingEntity) painSource, event.getDamage());
                }
            }
        }

        if (painSourceRoot instanceof Player && entityType == EntityType.PLAYER) {
            Player player = (Player) painSourceRoot;

            if (!mcMMO.getUserManager().hasPlayerDataKey(player)) {
                return;
            }

            ItemStack heldItem = player.getInventory().getItemInMainHand();

            if (target instanceof Tameable) {
                if (heldItem.getType() == Material.BONE) {
                    TamingManager tamingManager = mcMMO.getUserManager().queryPlayer(player).getTamingManager();

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
                if (!PrimarySkillType.SWORDS.shouldProcess(target)) {
                    return;
                }

                if (PrimarySkillType.SWORDS.getPermissions(player)) {
                    processSwordCombat(target, player, event);
                }
            }
            else if (ItemUtils.isAxe(heldItem)) {
                if (!PrimarySkillType.AXES.shouldProcess(target)) {
                    return;
                }

                if (PrimarySkillType.AXES.getPermissions(player)) {
                    processAxeCombat(target, player, event);
                }
            }
            else if (ItemUtils.isUnarmed(heldItem)) {
                if (!PrimarySkillType.UNARMED.shouldProcess(target)) {
                    return;
                }

                if (PrimarySkillType.UNARMED.getPermissions(player)) {
                    processUnarmedCombat(target, player, event);
                }
            }
            else if (ItemUtils.isTrident(heldItem)) {
                if(!PrimarySkillType.TRIDENTS.shouldProcess(target)) {
                    return;
                }

                if(PrimarySkillType.TRIDENTS.getPermissions(player)) {
                    processTridentCombat(target, player, event);
                }
            }
        }

        else if (entityType == EntityType.WOLF) {
            Wolf wolf = (Wolf) painSource;
            AnimalTamer tamer = wolf.getOwner();

            if (tamer instanceof Player && PrimarySkillType.TAMING.shouldProcess(target)) {
                Player master = (Player) tamer;

                if (!Misc.isNPCEntityExcludingVillagers(master) && PrimarySkillType.TAMING.getPermissions(master)) {
                    processTamingCombat(target, master, wolf, event);
                }
            }
        }
        else if (entityType == EntityType.ARROW || entityType == EntityType.SPECTRAL_ARROW) {
            Projectile arrow = (Projectile) painSource;
            ProjectileSource projectileShooter = arrow.getShooter();

            //Determine if the arrow belongs to a bow or xbow
            if (projectileShooter instanceof Player) {
                Player player = (Player) projectileShooter;

                //Has metadata
                if(arrow.getMetadata(mcMMO.PROJECTILE_ORIGIN_METAKEY).size() > 0) {
                    if (isProjectileFromBow(arrow)) {
                        if (PrimarySkillType.ARCHERY.shouldProcess(target)) {
                            if (!Misc.isNPCEntityExcludingVillagers(player) && PrimarySkillType.ARCHERY.getPermissions(player)) {
                                processArcheryCombat(target, player, event, arrow);
                            }
                        }
                    } else if (isProjectileFromCrossbow(arrow)) {
                        if (PrimarySkillType.CROSSBOWS.shouldProcess(target)) {
                            if (!Misc.isNPCEntityExcludingVillagers(player) && PrimarySkillType.CROSSBOWS.getPermissions(player)) {
                                processCrossbowCombat(target, player, event, arrow);
                            }
                        }
                    }
                } else {
                    //Cleanup Arrow
                    cleanupArrowMetadata(arrow);
                }

                if (target.getType() != EntityType.CREEPER && !Misc.isNPCEntityExcludingVillagers(player) && PrimarySkillType.TAMING.getPermissions(player)) {
                    OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(player);

                    if(mmoPlayer == null) {
                        return;
                    }

                    TamingManager tamingManager = ((McMMOPlayer) (mmoPlayer)).getTamingManager();
                    tamingManager.attackTarget(target);
                }
            }
        }
    }

    private static boolean isProjectileFromCrossbow(Projectile arrow) {
        return arrow.getMetadata(mcMMO.PROJECTILE_ORIGIN_METAKEY).get(0).asInt() == 2;
    }

    private static boolean isProjectileFromBow(Projectile arrow) {
        return arrow.getMetadata(mcMMO.PROJECTILE_ORIGIN_METAKEY).get(0).asInt() == 1;
    }

    /**
     * This cleans up names from displaying in chat as hearts
     * @param entity target entity
     */
    public static void fixNames(@NotNull LivingEntity entity)
    {
        List<MetadataValue> metadataValue = entity.getMetadata(TransientMetadataTools.OLD_NAME_METAKEY);

        if(metadataValue.size() <= 0)
            return;

        OldName oldName = (OldName) metadataValue.get(0);
        entity.setCustomName(oldName.asString());
        entity.setCustomNameVisible(false);
    }

    /**
     * Calculate and return the RAW damage bonus from Limit Break before reductions
     * @param attacker attacking player
     * @param defender defending living entity
     * @param subSkillType the specific limit break skill for calculations
     * @return the RAW damage bonus from Limit Break which is applied before reductions
     */
    public static int getLimitBreakDamage(@NotNull OnlineMMOPlayer attacker, @NotNull LivingEntity defender, @NotNull SubSkillType subSkillType) {
        if(defender instanceof Player) {
            Player playerDefender = (Player) defender;
            return getLimitBreakDamageAgainstQuality(attacker, subSkillType, getArmorQualityLevel(playerDefender));
        } else {
            return getLimitBreakDamageAgainstQuality(attacker, subSkillType, 1000);
        }
    }

    /**
     * Calculate the RAW daamge value of limit break based on the armor quality of the target
     * PVE mobs are passed in with a value of 1000 for armor quality, hacky... I'll change it later
     * @param attacker Living entity attacker
     * @param subSkillType Target limit break
     * @param armorQualityLevel Armor quality level
     * @return the RAW damage boost after its been mutated by armor quality
     */
    public static int getLimitBreakDamageAgainstQuality(@NotNull OnlineMMOPlayer attacker, @NotNull SubSkillType subSkillType, int armorQualityLevel) {
        int rawDamageBoost = RankUtils.getRank(attacker, subSkillType);

        if(armorQualityLevel <= 4) {
            rawDamageBoost *= .25; //75% Nerf
        } else if(armorQualityLevel <= 8) {
            rawDamageBoost *= .50; //50% Nerf
        } else if(armorQualityLevel <= 12) {
            rawDamageBoost *= .75; //25% Nerf
        }

        return rawDamageBoost;
    }

    /**
     * Get the quality level of the armor of a player used for Limit Break calculations
     * @param defender target defending player
     * @return the armor quality of the defending player
     */
    public static int getArmorQualityLevel(@NotNull Player defender) {
        int armorQualityLevel = 0;

        for(ItemStack itemStack : defender.getInventory().getArmorContents()) {
            if(itemStack != null) {
                armorQualityLevel += getArmorQuality(itemStack);
            }
        }

        return armorQualityLevel;
    }

    /**
     * Get the armor quality for a specific item used for Limit Break calculations
     * @param itemStack target item stack
     * @return the armor quality of a specific Item Stack
     */
    private static int getArmorQuality(@NotNull ItemStack itemStack) {
        return mcMMO.getMaterialMapStore().getTier(itemStack.getType().getKey().getKey());
    }

    /**
     * Checks if mmoPlayer has access to their weapons limit break
     * @param mmoPlayer target entity
     * @return true if the mmoPlayer has access to the limit break
     */
    public static boolean canUseLimitBreak(@NotNull OnlineMMOPlayer mmoPlayer, LivingEntity target, @NotNull SubSkillType subSkillType) {
        if(target instanceof Player || AdvancedConfig.getInstance().canApplyLimitBreakPVE()) {
            return RankUtils.hasUnlockedSubskill(mmoPlayer, subSkillType)
                    && Permissions.isSubSkillEnabled(Misc.adaptPlayer(mmoPlayer), subSkillType);
        } else {
            return false;
        }
    }

    /**
     * Attempt to damage target for value dmg with reason CUSTOM
     *
     * @param target LivingEntity which to attempt to damage
     * @param damage Amount of damage to attempt to do
     */
    @Deprecated
    public static void dealDamage(@NotNull LivingEntity target, double damage) {
        dealDamage(target, damage, DamageCause.CUSTOM, null);
    }

    /**
     * Attempt to damage target for value dmg with reason ENTITY_ATTACK with damager attacker
     *
     * @param target LivingEntity which to attempt to damage
     * @param damage Amount of damage to attempt to do
     * @param attacker Player to pass to event as damager
     */
    @Deprecated
    public static void dealDamage(@NotNull LivingEntity target, double damage, @NotNull LivingEntity attacker) {
        dealDamage(target, damage, DamageCause.CUSTOM, attacker);
    }

//    /**
//     * Attempt to damage target for value dmg with reason ENTITY_ATTACK with damager attacker
//     *
//     * @param target LivingEntity which to attempt to damage
//     * @param damage Amount of damage to attempt to do
//     * @param attacker Player to pass to event as damager
//     */
//    public static void dealDamage(LivingEntity target, double damage, Map<DamageModifier, Double> modifiers, LivingEntity attacker) {
//        if (target.isDead()) {
//            return;
//        }
//
//        // Aren't we applying the damage twice????
//        target.damage(getFakeDamageFinalResult(attacker, target, damage, modifiers));
//    }

    /**
     * Attempt to damage target for value dmg with reason ENTITY_ATTACK with damager attacker
     *
     * @param target LivingEntity which to attempt to damage
     * @param damage Amount of damage to attempt to do
     * @param attacker Player to pass to event as damager
     */
    @Deprecated
    public static void dealDamage(@NotNull LivingEntity target, double damage, @NotNull DamageCause cause, @Nullable Entity attacker) {
        if (target.isDead()) {
            return;
        }

        if(canDamage(attacker, target, cause, damage)) {
            applyIgnoreDamageMetadata(target);
            target.damage(damage);
            removeIgnoreDamageMetadata(target);
        }
    }

    private static boolean processingNoInvulnDamage;
    public static boolean isProcessingNoInvulnDamage() {
        return processingNoInvulnDamage;
    }

    public static void dealNoInvulnerabilityTickDamage(@NotNull LivingEntity target, double damage, @Nullable Entity attacker) {
        if (target.isDead()) {
            return;
        }

        // TODO: This is horrible, but there is no cleaner way to do this without potentially breaking existing code right now
        // calling damage here is a double edged sword: On one hand, without a call, plugins won't see this properly when the entity dies,
        // potentially mis-attributing the death cause; calling a fake event would partially fix this, but this and setting the last damage
        // cause do have issues around plugin observability. This is not a perfect solution, but it appears to be the best one here
        // We also set no damage ticks to 0, to ensure that damage is applied for this case, and reset it back to the original value
        // Snapshot current state so we can pop up properly
        boolean wasMetaSet = hasIgnoreDamageMetadata(target);
        boolean wasProcessing = processingNoInvulnDamage;
        // set markers
        processingNoInvulnDamage = true;
        applyIgnoreDamageMetadata(target);
        int noDamageTicks = target.getNoDamageTicks();
        target.setNoDamageTicks(0);
        target.damage(damage, attacker);
        target.setNoDamageTicks(noDamageTicks);
        if (!wasMetaSet)
            removeIgnoreDamageMetadata(target);
        if (!wasProcessing)
            processingNoInvulnDamage = false;
    }

    public static void removeIgnoreDamageMetadata(@NotNull LivingEntity target) {
        target.removeMetadata(mcMMO.CUSTOM_DAMAGE_METAKEY, mcMMO.p);
    }

    public static void applyIgnoreDamageMetadata(@NotNull LivingEntity target) {
        target.setMetadata(mcMMO.CUSTOM_DAMAGE_METAKEY, mcMMO.metadataValue);
    }

    public static boolean hasIgnoreDamageMetadata(@NotNull LivingEntity target) {
        return target.getMetadata(mcMMO.CUSTOM_DAMAGE_METAKEY).size() != 0;
    }

    public static void dealNoInvulnerabilityTickDamageRupture(@NotNull LivingEntity target, double damage, Entity attacker, int toolTier) {
        if (target.isDead()) {
            return;
        }

        dealNoInvulnerabilityTickDamage(target, damage, attacker);
    }

    /**
     * Apply Area-of-Effect ability actions.
     *
     * @param attacker The attacking player
     * @param target The defending entity
     * @param damage The initial damage amount
     * @param type The type of skill being used
     */
    public static void applyAbilityAoE(@NotNull Player attacker, @NotNull LivingEntity target, double damage, Map<DamageModifier, Double> modifiers, @NotNull PrimarySkillType type) {
        int numberOfTargets = getTier(attacker.getInventory().getItemInMainHand()); // The higher the weapon tier, the more targets you hit
        double damageAmount = Math.max(damage, 1);

        for (Entity entity : target.getNearbyEntities(2.5, 2.5, 2.5)) {
            if (numberOfTargets <= 0) {
                break;
            }

            if ((ExperienceConfig.getInstance().isNPCInteractionPrevented() && Misc.isNPCEntityExcludingVillagers(entity)) || !(entity instanceof LivingEntity) || !shouldBeAffected(attacker, entity)) {
                continue;
            }

            LivingEntity livingEntity = (LivingEntity) entity;
            EventUtils.callFakeArmSwingEvent(attacker);

            switch (type) {
                case SWORDS:
                    if (entity instanceof Player) {
                        NotificationManager.sendPlayerInformation((Player)entity, NotificationType.SUBSKILL_MESSAGE, "Swords.Combat.SS.Struck");
                    }

                    mcMMO.getUserManager().queryPlayer(attacker).getSwordsManager().ruptureCheck(target);
                    break;

                case AXES:
                    if (entity instanceof Player) {
                        NotificationManager.sendPlayerInformation((Player)entity, NotificationType.SUBSKILL_MESSAGE, "Axes.Combat.SS.Struck");
                    }

                    break;

                default:
                    break;
            }

            dealDamage(livingEntity, damageAmount, attacker);
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
    public static void processCombatXP(@NotNull OnlineMMOPlayer mmoPlayer, LivingEntity target, PrimarySkillType primarySkillType) {
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
    public static void processCombatXP(@NotNull OnlineMMOPlayer mmoPlayer, LivingEntity target, PrimarySkillType primarySkillType, double multiplier) {
        double baseXP = 0;
        XPGainReason xpGainReason;

        if (target instanceof Player) {
            if (!ExperienceConfig.getInstance().getExperienceGainsPlayerVersusPlayerEnabled() || mcMMO.getPartyManager().inSameParty(Misc.adaptPlayer(mmoPlayer), (Player) target)) {
                return;
            }

            xpGainReason = XPGainReason.PVP;
            Player defender = (Player) target;

            if (defender.isOnline() && SkillUtils.cooldownExpired(mmoPlayer.getRespawnATS(), Misc.PLAYER_RESPAWN_COOLDOWN_SECONDS)) {
                baseXP = 20 * ExperienceConfig.getInstance().getPlayerVersusPlayerXP();
            }
        }
        else {
            if (mcMMO.getModManager().isCustomEntity(target)) {
                baseXP = mcMMO.getModManager().getEntity(target).getXpMultiplier();
            }
            else if (target instanceof Animals) {
                EntityType type = target.getType();
                baseXP = ExperienceConfig.getInstance().getAnimalsXP(type);
            }
            else if (target instanceof Monster)
            {
                EntityType type = target.getType();
                baseXP = ExperienceConfig.getInstance().getCombatXP(type);
            }
            else {
                EntityType type = target.getType();

                if (ExperienceConfig.getInstance().hasCombatXP(type)) {
                    if (type == EntityType.IRON_GOLEM)
                    {
                        if (!((IronGolem) target).isPlayerCreated()) {
                            baseXP = ExperienceConfig.getInstance().getCombatXP(type);
                        }
                    }
                    else
                    {
                        baseXP = ExperienceConfig.getInstance().getCombatXP(type);
                    }
                }
                else
                {
                    baseXP = 1.0;
                    mcMMO.getModManager().addCustomEntity(target);
                }
            }

            if(getPersistentData().hasMobFlag(MobMetaFlagType.COTW_SUMMONED_MOB, target)) {
                baseXP = 0;
            } else if(getPersistentData().hasMobFlag(MobMetaFlagType.MOB_SPAWNER_MOB, target) || target.hasMetadata("ES")) {
                baseXP *= ExperienceConfig.getInstance().getSpawnedMobXpMultiplier();
            } else if(getPersistentData().hasMobFlag(MobMetaFlagType.NETHER_PORTAL_MOB, target)) {
                baseXP *= ExperienceConfig.getInstance().getNetherPortalXpMultiplier();
            } else if(getPersistentData().hasMobFlag(MobMetaFlagType.EGG_MOB, target)) {
                baseXP *= ExperienceConfig.getInstance().getEggXpMultiplier();
            } else if (getPersistentData().hasMobFlag(MobMetaFlagType.PLAYER_BRED_MOB, target)) {
                baseXP *= ExperienceConfig.getInstance().getBredMobXpMultiplier();
            } else if(getPersistentData().hasMobFlag(MobMetaFlagType.PLAYER_TAMED_MOB, target)) {
                baseXP *= ExperienceConfig.getInstance().getTamedMobXpMultiplier();
            }

            baseXP *= 10;
            xpGainReason = XPGainReason.PVE;
        }

        baseXP *= multiplier;

        if (baseXP != 0) {
            new AwardCombatXpTask(mmoPlayer, primarySkillType, baseXP, target, xpGainReason).runTaskLater(mcMMO.p, 0);
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
        if (entity instanceof Player) {
            Player defender = (Player) entity;

            //TODO: NPC Interaction?
            if(mcMMO.getUserManager().queryPlayer(defender) == null)
                return true;

            if (!defender.getWorld().getPVP() || defender == player || mcMMO.getUserManager().queryPlayer(defender).getGodMode()) {
                return false;
            }

            if ((mcMMO.getPartyManager().inSameParty(player, defender) || mcMMO.getPartyManager().areAllies(player, defender)) && !(Permissions.friendlyFire(player) && Permissions.friendlyFire(defender))) {
                return false;
            }

            // Vanished players should not be able to get hit by AoE effects
            if (!player.canSee(defender)) {
                return false;
            }
            
            // Spectators should not be affected 
            if (defender.getGameMode() == GameMode.SPECTATOR) {
                return false;
            }

            // It may seem a bit redundant but we need a check here to prevent bleed from being applied in applyAbilityAoE()
            return getFakeDamageFinalResult(player, entity, 1.0) != 0;
        }
        else if (entity instanceof Tameable) {
            Tameable tameableEntity = (Tameable) entity;

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
        return (entity.getNoDamageTicks() > entity.getMaximumNoDamageTicks() / 2.0F) && (eventDamage <= entity.getLastDamage());
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

            if (tamer instanceof Player) {
                Player owner = (Player) tamer;

                return (owner == attacker || mcMMO.getPartyManager().inSameParty(attacker, owner) || mcMMO.getPartyManager().areAllies(attacker, owner));
            }
        }

        return false;
    }

    @Deprecated
    public static double getFakeDamageFinalResult(@Nullable Entity attacker, @NotNull Entity target, double damage) {
        return getFakeDamageFinalResult(attacker, target, DamageCause.ENTITY_ATTACK, new EnumMap<>(ImmutableMap.of(DamageModifier.BASE, damage)));
    }

    @Deprecated
    public static double getFakeDamageFinalResult(@Nullable Entity attacker, @NotNull Entity target, @NotNull DamageCause damageCause, double damage) {
        EntityDamageEvent damageEvent = sendEntityDamageEvent(attacker, target, damageCause, damage);

        if (damageEvent.isCancelled()) {
            return 0;
        }

        return damageEvent.getFinalDamage();
    }

    public static boolean canDamage(@NotNull Entity attacker, @NotNull Entity target, @NotNull DamageCause damageCause, double damage) {
        EntityDamageEvent damageEvent = sendEntityDamageEvent(attacker, target, damageCause, damage);

        return !damageEvent.isCancelled();
    }

    public static @NotNull EntityDamageEvent sendEntityDamageEvent(@Nullable Entity attacker, @NotNull Entity target, @NotNull DamageCause damageCause, double damage) {
        EntityDamageEvent damageEvent = attacker == null ? new FakeEntityDamageEvent(target, damageCause, damage) : new FakeEntityDamageByEntityEvent(attacker, target, damageCause, damage);
        mcMMO.p.getServer().getPluginManager().callEvent(damageEvent);
        return damageEvent;
    }

    public static double getFakeDamageFinalResult(@Nullable Entity attacker, @NotNull Entity target, @NotNull Map<DamageModifier, Double> modifiers) {
        return getFakeDamageFinalResult(attacker, target, DamageCause.ENTITY_ATTACK, modifiers);
    }

    public static double getFakeDamageFinalResult(@Nullable Entity attacker, @NotNull Entity target, double damage, @NotNull Map<DamageModifier, Double> modifiers) {
        return getFakeDamageFinalResult(attacker, target, DamageCause.ENTITY_ATTACK, getScaledModifiers(damage, modifiers));
    }

    public static double getFakeDamageFinalResult(@Nullable Entity attacker, @NotNull Entity target, @NotNull DamageCause cause, @NotNull Map<DamageModifier, Double> modifiers) {
        EntityDamageEvent damageEvent = attacker == null ? new FakeEntityDamageEvent(target, cause, modifiers) : new FakeEntityDamageByEntityEvent(attacker, target, cause, modifiers);
        mcMMO.p.getServer().getPluginManager().callEvent(damageEvent);

        if (damageEvent.isCancelled()) {
            return 0;
        }

        return damageEvent.getFinalDamage();
    }

    private static @NotNull Map<DamageModifier, Double> getModifiers(@NotNull EntityDamageEvent event) {
        Map<DamageModifier, Double> modifiers = new HashMap<>();
        for (DamageModifier modifier : DamageModifier.values()) {
            modifiers.put(modifier, event.getDamage(modifier));
        }

        return modifiers;
    }

    private static @NotNull Map<DamageModifier, Double> getScaledModifiers(double damage, @NotNull Map<DamageModifier, Double> modifiers) {
        Map<DamageModifier, Double> scaledModifiers = new HashMap<>();

        for (DamageModifier modifier : modifiers.keySet()) {
            if (modifier == DamageModifier.BASE) {
                scaledModifiers.put(modifier, damage);
                continue;
            }

            scaledModifiers.put(modifier, damage * modifiers.get(modifier));
        }

        return scaledModifiers;
    }

    public static @NotNull EntityDamageByEntityEvent applyScaledModifiers(double initialDamage, double finalDamage, @NotNull EntityDamageByEntityEvent event) {
        // No additional damage
        if (initialDamage == finalDamage) {
            return event;
        }

        for (DamageModifier modifier : DamageModifier.values()) {
            if (!event.isApplicable(modifier)) {
                continue;
            }

            if (modifier == DamageModifier.BASE) {
                event.setDamage(modifier, finalDamage);
                continue;
            }

            event.setDamage(modifier, finalDamage / initialDamage * event.getDamage(modifier));
        }

        return event;
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
        }
        else if (ItemUtils.isStoneTool(inHand)) {
            tier = 2;
        }
        else if (ItemUtils.isIronTool(inHand)) {
            tier = 3;
        }
        else if (ItemUtils.isGoldTool(inHand)) {
            tier = 1;
        }
        else if (ItemUtils.isDiamondTool(inHand)) {
            tier = 4;
        } else if (ItemUtils.isNetheriteTool(inHand)) {
            tier = 5;
        }
        else if (mcMMO.getModManager().isCustomTool(inHand)) {
            tier = mcMMO.getModManager().getTool(inHand).getTier();
        }

        return tier;
    }

    public static void handleHealthbars(@NotNull Entity attacker, @NotNull LivingEntity target, double damage, @NotNull mcMMO plugin) {
        if (!(attacker instanceof Player)) {
            return;
        }

        Player player = (Player) attacker;

        if (Misc.isNPCEntityExcludingVillagers(player) || Misc.isNPCEntityExcludingVillagers(target)) {
            return;
        }

        if (!player.hasMetadata(mcMMO.playerDataKey)) {
            return;
        }

        MobHealthbarUtils.handleMobHealthbars(target, damage, plugin);
    }

    public static void modifyMoveSpeed(@NotNull LivingEntity livingEntity, double multiplier) {
        AttributeInstance attributeInstance = livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);

        if(attributeInstance != null) {
            double normalSpeed = attributeInstance.getBaseValue();
            attributeInstance.setBaseValue(normalSpeed * multiplier);
        }
    }

    /**
     * Clean up metadata from a projectile
     *
     * @param entity projectile
     */
    public static void cleanupArrowMetadata(@NotNull Projectile entity) {
        if(entity.hasMetadata(mcMMO.infiniteArrowKey)) {
            entity.removeMetadata(mcMMO.infiniteArrowKey, mcMMO.p);
        }

        if(entity.hasMetadata(mcMMO.bowForceKey)) {
            entity.removeMetadata(mcMMO.bowForceKey, mcMMO.p);
        }

        if(entity.hasMetadata(mcMMO.arrowDistanceKey)) {
            entity.removeMetadata(mcMMO.arrowDistanceKey, mcMMO.p);
        }
    }

    /**
     * Clean up metadata from a projectile after a minute has passed
     *
     * @param entity the projectile
     */
    public static void delayArrowMetaCleanup(@NotNull Projectile entity) {
        Bukkit.getServer().getScheduler().runTaskLater(mcMMO.p, () -> { cleanupArrowMetadata(entity);}, 20*60);
    }
}
