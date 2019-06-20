package com.gmail.nossr50.datatypes.items;

/**
 * Represents a property of an item which is used in strict matching
 * Typically this represents and NBT field and its value
 * All NBT entries have an ID and a value, this value can be one of many types
 */
public class ItemMatchProperty {

    final private Object propertyValue;
    final private String nbtID;

    public ItemMatchProperty(Object propertyValue, String nbtID) {
        this.propertyValue = propertyValue;
        this.nbtID = nbtID;
    }

    /**
     * The expected value for this NBT entry
     * @return the expected value for this NBT entry
     */
    public Object getPropertyValue() {
        return propertyValue;
    }

    /**
     * The ID (name) of this NBT entry
     * @return the ID (name) of this NBT entry
     */
    public String getID() {
        return nbtID;
    }
}
