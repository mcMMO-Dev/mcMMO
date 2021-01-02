package com.gmail.nossr50.util.blockmeta;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.BitSet;
import java.util.UUID;

public class BitSetChunkStore implements ChunkStore {
    private static final int CURRENT_VERSION = 8;
    private static final int MAGIC_NUMBER = 0xEA5EDEBB;

    private final int cx;
    private final int cz;
    private final int worldHeight;
    private final UUID worldUid;
    // Bitset store conforms to a "bottom-up" bit ordering consisting of a stack of {worldHeight} Y planes, each Y plane consists of 16 Z rows of 16 X bits.
    private final BitSet store;

    private transient boolean dirty = false;

    public BitSetChunkStore(@NotNull World world, int cx, int cz) {
        this(world.getUID(), world.getMaxHeight(), cx, cz);
    }

    private BitSetChunkStore(@NotNull UUID worldUid, int worldHeight, int cx, int cz) {
        this.cx = cx;
        this.cz = cz;
        this.worldUid = worldUid;
        this.worldHeight = worldHeight;
        this.store = new BitSet(16 * 16 * worldHeight);
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
        return coordToIndex(x, y, z, worldHeight);
    }

    private static int coordToIndex(int x, int y, int z, int worldHeight) {
        if (x < 0 || x >= 16 || y < 0 || y >= worldHeight || z < 0 || z >= 16)
            throw new IndexOutOfBoundsException(String.format("x: %d y: %d z: %d World Height: %d", x, y, z, worldHeight));
        return (z * 16 + x) + (256 * y);
    }

    private static int getWorldHeight(UUID worldUid, int storedWorldHeight)
    {
        World world = Bukkit.getWorld(worldUid);

        // Not sure how this case could come up, but might as well handle it gracefully.  Loading a chunkstore for an unloaded world?
        if (world == null)
            return storedWorldHeight;

        return world.getMaxHeight();
    }

    private void serialize(DataOutputStream out) throws IOException {
        out.writeInt(MAGIC_NUMBER);
        out.writeInt(CURRENT_VERSION);

        out.writeLong(worldUid.getLeastSignificantBits());
        out.writeLong(worldUid.getMostSignificantBits());
        out.writeInt(cx);
        out.writeInt(cz);
        out.writeInt(worldHeight);

        // Store the byte array directly so we don't have the object type info overhead
        byte[] storeData = store.toByteArray();
        out.writeInt(storeData.length);
        out.write(storeData);

        dirty = false;
    }

    private static BitSetChunkStore deserialize(@NotNull DataInputStream in) throws IOException {
        int magic = in.readInt();
        // Can be used to determine the format of the file
        int fileVersionNumber = in.readInt();

        if (magic != MAGIC_NUMBER || fileVersionNumber != CURRENT_VERSION)
            throw new IOException();

        long lsb = in.readLong();
        long msb = in.readLong();
        UUID worldUid = new UUID(msb, lsb);
        int cx = in.readInt();
        int cz = in.readInt();

        int worldHeight = in.readInt();
        byte[] temp = new byte[in.readInt()];
        in.readFully(temp);
        BitSet stored = BitSet.valueOf(temp);

        int currentWorldHeight = getWorldHeight(worldUid, worldHeight);

        boolean worldHeightShrunk = currentWorldHeight < worldHeight;
        // Lop off extra data if world height has shrunk
        if (worldHeightShrunk)
            stored.clear(coordToIndex(16, currentWorldHeight, 16, worldHeight), stored.length());

        BitSetChunkStore chunkStore = new BitSetChunkStore(worldUid, currentWorldHeight, cx, cz);
        chunkStore.store.or(stored);
        chunkStore.dirty = worldHeightShrunk; // In the expanded case there is no reason to re-write it unless the data changes

        return chunkStore;
    }

    public static class Serialization {

        public static final short STREAM_MAGIC = (short)0xACDC; // Rock on

