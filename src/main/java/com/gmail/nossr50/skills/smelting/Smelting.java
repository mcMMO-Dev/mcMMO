package com.gmail.nossr50.skills.smelting;

import org.bukkit.Material;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.skills.SkillType;

public class Smelting {
    // The order of the values is extremely important, a few methods depend on it to work properly
    protected enum Tier {
        FIVE(5) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getSmeltingVanillaXPBoostRank5Level(); }
            @Override public int getVanillaXPBoostModifier() { return AdvancedConfig.getInstance().getSmeltingVanillaXPBoostRank5Multiplier(); }},
        FOUR(4) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getSmeltingVanillaXPBoostRank4Level(); }
            @Override public int getVanillaXPBoostModifier() { return AdvancedConfig.getInstance().getSmeltingVanillaXPBoostRank4Multiplier(); }},
        THREE(3) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getSmeltingVanillaXPBoostRank3Level(); }
            @Override public int getVanillaXPBoostModifier() { return AdvancedConfig.getInstance().getSmeltingVanillaXPBoostRank3Multiplier(); }},
        TWO(2) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getSmeltingVanillaXPBoostRank2Level(); }
            @Override public int getVanillaXPBoostModifier() { return AdvancedConfig.getInstance().getSmeltingVanillaXPBoostRank2Multiplier(); }},
        ONE(1) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getSmeltingVanillaXPBoostRank1Level(); }
            @Override public int getVanillaXPBoostModifier() { return AdvancedConfig.getInstance().getSmeltingVanillaXPBoostRank1Multiplier(); }};

        int numerical;

        private Tier(int numerical) {
            this.numerical = numerical;
        }

        public int toNumerical() {
            return numerical;
        }

        abstract protected int getLevel();
        abstract protected int getVanillaXPBoostModifier();
    }

    public static int    burnModifierMaxLevel = AdvancedConfig.getInstance().getBurnModifierMaxLevel();
    public static double burnTimeMultiplier   = AdvancedConfig.getInstance().getBurnTimeMultiplier();

    public static int    secondSmeltMaxLevel  = AdvancedConfig.getInstance().getSecondSmeltMaxLevel();
    public static double secondSmeltMaxChance = AdvancedConfig.getInstance().getSecondSmeltMaxChance();

    public static int    fluxMiningUnlockLevel = AdvancedConfig.getInstance().getFluxMiningUnlockLevel();
    public static double fluxMiningChance      = AdvancedConfig.getInstance().getFluxMiningChance();

    protected static int getResourceXp(Material resourceType) {
        int xp = Config.getInstance().getXp(SkillType.SMELTING, resourceType);

        if (resourceType == Material.GLOWING_REDSTONE_ORE) {
            xp = Config.getInstance().getXp(SkillType.SMELTING, Material.REDSTONE_ORE);
        }

        return xp;
    }
}
