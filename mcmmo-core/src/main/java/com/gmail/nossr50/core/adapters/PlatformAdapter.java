package com.gmail.nossr50.core.adapters;

public abstract class PlatformAdapter {

    private NBTAdapter nbtAdapter; //nbt

    public PlatformAdapter(NBTAdapter nbtAdapter) {
        this.nbtAdapter = nbtAdapter;
    }

    /**
     * Get the NBT Adapter for this platform
     * @return the platform's NBT adapter
     */
    public NBTAdapter getNbtAdapter() {
        return nbtAdapter;
    }

}
