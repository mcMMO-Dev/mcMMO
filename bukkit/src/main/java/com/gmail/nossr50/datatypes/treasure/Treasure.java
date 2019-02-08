package com.gmail.nossr50.datatypes.treasure;

import com.gmail.nossr50.config.Config;
import org.bukkit.inventory.ItemStack;

public abstract class Treasure {
    private int xp;
    private double dropChance;
    private int dropLevel;
    private ItemStack drop;

    public Treasure(ItemStack drop, int xp, double dropChance, int dropLevel) {
        this.drop = drop;
        this.xp = xp;
        this.dropChance = dropChance;
        this.dropLevel = dropLevel;
    }

    public ItemStack getDrop() {
        return drop;
    }

    public void setDrop(ItemStack drop) {
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

    public void setDropChance(Double dropChance) {
        this.dropChance = dropChance;
    }

    public int getDropLevel() {
        //If they are in retro mode all requirements are scaled up by 10
        if(Config.getInstance().getIsRetroMode())
            return dropLevel * 10;

        return dropLevel;
    }

    public void setDropLevel(int dropLevel) {
        this.dropLevel = dropLevel;
    }
}
