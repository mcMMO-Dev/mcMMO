package com.gmail.nossr50.datatypes.items;

import net.minecraft.server.v1_14_R1.NBTTagCompound;

import java.util.Objects;

/**
 * Represents a property of an item which is used in strict matching
 * Typically this represents and NBT field and its value
 * All NBT entries have an ID and a value, this value can be one of many types
 */
public class ItemMatchProperty {

    final private NBTTagCompound nbtData;
    final private String nbtID;

    public ItemMatchProperty(String nbtID, NBTTagCompound nbtData) {
        this.nbtData = nbtData;
        this.nbtID = nbtID;
    }

    /**
     * The expected value for this NBT
     * @return the expected value for this NBT entry
     */
    public NBTTagCompound getNbtData() {
        return nbtData;
    }

    /**
     * The ID (name) of this NBT entry
     * @return the ID (name) of this NBT entry
     */
    public String getID() {
        return nbtID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemMatchProperty)) return false;
        ItemMatchProperty that = (ItemMatchProperty) o;
        return getNbtData().equals(that.getNbtData()) &&
                nbtID.equals(that.nbtID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNbtData(), nbtID);
    }
}
