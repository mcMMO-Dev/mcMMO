package com.gmail.nossr50.core.datatypes;

import com.gmail.nossr50.core.mcmmo.world.Location;

public class LimitedSizeList {
    private final int size;
    public Location[] limitedSizeOrderedList;


    public LimitedSizeList(int size) {
        this.size = size;
        limitedSizeOrderedList = new Location[size];
    }

    /**
     * Adds objects to our limited size ordered list
     * New objects are added to the front
     *
     * @param newItem
     */
    public void add(Location newItem) {
        Location[] newList = new Location[size];

        for (int i = 0; i < size - 1; i++) {
            if (i != 0)
                newList[i] = limitedSizeOrderedList[i - 1];
            else
                newList[i] = newItem;
        }

        limitedSizeOrderedList = newList;
    }

    /**
     * Returns true if the object is anywhere in our list
     *
     * @param targetLoc the object to check for
     * @return true if the object is in our list
     */
    public boolean contains(Location targetLoc) {
        for (Location iter : limitedSizeOrderedList) {
            if (iter == null)
                continue;

            if (iter.getX() == targetLoc.getX()
                    && iter.getY() == targetLoc.getY()
                    && iter.getZ() == targetLoc.getZ())
                return true;
        }

        return false;
    }
}
