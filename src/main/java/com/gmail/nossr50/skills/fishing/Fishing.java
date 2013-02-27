package com.gmail.nossr50.skills.fishing;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.AdvancedConfig;

public final class Fishing {
    // The order of the values is extremely important, a few methods depend on it to work properly
    protected enum Tier {
        FIVE(5) {
            @Override public int getLevel() {return AdvancedConfig.getInstance().getFishingTierLevelsTier5();}
            @Override public int getShakeChance() {return AdvancedConfig.getInstance().getShakeChanceRank5();}
            @Override public int getVanillaXPBoostModifier() {return AdvancedConfig.getInstance().getFishingVanillaXPModifierRank5();}},
        FOUR(4) {
            @Override public int getLevel() {return AdvancedConfig.getInstance().getFishingTierLevelsTier4();}
            @Override public int getShakeChance() {return AdvancedConfig.getInstance().getShakeChanceRank4();}
            @Override public int getVanillaXPBoostModifier() {return AdvancedConfig.getInstance().getFishingVanillaXPModifierRank4();}},
        THREE(3) {
            @Override public int getLevel() {return AdvancedConfig.getInstance().getFishingTierLevelsTier3();}
            @Override public int getShakeChance() {return AdvancedConfig.getInstance().getShakeChanceRank3();}
            @Override public int getVanillaXPBoostModifier() {return AdvancedConfig.getInstance().getFishingVanillaXPModifierRank3();}},
        TWO(2) {
            @Override public int getLevel() {return AdvancedConfig.getInstance().getFishingTierLevelsTier2();}
            @Override public int getShakeChance() {return AdvancedConfig.getInstance().getShakeChanceRank2();}
            @Override public int getVanillaXPBoostModifier() {return AdvancedConfig.getInstance().getFishingVanillaXPModifierRank2();}},
        ONE(1) {
            @Override public int getLevel() {return AdvancedConfig.getInstance().getFishingTierLevelsTier1();}
            @Override public int getShakeChance() {return AdvancedConfig.getInstance().getShakeChanceRank1();}
            @Override public int getVanillaXPBoostModifier() {return AdvancedConfig.getInstance().getFishingVanillaXPModifierRank1();}};

        int numerical;

        private Tier(int numerical) {
            this.numerical = numerical;
        }

        public int toNumerical() {
            return numerical;
        }

        abstract protected int getLevel();
        abstract protected int getShakeChance();
        abstract protected int getVanillaXPBoostModifier();
    }

    // TODO: Get rid of that
    public static int fishermansDietRankLevel1 = AdvancedConfig.getInstance().getFishermanDietRankChange();
    public static int fishermansDietRankLevel2 = fishermansDietRankLevel1 * 2;
    public static int fishermansDietMaxLevel = fishermansDietRankLevel1 * 5;
    public static final double STORM_MODIFIER = 0.909; 

    private Fishing() {}

    /**
     * Begins Shake Mob ability
     *
     * @param player Player using the ability
     * @param mob Targeted mob
     * @param skillLevel Fishing level of the player
     */
    public static void beginShakeMob(Player player, LivingEntity mob, int skillLevel) {
        ShakeMob.process(player, mob, skillLevel);
    }
}
