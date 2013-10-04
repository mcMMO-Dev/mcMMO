package com.gmail.nossr50.config.tiers;

import org.bukkit.configuration.ConfigurationSection;

public class SmeltingRank extends Rank {

    private int xpMultiplier;

    public SmeltingRank(int level, int xpMultiplier) {
        super(level);
        this.xpMultiplier = xpMultiplier;
    }

    public SmeltingRank(ConfigurationSection section) {
        this(section.getInt("Level"), section.getInt("VanillaXPMultiplier"));
    }

    public int getXpMultiplier() {
        return xpMultiplier;
    }
}
