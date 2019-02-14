package com.gmail.nossr50.core.data.blockmeta.chunkmeta;

import com.gmail.nossr50.core.config.ChunkConversionOptions;

public class ChunkManagerFactory {
    public static ChunkManager getChunkManager() {
        ChunkConversionOptions hConfig = ChunkConversionOptions.getInstance();

        if (hConfig.getChunkletsEnabled()) {
            return new HashChunkManager();
        }

        return new NullChunkManager();
    }
}
