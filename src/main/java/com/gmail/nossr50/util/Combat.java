package com.gmail.nossr50.util;

import org.bukkit.Material;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
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
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.runnables.GainXp;
import com.gmail.nossr50.runnables.BleedTimer;
import com.gmail.nossr50.skills.Acrobatics;
import com.gmail.nossr50.skills.Archery;
import com.gmail.nossr50.skills.Axes;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.skills.Swords;
import com.gmail.nossr50.skills.Taming;
import com.gmail.nossr50.skills.Unarmed;

public class Combat {

    /**
     * Apply combat modifiers and process and XP gain.
     *
     * @param event The event to run the combat checks on.
     * @param plugin mcMMO plugin instance
     */
    public static void combatChecks(EntityDamageByEntityEvent event, mcMMO plugin) {
        if (event.getDamage() == 0 || event.getEntity().isDead()) {
            return;
        }

        Entity damager = event.getDamager();
        LivingEntity target = (LivingEntity) event.getEntity();
        EntityType damagerType = damager.getType();
        EntityType targetType = target.getType();

        switch (damagerType) {
        case PLAYER:
            Player attacker = (Player) event.getDamager();
            ItemStack itemInHand = attacker.getItemInHand();
            PlayerProfile PPa = Users.getProfile(attacker);

            combatAbilityChecks(attacker);

            if (ItemChecks.isSword(itemInHand) && Permissions.getInstance().swords(attacker)) {
                if (Permissions.getInstance().swordsBleed(attacker)) {
                    Swords.bleedCheck(attacker, target, plugin);
                }

                if (PPa.getAbilityMode(AbilityType.SERRATED_STRIKES) && Permissions.getInstance().serratedStrikes(attacker)) {
                    applyAbilityAoE(attacker, target, event.getDamage() / 4, plugin, SkillType.SWORDS);
                    BleedTimer.add(target, 5);
                }

                startGainXp(attacker, PPa, target, SkillType.SWORDS, plugin);
            }
            else if (ItemChecks.isAxe(itemInHand) && Permissions.getInstance().axes(attacker)) {
                if (Permissions.getInstance().axeBonus(attacker)) {
                    Axes.axesBonus(attacker, event);
                }

                if (Permissions.getInstance().criticalHit(attacker)) {
                    Axes.axeCriticalCheck(attacker, event);
                }

                if (Permissions.getInstance().impact(attacker)) {
                    Axes.impact(attacker, target, event);
                }
 
                if (PPa.getAbilityMode(AbilityType.SKULL_SPLIITER) && Permissions.getInstance().skullSplitter(attacker)) {
                    applyAbilityAoE(attacker, target, event.getDamage() / 2, plugin, SkillType.AXES);
                }

                startGainXp(attacker, PPa, target, SkillType.AXES, plugin);
            }
            else if (itemInHand.getType().equals(Material.AIR) && Permissions.getInstance().unarmed(attacker)) {
                if (Permissions.getInstance().unarmedBonus(attacker)) {
                    Unarmed.unarmedBonus(PPa, event);
                }

                if (PPa.getAbilityMode(AbilityType.BERSERK) && Permissions.getInstance().berserk(attacker)) {
                    event.setDamage((int) (event.getDamage() * 1.5));
                }

                if (targetType.equals(EntityType.PLAYER) && Permissions.getInstance().disarm(attacker)) {
                    Unarmed.disarmProcCheck(attacker, (Player) target);
                }

                startGainXp(attacker, PPa, target, SkillType.UNARMED, plugin);
            }
            else if (itemInHand.getType().equals(Material.BONE) && Permissions.getInstance().beastLore(attacker)) {
                Taming.beastLore(event, target, attacker);
            }
            break;

        case WOLF:
            Wolf wolf = (Wolf) damager;

            if (wolf.isTamed() && wolf.getOwner() instanceof Player) {
                Player master = (Player) wolf.getOwner();
                PlayerProfile PPo = Users.getProfile(master);

                if (Permissions.getInstance().taming(master)) {
                    if (Permissions.getInstance().fastFoodService(master)) {
                        Taming.fastFoodService(PPo, wolf, event);
                    }

                    if (Permissions.getInstance().sharpenedclaws(master)) {
                        Taming.sharpenedClaws(PPo, event);
                    }

                    if (Permissions.getInstance().gore(master)) {
                        Taming.gore(PPo, event, master, plugin);
                    }

                    startGainXp(master, PPo, target, SkillType.TAMING, plugin);
                }
            }
            break;

        case ARROW:
            archeryCheck((EntityDamageByEntityEvent) event, plugin);
            break;
        }

        if (targetType.equals(EntityType.PLAYER)) {
            Swords.counterAttackChecks(event);
            Acrobatics.dodgeChecks(event);
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
     * @param event The event to run the archery checks on.
     * @param pluginx mcMMO plugin instance
     */
    public static void archeryCheck(EntityDamageByEntityEvent event, mcMMO pluginx) {
        Arrow arrow = (Arrow) event.getDamager();
        LivingEntity shooter = arrow.getShooter();
        LivingEntity target = (LivingEntity) event.getEntity();

        if (target instanceof Player) {
            Player defender = (Player) target;

            if (defender.getItemInHand().getType().equals(Material.AIR)) {
                Unarmed.deflectCheck(defender, event);
            }
        }

        if (shooter instanceof Player) {
            Player attacker = (Player) shooter;
            PlayerProfile PPa = Users.getProfile(attacker);
            int damage = event.getDamage();

            if (Permissions.getInstance().archery(attacker) && damage > 0) {

                if (Permissions.getInstance().archeryBonus(attacker)) {

                    /*Archery needs a damage bonus to be viable in PVP*/
                    int skillLvl = Users.getProfile(attacker).getSkillLevel(SkillType.ARCHERY);
                    double dmgBonusPercent = ((skillLvl / 50) * 0.1D);

                    /* Cap maximum bonus at 200% */
                    if (dmgBonusPercent > 2) {
                        dmgBonusPercent = 2;
                    }

                    /* Every 50 skill levels Archery gains 10% damage bonus, set that here */
                    //TODO: Work in progress for balancing out Archery, will work on it more later...
                    int archeryBonus = (int)(event.getDamage() * dmgBonusPercent);
                    event.setDamage(event.getDamage() + archeryBonus);
                }

                if (Permissions.getInstance().trackArrows(attacker)) {
                    Archery.trackArrows(pluginx, target, PPa);
                }

                startGainXp(attacker, PPa, target, SkillType.ARCHERY, pluginx);

                if (target instanceof Player) {
                    Player defender = (Player) target;
                    PlayerProfile PPd = Users.getProfile(defender);

                    if (PPa.inParty() && PPd.inParty() && Party.getInstance().inSameParty(defender, attacker)) {
                        event.setCancelled(true);
                        return;
                    }

                    if (Permissions.getInstance().daze(attacker)) {
                        Archery.dazeCheck(defender, attacker);
                    }
                }
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
        if (Config.getInstance().getEventCallbackEnabled()) {
            EntityDamageEvent ede = (EntityDamageEvent) new FakeEntityDamageEvent(target, cause, dmg);
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
            EntityDamageEvent ede = (EntityDamageByEntityEvent) new FakeEntityDamageByEntityEvent(attacker, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, dmg);
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
     * @param plugin mcMMO plugin instance
     * @param type The type of skill being used
     */
    private static void applyAbilityAoE(Player attacker, LivingEntity target, int damage, mcMMO plugin, SkillType type) {
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

            switch (entity.getType()) {
            case WOLF:
                AnimalTamer tamer = ((Wolf) entity).getOwner();

                if (tamer instanceof Player) {
                    if (tamer.equals(attacker) || Party.getInstance().inSameParty(attacker, (Player) tamer)) {
                        continue;
                    }
                }

                break;
            case PLAYER:
                Player defender = (Player) entity;

                if (!target.getWorld().getPVP()) {
                    continue;
                }

                if (defender.getName().equals(attacker.getName())) {
                    continue;
                }

                if (Party.getInstance().inSameParty(attacker, defender)) {
                    continue;
                }

                PlayerProfile playerProfile = Users.getProfile((Player) entity);

                if (playerProfile.getGodMode()) {
                    continue;
                }

                break;
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
     * @param plugin mcMMO plugin instance
     */
    public static void startGainXp(Player attacker, PlayerProfile PP, LivingEntity target, SkillType skillType, mcMMO pluginx) {
        double baseXP = 0;

        if (target instanceof Player) {
            if (!Config.getInstance().getExperienceGainsPlayerVersusPlayerEnabled()) {
                return;
            }

            Player defender = (Player) target;
            PlayerProfile PPd = Users.getProfile(defender);

            if (System.currentTimeMillis() >= (PPd.getRespawnATS() * 1000) + 5000 && ((PPd.getLastLogin() + 5) * 1000) < System.currentTimeMillis() && defender.getHealth() >= 1) {
                baseXP = 20 * Config.getInstance().getPlayerVersusPlayerXP();
            }
        }
        else if (!target.hasMetadata("mcmmoFromMobSpawner")) {
            if (target instanceof Animals && !target.hasMetadata("mcmmoSummoned")) {
                baseXP = Config.getInstance().getAnimalsXP();
            }
            else {
                EntityType type = target.getType();

                switch (type) {
                case BLAZE:
                    baseXP = Config.getInstance().getBlazeXP();
                    break;

                case CAVE_SPIDER:
                    baseXP = Config.getInstance().getCaveSpiderXP();
                    break;

                case CREEPER:
                    baseXP = Config.getInstance().getCreeperXP();
                    break;

                case ENDER_DRAGON:
                    baseXP = Config.getInstance().getEnderDragonXP();
                    break;

                case ENDERMAN:
                    baseXP = Config.getInstance().getEndermanXP();
                    break;

                case GHAST:
                    baseXP = Config.getInstance().getGhastXP();
                    break;

                case MAGMA_CUBE:
                    baseXP = Config.getInstance().getMagmaCubeXP();
                    break;

                case IRON_GOLEM:
                    if (!((IronGolem) target).isPlayerCreated())
                        baseXP = Config.getInstance().getIronGolemXP();
                    break;

                case PIG_ZOMBIE:
                    baseXP = Config.getInstance().getPigZombieXP();
                    break;

                case SILVERFISH:
                    baseXP = Config.getInstance().getSilverfishXP();
                    break;

                case SKELETON:
                    baseXP = Config.getInstance().getSkeletonXP();
                    break;

                case SLIME:
                    baseXP = Config.getInstance().getSlimeXP();
                    break;

                case SPIDER:
                    baseXP = Config.getInstance().getSpiderXP();
                    break;

                case ZOMBIE:
                    baseXP = Config.getInstance().getZombieXP();
                    break;

                default:
                    break;
                }
            }

            baseXP *= 10;
        }

        if (baseXP != 0) {
            mcMMO.p.getServer().getScheduler().scheduleSyncDelayedTask(pluginx, new GainXp(attacker, PP, skillType, baseXP, target), 0);
        }
    }
}
