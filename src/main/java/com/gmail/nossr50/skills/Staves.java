package com.gmail.nossr50.skills;

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.party.Party;

public class Staves {

    /**
     * Handle the projectile effects from the staves.
     *
     * @param event The event to modify
     */
    public static void altFireCheck(EntityDamageByEntityEvent event) {
        LivingEntity defender = (LivingEntity) event.getEntity();
        Entity attacker = event.getDamager();

        if (attacker.hasMetadata("mcmmoFiredFromStaff")) {
            event.setDamage(0);

            Projectile projectile = (Projectile) attacker;
            Player shooter = (Player) projectile.getShooter();
            EntityType type = attacker.getType();

            projectileEffects(defender, shooter, type);
        }
    }

    /**
     * Fire a projectile on alt-fire from a staff.
     *
     * @param type The type of staff
     * @param attacker The attacking player
     * @param plugin mcMMO plugin instance
     */
    public static void altFire(Material type, Player attacker, mcMMO plugin) {
        Projectile projectile;

        if (attacker.getItemInHand().getAmount() > 1) {
            return;
        }

        switch (type) {
        case BLAZE_ROD:
            projectile = attacker.launchProjectile(SmallFireball.class);
            ((SmallFireball) projectile).setIsIncendiary(false);
            ((SmallFireball) projectile).setYield(0f);
            break;

        case BONE:
            projectile = attacker.launchProjectile(Snowball.class);
            break;

        case STICK:
            projectile = attacker.launchProjectile(Egg.class);
            break;

        default:
            return;
        }

        plugin.projectileTracker.add(projectile);
        projectile.setMetadata("mcmmoFiredFromStaff", new FixedMetadataValue(plugin, true));
    }

    /**
     * Handle the effects of the Bone's projectile.
     *
     * @param target Entity hit by the projectile
     * @param shooter Player who fired the projectile
     */
    private static void snowballEffect(Player target, Player shooter) {
        float xpLost = expLossCalculate();
        float xpGained = expGainCalculate();

        float shooterXP = shooter.getExp();
        float targetXP = target.getExp();
        int shooterLevel = shooter.getLevel();
        int targetLevel = target.getLevel();

        if (Party.getInstance().inSameParty(target, shooter)) {

            //Drain XP
            if (shooterXP - xpLost < 0f) {
                if (shooterLevel != 0) {
                    shooter.setLevel(shooterLevel - 1);
                    shooter.setExp(1f - xpLost);
                    shooter.sendMessage("You transfered some XP to your ally!"); //TODO: Use mcLocale
                }
                else {
                    shooter.sendMessage("You don't have enough XP to transfer!"); //TODO: Use mcLocale
                    return;
                }
            }
            else {
                shooter.setExp(shooterXP - xpLost);
                shooter.sendMessage("You transfered some XP to your ally!"); //TODO: Use mcLocale
            }

            //Reward XP
            if (targetXP + xpGained >= 1f) {
                target.setLevel(targetLevel + 1);
                target.setExp(0f + xpGained);
                target.sendMessage("You were given XP from your ally!"); //TODO: Use mcLocale
            }
            else {
                target.setExp(targetXP + xpGained);
                target.sendMessage("You were given XP from your ally!"); //TODO: Use mcLocale
            }
        }
        else {

            //Drain XP
            if (targetXP - xpLost < 0f) {
                if (targetLevel != 0) {
                    target.setLevel(targetLevel - 1);
                    target.setExp(1f - xpLost);
                    target.sendMessage("You were drained of XP!"); //TODO: Use mcLocale
                }
                else {
                    shooter.sendMessage("Your enemy doesn't have enough XP to drain!"); //TODO: Use mcLocale
                    return;
                }
            }
            else {
                target.setExp(targetXP - xpLost);
                target.sendMessage("You were drained of XP!"); //TODO: Use mcLocale
            }

            //Reward XP
            if (shooterXP + xpGained >= 1f) {
                shooter.setLevel(shooterLevel + 1);
                shooter.setExp(0f + xpGained);
                shooter.sendMessage("You gained XP from your enemy!"); //TODO: Use mcLocale
            }
            else {
                shooter.setExp(shooterXP + xpGained);
                shooter.sendMessage("You gained XP from your enemy!"); //TODO: Use mcLocale
            }
        }
    }

