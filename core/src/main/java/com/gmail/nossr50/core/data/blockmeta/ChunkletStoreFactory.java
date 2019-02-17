package com.gmail.nossr50.core.data.blockmeta;

public class ChunkletStoreFactory {
    protected static ChunkletStore getChunkletStore() {
        // TODO: Add in loading from config what type of store we want.
        return new PrimitiveExChunkletStore();
    }
}
