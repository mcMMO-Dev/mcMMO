package com.gmail.nossr50.util.blockmeta;

import static com.gmail.nossr50.util.blockmeta.BlockStoreTestUtils.LEGACY_WORLD_HEIGHT_MAX;
import static com.gmail.nossr50.util.blockmeta.BlockStoreTestUtils.LEGACY_WORLD_HEIGHT_MIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Stress and scale-oriented migration tests for {@link McMMORegionBackupStore}.
 *
 * <p>These tests intentionally generate large datasets and are tagged as {@code stress} so
 * default Surefire runs can skip them.
 */
@Tag("stress")
class McMMORegionBackupStoreStressTest {

    private static final class PlacedBlockExpectation {
        private final int chunkX;
        private final int chunkZ;
        private final int[][] expectedTrueBits;

        private PlacedBlockExpectation(int chunkX, int chunkZ, int[][] expectedTrueBits) {
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.expectedTrueBits = expectedTrueBits;
        }
    }

    private enum WorldDatasetMode {
        RANDOM_DENSE,
        ALL_TRUE,
        ALL_FALSE,
        NO_DATA
    }

    private static final class WorldStressConfig {
        private final WorldDatasetMode datasetMode;

        private WorldStressConfig(WorldDatasetMode datasetMode) {
            this.datasetMode = datasetMode;
        }
    }

    private static final class MigrationStressScenario {
        private final String name;
        private final WorldStressConfig overworldConfig;
        private final WorldStressConfig netherConfig;
        private final WorldStressConfig endConfig;

