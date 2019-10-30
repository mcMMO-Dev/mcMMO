package com.gmail.nossr50.core.nbt;

public class NBTLong implements NBTBase {

    private String key;
    private long value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public NBTType getNBTType() {
        return NBTType.LONG;
    }
}
