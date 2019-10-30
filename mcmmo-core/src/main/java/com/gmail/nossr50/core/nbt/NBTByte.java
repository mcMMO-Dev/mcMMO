package com.gmail.nossr50.core.nbt;

public class NBTByte implements NBTBase {

    private String key;
    private Byte value;

    public NBTByte(Byte value) {
        this.value = value;
    }

    @Override
    public NBTType getNBTType() {
        return NBTType.BYTE;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Byte getValue() {
        return value;
    }

    public void setValue(Byte value) {
        this.value = value;
    }

}
