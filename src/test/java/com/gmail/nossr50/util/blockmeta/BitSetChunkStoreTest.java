package com.gmail.nossr50.util.blockmeta;

import static com.gmail.nossr50.util.blockmeta.BlockStoreTestUtils.LEGACY_WORLD_HEIGHT_MAX;
import static com.gmail.nossr50.util.blockmeta.BlockStoreTestUtils.LEGACY_WORLD_HEIGHT_MIN;
import static com.gmail.nossr50.util.blockmeta.BlockStoreTestUtils.assertChunkStoreEquals;
import static com.gmail.nossr50.util.blockmeta.BlockStoreTestUtils.assertEqualIgnoreMinMax;
import static com.gmail.nossr50.util.blockmeta.BlockStoreTestUtils.serializeChunkStore;
import static com.gmail.nossr50.util.blockmeta.UserBlockTrackerTest.recursiveDelete;
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
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
}