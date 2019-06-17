package com.gmail.nossr50.util.nbt;

public class RawNBT {
    String nbtContents;

    public RawNBT(String nbtContents) {
        this.nbtContents = nbtContents;
    }

    public String getNbtContents() {
        return nbtContents;
    }

    public void setNbtContents(String nbtContents) {
        this.nbtContents = nbtContents;
    }

}
