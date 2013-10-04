package com.gmail.nossr50.config.tiers;

public abstract class Rank {
    private int level;

    public Rank(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
