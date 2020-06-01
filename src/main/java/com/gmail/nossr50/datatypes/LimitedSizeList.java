package com.gmail.nossr50.datatypes;


import org.bukkit.Location;

public class LimitedSizeList {
    public Location[] limitedSizeOrderedList;
    private final int size;


    public LimitedSizeList(int size)
    {
        this.size = size;
        limitedSizeOrderedList = new Location[size];
    }

    /**
     * Adds objects to our limited size ordered list
     * New objects are added to the front
     * @param newItem
     */
    public void add(Location newItem)
    {
        Location[] newList = new Location[size];
        newList[0] = newItem;
        System.arraycopy(limitedSizeOrderedList, 0, newList, 1, size-1);
        limitedSizeOrderedList = newList;
    }

    /**
     * Returns true if the object is anywhere in our list
     * @param targetLoc the object to check for
     * @return true if the object is in our list
     */
    public boolean contains(Location targetLoc)
    {
        for(Location iter : limitedSizeOrderedList)
        {
            if(iter == null)
                continue;

            if(iter.getX() == targetLoc.getX()
                    && iter.getY() == targetLoc.getY()
                    && iter.getZ() == targetLoc.getZ())
                return true;
        }

        return false;
    }
}
