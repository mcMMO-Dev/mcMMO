package com.gmail.nossr50.util.skills;

import org.bukkit.Material;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.fake.FakeEntityDamageByEntityEvent;
import com.gmail.nossr50.events.fake.FakeEntityDamageEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.runnables.skills.AwardCombatXpTask;
import com.gmail.nossr50.runnables.skills.BleedTimerTask;
import com.gmail.nossr50.skills.acrobatics.AcrobaticsManager;
import com.gmail.nossr50.skills.archery.ArcheryManager;
import com.gmail.nossr50.skills.axes.AxesManager;
import com.gmail.nossr50.skills.swords.Swords;
import com.gmail.nossr50.skills.swords.SwordsManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.MobHealthbarUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;

public final class CombatUtils {
    private CombatUtils() {}

    private static void processSwordCombat(LivingEntity target, Player player, double damage) {
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        SwordsManager swordsManager = mcMMOPlayer.getSwordsManager();

        if (swordsManager.canActivateAbility()) {
            mcMMOPlayer.checkAbilityActivation(SkillType.SWORDS);
        }

        if (swordsManager.canUseBleed()) {
            swordsManager.bleedCheck(target);
        }

        if (swordsManager.canUseSerratedStrike()) {
            swordsManager.serratedStrikes(target, damage);
        }

        startGainXp(mcMMOPlayer, target, SkillType.SWORDS);
    }

    private static void processAxeCombat(LivingEntity target, Player player, EntityDamageByEntityEvent event) {
        double initialDamage = event.getDamage();
        double finalDamage = initialDamage;

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        AxesManager axesManager = mcMMOPlayer.getAxesManager();

        if (axesManager.canActivateAbility()) {
            mcMMOPlayer.checkAbilityActivation(SkillType.AXES);
        }

        if (axesManager.canUseAxeMastery()) {
            finalDamage += axesManager.axeMastery(target);
        }

        if (axesManager.canCriticalHit(target)) {
            finalDamage += axesManager.criticalHit(target, initialDamage);
        }

        if (axesManager.canImpact(target)) {
            axesManager.impactCheck(target);
        }
        else if (axesManager.canGreaterImpact(target)) {
            finalDamage += axesManager.greaterImpact(target);
        }

        if (axesManager.canUseSkullSplitter(target)) {
            axesManager.skullSplitterCheck(target, initialDamage);
        }

        event.setDamage(finalDamage);
        startGainXp(mcMMOPlayer, target, SkillType.AXES);
    }

    private static void processUnarmedCombat(LivingEntity target, Player player, EntityDamageByEntityEvent event) {
        double initialDamage = event.getDamage();
        double finalDamage = initialDamage;

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        UnarmedManager unarmedManager = mcMMOPlayer.getUnarmedManager();

        if (unarmedManager.canActivateAbility()) {
            mcMMOPlayer.checkAbilityActivation(SkillType.UNARMED);
        }

        if (unarmedManager.canUseIronArm()) {
            finalDamage += unarmedManager.ironArm(target);
        }

        if (unarmedManager.canUseBerserk()) {
            finalDamage += unarmedManager.berserkDamage(target, initialDamage);
        }

        if (unarmedManager.canDisarm(target)) {
            unarmedManager.disarmCheck((Player) target);
        }

        event.setDamage(finalDamage);
        startGainXp(mcMMOPlayer, target, SkillType.UNARMED);
    }

    private static void processTamingCombat(LivingEntity target, Player master, Wolf wolf, EntityDamageByEntityEvent event) {
        double initialDamage = event.getDamage();
        double finalDamage = initialDamage;

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(master);
        TamingManager tamingManager = mcMMOPlayer.getTamingManager();

        if (tamingManager.canUseFastFoodService()) {
            tamingManager.fastFoodService(wolf, event.getDamage());
        }

        if (tamingManager.canUseSharpenedClaws()) {
            finalDamage += tamingManager.sharpenedClaws(target, wolf);
        }

        if (tamingManager.canUseGore()) {
            finalDamage += tamingManager.gore(target, initialDamage, wolf);
        }

        event.setDamage(finalDamage);
        startGainXp(mcMMOPlayer, target, SkillType.TAMING);
    }

