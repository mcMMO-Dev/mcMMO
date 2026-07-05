package com.gmail.nossr50.util.blockmeta;

import static com.gmail.nossr50.util.blockmeta.BlockStoreTestUtils.LEGACY_WORLD_HEIGHT_MAX;
import static com.gmail.nossr50.util.blockmeta.BlockStoreTestUtils.LEGACY_WORLD_HEIGHT_MIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Tests for {@link McMMORegionBackupStore}.
 *
 * <p>The canonical on-disk location for mcMMO region files is
 * {@code [worldFolder]/mcmmo_regions/}. On Spigot / pre-26.1 Paper ("legacy shape") that
 * resolves to {@code [container]/[worldName]/mcmmo_regions/}; on Paper 26.1+ ("new shape") it
 * resolves to {@code [container]/[worldName]/dimensions/minecraft/<dim>/mcmmo_regions/}. To
 * survive Paper's destructive {@code LegacyCraftBukkitWorldMigration} (PR #13736), mcMMO writes
 * a flat snapshot of the legacy-shape data into the mcMMO plugin data directory under
 * {@code region_data_backups_for_migration/[worldName]/<timestamp>Z/} on shutdown, then restores the newest complete
 * snapshot into the new in-world location on the next startup once Paper has reshaped the world.
 *
 * <p>{@code containerRoot} is the simulated server container directory (equivalent to the
 * server working directory, parent of world folders on legacy shape).
 * {@code pluginDataRoot} is the simulated mcMMO plugin data directory (equivalent to
 * {@code plugins/mcMMO/}). These are kept separate to match the real on-disk layout where the
 * backup store lives inside the plugin folder, not inside a world folder.
 */
class McMMORegionBackupStoreTest {

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

    @TempDir
    Path containerRoot;

    @TempDir
    Path pluginDataRoot;

    private World mockWorld;
    private UUID worldUid;
    private MockedStatic<Bukkit> bukkitMock;
    private final Logger silentLogger = Logger.getLogger("McMMORegionBackupStoreTest");

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

    /**
     * Writes a region file containing one chunk with the given placed-block bits set to true.
     * The region file is named after the region coordinates derived from {@code chunkX} and
     * {@code chunkZ} (each shifted right 5 bits to get region-space coordinates).
     */
    private Path writeRegionFileWithChunk(Path regionFolder, int chunkX, int chunkZ,
            int[][] trueBits) throws IOException {
        Files.createDirectories(regionFolder);
        final Path regionFile = regionFolder.resolve(
                "mcmmo_" + (chunkX >> 5) + "_" + (chunkZ >> 5) + "_.mcm");
        final BitSetChunkStore store = new BitSetChunkStore(mockWorld, chunkX, chunkZ);
        for (int[] xyz : trueBits) {
            store.setTrue(xyz[0], xyz[1], xyz[2]);
        }
        final McMMOSimpleRegionFile rf = new McMMOSimpleRegionFile(
                regionFile.toFile(), chunkX >> 5, chunkZ >> 5);
        try (DataOutputStream out = rf.getOutputStream(chunkX, chunkZ)) {
            BitSetChunkStore.Serialization.writeChunkStore(out, store);
        }
        rf.close();
        return regionFile;
    }

    private ChunkStore readChunkFromRegionFile(Path regionFile, int chunkX, int chunkZ)
            throws IOException {
        final McMMOSimpleRegionFile rf = new McMMOSimpleRegionFile(
                regionFile.toFile(), chunkX >> 5, chunkZ >> 5);
        try (DataInputStream in = rf.getInputStream(chunkX, chunkZ)) {
            if (in == null) {
                return null;
            }
            return BitSetChunkStore.Serialization.readChunkStore(in);
        } finally {
            rf.close();
        }
    }

    /** Returns the world folder path for a world still on the Spigot / pre-26.1 Paper layout. */
    private Path legacyWorldFolder(String worldName) {
        return containerRoot.resolve(worldName);
    }

    /**
     * Returns the world folder path for a dimension on the Paper 26.1+ layout, where each
     * dimension lives under {@code [worldName]/dimensions/minecraft/<dimensionKey>/}.
     */
    private Path newPaperWorldFolder(String worldName, String dimensionKey) {
        return containerRoot.resolve(worldName).resolve("dimensions").resolve("minecraft")
                .resolve(dimensionKey);
    }

    /** Returns the in-world mcmmo_regions folder for the given world folder. */
    private Path inWorld(Path worldFolder) {
        return worldFolder.resolve(McMMORegionBackupStore.IN_WORLD_FOLDER_NAME);
    }

    /**
     * Returns the per-world backup-store folder inside the simulated plugin data directory.
         * On a real server this resolves to
         * {@code plugins/mcMMO/region_data_backups_for_migration/<worldName>/}.
     */
    private Path worldBackupRoot(String worldName) {
        return pluginDataRoot.resolve(McMMORegionBackupStore.BACKUP_ROOT_FOLDER_NAME)
                .resolve(worldName);
    }

        /** Returns the backup-store root folder inside the simulated plugin data directory. */
        private Path backupStoreRoot() {
                return pluginDataRoot.resolve(McMMORegionBackupStore.BACKUP_ROOT_FOLDER_NAME);
        }

        private Path archivedWorldBackupRoot(String worldName) {
                return backupStoreRoot().resolve(McMMORegionBackupStore.ARCHIVE_ROOT_FOLDER_NAME)
                                .resolve(worldName);
        }

