package com.gmail.nossr50.core.nbt;

public class NBTShort implements NBTBase {

    private String key;
    private short value;

    @Override
    public NBTType getNBTType() {
        return NBTType.SHORT;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public short getValue() {
        return value;
    }

    public void setValue(short value) {
        this.value = value;
    }
}
