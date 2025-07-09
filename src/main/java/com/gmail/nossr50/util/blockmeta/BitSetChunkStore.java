package com.gmail.nossr50.util.blockmeta;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.ObjectStreamConstants;
import java.io.PushbackInputStream;
import java.io.Serializable;
import java.util.BitSet;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BitSetChunkStore implements ChunkStore {
    private static final int CURRENT_VERSION = 9;
    private static final int MAGIC_NUMBER = 0xEA5EDEBB;

    private final int cx;
    private final int cz;
    private final int worldMin;
    private final int worldMax;
    private final @NotNull UUID worldUid;
    // Bitset store conforms to a "bottom-up" bit ordering consisting of a stack of {worldHeight} Y planes, each Y plane consists of 16 Z rows of 16 X bits.
    private final @NotNull BitSet store;

    private transient boolean dirty = false;

    public BitSetChunkStore(@NotNull World world, int cx, int cz) {
        this(world.getUID(), world.getMinHeight(), world.getMaxHeight(), cx, cz);
    }

    private BitSetChunkStore(@NotNull UUID worldUid, int worldMin, int worldMax, int cx, int cz) {
        this.cx = cx;
        this.cz = cz;
        this.worldUid = worldUid;
        this.worldMin = worldMin;
        this.worldMax = worldMax;
        this.store = new BitSet(16 * 16 * (worldMax - worldMin));
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
        return worldMin;
    }

    @Override
    public int getChunkMax() {
        return worldMax;
    }

    @Override
    public @NotNull UUID getWorldId() {
        return worldUid;
    }

    @Override
    public boolean isTrue(int x, int y, int z) {
        return store.get(coordToIndex(x, y, z));
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
    public void set(int x, int y, int z, boolean value) {
        store.set(coordToIndex(x, y, z), value);
        dirty = true;
    }

    @Override
    public boolean isEmpty() {
        return store.isEmpty();
    }

    private int coordToIndex(int x, int y, int z) {
        return coordToIndex(x, y, z, worldMin, worldMax);
    }

    private static int coordToIndex(int x, int y, int z, int worldMin, int worldMax) {
        if (x < 0 || x >= 16 || y < worldMin || y > worldMax || z < 0 || z >= 16) {
            throw new IndexOutOfBoundsException(
                    String.format("x: %d y: %d z: %d World Min: %d World Max: %d", x, y, z,
                            worldMin, worldMax));
        }
        int yOffset = -worldMin; // Ensures y multiplier remains positive
        return (z * 16 + x) + (256 * (y + yOffset));
    }

    private static int getWorldMin(@NotNull UUID worldUid) {
        World world = Bukkit.getWorld(worldUid);

        // Not sure how this case could come up, but might as well handle it gracefully.  Loading a chunkstore for an unloaded world?
        if (world == null) {
            throw new RuntimeException("Cannot grab a minimum world height for an unloaded world");
        }

        return world.getMinHeight();
    }

    private static int getWorldMax(@NotNull UUID worldUid) {
        World world = Bukkit.getWorld(worldUid);

        // Not sure how this case could come up, but might as well handle it gracefully.  Loading a chunkstore for an unloaded world?
        if (world == null) {
            throw new RuntimeException("Cannot grab a maximum world height for an unloaded world");
        }

        return world.getMaxHeight();
    }

    private void serialize(@NotNull DataOutputStream out) throws IOException {
        out.writeInt(MAGIC_NUMBER);
        out.writeInt(CURRENT_VERSION);

        out.writeLong(worldUid.getLeastSignificantBits());
        out.writeLong(worldUid.getMostSignificantBits());
        out.writeInt(cx);
        out.writeInt(cz);
        out.writeInt(worldMin);
        out.writeInt(worldMax);

        // Store the byte array directly so we don't have the object type info overhead
        byte[] storeData = store.toByteArray();
        out.writeInt(storeData.length);
        out.write(storeData);

        dirty = false;
    }

    private static @NotNull BitSetChunkStore deserialize(@NotNull DataInputStream in)
            throws IOException {
        int magic = in.readInt();
        // Can be used to determine the format of the file
        int fileVersionNumber = in.readInt();

        if (magic != MAGIC_NUMBER || fileVersionNumber < 8) {
            throw new IOException();
        }

        long lsb = in.readLong();
        long msb = in.readLong();
        UUID worldUid = new UUID(msb, lsb);
        int cx = in.readInt();
        int cz = in.readInt();

        int worldMin = 0;
        if (fileVersionNumber >= 9) {
            worldMin = in.readInt();
        }
        int worldMax = in.readInt();
        byte[] temp = new byte[in.readInt()];
        in.readFully(temp);
        BitSet stored = BitSet.valueOf(temp);

        int currentWorldMin = getWorldMin(worldUid);
        int currentWorldMax = getWorldMax(worldUid);

        // The order in which the world height update code occurs here is important, the world max truncate math only holds up if done before adjusting for min changes
        // Lop off extra data if world max has shrunk
        if (currentWorldMax < worldMax) {
            stored.clear(coordToIndex(16, currentWorldMax, 16, worldMin, worldMax),
                    stored.length());
        }
        // Left shift store if world min has shrunk
        if (currentWorldMin > worldMin) {
            stored = stored.get(currentWorldMin,
                    stored.length()); // Because BitSet's aren't fixed size, a "substring" operation is equivalent to a left shift
        }
        // Right shift store if world min has expanded
        if (currentWorldMin < worldMin) {
            int offset = (worldMin - currentWorldMin) * 16
                    * 16; // We are adding this many bits to the front
            // This isn't the most efficient way to do this, however, its a rare case to occur, and in the grand scheme of things, the small performance we could gain would cost us significant reduced readability of the code
            BitSet shifted = new BitSet();
            for (int i = 0; i < stored.length(); i++) {
                shifted.set(i + offset, stored.get(i));
            }
            stored = shifted;
        }

        BitSetChunkStore chunkStore = new BitSetChunkStore(worldUid, currentWorldMin,
                currentWorldMax, cx, cz);
        chunkStore.store.or(stored);
        chunkStore.dirty = currentWorldMin != worldMin || currentWorldMax != worldMax;

        return chunkStore;
    }

    public static class Serialization {

        public static final short STREAM_MAGIC = (short) 0xACDC; // Rock on

        public static @Nullable ChunkStore readChunkStore(@NotNull DataInputStream inputStream)
                throws IOException {
            if (inputStream.markSupported()) {
                inputStream.mark(2);
            }
            short magicNumber = inputStream.readShort();

            // Java serializable, use legacy serialization
            if (magicNumber == ObjectStreamConstants.STREAM_MAGIC) {
                // "Un-read" the magic number for Serializables, they need it to still be in the stream
                if (inputStream.markSupported()) {
                    inputStream.reset(); // Pretend we never read those bytes
                } else {
                    // Creates a new stream with the two magic number bytes and then the rest of the original stream...   Java is so dumb.  I just wanted to look at two bytes.
                    PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream,
                            2);
                    pushbackInputStream.unread((magicNumber) & 0xFF);
                    pushbackInputStream.unread((magicNumber >>> 8) & 0xFF);
                    inputStream = new DataInputStream(pushbackInputStream);
                }
                return new LegacyDeserializationInputStream(inputStream).readLegacyChunkStore();
            } else if (magicNumber == STREAM_MAGIC) {
                // Pure bytes format
                return BitSetChunkStore.deserialize(inputStream);
            }
            throw new IOException("Bad Data Format");
        }

        public static void writeChunkStore(@NotNull DataOutputStream outputStream,
                @NotNull ChunkStore chunkStore) throws IOException {
            if (!(chunkStore instanceof BitSetChunkStore)) {
                throw new InvalidClassException("ChunkStore must be instance of BitSetChunkStore");
            }
            outputStream.writeShort(STREAM_MAGIC);
            ((BitSetChunkStore) chunkStore).serialize(outputStream);
        }

        // Handles loading the old serialized class
        private static class LegacyDeserializationInputStream extends ObjectInputStream {
            private static class LegacyChunkStoreDeserializer implements Serializable {
                private static final long serialVersionUID = -1L;

                private int cx;
                private int cz;
                private int worldMax;
                private UUID worldUid;
                private boolean[][][] store;

                private LegacyChunkStoreDeserializer() {
                }

                @Deprecated
                private void writeObject(@NotNull ObjectOutputStream out) throws IOException {
                    throw new UnsupportedOperationException("You goofed.");
                }

                @Deprecated
                private void readObject(@NotNull ObjectInputStream in)
                        throws IOException, ClassNotFoundException {
                    in.readInt(); // Magic number
                    in.readInt(); // Format version
                    long lsb = in.readLong();
                    long msb = in.readLong();

                    worldUid = new UUID(msb, lsb);
                    cx = in.readInt();
                    cz = in.readInt();

                    store = (boolean[][][]) in.readObject();
                    worldMax = store[0][0].length;
                }

                public @NotNull BitSetChunkStore convert() {
                    int currentWorldMin = getWorldMin(worldUid);
                    int currentWorldMax = getWorldMax(worldUid);

                    BitSetChunkStore converted = new BitSetChunkStore(worldUid, currentWorldMin,
                            currentWorldMax, cx, cz);

                    // Read old data into new chunkstore
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (int y = 0; y < worldMax && y < currentWorldMax; y++) {
                                converted.store.set(converted.coordToIndex(x, y, z),
                                        store[x][z][y]);
                            }
                        }
                    }
                    // Mark dirty so it will be re-written in new format on close
                    converted.dirty = true;
                    return converted;
                }
            }


            public LegacyDeserializationInputStream(@NotNull InputStream in) throws IOException {
                super(in);
                enableResolveObject(true);
            }

            @Override
            protected @NotNull ObjectStreamClass readClassDescriptor()
                    throws IOException, ClassNotFoundException {
                ObjectStreamClass read = super.readClassDescriptor();
                if (read.getName().contentEquals(
                        "com.gmail.nossr50.util.blockmeta.chunkmeta.PrimitiveChunkStore")) {
                    return ObjectStreamClass.lookup(LegacyChunkStoreDeserializer.class);
                }
                return read;
            }

            public @Nullable ChunkStore readLegacyChunkStore() {
                try {
                    LegacyChunkStoreDeserializer deserializer = (LegacyChunkStoreDeserializer) readObject();
                    return deserializer.convert();
                } catch (IOException | ClassNotFoundException e) {
                    return null;
                }
            }
        }
    }
}
