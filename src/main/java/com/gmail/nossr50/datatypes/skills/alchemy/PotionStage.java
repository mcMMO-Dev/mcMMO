package com.gmail.nossr50.datatypes.skills.alchemy;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

public enum PotionStage {
    FIVE(5),
    FOUR(4),
    THREE(3),
    TWO(2),
    ONE(1);

    int numerical;

    private PotionStage(int numerical) {
        this.numerical = numerical;
    }

    public int toNumerical() {
        return numerical;
    }

    private static PotionStage getPotionStageNumerical(int numerical) {
        for (PotionStage potionStage : values()) {
            if (numerical >= potionStage.toNumerical()) {
                return potionStage;
            }
        }

        return ONE;
    }

    public static PotionStage getPotionStage(AlchemyPotion input, AlchemyPotion output) {
        PotionStage potionStage = getPotionStage(output);
        if (!isWaterBottle(input) && getPotionStage(input) == potionStage) {
            potionStage = PotionStage.FIVE;
        }

        return potionStage;
    }

    private static boolean isWaterBottle(AlchemyPotion input) {
        return input.getData().getType() == PotionType.WATER;
    }

    public static PotionStage getPotionStage(AlchemyPotion alchemyPotion) {
        PotionData data = alchemyPotion.getData();
        List<PotionEffect> effects = alchemyPotion.getEffects();

        int stage = 1;

        // Check if potion has an effect of any sort
        if (data.getType().getEffectType() != null || !effects.isEmpty()) {
            stage++;
        }

        // Check if potion has a glowstone dust amplifier
        // Else check if the potion has a custom effect with an amplifier added by mcMMO 
        if (data.isUpgraded()) {
            stage++;
        } else if(!effects.isEmpty()) {
            for (PotionEffect effect : effects){
                if(effect.getAmplifier() > 0){
                    stage++;
                    break;
                }
            }
        }

        // Check if potion has a redstone dust amplifier
        if (data.isExtended()) {
            stage++;
        }

        // Check if potion has a gunpowder amplifier
        if (alchemyPotion.getMaterial() == Material.SPLASH_POTION || alchemyPotion.getMaterial() == Material.LINGERING_POTION) {
            stage++;
        }

        return PotionStage.getPotionStageNumerical(stage);
    }
}
