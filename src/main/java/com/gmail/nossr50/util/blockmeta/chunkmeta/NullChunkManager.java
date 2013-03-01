package com.gmail.nossr50.util.blockmeta.chunkmeta;

import java.io.IOException;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;

public class NullChunkManager implements ChunkManager {

    @Override
    public void closeAll() {}

    @Override
    public ChunkStore readChunkStore(World world, int x, int z) throws IOException {
        return null;
    }

    @Override
    public void writeChunkStore(World world, int x, int z, ChunkStore data) {}

    @Override
    public void closeChunkStore(World world, int x, int z) {}

    @Override
    public void loadChunklet(int cx, int cy, int cz, World world) {}

    @Override
    public void unloadChunklet(int cx, int cy, int cz, World world) {}

    @Override
    public void loadChunk(int cx, int cz, World world, Entity[] entities) {}

    @Override
    public void unloadChunk(int cx, int cz, World world) {}

    @Override
    public void saveChunk(int cx, int cz, World world) {}

    @Override
    public boolean isChunkLoaded(int cx, int cz, World world) {
        return true;
    }

    @Override
    public void chunkLoaded(int cx, int cz, World world) {}

    @Override
    public void chunkUnloaded(int cx, int cz, World world) {}

    @Override
    public void saveWorld(World world) {}

    @Override
    public void unloadWorld(World world) {}

    @Override
    public void loadWorld(World world) {}

    @Override
    public void saveAll() {}

    @Override
    public void unloadAll() {}

    @Override
    public boolean isTrue(int x, int y, int z, World world) {
        return false;
    }

    @Override
    public boolean isTrue(Block block) {
        return false;
    }

    @Override
    public boolean isTrue(BlockState blockState) {
        return false;
    }

    @Override
    public void setTrue(int x, int y, int z, World world) {}

    @Override
    public void setTrue(Block block) {}

    @Override
    public void setTrue(BlockState blockState) {}

    @Override
    public void setFalse(int x, int y, int z, World world) {}

    @Override
    public void setFalse(Block block) {}

    @Override
    public void setFalse(BlockState blockState) {}

    @Override
    public void cleanUp() {}
}
