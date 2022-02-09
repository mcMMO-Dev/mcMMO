package com.gmail.nossr50.util.blockmeta;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class NullChunkManager implements ChunkManager {

    @Override
    public void closeAll() {}

    @Override
    public void chunkUnloaded(int cx, int cz, @NotNull World world) {}

    @Override
    public void unloadWorld(@NotNull World world) {}

    @Override
    public boolean isTrue(@NotNull Block block) {
        return false;
    }

    @Override
    public boolean isTrue(@NotNull BlockState blockState) {
        return false;
    }

    @Override
    public void setTrue(@NotNull Block block) {}

    @Override
    public void setTrue(@NotNull BlockState blockState) {}

    @Override
    public void setFalse(@NotNull Block block) {}

    @Override
    public void setFalse(@NotNull BlockState blockState) {}
}
