package com.gmail.nossr50.runnables;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Chunk;
import org.bukkit.World;

import com.gmail.nossr50.mcMMO;

public class ChunkletUnloader implements Runnable {
    private static Map<Chunk, Integer> unloadedChunks = new HashMap<Chunk, Integer>();
    private static int minimumInactiveTime = 60; //Should be a multiple of RUN_INTERVAL for best performance
    public static final int RUN_INTERVAL = 20;

    public static void addToList(Chunk chunk) {
        //Unfortunately we can't use Map.contains() because Chunks are always new objects
        //This method isn't efficient enough for me
        for (Chunk otherChunk : unloadedChunks.keySet()) {
            if (chunk.getX() == otherChunk.getX() && chunk.getZ() == otherChunk.getZ()) {
                return;
            }
        }

        unloadedChunks.put(chunk, 0);
    }

    public static void addToList(int cx, int cz, World world) {
        addToList(world.getChunkAt(cx, cz));
    }

    @Override
    public void run() {
        for (Iterator<Entry<Chunk, Integer>> it = unloadedChunks.entrySet().iterator() ; it.hasNext() ; ) {
            Entry<Chunk, Integer> entry = it.next();
            Chunk chunk = entry.getKey();

            if (!chunk.isLoaded()) {
                int inactiveTime = entry.getValue() + RUN_INTERVAL;

                //Chunklets are unloaded only if their chunk has been unloaded for minimumInactiveTime
                if (inactiveTime >= minimumInactiveTime) {
                    mcMMO.placeStore.unloadChunk(chunk.getX(), chunk.getZ(), chunk.getWorld());
                    it.remove();
                    continue;
                }

                unloadedChunks.put(entry.getKey(), inactiveTime);
            }
            else {
                //Just remove the entry if the chunk has been reloaded.
                it.remove();
            }
        }
    }
}
