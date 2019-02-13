package com.gmail.nossr50.core.mcmmo.block;

import com.gmail.nossr50.core.mcmmo.world.World;

/**
 * Block positions are handled a bit differently than other locations
 */
public interface BlockPos {

    int getX();

    int getY();

    int getZ();

    /**
     * Gets the world for this block
     * @return this block's world
     */
    World getWorld();
}
