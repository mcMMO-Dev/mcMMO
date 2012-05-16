package com.gmail.nossr50.skills.combat;

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
import com.gmail.nossr50.runnables.mcBleedTimer;

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

        projectile.setMetadata("mcmmoFiredFromStaff", new FixedMetadataValue(plugin, true));
    }

    /**
     * Handle the effects of the Bone's direct hit.
     *
     * @param defender The defending player
     * @param attacker The attacking player
     */
    public static void boneEffect(Player defender, Player attacker) {
        float xpLost = expLossCalculate();
        float xpGained = expGainCalculate();

        float attackerXP = attacker.getExp();
        float defenderXP = defender.getExp();
        int attackerLevel = attacker.getLevel();
        int defenderLevel = defender.getLevel();

        if (Party.getInstance().inSameParty(defender, attacker)) {

            //Drain XP
            if (attackerXP - xpLost < 0f) {
                if (attackerLevel != 0) {
                    attacker.setLevel(attackerLevel - 1);
                    attacker.setExp(1f - xpLost);
                    attacker.sendMessage("You transfered some XP to your ally!"); //TODO: Use mcLocale
                }
                else {
                    attacker.sendMessage("You don't have enough XP to transfer!"); //TODO: Use mcLocale
                    return;
                }
            }
            else {
                attacker.setExp(attackerXP - xpLost);
                attacker.sendMessage("You transfered some XP to your ally!"); //TODO: Use mcLocale
            }

            //Reward XP
            if (defenderXP + xpGained >= 1f) {
                defender.setLevel(defenderLevel + 1);
                defender.setExp(0f + xpGained);
                defender.sendMessage("You were given XP from your ally!"); //TODO: Use mcLocale
            }
            else {
                defender.setExp(defenderXP + xpGained);
                defender.sendMessage("You were given XP from your ally!"); //TODO: Use mcLocale
            }
        }
        else {

            //Drain XP
            if (defenderXP - xpLost < 0f) {
                if (defenderLevel != 0) {
                    defender.setLevel(defenderLevel - 1);
                    defender.setExp(1f - xpLost);
                    defender.sendMessage("You were drained of XP!"); //TODO: Use mcLocale
                }
                else {
                    attacker.sendMessage("Your enemy doesn't have enough XP to drain!"); //TODO: Use mcLocale
                    return;
                }
            }
            else {
                defender.setExp(defenderXP - xpLost);
                defender.sendMessage("You were drained of XP!"); //TODO: Use mcLocale
            }

            //Reward XP
            if (attackerXP + xpGained >= 1f) {
                attacker.setLevel(attackerLevel + 1);
                attacker.setExp(0f + xpGained);
                attacker.sendMessage("You gained XP from your enemy!"); //TODO: Use mcLocale
            }
            else {
                attacker.setExp(attackerXP + xpGained);
                attacker.sendMessage("You gained XP from your enemy!"); //TODO: Use mcLocale
            }
        }
    }

    /**
     * Handle the effects of the Blaze Rod's direct hit.
     *
     * @param target The defending entity
     * @param attacker The attacking player
     */
    public static void blazeRodEffect(LivingEntity target, Player attacker) {
        if (target instanceof Player && Party.getInstance().inSameParty((Player) target, attacker)) {
            target.setFireTicks(0);
            mcBleedTimer.remove(target);

            for (PotionEffect effect : target.getActivePotionEffects()) {
                if (effect.getType().equals(PotionEffectType.POISON) || effect.getType().equals(PotionEffectType.WEAKNESS) || effect.getType().equals(PotionEffectType.SLOW)) {
                    target.removePotionEffect(effect.getType());
                }
            }

            attacker.sendMessage("Your ally was cured of all status effects!"); //TODO: Use locale
            ((Player) target).sendMessage("You were cured of all status effects!"); //TODO: Use locale
        }
        else {
            if (target.getFireTicks() + fireTicksCalculate() > target.getMaxFireTicks()) {
                target.setFireTicks(target.getMaxFireTicks());
            }
            else {
                target.setFireTicks(target.getFireTicks() + fireTicksCalculate());
            }

            attacker.sendMessage("You set your enemy on fire!"); //TODO: Use locale

            if (target instanceof Player) {
                ((Player) target).sendMessage("You were set on fire!"); //TODO: Use locale
            }
        }
    }

    /**
     * Handle the effects of the Stick's direct hit.
     *
     * @param target The defending entity
     * @param attacker The attacking player
     */
    public static void stickEffect(LivingEntity target, Player attacker) {
        if (target instanceof Player && Party.getInstance().inSameParty((Player) target, attacker)) {
            Player defender = (Player) target;
            defender.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, amplifierCalulate()));

            /* Do we WANT to heal food along with health? */
            if (defender.getFoodLevel() + amplifierCalulate() > 20) {
                defender.setFoodLevel(20);
            }
            else {
                defender.setFoodLevel(defender.getFoodLevel() + amplifierCalulate());
            }

            attacker.sendMessage("Your ally was healed!"); //TODO: Use locale
            defender.sendMessage("You were healed!"); //TODO: Use locale
        }
        else {
            target.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 1, amplifierCalulate()));

            attacker.sendMessage("Your enemy was harmed!"); //TODO: Use locale
            if (target instanceof Player) {
                ((Player) target).sendMessage("You were harmed!"); //TODO: Use locale
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
            shooter.sendMessage("Your ally was strengthened!"); //TODO: Use mcLocale
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
        //TODO: Calculate exp lost based on skill level
        return 0.25f;
    }


    private static float expGainCalculate() {
        //TODO: Calculate exp gained based on skill level
        return 0.10f;
    }

    private static int fireTicksCalculate() {
        //TODO: Calculate based on skill level
        return 2;
    }
}
