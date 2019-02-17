package com.gmail.nossr50.util.blockmeta.chunkmeta;

import com.gmail.nossr50.config.HiddenConfig;

public class ChunkManagerFactory {
    public static ChunkManager getChunkManager() {
        HiddenConfig hConfig = HiddenConfig.getInstance();

        if (hConfig.getChunkletsEnabled()) {
            return new HashChunkManager();
        }

        return new NullChunkManager();
    }
}
