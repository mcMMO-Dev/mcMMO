package com.gmail.nossr50.skills.repair;

import com.gmail.nossr50.config.AdvancedConfig;

public class ArcaneForging {
    // The order of the values is extremely important, a few methods depend on it to work properly
    protected enum Tier {
        EIGHT(8) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getArcaneForgingRankLevels8(); }
            @Override public double getKeepEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingKeepEnchantsChanceRank8(); }
            @Override public double getDowngradeEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingDowngradeChanceRank8(); }},
        SEVEN(7) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getArcaneForgingRankLevels7(); }
            @Override public double getKeepEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingKeepEnchantsChanceRank7(); }
            @Override public double getDowngradeEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingDowngradeChanceRank7(); }},
        SIX(6) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getArcaneForgingRankLevels6(); }
            @Override public double getKeepEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingKeepEnchantsChanceRank6(); }
            @Override public double getDowngradeEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingDowngradeChanceRank6(); }},
        FIVE(5) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getArcaneForgingRankLevels5(); }
            @Override public double getKeepEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingKeepEnchantsChanceRank5(); }
            @Override public double getDowngradeEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingDowngradeChanceRank5(); }},
        FOUR(4) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getArcaneForgingRankLevels4(); }
            @Override public double getKeepEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingKeepEnchantsChanceRank4(); }
            @Override public double getDowngradeEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingDowngradeChanceRank4(); }},
        THREE(3) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getArcaneForgingRankLevels3(); }
            @Override public double getKeepEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingKeepEnchantsChanceRank3(); }
            @Override public double getDowngradeEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingDowngradeChanceRank3(); }},
        TWO(2) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getArcaneForgingRankLevels2(); }
            @Override public double getKeepEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingKeepEnchantsChanceRank2(); }
            @Override public double getDowngradeEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingDowngradeChanceRank2(); }},
        ONE(1) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getArcaneForgingRankLevels1(); }
            @Override public double getKeepEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingKeepEnchantsChanceRank1(); }
            @Override public double getDowngradeEnchantChance() { return AdvancedConfig.getInstance().getArcaneForgingDowngradeChanceRank1(); }};

        int numerical;

        private Tier(int numerical) {
            this.numerical = numerical;
        }

        public int toNumerical() {
            return numerical;
        }

        abstract protected int getLevel();
        abstract protected double getKeepEnchantChance();
        abstract protected double getDowngradeEnchantChance();
    }

    public static boolean arcaneForgingDowngrades  = AdvancedConfig.getInstance().getArcaneForgingDowngradeEnabled();
    public static boolean arcaneForgingEnchantLoss = AdvancedConfig.getInstance().getArcaneForgingEnchantLossEnabled();
}
