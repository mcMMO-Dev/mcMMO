package com.gmail.nossr50.core.nbt;

public class NBTFloat implements NBTBase {

    private String key;
    private float value;

    @Override
    public NBTType getNBTType() {
        return NBTType.FLOAT;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
