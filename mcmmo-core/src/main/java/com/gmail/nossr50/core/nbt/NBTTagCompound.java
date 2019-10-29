package com.gmail.nossr50.core.nbt;

import java.util.Map;

public class NBTTagCompound implements NBTBase {

    private Map<String, NBTBase> tagMap;

    @Override
    public NBTType getNBTType() {
        return NBTType.COMPOUND;
    }

    public NBTBase getTag(String key) {
        return tagMap.get(key);
    }

}
