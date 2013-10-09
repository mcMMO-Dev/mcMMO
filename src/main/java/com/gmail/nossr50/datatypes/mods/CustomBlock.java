package com.gmail.nossr50.datatypes.mods;

public class CustomBlock {
    private int xpGain;
    private boolean canDoubleDrop;

    public CustomBlock(int xpGain, boolean canDoubleDrop) {
        this.xpGain = xpGain;
        this.canDoubleDrop = canDoubleDrop;
    }

    public int getXpGain() {
        return xpGain;
    }

    public boolean isDoubleDropEnabled() {
        return canDoubleDrop;
    }
}
