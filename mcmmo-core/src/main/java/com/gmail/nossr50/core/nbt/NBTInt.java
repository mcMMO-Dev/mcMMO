package com.gmail.nossr50.core.nbt;

public class NBTInt implements NBTBase {

    private String key;
    private int value;

    @Override
    public NBTType getNBTType() {
        return NBTType.INT;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
