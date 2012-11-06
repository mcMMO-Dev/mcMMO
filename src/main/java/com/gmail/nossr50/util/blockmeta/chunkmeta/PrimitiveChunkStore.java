package com.gmail.nossr50.util.blockmeta.chunkmeta;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

import org.bukkit.World;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.blockmeta.ChunkletStore;

public class PrimitiveChunkStore implements ChunkStore {
    private static final long serialVersionUID = -1L;
    transient private boolean dirty = false;
    /** X, Z, Y */
    public boolean[][][] store;
    private static final int CURRENT_VERSION = 4;
    private static final int MAGIC_NUMBER = 0xEA5EDEBB;
    private int cx;
    private int cz;
    private UUID worldUid;
    transient private int worldHeight;
    transient private int xBitShifts;
    transient private int zBitShifts;
    transient private boolean conversionNeeded;

    public PrimitiveChunkStore(World world, int cx, int cz) {
        this.cx = cx;
        this.cz = cz;
	this.worldUid = world.getUID();

        this.worldHeight = world != null ? world.getMaxHeight() : 128;
        this.xBitShifts = 11;
        this.zBitShifts = 7;

        this.store = new boolean[16][16][this.worldHeight - 1];

        conversionNeeded = false;
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
    public boolean isTrue(int x, int y, int z) {
        return store[x][z][y];
    }

    @Override
    public void setTrue(int x, int y, int z) {
        store[x][z][y] = true;
        dirty = true;
    }

    @Override
    public void setFalse(int x, int y, int z) {
        store[x][z][y] = false;
        dirty = true;
    }

    @Override
    public boolean isEmpty() {
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                for(int y = 0; y < this.worldHeight; y++) {
                    if(store[x][z][y]) return false;
                }
            }
        }
        return true;
    }

    @Override
    public void copyFrom(ChunkletStore otherStore) {
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                for(int y = 0; y < this.worldHeight; y++) {
                    store[x][z][y] = otherStore.isTrue(x, y, z);
                }
            }
        }
        dirty = true;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(MAGIC_NUMBER);
        out.writeInt(CURRENT_VERSION);

        out.writeLong(worldUid.getLeastSignificantBits());
        out.writeLong(worldUid.getMostSignificantBits());
        out.writeInt(cx);
        out.writeInt(cz);
	out.writeObject(store);

        dirty = false;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int fileVersionNumber; // Can be used to determine the format of the file

        long lsb = in.readLong();
        if (((int) (lsb >> 32)) == MAGIC_NUMBER) {
            fileVersionNumber = (int) lsb;
            lsb = in.readLong();
        } else {
            fileVersionNumber = 0;
        }

        long msb = in.readLong();
        worldUid = new UUID(msb, lsb);
        cx = in.readInt();
        cz = in.readInt();

        // Constructor is not invoked, need to set these fields
        World world = mcMMO.p.getServer().getWorld(this.worldUid);

        this.worldHeight = world.getMaxHeight();
        this.xBitShifts = 11;
        this.zBitShifts = 7;

        store = (boolean[][][]) in.readObject();

        if (fileVersionNumber < CURRENT_VERSION) {
            dirty = true;
        }
    }
}