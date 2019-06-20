package com.gmail.nossr50.util.nbt;

/**
 * A simple class that acts as a container for raw NBT data
 * NBT data will be constructed from the raw NBT string using server internals
 * This type is mostly used to take a raw string of NBT and transform it into the NBT data type used by internals
 *  the transformed data can then be used to check NBT data of entities in Minecraft
 *
 *  One use of this type is as follows
 *  1) Read partial or complete NBT from the config file for an item
 *  2) Check an items NBT tree for this NBT data during runtime once transformed
 */
public class RawNBT<T> {
    private String nbtContents;
    private T nbtTree; //Will be constructed using server internals to make matching NBT easier

    public RawNBT(String nbtContents, T nbtTree) {
        this.nbtContents = nbtContents;
        NBTUtils
    }

    public String getNbtContents() {
        return nbtContents;
    }

    public void setNbtContents(String nbtContents) {
        this.nbtContents = nbtContents;
    }

}