    private static Clock fixedUtc(String isoInstant) {
        return Clock.fixed(Instant.parse(isoInstant), ZoneOffset.UTC);
    }

    private static String snapshotName(String isoInstant) {
        return McMMORegionBackupStore.SNAPSHOT_TIMESTAMP_FORMAT.format(
                Instant.parse(isoInstant));
    }

    @Nested
    class ShapeDetection {

        @Test
        void legacyShapeWhenWorldFolderEqualsContainerSlashWorldName() {
            // Given Spigot / pre-26.1 Paper layout
            assertThat(McMMORegionBackupStore.isLegacyShape(
                    containerRoot, "world_nether", legacyWorldFolder("world_nether"))).isTrue();
        }

        @Test
        void newShapeWhenWorldFolderHasDimensionsSubpath() {
            // Given Paper 26.1+ layout
            assertThat(McMMORegionBackupStore.isLegacyShape(
                    containerRoot, "world_nether",
                    newPaperWorldFolder("world_nether", "the_nether"))).isFalse();
        }

        @Test
        void normalisesDotSegmentsBeforeComparison() {
            // Given a non-normalised worldFolder with a trailing dot-segment
            final Path nonNormalised = containerRoot.resolve("world").resolve(".");
            assertThat(McMMORegionBackupStore.isLegacyShape(
                    containerRoot, "world", nonNormalised)).isTrue();
        }
    }

    @Nested
    class BackupWritesSnapshot {

        @Test
        void writesEveryInWorldRegionFileIntoTimestampedSnapshotWithSentinel() throws IOException {
            // Given a legacy-shape world with two region files in-world
            final String worldName = "world";
            final Path worldFolder = legacyWorldFolder(worldName);
            writeRegionFileWithChunk(inWorld(worldFolder), 0, 0, new int[][] { { 1, 64, 2 } });
            writeRegionFileWithChunk(inWorld(worldFolder), 32, 0, new int[][] { { 3, 65, 4 } });
            final Clock clock = fixedUtc("2026-05-31T14:23:05Z");

            // When backup runs
            McMMORegionBackupStore.backup(containerRoot, pluginDataRoot, worldName, worldFolder,
                    silentLogger, clock);

            // Then a snapshot directory exists with both .mcm files and a BACKUP_COMPLETE stamp
            final Path snapshot = worldBackupRoot(worldName)
                    .resolve(snapshotName("2026-05-31T14:23:05Z"));
            assertThat(Files.isDirectory(snapshot)).isTrue();
            assertThat(Files.isRegularFile(snapshot.resolve("mcmmo_0_0_.mcm"))).isTrue();
            assertThat(Files.isRegularFile(snapshot.resolve("mcmmo_1_0_.mcm"))).isTrue();
            assertThat(Files.isRegularFile(
                    snapshot.resolve(McMMORegionBackupStore.BACKUP_COMPLETE_SENTINEL))).isTrue();
        }

        @Test
        void sentinelContentIdentifiesWorldAndFileCount() throws IOException {
            // Given a legacy-shape world with one region file
            final String worldName = "world";
            final Path worldFolder = legacyWorldFolder(worldName);
            writeRegionFileWithChunk(inWorld(worldFolder), 0, 0, new int[][] { { 0, 0, 0 } });
            final Clock clock = fixedUtc("2026-05-31T14:23:05Z");

            // When backup runs
            McMMORegionBackupStore.backup(containerRoot, pluginDataRoot, worldName, worldFolder,
                    silentLogger, clock);

            // Then the completion stamp records the world name, file count, and timestamp
            final Path sentinel = worldBackupRoot(worldName)
                    .resolve(snapshotName("2026-05-31T14:23:05Z"))
                    .resolve(McMMORegionBackupStore.BACKUP_COMPLETE_SENTINEL);
            final String body = Files.readString(sentinel, StandardCharsets.UTF_8);
            assertThat(body).contains("world_name=" + worldName);
            assertThat(body).contains("file_count=1");
            assertThat(body).contains("timestamp=2026-05-31T14:23:05Z");
        }

        @Test
        void retainsOnlyTheNewestThreeCompleteSnapshots() throws IOException {
            // Given a legacy-shape world with one region file and four backups taken in order
            final String worldName = "world";
            final Path worldFolder = legacyWorldFolder(worldName);
            writeRegionFileWithChunk(inWorld(worldFolder), 0, 0, new int[][] { { 0, 0, 0 } });
            final String[] timestamps = {
                    "2026-05-28T10:00:00Z",
                    "2026-05-29T10:00:00Z",
                    "2026-05-30T10:00:00Z",
                    "2026-05-31T10:00:00Z"
            };

            // When four backups are written
            for (String iso : timestamps) {
                McMMORegionBackupStore.backup(containerRoot, pluginDataRoot, worldName, worldFolder,
                        silentLogger, fixedUtc(iso));
            }

            // Then only the three most recent snapshots are retained
            final List<String> remaining;
            try (Stream<Path> stream = Files.list(worldBackupRoot(worldName))) {
                remaining = stream.filter(Files::isDirectory)
                        .map(p -> p.getFileName().toString())
                        .sorted()
                        .toList();
            }
            assertThat(remaining).isEqualTo(
                    List.of(snapshotName(timestamps[1]), snapshotName(timestamps[2]),
                            snapshotName(timestamps[3])));
        }

