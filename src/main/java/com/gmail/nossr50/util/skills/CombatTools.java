package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.datatypes.experience.SpecialXPKey;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.meta.OldName;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.events.fake.FakeEntityDamageByEntityEvent;
import com.gmail.nossr50.events.fake.FakeEntityDamageEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.skills.AwardCombatXpTask;
import com.gmail.nossr50.skills.acrobatics.AcrobaticsManager;
import com.gmail.nossr50.skills.archery.ArcheryManager;
import com.gmail.nossr50.skills.axes.AxesManager;
import com.gmail.nossr50.skills.swords.SwordsManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;
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

public final class CombatTools {
    private final mcMMO pluginRef;
    
    public CombatTools(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    private void processSwordCombat(LivingEntity target, Player player, EntityDamageByEntityEvent event) {
        if (event.getCause() == DamageCause.THORNS) {
            return;
        }

        McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);
        SwordsManager swordsManager = mcMMOPlayer.getSwordsManager();
        double initialDamage = event.getDamage();
        double finalDamage = initialDamage;

        Map<DamageModifier, Double> modifiers = getModifiers(event);

        if (swordsManager.canActivateAbility()) {
            mcMMOPlayer.checkAbilityActivation(PrimarySkillType.SWORDS);
        }

        if (target.getHealth() - event.getFinalDamage() >= 1) {
            if (swordsManager.canUseRupture()) {
                swordsManager.ruptureCheck(target);
            }
        }

        //Add Stab Damage
        if (swordsManager.canUseStab()) {
            finalDamage += swordsManager.getStabDamage();
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

    private void processAxeCombat(LivingEntity target, Player player, EntityDamageByEntityEvent event) {
        if (event.getCause() == DamageCause.THORNS) {
            return;
        }

        double initialDamage = event.getDamage();
        double finalDamage = initialDamage;
        Map<DamageModifier, Double> modifiers = getModifiers(event);

        McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);
        AxesManager axesManager = mcMMOPlayer.getAxesManager();

        if (axesManager.canActivateAbility()) {
            mcMMOPlayer.checkAbilityActivation(PrimarySkillType.AXES);
        }

        if (axesManager.canUseAxeMastery()) {
            finalDamage += axesManager.axeMastery();
        }

        if (axesManager.canImpact(target)) {
            axesManager.impactCheck(target);
        } else if (axesManager.canGreaterImpact(target)) {
            finalDamage += axesManager.greaterImpact(target);
        }

        if (axesManager.canUseSkullSplitter(target)) {
            axesManager.skullSplitterCheck(target, initialDamage, modifiers);
        }

        if (axesManager.canCriticalHit(target)) {
            finalDamage += axesManager.criticalHit(target, finalDamage);
        }

        if(canUseLimitBreak(player, target, SubSkillType.AXES_AXES_LIMIT_BREAK))
        {
            finalDamage+=getLimitBreakDamage(player, target, SubSkillType.AXES_AXES_LIMIT_BREAK);
        }

        applyScaledModifiers(initialDamage, finalDamage, event);
        startGainXp(mcMMOPlayer, target, PrimarySkillType.AXES);
    }

    private void processUnarmedCombat(LivingEntity target, Player player, EntityDamageByEntityEvent event) {
        if (event.getCause() == DamageCause.THORNS) {
            return;
        }

        double initialDamage = event.getDamage();
        double finalDamage = initialDamage;

        McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);
        UnarmedManager unarmedManager = mcMMOPlayer.getUnarmedManager();

        if (unarmedManager.canActivateAbility()) {
            mcMMOPlayer.checkAbilityActivation(PrimarySkillType.UNARMED);
        }

        if (unarmedManager.isPunchingCooldownOver()) {
            //Only execute bonuses if the player is not spamming
            if (unarmedManager.canUseIronArm()) {
                finalDamage += unarmedManager.ironArm();
            }

            if (unarmedManager.canUseBerserk()) {
                finalDamage += unarmedManager.berserkDamage(finalDamage);
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

    private void processTamingCombat(LivingEntity target, Player master, Wolf wolf, EntityDamageByEntityEvent event) {
        double initialDamage = event.getDamage();
        double finalDamage = initialDamage;

        if(master != null && !master.isOnline() && master.isValid()) {
            McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(master);

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
                if(target instanceof Player) {
                    finalDamage+=tamingManager.sharpenedClaws(false);
                } else {
                    finalDamage+=tamingManager.sharpenedClaws(true);
                }

            }

            if (tamingManager.canUseGore()) {
                finalDamage+=tamingManager.gore(target, initialDamage);
            }

            applyScaledModifiers(initialDamage, finalDamage, event);
            startGainXp(mcMMOPlayer, target, PrimarySkillType.TAMING);
        }

    }

    private void processArcheryCombat(LivingEntity target, Player player, EntityDamageByEntityEvent event, Arrow arrow) {
        double initialDamage = event.getDamage();

        McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);
        ArcheryManager archeryManager = mcMMOPlayer.getArcheryManager();

        double finalDamage = event.getDamage();

        if (target instanceof Player
                && pluginRef.getSkillTools().getPVPEnabled(PrimarySkillType.UNARMED)) {
            UnarmedManager unarmedManager = pluginRef.getUserManager().getPlayer((Player) target).getUnarmedManager();

            if (unarmedManager.canDeflect()) {
                event.setCancelled(unarmedManager.deflectCheck());

                if (event.isCancelled()) {
                    return;
                }
            }
        }

        if (archeryManager.canSkillShot()) {
            finalDamage += archeryManager.skillShot(initialDamage);
        }

        if (archeryManager.canDaze(target)) {
            finalDamage += archeryManager.daze((Player) target);
        }

        if (!arrow.hasMetadata(MetadataConstants.INFINITE_ARROW_METAKEY) && archeryManager.canRetrieveArrows()) {
            archeryManager.processArrowRetrievalActivation(target, arrow);
        }

        if(canUseLimitBreak(player, target, SubSkillType.ARCHERY_ARCHERY_LIMIT_BREAK))
        {
            finalDamage+=getLimitBreakDamage(player, target, SubSkillType.ARCHERY_ARCHERY_LIMIT_BREAK);
        }

        double distanceMultiplier = archeryManager.distanceXpBonusMultiplier(target, arrow);
        double forceMultiplier = 1.0; //Hacky Fix - some plugins spawn arrows and assign them to players after the ProjectileLaunchEvent fires

        if(arrow.hasMetadata(MetadataConstants.BOW_FORCE_METAKEY))
            forceMultiplier = arrow.getMetadata(MetadataConstants.BOW_FORCE_METAKEY).get(0).asDouble();

        applyScaledModifiers(initialDamage, finalDamage, event);
        startGainXp(mcMMOPlayer, target, PrimarySkillType.ARCHERY, forceMultiplier * distanceMultiplier);
    }

    /**
     * Apply combat modifiers and process and XP gain.
     *
     * @param event The event to run the combat checks on.
     */
    public void processCombatAttack(EntityDamageByEntityEvent event, Entity attacker, LivingEntity target) {
        Entity damager = event.getDamager();
        EntityType entityType = damager.getType();

        if (target instanceof Player) {
            if (pluginRef.getMiscTools().isNPCEntityExcludingVillagers(target)) {
                return;
            }

            Player player = (Player) target;
            if (!pluginRef.getUserManager().hasPlayerDataKey(player)) {
                return;
            }
            McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);
            AcrobaticsManager acrobaticsManager = mcMMOPlayer.getAcrobaticsManager();

            if (acrobaticsManager.canDodge(target)) {
                event.setDamage(acrobaticsManager.dodgeCheck(event.getDamage()));
            }

            if (pluginRef.getItemTools().isSword(player.getInventory().getItemInMainHand())) {
                if (!pluginRef.getSkillTools().canCombatSkillsTrigger(PrimarySkillType.SWORDS, target)) {
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

            if (!pluginRef.getUserManager().hasPlayerDataKey(player)) {
                return;
            }

            ItemStack heldItem = player.getInventory().getItemInMainHand();

            if (target instanceof Tameable) {
                if (heldItem.getType() == Material.BONE) {
                    TamingManager tamingManager = pluginRef.getUserManager().getPlayer(player).getTamingManager();

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

            if (pluginRef.getItemTools().isSword(heldItem)) {
                if (!pluginRef.getSkillTools().canCombatSkillsTrigger(PrimarySkillType.SWORDS, target)) {
                    return;
                }

                if (pluginRef.getSkillTools().doesPlayerHaveSkillPermission(PrimarySkillType.SWORDS, player)) {
                    processSwordCombat(target, player, event);
                }
            } else if (pluginRef.getItemTools().isAxe(heldItem)) {
                if (!pluginRef.getSkillTools().canCombatSkillsTrigger(PrimarySkillType.AXES, target)) {
                    return;
                }

                if (pluginRef.getSkillTools().doesPlayerHaveSkillPermission(PrimarySkillType.AXES, player)) {
                    processAxeCombat(target, player, event);
                }
            } else if (pluginRef.getItemTools().isUnarmed(heldItem)) {
                if (!pluginRef.getSkillTools().canCombatSkillsTrigger(PrimarySkillType.UNARMED, target)) {
                    return;
                }

                if (pluginRef.getSkillTools().doesPlayerHaveSkillPermission(PrimarySkillType.UNARMED, player)) {
                    processUnarmedCombat(target, player, event);
                }
            }
        } else if (entityType == EntityType.WOLF) {
            Wolf wolf = (Wolf) damager;
            AnimalTamer tamer = wolf.getOwner();

            if (tamer instanceof Player && pluginRef.getSkillTools().canCombatSkillsTrigger(PrimarySkillType.TAMING, target)) {
                Player master = (Player) tamer;

                if (!pluginRef.getMiscTools().isNPCEntityExcludingVillagers(master) && pluginRef.getSkillTools().doesPlayerHaveSkillPermission(PrimarySkillType.TAMING, master)) {
                    processTamingCombat(target, master, wolf, event);
                }
            }
        } else if (entityType == EntityType.ARROW) {
            Arrow arrow = (Arrow) damager;
            ProjectileSource projectileSource = arrow.getShooter();

            if (projectileSource instanceof Player && pluginRef.getSkillTools().canCombatSkillsTrigger(PrimarySkillType.ARCHERY, target)) {
                Player player = (Player) projectileSource;

                if (!pluginRef.getMiscTools().isNPCEntityExcludingVillagers(player) && pluginRef.getSkillTools().doesPlayerHaveSkillPermission(PrimarySkillType.ARCHERY, player)) {
                    processArcheryCombat(target, player, event, arrow);
                }

                if (target.getType() != EntityType.CREEPER && !pluginRef.getMiscTools().isNPCEntityExcludingVillagers(player) && pluginRef.getSkillTools().doesPlayerHaveSkillPermission(PrimarySkillType.TAMING, player)) {
                    McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);
                    TamingManager tamingManager = mcMMOPlayer.getTamingManager();
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
    public void fixNames(LivingEntity entity) {
        List<MetadataValue> metadataValue = entity.getMetadata("mcMMO_oldName");

        if (metadataValue.size() <= 0)
            return;

        if (metadataValue != null) {
            OldName oldName = (OldName) metadataValue.get(0);
            entity.setCustomName(oldName.asString());
            entity.setCustomNameVisible(false);
        }
    }

    public int getLimitBreakDamage(Player player, LivingEntity defender, SubSkillType subSkillType) {
        if(defender instanceof Player) {
            Player playerDefender = (Player) defender;
            return getLimitBreakDamageAgainstQuality(player, subSkillType, getArmorQualityLevel(playerDefender));
        } else {
            return getLimitBreakDamageAgainstQuality(player, subSkillType, 1000);
        }
    }

    public int getLimitBreakDamageAgainstQuality(Player player, SubSkillType subSkillType, int armorQualityLevel) {
        int rawDamageBoost = pluginRef.getRankTools().getRank(player, subSkillType);

        if(armorQualityLevel <= 4) {
            rawDamageBoost *= .25; //75% Nerf
        } else if(armorQualityLevel <= 8) {
            rawDamageBoost *= .50; //50% Nerf
        } else if(armorQualityLevel <= 12) {
            rawDamageBoost *= .75; //25% Nerf
        }

        return rawDamageBoost;
    }

    public int getArmorQualityLevel(Player defender) {
        int armorQualityLevel = 0;

        for(ItemStack itemStack : defender.getInventory().getArmorContents()) {
            if(itemStack != null) {
                armorQualityLevel += getArmorQuality(itemStack);
            }
        }

        return armorQualityLevel;
    }

    private int getArmorQuality(ItemStack itemStack) {
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
     *
     * @param player target entity
     * @return true if the player has access to the limit break
     */
    public boolean canUseLimitBreak(Player player, LivingEntity target, SubSkillType subSkillType) {
        if(target instanceof Player || AdvancedConfig.getInstance().canApplyLimitBreakPVE()) {
            return pluginRef.getRankTools().hasUnlockedSubskill(player, subSkillType)
                    && pluginRef.getPermissionTools().isSubSkillEnabled(player, subSkillType);
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
    public void dealDamage(LivingEntity target, double damage) {
        dealDamage(target, damage, DamageCause.CUSTOM, null);
    }

    /**
     * Attempt to damage target for value dmg with reason ENTITY_ATTACK with damager attacker
     *
     * @param target   LivingEntity which to attempt to damage
     * @param damage   Amount of damage to attempt to do
     * @param attacker Player to pass to event as damager
     */
    @Deprecated
    public void dealDamage(LivingEntity target, double damage, LivingEntity attacker) {
        dealDamage(target, damage, DamageCause.CUSTOM, attacker);
    }

    /**
     * Attempt to damage target for value dmg with reason ENTITY_ATTACK with damager attacker
     *
     * @param target   LivingEntity which to attempt to damage
     * @param damage   Amount of damage to attempt to do
     * @param attacker Player to pass to event as damager
     */
    public void dealDamage(LivingEntity target, double damage, Map<DamageModifier, Double> modifiers, LivingEntity attacker) {
        if (target.isDead()) {
            return;
        }

        // Aren't we applying the damage twice????
        target.damage(getFakeDamageFinalResult(attacker, target, damage, modifiers));
    }

    /**
     * Attempt to damage target for value dmg with reason ENTITY_ATTACK with damager attacker
     *
     * @param target   LivingEntity which to attempt to damage
     * @param damage   Amount of damage to attempt to do
     * @param attacker Player to pass to event as damager
     */
    @Deprecated
    public void dealDamage(LivingEntity target, double damage, DamageCause cause, Entity attacker) {
        if (target.isDead()) {
            return;
        }

        if (canDamage(attacker, target, cause, damage))
            target.damage(damage);
    }

    public void dealNoInvulnerabilityTickDamage(LivingEntity target, double damage, Entity attacker) {
        if (target.isDead()) {
            return;
        }

        double incDmg = getFakeDamageFinalResult(attacker, target, DamageCause.ENTITY_ATTACK, damage);

        double newHealth = Math.max(0, target.getHealth() - incDmg);

        if (newHealth == 0) {
            target.damage(9999, attacker);
        } else
            target.setHealth(newHealth);
    }

    public void dealNoInvulnerabilityTickDamageRupture(LivingEntity target, double damage, Entity attacker, int toolTier) {
        if (target.isDead()) {
            return;
        }

        target.setMetadata(MetadataConstants.CUSTOM_DAMAGE_METAKEY, MetadataConstants.metadataValue);
        target.damage(damage, attacker);
    }

    /**
     * Apply Area-of-Effect ability actions.
     *
     * @param attacker The attacking player
     * @param target   The defending entity
     * @param damage   The initial damage amount
     * @param type     The type of skill being used
     */
    public void applyAbilityAoE(Player attacker, LivingEntity target, double damage, Map<DamageModifier, Double> modifiers, PrimarySkillType type) {
        int numberOfTargets = getTier(attacker.getInventory().getItemInMainHand()); // The higher the weapon tier, the more targets you hit
        double damageAmount = Math.max(damage, 1);

        for (Entity entity : target.getNearbyEntities(2.5, 2.5, 2.5)) {
            if (numberOfTargets <= 0) {
                break;
            }

            if (pluginRef.getMiscTools().isNPCEntityExcludingVillagers(entity) || !(entity instanceof LivingEntity) || !shouldBeAffected(attacker, entity)) {
                continue;
            }

            LivingEntity livingEntity = (LivingEntity) entity;
            pluginRef.getEventManager().callFakeArmSwingEvent(attacker);

            switch (type) {
                case SWORDS:
                    if (entity instanceof Player) {
                        pluginRef.getNotificationManager().sendPlayerInformation((Player) entity, NotificationType.SUBSKILL_MESSAGE, "Swords.Combat.SS.Struck");
                    }

                    pluginRef.getUserManager().getPlayer(attacker).getSwordsManager().ruptureCheck(target);
                    break;

                case AXES:
                    if (entity instanceof Player) {
                        pluginRef.getNotificationManager().sendPlayerInformation((Player) entity, NotificationType.SUBSKILL_MESSAGE, "Axes.Combat.SS.Struck");
                    }

                    break;

                default:
                    break;
            }

            dealDamage(livingEntity, damageAmount, attacker);
            numberOfTargets--;
        }
    }

    public void startGainXp(McMMOPlayer mcMMOPlayer, LivingEntity target, PrimarySkillType primarySkillType) {
        startGainXp(mcMMOPlayer, target, primarySkillType, 1.0);
    }

    /**
     * Start the task that gives combat XP.
     *
     * @param mcMMOPlayer      The attacking player
     * @param target           The defending entity
     * @param primarySkillType The skill being used
     */
    private void startGainXp(McMMOPlayer mcMMOPlayer, LivingEntity target, PrimarySkillType primarySkillType, double multiplier) {
        double baseXPMultiplier = 0;
        XPGainReason xpGainReason;

        if (target instanceof Player) {
            if (!pluginRef.getConfigManager().getConfigExperience().isPvpXPEnabled() || pluginRef.getPartyManager().inSameParty(mcMMOPlayer.getPlayer(), (Player) target)) {
                return;
            }

            xpGainReason = XPGainReason.PVP;
            Player defender = (Player) target;

            if (defender.isOnline() && pluginRef.getSkillTools().cooldownExpired(mcMMOPlayer.getRespawnATS(), pluginRef.getMiscTools().PLAYER_RESPAWN_COOLDOWN_SECONDS)) {
                baseXPMultiplier = 20 * pluginRef.getDynamicSettingsManager().getExperienceManager().getSpecialCombatXP(SpecialXPKey.PVP);
            }
        } else {
            /*if (mcMMO.getModManager().isCustomEntity(target)) {
                baseXP = mcMMO.getModManager().getEntity(target).getXpMultiplier();
            }*/
            //else if (target instanceof Animals) {
            if (target instanceof Animals) {
                baseXPMultiplier = pluginRef.getDynamicSettingsManager().getExperienceManager().getSpecialCombatXP(SpecialXPKey.ANIMALS);
            } else if (target instanceof Monster) {
                EntityType type = target.getType();
                baseXPMultiplier = pluginRef.getDynamicSettingsManager().getExperienceManager().getCombatXPMultiplier(type);
            } else {
                EntityType type = target.getType();

                if (pluginRef.getDynamicSettingsManager().getExperienceManager().hasCombatXP(type)) {
                    //Exploit stuff
                    if (type == EntityType.IRON_GOLEM) {
                        if (!((IronGolem) target).isPlayerCreated()) {
                            baseXPMultiplier = pluginRef.getDynamicSettingsManager().getExperienceManager().getCombatXPMultiplier(type);
                        }
                    } else {
                        baseXPMultiplier = pluginRef.getDynamicSettingsManager().getExperienceManager().getCombatXPMultiplier(type);
                    }
                } else {
                    baseXPMultiplier = 1.0f;
                }
            }

            if (target.hasMetadata(MetadataConstants.UNNATURAL_MOB_METAKEY) || target.hasMetadata("ES")) {
                baseXPMultiplier *= pluginRef.getDynamicSettingsManager().getExperienceManager().getSpecialCombatXP(SpecialXPKey.SPAWNED);
            }

            if (target.hasMetadata(MetadataConstants.PETS_ANIMAL_TRACKING_METAKEY)) {
                baseXPMultiplier *= pluginRef.getDynamicSettingsManager().getExperienceManager().getSpecialCombatXP(SpecialXPKey.PETS);
            }

            xpGainReason = XPGainReason.PVE;

            baseXPMultiplier *= 10;
        }

        baseXPMultiplier *= multiplier;

        if (baseXPMultiplier != 0) {
            new AwardCombatXpTask(mcMMOPlayer, primarySkillType, baseXPMultiplier, target, xpGainReason).runTaskLater(pluginRef, 0);
        }
    }

    /**
     * Check to see if the given LivingEntity should be affected by a combat ability.
     *
     * @param player The attacking Player
     * @param entity The defending Entity
     * @return true if the Entity should be damaged, false otherwise.
     */
    private boolean shouldBeAffected(Player player, Entity entity) {
        if (entity instanceof Player) {
            Player defender = (Player) entity;

            //TODO: NPC Interaction?
            if (pluginRef.getUserManager().getPlayer(defender) == null)
                return true;

            if (!defender.getWorld().getPVP() || defender == player || pluginRef.getUserManager().getPlayer(defender).getGodMode()) {
                return false;
            }

            if ((pluginRef.getPartyManager().inSameParty(player, defender) || pluginRef.getPartyManager().areAllies(player, defender)) && !(pluginRef.getPermissionTools().friendlyFire(player) && pluginRef.getPermissionTools().friendlyFire(defender))) {
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
            if (getFakeDamageFinalResult(player, entity, 1.0) == 0) {
                return false;
            }
        } else if (entity instanceof Tameable) {
            if (isFriendlyPet(player, (Tameable) entity)) {
                // isFriendlyPet ensures that the Tameable is: Tamed, owned by a player, and the owner is in the same party
                // So we can make some assumptions here, about our casting and our check
                Player owner = (Player) ((Tameable) entity).getOwner();
                if (!(pluginRef.getPermissionTools().friendlyFire(player) && pluginRef.getPermissionTools().friendlyFire(owner))) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks to see if an entity is currently invincible.
     *
     * @param entity      The {@link LivingEntity} to check
     * @param eventDamage The damage from the event the entity is involved in
     * @return true if the entity is invincible, false otherwise
     */
    public boolean isInvincible(LivingEntity entity, double eventDamage) {
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
     * @param pet      The entity to check.
     * @return true if the entity is friendly, false otherwise
     */
    public boolean isFriendlyPet(Player attacker, Tameable pet) {
        if (pet.isTamed()) {
            AnimalTamer tamer = pet.getOwner();

            if (tamer instanceof Player) {
                Player owner = (Player) tamer;

                return (owner == attacker || pluginRef.getPartyManager().inSameParty(attacker, owner) || pluginRef.getPartyManager().areAllies(attacker, owner));
            }
        }

        return false;
    }

    @Deprecated
    public double getFakeDamageFinalResult(Entity attacker, Entity target, double damage) {
        return getFakeDamageFinalResult(attacker, target, DamageCause.ENTITY_ATTACK, new EnumMap<>(ImmutableMap.of(DamageModifier.BASE, damage)));
    }

    @Deprecated
    public double getFakeDamageFinalResult(Entity attacker, Entity target, DamageCause damageCause, double damage) {
        EntityDamageEvent damageEvent = sendEntityDamageEvent(attacker, target, damageCause, damage);

        if (damageEvent.isCancelled()) {
            return 0;
        }

        return damageEvent.getFinalDamage();
    }

    public boolean canDamage(Entity attacker, Entity target, DamageCause damageCause, double damage) {
        EntityDamageEvent damageEvent = sendEntityDamageEvent(attacker, target, damageCause, damage);

        if (damageEvent.isCancelled()) {
            return false;
        }

        return true;
    }

    public EntityDamageEvent sendEntityDamageEvent(Entity attacker, Entity target, DamageCause damageCause, double damage) {
        EntityDamageEvent damageEvent = attacker == null ? new FakeEntityDamageEvent(target, damageCause, damage) : new FakeEntityDamageByEntityEvent(attacker, target, damageCause, damage);
        pluginRef.getServer().getPluginManager().callEvent(damageEvent);
        return damageEvent;
    }

    public double getFakeDamageFinalResult(Entity attacker, Entity target, Map<DamageModifier, Double> modifiers) {
        return getFakeDamageFinalResult(attacker, target, DamageCause.ENTITY_ATTACK, modifiers);
    }

    public double getFakeDamageFinalResult(Entity attacker, Entity target, double damage, Map<DamageModifier, Double> modifiers) {
        return getFakeDamageFinalResult(attacker, target, DamageCause.ENTITY_ATTACK, getScaledModifiers(damage, modifiers));
    }

    public double getFakeDamageFinalResult(Entity attacker, Entity target, DamageCause cause, Map<DamageModifier, Double> modifiers) {
        EntityDamageEvent damageEvent = attacker == null ? new FakeEntityDamageEvent(target, cause, modifiers) : new FakeEntityDamageByEntityEvent(attacker, target, cause, modifiers);
        pluginRef.getServer().getPluginManager().callEvent(damageEvent);

        if (damageEvent.isCancelled()) {
            return 0;
        }

        return damageEvent.getFinalDamage();
    }

    private Map<DamageModifier, Double> getModifiers(EntityDamageEvent event) {
        Map<DamageModifier, Double> modifiers = new HashMap<>();
        for (DamageModifier modifier : DamageModifier.values()) {
            modifiers.put(modifier, event.getDamage(modifier));
        }

        return modifiers;
    }

    private Map<DamageModifier, Double> getScaledModifiers(double damage, Map<DamageModifier, Double> modifiers) {
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

    public void applyScaledModifiers(double initialDamage, double finalDamage, EntityDamageByEntityEvent event) {
        // No additional damage
        if (initialDamage == finalDamage) {
            return;
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

    }

    /**
     * Get the upgrade tier of the item in hand.
     *
     * @param inHand The item to check the tier of
     * @return the tier of the item
     */
    private int getTier(ItemStack inHand) {
        int tier = 0;

        if (pluginRef.getItemTools().isWoodTool(inHand)) {
            tier = 1;
        } else if (pluginRef.getItemTools().isStoneTool(inHand)) {
            tier = 2;
        } else if (pluginRef.getItemTools().isIronTool(inHand)) {
            tier = 3;
        } else if (pluginRef.getItemTools().isGoldTool(inHand)) {
            tier = 1;
        } else if (pluginRef.getItemTools().isDiamondTool(inHand)) {
            tier = 4;
        }
        /*else if (mcMMO.getModManager().isCustomTool(inHand)) {
            tier = mcMMO.getModManager().getTool(inHand).getTier();
        }*/

        return tier;
    }

    public void handleHealthbars(Entity attacker, LivingEntity target, double damage, mcMMO plugin) {
        if (!(attacker instanceof Player)) {
            return;
        }

        Player player = (Player) attacker;

        if (pluginRef.getMiscTools().isNPCEntityExcludingVillagers(player) || pluginRef.getMiscTools().isNPCEntityExcludingVillagers(target)) {
            return;
        }

        if (!player.hasMetadata(MetadataConstants.PLAYER_DATA_METAKEY)) {
            return;
        }

        pluginRef.getMobHealthBarManager().handleMobHealthbars(target, damage, plugin);
    }
}
