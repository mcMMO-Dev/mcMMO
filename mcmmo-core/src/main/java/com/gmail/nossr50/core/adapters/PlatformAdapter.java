package com.gmail.nossr50.core.adapters;

public abstract class PlatformAdapter {

    private NBTAdapter nbtAdapter; //nbt

    public PlatformAdapter(NBTAdapter nbtAdapter) {
        this.nbtAdapter = nbtAdapter;
    }

    public NBTAdapter getNbtAdapter() {
        return nbtAdapter;
    }

}
