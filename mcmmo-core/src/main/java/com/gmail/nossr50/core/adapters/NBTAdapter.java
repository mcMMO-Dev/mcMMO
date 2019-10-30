package com.gmail.nossr50.core.adapters;

import com.gmail.nossr50.core.nbt.NBTBase;

public interface NBTAdapter {
    Object asNative(NBTBase nbtBase);
}
