package com.gmail.nossr50.datatypes.treasure;

import org.bukkit.inventory.ItemStack;

public class HylianTreasure extends Treasure {
    // bushes | flowers | pots
    // 00000001 - bushes    1
    // 00000010 - flowers   2
    // 00000100 - pots      4
    private byte dropsFrom = 0x0;

    public HylianTreasure(ItemStack drop, int xp, double dropChance, int dropLevel) {
        super(drop, xp, dropChance, dropLevel);
    }

    // Raw getters and setters
    public byte getDropsFrom() {
        return dropsFrom;
    }

    public void setDropsFrom(byte dropsFrom) {
        this.dropsFrom = dropsFrom;
    }

    // Getters
    public boolean getDropsFromBushes() {
        return getDropFromMask(1);
    }

    public boolean getDropsFromFlowers() {
        return getDropFromMask(2);
    }

    public boolean getDropsFromPots() {
        return getDropFromMask(4);
    }

    private boolean getDropFromMask(int mask) {
        return ((dropsFrom & mask) > 0) ? true : false;
    }

    // Setters
    public void setDropsFromBushes() {
        setDropFromMask(1);
    }

    public void setDropsFromFlowers() {
        setDropFromMask(2);
    }

    public void setDropsFromPots() {
        setDropFromMask(4);
    }

    private void setDropFromMask(int mask) {
        dropsFrom |= mask;
    }

    // Un-setters
    public void unsetDropsFromBushes() {
        unsetDropFromMask(1);
    }

    public void unsetDropsFromFlowers() {
        unsetDropFromMask(2);
    }

    public void unsetDropsFromPots() {
        unsetDropFromMask(4);
    }

    private void unsetDropFromMask(int mask) {
        dropsFrom &= ~mask;
    }
}
