package com.gmail.nossr50.core.nbt;

public class NBTIntArray implements NBTBase {

    private String key;
    private int[] values;

    @Override
    public NBTType getNBTType() {
        return NBTType.INT_ARRAY;
    }

    public int getLength() {
        return values.length;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int[] getValues() {
        return values;
    }

    public void setValues(int[] values) {
        this.values = values;
    }
}
