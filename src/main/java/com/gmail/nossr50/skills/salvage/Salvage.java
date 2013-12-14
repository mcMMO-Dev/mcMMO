package com.gmail.nossr50.skills.salvage;

import org.bukkit.Material;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;

public class Salvage {
    // The order of the values is extremely important, a few methods depend on it to work properly
    public enum Tier {
        EIGHT(8),
        SEVEN(7),
        SIX(6),
        FIVE(5),
        FOUR(4),
        THREE(3),
        TWO(2),
        ONE(1);

        int numerical;

        private Tier(int numerical) {
            this.numerical = numerical;
        }

        public int toNumerical() {
            return numerical;
        }

        protected int getLevel() {
            return AdvancedConfig.getInstance().getArcaneSalvageRankLevel(this);
        }

        protected double getExtractFullEnchantChance() {
            return AdvancedConfig.getInstance().getArcaneSalvageExtractFullEnchantsChance(this);
        }

        protected double getExtractPartialEnchantChance() {
            return AdvancedConfig.getInstance().getArcaneSalvageExtractPartialEnchantsChance(this);
        }
    }

    public static Material anvilMaterial = Config.getInstance().getSalvageAnvilMaterial();

    public static int    salvageMaxPercentageLevel = AdvancedConfig.getInstance().getSalvageMaxPercentageLevel();
    public static double salvageMaxPercentage      = AdvancedConfig.getInstance().getSalvageMaxPercentage();

    public static int advancedSalvageUnlockLevel = AdvancedConfig.getInstance().getAdvancedSalvageUnlockLevel();

    public static boolean arcaneSalvageDowngrades  = AdvancedConfig.getInstance().getArcaneSalvageEnchantDowngradeEnabled();
    public static boolean arcaneSalvageEnchantLoss = AdvancedConfig.getInstance().getArcaneSalvageEnchantLossEnabled();

    protected static int calculateSalvageableAmount(short currentDurability, short maxDurability, int baseAmount) {
        double percentDamaged = (maxDurability <= 0) ? 1D : (double) (maxDurability - currentDurability) / maxDurability;

        return (int) Math.floor(baseAmount * percentDamaged);
    }
}