        @Test
        void doesNothingWhenInWorldHasNoRegionFiles() throws IOException {
            // Given a legacy-shape world with an empty (or missing) in-world folder
            final String worldName = "world";
            final Path worldFolder = legacyWorldFolder(worldName);
            Files.createDirectories(inWorld(worldFolder));

            // When backup runs
            McMMORegionBackupStore.backup(containerRoot, pluginDataRoot, worldName, worldFolder,
                    silentLogger, fixedUtc("2026-05-31T14:23:05Z"));

            // Then no backup root is created inside the plugin data directory
            assertThat(Files.exists(worldBackupRoot(worldName))).isFalse();
        }

        @Test
        void prunesIncompleteSnapshotsFromExistingBackupStoreWhenWorldHasNoData()
                throws IOException {
            // Given a legacy-shape world with NO in-world .mcm files (e.g. brand-new world or
            // world was just deleted/reset) but an existing backup store with a crash-interrupted
            // incomplete snapshot. Backup is skipped (nothing to back up) but the crash artifact
            // in the backup store should still be cleaned up on this shutdown.
            final String worldName = "world";
            final Path worldFolder = legacyWorldFolder(worldName);
            Files.createDirectories(inWorld(worldFolder)); // folder exists, but no .mcm files
            final Path existingBackupRoot = worldBackupRoot(worldName);
            final Path incomplete = existingBackupRoot.resolve(
                    snapshotName("2026-05-30T10:00:00Z"));
            Files.createDirectories(incomplete);
            Files.writeString(incomplete.resolve("mcmmo_0_0_.mcm"), "partial"); // no sentinel

            // When backup runs
            McMMORegionBackupStore.backup(containerRoot, pluginDataRoot, worldName, worldFolder,
                    silentLogger, fixedUtc("2026-05-31T14:23:05Z"));

            // Then the crash-interrupted snapshot is pruned
            assertThat(Files.exists(incomplete)).isFalse();
            // And no new snapshot is created (nothing to back up)
            assertThat(Files.exists(existingBackupRoot.resolve(
                    snapshotName("2026-05-31T14:23:05Z")))).isFalse();
        }

        @Test
        void doesNothingWhenWorldIsOnTheNewPaperShape() throws IOException {
            // Given a new-shape (Paper 26.1+) world with in-world data
            final String worldName = "world";
            final Path worldFolder = newPaperWorldFolder(worldName, "overworld");
            writeRegionFileWithChunk(inWorld(worldFolder), 0, 0, new int[][] { { 0, 0, 0 } });

            // When backup runs
            McMMORegionBackupStore.backup(containerRoot, pluginDataRoot, worldName, worldFolder,
                    silentLogger, fixedUtc("2026-05-31T14:23:05Z"));

            // Then no backup root is created — backup snapshots are only needed on legacy shape
            assertThat(Files.exists(worldBackupRoot(worldName))).isFalse();
        }

        @Test
        void logsStartMessageToWarnAgainstForceShutdownDuringLegacyBackup() throws IOException {
            // Given a legacy-shape world with tracked block data and a logger that captures INFO
            final String worldName = "world";
            final Path worldFolder = legacyWorldFolder(worldName);
            writeRegionFileWithChunk(inWorld(worldFolder), 0, 0, new int[][] { { 0, 0, 0 } });
            final Clock clock = fixedUtc("2026-05-31T14:23:05Z");
            final Path expectedSnapshotPath = worldBackupRoot(worldName)
                    .resolve(snapshotName("2026-05-31T14:23:05Z"));

            final Logger captureLogger = Logger.getLogger("McMMORegionBackupStoreTest.capture");
            captureLogger.setUseParentHandlers(false);
            captureLogger.setLevel(Level.ALL);
            final List<String> loggedMessages = new ArrayList<>();
            final Handler handler = new Handler() {
                @Override
                public void publish(LogRecord record) {
                    loggedMessages.add(record.getMessage());
                }

                @Override
                public void flush() {
                }

                @Override
                public void close() {
                }
            };
            captureLogger.addHandler(handler);

            try {
                // When backup runs
                McMMORegionBackupStore.backup(containerRoot, pluginDataRoot, worldName,
                        worldFolder, captureLogger, clock);
            } finally {
                captureLogger.removeHandler(handler);
            }

            // Then an explicit start warning is logged before completion
            assertThat(loggedMessages.stream().anyMatch(message -> message.contains(
                    "Backing up region data for world named '" + worldName + "'"))).isTrue();
            // And the backup destination path is included in progress and completion logs
            assertThat(loggedMessages.stream().anyMatch(message -> message.contains(
                    "to " + expectedSnapshotPath))).isTrue();
            assertThat(loggedMessages.stream().anyMatch(message -> message.contains(
                    "Backup complete for world '" + worldName + "'"))).isTrue();
        }
    }

    @Nested
    class Pruning {

        @Test
        void deletesSnapshotDirectoryWithoutSentinel() throws IOException {
            // Given an orphaned snapshot directory missing the BACKUP_COMPLETE stamp
            final Path worldBackupRoot = worldBackupRoot("world");
            final Path orphan = worldBackupRoot.resolve(snapshotName("2026-05-31T14:23:05Z"));
            Files.createDirectories(orphan);
            Files.writeString(orphan.resolve("mcmmo_0_0_.mcm"), "stale");

            // When the incomplete-snapshot janitor runs
            McMMORegionBackupStore.pruneIncompleteSnapshots(worldBackupRoot, silentLogger);

            // Then the orphan is gone
            assertThat(Files.exists(orphan)).isFalse();
        }

