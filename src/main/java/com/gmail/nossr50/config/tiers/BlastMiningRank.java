package com.gmail.nossr50.config.tiers;

import org.bukkit.configuration.ConfigurationSection;

public class BlastMiningRank extends Rank {

    private double blastDamageDecrease;
    private double oreBonus;
    private double debrisReduction;
    private int dropMultiplier;
    private double blastRadiusModifier;

    public BlastMiningRank(int level, double blastDamageDecrease, double oreBonus, double debrisReduction, int dropMultiplier, double blastRadiusModifier) {
        super(level);
        this.blastDamageDecrease = blastDamageDecrease;
        this.oreBonus = oreBonus;
        this.debrisReduction = debrisReduction;
        this.dropMultiplier = dropMultiplier;
        this.blastRadiusModifier = blastRadiusModifier;
    }

    public BlastMiningRank(ConfigurationSection section) {
        this(section.getInt("Level"), section.getDouble("BlastDamageDecrease"), section.getDouble("OreBonus"), section.getDouble("DebrisReduction"), section.getInt("DropMultiplier"), section.getDouble("BlastRadiusModifier"));
    }

    public double getBlastDamageDecrease() {
        return blastDamageDecrease;
    }

    public double getOreBonus() {
        return oreBonus;
    }

    public double getDebrisReduction() {
        return debrisReduction;
    }

    public int getDropMultiplier() {
        return dropMultiplier;
    }

    public double getBlastRadiusModifier() {
        return blastRadiusModifier;
    }
}
