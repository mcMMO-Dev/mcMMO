package com.gmail.nossr50.core.mcmmo;

/**
 * Custom Definitions for Block's Type
 * Unlike Bukkit's Material system, this matches a block by its state information
 * For example, an Oak Log in older versions of MC was simply a block with state information of being variant oak
 * To account for all the differences between version we have our own custom constants that will match based on platform
 */
public enum BlockType {
    //TODO: Fill in every block that has ever existed
    AIR,
    WATER;

    //TODO: Wire this up

    /**
     * Gets the config name for a block type
     * @return the config name for this block type
     */
    public String getConfigName()
    {

    }
}