        @Test
        void deletesInProgressTempDirectoryLeftByCrash() throws IOException {
            // Given a *.tmp staging folder left by a crashed prior backup
            final Path worldBackupRoot = worldBackupRoot("world");
            final Path tempLeftover = worldBackupRoot.resolve(
                    snapshotName("2026-05-31T14:23:05Z")
                            + McMMORegionBackupStore.IN_PROGRESS_SUFFIX);
            Files.createDirectories(tempLeftover);
            Files.writeString(tempLeftover.resolve("partial.txt"), "in-progress");

            // When the janitor runs
            McMMORegionBackupStore.pruneIncompleteSnapshots(worldBackupRoot, silentLogger);

            // Then the temp leftover is gone
            assertThat(Files.exists(tempLeftover)).isFalse();
        }

        @Test
        void keepsCompleteSnapshotsAndIgnoresUnknownNamedFolders() throws IOException {
            // Given one complete snapshot and a non-snapshot folder (e.g., operator notes)
            final Path worldBackupRoot = worldBackupRoot("world");
            final Path completeSnapshot = worldBackupRoot.resolve(
                    snapshotName("2026-05-31T14:23:05Z"));
            Files.createDirectories(completeSnapshot);
            Files.writeString(completeSnapshot.resolve(
                    McMMORegionBackupStore.BACKUP_COMPLETE_SENTINEL), "ok");
            final Path operatorNotes = worldBackupRoot.resolve("operator_notes");
            Files.createDirectories(operatorNotes);
            Files.writeString(operatorNotes.resolve("readme.txt"), "do not touch");

            // When the janitor runs
            McMMORegionBackupStore.pruneIncompleteSnapshots(worldBackupRoot, silentLogger);

            // Then the complete snapshot is preserved and the unrelated folder is untouched
            assertThat(Files.exists(completeSnapshot)).isTrue();
            assertThat(Files.exists(operatorNotes.resolve("readme.txt"))).isTrue();
        }
    }

    @Nested
    class RestoreFromBackup {

        @Test
        void logsOneTimeMigrationRestoreStartAndCompletion() throws IOException {
            // Given a new-shape world with one complete migration backup snapshot
            final String worldName = "world";
            final Path worldFolder = newPaperWorldFolder(worldName, "overworld");
            createCompleteSnapshot(worldName, "2026-05-31T10:00:00Z",
                    "mcmmo_0_0_.mcm", "restore-payload");

            final Logger captureLogger = Logger.getLogger("McMMORegionBackupStoreTest.restore");
            captureLogger.setUseParentHandlers(false);
            captureLogger.setLevel(Level.ALL);
            final List<String> loggedMessages = new ArrayList<>();
            final Handler handler = new Handler() {
                @Override
                public void publish(LogRecord record) {
                    loggedMessages.add(record.getMessage());
                }

                @Override
                public void flush() {
                }

                @Override
                public void close() {
                }
            };
            captureLogger.addHandler(handler);

            try {
                // When restore runs
                McMMORegionBackupStore.restore(containerRoot, pluginDataRoot, worldName,
                        worldFolder, captureLogger);
            } finally {
                captureLogger.removeHandler(handler);
            }

            // Then restore progress is clearly logged in concise operator-friendly wording
            assertThat(loggedMessages.stream().anyMatch(message -> message.contains(
                    "Restoring region data for world named '" + worldName + "'"))).isTrue();
            assertThat(loggedMessages.stream().anyMatch(message -> message.contains(
                    "Restore complete for world '" + worldName + "'"))).isTrue();
            assertThat(loggedMessages.stream().anyMatch(message -> message.contains(
                    "were successfully restored in "))).isTrue();
            assertThat(loggedMessages.stream().anyMatch(message -> message.contains(
                    "migration backup archive COMPLETE - saved previous migration backup data to"))).isTrue();
        }

        @Test
        void restoresIntoNewShapeInWorldFolderWhenEmpty() throws IOException {
            // Given a new-shape world with no in-world data
            final String worldName = "world_nether";
            final Path worldFolder = newPaperWorldFolder(worldName, "the_nether");
            // And two complete backup snapshots (the newer one should be restored)
            createCompleteSnapshot(worldName, "2026-05-30T10:00:00Z",
                    "mcmmo_0_0_.mcm", "older-data");
            createCompleteSnapshot(worldName, "2026-05-31T10:00:00Z",
                    "mcmmo_0_0_.mcm", "newest-data");

            // When restore runs
            McMMORegionBackupStore.restore(containerRoot, pluginDataRoot, worldName, worldFolder,
                    silentLogger);

            // Then the in-world folder receives the newest snapshot's file
            final Path restored = inWorld(worldFolder).resolve("mcmmo_0_0_.mcm");
            assertThat(Files.isRegularFile(restored)).isTrue();
            assertThat(Files.readString(restored)).isEqualTo("newest-data");
            // And the restored backup is archived for possible re-use
            assertThat(Files.exists(worldBackupRoot(worldName))).isFalse();
            assertThat(Files.isDirectory(archivedWorldBackupRoot(worldName))).isTrue();
        }

        @Test
        void ignoresSnapshotsMissingTheBackupCompleteSentinel() throws IOException {
            // Given a newer incomplete snapshot and an older complete snapshot
            final String worldName = "world";
            final Path worldFolder = newPaperWorldFolder(worldName, "overworld");
            createCompleteSnapshot(worldName, "2026-05-29T10:00:00Z",
                    "mcmmo_0_0_.mcm", "complete-payload");
            // Newer but incomplete (no completion stamp)
            final Path incomplete = worldBackupRoot(worldName)
                    .resolve(snapshotName("2026-05-31T10:00:00Z"));
            Files.createDirectories(incomplete);
            Files.writeString(incomplete.resolve("mcmmo_0_0_.mcm"), "torn-payload");

            // When restore runs
            McMMORegionBackupStore.restore(containerRoot, pluginDataRoot, worldName, worldFolder,
                    silentLogger);

            // Then the older complete snapshot wins
            assertThat(Files.readString(inWorld(worldFolder).resolve("mcmmo_0_0_.mcm")))
                    .isEqualTo("complete-payload");
        }