    private static void processArcheryCombat(LivingEntity target, Player player, EntityDamageByEntityEvent event, Arrow arrow) {
        double initialDamage = event.getDamage();
        double finalDamage = initialDamage;

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        ArcheryManager archeryManager = mcMMOPlayer.getArcheryManager();

        if (target instanceof Player && SkillType.UNARMED.getPVPEnabled()) {
            UnarmedManager unarmedManager = UserManager.getPlayer((Player) target).getUnarmedManager();

            if (unarmedManager.canDeflect()) {
                event.setCancelled(unarmedManager.deflectCheck());

                if (event.isCancelled()) {
                    return;
                }
            }
        }

        if (archeryManager.canSkillShot()) {
            finalDamage += archeryManager.skillShot(target, initialDamage, arrow);
        }

        if (archeryManager.canDaze(target)) {
            finalDamage += archeryManager.daze((Player) target, arrow);
        }

        if (!arrow.hasMetadata(mcMMO.infiniteArrowKey) && archeryManager.canRetrieveArrows()) {
            archeryManager.retrieveArrows(target);
        }

        archeryManager.distanceXpBonus(target, arrow);

        event.setDamage(finalDamage);
        startGainXp(mcMMOPlayer, target, SkillType.ARCHERY, arrow.getMetadata(mcMMO.bowForceKey).get(0).asDouble());
    }

    /**
     * Apply combat modifiers and process and XP gain.
     *
     * @param event The event to run the combat checks on.
     */
    public static void processCombatAttack(EntityDamageByEntityEvent event, Entity attacker, LivingEntity target) {
        Entity damager = event.getDamager();
        EntityType entityType = damager.getType();

        if (attacker instanceof Player && entityType == EntityType.PLAYER) {
            Player player = (Player) attacker;

            if (Misc.isNPCEntity(player)) {
                return;
            }

            ItemStack heldItem = player.getItemInHand();

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
                if (!SkillType.SWORDS.shouldProcess(target)) {
                    return;
                }

                if (SkillType.SWORDS.getPermissions(player)) {
                    processSwordCombat(target, player, event.getDamage());
                }
            }
            else if (ItemUtils.isAxe(heldItem)) {
                if (!SkillType.AXES.shouldProcess(target)) {
                    return;
                }

                if (SkillType.AXES.getPermissions(player)) {
                    processAxeCombat(target, player, event);
                }
            }
            else if (heldItem.getType() == Material.AIR) {
                if (!SkillType.UNARMED.shouldProcess(target)) {
                    return;
                }

                if (SkillType.UNARMED.getPermissions(player)) {
                    processUnarmedCombat(target, player, event);
                }
            }
        }

        else if (entityType == EntityType.WOLF) {
            Wolf wolf = (Wolf) damager;
            AnimalTamer tamer = wolf.getOwner();

            if (tamer != null && tamer instanceof Player && SkillType.TAMING.shouldProcess(target)) {
                Player master = (Player) tamer;

                if (!Misc.isNPCEntity(master) && SkillType.TAMING.getPermissions(master)) {
                    processTamingCombat(target, master, wolf, event);
                }
            }
        }
        else if (entityType == EntityType.ARROW) {
            Arrow arrow = (Arrow) damager;
            ProjectileSource projectileSource = arrow.getShooter();

            if (projectileSource != null && projectileSource instanceof Player && SkillType.ARCHERY.shouldProcess(target)) {
                Player player = (Player) projectileSource;

                if (!Misc.isNPCEntity(player) && SkillType.ARCHERY.getPermissions(player)) {
                    processArcheryCombat(target, player, event, arrow);
                }
            }
        }

