package com.gmail.nossr50.datatypes.skills.alchemy;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

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
        if (getPotionStage(input) == potionStage) {
            potionStage = PotionStage.FIVE;
        }

        return potionStage;
    }

    public static PotionStage getPotionStage(AlchemyPotion alchemyPotion) {
        Potion potion = Potion.fromItemStack(new ItemStack(Material.POTION, 1, alchemyPotion.getDataValue()));

        int stage = 1;

        // Check if potion isn't awkward or mundane
        if (potion.getType() != null) {
            stage++;
        }

        // Check if potion has a glowstone dust amplifier
        if (potion.getLevel() > 1) {
            stage++;
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