                @Test
                void archivesBackupStoreWhenNewShapeInWorldAlreadyHasData() throws IOException {
            // Given a new-shape world with existing in-world data AND a backup-store snapshot.
            // This happens when Paper's migration already moved the data (or mcMMO already
                        // restored it on a prior startup). The backup store should be archived so an admin
                        // can re-use those snapshots for another merge pass later.
            final String worldName = "world";
            final Path worldFolder = newPaperWorldFolder(worldName, "overworld");
            writeRegionFileWithChunk(inWorld(worldFolder), 0, 0, new int[][] { { 1, 1, 1 } });
            createCompleteSnapshot(worldName, "2026-05-31T10:00:00Z",
                    "mcmmo_0_0_.mcm", "restore-payload");

            // When restore runs
            McMMORegionBackupStore.restore(containerRoot, pluginDataRoot, worldName, worldFolder,
                    silentLogger);

            // Then the in-world data is left untouched
            final ChunkStore preserved = readChunkFromRegionFile(
                    inWorld(worldFolder).resolve("mcmmo_0_0_.mcm"), 0, 0);
                        assertThat(preserved).isNotNull();
                        assertThat(preserved.isTrue(1, 1, 1)).isTrue();
            // And the backup store is archived instead of deleted
                        assertThat(Files.exists(worldBackupRoot(worldName))).isFalse();
                        assertThat(Files.isDirectory(archivedWorldBackupRoot(worldName))).isTrue();
            try (Stream<Path> archiveEntries = Files.list(archivedWorldBackupRoot(worldName))) {
                                assertThat(archiveEntries.anyMatch(Files::isDirectory)).isTrue();
            }
        }

        @Test
        void mergesLegacyRootDataAndArchivesSnapshotsWhenInWorldAlreadyHasData()
                throws IOException {
            // Given a new-shape world with existing in-world data
            final String worldName = "world";
            final Path worldFolder = newPaperWorldFolder(worldName, "overworld");
            writeRegionFileWithChunk(inWorld(worldFolder), 0, 0, new int[][] { { 1, 1, 1 } });

            // And backup-store snapshot data that should NOT be applied in this code path
            writeRegionFileWithChunk(
                    worldBackupRoot(worldName).resolve(snapshotName("2026-05-31T10:00:00Z")),
                    0,
                    0,
                    new int[][] { { 7, 7, 7 } });
            Files.writeString(
                    worldBackupRoot(worldName)
                            .resolve(snapshotName("2026-05-31T10:00:00Z"))
                            .resolve(McMMORegionBackupStore.BACKUP_COMPLETE_SENTINEL),
                    "timestamp=2026-05-31T10:00:00Z\nworld_name=" + worldName + "\n",
                    StandardCharsets.UTF_8);

            // And surviving legacy-root data that SHOULD be merged into the new in-world folder
            writeRegionFileWithChunk(inWorld(legacyWorldFolder(worldName)), 0, 0,
                    new int[][] { { 2, 2, 2 } });

            // When restore runs on the new layout with in-world data already present
            McMMORegionBackupStore.restore(containerRoot, pluginDataRoot, worldName, worldFolder,
                    silentLogger);

            // Then in-world data keeps its existing entries and merges legacy-root entries
            final ChunkStore merged = readChunkFromRegionFile(
                    inWorld(worldFolder).resolve("mcmmo_0_0_.mcm"), 0, 0);
            assertThat(merged).isNotNull();
            assertThat(merged.isTrue(1, 1, 1)).isTrue();
            assertThat(merged.isTrue(2, 2, 2)).isTrue();

            // And snapshot data is NOT applied in this path (in-world was already authoritative)
            assertThat(merged.isTrue(7, 7, 7)).isFalse();

            // And backup snapshots are archived, while legacy-root source files are removed
            assertThat(Files.exists(worldBackupRoot(worldName))).isFalse();
            assertThat(Files.isDirectory(archivedWorldBackupRoot(worldName))).isTrue();
            assertThat(Files.exists(
                    inWorld(legacyWorldFolder(worldName)).resolve("mcmmo_0_0_.mcm"))).isFalse();
        }

        @Test
        void doesNotRestoreOnLegacyShapeEvenWhenBackupExists() throws IOException {
            // Given a legacy-shape world with empty in-world AND a backup present in the restore
            // store — restore must not auto-restore on the legacy shape because the in-world
            // location is authoritative there and may be intentionally empty
            final String worldName = "world";
            final Path worldFolder = legacyWorldFolder(worldName);
            createCompleteSnapshot(worldName, "2026-05-31T10:00:00Z",
                    "mcmmo_0_0_.mcm", "restore-payload");

            // When restore runs
            McMMORegionBackupStore.restore(containerRoot, pluginDataRoot, worldName, worldFolder,
                    silentLogger);

            // Then the in-world folder stays empty and the backup store is preserved
            assertThat(Files.exists(inWorld(worldFolder).resolve("mcmmo_0_0_.mcm"))).isFalse();
            assertThat(Files.exists(worldBackupRoot(worldName))).isTrue();
        }

