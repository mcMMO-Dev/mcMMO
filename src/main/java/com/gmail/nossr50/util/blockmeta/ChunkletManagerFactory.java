package com.gmail.nossr50.util.blockmeta;

import com.gmail.nossr50.config.HiddenConfig;

public class ChunkletManagerFactory {
    public static ChunkletManager getChunkletManager() {
        HiddenConfig hConfig = HiddenConfig.getInstance();

        if (hConfig.getChunkletsEnabled()) {
            return new HashChunkletManager();
        }

        return new NullChunkletManager();
    }
}