        if (target instanceof Player) {
            if (Misc.isNPCEntity(target)) {
                return;
            }

            Player player = (Player) target;
            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
            AcrobaticsManager acrobaticsManager = mcMMOPlayer.getAcrobaticsManager();

            if (acrobaticsManager.canDodge(damager)) {
                event.setDamage(acrobaticsManager.dodgeCheck(event.getDamage()));
            }

            if (ItemUtils.isSword(player.getItemInHand())) {
                if (!SkillType.SWORDS.shouldProcess(target)) {
                    return;
                }

                SwordsManager swordsManager = mcMMOPlayer.getSwordsManager();

                if (swordsManager.canUseCounterAttack(damager)) {
                    swordsManager.counterAttackChecks((LivingEntity) damager, event.getDamage());
                }
            }
        }
        else if (attacker instanceof Player) {
            Player player = (Player) attacker;

            if (Misc.isNPCEntity(player) || Misc.isNPCEntity(target)) {
                return;
            }

            MobHealthbarUtils.handleMobHealthbars(player, target, event.getDamage());
        }
    }

    /**
     * Attempt to damage target for value dmg with reason CUSTOM
     *
     * @param target LivingEntity which to attempt to damage
     * @param damage Amount of damage to attempt to do
     */
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
    public static void dealDamage(LivingEntity target, double damage, LivingEntity attacker) {
        dealDamage(target, damage, DamageCause.ENTITY_ATTACK, attacker);
    }

    /**
     * Attempt to damage target for value dmg with reason ENTITY_ATTACK with damager attacker
     *
     * @param target LivingEntity which to attempt to damage
     * @param damage Amount of damage to attempt to do
     * @param attacker Player to pass to event as damager
     */
    public static void dealDamage(LivingEntity target, double damage, DamageCause cause, Entity attacker) {
        if (target.isDead()) {
            return;
        }

        target.damage(callFakeDamageEvent(attacker, target, cause, damage));
    }

    /**
     * Apply Area-of-Effect ability actions.
     *
     * @param attacker The attacking player
     * @param target The defending entity
     * @param damage The initial damage amount
     * @param type The type of skill being used
     */
    public static void applyAbilityAoE(Player attacker, LivingEntity target, double damage, SkillType type) {
        int numberOfTargets = getTier(attacker.getItemInHand()); // The higher the weapon tier, the more targets you hit
        double damageAmount = Math.max(damage, 1);

        for (Entity entity : target.getNearbyEntities(2.5, 2.5, 2.5)) {
            if (numberOfTargets <= 0) {
                break;
            }

            if (Misc.isNPCEntity(entity) || !(entity instanceof LivingEntity) || !shouldBeAffected(attacker, entity)) {
                continue;
            }

            LivingEntity livingEntity = (LivingEntity) entity;
            EventUtils.callFakeArmSwingEvent(attacker);

            switch (type) {
                case SWORDS:
                    if (entity instanceof Player) {
                        ((Player) entity).sendMessage(LocaleLoader.getString("Swords.Combat.SS.Struck"));
                    }

                    BleedTimerTask.add(livingEntity, Swords.serratedStrikesBleedTicks);
                    break;

                case AXES:
                    if (entity instanceof Player) {
                        ((Player) entity).sendMessage(LocaleLoader.getString("Axes.Combat.SS.Struck"));
                    }

                    break;

                default:
                    break;
            }

            dealDamage(livingEntity, damageAmount, attacker);
            numberOfTargets--;
        }
    }

    public static void startGainXp(McMMOPlayer mcMMOPlayer, LivingEntity target, SkillType skillType) {
        startGainXp(mcMMOPlayer, target, skillType, 1.0);
    }

    /**
     * Start the task that gives combat XP.
     *
     * @param mcMMOPlayer The attacking player
     * @param target The defending entity
     * @param skillType The skill being used
     */
    private static void startGainXp(McMMOPlayer mcMMOPlayer, LivingEntity target, SkillType skillType, double multiplier) {
        double baseXP = 0;

        if (target instanceof Player) {
            if (!ExperienceConfig.getInstance().getExperienceGainsPlayerVersusPlayerEnabled()) {
                return;
            }

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
                baseXP = ExperienceConfig.getInstance().getAnimalsXP();
            }
            else {
                EntityType type = target.getType();

                switch (type) {
                    case BAT:
                    case SQUID:
                        baseXP = ExperienceConfig.getInstance().getAnimalsXP();
                        break;

                    case BLAZE:
                    case CAVE_SPIDER:
                    case CREEPER:
                    case ENDER_DRAGON:
                    case ENDERMAN:
                    case GHAST:
                    case GIANT:
                    case MAGMA_CUBE:
                    case PIG_ZOMBIE:
                    case SILVERFISH:
                    case SLIME:
                    case SPIDER:
                    case WITCH:
                    case WITHER:
                    case ZOMBIE:
                        baseXP = ExperienceConfig.getInstance().getCombatXP(type);
                        break;

                    case SKELETON:
                        switch (((Skeleton) target).getSkeletonType()) {
                            case WITHER:
                                baseXP = ExperienceConfig.getInstance().getWitherSkeletonXP();
                                break;
                            default:
                                baseXP = ExperienceConfig.getInstance().getCombatXP(type);
                                break;
                        }
                        break;

                    case IRON_GOLEM:
                        if (!((IronGolem) target).isPlayerCreated()) {
                            baseXP = ExperienceConfig.getInstance().getCombatXP(type);
                        }
                        break;

                    default:
                        baseXP = 1.0;
                        mcMMO.getModManager().addCustomEntity(target);
                        break;
                }
            }

            if (target.hasMetadata(mcMMO.entityMetadataKey)) {
                baseXP *= ExperienceConfig.getInstance().getSpawnedMobXpMultiplier();
            }

            baseXP *= 10;
        }

        baseXP *= multiplier;

        if (baseXP != 0) {
            new AwardCombatXpTask(mcMMOPlayer, skillType, baseXP, target).runTaskLater(mcMMO.p, 0);
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

            if (!defender.getWorld().getPVP() || defender == player || UserManager.getPlayer(defender).getGodMode()) {
                return false;
            }

            if (PartyManager.inSameParty(player, defender) && !(Permissions.friendlyFire(player) && Permissions.friendlyFire(defender))) {
                return false;
            }

            // It may seem a bit redundant but we need a check here to prevent bleed from being applied in applyAbilityAoE()
            if (callFakeDamageEvent(player, entity, 1.0) == 0) {
                return false;
            }
        }
        else if (entity instanceof Tameable) {
            if (isFriendlyPet(player, (Tameable) entity)) {
                // isFriendlyPet ensures that the Tameable is: Tamed, owned by a player, and the owner is in the same party
                // So we can make some assumptions here, about our casting and our check
                Player owner = (Player) ((Tameable) entity).getOwner();
                if (!(Permissions.friendlyFire(player) && Permissions.friendlyFire(owner))) {
                    return false;
                }
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

                return (owner == attacker || PartyManager.inSameParty(attacker, owner));
            }
        }

        return false;
    }

    public static double callFakeDamageEvent(Entity attacker, Entity target, double damage) {
        return callFakeDamageEvent(attacker, target, DamageCause.ENTITY_ATTACK, damage);
    }

    public static double callFakeDamageEvent(Entity attacker, Entity target, DamageCause cause, double damage) {
        if (Config.getInstance().getEventCallbackEnabled()) {
            EntityDamageEvent damageEvent = attacker == null ? new FakeEntityDamageEvent(target, cause, damage) : new FakeEntityDamageByEntityEvent(attacker, target, cause, damage);
            mcMMO.p.getServer().getPluginManager().callEvent(damageEvent);

            if (damageEvent.isCancelled()) {
                return 0;
            }

            damage = damageEvent.getDamage();
        }

        return damage;
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
}
