package com.gmail.nossr50.datatypes.mods;

public class CustomBlock {
    private int xpGain;
    private boolean canDoubleDrop;
    private int smeltingXpGain;

    public CustomBlock(int xpGain, boolean canDoubleDrop, int smeltingXpGain) {
        this.xpGain = xpGain;
        this.canDoubleDrop = canDoubleDrop;
        this.smeltingXpGain = smeltingXpGain;
    }

    public int getXpGain() {
        return xpGain;
    }

    public boolean isDoubleDropEnabled() {
        return canDoubleDrop;
    }

    public int getSmeltingXpGain() {
        return smeltingXpGain;
    }
}
