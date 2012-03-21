package com.gmail.nossr50.skills;

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.party.Party;

public class Staves {
    

    /**
     * Fire a projectile on alt-fire from a staff.
     *
     * @param type The type of staff
     * @param attacker The attacking player
     * @param plugin mcMMO plugin instance
     */
    public static void altFire(Material type, Player attacker, mcMMO plugin) {
        Projectile projectile = null;

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
            break;
        }

        projectile.setMetadata("mcmmoFiredFromStaff", new FixedMetadataValue(plugin, true));
    }

    /**
     * Handle the effects of the Stick's projectile.
     *
     * @param target Entity hit by the projectile
     * @param shooter Player who fired the projectile
     */
    public static void eggEffect(LivingEntity target, Player shooter) {
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

    private static int durationCalulate() {
        //TODO: Calculate duration based off time held
        return 80;
    }

    private static int amplifierCalulate() {
        //TODO: Calculate amplifier based off skill level
        return 10;
    }
}
