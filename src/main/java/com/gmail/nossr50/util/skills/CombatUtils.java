package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.meta.OldName;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.events.fake.FakeEntityDamageByEntityEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.metadata.MobMetaFlagType;
import com.gmail.nossr50.metadata.MobMetadataService;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.runnables.skills.AwardCombatXpTask;
import com.gmail.nossr50.skills.acrobatics.AcrobaticsManager;
import com.gmail.nossr50.skills.archery.ArcheryManager;
import com.gmail.nossr50.skills.axes.AxesManager;
import com.gmail.nossr50.skills.swords.SwordsManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;
import com.gmail.nossr50.util.*;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class CombatUtils {

    private CombatUtils() {}

    private static @NotNull MobMetadataService getMobMetadataService() {
        return mcMMO.getMetadataService().getMobMetadataService();
    }

    //Likely.. because who knows what plugins are throwing around
    public static boolean isDamageLikelyFromNormalCombat(@NotNull DamageCause damageCause) {
        return switch (damageCause) {
            case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK, PROJECTILE -> true;
            default -> false;
        };
    }

    public static boolean hasWeakenedDamage(@NotNull LivingEntity livingEntity) {
        return livingEntity.hasPotionEffect(PotionEffectType.WEAKNESS);
    }

    private static void processSwordCombat(@NotNull LivingEntity target, @NotNull Player player, @NotNull EntityDamageByEntityEvent event) {
        if (event.getCause() == DamageCause.THORNS) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        //Make sure the profiles been loaded
        if(mcMMOPlayer == null) {
            return;
        }

        SwordsManager swordsManager = mcMMOPlayer.getSwordsManager();
        double boostedDamage = event.getDamage();

        if (swordsManager.canActivateAbility()) {
            mcMMOPlayer.checkAbilityActivation(PrimarySkillType.SWORDS);
        }

        if(target.getHealth() - event.getFinalDamage() > 0) {
            swordsManager.processRupture(target);
        }

        //Add Stab Damage
        if(swordsManager.canUseStab())
        {
            boostedDamage += (swordsManager.getStabDamage() * mcMMOPlayer.getAttackStrength());
        }

        if (swordsManager.canUseSerratedStrike()) {
            swordsManager.serratedStrikes(target, event.getDamage());
        }

        if(canUseLimitBreak(player, target, SubSkillType.SWORDS_SWORDS_LIMIT_BREAK))
        {
            boostedDamage += (getLimitBreakDamage(player, target, SubSkillType.SWORDS_SWORDS_LIMIT_BREAK) * mcMMOPlayer.getAttackStrength());
        }

        event.setDamage(boostedDamage);
        processCombatXP(mcMMOPlayer, target, PrimarySkillType.SWORDS);

        printFinalDamageDebug(player, event, mcMMOPlayer);
    }

    private static void printFinalDamageDebug(@NotNull Player player, @NotNull EntityDamageByEntityEvent event, @NotNull McMMOPlayer mcMMOPlayer, @Nullable String @Nullable ... extraInfoLines) {
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

        double boostedDamage = event.getDamage();

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        //Make sure the profiles been loaded
        if(mcMMOPlayer == null) {
            return;
        }

        AxesManager axesManager = mcMMOPlayer.getAxesManager();

        if (axesManager.canActivateAbility()) {
            mcMMOPlayer.checkAbilityActivation(PrimarySkillType.AXES);
        }

        if (axesManager.canUseAxeMastery()) {
            boostedDamage+=axesManager.axeMastery();
        }

        if (axesManager.canImpact(target)) {
            axesManager.impactCheck(target);
        }
        else if (axesManager.canGreaterImpact(target)) {
            boostedDamage+=axesManager.greaterImpact(target);
        }

        if (axesManager.canUseSkullSplitter(target)) {
            axesManager.skullSplitterCheck(target, event.getDamage());
        }

        if (axesManager.canCriticalHit(target)) {
            boostedDamage+=(axesManager.criticalHit(target, boostedDamage) * mcMMOPlayer.getAttackStrength());
        }

        if(canUseLimitBreak(player, target, SubSkillType.AXES_AXES_LIMIT_BREAK))
        {
            boostedDamage+=(getLimitBreakDamage(player, target, SubSkillType.AXES_AXES_LIMIT_BREAK) * mcMMOPlayer.getAttackStrength());
        }

        event.setDamage(boostedDamage);
        processCombatXP(mcMMOPlayer, target, PrimarySkillType.AXES);

        printFinalDamageDebug(player, event, mcMMOPlayer);
    }

    private static void processUnarmedCombat(@NotNull LivingEntity target, @NotNull Player player, @NotNull EntityDamageByEntityEvent event) {
        if (event.getCause() == DamageCause.THORNS) {
            return;
        }

        double boostedDamage = event.getDamage();

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        //Make sure the profiles been loaded
        if(mcMMOPlayer == null) {
            return;
        }

        UnarmedManager unarmedManager = mcMMOPlayer.getUnarmedManager();

        if (unarmedManager.canActivateAbility()) {
            mcMMOPlayer.checkAbilityActivation(PrimarySkillType.UNARMED);
        }

        if (unarmedManager.canUseSteelArm()) {
            boostedDamage+=(unarmedManager.calculateSteelArmStyleDamage() * mcMMOPlayer.getAttackStrength());
        }

        if (unarmedManager.canUseBerserk()) {
            boostedDamage+=(unarmedManager.berserkDamage(boostedDamage) * mcMMOPlayer.getAttackStrength());
        }

        if (unarmedManager.canDisarm(target)) {
            unarmedManager.disarmCheck((Player) target);
        }

        if(canUseLimitBreak(player, target, SubSkillType.UNARMED_UNARMED_LIMIT_BREAK))
        {
            boostedDamage+=(getLimitBreakDamage(player, target, SubSkillType.UNARMED_UNARMED_LIMIT_BREAK) * mcMMOPlayer.getAttackStrength());
        }

        event.setDamage(boostedDamage);
        processCombatXP(mcMMOPlayer, target, PrimarySkillType.UNARMED);

        printFinalDamageDebug(player, event, mcMMOPlayer);
    }

    private static void processTamingCombat(@NotNull LivingEntity target, @Nullable Player master, @NotNull Wolf wolf, @NotNull EntityDamageByEntityEvent event) {
        double initialDamage = event.getDamage();
        double boostedDamage = initialDamage;

        if(master != null && master.isOnline() && master.isValid()) {
            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(master);

            //Make sure the profiles been loaded
            if(mcMMOPlayer == null) {
                return;
            }

            TamingManager tamingManager = mcMMOPlayer.getTamingManager();

            if (tamingManager.canUseFastFoodService()) {
                tamingManager.fastFoodService(wolf, event.getDamage());
            }

            tamingManager.pummel(target, wolf);

            if (tamingManager.canUseSharpenedClaws()) {
                boostedDamage+=tamingManager.sharpenedClaws();
            }

            if (tamingManager.canUseGore()) {
                boostedDamage+=tamingManager.gore(target, initialDamage);
            }

            event.setDamage(boostedDamage);
            processCombatXP(mcMMOPlayer, target, PrimarySkillType.TAMING, 3);
        }

    }

    private static void processArcheryCombat(@NotNull LivingEntity target, @NotNull Player player, @NotNull EntityDamageByEntityEvent event, @NotNull Projectile arrow) {
        double initialDamage = event.getDamage();

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        //Make sure the profiles been loaded
        if(mcMMOPlayer == null) {
            cleanupArrowMetadata(arrow);
            return;
        }

        ArcheryManager archeryManager = mcMMOPlayer.getArcheryManager();
        
        double boostedDamage = event.getDamage();

        if (archeryManager.canSkillShot()) {
            //Not Additive
            boostedDamage = archeryManager.skillShot(initialDamage);
        }

        if (archeryManager.canDaze(target)) {
            boostedDamage+=archeryManager.daze((Player) target); //the cast is checked by the if condition
        }

        if (!arrow.hasMetadata(MetadataConstants.METADATA_KEY_INF_ARROW) && archeryManager.canRetrieveArrows()) {
            archeryManager.retrieveArrows(target, arrow);
        }

        if(canUseLimitBreak(player, target, SubSkillType.ARCHERY_ARCHERY_LIMIT_BREAK))
        {
            boostedDamage+=getLimitBreakDamage(player, target, SubSkillType.ARCHERY_ARCHERY_LIMIT_BREAK);
        }

        double distanceMultiplier = archeryManager.distanceXpBonusMultiplier(target, arrow);
        double forceMultiplier = 1.0; //Hacky Fix - some plugins spawn arrows and assign them to players after the ProjectileLaunchEvent fires

        if(arrow.hasMetadata(MetadataConstants.METADATA_KEY_BOW_FORCE))
            forceMultiplier = arrow.getMetadata(MetadataConstants.METADATA_KEY_BOW_FORCE).get(0).asDouble();

        event.setDamage(boostedDamage);
        processCombatXP(mcMMOPlayer, target, PrimarySkillType.ARCHERY, forceMultiplier * distanceMultiplier);

        printFinalDamageDebug(player, event, mcMMOPlayer,
                "Distance Multiplier: "+distanceMultiplier,
                "Force Multiplier: "+forceMultiplier,
                "Initial Damage: "+initialDamage,
                "Final Damage: "+boostedDamage);
        //Clean data
        cleanupArrowMetadata(arrow);
    }

    /**
     * Apply combat modifiers and process and XP gain.
     *
     * @param event The event to run the combat checks on.
     */
    public static void processCombatAttack(@NotNull EntityDamageByEntityEvent event, @NotNull Entity painSourceRoot, @NotNull LivingEntity target) {
        Entity painSource = event.getDamager();
        EntityType entityType = painSource.getType();

        if (target instanceof Player player) {
            if(ExperienceConfig.getInstance().isNPCInteractionPrevented()) {
                if (Misc.isNPCEntityExcludingVillagers(target)) {
                    return;
                }
            }

            if (!UserManager.hasPlayerDataKey(player)) {
                return;
            }

            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
            AcrobaticsManager acrobaticsManager = mcMMOPlayer.getAcrobaticsManager();

            if (acrobaticsManager.canDodge(target)) {
                event.setDamage(acrobaticsManager.dodgeCheck(painSourceRoot, event.getDamage()));
            }

            if (ItemUtils.isSword(player.getInventory().getItemInMainHand())) {
                if (!mcMMO.p.getSkillTools().canCombatSkillsTrigger(PrimarySkillType.SWORDS, target)) {
                    return;
                }

                SwordsManager swordsManager = mcMMOPlayer.getSwordsManager();

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
                if (!mcMMO.p.getSkillTools().canCombatSkillsTrigger(PrimarySkillType.SWORDS, target)) {
                    return;
                }

                if (mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, PrimarySkillType.SWORDS)) {
                    processSwordCombat(target, player, event);

                }
            }
            else if (ItemUtils.isAxe(heldItem)) {
                if (!mcMMO.p.getSkillTools().canCombatSkillsTrigger(PrimarySkillType.AXES, target)) {
                    return;
                }

                if (mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, PrimarySkillType.AXES)) {
                    processAxeCombat(target, player, event);
                }
            }
            else if (ItemUtils.isUnarmed(heldItem)) {
                if (!mcMMO.p.getSkillTools().canCombatSkillsTrigger(PrimarySkillType.UNARMED, target)) {
                    return;
                }

                if (mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, PrimarySkillType.UNARMED)) {
                    processUnarmedCombat(target, player, event);
                }
            }
        }

        else if (entityType == EntityType.WOLF) {
            Wolf wolf = (Wolf) painSource;
            AnimalTamer tamer = wolf.getOwner();

            if (tamer instanceof Player master && mcMMO.p.getSkillTools().canCombatSkillsTrigger(PrimarySkillType.TAMING, target)) {

                if (!Misc.isNPCEntityExcludingVillagers(master) && mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(master, PrimarySkillType.TAMING)) {
                    processTamingCombat(target, master, wolf, event);
                }
            }
        }
        else if (entityType == EntityType.ARROW || entityType == EntityType.SPECTRAL_ARROW) {
            Projectile arrow = (Projectile) painSource;
            ProjectileSource projectileSource = arrow.getShooter();

            if (projectileSource instanceof Player player && mcMMO.p.getSkillTools().canCombatSkillsTrigger(PrimarySkillType.ARCHERY, target)) {

                if (!Misc.isNPCEntityExcludingVillagers(player) && mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, PrimarySkillType.ARCHERY)) {
                    processArcheryCombat(target, player, event, arrow);
                } else {
                    //Cleanup Arrow
                    cleanupArrowMetadata(arrow);
                }

                if (target.getType() != EntityType.CREEPER && !Misc.isNPCEntityExcludingVillagers(player) && mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, PrimarySkillType.TAMING)) {
                    McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

                    if(mcMMOPlayer == null)
                        return;

                    TamingManager tamingManager = mcMMOPlayer.getTamingManager();
                    tamingManager.attackTarget(target);
                }
            }
        }

    }

    /**
     * This cleans up names from displaying in chat as hearts
     * @param entity target entity
     */
    public static void fixNames(@NotNull LivingEntity entity)
    {
        List<MetadataValue> metadataValue = entity.getMetadata(MetadataConstants.METADATA_KEY_OLD_NAME_KEY);

        if(metadataValue.size() <= 0)
            return;

        OldName oldName = (OldName) metadataValue.get(0);
        entity.setCustomName(oldName.asString());
        entity.setCustomNameVisible(false);

        entity.removeMetadata(MetadataConstants.METADATA_KEY_OLD_NAME_KEY, mcMMO.p);
    }

    /**
     * Calculate and return the RAW damage bonus from Limit Break before reductions
     * @param attacker attacking player
     * @param defender defending living entity
     * @param subSkillType the specific limit break skill for calculations
     * @return the RAW damage bonus from Limit Break which is applied before reductions
     */
    public static int getLimitBreakDamage(@NotNull Player attacker, @NotNull LivingEntity defender, @NotNull SubSkillType subSkillType) {
        if(defender instanceof Player playerDefender) {
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
    public static int getLimitBreakDamageAgainstQuality(@NotNull Player attacker, @NotNull SubSkillType subSkillType, int armorQualityLevel) {
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
     * Checks if player has access to their weapons limit break
     * @param player target entity
     * @return true if the player has access to the limit break
     */
    public static boolean canUseLimitBreak(@NotNull Player player, LivingEntity target, @NotNull SubSkillType subSkillType) {
        if(target instanceof Player || mcMMO.p.getAdvancedConfig().canApplyLimitBreakPVE()) {
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
        target.removeMetadata(MetadataConstants.METADATA_KEY_CUSTOM_DAMAGE, mcMMO.p);
    }

    public static void applyIgnoreDamageMetadata(@NotNull LivingEntity target) {
        target.setMetadata(MetadataConstants.METADATA_KEY_CUSTOM_DAMAGE, MetadataConstants.MCMMO_METADATA_VALUE);
    }

    public static boolean hasIgnoreDamageMetadata(@NotNull LivingEntity target) {
        return target.getMetadata(MetadataConstants.METADATA_KEY_CUSTOM_DAMAGE).size() != 0;
    }

    public static void dealNoInvulnerabilityTickDamageRupture(@NotNull LivingEntity target, double damage, Entity attacker, int toolTier) {
        if (target.isDead()) {
            return;
        }

        dealNoInvulnerabilityTickDamage(target, damage, attacker);

//        //IFrame storage
////        int noDamageTicks = target.getNoDamageTicks();
//
////        String debug = "BLEED DMG RESULT: INC DMG:"+damage+", HP-Before:"+target.getHealth()+", HP-After:";
//
////        double incDmg = getFakeDamageFinalResult(attacker, target, DamageCause.ENTITY_ATTACK, damage);
//
////        double newHealth = Math.max(0, target.getHealth() - incDmg);
//
//        //Don't kill things with a stone or wooden weapon
////        if(toolTier < 3 && newHealth == 0)
////            return;
//
//        target.setMetadata(mcMMO.CUSTOM_DAMAGE_METAKEY, mcMMO.metadataValue);
//
//        if(newHealth == 0 && !(target instanceof Player))
//        {
//            target.damage(99999, attacker);
//        }
//        else
//        {
////            Vector beforeRuptureVec = new Vector(target.getVelocity().getX(), target.getVelocity().getY(), target.getVelocity().getZ()); ;
//            target.damage(damage, attacker);
////            debug+=target.getHealth();
//            Bukkit.broadcastMessage(debug);
////            target.setNoDamageTicks(noDamageTicks); //Do not add additional IFrames
////            target.setVelocity(beforeRuptureVec);
//        }
    }

    /**
     * Apply Area-of-Effect ability actions.
     *  @param attacker The attacking player
     * @param target The defending entity
     * @param damage The initial damage amount
     * @param type The type of skill being used
     */
    public static void applyAbilityAoE(@NotNull Player attacker, @NotNull LivingEntity target, double damage, @NotNull PrimarySkillType type) {
        int numberOfTargets = getTier(attacker.getInventory().getItemInMainHand()); // The higher the weapon tier, the more targets you hit
        double damageAmount = Math.max(damage, 1);

        for (Entity entity : target.getNearbyEntities(2.5, 2.5, 2.5)) {
            if (numberOfTargets <= 0) {
                break;
            }

            if ((ExperienceConfig.getInstance().isNPCInteractionPrevented() && Misc.isNPCEntityExcludingVillagers(entity))
                    || !(entity instanceof LivingEntity livingEntity) || !shouldBeAffected(attacker, entity)) {
                continue;
            }

            EventUtils.callFakeArmSwingEvent(attacker);

            switch (type) {
                case SWORDS:
                    if (entity instanceof Player) {
                        NotificationManager.sendPlayerInformation((Player)entity, NotificationType.SUBSKILL_MESSAGE, "Swords.Combat.SS.Struck");
                    }

                    McMMOPlayer mmoAttacker = UserManager.getPlayer(attacker);

                    if(mmoAttacker != null) {
                        mmoAttacker.getSwordsManager().processRupture(livingEntity);
                    }

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
     * @param mcMMOPlayer The attacking player
     * @param target The defending entity
     * @param primarySkillType The skill being used
     */
    public static void processCombatXP(@NotNull McMMOPlayer mcMMOPlayer, @NotNull LivingEntity target, @NotNull PrimarySkillType primarySkillType) {
        processCombatXP(mcMMOPlayer, target, primarySkillType, 1.0);
    }

    /**
     * Start the task that gives combat XP.
     *
     * @param mcMMOPlayer The attacking player
     * @param target The defending entity
     * @param primarySkillType The skill being used
     * @param multiplier final XP result will be multiplied by this
     */
    public static void processCombatXP(@NotNull McMMOPlayer mcMMOPlayer,
                                       @NotNull LivingEntity target,
                                       @NotNull PrimarySkillType primarySkillType,
                                       double multiplier) {
        double baseXP = 0;
        XPGainReason xpGainReason;

        if (target instanceof Player defender) {
            if (!ExperienceConfig.getInstance().getExperienceGainsPlayerVersusPlayerEnabled() || PartyManager.inSameParty(mcMMOPlayer.getPlayer(), (Player) target)) {
                return;
            }

            xpGainReason = XPGainReason.PVP;

            if (defender.isOnline() && SkillUtils.cooldownExpired(mcMMOPlayer.getRespawnATS(), Misc.PLAYER_RESPAWN_COOLDOWN_SECONDS)) {
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

            if(getMobMetadataService().hasMobFlag(MobMetaFlagType.COTW_SUMMONED_MOB, target)) {
                baseXP = 0;
            } else if(getMobMetadataService().hasMobFlag(MobMetaFlagType.MOB_SPAWNER_MOB, target) || target.hasMetadata("ES")) {
                baseXP *= ExperienceConfig.getInstance().getSpawnedMobXpMultiplier();
            } else if(getMobMetadataService().hasMobFlag(MobMetaFlagType.NETHER_PORTAL_MOB, target)) {
                baseXP *= ExperienceConfig.getInstance().getNetherPortalXpMultiplier();
            } else if(getMobMetadataService().hasMobFlag(MobMetaFlagType.EGG_MOB, target)) {
                baseXP *= ExperienceConfig.getInstance().getEggXpMultiplier();
            } else if (getMobMetadataService().hasMobFlag(MobMetaFlagType.PLAYER_BRED_MOB, target)) {
                baseXP *= ExperienceConfig.getInstance().getBredMobXpMultiplier();
            } else if(getMobMetadataService().hasMobFlag(MobMetaFlagType.PLAYER_TAMED_MOB, target)) {
                baseXP *= ExperienceConfig.getInstance().getTamedMobXpMultiplier();
            }

            baseXP *= 10;
            xpGainReason = XPGainReason.PVE;
        }

        baseXP *= multiplier;

        if (baseXP != 0) {
            new AwardCombatXpTask(mcMMOPlayer, primarySkillType, baseXP, target, xpGainReason).runTaskLater(mcMMO.p, 0);
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
            if(UserManager.getPlayer(defender) == null)
                return true;

            if (!defender.getWorld().getPVP() || defender == player || UserManager.getPlayer(defender).getGodMode()) {
                return false;
            }

            if ((PartyManager.inSameParty(player, defender) || PartyManager.areAllies(player, defender)) && !(Permissions.friendlyFire(player) && Permissions.friendlyFire(defender))) {
                return false;
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

            if (tamer instanceof Player owner) {

                return (owner == attacker || PartyManager.inSameParty(attacker, owner) || PartyManager.areAllies(attacker, owner));
            }
        }

        return false;
    }

    public static boolean canDamage(@NotNull Entity attacker, @NotNull Entity target, @NotNull DamageCause damageCause, double damage) {
        EntityDamageEvent damageEvent = new FakeEntityDamageByEntityEvent(attacker, target, damageCause, damage);
        mcMMO.p.getServer().getPluginManager().callEvent(damageEvent);

        return !damageEvent.isCancelled();
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
        if (!(attacker instanceof Player player)) {
            return;
        }

        if (Misc.isNPCEntityExcludingVillagers(player) || Misc.isNPCEntityExcludingVillagers(target)) {
            return;
        }

        if (!player.hasMetadata(MetadataConstants.METADATA_KEY_PLAYER_DATA)) {
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
        if(entity.hasMetadata(MetadataConstants.METADATA_KEY_INF_ARROW)) {
            entity.removeMetadata(MetadataConstants.METADATA_KEY_INF_ARROW, mcMMO.p);
        }

        if(entity.hasMetadata(MetadataConstants.METADATA_KEY_BOW_FORCE)) {
            entity.removeMetadata(MetadataConstants.METADATA_KEY_BOW_FORCE, mcMMO.p);
        }

        if(entity.hasMetadata(MetadataConstants.METADATA_KEY_ARROW_DISTANCE)) {
            entity.removeMetadata(MetadataConstants.METADATA_KEY_ARROW_DISTANCE, mcMMO.p);
        }
    }

    /**
     * Clean up metadata from a projectile after a minute has passed
     *
     * @param entity the projectile
     */
    public static void delayArrowMetaCleanup(@NotNull Projectile entity) {
        Bukkit.getServer().getScheduler().runTaskLater(mcMMO.p, () -> cleanupArrowMetadata(entity), 20*60);
    }
}
