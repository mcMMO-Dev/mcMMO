package com.gmail.nossr50.util.blockmeta;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.compat.CompatibilityManager;
import com.gmail.nossr50.util.compat.layers.world.WorldCompatibilityLayer;
import com.gmail.nossr50.util.platform.PlatformManager;
import com.google.common.io.Files;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.*;
import java.util.UUID;

import static org.mockito.Mockito.mock;

/**
 * Could be a lot better.  But some tests are better than none!  Tests the major things, still kinda unit-testy.  Verifies that the serialization isn't completely broken.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Bukkit.class, mcMMO.class})
public class ChunkStoreTest {
    public static final int LEGACY_WORLD_HEIGHT_MAX = 256;
    public static final int LEGACY_WORLD_HEIGHT_MIN = 0;
    private static File tempDir;
    @BeforeClass
    public static void setUpClass() {
        tempDir = Files.createTempDir();
    }

    @AfterClass
    public static void tearDownClass() {
        recursiveDelete(tempDir);
    }

    private World mockWorld;
    private CompatibilityManager compatibilityManager;
    private WorldCompatibilityLayer worldCompatibilityLayer;
    private PlatformManager platformManager;

    @Before
    public void setUpMock(){
        UUID worldUUID = UUID.randomUUID();
        mockWorld = mock(World.class);
        Mockito.when(mockWorld.getUID()).thenReturn(worldUUID);
        Mockito.when(mockWorld.getMaxHeight()).thenReturn(256);
        Mockito.when(mockWorld.getWorldFolder()).thenReturn(tempDir);
        PowerMockito.mockStatic(Bukkit.class);
        Mockito.when(Bukkit.getWorld(worldUUID)).thenReturn(mockWorld);

        platformManager = mock(PlatformManager.class);
        compatibilityManager = mock(CompatibilityManager.class);
        worldCompatibilityLayer = mock(WorldCompatibilityLayer.class);

        Whitebox.setInternalState(mcMMO.class, "platformManager", platformManager);
        Mockito.when(mcMMO.getCompatibilityManager()).thenReturn(compatibilityManager);

        Assert.assertNotNull(mcMMO.getCompatibilityManager());
        Mockito.when(platformManager.getCompatibilityManager()).thenReturn(compatibilityManager);
        Mockito.when(platformManager.getCompatibilityManager().getWorldCompatibilityLayer()).thenReturn(worldCompatibilityLayer);
        Assert.assertNotNull(mcMMO.getCompatibilityManager().getWorldCompatibilityLayer());
        Mockito.when(worldCompatibilityLayer.getMinWorldHeight(mockWorld)).thenReturn(LEGACY_WORLD_HEIGHT_MIN);
        Mockito.when(worldCompatibilityLayer.getMaxWorldHeight(mockWorld)).thenReturn(LEGACY_WORLD_HEIGHT_MAX);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIndexOutOfBounds() {
        Mockito.when(mcMMO.getCompatibilityManager().getWorldCompatibilityLayer().getMinWorldHeight(mockWorld)).thenReturn(-64);
        HashChunkManager hashChunkManager = new HashChunkManager();
        
        //Top Block
        Block illegalHeightBlock = initMockBlock(1337, 256, -1337);
        Assert.assertFalse(hashChunkManager.isTrue(illegalHeightBlock));
        hashChunkManager.setTrue(illegalHeightBlock);
    }

    @Test
    public void testSetTrue() {
        Mockito.when(mcMMO.getCompatibilityManager().getWorldCompatibilityLayer().getMinWorldHeight(mockWorld)).thenReturn(-64);
        HashChunkManager hashChunkManager = new HashChunkManager();
        int radius = 2; //Could be anything but drastically changes test time

        for(int x = -radius; x <= radius; x++) {
            for(int y = mockWorld.getMinHeight(); y < mockWorld.getMaxHeight(); y++) {
                for(int z = -radius; z <= radius; z++) {
                    Block testBlock = initMockBlock(x, y, z);

                    hashChunkManager.setTrue(testBlock);
                    Assert.assertTrue(hashChunkManager.isTrue(testBlock));
                    hashChunkManager.setFalse(testBlock);
                    Assert.assertFalse(hashChunkManager.isTrue(testBlock));
                }
            }
        }

        //Bot Block
        Block bottomBlock = initMockBlock(1337, 0, -1337);
        Assert.assertFalse(hashChunkManager.isTrue(bottomBlock));

        Assert.assertTrue(BlockUtils.isWithinWorldBounds(worldCompatibilityLayer, bottomBlock));
        hashChunkManager.setTrue(bottomBlock);
        Assert.assertTrue(hashChunkManager.isTrue(bottomBlock));

        //Top Block
        Block topBlock = initMockBlock(1337, 255, -1337);
        Assert.assertFalse(hashChunkManager.isTrue(topBlock));

        Assert.assertTrue(BlockUtils.isWithinWorldBounds(worldCompatibilityLayer, topBlock));
        hashChunkManager.setTrue(topBlock);
        Assert.assertTrue(hashChunkManager.isTrue(topBlock));
    }

    @Test
    public void testSetValue() {
        BitSetChunkStore original = new BitSetChunkStore(mockWorld, 0, 0);
        original.setTrue(0, 0, 0);
        Assert.assertTrue(original.isTrue(0, 0, 0));
        original.setFalse(0, 0, 0);
        Assert.assertFalse(original.isTrue(0, 0, 0));
    }

    @Test
    public void testIsEmpty() {
        BitSetChunkStore original = new BitSetChunkStore(mockWorld, 0, 0);
        Assert.assertTrue(original.isEmpty());
        original.setTrue(0, 0, 0);
        original.setFalse(0, 0, 0);
        Assert.assertTrue(original.isEmpty());
    }

    @Test
    public void testRoundTrip() throws IOException {
        BitSetChunkStore original = new BitSetChunkStore(mockWorld, 1, 2);
        original.setTrue(14, 89, 12);
        original.setTrue(14, 90, 12);
        original.setTrue(13, 89, 12);
        byte[] serializedBytes = serializeChunkstore(original);
        ChunkStore deserialized = BitSetChunkStore.Serialization.readChunkStore(new DataInputStream(new ByteArrayInputStream(serializedBytes)));
        assertEqual(original, deserialized);
    }

    @Test
    public void testNegativeWorldMin() throws IOException {
        Mockito.when(mcMMO.getCompatibilityManager().getWorldCompatibilityLayer().getMinWorldHeight(mockWorld)).thenReturn(-64);

        BitSetChunkStore original = new BitSetChunkStore(mockWorld, 1, 2);
        original.setTrue(14, -32, 12);
        original.setTrue(14, -64, 12);
        original.setTrue(13, -63, 12);
        byte[] serializedBytes = serializeChunkstore(original);
        ChunkStore deserialized = BitSetChunkStore.Serialization.readChunkStore(new DataInputStream(new ByteArrayInputStream(serializedBytes)));
        assertEqual(original, deserialized);
    }

    @Test
    public void testNegativeWorldMinUpgrade() throws IOException {
        BitSetChunkStore original = new BitSetChunkStore(mockWorld, 1, 2);
        original.setTrue(14, 1, 12);
        original.setTrue(14, 2, 12);
        original.setTrue(13, 3, 12);
        byte[] serializedBytes = serializeChunkstore(original);

        Mockito.when(mcMMO.getCompatibilityManager().getWorldCompatibilityLayer().getMinWorldHeight(mockWorld)).thenReturn(-64);
        ChunkStore deserialized = BitSetChunkStore.Serialization.readChunkStore(new DataInputStream(new ByteArrayInputStream(serializedBytes)));
        assertEqualIgnoreMinMax(original, deserialized);
    }

    @Test
    public void testChunkCoords() throws IOException {
        for (int x = -96; x < 0; x++) {
                int cx = x >> 4;
                int ix = Math.abs(x) % 16;
                System.out.print(cx + ":" + ix + "  ");
        }
    }

    @Test
    public void testUpgrade() throws IOException {
        LegacyChunkStore original = new LegacyChunkStore(mockWorld, 12, 32);
        original.setTrue(14, 89, 12);
        original.setTrue(14, 90, 12);
        original.setTrue(13, 89, 12);
        byte[] serializedBytes = serializeChunkstore(original);
        ChunkStore deserialized = BitSetChunkStore.Serialization.readChunkStore(new DataInputStream(new ByteArrayInputStream(serializedBytes)));
        assertEqual(original, deserialized);
    }

    @Test
    public void testSimpleRegionRoundtrip() throws IOException {
        LegacyChunkStore original = new LegacyChunkStore(mockWorld, 12, 12);
        original.setTrue(14, 89, 12);
        original.setTrue(14, 90, 12);
        original.setTrue(13, 89, 12);
        File file = new File(tempDir, "SimpleRegionRoundTrip.region");
        McMMOSimpleRegionFile region = new McMMOSimpleRegionFile(file, 0, 0);
        try (DataOutputStream outputStream = region.getOutputStream(12, 12)){
            outputStream.write(serializeChunkstore(original));
        }
        region.close();
        region = new McMMOSimpleRegionFile(file, 0, 0);
        try (DataInputStream is = region.getInputStream(original.getChunkX(), original.getChunkZ()))
        {
            Assert.assertNotNull(is);
            ChunkStore deserialized = BitSetChunkStore.Serialization.readChunkStore(is);
            assertEqual(original, deserialized);
        }
        region.close();
        file.delete();
    }

    @Test
    public void testSimpleRegionRejectsOutOfBounds() {
        File file = new File(tempDir, "SimpleRegionRoundTrip.region");
        McMMOSimpleRegionFile region = new McMMOSimpleRegionFile(file, 0, 0);
        assertThrows(() -> region.getOutputStream(-1, 0), IndexOutOfBoundsException.class);
        assertThrows(() -> region.getOutputStream(0, -1), IndexOutOfBoundsException.class);
        assertThrows(() -> region.getOutputStream(32, 0), IndexOutOfBoundsException.class);
        assertThrows(() -> region.getOutputStream(0, 32), IndexOutOfBoundsException.class);
        region.close();
    }

    @Test
    public void testChunkStoreRejectsOutOfBounds() {
        ChunkStore chunkStore = new BitSetChunkStore(mockWorld, 0, 0);
        assertThrows(() -> chunkStore.setTrue(-1, 0, 0), IndexOutOfBoundsException.class);
        assertThrows(() -> chunkStore.setTrue(0, -1, 0), IndexOutOfBoundsException.class);
        assertThrows(() -> chunkStore.setTrue(0, 0, -1), IndexOutOfBoundsException.class);
        assertThrows(() -> chunkStore.setTrue(16, 0, 0), IndexOutOfBoundsException.class);
        assertThrows(() -> chunkStore.setTrue(0, mockWorld.getMaxHeight(), 0), IndexOutOfBoundsException.class);
        assertThrows(() -> chunkStore.setTrue(0, 0, 16), IndexOutOfBoundsException.class);
    }

    @Test
    public void testRegressionChunkMirrorBug() {
        ChunkManager chunkManager = new HashChunkManager();
        Block mockBlockA = mock(Block.class);
        Mockito.when(mockBlockA.getX()).thenReturn(15);
        Mockito.when(mockBlockA.getZ()).thenReturn(15);
        Mockito.when(mockBlockA.getY()).thenReturn(0);
        Mockito.when(mockBlockA.getWorld()).thenReturn(mockWorld);
        Block mockBlockB = mock(Block.class);
        Mockito.when(mockBlockB.getX()).thenReturn(-15);
        Mockito.when(mockBlockB.getZ()).thenReturn(-15);
        Mockito.when(mockBlockB.getY()).thenReturn(0);
        Mockito.when(mockBlockB.getWorld()).thenReturn(mockWorld);

        chunkManager.setTrue(mockBlockA);
        chunkManager.setFalse(mockBlockB);
        Assert.assertTrue(chunkManager.isTrue(mockBlockA));
    }

    private interface Delegate {

        void run();
    }
    private void assertThrows(@NotNull Delegate delegate, @NotNull Class<?> clazz) {
        try {
            delegate.run();
            Assert.fail(); // We didn't throw
        }
        catch (Throwable t) {
            Assert.assertTrue(t.getClass().equals(clazz));
        }
    }

    private void assertEqual(ChunkStore expected, ChunkStore actual)
    {
        Assert.assertEquals(expected.getChunkMin(), actual.getChunkMin());
        Assert.assertEquals(expected.getChunkMax(), actual.getChunkMax());
        assertEqualIgnoreMinMax(expected, actual);
    }

    private void assertEqualIgnoreMinMax(ChunkStore expected, ChunkStore actual)
    {
        Assert.assertEquals(expected.getChunkX(), actual.getChunkX());
        Assert.assertEquals(expected.getChunkZ(), actual.getChunkZ());
        Assert.assertEquals(expected.getWorldId(), actual.getWorldId());
        for (int y = Math.min(actual.getChunkMin(), expected.getChunkMin()); y < Math.max(actual.getChunkMax(), expected.getChunkMax()); y++)
        {
            if (expected.getChunkMin() > y || actual.getChunkMin() > y || expected.getChunkMax() <= y || actual.getChunkMax() <= y)
                continue; // Ignore
            for (int x = 0; x < 16; x++)
                for (int z = 0; z < 16; z++)
                    Assert.assertTrue(expected.isTrue(x, y, z) == actual.isTrue(x, y, z));
        }
    }

    private static byte[] serializeChunkstore(@NotNull ChunkStore chunkStore) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (chunkStore instanceof BitSetChunkStore)
            BitSetChunkStore.Serialization.writeChunkStore(new DataOutputStream(byteArrayOutputStream), chunkStore);
        else
            new UnitTestObjectOutputStream(byteArrayOutputStream).writeObject(chunkStore); // Serializes the class as if it were the old PrimitiveChunkStore
        return byteArrayOutputStream.toByteArray();
    }

    public static class LegacyChunkStore implements ChunkStore, Serializable {

        private static final long serialVersionUID = -1L;
        transient private boolean dirty = false;
        public boolean[][][] store;
        private static final int CURRENT_VERSION = 7;
        private static final int MAGIC_NUMBER = 0xEA5EDEBB;
        private final int cx;
        private final int cz;
        private final @NotNull UUID worldUid;
        public LegacyChunkStore(@NotNull World world, int cx, int cz) {
            this.cx = cx;
            this.cz = cz;
            this.worldUid = world.getUID();
            this.store = new boolean[16][16][world.getMaxHeight()];
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
            return 0;
        }

        @Override
        public int getChunkMax() {
            return store[0][0].length;
        }

        @Override
        public @NotNull UUID getWorldId() {
            return worldUid;
        }

        @Override
        public boolean isTrue(int x, int y, int z) {
            return store[x][z][y];
        }

        @Override
        public void setTrue(int x, int y, int z) {
            if (y >= store[0][0].length || y < 0)
                return;
            store[x][z][y] = true;
            dirty = true;
        }

        @Override
        public void setFalse(int x, int y, int z) {
            if (y >= store[0][0].length || y < 0)
                return;
            store[x][z][y] = false;
            dirty = true;
        }

        @Override
        public void set(int x, int y, int z, boolean value) {
            if (y >= store[0][0].length || y < 0)
                return;
            store[x][z][y] = value;
            dirty = true;
        }

        @Override
        public boolean isEmpty() {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < store[0][0].length; y++) {
                        if (store[x][z][y]) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        private void writeObject(@NotNull ObjectOutputStream out) throws IOException {
            out.writeInt(MAGIC_NUMBER);
            out.writeInt(CURRENT_VERSION);

            out.writeLong(worldUid.getLeastSignificantBits());
            out.writeLong(worldUid.getMostSignificantBits());
            out.writeInt(cx);
            out.writeInt(cz);
            out.writeObject(store);

            dirty = false;
        }

        private void readObject(@NotNull ObjectInputStream in) throws IOException, ClassNotFoundException {
            throw new UnsupportedOperationException();
        }

    }
    private static class UnitTestObjectOutputStream extends ObjectOutputStream {

        public UnitTestObjectOutputStream(@NotNull OutputStream outputStream) throws IOException {
            super(outputStream);
        }
        @Override
        public void writeUTF(@NotNull String str) throws IOException {
            // Pretend to be the old class
            if (str.equals(LegacyChunkStore.class.getName()))
                str = "com.gmail.nossr50.util.blockmeta.chunkmeta.PrimitiveChunkStore";
            super.writeUTF(str);
        }

    }
    @NotNull
    private Block initMockBlock(int x, int y, int z) {
        Block testBlock = mock(Block.class);
        Mockito.when(testBlock.getX()).thenReturn(x);
        Mockito.when(testBlock.getY()).thenReturn(y);
        Mockito.when(testBlock.getZ()).thenReturn(z);
        Mockito.when(testBlock.getWorld()).thenReturn(mockWorld);
        return testBlock;
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
