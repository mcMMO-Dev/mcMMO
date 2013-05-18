package com.gmail.nossr50.skills.ranching;

import com.gmail.nossr50.config.AdvancedConfig;

public class Ranching {
    // The order of the values is extremely important, a few methods depend on it to work properly
    protected enum Tier {
        FIVE(5) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getRanchingVanillaXPBoostRank5Level(); }
            @Override public int getVanillaXPBoostModifier() { return AdvancedConfig.getInstance().getRanchingVanillaXPBoostRank5Multiplier(); }},
        FOUR(4) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getRanchingVanillaXPBoostRank4Level(); }
            @Override public int getVanillaXPBoostModifier() { return AdvancedConfig.getInstance().getRanchingVanillaXPBoostRank4Multiplier(); }},
        THREE(3) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getRanchingVanillaXPBoostRank3Level(); }
            @Override public int getVanillaXPBoostModifier() { return AdvancedConfig.getInstance().getRanchingVanillaXPBoostRank3Multiplier(); }},
        TWO(2) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getRanchingVanillaXPBoostRank2Level(); }
            @Override public int getVanillaXPBoostModifier() { return AdvancedConfig.getInstance().getRanchingVanillaXPBoostRank2Multiplier(); }},
        ONE(1) {
            @Override public int getLevel() { return AdvancedConfig.getInstance().getRanchingVanillaXPBoostRank1Level(); }
            @Override public int getVanillaXPBoostModifier() { return AdvancedConfig.getInstance().getRanchingVanillaXPBoostRank1Multiplier(); }};

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

    public static int    multipleBirthIncreaseLevel  = AdvancedConfig.getInstance().getMultipleBirthIncreasekLevel();
    public static int    multipleBirthMaxChance      = AdvancedConfig.getInstance().getMultipleBirthMaxChance();
    public static int    multipleBirthLitterModifier = AdvancedConfig.getInstance().getMultipleBirthLitterModifier();

    public static int    masterHerderIncreaseLevel   = AdvancedConfig.getInstance().getMultipleBirthIncreasekLevel();
    public static int    masterHerderMaxLevel        = AdvancedConfig.getInstance().getMasterHerderMaxLevel();
    public static int    masterHerderMinimumSeconds  = AdvancedConfig.getInstance().getMasterHerderMinimumSeconds();

    public static int    shearsMasteryMaxLevel       = AdvancedConfig.getInstance().getShearsMasteryMaxLevel();
    public static int    shearsMasteryMaxChance      = AdvancedConfig.getInstance().getShearsMasteryMaxChance();
    public static int    shearsMasteryMaxBonus       = AdvancedConfig.getInstance().getShearsMasteryMaxBonus();

    public static int    artisanButcherMaxLevel      = AdvancedConfig.getInstance().getArtisanButcherMaxLevel();
    public static int    artisanButcherMaxChance     = AdvancedConfig.getInstance().getArtisanButcherMaxChance();
    public static int    artisanButcherMaxBonus      = AdvancedConfig.getInstance().getArtisanButcherMaxBonus();

    public static int    carnivoresDietRankLevel1    = AdvancedConfig.getInstance().getCarnivoresDietRankChange();
    public static int    carnivoresDietRankLevel2    = carnivoresDietRankLevel1 * 2;
    public static int    carnivoresDietMaxLevel      = carnivoresDietRankLevel1 * 5;

    public static float shearExperience = 10;
    public static float breedExperience = 10;
}
