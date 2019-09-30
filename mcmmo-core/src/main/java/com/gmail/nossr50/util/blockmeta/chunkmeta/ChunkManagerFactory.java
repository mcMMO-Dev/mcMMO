package com.gmail.nossr50.util.blockmeta.chunkmeta;

import com.gmail.nossr50.mcMMO;

public class ChunkManagerFactory {
    private final mcMMO pluginRef;

    public ChunkManagerFactory(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    public ChunkManager getChunkManager() {
        return new HashChunkManager(pluginRef);
    }
}
