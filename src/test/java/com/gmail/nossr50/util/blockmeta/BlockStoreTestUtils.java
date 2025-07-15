package com.gmail.nossr50.util.blockmeta;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class BlockStoreTestUtils {
    public static final int LEGACY_WORLD_HEIGHT_MAX = 256;
    public static final int LEGACY_WORLD_HEIGHT_MIN = 0;

    /**
     * Asserts that the two ChunkStores are equal.
     *
     * @param expected The expected ChunkStore
     * @param actual The actual ChunkStore
     */
    static void assertChunkStoreEquals(ChunkStore expected, ChunkStore actual) {
        assertEquals(expected.getChunkMin(), actual.getChunkMin());
        assertEquals(expected.getChunkMax(), actual.getChunkMax());
        assertEqualIgnoreMinMax(expected, actual);
    }

    static byte[] serializeChunkStore(@NotNull ChunkStore chunkStore) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (chunkStore instanceof BitSetChunkStore) {
            BitSetChunkStore.Serialization.writeChunkStore(
                    new DataOutputStream(byteArrayOutputStream), chunkStore);
        } else {
            new UnitTestObjectOutputStream(byteArrayOutputStream).writeObject(
                    chunkStore); // Serializes the class as if
        }
        // it were the old
        // PrimitiveChunkStore
        return byteArrayOutputStream.toByteArray();
    }

    static void assertEqualIgnoreMinMax(ChunkStore expected, ChunkStore actual) {
        assertEquals(expected.getChunkX(), actual.getChunkX());
        assertEquals(expected.getChunkZ(), actual.getChunkZ());
        assertEquals(expected.getWorldId(), actual.getWorldId());
        for (int y = Math.min(actual.getChunkMin(), expected.getChunkMin());
                y < Math.max(actual.getChunkMax(), expected.getChunkMax()); y++) {
            if (expected.getChunkMin() > y || actual.getChunkMin() > y
                    || expected.getChunkMax() <= y || actual.getChunkMax() <= y) {
                continue; // Ignore
            }
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    assertEquals(expected.isTrue(x, y, z), actual.isTrue(x, y, z));
                }
            }
        }
    }
}
