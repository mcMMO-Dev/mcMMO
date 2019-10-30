package com.gmail.nossr50.core.nbt;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class NBTCompound implements NBTBase {

    private String key;
    private Map<String, NBTBase> tagMap;

    public NBTCompound(String key) {
        tagMap = new LinkedHashMap<>();
    }

    @Override
    public NBTType getNBTType() {
        return NBTType.COMPOUND;
    }

    public NBTBase getTag(String key) {
        return tagMap.get(key);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void addNBT(String tagKey, NBTBase nbt) {
        tagMap.put(tagKey, nbt);
    }

    public Collection<NBTBase> getMapValues() {
        return tagMap.values();
    }

    public Set<String> getMapKeys() {
        return tagMap.keySet();
    }

    public int getMapSize() {
        return tagMap.size();
    }

    public void removeEntry(String tagKey) {
        tagMap.remove(tagKey);
    }
}

