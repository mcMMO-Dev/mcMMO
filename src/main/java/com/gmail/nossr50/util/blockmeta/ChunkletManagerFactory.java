package com.gmail.nossr50.util.blockmeta;

import com.gmail.nossr50.config.HiddenConfig;

public class ChunkletManagerFactory {
    public static ChunkletManager getChunkletManager() {
        HiddenConfig hConfig = HiddenMainConfig.getInstance();

        if (hConfig.getChunkletsEnabled()) {
            return new HashChunkletManager();
        }

        return new NullChunkletManager();
    }
}