    /**
     * Handle the special effects from the staff projectiles.
     *
     * @param defender The defending entity
     * @param shooter The attacking player
     * @param type The type of projectile
     */
    private static void projectileEffects(LivingEntity target, Player shooter, EntityType projectile) {
        final int TICKS_PER_SECOND = 20;
        final int MAX_SLOW_DURATION_SECONDS = 240;
        final int MAX_SPEED_DURATION_SECONDS = 480;
        final int MAX_WEAKNESS_DURATION_SECONDS = 240;
        final int MAX_STRENGTH_DURATION_SECONDS = 480;
        final int MAX_POISON_DURATION_SECONDS = 120;
        final int MAX_REGEN_DURATION_SECONDS = 120;

        Collection<PotionEffect> potionEffects = target.getActivePotionEffects();

        int duration = durationCalulate();
        int amplifier = amplifierCalulate();
        PotionEffectType potionType = null;

        if (target instanceof Player && Party.getInstance().inSameParty((Player) target, shooter)) {
            switch (projectile) {
            case EGG:
                potionType = PotionEffectType.REGENERATION;
                break;

            case SMALL_FIREBALL:
                potionType = PotionEffectType.SPEED;
                break;

            case SNOWBALL:
                potionType = PotionEffectType.INCREASE_DAMAGE;
                break;

            default:
                break;
            }
        }
        else {
            switch (projectile) {
            case EGG:
                potionType = PotionEffectType.POISON;
                break;

            case SMALL_FIREBALL:
                potionType = PotionEffectType.SLOW;
                break;

            case SNOWBALL:
                potionType = PotionEffectType.WEAKNESS;
                break;

            default:
                break;
            }
        }

        for (PotionEffect effect : potionEffects) {
            if (effect.getType().equals(potionType)) {
                duration = duration + effect.getDuration();
                break;
            }
        }

        if (potionType.equals(PotionEffectType.SLOW) && duration > (TICKS_PER_SECOND * MAX_SLOW_DURATION_SECONDS)) {
            duration = (TICKS_PER_SECOND * MAX_SLOW_DURATION_SECONDS);
        }
        else if (potionType.equals(PotionEffectType.SPEED) && duration > (TICKS_PER_SECOND * MAX_SPEED_DURATION_SECONDS)) {
            duration = (TICKS_PER_SECOND * MAX_SPEED_DURATION_SECONDS);
        }
        else if (potionType.equals(PotionEffectType.WEAKNESS) && duration > (TICKS_PER_SECOND * MAX_WEAKNESS_DURATION_SECONDS)) {
            duration = (TICKS_PER_SECOND * MAX_WEAKNESS_DURATION_SECONDS);
        }
        else if (potionType.equals(PotionEffectType.INCREASE_DAMAGE) && duration > (TICKS_PER_SECOND * MAX_STRENGTH_DURATION_SECONDS)) {
            duration = (TICKS_PER_SECOND * MAX_STRENGTH_DURATION_SECONDS);
        }
        else if (potionType.equals(PotionEffectType.POISON) && duration > (TICKS_PER_SECOND * MAX_POISON_DURATION_SECONDS)) {
            duration = (TICKS_PER_SECOND * MAX_POISON_DURATION_SECONDS);
        }
        else if (potionType.equals(PotionEffectType.REGENERATION) && duration > (TICKS_PER_SECOND * MAX_REGEN_DURATION_SECONDS)) {
            duration = (TICKS_PER_SECOND * MAX_REGEN_DURATION_SECONDS);
        }

        target.addPotionEffect(new PotionEffect(potionType, duration, amplifier));

        if (potionType.equals(PotionEffectType.SLOW)) {
            shooter.sendMessage("Your enemy was slowed!"); //TODO: Use mcLocale
            if (target instanceof Player) {
                ((Player) target).sendMessage("You were slowed!"); //TODO: Use mcLocale
            }
        }
        else if (potionType.equals(PotionEffectType.SPEED)) {
            shooter.sendMessage("Your ally's speed was boosted!"); //TODO: Use mcLocale
            ((Player) target).sendMessage("Your speed was boosted!"); //TODO: Use mcLocale
        }
        else if (potionType.equals(PotionEffectType.WEAKNESS)) {
            shooter.sendMessage("Your enemy was weakened!"); //TODO: Use mcLocale
            if (target instanceof Player) {
                ((Player) target).sendMessage("You were weakened!"); //TODO: Use mcLocale
            }
        }
        else if (potionType.equals(PotionEffectType.INCREASE_DAMAGE)) {
            shooter.sendMessage("Your ally's was strengthened!"); //TODO: Use mcLocale
            ((Player) target).sendMessage("You were strengthened!"); //TODO: Use mcLocale
        }
        else if (potionType.equals(PotionEffectType.POISON)) {
            shooter.sendMessage("Your enemy was poisoned!"); //TODO: Use mcLocale
            if (target instanceof Player) {
                ((Player) target).sendMessage("You were poisoned!"); //TODO: Use mcLocale
            }
        }
        else if (potionType.equals(PotionEffectType.REGENERATION)) {
            shooter.sendMessage("Your ally's began regaining health!"); //TODO: Use mcLocale
            ((Player) target).sendMessage("You began regaining health!"); //TODO: Use mcLocale
        }
    }

    private static int durationCalulate() {
        //TODO: Calculate duration based off time held
        return 80;
    }

    private static int amplifierCalulate() {
        //TODO: Calculate amplifier based off skill level
        return 10;
    }

    private static float expLossCalculate() {
        //TODO: Calculate exp lost based on time held
        return 0.25f;
    }

    private static float expGainCalculate() {
        //TODO: Calculate exp gained based on skill level
        return 0.10f;
    }
}
