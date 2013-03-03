package com.gmail.nossr50.skills.taming;

import org.bukkit.EntityEffect;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;

public class Taming {
    public static int environmentallyAwareUnlockLevel = AdvancedConfig.getInstance().getEnviromentallyAwareUnlock();
    public static int holyHoundUnlockLevel            = AdvancedConfig.getInstance().getHolyHoundUnlock();

    public static int    fastFoodServiceUnlockLevel      = AdvancedConfig.getInstance().getFastFoodUnlock();
    public static double fastFoodServiceActivationChance = AdvancedConfig.getInstance().getFastFoodChance();

    public static int    goreBleedTicks    = AdvancedConfig.getInstance().getGoreBleedTicks();
    public static int    goreMaxBonusLevel = AdvancedConfig.getInstance().getGoreMaxBonusLevel();
    public static int    goreModifier      = AdvancedConfig.getInstance().getGoreModifier();
    public static double goreMaxChance     = AdvancedConfig.getInstance().getGoreChanceMax();

    public static int sharpenedClawsUnlockLevel = AdvancedConfig.getInstance().getSharpenedClawsUnlock();
    public static int sharpenedClawsBonusDamage = AdvancedConfig.getInstance().getSharpenedClawsBonus();

    public static int shockProofUnlockLevel = AdvancedConfig.getInstance().getShockProofUnlock();
    public static int shockProofModifier    = AdvancedConfig.getInstance().getShockProofModifier();

    public static int thickFurUnlockLevel = AdvancedConfig.getInstance().getThickFurUnlock();
    public static int thickFurModifier    = AdvancedConfig.getInstance().getThickFurModifier();

    public static int wolfXp   = Config.getInstance().getTamingXPWolf();
    public static int ocelotXp = Config.getInstance().getTamingXPOcelot();

    public static boolean canPreventDamage(Tameable pet, AnimalTamer owner) {
        return pet.isTamed() && owner instanceof Player && pet instanceof Wolf;
    }

    public static int processThickFur(Wolf wolf, int damage) {
        wolf.playEffect(EntityEffect.WOLF_SHAKE);
        return damage / thickFurModifier;
    }

    public static void processThickFurFire(Wolf wolf) {
        wolf.playEffect(EntityEffect.WOLF_SMOKE);
        wolf.setFireTicks(0);
    }

    public static int processShockProof(Wolf wolf, int damage) {
        wolf.playEffect(EntityEffect.WOLF_SHAKE);
        return damage / shockProofModifier;
    }

    /**
     * Apply the Sharpened Claws ability.
     *
     * @param event The event to modify
     */
    public static int sharpenedClaws(int damage) {
        return damage + Taming.sharpenedClawsBonusDamage;
    }

    public static void processHolyHound(Wolf wolf, int damage) {
        int modifiedHealth = Math.min(wolf.getHealth() + damage, wolf.getMaxHealth());

        wolf.setHealth(modifiedHealth);
        wolf.playEffect(EntityEffect.WOLF_HEARTS);
    }

    protected static String getCallOfTheWildFailureMessage(EntityType type) {
        switch (type) {
            case OCELOT:
                return LocaleLoader.getString("Taming.Summon.Fail.Ocelot");

            case WOLF:
                return LocaleLoader.getString("Taming.Summon.Fail.Wolf");

            default:
                return "";
        }
    }
}
