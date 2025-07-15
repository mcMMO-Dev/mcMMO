package com.gmail.nossr50.datatypes;

import com.google.common.collect.HashMultiset;
import java.util.LinkedList;
import org.bukkit.Location;

/**
 * This class works with the assumption that you only pass in Block Locations.  If locations have
 * differing pitch/yaw, the logic breaks
 */
public class BlockLocationHistory {
    private final LinkedList<Location> limitedSizeOrderedList = new LinkedList<>();
    private final HashMultiset<Location> lookup = HashMultiset.create();
    private final int maxSize;

    public BlockLocationHistory(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * Adds a block location to the history.  If the history memory would exceed the max size, it
     * will remove the least recently added block location
     *
     * @param newItem
     */
    public void add(Location newItem) {
        limitedSizeOrderedList.addFirst(newItem);
        lookup.add(newItem);
        if (limitedSizeOrderedList.size() > maxSize) {
            lookup.remove(limitedSizeOrderedList.removeLast());
        }
    }

    /**
     * Returns true if the block location is in the recorded history
     *
     * @param targetLoc the block location to search for
     * @return true if the block location is in the recorded history
     */
    public boolean contains(Location targetLoc) {
        return lookup.contains(targetLoc);
    }
}
