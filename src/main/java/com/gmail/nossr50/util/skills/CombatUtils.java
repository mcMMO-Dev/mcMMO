package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.meta.OldName;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.events.fake.FakeEntityDamageByEntityEvent;
import com.gmail.nossr50.events.fake.FakeEntityDamageEvent;
import com.gmail.nossr50.mcMMO;
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
import com.google.common.collect.ImmutableMap;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.ProjectileSource;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CombatUtils {
    private CombatUtils() {}

    private static void processSwordCombat(LivingEntity target, Player player, EntityDamageByEntityEvent event) {
        if (event.getCause() == DamageCause.THORNS) {
            return;
        }
        
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        SwordsManager swordsManager = mcMMOPlayer.getSwordsManager();
        double initialDamage = event.getDamage();
        double finalDamage = initialDamage;

        Map<DamageModifier, Double> modifiers = getModifiers(event);

        if (swordsManager.canActivateAbility()) {
            mcMMOPlayer.checkAbilityActivation(PrimarySkillType.SWORDS);
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
            finalDamage+=swordsManager.getStabDamage();
        }

        if (swordsManager.canUseSerratedStrike()) {
            swordsManager.serratedStrikes(target, initialDamage, modifiers);
        }

        if(canUseLimitBreak(player, target, SubSkillType.SWORDS_SWORDS_LIMIT_BREAK))
        {
            finalDamage+=getLimitBreakDamage(player, target, SubSkillType.SWORDS_SWORDS_LIMIT_BREAK);
        }

        applyScaledModifiers(initialDamage, finalDamage, event);
        startGainXp(mcMMOPlayer, target, PrimarySkillType.SWORDS);
    }

    private static void processAxeCombat(LivingEntity target, Player player, EntityDamageByEntityEvent event) {
        if (event.getCause() == DamageCause.THORNS) {
            return;
        }
        
        double initialDamage = event.getDamage();
        double finalDamage = initialDamage;
        Map<DamageModifier, Double> modifiers = getModifiers(event);

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        AxesManager axesManager = mcMMOPlayer.getAxesManager();

        if (axesManager.canActivateAbility()) {
            mcMMOPlayer.checkAbilityActivation(PrimarySkillType.AXES);
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
            finalDamage+=axesManager.criticalHit(target, finalDamage);
        }

        if(canUseLimitBreak(player, target, SubSkillType.AXES_AXES_LIMIT_BREAK))
        {
            finalDamage+=getLimitBreakDamage(player, target, SubSkillType.AXES_AXES_LIMIT_BREAK);
        }

        applyScaledModifiers(initialDamage, finalDamage, event);
        startGainXp(mcMMOPlayer, target, PrimarySkillType.AXES);
    }

    private static void processUnarmedCombat(LivingEntity target, Player player, EntityDamageByEntityEvent event) {
        if (event.getCause() == DamageCause.THORNS) {
            return;
        }

        double initialDamage = event.getDamage();
        double finalDamage = initialDamage;

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        UnarmedManager unarmedManager = mcMMOPlayer.getUnarmedManager();

        if (unarmedManager.canActivateAbility()) {
            mcMMOPlayer.checkAbilityActivation(PrimarySkillType.UNARMED);
        }

        if(unarmedManager.isPunchingCooldownOver())
        {
            //Only execute bonuses if the player is not spamming
            if (unarmedManager.canUseIronArm()) {
                finalDamage+=unarmedManager.ironArm();
            }

            if (unarmedManager.canUseBerserk()) {
                finalDamage+=unarmedManager.berserkDamage(finalDamage);
            }

            if (unarmedManager.canDisarm(target)) {
                unarmedManager.disarmCheck((Player) target);
            }

            if(canUseLimitBreak(player, target, SubSkillType.UNARMED_UNARMED_LIMIT_BREAK))
            {
                finalDamage+=getLimitBreakDamage(player, target, SubSkillType.UNARMED_UNARMED_LIMIT_BREAK);
            }
        }

        applyScaledModifiers(initialDamage, finalDamage, event);
        startGainXp(mcMMOPlayer, target, PrimarySkillType.UNARMED);
        unarmedManager.setLastAttacked(System.currentTimeMillis()); //Track how often the player is punching
    }

    private static void processTamingCombat(LivingEntity target, Player master, Wolf wolf, EntityDamageByEntityEvent event) {
        double initialDamage = event.getDamage();
        double finalDamage = initialDamage;

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
                finalDamage+=tamingManager.sharpenedClaws();
            }

            if (tamingManager.canUseGore()) {
                finalDamage+=tamingManager.gore(target, initialDamage);
            }

            applyScaledModifiers(initialDamage, finalDamage, event);
            startGainXp(mcMMOPlayer, target, PrimarySkillType.TAMING);
        }

    }

    private static void processArcheryCombat(LivingEntity target, Player player, EntityDamageByEntityEvent event, Arrow arrow) {
        double initialDamage = event.getDamage();

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        ArcheryManager archeryManager = mcMMOPlayer.getArcheryManager();
        
        double finalDamage = event.getDamage();

        if (target instanceof Player && PrimarySkillType.UNARMED.getPVPEnabled()) {
            UnarmedManager unarmedManager = UserManager.getPlayer((Player) target).getUnarmedManager();

            if (unarmedManager.canDeflect()) {
                event.setCancelled(unarmedManager.deflectCheck());

                if (event.isCancelled()) {
                    return;
                }
            }
        }

        if (archeryManager.canSkillShot()) {
            finalDamage+=archeryManager.skillShot(initialDamage);
        }

        if (archeryManager.canDaze(target)) {
            finalDamage+=archeryManager.daze((Player) target);
        }

        if (!arrow.hasMetadata(mcMMO.infiniteArrowKey) && archeryManager.canRetrieveArrows()) {
            archeryManager.retrieveArrows(target, arrow);
        }

        if(canUseLimitBreak(player, target, SubSkillType.ARCHERY_ARCHERY_LIMIT_BREAK))
        {
            finalDamage+=getLimitBreakDamage(player, target, SubSkillType.ARCHERY_ARCHERY_LIMIT_BREAK);
        }

        double distanceMultiplier = archeryManager.distanceXpBonusMultiplier(target, arrow);
        double forceMultiplier = 1.0; //Hacky Fix - some plugins spawn arrows and assign them to players after the ProjectileLaunchEvent fires

        if(arrow.hasMetadata(mcMMO.bowForceKey))
            forceMultiplier = arrow.getMetadata(mcMMO.bowForceKey).get(0).asDouble();

        applyScaledModifiers(initialDamage, finalDamage, event);
        startGainXp(mcMMOPlayer, target, PrimarySkillType.ARCHERY, forceMultiplier * distanceMultiplier);
    }

    /**
     * Apply combat modifiers and process and XP gain.
     *
     * @param event The event to run the combat checks on.
     */
    public static void processCombatAttack(EntityDamageByEntityEvent event, Entity attacker, LivingEntity target) {
        Entity damager = event.getDamager();
        EntityType entityType = damager.getType();

        if (target instanceof Player) {
            if (Misc.isNPCEntityExcludingVillagers(target)) {
                return;
            }

            Player player = (Player) target;
            if (!UserManager.hasPlayerDataKey(player)) {
                return;
            }

            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
            AcrobaticsManager acrobaticsManager = mcMMOPlayer.getAcrobaticsManager();

            if (acrobaticsManager.canDodge(target)) {
                event.setDamage(acrobaticsManager.dodgeCheck(attacker, event.getDamage()));
            }

            if (ItemUtils.isSword(player.getInventory().getItemInMainHand())) {
                if (!PrimarySkillType.SWORDS.shouldProcess(target)) {
                    return;
                }

                SwordsManager swordsManager = mcMMOPlayer.getSwordsManager();

                if (swordsManager.canUseCounterAttack(damager)) {
                    swordsManager.counterAttackChecks((LivingEntity) damager, event.getDamage());
                }
            }
        }

        if (attacker instanceof Player && entityType == EntityType.PLAYER) {
            Player player = (Player) attacker;

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
        }

        else if (entityType == EntityType.WOLF) {
            Wolf wolf = (Wolf) damager;
            AnimalTamer tamer = wolf.getOwner();

            if (tamer != null && tamer instanceof Player && PrimarySkillType.TAMING.shouldProcess(target)) {
                Player master = (Player) tamer;

                if (!Misc.isNPCEntityExcludingVillagers(master) && PrimarySkillType.TAMING.getPermissions(master)) {
                    processTamingCombat(target, master, wolf, event);
                }
            }
        }
        else if (entityType == EntityType.ARROW) {
            Arrow arrow = (Arrow) damager;
            ProjectileSource projectileSource = arrow.getShooter();

            if (projectileSource != null && projectileSource instanceof Player && PrimarySkillType.ARCHERY.shouldProcess(target)) {
                Player player = (Player) projectileSource;

                if (!Misc.isNPCEntityExcludingVillagers(player) && PrimarySkillType.ARCHERY.getPermissions(player)) {
                    processArcheryCombat(target, player, event, arrow);
                }

                if (target.getType() != EntityType.CREEPER && !Misc.isNPCEntityExcludingVillagers(player) && PrimarySkillType.TAMING.getPermissions(player)) {
                    McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
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
    public static void fixNames(LivingEntity entity)
    {
        List<MetadataValue> metadataValue = entity.getMetadata("mcMMO_oldName");

        if(metadataValue.size() <= 0)
            return;

        if(metadataValue != null)
        {
            OldName oldName = (OldName) metadataValue.get(0);
            entity.setCustomName(oldName.asString());
            entity.setCustomNameVisible(false);
        }
    }

    /**
     * Calculate and return the RAW damage bonus from Limit Break before reductions
     * @param attacker attacking player
     * @param defender defending living entity
     * @param subSkillType the specific limit break skill for calculations
     * @return the RAW damage bonus from Limit Break which is applied before reductions
     */
    public static int getLimitBreakDamage(Player attacker, LivingEntity defender, SubSkillType subSkillType) {
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
    public static int getLimitBreakDamageAgainstQuality(Player attacker, SubSkillType subSkillType, int armorQualityLevel) {
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
    public static int getArmorQualityLevel(Player defender) {
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
    private static int getArmorQuality(ItemStack itemStack) {
        int quality = 0;

        switch(itemStack.getType()) {
            case LEATHER_HELMET:
            case LEATHER_BOOTS:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
                return 1;
            case IRON_HELMET:
            case IRON_BOOTS:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
                return 2;
            case GOLDEN_HELMET:
            case GOLDEN_BOOTS:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_LEGGINGS:
                return 3;
            case DIAMOND_HELMET:
            case DIAMOND_BOOTS:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
                return 6;
            default:
                return 1;

        }
    }

    /**
     * Checks if player has access to their weapons limit break
     * @param player target entity
     * @return true if the player has access to the limit break
     */
    public static boolean canUseLimitBreak(Player player, LivingEntity target, SubSkillType subSkillType) {
        if(target instanceof Player || AdvancedConfig.getInstance().canApplyLimitBreakPVE()) {
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
    public static void dealDamage(LivingEntity target, double damage) {
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
    public static void dealDamage(LivingEntity target, double damage, LivingEntity attacker) {
        dealDamage(target, damage, DamageCause.CUSTOM, attacker);
    }

    /**
     * Attempt to damage target for value dmg with reason ENTITY_ATTACK with damager attacker
     *
     * @param target LivingEntity which to attempt to damage
     * @param damage Amount of damage to attempt to do
     * @param attacker Player to pass to event as damager
     */
    public static void dealDamage(LivingEntity target, double damage, Map<DamageModifier, Double> modifiers, LivingEntity attacker) {
        if (target.isDead()) {
            return;
        }

        // Aren't we applying the damage twice????
        target.damage(getFakeDamageFinalResult(attacker, target, damage, modifiers));
    }

    /**
     * Attempt to damage target for value dmg with reason ENTITY_ATTACK with damager attacker
     *
     * @param target LivingEntity which to attempt to damage
     * @param damage Amount of damage to attempt to do
     * @param attacker Player to pass to event as damager
     */
    @Deprecated
    public static void dealDamage(LivingEntity target, double damage, DamageCause cause, Entity attacker) {
        if (target.isDead()) {
            return;
        }

        if(canDamage(attacker, target, cause, damage))
            target.damage(damage);
    }

    public static void dealNoInvulnerabilityTickDamage(LivingEntity target, double damage, Entity attacker) {
        if (target.isDead()) {
            return;
        }

        // TODO: This is horrible, but there is no cleaner way to do this without potentially breaking existing code right now
        // calling damage here is a double edged sword: On one hand, without a call, plugins won't see this properly when the entity dies,
        // potentially mis-attributing the death cause; calling a fake event would partially fix this, but this and setting the last damage
        // cause do have issues around plugin observability. This is not a perfect solution, but it appears to be the best one here
        // We also set no damage ticks to 0, to ensure that damage is applied for this case, and reset it back to the original value
        boolean wasMetaSet = target.getMetadata(mcMMO.CUSTOM_DAMAGE_METAKEY).size() != 0;
        target.setMetadata(mcMMO.CUSTOM_DAMAGE_METAKEY, mcMMO.metadataValue);
        int noDamageTicks = target.getNoDamageTicks();
        target.setNoDamageTicks(0);
        target.damage(damage, attacker);
        target.setNoDamageTicks(noDamageTicks);
        if (!wasMetaSet)
            target.removeMetadata(mcMMO.CUSTOM_DAMAGE_METAKEY, mcMMO.p);
    }

    public static void dealNoInvulnerabilityTickDamageRupture(LivingEntity target, double damage, Entity attacker, int toolTier) {
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
     *
     * @param attacker The attacking player
     * @param target The defending entity
     * @param damage The initial damage amount
     * @param type The type of skill being used
     */
    public static void applyAbilityAoE(Player attacker, LivingEntity target, double damage, Map<DamageModifier, Double> modifiers, PrimarySkillType type) {
        int numberOfTargets = getTier(attacker.getInventory().getItemInMainHand()); // The higher the weapon tier, the more targets you hit
        double damageAmount = Math.max(damage, 1);

        for (Entity entity : target.getNearbyEntities(2.5, 2.5, 2.5)) {
            if (numberOfTargets <= 0) {
                break;
            }

            if (Misc.isNPCEntityExcludingVillagers(entity) || !(entity instanceof LivingEntity) || !shouldBeAffected(attacker, entity)) {
                continue;
            }

            LivingEntity livingEntity = (LivingEntity) entity;
            EventUtils.callFakeArmSwingEvent(attacker);

            switch (type) {
                case SWORDS:
                    if (entity instanceof Player) {
                        NotificationManager.sendPlayerInformation((Player)entity, NotificationType.SUBSKILL_MESSAGE, "Swords.Combat.SS.Struck");
                    }

                    UserManager.getPlayer(attacker).getSwordsManager().ruptureCheck(target);
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

    public static void startGainXp(McMMOPlayer mcMMOPlayer, LivingEntity target, PrimarySkillType primarySkillType) {
        startGainXp(mcMMOPlayer, target, primarySkillType, 1.0);
    }

    /**
     * Start the task that gives combat XP.
     *
     * @param mcMMOPlayer The attacking player
     * @param target The defending entity
     * @param primarySkillType The skill being used
     */
    private static void startGainXp(McMMOPlayer mcMMOPlayer, LivingEntity target, PrimarySkillType primarySkillType, double multiplier) {
        double baseXP = 0;
        XPGainReason xpGainReason;

        if (target instanceof Player) {
            if (!ExperienceConfig.getInstance().getExperienceGainsPlayerVersusPlayerEnabled() || PartyManager.inSameParty(mcMMOPlayer.getPlayer(), (Player) target)) {
                return;
            }

            xpGainReason = XPGainReason.PVP;
            Player defender = (Player) target;

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

            if (target.hasMetadata(mcMMO.entityMetadataKey)
                    //Epic Spawners compatibility
                    || target.hasMetadata("ES")) {
                baseXP *= ExperienceConfig.getInstance().getSpawnedMobXpMultiplier();
            }

            if (target.hasMetadata(mcMMO.bredMetadataKey)) {
                baseXP *= ExperienceConfig.getInstance().getBredMobXpMultiplier();
            }

            xpGainReason = XPGainReason.PVE;

            baseXP *= 10;
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
    private static boolean shouldBeAffected(Player player, Entity entity) {
        if (entity instanceof Player) {
            Player defender = (Player) entity;

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
            if (defender.getGameMode() == GameMode.SPECTATOR) {
                return false;
            }

            // It may seem a bit redundant but we need a check here to prevent bleed from being applied in applyAbilityAoE()
            return getFakeDamageFinalResult(player, entity, 1.0) != 0;
        }
        else if (entity instanceof Tameable) {
            if (isFriendlyPet(player, (Tameable) entity)) {
                // isFriendlyPet ensures that the Tameable is: Tamed, owned by a player, and the owner is in the same party
                // So we can make some assumptions here, about our casting and our check
                Player owner = (Player) ((Tameable) entity).getOwner();
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
    public static boolean isInvincible(LivingEntity entity, double eventDamage) {
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
    public static boolean isFriendlyPet(Player attacker, Tameable pet) {
        if (pet.isTamed()) {
            AnimalTamer tamer = pet.getOwner();

            if (tamer instanceof Player) {
                Player owner = (Player) tamer;

                return (owner == attacker || PartyManager.inSameParty(attacker, owner) || PartyManager.areAllies(attacker, owner));
            }
        }

        return false;
    }

    @Deprecated
    public static double getFakeDamageFinalResult(Entity attacker, Entity target, double damage) {
        return getFakeDamageFinalResult(attacker, target, DamageCause.ENTITY_ATTACK, new EnumMap<DamageModifier, Double>(ImmutableMap.of(DamageModifier.BASE, damage)));
    }

    @Deprecated
    public static double getFakeDamageFinalResult(Entity attacker, Entity target, DamageCause damageCause, double damage) {
        EntityDamageEvent damageEvent = sendEntityDamageEvent(attacker, target, damageCause, damage);

        if (damageEvent.isCancelled()) {
            return 0;
        }

        return damageEvent.getFinalDamage();
    }

    public static boolean canDamage(Entity attacker, Entity target, DamageCause damageCause, double damage) {
        EntityDamageEvent damageEvent = sendEntityDamageEvent(attacker, target, damageCause, damage);

        return !damageEvent.isCancelled();
    }

    public static EntityDamageEvent sendEntityDamageEvent(Entity attacker, Entity target, DamageCause damageCause, double damage) {
        EntityDamageEvent damageEvent = attacker == null ? new FakeEntityDamageEvent(target, damageCause, damage) : new FakeEntityDamageByEntityEvent(attacker, target, damageCause, damage);
        mcMMO.p.getServer().getPluginManager().callEvent(damageEvent);
        return damageEvent;
    }

    public static double getFakeDamageFinalResult(Entity attacker, Entity target, Map<DamageModifier, Double> modifiers) {
        return getFakeDamageFinalResult(attacker, target, DamageCause.ENTITY_ATTACK, modifiers);
    }

    public static double getFakeDamageFinalResult(Entity attacker, Entity target, double damage, Map<DamageModifier, Double> modifiers) {
        return getFakeDamageFinalResult(attacker, target, DamageCause.ENTITY_ATTACK, getScaledModifiers(damage, modifiers));
    }

    public static double getFakeDamageFinalResult(Entity attacker, Entity target, DamageCause cause, Map<DamageModifier, Double> modifiers) {
        EntityDamageEvent damageEvent = attacker == null ? new FakeEntityDamageEvent(target, cause, modifiers) : new FakeEntityDamageByEntityEvent(attacker, target, cause, modifiers);
        mcMMO.p.getServer().getPluginManager().callEvent(damageEvent);

        if (damageEvent.isCancelled()) {
            return 0;
        }

        return damageEvent.getFinalDamage();
    }

    private static Map<DamageModifier, Double> getModifiers(EntityDamageEvent event) {
        Map<DamageModifier, Double> modifiers = new HashMap<DamageModifier, Double>();
        for (DamageModifier modifier : DamageModifier.values()) {
            modifiers.put(modifier, event.getDamage(modifier));
        }

        return modifiers;
    }

    private static Map<DamageModifier, Double> getScaledModifiers(double damage, Map<DamageModifier, Double> modifiers) {
        Map<DamageModifier, Double> scaledModifiers = new HashMap<DamageModifier, Double>();

        for (DamageModifier modifier : modifiers.keySet()) {
            if (modifier == DamageModifier.BASE) {
                scaledModifiers.put(modifier, damage);
                continue;
            }

            scaledModifiers.put(modifier, damage * modifiers.get(modifier));
        }

        return scaledModifiers;
    }

    public static EntityDamageByEntityEvent applyScaledModifiers(double initialDamage, double finalDamage, EntityDamageByEntityEvent event) {
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
    private static int getTier(ItemStack inHand) {
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
        }
        else if (mcMMO.getModManager().isCustomTool(inHand)) {
            tier = mcMMO.getModManager().getTool(inHand).getTier();
        }

        return tier;
    }

    public static void handleHealthbars(Entity attacker, LivingEntity target, double damage, mcMMO plugin) {
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
}
