package com.gmail.nossr50.core.adapters;

import com.gmail.nossr50.core.nbt.NBTBase;

public interface NBTAdapter {

    /**
     * Transform our NBT type representation to its implementation on the target platform
     * @param nbtBase target NBT type representation
     * @return platform specific implementation of our NBT Type
     */
    Object asNative(NBTBase nbtBase);
}
