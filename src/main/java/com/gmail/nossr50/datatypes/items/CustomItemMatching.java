package com.gmail.nossr50.datatypes.items;

public interface CustomItemMatching {

    /**
     * Determines whether or not an item matches this one
     * Behaviours for matching can vary based on the implementation
     * @param otherItem target item to compare itself to
     * @return true if this item matches the target item
     */
    boolean isMatch(MMOItem otherItem);

}
