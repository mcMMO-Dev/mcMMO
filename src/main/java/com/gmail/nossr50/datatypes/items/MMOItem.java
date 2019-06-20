package com.gmail.nossr50.datatypes.items;

import com.gmail.nossr50.util.nbt.RawNBT;

/**
 * Represents the abstracted form of an item in Minecraft which can be used to construct an item implementation per platform
 * Return types of this object are not platform specific
 * Only requires a namespace key to be defined, all other properties will be default initialized
 */
public interface MMOItem<T> {

    /**
     * Gets the item implementation of this type for this object
     * @return the item implementation
     */
    T getItemImplementation();

    /**
     * Get the Minecraft fully qualified namespace (FQN) key for this item
     * Typically the FQN will read like this 'minecraft:name_here'
     * @return the fully qualified namespace key for this item
     */
    String getNamespaceKey();

    /**
     * Get the amount of this Item
     * Items in Minecraft are technically stacks of items with the minimum amount being 1 in most cases
     * The amount is used for matching purposes, and should default to 1 if undefined
     * @return the amount for this Item
     */
    int getItemAmount();

    /**
     * Get the RawNBT for this item if it has any
     * This can be null
     * @return the raw NBT if it exists, null otherwise
     */
    RawNBT getRawNBT();

}