        @Test
        void doesNothingWhenNoBackupStoreEntryExists() {
            // Given no backup store at all for this world
            final String worldName = "world";
            final Path worldFolder = newPaperWorldFolder(worldName, "overworld");

            // When restore runs, it should be a no-op and never throw
            McMMORegionBackupStore.restore(containerRoot, pluginDataRoot, worldName, worldFolder,
                    silentLogger);

            // Then no in-world folder is created
            assertThat(Files.exists(inWorld(worldFolder))).isFalse();
        }

                @Test
                void prunesIncompleteSnapshotsAndDeletesEmptyBackupStoreWhenNewShapeInWorldHasData()
                throws IOException {
            // Given a new-shape world with in-world data (no restore needed) and an incomplete
            // snapshot left from a crashed previous backup. restore() should first prune the
                        // incomplete snapshot, then remove the empty per-world folder because there is
                        // nothing worth archiving.
            final String worldName = "world";
            final Path worldFolder = newPaperWorldFolder(worldName, "overworld");
            writeRegionFileWithChunk(inWorld(worldFolder), 0, 0, new int[][] { { 0, 0, 0 } });
            final Path incomplete = worldBackupRoot(worldName).resolve(
                    snapshotName("2026-05-30T10:00:00Z"));
            Files.createDirectories(incomplete);

            // When restore runs
            McMMORegionBackupStore.restore(containerRoot, pluginDataRoot, worldName, worldFolder,
                    silentLogger);

            // Then the incomplete snapshot is gone
            assertThat(Files.exists(incomplete)).isFalse();
            // And the backup store itself is deleted because there is no snapshot worth keeping
            assertThat(Files.exists(worldBackupRoot(worldName))).isFalse();
        }

        @Test
        void deletesLegacyRootRegionFilesAfterSnapshotRestore() throws IOException {
            // Given a new-shape world with a restorable snapshot
            final String worldName = "world";
            final Path worldFolder = newPaperWorldFolder(worldName, "overworld");
            createCompleteSnapshot(worldName, "2026-05-31T10:00:00Z",
                    "mcmmo_0_0_.mcm", "snapshot-data");
            // And leftover legacy-root region data that Paper migration did not remove
            final Path legacyRootRegionFolder = inWorld(legacyWorldFolder(worldName));
            Files.createDirectories(legacyRootRegionFolder);
            Files.writeString(legacyRootRegionFolder.resolve("mcmmo_1_0_.mcm"), "legacy-data");

            // When restore runs and uses the snapshot
            McMMORegionBackupStore.restore(containerRoot, pluginDataRoot, worldName, worldFolder,
                    silentLogger);

            // Then snapshot data is restored
            assertThat(Files.readString(inWorld(worldFolder).resolve("mcmmo_0_0_.mcm")))
                    .isEqualTo("snapshot-data");
            // And leftover legacy-root data is deleted (not merged, not archived)
            assertThat(Files.exists(legacyRootRegionFolder.resolve("mcmmo_1_0_.mcm"))).isFalse();
            assertThat(Files.exists(inWorld(worldFolder).resolve("mcmmo_1_0_.mcm"))).isFalse();
        }

        @Test
        void mergesLegacyRootRegionFilesAndDeletesSourceWhenNoSnapshotExists()
                throws IOException {
            // Given a new-shape world with existing in-world data and no migration snapshot
            final String worldName = "world";
            final Path worldFolder = newPaperWorldFolder(worldName, "overworld");
            writeRegionFileWithChunk(inWorld(worldFolder), 0, 0, new int[][] { { 1, 1, 1 } });
            // And leftover legacy-root region data
            final Path legacyRootRegionFolder = inWorld(legacyWorldFolder(worldName));
            writeRegionFileWithChunk(legacyRootRegionFolder, 0, 0, new int[][] { { 2, 2, 2 } });

            // When restore runs
            McMMORegionBackupStore.restore(containerRoot, pluginDataRoot, worldName, worldFolder,
                    silentLogger);

            // Then legacy-root data is merged into in-world data
            final ChunkStore merged = readChunkFromRegionFile(
                    inWorld(worldFolder).resolve("mcmmo_0_0_.mcm"), 0, 0);
            assertThat(merged).isNotNull();
            assertThat(merged.isTrue(1, 1, 1)).isTrue();
            assertThat(merged.isTrue(2, 2, 2)).isTrue();
            // And source files are deleted afterwards
            assertThat(Files.exists(legacyRootRegionFolder.resolve("mcmmo_0_0_.mcm"))).isFalse();
        }

        private void createCompleteSnapshot(String worldName, String isoTimestamp,
                String regionFileName, String content) throws IOException {
            final Path snapshot = worldBackupRoot(worldName).resolve(snapshotName(isoTimestamp));
            Files.createDirectories(snapshot);
            Files.writeString(snapshot.resolve(regionFileName), content);
            Files.writeString(
                    snapshot.resolve(McMMORegionBackupStore.BACKUP_COMPLETE_SENTINEL),
                    "timestamp=" + isoTimestamp + "\nworld_name=" + worldName + "\n");
        }
    }

    @Nested
    class IdempotencyAndCrashRecovery {

