package com.gmail.nossr50.core.nbt;

public class NBTDouble implements NBTBase {

    private String key;
    private double value;

    @Override
    public NBTType getNBTType() {
        return NBTType.DOUBLE;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
