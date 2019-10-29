package com.gmail.nossr50.core.nbt;

public class NBTByteArray implements NBTBase {

    private String key;
    private byte[] values;

    @Override
    public NBTType getNBTType() {
        return NBTType.BYTE_ARRAY;
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

    public byte[] getValues() {
        return values;
    }

    public void setValues(byte[] values) {
        this.values = values;
    }
}
