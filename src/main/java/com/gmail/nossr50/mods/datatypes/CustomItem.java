package com.gmail.nossr50.mods.datatypes;

public class CustomItem {
    protected int itemID;
    protected short durability;

    public CustomItem(int itemID, short durability) {
        this.itemID = itemID;
        this.durability = durability;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public short getDurability() {
        return durability;
    }

    public void setDurability(short durability) {
        this.durability = durability;
    }
}
