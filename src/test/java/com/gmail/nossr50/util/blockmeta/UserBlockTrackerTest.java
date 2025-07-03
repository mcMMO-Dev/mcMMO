package com.gmail.nossr50.util.blockmeta;

import static com.gmail.nossr50.util.blockmeta.BlockStoreTestUtils.LEGACY_WORLD_HEIGHT_MAX;
import static com.gmail.nossr50.util.blockmeta.BlockStoreTestUtils.LEGACY_WORLD_HEIGHT_MIN;
import static org.bukkit.Bukkit.getWorld;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.BlockUtils;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
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

        when(mockWorld.getMinHeight()).thenReturn(LEGACY_WORLD_HEIGHT_MIN);
        when(mockWorld.getMaxHeight()).thenReturn(LEGACY_WORLD_HEIGHT_MAX);
    }

    @AfterEach
    void teardownMock() {
        bukkitMock.close();
        mcMMOMock.close();
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
