package com.gmail.nossr50.skills.taming;

import org.bukkit.EntityEffect;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.locale.LocaleLoader;

public class Taming {
    public static int environmentallyAwareUnlockLevel = AdvancedConfig.getInstance().getEnviromentallyAwareUnlock();
    public static int holyHoundUnlockLevel            = AdvancedConfig.getInstance().getHolyHoundUnlock();

    public static int    fastFoodServiceUnlockLevel      = AdvancedConfig.getInstance().getFastFoodUnlock();
    public static double fastFoodServiceActivationChance = AdvancedConfig.getInstance().getFastFoodChance();

    public static int    goreBleedTicks    = AdvancedConfig.getInstance().getGoreBleedTicks();
    public static double goreModifier      = AdvancedConfig.getInstance().getGoreModifier();

    public static int    sharpenedClawsUnlockLevel = AdvancedConfig.getInstance().getSharpenedClawsUnlock();
    public static double sharpenedClawsBonusDamage = AdvancedConfig.getInstance().getSharpenedClawsBonus();

    public static int    shockProofUnlockLevel = AdvancedConfig.getInstance().getShockProofUnlock();
    public static double shockProofModifier    = AdvancedConfig.getInstance().getShockProofModifier();

    public static int    thickFurUnlockLevel = AdvancedConfig.getInstance().getThickFurUnlock();
    public static double thickFurModifier    = AdvancedConfig.getInstance().getThickFurModifier();

    public static boolean canPreventDamage(Tameable pet, AnimalTamer owner) {
        return pet.isTamed() && owner instanceof Player && pet instanceof Wolf;
    }

    public static double processThickFur(Wolf wolf, double damage) {
        wolf.playEffect(EntityEffect.WOLF_SHAKE);
        return damage / thickFurModifier;
    }

    public static void processThickFurFire(Wolf wolf) {
        wolf.playEffect(EntityEffect.WOLF_SMOKE);
        wolf.setFireTicks(0);
    }

    public static double processShockProof(Wolf wolf, double damage) {
        wolf.playEffect(EntityEffect.WOLF_SHAKE);
        return damage / shockProofModifier;
    }

    public static void processHolyHound(Wolf wolf, double damage) {
        double modifiedHealth = Math.min(wolf.getHealth() + damage, wolf.getMaxHealth());

        wolf.setHealth(modifiedHealth);
        wolf.playEffect(EntityEffect.WOLF_HEARTS);
    }

    protected static String getCallOfTheWildFailureMessage(EntityType type) {
        switch (type) {
            case OCELOT:
                return LocaleLoader.getString("Taming.Summon.Fail.Ocelot");

            case WOLF:
                return LocaleLoader.getString("Taming.Summon.Fail.Wolf");

            case HORSE:
                return LocaleLoader.getString("Taming.Summon.Fail.Horse");

            default:
                return "";
        }
    }
}
