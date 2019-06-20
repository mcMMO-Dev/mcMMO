package com.gmail.nossr50.datatypes.nbt;

import com.gmail.nossr50.util.nbt.RawNBT;

/**
 * Many things in Minecraft make use of the NBT System
 * You can read about the NBT System here - https://wiki.vg/NBT
 *
 * Types that support NBT will implement this interface
 */
public interface NBTHolder {

    /**
     * Get the RawNBT for this object
     * @return the RawNBT for this object
     */
    RawNBT getRawNBT();

    /**
     * Determine whether or not this object has the specific NBT entry and matching value
     * @param id the ID fof the NBT entry
     * @param value the value of the NBT entry
     * @return returns true if the NBT of id exists and its value matches
     */
    boolean hasNBTValue(String id, Object value);
}
