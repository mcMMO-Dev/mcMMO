package com.gmail.nossr50.datatypes.mods;

public class CustomItem {
    protected int itemID;

    public CustomItem(int itemID) {
        this.itemID = itemID;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }
}
