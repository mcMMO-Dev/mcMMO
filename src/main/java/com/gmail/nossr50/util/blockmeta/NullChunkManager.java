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
    public boolean isIneligible(@NotNull Block block) {
        return false;
    }

    @Override
    public boolean isIneligible(@NotNull BlockState blockState) {
        return false;
    }

    @Override
    public boolean isEligible(@NotNull Block block) {
        return true;
    }

    @Override
    public boolean isEligible(@NotNull BlockState blockState) {
        return true;
    }

    @Override
    public void setIneligible(@NotNull Block block) {}

    @Override
    public void setIneligible(@NotNull BlockState blockState) {}

    @Override
    public void setEligible(@NotNull Block block) {}

    @Override
    public void setEligible(@NotNull BlockState blockState) {}
}
