package com.gmail.nossr50.util.blockmeta;

import com.gmail.nossr50.config.HiddenConfig;
import org.jetbrains.annotations.NotNull;

public class ChunkManagerFactory {
    public static @NotNull ChunkManager getChunkManager() {
        HiddenConfig hConfig = HiddenConfig.getInstance();

        if (hConfig.getChunkletsEnabled()) {
            return new HashChunkManager();
        }

        return new NullChunkManager();
    }
}
