package com.gmail.nossr50.core.nbt;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

public class NBTCompound implements NBTBase {

    @NonNull
    private Map<String, NBTBase> tagMap;

    public NBTCompound() {
        tagMap = new LinkedHashMap<>();
    }

    @Override
    public NBTType getNBTType() {
        return NBTType.COMPOUND;
    }

    public NBTBase getTag(String key) {
        return tagMap.get(key);
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

    @Override
    public String toString() {
        return "NBTCompound{" +
                "tagMap=" + tagMap +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NBTCompound that = (NBTCompound) o;
        return tagMap.equals(that.tagMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagMap);
    }
}

