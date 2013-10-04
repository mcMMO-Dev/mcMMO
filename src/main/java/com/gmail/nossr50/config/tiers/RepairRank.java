package com.gmail.nossr50.config.tiers;

import org.bukkit.configuration.ConfigurationSection;

public class RepairRank extends Rank {

    private double downgradeChance;
    private double keepEnchantsChance;

    public RepairRank(int level, double downgradeChance, double keepEnchantsChance) {
        super(level);
        this.downgradeChance = downgradeChance;
        this.keepEnchantsChance = keepEnchantsChance;
    }

    public RepairRank(ConfigurationSection section) {
        this(section.getInt("Level"), section.getDouble("DowngradeChance"), section.getDouble("KeepEnchantsChance"));
    }

    public double getDowngradeChance() {
        return downgradeChance;
    }

    public double getKeepEnchantsChance() {
        return keepEnchantsChance;
    }
}
