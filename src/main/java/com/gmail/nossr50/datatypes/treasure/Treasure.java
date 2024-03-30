package com.gmail.nossr50.datatypes.treasure;

import com.gmail.nossr50.util.random.Probability;
import com.google.common.base.Objects;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class Treasure {
    private int xp;
    private double dropChance;
    private @NotNull Probability dropProbability;
    private int dropLevel;
    private @NotNull ItemStack drop;

    public Treasure(ItemStack drop, int xp, double dropChance, int dropLevel) {
        this.drop = drop;
        this.xp = xp;
        this.dropChance = dropChance;
        this.dropProbability = Probability.ofPercent(dropChance);
        this.dropLevel = dropLevel;
    }

    public @NotNull Probability getDropProbability() {
        return dropProbability;
    }

    public @NotNull ItemStack getDrop() {
        return drop;
    }

    public void setDrop(@NotNull ItemStack drop) {
        this.drop = drop;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public double getDropChance() {
        return dropChance;
    }

    public void setDropChance(double dropChance) {
        this.dropChance = dropChance;
        this.dropProbability = Probability.ofPercent(dropChance);
    }

    public int getDropLevel() {
        return dropLevel;
    }

    public void setDropLevel(int dropLevel) {
        this.dropLevel = dropLevel;
    }

    @Override
    public String toString() {
        return "Treasure{" +
                "xp=" + xp +
                ", dropChance=" + dropChance +
                ", dropProbability=" + dropProbability +
                ", dropLevel=" + dropLevel +
                ", drop=" + drop +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Treasure treasure = (Treasure) o;
        return xp == treasure.xp && Double.compare(treasure.dropChance, dropChance) == 0 && dropLevel == treasure.dropLevel && Objects.equal(dropProbability, treasure.dropProbability) && Objects.equal(drop, treasure.drop);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(xp, dropChance, dropProbability, dropLevel, drop);
    }
}
