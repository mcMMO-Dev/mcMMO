package com.gmail.nossr50.util.blockmeta;

import com.gmail.nossr50.mcMMO;
import com.google.common.io.Files;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.*;
import java.util.UUID;

import static com.gmail.nossr50.util.blockmeta.BlockStoreTestUtils.*;
import static com.gmail.nossr50.util.blockmeta.UserBlockTrackerTest.recursiveDelete;
import static org.bukkit.Bukkit.getWorld;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class ChunkStoreTest {
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
    void testUpgrade() throws IOException {
        LegacyChunkStore original = new LegacyChunkStore(mockWorld, 12, 32);
        original.setTrue(14, 89, 12);
        original.setTrue(14, 90, 12);
        original.setTrue(13, 89, 12);
        byte[] serializedBytes = serializeChunkStore(original);
        ChunkStore deserialized = BitSetChunkStore.Serialization.readChunkStore(new DataInputStream(new ByteArrayInputStream(serializedBytes)));
        assert deserialized != null;
        assertChunkStoreEquals(original, deserialized);
    }

    @Test
    void testSimpleRegionRoundTrip() throws IOException {
        LegacyChunkStore original = new LegacyChunkStore(mockWorld, 12, 12);
        original.setTrue(14, 89, 12);
        original.setTrue(14, 90, 12);
        original.setTrue(13, 89, 12);
        File file = new File(tempDir, "SimpleRegionRoundTrip.region");
        McMMOSimpleRegionFile region = new McMMOSimpleRegionFile(file, 0, 0);
        try (DataOutputStream outputStream = region.getOutputStream(12, 12)) {
            outputStream.write(serializeChunkStore(original));
        }
        region.close();
        region = new McMMOSimpleRegionFile(file, 0, 0);
        try (DataInputStream is = region.getInputStream(original.getChunkX(), original.getChunkZ())) {
            Assertions.assertNotNull(is);
            ChunkStore deserialized = BitSetChunkStore.Serialization.readChunkStore(is);
            assert deserialized != null;
            assertChunkStoreEquals(original, deserialized);
        }
        region.close();
        file.delete();
    }

}