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
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
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
import com.gmail.nossr50.skills.taming.Taming;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.ModUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;

public final class CombatUtils {
    private CombatUtils() {}

    /**
     * Apply combat modifiers and process and XP gain.
     *
     * @param event The event to run the combat checks on.
     */
    public static void combatChecks(EntityDamageByEntityEvent event, Entity attacker, LivingEntity target) {
        Entity damager = event.getDamager();

        if (attacker instanceof Player && damager.getType() == EntityType.PLAYER) {
            Player player = (Player) attacker;
            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

            if (Misc.isNPCEntity(player)) {
                return;
            }

            ItemStack heldItem = player.getItemInHand();

            if (target instanceof Tameable) {
                if (heldItem.getType() == Material.BONE && Permissions.beastLore(player)) {
                    mcMMOPlayer.getTamingManager().beastLore(target);
                    event.setCancelled(true);
                    return;
                }
                else if (isFriendlyPet(player, (Tameable) target)) {
                    return;
                }
            }

            if (ItemUtils.isSword(heldItem)) {
                if (!shouldProcessSkill(target, SkillType.SWORDS)) {
                    return;
                }

                if (Permissions.skillEnabled(player, SkillType.SWORDS)) {
                    SwordsManager swordsManager = mcMMOPlayer.getSwordsManager();

                    if (swordsManager.canActivateAbility()) {
                        SkillUtils.abilityCheck(mcMMOPlayer, SkillType.SWORDS);
                    }

                    if (swordsManager.canUseBleed()) {
                        swordsManager.bleedCheck(target);
                    }

                    if (swordsManager.canUseSerratedStrike()) {
                        swordsManager.serratedStrikes(target, event.getDamage());
                    }

                    startGainXp(swordsManager.getMcMMOPlayer(), target, SkillType.SWORDS);
                }
            }
            else if (ItemUtils.isAxe(heldItem)) {
                if (!shouldProcessSkill(target, SkillType.AXES)) {
                    return;
                }

                if (Permissions.skillEnabled(player, SkillType.AXES)) {
                    AxesManager axesManager = mcMMOPlayer.getAxesManager();

                    if (axesManager.canActivateAbility()) {
                        SkillUtils.abilityCheck(mcMMOPlayer, SkillType.AXES);
                    }

                    if (axesManager.canUseAxeMastery()) {
                        event.setDamage(axesManager.axeMasteryCheck(event.getDamage()));
                    }

                    if (axesManager.canCriticalHit(target)) {
                        event.setDamage(axesManager.criticalHitCheck(target, event.getDamage()));
                    }

                    if (axesManager.canImpact(target)) {
                        axesManager.impactCheck(target);
                    }
                    else if (axesManager.canGreaterImpact(target)) {
                        event.setDamage(axesManager.greaterImpactCheck(target, event.getDamage()));
                    }

                    if (axesManager.canUseSkullSplitter(target)) {
                        axesManager.skullSplitterCheck(target, event.getDamage());
                    }

                    startGainXp(axesManager.getMcMMOPlayer(), target, SkillType.AXES);
                }
            }
            else if (heldItem.getType() == Material.AIR) {
                if (!shouldProcessSkill(target, SkillType.UNARMED)) {
                    return;
                }

                if (Permissions.skillEnabled(player, SkillType.UNARMED)) {
                    UnarmedManager unarmedManager = mcMMOPlayer.getUnarmedManager();

                    if (unarmedManager.canActivateAbility()) {
                        SkillUtils.abilityCheck(mcMMOPlayer, SkillType.UNARMED);
                    }

                    if (unarmedManager.canUseIronArm()) {
                        event.setDamage(unarmedManager.ironArmCheck(event.getDamage()));
                    }

                    if (unarmedManager.canUseBerserk()) {
                        event.setDamage(unarmedManager.berserkDamage(event.getDamage()));
                    }

                    if (unarmedManager.canDisarm(target)) {
                        unarmedManager.disarmCheck((Player) target);
                    }

                    startGainXp(unarmedManager.getMcMMOPlayer(), target, SkillType.UNARMED);
                }
            }
            else if (heldItem.getType() == Material.BONE) {
                TamingManager tamingManager = mcMMOPlayer.getTamingManager();

                if (tamingManager.canUseBeastLore(target)) {
                    tamingManager.beastLore(target);
                }
            }
        }

        switch (damager.getType()) {
            case WOLF:
                Wolf wolf = (Wolf) damager;

                if (wolf.isTamed() && wolf.getOwner() instanceof Player) {
                    Player master = (Player) wolf.getOwner();

                    if (Misc.isNPCEntity(master)) {
                        return;
                    }

                    if (!shouldProcessSkill(target, SkillType.TAMING)) {
                        return;
                    }

                    if (Permissions.skillEnabled(master, SkillType.TAMING)) {
                        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(master);
                        TamingManager tamingManager = mcMMOPlayer.getTamingManager();
                        int skillLevel = tamingManager.getSkillLevel();

                        if (skillLevel >= Taming.fastFoodServiceUnlockLevel && Permissions.fastFoodService(master)) {
                            tamingManager.fastFoodService(wolf, event.getDamage());
                        }

                        if (skillLevel >= Taming.sharpenedClawsUnlockLevel && Permissions.sharpenedClaws(master)) {
                            event.setDamage(Taming.sharpenedClaws(event.getDamage()));
                        }

                        if (Permissions.gore(master)) {
                            event.setDamage(tamingManager.gore(target, event.getDamage()));
                        }

                        startGainXp(mcMMOPlayer, target, SkillType.TAMING);
                    }
                }

                break;

            case ARROW:
                LivingEntity shooter = ((Arrow) damager).getShooter();

                /* Break instead of return due to Dodge/Counter/Deflect abilities */
                if (shooter == null || !(shooter instanceof Player)) {
                    break;
                }

                if (!shouldProcessSkill(target, SkillType.ARCHERY)) {
                    return;
                }

                archeryCheck((Player) shooter, target, event);
                break;

            default:
                break;
        }

        if (target instanceof Player) {
            Player player = (Player) target;

            if (Misc.isNPCEntity(player)) {
                return;
            }

            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
            AcrobaticsManager acrobaticsManager = mcMMOPlayer.getAcrobaticsManager();

            if (acrobaticsManager.canDodge(damager)) {
                event.setDamage(acrobaticsManager.dodgeCheck(event.getDamage()));
            }

            ItemStack heldItem = player.getItemInHand();

            if (damager instanceof Player) {
                if (SkillType.SWORDS.getPVPEnabled() && ItemUtils.isSword(heldItem) && Permissions.counterAttack(player)) {
                    mcMMOPlayer.getSwordsManager().counterAttackChecks((LivingEntity) damager, event.getDamage());
                }
            }
            else {
                if (SkillType.SWORDS.getPVEEnabled() && damager instanceof LivingEntity && ItemUtils.isSword(heldItem) && Permissions.counterAttack(player)) {
                    mcMMOPlayer.getSwordsManager().counterAttackChecks((LivingEntity) damager, event.getDamage());
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
        if (Misc.isNPCEntity(shooter)) {
            return;
        }

        if (Permissions.skillEnabled(shooter, SkillType.ARCHERY)) {
            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(shooter);
            ArcheryManager archeryManager = mcMMOPlayer.getArcheryManager();

            if (archeryManager.canSkillShot()) {
                event.setDamage(archeryManager.skillShotCheck(event.getDamage()));
            }

            if (target instanceof Player && SkillType.UNARMED.getPVPEnabled() && ((Player) target).getItemInHand().getType() == Material.AIR && Permissions.arrowDeflect((Player) target)) {
                event.setCancelled(mcMMOPlayer.getUnarmedManager().deflectCheck());

                if (event.isCancelled()) {
                    return;
                }
            }

            if (archeryManager.canDaze(target)) {
                event.setDamage(archeryManager.dazeCheck((Player) target, event.getDamage()));
            }

            if (archeryManager.canTrackArrows()) {
                archeryManager.trackArrows(target);
            }

            archeryManager.distanceXpBonus(target);
            startGainXp(UserManager.getPlayer(shooter), target, SkillType.ARCHERY);
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
        if (Config.getInstance().getEventCallbackEnabled()) {
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
        if (Config.getInstance().getEventCallbackEnabled()) {
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
        int numberOfTargets = Misc.getTier(attacker.getItemInHand()); // The higher the weapon tier, the more targets you hit
        int damageAmount = damage;

        if (damageAmount < 1) {
            damageAmount = 1;
        }

        for (Entity entity : target.getNearbyEntities(2.5, 2.5, 2.5)) {
            if (Misc.isNPCEntity(entity) || !(entity instanceof LivingEntity) || !shouldBeAffected(attacker, entity)) {
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

                    BleedTimerTask.add((LivingEntity) entity, Swords.serratedStrikesBleedTicks);

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
     * @param mcMMOPlayer The attacking player
     * @param target The defending entity
     * @param skillType The skill being used
     */
    public static void startGainXp(McMMOPlayer mcMMOPlayer, LivingEntity target, SkillType skillType) {
        double baseXP = 0;

        if (target instanceof Player) {
            if (!Config.getInstance().getExperienceGainsPlayerVersusPlayerEnabled()) {
                return;
            }

            Player defender = (Player) target;

            if (System.currentTimeMillis() >= UserManager.getPlayer(defender).getRespawnATS() + 5) {
                baseXP = 20 * Config.getInstance().getPlayerVersusPlayerXP();
            }
        }
        else if (!target.hasMetadata(mcMMO.entityMetadataKey)) {
            if (target instanceof Animals) {
                if (ModUtils.isCustomEntity(target)) {
                    baseXP = ModUtils.getCustomEntity(target).getXpMultiplier();
                }
                else {
                    baseXP = Config.getInstance().getAnimalsXP();
                }
            }
            else {
                EntityType type = target.getType();

                switch (type) {
                    case BAT:
                        baseXP = Config.getInstance().getAnimalsXP();
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
                        baseXP = Config.getInstance().getCombatXP(type);
                        break;

                    // Temporary workaround for custom entities
                    case UNKNOWN:
                        baseXP = 1.0;
                        break;

                    case SKELETON:
                        switch (((Skeleton) target).getSkeletonType()) {
                            case WITHER:
                                baseXP = Config.getInstance().getWitherSkeletonXP();
                                break;
                            default:
                                baseXP = Config.getInstance().getCombatXP(type);
                                break;
                        }
                        break;

                    case IRON_GOLEM:
                        if (!((IronGolem) target).isPlayerCreated()) {
                            baseXP = Config.getInstance().getCombatXP(type);
                        }
                        break;

                    default:
                        if (ModUtils.isCustomEntity(target)) {
                            baseXP = ModUtils.getCustomEntity(target).getXpMultiplier();
                        }
                        break;
                }
            }

            baseXP *= 10;
        }

        if (baseXP != 0) {
            mcMMO.p.getServer().getScheduler().scheduleSyncDelayedTask(mcMMO.p, new AwardCombatXpTask(mcMMOPlayer, skillType, baseXP, target), 0);
        }
    }

    /**
     * Check to see if the given LivingEntity should be affected by a combat ability.
     *
     * @param player The attacking Player
     * @param entity The defending Entity
     * @return true if the Entity should be damaged, false otherwise.
     */
    public static boolean shouldBeAffected(Player player, Entity entity) {
        if (entity instanceof Player) {
            Player defender = (Player) entity;

            if (!defender.getWorld().getPVP() || defender == player || UserManager.getPlayer(defender).getGodMode()) {
                return false;
            }

            if (PartyManager.inSameParty(player, defender) && !(Permissions.friendlyFire(player) && Permissions.friendlyFire(defender))) {
                return false;
            }

            // It may seem a bit redundant but we need a check here to prevent bleed from being applied in applyAbilityAoE()
            EntityDamageEvent ede = new FakeEntityDamageByEntityEvent(player, entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1);
            mcMMO.p.getServer().getPluginManager().callEvent(ede);

            if (ede.isCancelled()) {
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
    public static boolean isInvincible(LivingEntity entity, int eventDamage) {

        /*
         * So apparently if you do more damage to a LivingEntity than its last damage int you bypass the invincibility.
         * So yeah, this is for that.
         */
        if ((entity.getNoDamageTicks() > entity.getMaximumNoDamageTicks() / 2.0F) && (eventDamage <= entity.getLastDamage())) {
            return true;
        }

        return false;
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

                if (owner == attacker || PartyManager.inSameParty(attacker, owner)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean shouldProcessSkill(LivingEntity target, SkillType skill) {
        boolean process;

        if (target instanceof Player || (target instanceof Tameable && ((Tameable) target).isTamed())) {
            process = skill.getPVPEnabled();
        }
        else {
            process = skill.getPVEEnabled();
        }

        return process;
    }
}
