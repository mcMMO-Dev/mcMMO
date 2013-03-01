package com.gmail.nossr50.datatypes.treasure;

import org.bukkit.inventory.ItemStack;

public class ExcavationTreasure extends Treasure {
    // dirt | grass | sand | gravel | clay | mycel | soulsand
    // 00000001 - dirt      1
    // 00000010 - grass     2
    // 00000100 - sand      4
    // 00001000 - gravel    8
    // 00010000 - clay      16
    // 00100000 - mycel     32
    // 01000000 - soulsand  64
    private byte dropsFrom = 0x0;

    public ExcavationTreasure(ItemStack drop, int xp, double dropChance, int dropLevel) {
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
    public boolean getDropsFromDirt() {
        return getDropFromMask(1);
    }

    public boolean getDropsFromGrass() {
        return getDropFromMask(2);
    }

    public boolean getDropsFromSand() {
        return getDropFromMask(4);
    }

    public boolean getDropsFromGravel() {
        return getDropFromMask(8);
    }

    public boolean getDropsFromClay() {
        return getDropFromMask(16);
    }

    public boolean getDropsFromMycel() {
        return getDropFromMask(32);
    }

    public boolean getDropsFromSoulSand() {
        return getDropFromMask(64);
    }

    private boolean getDropFromMask(int mask) {
        return ((dropsFrom & mask) > 0) ? true : false;
    }

    // Setters
    public void setDropsFromDirt() {
        setDropFromMask(1);
    }

    public void setDropsFromGrass() {
        setDropFromMask(2);
    }

    public void setDropsFromSand() {
        setDropFromMask(4);
    }

    public void setDropsFromGravel() {
        setDropFromMask(8);
    }

    public void setDropsFromClay() {
        setDropFromMask(16);
    }

    public void setDropsFromMycel() {
        setDropFromMask(32);
    }

    public void setDropsFromSoulSand() {
        setDropFromMask(64);
    }

    private void setDropFromMask(int mask) {
        dropsFrom |= mask;
    }

    // Un-setters
    public void unsetDropsFromDirt() {
        unsetDropFromMask(1);
    }

    public void unsetDropsFromGrass() {
        unsetDropFromMask(2);
    }

    public void unsetDropsFromSand() {
        unsetDropFromMask(4);
    }

    public void unsetDropsFromGravel() {
        unsetDropFromMask(8);
    }

    public void unsetDropsFromClay() {
        unsetDropFromMask(16);
    }

    public void unsetDropsFromMycel() {
        unsetDropFromMask(32);
    }

    public void unsetDropsFromSoulSand() {
        unsetDropFromMask(64);
    }

    private void unsetDropFromMask(int mask) {
        dropsFrom &= ~mask;
    }
}
