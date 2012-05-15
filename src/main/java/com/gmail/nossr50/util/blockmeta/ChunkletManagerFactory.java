package com.gmail.nossr50.util.blockmeta;

public class ChunkletManagerFactory {
	public static ChunkletManager getChunkletManager() {
		// TODO: Add in loading from config what type of manager we want.
		return new HashChunkletManager();
	}
}
