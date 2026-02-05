package com.gmail.nossr50.util.blockmeta;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public interface ChunkManager extends UserBlockTracker {
    void closeAll();

    void chunkUnloaded(int cx, int cz, @NotNull World world);

    void unloadWorld(@NotNull World world);
}
