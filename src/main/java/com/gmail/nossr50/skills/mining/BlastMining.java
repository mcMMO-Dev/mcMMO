package com.gmail.nossr50.skills.mining;

import java.util.HashSet;

import org.bukkit.Material;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;

public class BlastMining {
    // The order of the values is extremely important, a few methods depend on it to work properly
    protected enum Tier {
        EIGHT(8) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getBlastMiningRank8(); }
            @Override public double getBlastRadiusModifier() { return AdvancedConfig.getInstance().getBlastRadiusModifierRank8(); }
            @Override public double getOreBonus() { return AdvancedConfig.getInstance().getOreBonusRank8(); }
            @Override public double getDebrisReduction() { return AdvancedConfig.getInstance().getDebrisReductionRank8(); }
            @Override public double getBlastDamageDecrease() { return AdvancedConfig.getInstance().getBlastDamageDecreaseRank8(); }
            @Override public int getDropMultiplier() { return AdvancedConfig.getInstance().getDropMultiplierRank8(); }},
        SEVEN(7) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getBlastMiningRank7(); }
            @Override public double getBlastRadiusModifier() { return AdvancedConfig.getInstance().getBlastRadiusModifierRank7(); }
            @Override public double getOreBonus() { return AdvancedConfig.getInstance().getOreBonusRank7(); }
            @Override public double getDebrisReduction() { return AdvancedConfig.getInstance().getDebrisReductionRank7(); }
            @Override public double getBlastDamageDecrease() { return AdvancedConfig.getInstance().getBlastDamageDecreaseRank7(); }
            @Override public int getDropMultiplier() { return AdvancedConfig.getInstance().getDropMultiplierRank7(); }},
        SIX(6) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getBlastMiningRank6(); }
            @Override public double getBlastRadiusModifier() { return AdvancedConfig.getInstance().getBlastRadiusModifierRank6(); }
            @Override public double getOreBonus() { return AdvancedConfig.getInstance().getOreBonusRank6(); }
            @Override public double getDebrisReduction() { return AdvancedConfig.getInstance().getDebrisReductionRank6(); }
            @Override public double getBlastDamageDecrease() { return AdvancedConfig.getInstance().getBlastDamageDecreaseRank6(); }
            @Override public int getDropMultiplier() { return AdvancedConfig.getInstance().getDropMultiplierRank6(); }},
        FIVE(5) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getBlastMiningRank5(); }
            @Override public double getBlastRadiusModifier() { return AdvancedConfig.getInstance().getBlastRadiusModifierRank5(); }
            @Override public double getOreBonus() { return AdvancedConfig.getInstance().getOreBonusRank5(); }
            @Override public double getDebrisReduction() { return AdvancedConfig.getInstance().getDebrisReductionRank5(); }
            @Override public double getBlastDamageDecrease() { return AdvancedConfig.getInstance().getBlastDamageDecreaseRank5(); }
            @Override public int getDropMultiplier() { return AdvancedConfig.getInstance().getDropMultiplierRank5(); }},
        FOUR(4) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getBlastMiningRank4(); }
            @Override public double getBlastRadiusModifier() { return AdvancedConfig.getInstance().getBlastRadiusModifierRank4(); }
            @Override public double getOreBonus() { return AdvancedConfig.getInstance().getOreBonusRank4(); }
            @Override public double getDebrisReduction() { return AdvancedConfig.getInstance().getDebrisReductionRank4(); }
            @Override public double getBlastDamageDecrease() { return AdvancedConfig.getInstance().getBlastDamageDecreaseRank4(); }
            @Override public int getDropMultiplier() { return AdvancedConfig.getInstance().getDropMultiplierRank4(); }},
        THREE(3) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getBlastMiningRank3(); }
            @Override public double getBlastRadiusModifier() { return AdvancedConfig.getInstance().getBlastRadiusModifierRank3(); }
            @Override public double getOreBonus() { return AdvancedConfig.getInstance().getOreBonusRank3(); }
            @Override public double getDebrisReduction() { return AdvancedConfig.getInstance().getDebrisReductionRank3(); }
            @Override public double getBlastDamageDecrease() { return AdvancedConfig.getInstance().getBlastDamageDecreaseRank3(); }
            @Override public int getDropMultiplier() { return AdvancedConfig.getInstance().getDropMultiplierRank3(); }},
        TWO(2) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getBlastMiningRank2(); }
            @Override public double getBlastRadiusModifier() { return AdvancedConfig.getInstance().getBlastRadiusModifierRank2(); }
            @Override public double getOreBonus() { return AdvancedConfig.getInstance().getOreBonusRank2(); }
            @Override public double getDebrisReduction() { return AdvancedConfig.getInstance().getDebrisReductionRank2(); }
            @Override public double getBlastDamageDecrease() { return AdvancedConfig.getInstance().getBlastDamageDecreaseRank2(); }
            @Override public int getDropMultiplier() { return AdvancedConfig.getInstance().getDropMultiplierRank2(); }},
        ONE(1) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getBlastMiningRank1(); }
            @Override public double getBlastRadiusModifier() { return AdvancedConfig.getInstance().getBlastRadiusModifierRank1(); }
            @Override public double getOreBonus() { return AdvancedConfig.getInstance().getOreBonusRank1(); }
            @Override public double getDebrisReduction() { return AdvancedConfig.getInstance().getDebrisReductionRank1(); }
            @Override public double getBlastDamageDecrease() { return AdvancedConfig.getInstance().getBlastDamageDecreaseRank1(); }
            @Override public int getDropMultiplier() { return AdvancedConfig.getInstance().getDropMultiplierRank1(); }};

        int numerical;

        private Tier(int numerical) {
            this.numerical = numerical;
        }

        public int toNumerical() {
            return numerical;
        }

        abstract protected int getLevel();
        abstract protected double getBlastRadiusModifier();
        abstract protected double getOreBonus();
        abstract protected double getDebrisReduction();
        abstract protected double getBlastDamageDecrease();
        abstract protected int getDropMultiplier();
    }

    public static int detonatorID = Config.getInstance().getDetonatorItemID();

    public final static int MAXIMUM_REMOTE_DETONATION_DISTANCE = 100;

    protected static HashSet<Byte> generateTransparentBlockList() {
        HashSet<Byte> transparentBlocks = new HashSet<Byte>();

        for (Material material : Material.values()) {
            if (material.isTransparent()) {
                transparentBlocks.add((byte) material.getId());
            }
        }

        return transparentBlocks;
    }
}
