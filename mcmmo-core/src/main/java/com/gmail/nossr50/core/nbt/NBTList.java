package com.gmail.nossr50.core.nbt;

import java.util.List;

public class NBTList implements NBTBase {

    private String key;
    private List<? extends NBTBase> values;

    @Override
    public NBTType getNBTType() {
        return NBTType.LIST;
    }

    public int getLength() {
        return values.size();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<? extends NBTBase> getValues() {
        return values;
    }

    public void setValues(List<? extends NBTBase> values) {
        this.values = values;
    }
}
