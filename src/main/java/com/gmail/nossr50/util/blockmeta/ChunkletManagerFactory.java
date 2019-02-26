package com.gmail.nossr50.util.blockmeta;

public class ChunkletManagerFactory {
    public static ChunkletManager getChunkletManager() {
        return new HashChunkletManager();
    }
}