        public static @NotNull ChunkStore readChunkStore(DataInputStream inputStream) throws IOException {
            if (inputStream.markSupported())
                inputStream.mark(2);
            short magicNumber = inputStream.readShort();

            if (magicNumber == ObjectStreamConstants.STREAM_MAGIC) // Java serializable, use legacy serialization
            {
                // "Un-read" the magic number for Serializables, they need it to still be in the stream
                if (inputStream.markSupported())
                    inputStream.reset(); // Pretend we never read those bytes
                else
                {
                    // Creates a new stream with the two magic number bytes and then the rest of the original stream...   Java is so dumb.  I just wanted to look at two bytes.
                    PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream, 2);
                    pushbackInputStream.unread((magicNumber) & 0xFF);
                    pushbackInputStream.unread((magicNumber >>> 8) & 0xFF);
                    inputStream = new DataInputStream(pushbackInputStream);
                }
                return new LegacyDeserializationInputStream(inputStream).readLegacyChunkStore();
            }
            else if (magicNumber == STREAM_MAGIC) // Pure bytes format
            {
                return BitSetChunkStore.deserialize(inputStream);
            }
            throw new IOException("Bad Data Format");
        }

        public static void writeChunkStore(DataOutputStream outputStream, ChunkStore chunkStore) throws IOException {
            if (!(chunkStore instanceof BitSetChunkStore))
                throw new InvalidClassException("ChunkStore must be instance of BitSetChunkStore");
            outputStream.writeShort(STREAM_MAGIC);
            ((BitSetChunkStore)chunkStore).serialize(outputStream);
        }

        // Handles loading the old serialized class
        private static class LegacyDeserializationInputStream extends ObjectInputStream {
            private static class LegacyChunkStoreDeserializer implements Serializable
            {
                private static final long serialVersionUID = -1L;

                private int cx;
                private int cz;
                private int worldHeight;
                private UUID worldUid;
                private boolean[][][] store;

                private LegacyChunkStoreDeserializer() {}

                @Deprecated
                private void writeObject(ObjectOutputStream out) throws IOException {
                    throw new UnsupportedOperationException("You goofed.");
                }

                @Deprecated
                private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
                    in.readInt(); // Magic number
                    in.readInt(); // Format version
                    long lsb = in.readLong();
                    long msb = in.readLong();

                    worldUid = new UUID(msb, lsb);
                    cx = in.readInt();
                    cz = in.readInt();

                    store = (boolean[][][]) in.readObject();
                    worldHeight = store[0][0].length;
                }

                public BitSetChunkStore convert()
                {
                    int currentWorldHeight = getWorldHeight(worldUid, worldHeight);

                    BitSetChunkStore converted = new BitSetChunkStore(worldUid, currentWorldHeight, cx, cz);

                    // Read old data into new chunkstore
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (int y = 0; y < worldHeight && y < currentWorldHeight; y++) {
                                converted.store.set(converted.coordToIndex(x, y, z), store[x][z][y]);
                            }
                        }
                    }
                    // Mark dirty so it will be re-written in new format on close
                    converted.dirty = true;
                    return converted;
                }
            }


            public LegacyDeserializationInputStream(InputStream in) throws IOException {
                super(in);
                enableResolveObject(true);
            }

            @Override
            protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
                ObjectStreamClass read = super.readClassDescriptor();
                if (read.getName().contentEquals("com.gmail.nossr50.util.blockmeta.chunkmeta.PrimitiveChunkStore"))
                    return ObjectStreamClass.lookup(LegacyChunkStoreDeserializer.class);
                return read;
            }

            public ChunkStore readLegacyChunkStore(){
                try {
                    LegacyChunkStoreDeserializer deserializer = (LegacyChunkStoreDeserializer)readObject();
                    return deserializer.convert();
                } catch (IOException | ClassNotFoundException e) {
                    return null;
                }
            }
        }
    }
}
