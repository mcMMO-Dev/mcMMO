package com.gmail.nossr50.core.data.blockmeta.chunkmeta;

import com.gmail.nossr50.core.config.HiddenConfig;

public class ChunkManagerFactory {
    public static ChunkManager getChunkManager() {
        HiddenConfig hConfig = HiddenConfig.getInstance();

        if (hConfig.getChunkletsEnabled()) {
            return new HashChunkManager();
        }

        return new NullChunkManager();
    }
}
