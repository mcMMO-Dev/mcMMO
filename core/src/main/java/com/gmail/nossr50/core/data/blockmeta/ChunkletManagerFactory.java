package com.gmail.nossr50.core.data.blockmeta;

import com.gmail.nossr50.core.config.skills.HiddenConfig;

public class ChunkletManagerFactory {
    public static ChunkletManager getChunkletManager() {
        HiddenConfig hConfig = HiddenConfig.getInstance();

        if (hConfig.getChunkletsEnabled()) {
            return new HashChunkletManager();
        }

        return new NullChunkletManager();
    }
}
