package com.gmail.nossr50.skills.repair;

import com.gmail.nossr50.config.AdvancedConfig;

public class ArcaneForging {
    // The order of the values is extremely important, a few methods depend on it to work properly
    protected enum Tier {
        FOUR(4) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getArcaneForgingRankLevels4(); }
            @Override public int getKeepEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingKeepEnchantsChanceRank4(); }
            @Override public int getDowngradeEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingDowngradeChanceRank4(); }},
        THREE(3) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getArcaneForgingRankLevels3(); }
            @Override public int getKeepEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingKeepEnchantsChanceRank3(); }
            @Override public int getDowngradeEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingDowngradeChanceRank3(); }},
        TWO(2) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getArcaneForgingRankLevels2(); }
            @Override public int getKeepEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingKeepEnchantsChanceRank2(); }
            @Override public int getDowngradeEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingDowngradeChanceRank2(); }},
        ONE(1) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getArcaneForgingRankLevels1(); }
            @Override public int getKeepEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingKeepEnchantsChanceRank1(); }
            @Override public int getDowngradeEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingDowngradeChanceRank1(); }};

        int numerical;

        private Tier(int numerical) {
            this.numerical = numerical;
        }

        public int toNumerical() {
            return numerical;
        }

        abstract protected int getLevel();
        abstract protected int getKeepEnchantChance();
        abstract protected int getDowngradeEnchantChance();
    }

    public static boolean arcaneForgingDowngrades  = AdvancedConfig.getInstance().getArcaneForgingDowngradeEnabled();
    public static boolean arcaneForgingEnchantLoss = AdvancedConfig.getInstance().getArcaneForgingEnchantLossEnabled();
}
