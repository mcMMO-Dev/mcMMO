package com.gmail.nossr50.skills;

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.party.Party;

public class Staves {

    public static void altFireCheck(EntityDamageByEntityEvent event) {
        LivingEntity defender = (LivingEntity) event.getEntity();
        Entity attacker = event.getDamager();

        if (attacker.hasMetadata("mcmmoFiredFromStaff")) {
            event.setDamage(0);

            Projectile projectile = (Projectile) attacker;
            Player shooter = (Player) projectile.getShooter();

            switch (attacker.getType()) {
            case EGG:
                eggEffect(defender, shooter);
                break;

            case FIREBALL:
                break;

            case SNOWBALL:
                if (defender.getType().equals(EntityType.PLAYER)) {
                    snowballEffect((Player) defender, shooter);
                }
                break;

            default:
                break;
            }
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

        switch (type) {
        case BLAZE_ROD:
            projectile = attacker.launchProjectile(Fireball.class);
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
     * Handle the effects of the Stick's projectile.
     *
     * @param target Entity hit by the projectile
     * @param shooter Player who fired the projectile
     */
    private static void eggEffect(LivingEntity target, Player shooter) {
        final int TICKS_PER_SECOND = 20;
        final int MAX_SLOW_DURATION_SECONDS = 240;
        final int MAX_SPEED_DURATION_SECONDS = 800;

        Collection<PotionEffect> potionEffects = target.getActivePotionEffects();

        int duration = durationCalulate();
        int amplifier = amplifierCalulate();
        PotionEffectType type;

        if (target instanceof Player && Party.getInstance().inSameParty((Player) target, shooter)) {
            type = PotionEffectType.SPEED;
        }
        else {
            type = PotionEffectType.SLOW;
        }

        for (PotionEffect effect : potionEffects) {
            if (effect.getType().equals(type)) {
                duration = duration + effect.getDuration();
                break;
            }
        }

        if (type.equals(PotionEffectType.SLOW) && duration > (TICKS_PER_SECOND * MAX_SLOW_DURATION_SECONDS)) {
            duration = (TICKS_PER_SECOND * MAX_SLOW_DURATION_SECONDS);
        }
        else if (type.equals(PotionEffectType.SPEED) && duration > (TICKS_PER_SECOND * MAX_SPEED_DURATION_SECONDS)) {
            duration = (TICKS_PER_SECOND * MAX_SPEED_DURATION_SECONDS);
        }

        target.addPotionEffect(new PotionEffect(type, duration, amplifier));

        if (type.equals(PotionEffectType.SLOW)) {
            shooter.sendMessage("Your enemy was slowed!"); //TODO: Use mcLocale
            if (target instanceof Player) {
                ((Player) target).sendMessage("You were slowed!"); //TODO: Use mcLocale
            }
        }
        else {
            shooter.sendMessage("Your ally's speed was boosted!"); //TODO: Use mcLocale
            ((Player) target).sendMessage("Your speed was boosted!"); //TODO: Use mcLocale
        }
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
