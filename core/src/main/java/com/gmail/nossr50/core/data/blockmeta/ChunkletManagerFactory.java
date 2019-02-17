package com.gmail.nossr50.core.data.blockmeta;

import com.gmail.nossr50.core.config.ChunkConversionOptions;

public class ChunkletManagerFactory {
    public static ChunkletManager getChunkletManager() {
        ChunkConversionOptions hConfig = ChunkConversionOptions.getInstance();

        if (hConfig.getChunkletsEnabled()) {
            return new HashChunkletManager();
        }

        return new NullChunkletManager();
    }
}
