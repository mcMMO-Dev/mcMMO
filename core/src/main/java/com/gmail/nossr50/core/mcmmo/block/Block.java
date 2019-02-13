package com.gmail.nossr50.core.mcmmo.block;

/**
 * Represents a container of properties and values for a Block
 *
 * @see Property
 * @see BlockState
 */
public interface Block extends BlockPos {
    /**
     * Get the state for this block
     * @return the block state
     */
    BlockState getBlockState();
}
