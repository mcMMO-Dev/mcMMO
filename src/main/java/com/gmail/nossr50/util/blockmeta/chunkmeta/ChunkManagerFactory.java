package com.gmail.nossr50.util.blockmeta.chunkmeta;

public class ChunkManagerFactory {
    public static ChunkManager getChunkManager() {
        return new HashChunkManager();
    }
}
