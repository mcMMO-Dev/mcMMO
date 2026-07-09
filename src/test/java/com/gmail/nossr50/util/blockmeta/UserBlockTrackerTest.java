package com.gmail.nossr50.util.blockmeta;

import static com.gmail.nossr50.util.blockmeta.BlockStoreTestUtils.LEGACY_WORLD_HEIGHT_MAX;
import static com.gmail.nossr50.util.blockmeta.BlockStoreTestUtils.LEGACY_WORLD_HEIGHT_MIN;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.bukkit.Bukkit.getWorld;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.BlockUtils;
import com.google.common.io.Files;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Could be a lot better. But some tests are better than none! Tests the major things, still kinda
 * unit-testy. Verifies that the serialization isn't completely broken.
 */
class UserBlockTrackerTest {
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
        mcMMO.p = mock(mcMMO.class);
        when(mcMMO.p.getLogger()).thenReturn(Logger.getLogger(UserBlockTrackerTest.class.getName()));

        when(mockWorld.getMinHeight()).thenReturn(LEGACY_WORLD_HEIGHT_MIN);
        when(mockWorld.getMaxHeight()).thenReturn(LEGACY_WORLD_HEIGHT_MAX);
    }

    @AfterEach
    void teardownMock() {
        bukkitMock.close();
        mcMMOMock.close();
        mcMMO.p = null;
    }

    @Test
    void setIneligibleShouldThrowIndexOutOfBoundsException() {
        when(mockWorld.getMinHeight()).thenReturn(-64);
        final HashChunkManager hashChunkManager = new HashChunkManager();

        // Top Block
        int illegalMaxHeight = 256 + 1;
        final Block illegalHeightBlock = initMockBlock(1337, illegalMaxHeight, -1337);
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> hashChunkManager.setIneligible(illegalHeightBlock));

        int illegalMinHeight = -65;
        final Block otherIllegalHeightBlock = initMockBlock(1337, illegalMinHeight, -1337);
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> hashChunkManager.setIneligible(otherIllegalHeightBlock));
    }

    @Test
    void testSetEligibility() {
        when(mockWorld.getMinHeight()).thenReturn(-64);
        final HashChunkManager hashChunkManager = new HashChunkManager();
        int radius = 2; // Could be anything but drastically changes test time

        for (int x = -radius; x <= radius; x++) {
            for (int y = mockWorld.getMinHeight(); y <= mockWorld.getMaxHeight(); y++) {
                for (int z = -radius; z <= radius; z++) {
                    final Block testBlock = initMockBlock(x, y, z);
                    // mark ineligible
                    hashChunkManager.setIneligible(testBlock);
                    assertTrue(hashChunkManager.isIneligible(testBlock));

                    // mark eligible
                    hashChunkManager.setEligible(testBlock);
                    // Might as well test both isIneligible and isEligible while we are here
                    assertFalse(hashChunkManager.isIneligible(testBlock));
                    assertTrue(hashChunkManager.isEligible(testBlock));
                }
            }
        }

        // TODO: Whatever is going on down here should be in its own test
        // Bot Block
        final Block bottomBlock = initMockBlock(1337, 0, -1337);
        assertFalse(hashChunkManager.isIneligible(bottomBlock));

        assertTrue(BlockUtils.isWithinWorldBounds(bottomBlock));
        hashChunkManager.setIneligible(bottomBlock);
        assertTrue(hashChunkManager.isIneligible(bottomBlock));

        // Top Block
        final Block topBlock = initMockBlock(1337, 255, -1337);
        assertFalse(hashChunkManager.isIneligible(topBlock));

        assertTrue(BlockUtils.isWithinWorldBounds(topBlock));
        hashChunkManager.setIneligible(topBlock);
        assertTrue(hashChunkManager.isIneligible(topBlock));
    }

    @Test
    void testChunkCoords() throws IOException {
        // TODO: Unfinished test?
        for (int x = -96; x < 0; x++) {
            int cx = x >> 4;
            int ix = Math.abs(x) % 16;
            //System.out.print(cx + ":" + ix + "  ");
        }
    }

    @Test
    void testSimpleRegionRejectsOutOfBounds() {
        File file = new File(tempDir, "SimpleRegionRoundTrip.region");
        McMMOSimpleRegionFile region = new McMMOSimpleRegionFile(file, 0, 0);
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> region.getOutputStream(-1, 0));
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> region.getOutputStream(0, -1));
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> region.getOutputStream(32, 0));
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> region.getOutputStream(0, 32));
        region.close();
    }

    @Test
    void testChunkStoreRejectsOutOfBounds() {
        ChunkStore chunkStore = new BitSetChunkStore(mockWorld, 0, 0);
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> chunkStore.setTrue(-1, 0, 0));
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> chunkStore.setTrue(0, -1, 0));
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> chunkStore.setTrue(0, 0, -1));
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> chunkStore.setTrue(16, 0, 0));
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> chunkStore.setTrue(0, mockWorld.getMaxHeight() + 1, 0));
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> chunkStore.setTrue(0, mockWorld.getMinHeight() - 1, 0));
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> chunkStore.setTrue(0, 0, 16));
    }

    @Test
    void testRegressionChunkMirrorBug() {
        final UserBlockTracker chunkManager = new HashChunkManager();
        Block mockBlockA = Mockito.mock(Block.class);
        when(mockBlockA.getX()).thenReturn(15);
        when(mockBlockA.getZ()).thenReturn(15);
        when(mockBlockA.getY()).thenReturn(0);
        when(mockBlockA.getWorld()).thenReturn(mockWorld);
        Block mockBlockB = Mockito.mock(Block.class);
        when(mockBlockB.getX()).thenReturn(-15);
        when(mockBlockB.getZ()).thenReturn(-15);
        when(mockBlockB.getY()).thenReturn(0);
        when(mockBlockB.getWorld()).thenReturn(mockWorld);

        chunkManager.setIneligible(mockBlockA);
        chunkManager.setEligible(mockBlockB);
        assertTrue(chunkManager.isIneligible(mockBlockA));
    }

    @Test
    void testUnload() {
        final ChunkManager chunkManager = new HashChunkManager();
        Block mockBlockA = Mockito.mock(Block.class);
        when(mockBlockA.getX()).thenReturn(15);
        when(mockBlockA.getZ()).thenReturn(15);
        when(mockBlockA.getY()).thenReturn(0);
        when(mockBlockA.getWorld()).thenReturn(mockWorld);
        Block mockBlockB = Mockito.mock(Block.class);
        when(mockBlockB.getX()).thenReturn(-15);
        when(mockBlockB.getZ()).thenReturn(-15);
        when(mockBlockB.getY()).thenReturn(0);
        when(mockBlockB.getWorld()).thenReturn(mockWorld);

        chunkManager.setIneligible(mockBlockA);
        chunkManager.setEligible(mockBlockB);
        assertTrue(chunkManager.isIneligible(mockBlockA));

        chunkManager.chunkUnloaded(0, 0, mockWorld);
    }

    /**
     * Full disk round-trip: markers must survive the chunk being unloaded (which writes the
     * region file) and must be readable by a completely fresh manager instance, proving the
     * data actually lives on disk rather than in the manager's in-memory maps.
     */
    @Test
    void setIneligibleShouldSurviveChunkUnloadAndReloadFromDisk() {
        // Given - an isolated world folder and markers in a positive and a negative chunk
        when(mockWorld.getWorldFolder()).thenReturn(new File(tempDir, "roundTripWorld"));
        final HashChunkManager writingManager = new HashChunkManager();
        final Block positiveBlock = initMockBlock(1337, 42, 1337);
        final Block negativeBlock = initMockBlock(-1337, 42, -1337);
        final Block untouchedBlock = initMockBlock(1338, 42, 1337);
        writingManager.setIneligible(positiveBlock);
        writingManager.setIneligible(negativeBlock);

        // When - the chunks are unloaded (persisting them) and a fresh manager reads the disk
        writingManager.chunkUnloaded(1337 >> 4, 1337 >> 4, mockWorld);
        writingManager.chunkUnloaded(-1337 >> 4, -1337 >> 4, mockWorld);
        final HashChunkManager readingManager = new HashChunkManager();

        // Then - the markers are still present and untouched neighbors stay eligible
        assertTrue(readingManager.isIneligible(positiveBlock));
        assertTrue(readingManager.isIneligible(negativeBlock));
        assertFalse(readingManager.isIneligible(untouchedBlock));
    }

    /**
     * Region files store chunk data in fixed-size segments; rewriting a chunk with more data
     * than its old segments can hold forces a relocation past its neighbor. Both chunks must
     * survive that relocation and a close/reopen cycle intact.
     */
    @Test
    void regionFileShouldPreserveChunksWhenRewrittenDataGrowsAcrossSegments()
            throws IOException {
        // Given - two neighboring chunks, the first holding a small single-segment payload
        final File regionFile = new File(tempDir, "SegmentGrowth.mcm");
        final byte[] smallPayload = payload(100, (byte) 1);
        final byte[] neighborPayload = payload(200, (byte) 2);
        final byte[] grownPayload = payload(5000, (byte) 3);

        McMMOSimpleRegionFile region = new McMMOSimpleRegionFile(regionFile, 0, 0);
        writeChunkPayload(region, 0, 0, smallPayload);
        writeChunkPayload(region, 0, 1, neighborPayload);

        // When - the first chunk is rewritten with a payload spanning several segments,
        // forcing it to relocate past its neighbor, and the file is closed and reopened
        writeChunkPayload(region, 0, 0, grownPayload);
        region.close();
        region = new McMMOSimpleRegionFile(regionFile, 0, 0);

        // Then - both chunks read back their latest payloads intact
        Assertions.assertArrayEquals(grownPayload, readChunkPayload(region, 0, 0));
        Assertions.assertArrayEquals(neighborPayload, readChunkPayload(region, 0, 1));
        region.close();
    }

    /**
     * A corrupt header (impossible negative chunk byte length) must fail cleanly: the error
     * should name the file, and the failed open must release its file handle - a leaked
     * RandomAccessFile keeps the region file locked on Windows until GC.
     */
    @Test
    void regionFileOpenShouldFailCleanlyOnCorruptHeader() throws IOException {
        // Given - a region file whose header claims a negative byte length for chunk 0
        final File regionFile = new File(tempDir, "CorruptHeader.mcm");
        try (RandomAccessFile raf = new RandomAccessFile(regionFile, "rw")) {
            raf.write(new byte[12288]);
            raf.seek(8192);
            raf.writeInt(10); // segment exponent
            raf.seek(4096);
            raf.writeInt(-100); // chunkNumBytes[0] - impossible value
        }

        // When / Then - opening it fails with an error that names the file
        assertThatThrownBy(() -> new McMMOSimpleRegionFile(regionFile, 0, 0))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(regionFile.getName());

        // And - the handle was released, so the corrupt file can still be deleted or replaced
        assertTrue(regionFile.delete());
    }

    /**
     * Corrupt on-disk data must degrade to "no data" at the manager boundary; eligibility
     * checks and later writes must keep working rather than propagate the read failure.
     */
    @Test
    void isIneligibleShouldTreatCorruptRegionDataAsEmpty() throws IOException {
        // Given - a corrupt region file sitting where the manager expects chunk (0, 0)'s region
        final File worldFolder = new File(tempDir, "corruptRegionWorld");
        when(mockWorld.getWorldFolder()).thenReturn(worldFolder);
        final File regionFolder = new File(worldFolder, "mcmmo_regions");
        assertTrue(regionFolder.mkdirs());
        try (RandomAccessFile raf = new RandomAccessFile(new File(regionFolder, "mcmmo_0_0_.mcm"),
                "rw")) {
            raf.write(new byte[12288]);
            raf.seek(8192);
            raf.writeInt(10); // segment exponent
            raf.seek(4096);
            raf.writeInt(-100); // chunkNumBytes[0] - impossible value
        }

        // When - a block in that region is checked
        final HashChunkManager hashChunkManager = new HashChunkManager();
        final Block block = initMockBlock(1, 42, 1);

        // Then - the corrupt data reads as empty instead of throwing
        assertFalse(hashChunkManager.isIneligible(block));
    }

    private static byte[] payload(int size, byte fill) {
        final byte[] data = new byte[size];
        Arrays.fill(data, fill);
        // Vary the tail so compression cannot collapse payloads into identical streams
        for (int i = 0; i < Math.min(size, 32); i++) {
            data[size - 1 - i] = (byte) (fill + i);
        }
        return data;
    }

    private static void writeChunkPayload(McMMOSimpleRegionFile region, int cx, int cz,
            byte[] payload) throws IOException {
        try (DataOutputStream out = region.getOutputStream(cx, cz)) {
            out.writeInt(payload.length);
            out.write(payload);
        }
    }

    private static byte[] readChunkPayload(McMMOSimpleRegionFile region, int cx, int cz)
            throws IOException {
        try (DataInputStream in = region.getInputStream(cx, cz)) {
            Assertions.assertNotNull(in, "expected chunk data at (" + cx + ", " + cz + ")");
            final byte[] data = new byte[in.readInt()];
            in.readFully(data);
            return data;
        }
    }

    @NotNull
    private Block initMockBlock(int x, int y, int z) {
        final Block mockBlock = Mockito.mock(Block.class);
        when(mockBlock.getX()).thenReturn(x);
        when(mockBlock.getY()).thenReturn(y);
        when(mockBlock.getZ()).thenReturn(z);
        when(mockBlock.getWorld()).thenReturn(mockWorld);
        return mockBlock;
    }

    public static void recursiveDelete(@NotNull File directoryToBeDeleted) {
        if (directoryToBeDeleted.isDirectory()) {
            for (File file : directoryToBeDeleted.listFiles()) {
                recursiveDelete(file);
            }
        }
        directoryToBeDeleted.delete();
    }
}
