package com.gmail.nossr50.skills.repair;

import com.gmail.nossr50.config.AdvancedConfig;

public class ArcaneForging {
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
            return AdvancedConfig.getInstance().getArcaneForgingRankLevel(this);
        }

        protected double getKeepEnchantChance() {
            return AdvancedConfig.getInstance().getArcaneForgingKeepEnchantsChance(this);
        }

        protected double getDowngradeEnchantChance() {
            return AdvancedConfig.getInstance().getArcaneForgingDowngradeChance(this);
        }
    }

    public static boolean arcaneForgingDowngrades  = AdvancedConfig.getInstance().getArcaneForgingDowngradeEnabled();
    public static boolean arcaneForgingEnchantLoss = AdvancedConfig.getInstance().getArcaneForgingEnchantLossEnabled();
}
