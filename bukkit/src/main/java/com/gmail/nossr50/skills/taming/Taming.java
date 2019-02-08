package com.gmail.nossr50.skills.taming;

import com.gmail.nossr50.config.AdvancedConfig;
import org.bukkit.EntityEffect;
import org.bukkit.entity.*;

public class Taming {
    public static double fastFoodServiceActivationChance = AdvancedConfig.getInstance().getFastFoodChance();

    public static int    goreBleedTicks    = 2; //Equivalent to rank 1 in Rupture
    public static double goreModifier      = AdvancedConfig.getInstance().getGoreModifier();

    public static double sharpenedClawsBonusDamage = AdvancedConfig.getInstance().getSharpenedClawsBonus();

    public static double shockProofModifier    = AdvancedConfig.getInstance().getShockProofModifier();

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
                return "Taming.Summon.Fail.Ocelot";

            case WOLF:
                return "Taming.Summon.Fail.Wolf";

            case HORSE:
                return "Taming.Summon.Fail.Horse";

            default:
                return "";
        }
    }
}