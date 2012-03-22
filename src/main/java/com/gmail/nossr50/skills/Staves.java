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
        }
        else {
            shooter.sendMessage("Your ally's speed was boosted!"); //TODO: Use mcLocale
        }
    }

    /**
     * Handle the effects of the Bone's projectile.
     *
     * @param target Entity hit by the projectile
     * @param shooter Player who fired the projectile
     */
    private static void snowballEffect(Player target, Player shooter) {
        int expLost = expLossCalculate();
        int expGained = expGainCalculate();

        if (Party.getInstance().inSameParty(target, shooter)) {
            target.giveExp(expGained);
        }
        else {
            System.out.println(shooter.getLevel());
            shooter.setLevel(shooter.getLevel() - 2);
            System.out.println(shooter.getLevel());
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

    private static int expLossCalculate() {
        //TODO: Calculate exp lost based on time held
        return 1000;
    }

    private static int expGainCalculate() {
        //TODO: Calculate exp gained based on skill level
        return 500;
    }
}
