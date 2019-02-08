package com.gmail.nossr50.util.blockmeta.chunkmeta;

import org.bukkit.World;

public class ChunkStoreFactory {
    protected static ChunkStore getChunkStore(World world, int x, int z) {
        // TODO: Add in loading from config what type of store we want.
        return new PrimitiveChunkStore(world, x, z);
    }
}
