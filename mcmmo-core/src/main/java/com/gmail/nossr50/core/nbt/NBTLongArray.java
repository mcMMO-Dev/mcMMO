package com.gmail.nossr50.core.nbt;

public class NBTLongArray implements NBTBase {

    private String key;
    private long[] values;

    @Override
    public NBTType getNBTType() {
        return NBTType.LONG_ARRAY;
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

    public long[] getValues() {
        return values;
    }

    public void setValues(long[] values) {
        this.values = values;
    }
}
