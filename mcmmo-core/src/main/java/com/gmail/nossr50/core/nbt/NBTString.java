package com.gmail.nossr50.core.nbt;

public class NBTString implements NBTBase {

    private String key;
    private String value;

    @Override
    public NBTType getNBTType() {
        return NBTType.STRING;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
