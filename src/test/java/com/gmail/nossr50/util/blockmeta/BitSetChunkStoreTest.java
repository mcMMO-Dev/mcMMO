package com.gmail.nossr50.util.blockmeta;

import static com.gmail.nossr50.util.blockmeta.BlockStoreTestUtils.LEGACY_WORLD_HEIGHT_MAX;
import static com.gmail.nossr50.util.blockmeta.BlockStoreTestUtils.LEGACY_WORLD_HEIGHT_MIN;
import static com.gmail.nossr50.util.blockmeta.BlockStoreTestUtils.assertChunkStoreEquals;
import static com.gmail.nossr50.util.blockmeta.BlockStoreTestUtils.assertEqualIgnoreMinMax;
import static com.gmail.nossr50.util.blockmeta.BlockStoreTestUtils.serializeChunkStore;
import static com.gmail.nossr50.util.blockmeta.UserBlockTrackerTest.recursiveDelete;
import static org.assertj.core.api.Assertions.assertThat;
import static org.bukkit.Bukkit.getWorld;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.mcMMO;
import com.google.common.io.Files;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class BitSetChunkStoreTest {
    private static File tempDir;

    @BeforeAll
    public static void setUpClass() {
        tempDir = Files.createTempDir();
    }

    @AfterAll
    public static void tearDownClass() {
        recursiveDelete(tempDir);
    }

    private World mockWorld;

    private MockedStatic<Bukkit> bukkitMock;
    private MockedStatic<mcMMO> mcMMOMock;

    @BeforeEach
    void setUpMock() {
        UUID worldUUID = UUID.randomUUID();
        mockWorld = Mockito.mock(World.class);
        when(mockWorld.getUID()).thenReturn(worldUUID);
        when(mockWorld.getMaxHeight()).thenReturn(256);
        when(mockWorld.getWorldFolder()).thenReturn(tempDir);

        bukkitMock = mockStatic(Bukkit.class);
        bukkitMock.when(() -> getWorld(worldUUID)).thenReturn(mockWorld);

        mcMMOMock = mockStatic(mcMMO.class);

        when(mockWorld.getMinHeight()).thenReturn(LEGACY_WORLD_HEIGHT_MIN);
        when(mockWorld.getMaxHeight()).thenReturn(LEGACY_WORLD_HEIGHT_MAX);
    }

    @AfterEach
    void teardownMock() {
        bukkitMock.close();
        mcMMOMock.close();
    }

    @Test
    void testSetValue() {
        final BitSetChunkStore original = new BitSetChunkStore(mockWorld, 0, 0);
        original.setTrue(0, 0, 0);
        assertTrue(original.isTrue(0, 0, 0));
        original.setFalse(0, 0, 0);
        assertFalse(original.isTrue(0, 0, 0));
    }

    @Test
    void testIsEmpty() {
        final BitSetChunkStore original = new BitSetChunkStore(mockWorld, 0, 0);
        assertTrue(original.isEmpty());
        original.setTrue(0, 0, 0);
        original.setFalse(0, 0, 0);
        assertTrue(original.isEmpty());
    }

    @Test
    void testRoundTrip() throws IOException {
        final BitSetChunkStore original = new BitSetChunkStore(mockWorld, 1, 2);
        original.setTrue(14, 89, 12);
        original.setTrue(14, 90, 12);
        original.setTrue(13, 89, 12);
        byte[] serializedBytes = serializeChunkStore(original);
        final ChunkStore deserialized = BitSetChunkStore.Serialization.readChunkStore(
                new DataInputStream(new ByteArrayInputStream(serializedBytes)));
        assertChunkStoreEquals(original, deserialized);
    }

    @Test
    void testNegativeWorldMin() throws IOException {
        when(mockWorld.getMinHeight()).thenReturn(-64);

        final BitSetChunkStore original = new BitSetChunkStore(mockWorld, 1, 2);
        original.setTrue(14, -32, 12);
        original.setTrue(14, -64, 12);
        original.setTrue(13, -63, 12);
        byte[] serializedBytes = serializeChunkStore(original);
        final ChunkStore deserialized = BitSetChunkStore.Serialization.readChunkStore(
                new DataInputStream(new ByteArrayInputStream(serializedBytes)));
        assertChunkStoreEquals(original, deserialized);
    }

    @Test
    void testNegativeWorldMinUpgrade() throws IOException {
        final BitSetChunkStore original = new BitSetChunkStore(mockWorld, 1, 2);
        original.setTrue(14, 1, 12);
        original.setTrue(14, 2, 12);
        original.setTrue(13, 3, 12);
        byte[] serializedBytes = serializeChunkStore(original);

        when(mockWorld.getMinHeight()).thenReturn(-64);
        final ChunkStore deserialized = BitSetChunkStore.Serialization.readChunkStore(
                new DataInputStream(new ByteArrayInputStream(serializedBytes)));
        assert deserialized != null;
        assertEqualIgnoreMinMax(original, deserialized);
    }

    /**
     * A world's build height can shrink between server runs (world downgrade, datapack change).
     * In-range placed-block markers must survive at their original coordinates and only the
     * out-of-range markers may be dropped — nothing may shift and deserialization must not throw.
     */
    @ParameterizedTest
    @MethodSource("shrunkenWorldHeightCases")
    void deserializeShouldDropOnlyOutOfRangeMarkersWhenWorldHeightShrinks(int oldMin, int oldMax,
            int newMin, int newMax, int[][] inRange, int[][] outOfRange) throws IOException {
        // Given - a chunk store serialized under the old world height with markers both inside
        // and outside the shrunken bounds
        when(mockWorld.getMinHeight()).thenReturn(oldMin);
        when(mockWorld.getMaxHeight()).thenReturn(oldMax);
        final BitSetChunkStore original = new BitSetChunkStore(mockWorld, 1, 2);
        for (int[] coord : inRange) {
            original.setTrue(coord[0], coord[1], coord[2]);
        }
        for (int[] coord : outOfRange) {
            original.setTrue(coord[0], coord[1], coord[2]);
        }
        final byte[] serializedBytes = serializeChunkStore(original);

        // When - the world height shrinks and the chunk store is read back
        when(mockWorld.getMinHeight()).thenReturn(newMin);
        when(mockWorld.getMaxHeight()).thenReturn(newMax);
        final ChunkStore deserialized = BitSetChunkStore.Serialization.readChunkStore(
                new DataInputStream(new ByteArrayInputStream(serializedBytes)));

        // Then - every in-range marker survives at its original coordinates and every other
        // position in the new bounds is clear
        assertThat(deserialized).isNotNull();
        final Set<String> expected = new HashSet<>();
        for (int[] coord : inRange) {
            expected.add(coord[0] + "," + coord[1] + "," + coord[2]);
        }
        for (int y = newMin; y < newMax; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    assertThat(deserialized.isTrue(x, y, z))
                            .as("marker at (%d, %d, %d)", x, y, z)
                            .isEqualTo(expected.contains(x + "," + y + "," + z));
                }
            }
        }
    }

    private static Stream<Arguments> shrunkenWorldHeightCases() {
        return Stream.of(
                // World max shrinks: -64..320 becomes -64..128
                Arguments.of(-64, 320, -64, 128,
                        new int[][] {{14, -64, 12}, {5, 100, 5}, {0, 127, 0}},
                        new int[][] {{1, 128, 1}, {14, 319, 12}}),
                // World min rises to zero: -64..320 becomes 0..320
                Arguments.of(-64, 320, 0, 320,
                        new int[][] {{14, 0, 12}, {5, 100, 5}, {3, 319, 3}},
                        new int[][] {{2, -1, 2}, {14, -64, 12}}),
                // World min rises but stays negative: -64..320 becomes -32..320
                Arguments.of(-64, 320, -32, 320,
                        new int[][] {{5, -32, 5}, {6, 0, 6}},
                        new int[][] {{7, -64, 7}, {8, -33, 8}}),
                // Both ends shrink: -64..320 becomes 0..128
                Arguments.of(-64, 320, 0, 128,
                        new int[][] {{0, 0, 0}, {5, 100, 5}, {1, 127, 1}},
                        new int[][] {{2, -64, 2}, {3, -1, 3}, {4, 128, 4}, {14, 319, 12}}));
    }
}