        @Test
        void backupIsIdempotentWhenSameTimestampIsReplayedAfterSuccess() throws IOException {
            // Given a successful backup at a fixed clock
            final String worldName = "world";
            final Path worldFolder = legacyWorldFolder(worldName);
            writeRegionFileWithChunk(inWorld(worldFolder), 0, 0, new int[][] { { 0, 0, 0 } });
            final Clock clock = fixedUtc("2026-05-31T14:23:05Z");
            McMMORegionBackupStore.backup(containerRoot, pluginDataRoot, worldName, worldFolder,
                    silentLogger, clock);
            final Path snapshot = worldBackupRoot(worldName)
                    .resolve(snapshotName("2026-05-31T14:23:05Z"));
            final long sentinelMtimeBefore = Files.getLastModifiedTime(
                    snapshot.resolve(McMMORegionBackupStore.BACKUP_COMPLETE_SENTINEL))
                    .toMillis();

            // When backup is invoked again with the same clock
            McMMORegionBackupStore.backup(containerRoot, pluginDataRoot, worldName, worldFolder,
                    silentLogger, clock);

            // Then the existing snapshot is not rewritten (sentinel mtime unchanged)
            final long sentinelMtimeAfter = Files.getLastModifiedTime(
                    snapshot.resolve(McMMORegionBackupStore.BACKUP_COMPLETE_SENTINEL))
                    .toMillis();
            assertThat(sentinelMtimeAfter).isEqualTo(sentinelMtimeBefore);
        }

        @Test
        void backupCleansUpAnyPriorTempFolderAtSameTimestampBeforeRewriting() throws IOException {
            // Given a *.tmp staging folder left from a crash at the same timestamp the new
            // backup will use — the stale temp must be removed before the fresh copy starts
            final String worldName = "world";
            final Path worldFolder = legacyWorldFolder(worldName);
            writeRegionFileWithChunk(inWorld(worldFolder), 0, 0, new int[][] { { 0, 0, 0 } });
            final Clock clock = fixedUtc("2026-05-31T14:23:05Z");
            final Path stale = worldBackupRoot(worldName)
                    .resolve(snapshotName("2026-05-31T14:23:05Z")
                            + McMMORegionBackupStore.IN_PROGRESS_SUFFIX);
            Files.createDirectories(stale);
            Files.writeString(stale.resolve("garbage.txt"), "stale");

            // When backup runs
            McMMORegionBackupStore.backup(containerRoot, pluginDataRoot, worldName, worldFolder,
                    silentLogger, clock);

            // Then the stale *.tmp is gone and a clean complete snapshot is in its place
            assertThat(Files.exists(stale)).isFalse();
            final Path finalSnapshot = worldBackupRoot(worldName)
                    .resolve(snapshotName("2026-05-31T14:23:05Z"));
            assertThat(Files.isRegularFile(finalSnapshot.resolve("mcmmo_0_0_.mcm"))).isTrue();
            assertThat(Files.isRegularFile(
                    finalSnapshot.resolve(McMMORegionBackupStore.BACKUP_COMPLETE_SENTINEL))).isTrue();
            assertThat(Files.exists(finalSnapshot.resolve("garbage.txt"))).isFalse();
        }

        @Test
        void archivesBackupStoreAfterSuccessfulRestoreAndLeavesInWorldDataUntouched()
                throws IOException {
            // Given a complete snapshot AND in-world data that already contains the chunks we
            // care about. Re-running restore should keep the in-world data as-is and archive the
            // old backup so an admin can use it again later if needed.
            final String worldName = "world";
            final Path worldFolder = newPaperWorldFolder(worldName, "overworld");
            final Path snapshot = worldBackupRoot(worldName).resolve(
                    snapshotName("2026-05-31T10:00:00Z"));
            Files.createDirectories(snapshot);
            writeRegionFileWithChunk(snapshot, 0, 0, new int[][] { { 1, 1, 1 } });
            Files.writeString(snapshot.resolve(
                    McMMORegionBackupStore.BACKUP_COMPLETE_SENTINEL), "ok");
            // Simulate prior partial: in-world already has the same chunk with a different bit
            writeRegionFileWithChunk(inWorld(worldFolder), 0, 0, new int[][] { { 2, 2, 2 } });

            // When restore runs again
            McMMORegionBackupStore.restore(containerRoot, pluginDataRoot, worldName, worldFolder,
                    silentLogger);

            // Then the in-world chunk is preserved unmodified (no overwrite or merge)
            final ChunkStore inWorldChunk = readChunkFromRegionFile(
                    inWorld(worldFolder).resolve("mcmmo_0_0_.mcm"), 0, 0);
                        assertThat(inWorldChunk).isNotNull();
                        assertThat(inWorldChunk.isTrue(2, 2, 2)).isTrue();
                        assertThat(inWorldChunk.isTrue(1, 1, 1)).isFalse();
            // And the backup store is archived instead of removed
                        assertThat(Files.exists(worldBackupRoot(worldName))).isFalse();
                        assertThat(Files.isDirectory(archivedWorldBackupRoot(worldName))).isTrue();
        }
    }

    @Nested
        class NewestCompleteSnapshotPicker {

        @Test
        void returnsLexicographicallyNewestCompleteSnapshot() throws IOException {
            // Given several snapshots, only some complete
            final Path worldBackupRoot = worldBackupRoot("world");
            createCompleteSnapshot(worldBackupRoot, "2026-05-29T10:00:00Z");
            createIncompleteSnapshot(worldBackupRoot, "2026-05-31T10:00:00Z");
            createCompleteSnapshot(worldBackupRoot, "2026-05-30T10:00:00Z");

            // When asking for the newest complete snapshot
            final Path newest = McMMORegionBackupStore.newestCompleteSnapshot(worldBackupRoot);

            // Then the 2026-05-30 snapshot wins (the 31st is incomplete and has no stamp)
            assertThat(newest).isNotNull();
            assertThat(newest.getFileName().toString())
                    .isEqualTo(snapshotName("2026-05-30T10:00:00Z"));
        }

