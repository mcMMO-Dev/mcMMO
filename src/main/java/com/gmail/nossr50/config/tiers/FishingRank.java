package com.gmail.nossr50.config.tiers;

import org.bukkit.configuration.ConfigurationSection;

public class FishingRank extends Rank {

    private double shakeChance;
    private int xpBoost;

    private FishingRank(int level, double shakeChance, int xpBoost) {
        super(level);
        this.shakeChance = shakeChance;
        this.xpBoost = xpBoost;
    }

    public FishingRank(ConfigurationSection section) {
        this(section.getInt("Level"), section.getDouble("ShakeChance"), section.getInt("VanillaXPBoost"));
    }

    public double getShakeChance() {
        return shakeChance;
    }

    public int getXpBoost() {
        return xpBoost;
    }
}
