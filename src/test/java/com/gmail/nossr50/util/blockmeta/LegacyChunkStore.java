package com.gmail.nossr50.util.blockmeta;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;

/**
 * Used for unit testing upgrades from the old ChunkStore class.
 */
class LegacyChunkStore implements ChunkStore, Serializable {
    private static final long serialVersionUID = -1L;
    transient private boolean dirty = false;
    public boolean[][][] store;
    private static final int CURRENT_VERSION = 7;
    private static final int MAGIC_NUMBER = 0xEA5EDEBB;
    private final int cx;
    private final int cz;
    private final @NotNull UUID worldUid;

    public LegacyChunkStore(@NotNull World world, int cx, int cz) {
        this.cx = cx;
        this.cz = cz;
        this.worldUid = world.getUID();
        this.store = new boolean[16][16][world.getMaxHeight()];
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public int getChunkX() {
        return cx;
    }

    @Override
    public int getChunkZ() {
        return cz;
    }

    @Override
    public int getChunkMin() {
        return 0;
    }

    @Override
    public int getChunkMax() {
        return store[0][0].length;
    }

    @Override
    public @NotNull UUID getWorldId() {
        return worldUid;
    }

    @Override
    public boolean isTrue(int x, int y, int z) {
        return store[x][z][y];
    }

    @Override
    public void setTrue(int x, int y, int z) {
        if (y >= store[0][0].length || y < 0)
            return;
        store[x][z][y] = true;
        dirty = true;
    }

    @Override
    public void setFalse(int x, int y, int z) {
        if (y >= store[0][0].length || y < 0)
            return;
        store[x][z][y] = false;
        dirty = true;
    }

    @Override
    public void set(int x, int y, int z, boolean value) {
        if (y >= store[0][0].length || y < 0)
            return;
        store[x][z][y] = value;
        dirty = true;
    }

    @Override
    public boolean isEmpty() {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < store[0][0].length; y++) {
                    if (store[x][z][y]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void writeObject(@NotNull ObjectOutputStream out) throws IOException {
        out.writeInt(MAGIC_NUMBER);
        out.writeInt(CURRENT_VERSION);

        out.writeLong(worldUid.getLeastSignificantBits());
        out.writeLong(worldUid.getMostSignificantBits());
        out.writeInt(cx);
        out.writeInt(cz);
        out.writeObject(store);

        dirty = false;
    }

    private void readObject(@NotNull ObjectInputStream in) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException();
    }

}
