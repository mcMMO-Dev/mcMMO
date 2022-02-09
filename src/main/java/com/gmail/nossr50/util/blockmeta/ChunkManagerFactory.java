package com.gmail.nossr50.util.blockmeta;

import com.gmail.nossr50.config.PersistentDataConfig;
import org.jetbrains.annotations.NotNull;

public class ChunkManagerFactory {
    public static @NotNull ChunkManager getChunkManager() {

        if (PersistentDataConfig.getInstance().useBlockTracker()) {
            return new HashChunkManager();
        }

        return new NullChunkManager();
    }
}