        @Test
        void returnsNullWhenNoCompleteSnapshotExists() throws IOException {
            // Given a backup root with only incomplete snapshots
            final Path worldBackupRoot = worldBackupRoot("world");
            createIncompleteSnapshot(worldBackupRoot, "2026-05-31T10:00:00Z");

            // When asking for the newest complete snapshot
            // Then null is returned
            assertThat(McMMORegionBackupStore.newestCompleteSnapshot(worldBackupRoot)).isNull();
        }

        @Test
        void returnsNullWhenWorldBackupRootDoesNotExist() {
            // Given a non-existent root
            // When asking for the newest complete snapshot
            // Then null is returned without throwing
            assertThat(McMMORegionBackupStore.newestCompleteSnapshot(
                    worldBackupRoot("nonexistent"))).isNull();
        }

        private void createCompleteSnapshot(Path root, String iso) throws IOException {
            final Path snapshot = root.resolve(snapshotName(iso));
            Files.createDirectories(snapshot);
            Files.writeString(snapshot.resolve(
                    McMMORegionBackupStore.BACKUP_COMPLETE_SENTINEL), "ok");
        }

        private void createIncompleteSnapshot(Path root, String iso) throws IOException {
            Files.createDirectories(root.resolve(snapshotName(iso)));
        }
    }

    @Nested
    class Readme {

        @Test
        void writesReadmeWhenAbsent() throws IOException {
            // Given a fresh backup-store root with no README
            final Path backupStoreRoot = backupStoreRoot();
            Files.createDirectories(backupStoreRoot);

            // When ensureReadme runs
            McMMORegionBackupStore.writeReadme(backupStoreRoot, silentLogger);

            // Then a README.txt with operator documentation is written
            final Path readme = backupStoreRoot.resolve(McMMORegionBackupStore.README_FILE_NAME);
            assertThat(Files.isRegularFile(readme)).isTrue();
            assertThat(Files.readString(readme)).contains("mcMMO region backup store");
        }

        @Test
        void doesNotOverwriteAnExistingReadme() throws IOException {
            // Given an operator-edited README already in place
            final Path backupStoreRoot = backupStoreRoot();
            Files.createDirectories(backupStoreRoot);
            final Path readme = backupStoreRoot.resolve(McMMORegionBackupStore.README_FILE_NAME);
            Files.writeString(readme, "OPERATOR NOTES — DO NOT TOUCH");

            // When ensureReadme runs
            McMMORegionBackupStore.writeReadme(backupStoreRoot, silentLogger);

            // Then the operator content is preserved unchanged
            assertThat(Files.readString(readme)).isEqualTo("OPERATOR NOTES — DO NOT TOUCH");
        }

        @Test
        void writtenByBackupOnFirstRun() throws IOException {
            // Given a legacy-shape world with one in-world region file and no existing README
            final String worldName = "world";
            final Path worldFolder = legacyWorldFolder(worldName);
            writeRegionFileWithChunk(inWorld(worldFolder), 0, 0, new int[][] { { 0, 0, 0 } });

            // When backup runs for the first time
            McMMORegionBackupStore.backup(containerRoot, pluginDataRoot, worldName, worldFolder,
                    silentLogger, fixedUtc("2026-05-31T14:23:05Z"));

            // Then the README is written into the backup-store root
            assertThat(Files.isRegularFile(
                    backupStoreRoot().resolve(McMMORegionBackupStore.README_FILE_NAME))).isTrue();
            // And no per-world README is created
            assertThat(Files.exists(
                    worldBackupRoot(worldName).resolve(McMMORegionBackupStore.README_FILE_NAME))).isFalse();
        }
    }

    @Nested
    class CopyOrMergeRegionFile {

        @Test
        void copiesIntactWhenDestinationMissing(@TempDir Path scratch) throws IOException {
            // Given a region file at source and no destination file yet
            final Path source = writeRegionFileWithChunk(scratch.resolve("src"), 0, 0,
                    new int[][] { { 1, 1, 1 } });
            final Path destination = scratch.resolve("dst").resolve("mcmmo_0_0_.mcm");
            Files.createDirectories(destination.getParent());

            // When copy-or-merge runs
            McMMORegionBackupStore.copyOrMergeRegionFile(source, destination);

            // Then the destination is byte-equal to the source (straight copy)
            assertThat(Files.size(destination)).isEqualTo(Files.size(source));
        }

        @Test
        void unionMergesWhenDestinationExists(@TempDir Path scratch) throws IOException {
            // Given source with bit A at (1,64,2) and destination with bit B at (5,32,6) in the
            // same chunk — both bits must survive the merge
            final Path source = writeRegionFileWithChunk(scratch.resolve("src"), 0, 0,
                    new int[][] { { 1, 64, 2 } });
            final Path destination = writeRegionFileWithChunk(scratch.resolve("dst"), 0, 0,
                    new int[][] { { 5, 32, 6 } });

            // When copy-or-merge runs
            McMMORegionBackupStore.copyOrMergeRegionFile(source, destination);

            // Then both bits survive in the destination
            final ChunkStore merged = readChunkFromRegionFile(destination, 0, 0);
                        assertThat(merged).isNotNull();
                        assertThat(merged.isTrue(1, 64, 2)).isTrue();
                        assertThat(merged.isTrue(5, 32, 6)).isTrue();
        }
    }

}