        private MigrationStressScenario(String name, WorldStressConfig overworldConfig,
                WorldStressConfig netherConfig, WorldStressConfig endConfig) {
            this.name = name;
            this.overworldConfig = overworldConfig;
            this.netherConfig = netherConfig;
            this.endConfig = endConfig;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @TempDir
    Path containerRoot;

    @TempDir
    Path pluginDataRoot;

    private World mockWorld;
    private UUID worldUid;
    private MockedStatic<Bukkit> bukkitMock;
    private final Logger silentLogger = Logger.getLogger("McMMORegionBackupStoreStressTest");

    @BeforeEach
    void setUp() {
        worldUid = UUID.randomUUID();
        mockWorld = Mockito.mock(World.class);
        when(mockWorld.getUID()).thenReturn(worldUid);
        when(mockWorld.getMinHeight()).thenReturn(LEGACY_WORLD_HEIGHT_MIN);
        when(mockWorld.getMaxHeight()).thenReturn(LEGACY_WORLD_HEIGHT_MAX);

        bukkitMock = mockStatic(Bukkit.class);
        bukkitMock.when(() -> Bukkit.getWorld(worldUid)).thenReturn(mockWorld);
    }

    @AfterEach
    void tearDown() {
        bukkitMock.close();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("migrationStressScenarios")
    void restoresLargeMigrationDatasetAcrossScenarios(MigrationStressScenario migrationStressScenario)
            throws IOException {
        // Given
        final int regionFilesPerWorld = 1000;
        final String overworldName = "world";
        final String netherWorldName = "world_nether";
        final String endWorldName = "world_the_end";

        final Path overworldLegacyFolder = legacyWorldFolder(overworldName);
        final Path netherLegacyFolder = legacyWorldFolder(netherWorldName);
        final Path endLegacyFolder = legacyWorldFolder(endWorldName);

        final Map<String, List<PlacedBlockExpectation>> expectedPlacedBlocksByWorld = new HashMap<>();
        expectedPlacedBlocksByWorld.put(overworldName,
                writeLegacyRegionDataset(overworldLegacyFolder, regionFilesPerWorld,
                        migrationStressScenario.overworldConfig.datasetMode));
        expectedPlacedBlocksByWorld.put(netherWorldName,
                writeLegacyRegionDataset(netherLegacyFolder, regionFilesPerWorld,
                        migrationStressScenario.netherConfig.datasetMode));
        expectedPlacedBlocksByWorld.put(endWorldName,
                writeLegacyRegionDataset(endLegacyFolder, regionFilesPerWorld,
                        migrationStressScenario.endConfig.datasetMode));

        // When
        // Step 1: Simulate shutdown on legacy shape by writing migration backups.
        final Clock backupClock = fixedUtc("2026-06-01T17:50:22Z");
        McMMORegionBackupStore.backup(containerRoot, pluginDataRoot, overworldName,
                overworldLegacyFolder, silentLogger, backupClock);
        McMMORegionBackupStore.backup(containerRoot, pluginDataRoot, netherWorldName,
                netherLegacyFolder, silentLogger, backupClock);
        McMMORegionBackupStore.backup(containerRoot, pluginDataRoot, endWorldName,
                endLegacyFolder, silentLogger, backupClock);

        // Step 2: Simulate startup on new Paper layout by restoring backups into new shape.
        final Path overworldNewShapeFolder = newPaperWorldFolder(overworldName, "overworld");
        final Path netherNewShapeFolder = newPaperWorldFolder(netherWorldName, "the_nether");
        final Path endNewShapeFolder = newPaperWorldFolder(endWorldName, "the_end");

        McMMORegionBackupStore.restore(containerRoot, pluginDataRoot, overworldName,
                overworldNewShapeFolder, silentLogger);
        McMMORegionBackupStore.restore(containerRoot, pluginDataRoot, netherWorldName,
                netherNewShapeFolder, silentLogger);
        McMMORegionBackupStore.restore(containerRoot, pluginDataRoot, endWorldName,
                endNewShapeFolder, silentLogger);

        // Then
        assertRestoredDatasetContainsAllExpectedPlacedBlocks(
                inWorld(overworldNewShapeFolder),
                expectedPlacedBlocksByWorld.get(overworldName));
        assertRestoredDatasetContainsAllExpectedPlacedBlocks(
                inWorld(netherNewShapeFolder),
                expectedPlacedBlocksByWorld.get(netherWorldName));
        assertRestoredDatasetContainsAllExpectedPlacedBlocks(
                inWorld(endNewShapeFolder),
                expectedPlacedBlocksByWorld.get(endWorldName));

        // Worlds with NO_DATA should remain a no-op after backup+restore.
        assertNoOpWhenWorldHasNoMigrationDataset(
                inWorld(overworldNewShapeFolder),
                expectedPlacedBlocksByWorld.get(overworldName),
                migrationStressScenario.overworldConfig.datasetMode);
        assertNoOpWhenWorldHasNoMigrationDataset(
                inWorld(netherNewShapeFolder),
                expectedPlacedBlocksByWorld.get(netherWorldName),
                migrationStressScenario.netherConfig.datasetMode);
        assertNoOpWhenWorldHasNoMigrationDataset(
                inWorld(endNewShapeFolder),
                expectedPlacedBlocksByWorld.get(endWorldName),
                migrationStressScenario.endConfig.datasetMode);
    }

    @Test
    void skipsCorruptSnapshotFilesAndKeepsOtherData() throws IOException {
        // Given
        final int regionFilesPerWorld = 1000;
        final String overworldName = "world";
        final String netherWorldName = "world_nether";
        final String endWorldName = "world_the_end";

        final Path overworldLegacyFolder = legacyWorldFolder(overworldName);
        final Path netherLegacyFolder = legacyWorldFolder(netherWorldName);
        final Path endLegacyFolder = legacyWorldFolder(endWorldName);

        final List<PlacedBlockExpectation> overworldExpectations = writeLegacyRegionDataset(
                overworldLegacyFolder, regionFilesPerWorld, WorldDatasetMode.RANDOM_DENSE);
        final List<PlacedBlockExpectation> netherExpectations = writeLegacyRegionDataset(
                netherLegacyFolder, regionFilesPerWorld, WorldDatasetMode.RANDOM_DENSE);
        final List<PlacedBlockExpectation> endExpectations = writeLegacyRegionDataset(
                endLegacyFolder, regionFilesPerWorld, WorldDatasetMode.RANDOM_DENSE);

        // And
        final Clock backupClock = fixedUtc("2026-06-01T18:05:00Z");
        McMMORegionBackupStore.backup(containerRoot, pluginDataRoot, overworldName,
                overworldLegacyFolder, silentLogger, backupClock);
        McMMORegionBackupStore.backup(containerRoot, pluginDataRoot, netherWorldName,
                netherLegacyFolder, silentLogger, backupClock);
        McMMORegionBackupStore.backup(containerRoot, pluginDataRoot, endWorldName,
                endLegacyFolder, silentLogger, backupClock);

        final Path newestOverworldSnapshot = McMMORegionBackupStore.newestCompleteSnapshot(
                worldBackupRoot(overworldName));
        assertThat(newestOverworldSnapshot).isNotNull();
        final Path corruptSnapshotFile = newestOverworldSnapshot.resolve("mcmmo_0_0_.mcm");
        Files.writeString(corruptSnapshotFile, "corrupt-data");

        final Path overworldNewShapeFolder = newPaperWorldFolder(overworldName, "overworld");
        writeRegionFileWithChunk(inWorld(overworldNewShapeFolder), 0, 0,
                new int[][] { { 9, 9, 9 } });

        // When
        final Path netherNewShapeFolder = newPaperWorldFolder(netherWorldName, "the_nether");
        final Path endNewShapeFolder = newPaperWorldFolder(endWorldName, "the_end");
        McMMORegionBackupStore.restore(containerRoot, pluginDataRoot, overworldName,
                overworldNewShapeFolder, silentLogger);
        McMMORegionBackupStore.restore(containerRoot, pluginDataRoot, netherWorldName,
                netherNewShapeFolder, silentLogger);
        McMMORegionBackupStore.restore(containerRoot, pluginDataRoot, endWorldName,
                endNewShapeFolder, silentLogger);

        // Then
        assertRestoredDatasetContainsAllExpectedPlacedBlocks(
                inWorld(netherNewShapeFolder), netherExpectations);
        assertRestoredDatasetContainsAllExpectedPlacedBlocks(
                inWorld(endNewShapeFolder), endExpectations);

        final ChunkStore preservedChunk = readChunkFromRegionFile(
                inWorld(overworldNewShapeFolder).resolve("mcmmo_0_0_.mcm"), 0, 0);
        assertThat(preservedChunk).isNotNull();
        assertThat(preservedChunk.isTrue(9, 9, 9)).isTrue();

        final PlacedBlockExpectation safeOverworldExpectation = overworldExpectations.stream()
                .filter(placedBlockExpectation -> placedBlockExpectation.chunkX != 0)
                .findFirst()
                .orElseThrow();
        final Path safeOverworldRegionFile = inWorld(overworldNewShapeFolder).resolve(
                "mcmmo_" + (safeOverworldExpectation.chunkX >> 5) + "_"
                        + (safeOverworldExpectation.chunkZ >> 5) + "_.mcm");
        final ChunkStore restoredSafeChunk = readChunkFromRegionFile(safeOverworldRegionFile,
                safeOverworldExpectation.chunkX, safeOverworldExpectation.chunkZ);
        assertThat(restoredSafeChunk).isNotNull();
        for (int[] expectedTrueBit : safeOverworldExpectation.expectedTrueBits) {
            assertThat(restoredSafeChunk.isTrue(expectedTrueBit[0], expectedTrueBit[1],
                    expectedTrueBit[2])).isTrue();
        }
    }

    private Path writeRegionFileWithChunk(Path regionFolder, int chunkX, int chunkZ,
            int[][] trueBits) throws IOException {
        Files.createDirectories(regionFolder);
        final Path regionFile = regionFolder.resolve(
                "mcmmo_" + (chunkX >> 5) + "_" + (chunkZ >> 5) + "_.mcm");
        final BitSetChunkStore store = new BitSetChunkStore(mockWorld, chunkX, chunkZ);
        for (int[] xyz : trueBits) {
            store.setTrue(xyz[0], xyz[1], xyz[2]);
        }
        final McMMOSimpleRegionFile regionFileStore = new McMMOSimpleRegionFile(
                regionFile.toFile(), chunkX >> 5, chunkZ >> 5);
        try (DataOutputStream out = regionFileStore.getOutputStream(chunkX, chunkZ)) {
            BitSetChunkStore.Serialization.writeChunkStore(out, store);
        }
        regionFileStore.close();
        return regionFile;
    }

    private ChunkStore readChunkFromRegionFile(Path regionFile, int chunkX, int chunkZ)
            throws IOException {
        final McMMOSimpleRegionFile regionFileStore = new McMMOSimpleRegionFile(
                regionFile.toFile(), chunkX >> 5, chunkZ >> 5);
        try (DataInputStream in = regionFileStore.getInputStream(chunkX, chunkZ)) {
            if (in == null) {
                return null;
            }
            return BitSetChunkStore.Serialization.readChunkStore(in);
        } finally {
            regionFileStore.close();
        }
    }

    private Path legacyWorldFolder(String worldName) {
        return containerRoot.resolve(worldName);
    }

    private Path newPaperWorldFolder(String worldName, String dimensionKey) {
        return containerRoot.resolve(worldName).resolve("dimensions").resolve("minecraft")
                .resolve(dimensionKey);
    }

    private Path inWorld(Path worldFolder) {
        return worldFolder.resolve(McMMORegionBackupStore.IN_WORLD_FOLDER_NAME);
    }

    private Path worldBackupRoot(String worldName) {
        return pluginDataRoot.resolve(McMMORegionBackupStore.BACKUP_ROOT_FOLDER_NAME)
                .resolve(worldName);
    }

    private static Clock fixedUtc(String isoInstant) {
        return Clock.fixed(Instant.parse(isoInstant), ZoneOffset.UTC);
    }

    private List<PlacedBlockExpectation> writeLegacyRegionDataset(
            Path legacyWorldFolder, int regionFileCount, WorldDatasetMode worldDatasetMode)
            throws IOException {
        if (worldDatasetMode == WorldDatasetMode.NO_DATA) {
            return new ArrayList<>();
        }

        final List<PlacedBlockExpectation> expectations = new ArrayList<>(regionFileCount);
        final Path legacyRegionFolder = inWorld(legacyWorldFolder);

        for (int regionIndex = 0; regionIndex < regionFileCount; regionIndex++) {
            final int chunkX = regionIndex << 5;
            final int chunkZ = 0;

            final int[][] regionFileTrueBits = switch (worldDatasetMode) {
                case RANDOM_DENSE -> {
                    final int minimumTrueValuesPerRegionFile = 20;
                    final int maximumAdditionalTrueValuesPerRegionFile = 20;
                    final int trueValueCountForRegionFile = minimumTrueValuesPerRegionFile
                            + ThreadLocalRandom.current().nextInt(
                                    maximumAdditionalTrueValuesPerRegionFile + 1);
                    yield generateUniqueRandomTrueBits(trueValueCountForRegionFile);
                }
                case ALL_TRUE -> generateDeterministicAllTrueBits();
                case ALL_FALSE -> new int[][] {};
                case NO_DATA -> throw new IllegalStateException(
                        "NO_DATA should return before file generation");
            };

            writeRegionFileWithChunk(legacyRegionFolder, chunkX, chunkZ, regionFileTrueBits);
            expectations.add(new PlacedBlockExpectation(chunkX, chunkZ, regionFileTrueBits));
        }

        return expectations;
    }

    private int[][] generateDeterministicAllTrueBits() {
        final List<int[]> allTrueBits = new ArrayList<>();
        for (int blockX = 0; blockX < 4; blockX++) {
            for (int blockZ = 0; blockZ < 4; blockZ++) {
                for (int blockY = LEGACY_WORLD_HEIGHT_MIN; blockY < LEGACY_WORLD_HEIGHT_MIN
                        + 4; blockY++) {
                    allTrueBits.add(new int[] { blockX, blockY, blockZ });
                }
            }
        }
        return allTrueBits.toArray(int[][]::new);
    }

    private int[][] generateUniqueRandomTrueBits(int trueValueCount) {
        final List<int[]> randomizedTrueBits = new ArrayList<>(trueValueCount);
        final Set<Long> usedCoordinates = new HashSet<>(trueValueCount * 2);

        while (randomizedTrueBits.size() < trueValueCount) {
            final int randomizedBlockX = ThreadLocalRandom.current().nextInt(0, 16);
            final int randomizedBlockY = ThreadLocalRandom.current()
                    .nextInt(LEGACY_WORLD_HEIGHT_MIN, LEGACY_WORLD_HEIGHT_MAX);
            final int randomizedBlockZ = ThreadLocalRandom.current().nextInt(0, 16);

            final long coordinateKey = (((long) randomizedBlockX) << 40)
                    | (((long) (randomizedBlockY - LEGACY_WORLD_HEIGHT_MIN)) << 8)
                    | randomizedBlockZ;

            if (!usedCoordinates.add(coordinateKey)) {
                continue;
            }

            randomizedTrueBits.add(new int[] { randomizedBlockX, randomizedBlockY, randomizedBlockZ });
        }

        return randomizedTrueBits.toArray(int[][]::new);
    }

    private void assertRestoredDatasetContainsAllExpectedPlacedBlocks(Path restoredRegionFolder,
            List<PlacedBlockExpectation> expectations) throws IOException {
        for (PlacedBlockExpectation expectedPlacedBlock : expectations) {
            final Path expectedRegionFile = restoredRegionFolder.resolve(
                    "mcmmo_" + (expectedPlacedBlock.chunkX >> 5) + "_"
                            + (expectedPlacedBlock.chunkZ >> 5) + "_.mcm");

            assertThat(Files.isRegularFile(expectedRegionFile)).isTrue();

            final ChunkStore restoredChunkStore = readChunkFromRegionFile(expectedRegionFile,
                    expectedPlacedBlock.chunkX, expectedPlacedBlock.chunkZ);
            assertThat(restoredChunkStore).isNotNull();
            for (int[] expectedTrueBit : expectedPlacedBlock.expectedTrueBits) {
                assertThat(restoredChunkStore.isTrue(expectedTrueBit[0],
                        expectedTrueBit[1], expectedTrueBit[2])).isTrue();
            }
        }
    }

    private void assertNoOpWhenWorldHasNoMigrationDataset(Path restoredRegionFolder,
            List<PlacedBlockExpectation> expectations, WorldDatasetMode worldDatasetMode) {
        if (worldDatasetMode != WorldDatasetMode.NO_DATA) {
            return;
        }

        assertThat(expectations).isEmpty();
        assertThat(Files.exists(restoredRegionFolder)).isFalse();
    }

    private static Stream<Arguments> migrationStressScenarios() {
        return Stream.of(
                Arguments.of(new MigrationStressScenario(
                        "scenario1_denseRandom_allWorlds",
                        new WorldStressConfig(WorldDatasetMode.RANDOM_DENSE),
                        new WorldStressConfig(WorldDatasetMode.RANDOM_DENSE),
                        new WorldStressConfig(WorldDatasetMode.RANDOM_DENSE))),
                Arguments.of(new MigrationStressScenario(
                        "scenario2_allTrue_allWorlds",
                        new WorldStressConfig(WorldDatasetMode.ALL_TRUE),
                        new WorldStressConfig(WorldDatasetMode.ALL_TRUE),
                        new WorldStressConfig(WorldDatasetMode.ALL_TRUE))),
                Arguments.of(new MigrationStressScenario(
                        "scenario3_allFalse_allWorlds",
                        new WorldStressConfig(WorldDatasetMode.ALL_FALSE),
                        new WorldStressConfig(WorldDatasetMode.ALL_FALSE),
                        new WorldStressConfig(WorldDatasetMode.ALL_FALSE))),
                Arguments.of(new MigrationStressScenario(
                        "scenario4_oneWorldNoData",
                        new WorldStressConfig(WorldDatasetMode.RANDOM_DENSE),
                        new WorldStressConfig(WorldDatasetMode.RANDOM_DENSE),
                        new WorldStressConfig(WorldDatasetMode.NO_DATA))),
                Arguments.of(new MigrationStressScenario(
                        "scenario5_twoWorldsNoData",
                        new WorldStressConfig(WorldDatasetMode.RANDOM_DENSE),
                        new WorldStressConfig(WorldDatasetMode.NO_DATA),
                        new WorldStressConfig(WorldDatasetMode.NO_DATA))),
                Arguments.of(new MigrationStressScenario(
                        "scenario6_allWorldsNoData_noOp",
                        new WorldStressConfig(WorldDatasetMode.NO_DATA),
                        new WorldStressConfig(WorldDatasetMode.NO_DATA),
                        new WorldStressConfig(WorldDatasetMode.NO_DATA))),
                Arguments.of(new MigrationStressScenario(
                        "scenario7_mixedDenseTrueFalse",
                        new WorldStressConfig(WorldDatasetMode.RANDOM_DENSE),
                        new WorldStressConfig(WorldDatasetMode.ALL_TRUE),
                        new WorldStressConfig(WorldDatasetMode.ALL_FALSE))),
                Arguments.of(new MigrationStressScenario(
                        "scenario8_mixedFalseDenseNoData",
                        new WorldStressConfig(WorldDatasetMode.ALL_FALSE),
                        new WorldStressConfig(WorldDatasetMode.RANDOM_DENSE),
                        new WorldStressConfig(WorldDatasetMode.NO_DATA))));
    }
}
