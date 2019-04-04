package com.gmail.nossr50.skills.taming;

import com.gmail.nossr50.config.AdvancedConfig;
import org.bukkit.EntityEffect;
import org.bukkit.entity.*;

public class Taming {

    private static Taming instance;
    private double fastFoodServiceActivationChance;
    private int goreBleedTicks;
    private double goreModifier;
    private double sharpenedClawsBonusDamage;
    private double shockProofModifier;
    private double thickFurModifier;

    public Taming() {
        fastFoodServiceActivationChance = AdvancedConfig.getInstance().getFastFoodChance();

        //Equivalent to rank 1 in Rupture
        goreBleedTicks = 2;
        goreModifier = AdvancedConfig.getInstance().getGoreModifier();

        sharpenedClawsBonusDamage = AdvancedConfig.getInstance().getSharpenedClawsBonus();

        shockProofModifier = AdvancedConfig.getInstance().getShockProofModifier();

        thickFurModifier = AdvancedConfig.getInstance().getThickFurModifier();
    }

    public static Taming getInstance() {
        if (instance == null)
            instance = new Taming();

        return instance;
    }

    public double getFastFoodServiceActivationChance() {
        return fastFoodServiceActivationChance;
    }

    public int getGoreBleedTicks() {
        return goreBleedTicks;
    }

    public double getGoreModifier() {
        return goreModifier;
    }

    public double getSharpenedClawsBonusDamage() {
        return sharpenedClawsBonusDamage;
    }

    public double getShockProofModifier() {
        return shockProofModifier;
    }

    public double getThickFurModifier() {
        return thickFurModifier;
    }

    public boolean canPreventDamage(Tameable pet, AnimalTamer owner) {
        return pet.isTamed() && owner instanceof Player && pet instanceof Wolf;
    }

    public double processThickFur(Wolf wolf, double damage) {
        wolf.playEffect(EntityEffect.WOLF_SHAKE);
        return damage / thickFurModifier;
    }

    public void processThickFurFire(Wolf wolf) {
        wolf.playEffect(EntityEffect.WOLF_SMOKE);
        wolf.setFireTicks(0);
    }

    public double processShockProof(Wolf wolf, double damage) {
        wolf.playEffect(EntityEffect.WOLF_SHAKE);
        return damage / shockProofModifier;
    }

    public void processHolyHound(Wolf wolf, double damage) {
        double modifiedHealth = Math.min(wolf.getHealth() + damage, wolf.getMaxHealth());

        wolf.setHealth(modifiedHealth);
        wolf.playEffect(EntityEffect.WOLF_HEARTS);
    }

    public String getCallOfTheWildFailureMessage(EntityType type) {
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