package com.gmail.nossr50;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.events.FakeEntityDamageByEntityEvent;
import com.gmail.nossr50.events.FakeEntityDamageEvent;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;
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
     * @param pluginx mcMMO plugin instance
     */
    public static void combatChecks(EntityDamageByEntityEvent event, mcMMO pluginx) {
        if (event.getDamage() == 0 || event.getEntity().isDead()) {
            return;
        }

        Entity damager = event.getDamager();
        LivingEntity target = (LivingEntity) event.getEntity();
        int damage = event.getDamage();
        EntityType damagerType = damager.getType();
        EntityType targetType = target.getType();

        switch (damagerType) {
        case PLAYER:
            Player attacker = (Player) event.getDamager();
            ItemStack itemInHand = attacker.getItemInHand();
            PlayerProfile PPa = Users.getProfile(attacker);

            combatAbilityChecks(attacker);
            
            if (ItemChecks.isSword(itemInHand) && mcPermissions.getInstance().swords(attacker)) {
                if (!pluginx.misc.bleedTracker.contains(target)) {
                    Swords.bleedCheck(attacker, target, pluginx);
                }

                if (PPa.getSerratedStrikesMode()) {
                    applyAbilityAoE(attacker, target, damage, pluginx, SkillType.SWORDS);
                }

                startGainXp(attacker, PPa, target, SkillType.SWORDS, pluginx);
            }
            else if (ItemChecks.isAxe(itemInHand) && mcPermissions.getInstance().axes(attacker)) {
                Axes.axesBonus(attacker, event);
                Axes.axeCriticalCheck(attacker, event);
                Axes.impact(attacker, target, event);

                if (PPa.getSkullSplitterMode()) {
                    applyAbilityAoE(attacker, target, damage, pluginx, SkillType.AXES);
                }

                startGainXp(attacker, PPa, target, SkillType.AXES, pluginx);
            }
            else if (itemInHand.getType().equals(Material.AIR) && mcPermissions.getInstance().unarmed(attacker)) {
                Unarmed.unarmedBonus(attacker, event);

                if (PPa.getBerserkMode()) {
                    event.setDamage(damage + (damage / 2));
                }

                if (targetType.equals(EntityType.PLAYER)) {
                    Unarmed.disarmProcCheck(attacker, (Player) target);
                }
                
                startGainXp(attacker, PPa, target, SkillType.UNARMED, pluginx);
            }
            else if (itemInHand.getType().equals(Material.BONE) && mcPermissions.getInstance().taming(attacker) && targetType.equals(EntityType.WOLF)) {
                Wolf wolf = (Wolf) target;
                String message = mcLocale.getString("Combat.BeastLore") + " ";
                int health = wolf.getHealth();
                event.setCancelled(true);

                if (wolf.isTamed()) {
                    message = message.concat(mcLocale.getString("Combat.BeastLoreOwner", new Object[] {Taming.getOwnerName(wolf)}) + " ");
                    message = message.concat(mcLocale.getString("Combat.BeastLoreHealthWolfTamed", new Object[] {health}));
                }
                else {
                    message = message.concat(mcLocale.getString("Combat.BeastLoreHealthWolf", new Object[] {health}));
                }

                attacker.sendMessage(message);
            }
            break;

        case WOLF:
            Wolf wolf = (Wolf) damager;

            if (wolf.isTamed() && wolf.getOwner() instanceof Player) {
                Player master = (Player) wolf.getOwner();
                PlayerProfile PPo = Users.getProfile(master);

                if (mcPermissions.getInstance().taming(master)) {
                    Taming.fastFoodService(PPo, wolf, event);
                    Taming.sharpenedClaws(PPo, event);
                    Taming.gore(PPo, event, master, pluginx);

                    startGainXp(master, PPo, target, SkillType.TAMING, pluginx);
                }
            }
            break;

        case ARROW:
            archeryCheck((EntityDamageByEntityEvent)event, pluginx);
            break;

        default:
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

        if (PPa.getAxePreparationMode()) {
            Skills.abilityCheck(attacker, SkillType.AXES);
        }
        else if (PPa.getSwordsPreparationMode()) {
            Skills.abilityCheck(attacker, SkillType.SWORDS);
        }
        else if (PPa.getFistsPreparationMode()) {
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
            PlayerProfile PPd = Users.getProfile(defender);
            boolean deflect = false;

            if (mcPermissions.getInstance().unarmed(defender) && defender.getItemInHand().getType().equals(Material.AIR)) {
                if (PPd.getSkillLevel(SkillType.UNARMED) >= 1000 && (Math.random() * 1000 <= 500)) {
                    deflect = true;
                }
                else if (Math.random() * 1000 <= (PPd.getSkillLevel(SkillType.UNARMED) / 2)) {
                    deflect = true;
                }

                if (deflect) {
                    event.setCancelled(true);
                    defender.sendMessage(mcLocale.getString("Combat.ArrowDeflect"));
                    return;
                }
            }
        }

        if (shooter instanceof Player) {
            Player attacker = (Player) shooter;
            PlayerProfile PPa = Users.getProfile(attacker);
            int damage = event.getDamage();

            if (mcPermissions.getInstance().archery(attacker) && damage > 0) {
                Archery.trackArrows(pluginx, target, PPa);
                Archery.ignitionCheck(target, attacker);

                startGainXp(attacker, PPa, target, SkillType.ARCHERY, pluginx);

                if (target instanceof Player) {
                    Player defender = (Player) target;
                    PlayerProfile PPd = Users.getProfile(defender);

                    if (PPa.inParty() && PPd.inParty() && Party.getInstance().inSameParty(defender, attacker)) {
                        event.setCancelled(true);
                        return;
                    }

                    Archery.dazeCheck(defender, attacker);
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
    public static void dealDamage(LivingEntity target, int dmg, DamageCause cause) {
        if (LoadProperties.eventCallback) {
            EntityDamageEvent ede = (EntityDamageEvent) new FakeEntityDamageEvent(target, cause, dmg);
            Bukkit.getPluginManager().callEvent(ede);

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
    public static void dealDamage(LivingEntity target, int dmg, Player attacker) {
        if (LoadProperties.eventCallback) {
            EntityDamageEvent ede = (EntityDamageByEntityEvent) new FakeEntityDamageByEntityEvent(attacker, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, dmg);
            Bukkit.getPluginManager().callEvent(ede);

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
        int numberOfTargets = m.getTier(attacker.getItemInHand()); //The higher the weapon tier, the more targets you hit
        int damageAmount = 0;

        if (type.equals(SkillType.AXES)) {
            damageAmount = damage / 2;
        }
        else if (type.equals(SkillType.SWORDS)) {
            damageAmount = damage / 4;
        }

        if (damageAmount < 1) {
            damageAmount = 1;
        }

        for (Entity entity : target.getNearbyEntities(2.5, 2.5, 2.5)) {
            EntityType entityType = entity.getType();

            if (entityType.equals(EntityType.WOLF)) {
                Wolf wolf = (Wolf) entity;
                AnimalTamer tamer = wolf.getOwner();

                if (tamer instanceof Player) {
                    Player owner = (Player) tamer;

                    //Reasons why the target shouldn't be hit
                    if (owner.equals(attacker)) {
                        continue;
                    }

                    if (Party.getInstance().inSameParty(attacker, owner)) {
                        continue;
                    }
                }
            }

            if (entity instanceof LivingEntity && numberOfTargets >= 1) {
                if (entityType.equals(EntityType.PLAYER)) {
                    Player defender = (Player) entity;
                    PlayerProfile PP = Users.getProfile(defender);

                    //Reasons why the target shouldn't be hit
                    if (PP.getGodMode()) {
                        continue;
                    }

                    if (defender.getName().equals(attacker.getName())) { //Is this even possible?
                        continue;
                    }

                    if (Party.getInstance().inSameParty(attacker, defender)) {
                        continue;
                    }

                    if (defender.isDead()) {
                        continue;
                    }

                    //Apply effect to players only if PVP is enabled
                    if (target.getWorld().getPVP()) {
                        String message = "";

                        if (type.equals(SkillType.AXES)) {
                            message = mcLocale.getString("Axes.HitByCleave");
                        }
                        else if (type.equals(SkillType.SWORDS)) {
                            message = mcLocale.getString("Swords.HitBySerratedStrikes");
                        }

                        dealDamage(defender, damageAmount, attacker);
                        defender.sendMessage(message);

                        if (type.equals(SkillType.SWORDS)) {
                            PP.addBleedTicks(5);
                        }

                        numberOfTargets--;
                    }
                }
                else {
                    LivingEntity livingEntity = (LivingEntity) entity;

                    if (type.equals(SkillType.SWORDS) && !plugin.misc.bleedTracker.contains(entity)) {
                        plugin.misc.addToBleedQue(livingEntity);
                    }

                    dealDamage(livingEntity, damageAmount, attacker);
                    numberOfTargets--;
                }
            }
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
    public static void startGainXp(Player attacker, PlayerProfile PP, LivingEntity target, SkillType skillType, mcMMO pluginx)
    {
        double baseXP = 0;

        if (target instanceof Player) {
            if (!LoadProperties.pvpxp) {
                return;
            }

            Player defender = (Player) target;
            PlayerProfile PPd = Users.getProfile(defender);

            if (System.currentTimeMillis() >= (PPd.getRespawnATS() * 1000) + 5000 &&
               ((PPd.getLastLogin() + 5) * 1000) < System.currentTimeMillis() &&
               defender.getHealth() >= 1) {
                baseXP = 20 * LoadProperties.pvpxprewardmodifier;
            }
        }
        else if (!pluginx.misc.mobSpawnerList.contains(target.getEntityId())) {
            if (target instanceof Animals) {
                baseXP = 10 * LoadProperties.animalXP;
            }
            else
            {
                EntityType type = target.getType();

                switch (type) {
                case BLAZE:
                    baseXP = LoadProperties.blazeXP;
                    break;
                case CAVE_SPIDER:
                    baseXP = LoadProperties.cavespiderXP;
                    break;
                case CREEPER:
                    baseXP = LoadProperties.creeperXP;
                    break;
                case ENDER_DRAGON:
                    baseXP = LoadProperties.enderdragonXP;
                    break;
                case ENDERMAN:
                    baseXP = LoadProperties.endermanXP;
                    break;
                case GHAST:
                    baseXP = LoadProperties.ghastXP;
                    break;
                case MAGMA_CUBE:
                    baseXP = LoadProperties.magmacubeXP;
                    break;
                case PIG_ZOMBIE:
                    baseXP = LoadProperties.pigzombieXP;
                    break;
                case SILVERFISH:
                    baseXP = LoadProperties.silverfishXP;
                    break;
                case SKELETON:
                    baseXP = LoadProperties.skeletonXP;
                    break;
                case SLIME:
                    baseXP = LoadProperties.slimeXP;
                    break;
                case SPIDER:
                    baseXP = LoadProperties.spiderXP;
                    break;
                case ZOMBIE:
                    baseXP = LoadProperties.zombieXP;
                    break;
                }
            }

            baseXP *= 10;
        }

        if (baseXP != 0)
            Bukkit.getScheduler().scheduleSyncDelayedTask(pluginx, new GainXp(attacker, PP, skillType, baseXP, target), 0);
    }

}

class GainXp implements Runnable
{
    private Player player = null;
    private PlayerProfile PP = null;
    private double baseXp = 0;
    private SkillType skillType = null;
    private LivingEntity target = null;
    private int baseHealth = 0;

    public GainXp(Player player, PlayerProfile PP, SkillType skillType, double baseXp, LivingEntity target)
    {
        this.player = player;
        this.PP = PP;
        this.skillType = skillType;
        this.baseXp = baseXp;
        this.target = target;
        baseHealth = target.getHealth();
    }

    @Override
    public void run()
    {
        int health = target.getHealth();
        int damage =  baseHealth - health;

        //May avoid negative xp, we don't know what other plugins do with the entity health
        if (damage <= 0)
            return;

        //Don't reward the player for overkills
        if (health < 0)
            damage += health;

        PP.addXP(skillType, (int) (damage * baseXp), player);
        Skills.XpCheckSkill(skillType, player);
    }
}
