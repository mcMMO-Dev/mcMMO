package com.gmail.nossr50.runnables;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Chunk;
import org.bukkit.World;

import com.gmail.nossr50.datatypes.InactiveChunk;
import com.gmail.nossr50.mcMMO;

public class ChunkletUnloader implements Runnable {
    private static Map<String, InactiveChunk> unloadedChunks = new HashMap<String, InactiveChunk>();
    private static int minimumInactiveTime = 60; //Should be a multiple of RUN_INTERVAL for best performance
    public static final int RUN_INTERVAL = 20;

    public static void addToList(Chunk chunk) {
        if (chunk == null || chunk.getWorld() == null)
            return;

        String key = chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ();

        if (unloadedChunks.containsKey(key))
            return;

        unloadedChunks.put(key, new InactiveChunk(chunk));
    }

    public static void addToList(int cx, int cz, World world) {
        addToList(world.getChunkAt(cx, cz));
    }

    @Override
    public void run() {
        for (Iterator<Entry<String, InactiveChunk>> unloadedChunkIterator = unloadedChunks.entrySet().iterator() ; unloadedChunkIterator.hasNext() ; ) {
            Entry<String, InactiveChunk> entry = unloadedChunkIterator.next();

            if (entry.getKey() == null || entry.getValue() == null) {
                unloadedChunkIterator.remove();
                continue;
            }

            if (entry.getValue().chunk == null) {
                unloadedChunkIterator.remove();
                continue;
            }

            Chunk chunk = entry.getValue().chunk;

            if (!chunk.isLoaded()) {
                int inactiveTime = entry.getValue().inactiveTime + RUN_INTERVAL;

                //Chunklets are unloaded only if their chunk has been unloaded for minimumInactiveTime
                if (inactiveTime >= minimumInactiveTime) {
                    if (mcMMO.placeStore == null)
                        return;

                    mcMMO.placeStore.unloadChunk(chunk.getX(), chunk.getZ(), chunk.getWorld());
                    unloadedChunkIterator.remove();
                    continue;
                }

		entry.getValue().inactiveTime = inactiveTime;
            }
            else {
                //Just remove the entry if the chunk has been reloaded.
                unloadedChunkIterator.remove();
            }
        }
    }
}
