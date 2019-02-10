package com.gmail.nossr50.datatypes.block;

import com.gmail.nossr50.datatypes.Property;

/**
 * Represents a container of properties and values for a Block
 * @see Property
 * @see BlockState
 */
public class Block {

    private final String unlocalizedName; //The name before it is localized (english)
    private BlockState blockState;

    public Block(String unlocalizedName, BlockState blockState)
    {
        this.unlocalizedName = unlocalizedName;
        this.blockState      = blockState;
    }

    /**
     * Gets the name of this block in English
     * @return name of this block in English
     */
    public String getUnlocalizedName()
    {
        return unlocalizedName;
    }

    /**
     * Gets the state of this block
     * @return the state of this block
     */
    public BlockState getBlockState()
    {
        return blockState;
    }
}
