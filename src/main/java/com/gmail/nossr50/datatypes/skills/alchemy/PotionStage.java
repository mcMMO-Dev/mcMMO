package com.gmail.nossr50.datatypes.skills.alchemy;

import java.util.List;

import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

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
        return input.getDataValue() == 0;
    }

    public static PotionStage getPotionStage(AlchemyPotion alchemyPotion) {
        Potion potion = alchemyPotion.toPotion(1);
        List<PotionEffect> effects = alchemyPotion.getEffects();

        int stage = 1;

        // Check if potion isn't awkward or mundane
        // Check for custom effects added by mcMMO
        if (potion.getType() != null || !effects.isEmpty()) {
            stage++;
        }

        // Check if potion has a glowstone dust amplifier
        // Else check if the potion has a custom effect with an amplifier added by mcMMO 
        if (potion.getLevel() > 1) {
            stage++;
        }else if(!effects.isEmpty()){
            for (PotionEffect effect : effects){
                if(effect.getAmplifier() > 0){
                    stage++;
                    break;
                }
            }
        }

        // Check if potion has a redstone dust amplifier
        if (potion.hasExtendedDuration()) {
            stage++;
        }

        // Check if potion has a gunpowder amplifier
        if (potion.isSplash()) {
            stage++;
        }

        return PotionStage.getPotionStageNumerical(stage);
    }
}
