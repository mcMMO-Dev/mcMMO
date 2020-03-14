package com.gmail.nossr50.util.blockmeta.chunkmeta;

import com.gmail.nossr50.util.blockmeta.ChunkletStore;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class PrimitiveChunkStore implements ChunkStore {
    private static final long serialVersionUID = -1L;
    transient private boolean dirty = false;
    /**
     * X, Z, Y
     *
     * @deprecated This value will no longer be used.
     */
    @Deprecated
    @Nullable
    public boolean[][][] store;
    // IIII IIII IIII IIII IIII IIII IIII IIII
    // XXXX XXXX XXXX XXXX YYYY YYYY YYYY YYYY
    /**
     * Data of chunk.
     * <p>
     * <pre>[PAGE][PAGE]... (Y)
     *
     * Page from 8 int.
     * 1 int store 2 line.
     *
     * int position = (y * PAGE_SIZE) + (x >> 1);
     * int offset = z;
     * if ((x & 1) != 0) {
     *     offset += 16; // Because one integer can store two lines.
     * }
     * int code = chunk.data.get(position);
     * boolean isOk = ((code >>> offset) & 1) == 1;
     * </pre>
     */
    public AtomicIntegerArray data; // should it private or protected?
    public static final int PAGE_SIZE = 16 * 16 / Integer.SIZE;
    private static final int CURRENT_VERSION = 8;
    private static final int MAGIC_NUMBER = 0xEA5EDEBB;
    private int cx;
    private int cz;
    private UUID worldUid;
    private int worldHeight;

    public PrimitiveChunkStore(World world, int cx, int cz) {
        this.cx = cx;
        this.cz = cz;
        this.worldUid = world.getUID();
        this.data = new AtomicIntegerArray((worldHeight = world.getMaxHeight()) * PAGE_SIZE);
    }

    @Override
    @Contract(pure = true)
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    @Contract(pure = true)
    public int getChunkX() {
        return cx;
    }

    @Override
    @Contract(pure = true)
    public int getChunkZ() {
        return cz;
    }

    private boolean isInvalid(int x, int y, int z) {
        if (y < 0 || y >= worldHeight) return true;
        return ((x | z) & ~0xF) != 0; // < 0 || > 15
    }

    @Override
    @Contract(pure = true)
    public boolean isTrue(int x, int y, int z) {
        if (isInvalid(x, y, z)) return false;
        // An explanation of the operation of shift is on lines 26 to 42
        // This is compressed
        int status = data.get((y * PAGE_SIZE) + (x >> 1));
        return ((status >>> (z +
                (((x & 1) == 1) ? 16 : 0) // Is it on an odd line
        )) & 1) != 0;
    }

    private void set(int x, int y, int z, boolean val) {
        if (isInvalid(x, y, z))
            return;
        int point = (y * PAGE_SIZE) + (x >> 1);
        int bit = z + (((x & 1) == 1) ? 16 : 0);
        int source;
        // An explanation of the operation of shift is on lines 26 to 42
        do {
            source = data.get(point);
            int target = source;
            if (val) {
                // set bit to true
                target |= 1 << bit;
            } else {
                // set bit to false
                target &= ~(1 << bit);
            }
            // Try to store changes to memory
            if (data.compareAndSet(point, source, target)) break;
        } while (true);
        dirty = true;
    }

    @Override
    public void setTrue(int x, int y, int z) {
        set(x, y, z, true);
    }

    @Override
    public void setFalse(int x, int y, int z) {
        set(x, y, z, false);
    }

    @Override
    @Contract(pure = true)
    public boolean isEmpty() {
        int end = data.length();
        // same as for(int i = end.length()-1; i >= 0; i--)
        while (end-- > 0) {
            // If any bit is true, return false
            if (data.get(end) != 0) return false;
        }
        // No bit is true
        return true;
    }

    @Override
    public void copyFrom(ChunkletStore otherStore) {
        if (otherStore instanceof PrimitiveChunkStore) { // Direct copy.
            AtomicIntegerArray from = ((PrimitiveChunkStore) otherStore).data;
            AtomicIntegerArray into = this.data = new AtomicIntegerArray(
                    worldHeight * PAGE_SIZE
            );
            // To ensure that no cross-border
            // Like:
            // from: ---- ---- ---- ----
            // into: ---- ---- ---- ---- ---- ---- ---- ----

            // from: ---- ---- ---- ---- ---- ---- ---- ----
            // into: ---- ---- ---- ----
            int end = Math.min(from.length(), into.length());
            // same as for(int i = end.length()-1; i >= 0; i--)
            while (end-- > 0) {
                into.set(end, from.get(end));
            }
        } else {
            // Copy values with legacy method.
            int worldHeight = this.worldHeight;
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < worldHeight; y++) {
                        set(x, y, z, otherStore.isTrue(x, y, z));
                    }
                }
            }
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(MAGIC_NUMBER);
        out.writeInt(CURRENT_VERSION);

        out.writeLong(worldUid.getLeastSignificantBits());
        out.writeLong(worldUid.getMostSignificantBits());
        out.writeInt(cx);
        out.writeInt(cz);
        // We don't use out.writeObject(), because it stores extraneous information
        int len;
        out.writeInt(len = data.length());
        for (int i = 0; i < len; i++) {
            out.writeInt(data.get(i));
        }

        dirty = false;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int magic = in.readInt();
        // Can be used to determine the format of the file
        int fileVersionNumber = in.readInt();

        if (magic != MAGIC_NUMBER) {
            fileVersionNumber = 0;
        }

        long lsb = in.readLong();
        long msb = in.readLong();
        worldUid = new UUID(msb, lsb);
        cx = in.readInt();
        cz = in.readInt();

        if (fileVersionNumber == CURRENT_VERSION) {
            // File version 8
            int size;
            AtomicIntegerArray data = this.data = new AtomicIntegerArray(size = in.readInt());
            if ((size % PAGE_SIZE) != 0)
                throw new InvalidObjectException("Illegal data length");
            worldHeight = size / PAGE_SIZE;
            for (int i = 0; i < size; i++) {
                data.set(i, in.readInt());
            }
        } else {
            // File version 7
            boolean[][][] st = (boolean[][][]) in.readObject();
            int wh; // world height
            this.data = new AtomicIntegerArray((wh = worldHeight = st[0][0].length) * PAGE_SIZE);
            // Legacy value saving.
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < wh; y++) {
                        set(x, y, z, st[x][z][y]);
                    }
                }
            }
        }

        if (fileVersionNumber < 5) {
            fixArray();
            dirty = true;
        }
    }

    private void fixArray() {
        AtomicIntegerArray old = this.data;
        AtomicIntegerArray current = this.data = new AtomicIntegerArray(PAGE_SIZE * (
                worldHeight = Bukkit.getWorld(worldUid).getMaxHeight()
        ));
        // To ensure that no cross-border
        int end = Math.min(current.length(), old.length());
        // same as for(int i = end.length()-1; i >= 0; i--)
        while (end-- > 0) {
            current.set(end, old.get(end));
        }
    }
}
