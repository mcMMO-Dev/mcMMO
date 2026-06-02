package com.gmail.nossr50.util.blockmeta;

import com.gmail.nossr50.util.LogUtils;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Protects mcMMO block-tracker data from being lost when a Minecraft server upgrades from
 * Spigot or pre-26.1 Paper to Paper 26.1 or later.
 *
 * <h2>What is block-tracker data?</h2>
 * <p>mcMMO tracks which blocks in each world were placed by players (as opposed to naturally
 * generated). This prevents players from gaining XP by placing and mining the same block
 * repeatedly. The tracking data is stored as {@code .mcm} binary files inside each world
 * folder at {@code [worldFolder]/mcmmo_regions/}.
 *
 * <h2>What are "legacy shape" and "new shape" worlds?</h2>
 * <p>On <b>Spigot</b> and <b>Paper before version 26.1</b> (the "legacy shape"), every world
 * lives directly inside the server container folder:
 * <pre>
 *   [serverFolder]/
 *     world/                  ← overworld
 *     world_nether/           ← nether
 *     world_the_end/          ← end
 * </pre>
 * <p>On <b>Paper 26.1+</b> (the "new shape"), multidimensional worlds are reorganized so
 * that each world's non-overworld dimensions live in a subfolder:
 * <pre>
 *   [serverFolder]/
 *     world/                  ← overworld (unchanged)
 *     world/dimensions/minecraft/the_nether/    ← nether (moved here)
 *     world/dimensions/minecraft/the_end/       ← end (moved here)
 * </pre>
 * <p>mcMMO detects which shape is active at runtime by comparing
 * {@link World#getWorldFolder()} against the expected legacy path. This check happens on
 * every backup and restore call, so it is always accurate even if the server software changes.
 *
 * <h2>The problem this class solves</h2>
 * <p>Paper 26.1+ runs a one-time migration (<i>LegacyCraftBukkitWorldMigration</i>,
 * PaperMC/Paper PR #13736) on startup <b>before any plugins load</b>. This migration deletes
 * the old per-world root directories for non-overworld dimensions. Any
 * {@code mcmmo_regions/} subfolder that lived inside those directories is permanently lost.
 * mcMMO cannot intervene because it has not been loaded yet.
 *
 * <h2>How this class protects your data</h2>
 * <p>mcMMO keeps a backup store inside its own plugin data folder
 * ({@code plugins/mcMMO/region_data_backups_for_migration/}). The store works in two halves:
 *
 * <ul>
 *   <li><b>Backup (on shutdown, legacy-shape worlds only):</b> mcMMO copies every {@code .mcm}
 *       file from {@code [worldFolder]/mcmmo_regions/} into a timestamped subfolder of the
 *       backup store. The copy is staged in a {@code *.tmp} folder first, then atomically
 *       renamed to its final name — this ensures no other process ever sees a half-written
 *       snapshot. Only after every file is copied successfully is a small
 *       {@value #BACKUP_COMPLETE_SENTINEL} text file written into the snapshot folder as a
 *       "completion stamp". Any snapshot that lacks this stamp was interrupted by a crash and
 *       is automatically cleaned up on the next startup. Only the
 *       {@value #MAX_BACKUPS_RETAINED} newest complete snapshots are kept per world.</li>
 *   <li><b>restore (on startup, new-shape worlds only):</b> if the in-world
 *       {@code [worldFolder]/mcmmo_regions/} folder is empty but a complete backup snapshot
 *       exists, mcMMO copies the newest snapshot back into the new in-world location. Once
 *       the restore is complete, mcMMO moves the old backup-store tree into an archive folder
 *       so an administrator can re-use it for another merge pass if needed.</li>
 * </ul>
 *
 * <h2>Why backups only run on the legacy shape</h2>
 * <p>The migration from legacy to new shape is a one-time, one-way event. Once a world is on
 * the new Paper layout, future server updates have no reason to delete
 * {@code mcmmo_regions/} again. Writing backup snapshots for new-shape worlds would just
 * waste disk space. mcMMO detects the shape on every backup call and skips the backup
 * automatically when the world is already on the new shape.
 *
 * <h2>Admin deletes a world to start fresh</h2>
 * <p>If an admin deletes a world and recreates it from scratch, mcMMO will not back up the
 * fresh world until players start placing blocks (no {@code .mcm} files → no backup). Stale
 * snapshots for the deleted world remain in the backup store until three new successful
 * backups for that same world push them out under the
 * {@value #MAX_BACKUPS_RETAINED}-snapshot retention limit. If the world is permanently gone,
 * the snapshots remain until deleted manually.
 *
 * <p>If the server is on the <b>legacy shape</b> when the world is deleted and the admin
 * later upgrades to Paper 26.1+, mcMMO will see an empty in-world folder and existing
 * snapshots and will restore the pre-deletion data into the fresh world. To prevent this,
 * delete {@code plugins/mcMMO/region_data_backups_for_migration/<worldName>/} while the server is stopped before
 * upgrading.
 *
 * <p>If the server is already on the <b>new shape</b> (Paper 26.1+) when the world is
 * deleted, this is not a concern — mcMMO archives migration backup data for each world as
 * soon as it confirms that block-tracker data is present in the new-shape location.
 *
 * <p>See the README written into the backup store folder for full operator guidance.
 *
 * <h2>Merge semantics</h2>
 * <p>When both an incoming backup file and an existing in-world file contain data for the same
 * chunk, mcMMO uses {@link BitSetChunkStore#mergeFrom(BitSetChunkStore)} to OR the two sets of
 * placed-block bits together. No player-placed block record is ever silently discarded. This
 * makes both backup and restore safe to re-run after a partial failure.
 *
 * <h2>Error handling</h2>
 * <p>Neither {@link #backupWorld} nor {@link #restoreWorld} ever throws an exception. Any I/O
 * or runtime failure is logged at WARNING or SEVERE level so the server continues starting
 * and stopping cleanly even when disk errors occur.
 */
public final class McMMORegionBackupStore {

    /**
     * Name of the subfolder inside each world folder that holds canonical mcMMO region files.
     * Resolves to {@code [worldFolder]/mcmmo_regions/}.
     */
    public static final String IN_WORLD_FOLDER_NAME = "mcmmo_regions";

    /**
     * Name of the backup-store subfolder inside mcMMO's plugin data directory.
    * Resolves to {@code plugins/mcMMO/region_data_backups_for_migration/}.
     */
    public static final String BACKUP_ROOT_FOLDER_NAME = "region_data_backups_for_migration";

    /**
     * Subfolder under the backup store root that keeps world backups after a restore has used
     * them once.
     */
    static final String ARCHIVE_ROOT_FOLDER_NAME = "archive";

    /**
     * File written <em>last</em> inside a completed snapshot folder. Its presence is the only
     * thing that distinguishes a complete snapshot from one that was interrupted mid-write.
     */
    public static final String BACKUP_COMPLETE_SENTINEL = "BACKUP_COMPLETE";

    /** README file written into the backup-store root for operator reference. */
    public static final String README_FILE_NAME = "README.txt";

    /**
     * Suffix applied to a snapshot folder while it is still being assembled. The folder is
     * atomically renamed to drop this suffix once every file has been successfully copied.
     */
    static final String IN_PROGRESS_SUFFIX = ".tmp";

    /** Maximum number of complete snapshots to keep per world in the backup store. */
    static final int MAX_BACKUPS_RETAINED = 3;

    /**
     * Timestamp format used to name snapshot folders. Lexically sortable (newest = last when
     * sorted A→Z), uses UTC, and contains no characters that are illegal in folder names on
     * Windows or Linux.
     */
    static final DateTimeFormatter SNAPSHOT_TIMESTAMP_FORMAT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd_HH-mm-ss'Z'")
            .withZone(ZoneOffset.UTC);

    private static final Pattern SNAPSHOT_DIR_PATTERN = Pattern.compile(
            "\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}-\\d{2}Z");
    private static final Pattern REGION_FILE_PATTERN = Pattern.compile(
            "mcmmo_(-?\\d+)_(-?\\d+)_\\.mcm");
        private static final String BACKUP_LOG_TAG = "[RegionDataBackups]";
        private static final String MIGRATION_LOG_TAG = "[RegionDataMigration]";

    private static final String README_BODY = """
            mcMMO region backup store
            =========================
            Location: plugins/mcMMO/region_data_backups_for_migration/

            What is in this folder?
            -----------------------
            mcMMO tracks which blocks in each world were placed by players so that players
            cannot gain XP by repeatedly placing and breaking the same block. This tracking
            data is stored as .mcm binary files inside each world folder:

                [worldFolder]/mcmmo_regions/mcmmo_<regionX>_<regionZ>_.mcm

            This folder (region_data_backups_for_migration/) is NOT where that data normally lives. It is a backup
            store that mcMMO keeps so the tracking data survives if a server
            software upgrade accidentally deletes the world folder layout it was stored in.

            Each world has its own subfolder under this root:

                region_data_backups_for_migration/<worldName>/<timestamp>/

            Why does this backup store exist?
            ---------------------------------
            Paper 26.1+ (PaperMC PR #13736) runs a one-time migration when the server starts
            that reorganises how dimension folders (nether, end, custom dimensions) are stored
            on disk. This migration runs BEFORE any plugins load, so mcMMO cannot intervene.
            It deletes the old per-world root folders for non-overworld dimensions, which
            takes any mcmmo_regions/ subfolder stored there with it.

            To protect against this, mcMMO saves a backup of the region data into this folder
            on every clean shutdown, then restores it on the next startup if it detects that
            Paper has moved the world to the new layout.

            What does "world shape" mean?
            -----------------------------
            "Legacy shape" means the server is running Spigot or Paper before version 26.1.
            In this layout, every world lives directly inside the server folder:

                [serverFolder]/world/               <- overworld
                [serverFolder]/world_nether/        <- nether
                [serverFolder]/world_the_end/       <- end

            "New shape" means Paper 26.1 or later has reorganised the folders:

                [serverFolder]/world/               <- overworld (unchanged)
                [serverFolder]/world/dimensions/minecraft/the_nether/   <- nether (moved)
                [serverFolder]/world/dimensions/minecraft/the_end/      <- end (moved)

            mcMMO checks which shape is active at runtime on every backup and restore, so the
            check is always accurate even after a server software upgrade.

            How backup works
            ----------------
            On every clean shutdown, for each world still on the LEGACY shape, mcMMO:

            1. Copies all .mcm files from [worldFolder]/mcmmo_regions/ into a staging folder
               named after the current UTC date and time + ".tmp", for example:
               region_data_backups_for_migration/<worldName>/2026-05-31_14-23-05Z.tmp/

            2. Once every file is copied successfully, atomically renames the staging folder
               by removing the .tmp suffix:
               region_data_backups_for_migration/<worldName>/2026-05-31_14-23-05Z/

            3. Writes a small BACKUP_COMPLETE file into the snapshot folder as a
               "completion stamp" — proof that the snapshot is whole and can be trusted.

            4. Keeps only the 3 newest complete snapshots and deletes older ones.

            Backups do NOT run for worlds already on the new Paper shape. Once Paper has
            migrated a world, future server-jar updates have no reason to delete
            mcmmo_regions/ again, so a backup snapshot is no longer needed.

            How restore works
            -----------------
            On startup (and whenever a world loads at runtime), mcMMO checks each world that
            is on the new Paper shape (Paper 26.1+). Two things can happen:

            Scenario A — in-world mcmmo_regions/ is empty and a complete snapshot exists:

                mcMMO copies the files from the newest complete snapshot into
                [worldFolder]/mcmmo_regions/, then moves the old backup-store tree into
                region_data_backups_for_migration/archive/<worldName>/<timestamp>/ so an admin can re-use the same
                data for another merge pass if needed.

            Scenario B — in-world mcmmo_regions/ already has .mcm files:

                The Paper migration already completed successfully (either mcMMO already did
                the restore on a previous startup, or Paper preserved the folder). mcMMO
                moves the backup-store tree into the archive folder immediately if it still
                contains real snapshots; otherwise it deletes the empty per-world folder.

            In both scenarios the active per-world backup folder is cleared out. Any restored
            snapshots that are worth keeping are preserved under the archive folder.

            Legacy-root leftover files from Paper migration
            -----------------------------------------------
            Paper migration can leave .mcm files behind at:

                [container]/<worldName>/mcmmo_regions/

            mcMMO reconciles these leftovers on restore. If a snapshot restore was used, mcMMO
            deletes the leftover files. If no snapshot restore was used, mcMMO merges leftover
            files into the active in-world data and then deletes the leftover source files.
            Unlike snapshot data, these leftovers are never archived.

            How to retry a merge
            --------------------
            If mcMMO already archived a backup but you want it to try the same merge again,
            stop the server and move the archived world folder back here:

                region_data_backups_for_migration/<worldName>/<timestamp>/

            Then delete the archive folder for that world so only the active copy is left.
            The archive folder by itself is ignored; mcMMO only acts on the active copy.
            On the next startup, mcMMO will see the backup data again and repeat the merge.

            Merging is safe because mcMMO never guesses about player-placed blocks. If the
            world and the backup both know about the same chunk, mcMMO combines the two so
            any block either copy recorded is kept.

            If an admin deletes a world to start fresh
            ------------------------------------------
            mcMMO will NOT back up an empty world — if the world has no .mcm files, backup
            is skipped for that world. Old snapshots for the deleted world remain here until
            three new SUCCESSFUL backups for that same world push them out under the
            3-snapshot retention limit. If the world is permanently gone, the snapshots stay
            here until you delete them manually.

            DANGER — legacy-shape server + world deletion + Paper 26.1+ upgrade:

                If your server is on the LEGACY SHAPE when you delete a world, and you later
                upgrade to Paper 26.1+, mcMMO will see the empty new-shape in-world folder
                and the old snapshot here, and will RESTORE THE PRE-DELETION DATA into the
                fresh world. To prevent this:

                    1. While the server is stopped, delete the backup-store folder for that
                       world: region_data_backups_for_migration/<worldName>/
                    2. Do this BEFORE starting the server with Paper 26.1+ for the first time.

            If your server is ALREADY on the new Paper shape (Paper 26.1+) when you delete
            a world, this danger does not apply — mcMMO removes the backup store for each
            world as soon as it confirms block-tracker data is present in the new-shape
            location, so there is nothing left here to accidentally restore.

            Understanding snapshot folder names
            ------------------------------------
            Each snapshot is a folder named by the UTC date and time when it was written,
            for example: 2026-05-31_14-23-05Z/

            The Z at the end stands for UTC (Coordinated Universal Time). The format sorts
            lexically (alphabetically) so the newest snapshot is always the last one when
            the folder list is sorted A→Z.

            The BACKUP_COMPLETE file inside a snapshot folder is a plain text file that
            records the world name, file count, and timestamp. mcMMO only reads a snapshot
            if this file is present. A snapshot without this file was interrupted (by a
            crash, power loss, etc.) and is automatically cleaned up on the next startup or
            shutdown.

            Edge cases
            ----------
            1. Brand-new world on Paper 26.1+ with no prior data: no snapshot exists here,
               in-world is empty, nothing to restore. mcMMO just starts writing fresh data
               inside the world folder as players place blocks.

            2. mcMMO 2.2.053-SNAPSHOT layout (flat .mcm files directly under
               region_data_backups_for_migration/<worldName>/ with a migration_complete_marker_file): on first
               startup after upgrading from that snapshot, mcMMO copies those files back into
               [worldFolder]/mcmmo_regions/ (union-merging on any overlap), then deletes the
               marker and those flat files. Any timestamped snapshot subfolders are left alone.

            3. Crash or power loss mid-backup: the *.tmp staging folder or a snapshot folder
               without a BACKUP_COMPLETE file is cleaned up on the next startup. Previously
               completed snapshots are unaffected.

            4. Operator force-deletes in-world mcmmo_regions/ on a legacy-shape server:
               mcMMO will NOT auto-restore from this backup store on the legacy shape —
               restore is gated on the new Paper layout. To force a restore on the legacy
               shape, copy the .mcm files from the newest BACKUP_COMPLETE snapshot folder
               into [container]/[worldName]/mcmmo_regions/ manually while the server is
               stopped.

            5. World blacklisted in World_Blacklist.yml: mcMMO does not back up or restore
               blacklisted worlds.

            What is safe to delete?
            -----------------------
            - Deleting a whole <timestamp>Z/ snapshot folder just reduces how far back mcMMO
              can restore from. The oldest snapshots are deleted automatically anyway.
            - Deleting individual .mcm files from a snapshot will cause data loss for that
              512×512-block region when the snapshot is restored.
            - The BACKUP_COMPLETE file content does not matter; mcMMO only checks that the
              file exists.
            - You can freely add operator notes, README files, or any other files to this
              folder — mcMMO ignores files it does not recognise.
            """;

    private McMMORegionBackupStore() {
    }

    /**
     * On-shutdown entry point. Writes a complete snapshot of the in-world {@code mcmmo_regions/}
    * folder into {@code [pluginDataFolder]/region_data_backups_for_migration/[worldName]/<timestamp>Z/} and prunes
     * incomplete and over-retention snapshots. Silently skipped when the world is on the new
     * Paper shape (post-PR-#13736) or when the in-world folder has no {@code .mcm} files.
     *
     * @param world            the world whose block-tracker data should be backed up
     * @param logger           logger for progress and error messages
     * @param pluginDataFolder mcMMO's plugin data directory (e.g. {@code plugins/mcMMO/})
     */
    public static boolean backupWorld(@NotNull World world, @NotNull Logger logger,
            @NotNull Path pluginDataFolder) {
        try {
            final Path container = normalize(org.bukkit.Bukkit.getWorldContainer().toPath());
            final Path worldFolder = normalize(world.getWorldFolder().toPath());
            return backup(container, pluginDataFolder, world.getName(), worldFolder, logger,
                    Clock.systemUTC());
        } catch (RuntimeException unexpected) {
            logger.log(Level.SEVERE, BACKUP_LOG_TAG + " backup failed for world '"
                    + world.getName() + "'", unexpected);
            return false;
        }
    }

    /**
    * On-startup / on-world-load entry point. Prunes incomplete snapshots and then handles
    * restore for worlds on the new Paper shape:
     * <ul>
    *   <li>If the in-world folder is <b>empty</b> and a complete snapshot exists, copies
    *       the newest snapshot into the new in-world location and archives the migration
    *       backup data for manual reuse.</li>
    *   <li>If the in-world folder <b>already has data</b>, the Paper migration already
    *       completed successfully. Archives the backup store instead of deleting it so an
    *       operator can re-use the same snapshot set for another merge pass later.</li>
    *   <li>Independently of snapshot restore, reconciles legacy-root leftovers at
    *       {@code [container]/<worldName>/mcmmo_regions/}. Snapshot-restore runs delete those
    *       leftover files; non-snapshot runs merge them into in-world data and then delete
    *       the source files. These leftovers are never archived.</li>
     * </ul>
     *
     * @param world            the world to inspect and potentially restore data into
     * @param logger           logger for progress and error messages
     * @param pluginDataFolder mcMMO's plugin data directory (e.g. {@code plugins/mcMMO/})
     */
    public static boolean restoreWorld(@NotNull World world, @NotNull Logger logger,
            @NotNull Path pluginDataFolder) {
        try {
            final Path container = normalize(org.bukkit.Bukkit.getWorldContainer().toPath());
            final Path worldFolder = normalize(world.getWorldFolder().toPath());
            return restore(container, pluginDataFolder, world.getName(), worldFolder, logger);
        } catch (RuntimeException unexpected) {
            logger.log(Level.SEVERE, MIGRATION_LOG_TAG + " restore check failed for world '"
                    + world.getName() + "'", unexpected);
            return false;
        }
    }

    /**
     * Returns {@code true} when the world is on the Spigot / pre-26.1 Paper shape where every
     * world is a top-level child of the server container directory. On Paper 26.1+, non-overworld
     * dimensions resolve to a deeper path and this method returns {@code false}.
     *
     * <p>Public so that callers outside this package can gate logic on the current shape.
     */
    public static boolean isLegacyShape(@NotNull Path container, @NotNull String worldName,
            @NotNull Path worldFolder) {
        return normalize(worldFolder).equals(normalize(container.resolve(worldName)));
    }

    /**
     * Core backup implementation. Package-private for unit testing.
     *
     * @param container        the server container directory (parent of world folders on legacy
     *                         shape); used only for shape detection
     * @param pluginDataFolder mcMMO's plugin data directory; the backup store lives at
    *                         {@code pluginDataFolder/region_data_backups_for_migration/}
     * @param worldName        the name of the world being backed up
     * @param worldFolder      the world's current folder as returned by
     *                         {@link World#getWorldFolder()}
     * @param logger           logger for progress and error messages
     * @param clock            clock used to generate the snapshot timestamp; injectable for tests
     */
    static boolean backup(@NotNull Path container, @NotNull Path pluginDataFolder,
            @NotNull String worldName, @NotNull Path worldFolder,
            @NotNull Logger logger, @NotNull Clock clock) {
        if (!isLegacyShape(container, worldName, worldFolder)) {
            // This world is already on the new Paper layout; backup snapshots are not needed.
            return false;
        }
        final Path inWorldFolder = worldFolder.resolve(IN_WORLD_FOLDER_NAME);
        final List<Path> regionFiles = listRegionFiles(inWorldFolder);
        if (regionFiles.isEmpty()) {
            // Nothing to back up — world has no tracked block data.
            // Still clean up any crash-interrupted incomplete snapshots so the backup store
            // stays tidy even when no new snapshot is written this shutdown.
            final Path existingBackupRoot = pluginDataFolder.resolve(BACKUP_ROOT_FOLDER_NAME)
                    .resolve(worldName);
            if (Files.isDirectory(existingBackupRoot)) {
                pruneIncompleteSnapshots(existingBackupRoot, logger);
            }
            return false;
        }
        final Path backupStoreRoot = pluginDataFolder.resolve(BACKUP_ROOT_FOLDER_NAME);
        final Path worldBackupRoot = backupStoreRoot.resolve(worldName);
        try {
            Files.createDirectories(worldBackupRoot);
        } catch (IOException ioException) {
            logger.log(Level.SEVERE, BACKUP_LOG_TAG + " could not create backup-store root "
                    + worldBackupRoot, ioException);
            return false;
        }
        writeReadme(backupStoreRoot, logger);
        pruneIncompleteSnapshots(worldBackupRoot, logger);

        final String snapshotName = SNAPSHOT_TIMESTAMP_FORMAT.format(clock.instant());
        final Path snapshotFinal = worldBackupRoot.resolve(snapshotName);
        if (Files.exists(snapshotFinal)) {
            logger.fine(BACKUP_LOG_TAG + " snapshot " + snapshotName
                    + " already exists for world '" + worldName + "', skipping");
            return false;
        }

        logger.info(BACKUP_LOG_TAG + " Backing up region data for world named '"
            + worldName + "' to " + snapshotFinal);

        // Stage the snapshot under a *.tmp name so that a crash mid-copy leaves a clearly
        // incomplete artifact rather than a folder that looks complete but is not.
        final Path snapshotTemp = worldBackupRoot.resolve(snapshotName + IN_PROGRESS_SUFFIX);
        deleteRecursivelyQuietly(snapshotTemp);
        try {
            Files.createDirectories(snapshotTemp);
        } catch (IOException ioException) {
            logger.log(Level.WARNING, BACKUP_LOG_TAG + " could not create staging folder "
                    + snapshotTemp, ioException);
            return false;
        }

        int copied = 0;
        for (Path source : regionFiles) {
            final Path destination = snapshotTemp.resolve(source.getFileName().toString());
            try {
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                copied++;
            } catch (IOException ioException) {
                logger.log(Level.WARNING, BACKUP_LOG_TAG + " failed to copy " + source
                        + " into staging folder " + snapshotTemp.getFileName()
                        + "; aborting snapshot", ioException);
                deleteRecursivelyQuietly(snapshotTemp);
                return false;
            }
        }

        // Atomic rename: other processes always see either the old staging name or the final
        // name, never a partially-renamed state.
        try {
            try {
                Files.move(snapshotTemp, snapshotFinal, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException atomicNotSupported) {
                Files.move(snapshotTemp, snapshotFinal);
            }
        } catch (IOException ioException) {
            logger.log(Level.WARNING, BACKUP_LOG_TAG + " could not rename staging folder to "
                    + snapshotFinal, ioException);
            deleteRecursivelyQuietly(snapshotTemp);
            return false;
        }

        // Write the completion stamp LAST. Any snapshot without this file is treated as
        // incomplete by all readers and is automatically cleaned up.
        try {
            writeSentinel(snapshotFinal, worldName, copied, clock.instant());
        } catch (IOException ioException) {
            logger.log(Level.WARNING,
                    BACKUP_LOG_TAG + " could not write completion stamp in " + snapshotFinal
                    + "; snapshot will be pruned on next startup", ioException);
            return false;
        }
        logger.info(BACKUP_LOG_TAG + " Backup complete for world '" + worldName + "', "
            + copied + " mcMMO region file(s) were successfully backed up.");

        pruneOldSnapshots(worldBackupRoot, logger);
        return true;
    }

    /**
     * Core restore implementation. Package-private for unit testing.
     *
     * @param container        the server container directory; used for shape detection
     * @param pluginDataFolder mcMMO's plugin data directory; the backup store lives at
    *                         {@code pluginDataFolder/region_data_backups_for_migration/}
     * @param worldName        the name of the world being inspected
     * @param worldFolder      the world's current folder as returned by
     *                         {@link World#getWorldFolder()}
     * @param logger           logger for progress and error messages
     */
    static boolean restore(@NotNull Path container, @NotNull Path pluginDataFolder,
            @NotNull String worldName, @NotNull Path worldFolder, @NotNull Logger logger) {
        final Path worldBackupRoot = pluginDataFolder.resolve(BACKUP_ROOT_FOLDER_NAME)
                .resolve(worldName);
        final Path inWorldFolder = worldFolder.resolve(IN_WORLD_FOLDER_NAME);
        final Path legacyRootRegionFolder = container.resolve(worldName)
                .resolve(IN_WORLD_FOLDER_NAME);

        if (Files.isDirectory(worldBackupRoot)) {
            pruneIncompleteSnapshots(worldBackupRoot, logger);
        }

        if (isLegacyShape(container, worldName, worldFolder)) {
            // On the legacy shape, the in-world folder is authoritative. Restoring from the
            // backup store here would silently overwrite data that the operator considers live.
            return false;
        }

        if (!listRegionFiles(inWorldFolder).isEmpty()) {
            // In-world already has data on the new Paper layout — either mcMMO already
            // completed the restore on a prior startup, or Paper's migration preserved the
            // mcmmo_regions/ folder. If the backup store still contains real snapshots, move
            // that tree aside so an admin can re-use it for another merge pass later.
            int mergedCount;
            if (Files.isDirectory(worldBackupRoot)) {
                if (hasRestorableContent(worldBackupRoot)) {
                    archiveRestoredBackupStore(pluginDataFolder.resolve(BACKUP_ROOT_FOLDER_NAME),
                            worldBackupRoot, worldName, logger);
                } else {
                    deleteRecursivelyQuietly(worldBackupRoot);
                }
            }
                mergedCount = mergeLegacyRootRegionDataAndDeleteSource(
                    legacyRootRegionFolder, inWorldFolder, worldName, logger);
            return mergedCount > 0;
        }

        final Path newestSnapshot = Files.isDirectory(worldBackupRoot)
                ? newestCompleteSnapshot(worldBackupRoot)
                : null;
        if (newestSnapshot == null) {
                final int mergedCount = mergeLegacyRootRegionDataAndDeleteSource(
                    legacyRootRegionFolder, inWorldFolder, worldName, logger);
            return mergedCount > 0;
        }
        final long restoreStartNanos = System.nanoTime();
        logger.info(MIGRATION_LOG_TAG + " Restoring region data for world named '"
            + worldName + "'... this may take a while.");
        try {
            Files.createDirectories(inWorldFolder);
        } catch (IOException ioException) {
            logger.log(Level.SEVERE, MIGRATION_LOG_TAG + " could not create in-world folder "
                    + inWorldFolder + " during restore", ioException);
            return false;
        }
        final int restored = restoreSnapshot(newestSnapshot, inWorldFolder, worldName, logger);
        final long restoreElapsedNanos = System.nanoTime() - restoreStartNanos;
        logger.info(MIGRATION_LOG_TAG + " Restore complete for world '" + worldName + "', "
            + restored + " mcMMO region file(s) were successfully restored in "
            + formatDurationNanos(restoreElapsedNanos) + ".");
        deleteLegacyRootRegionDataAfterSnapshotRestore(legacyRootRegionFolder, worldName, logger);
        // The backup store has served its purpose for this world. Move it into an archive so
        // an admin can re-use the same data for another merge pass if needed.
        archiveRestoredBackupStore(pluginDataFolder.resolve(BACKUP_ROOT_FOLDER_NAME),
                worldBackupRoot, worldName, logger);
        return restored > 0;
    }

    private static void deleteLegacyRootRegionDataAfterSnapshotRestore(
            @NotNull Path legacyRootRegionFolder, @NotNull String worldName,
            @NotNull Logger logger) {
        final List<Path> legacyRegionFiles = listRegionFiles(legacyRootRegionFolder);
        if (legacyRegionFiles.isEmpty()) {
            return;
        }
        logger.fine(MIGRATION_LOG_TAG + " world '" + worldName
                + "': migration cleanup START - deleting " + legacyRegionFiles.size()
                + " legacy-root region file(s) from " + legacyRootRegionFolder
                + " after successful snapshot restore (no archive)");
        int deletedCount = 0;
        int failedCount = 0;
        for (Path source : legacyRegionFiles) {
            try {
                Files.deleteIfExists(source);
                deletedCount++;
            } catch (IOException ioException) {
                failedCount++;
                logger.log(Level.WARNING, MIGRATION_LOG_TAG + " failed to delete legacy-root region file "
                        + source + " for world '" + worldName + "'", ioException);
            }
        }
        if (failedCount == 0) {
            deleteRecursivelyQuietly(legacyRootRegionFolder);
        }
        logger.fine(MIGRATION_LOG_TAG + " world '" + worldName
                + "': migration cleanup COMPLETE - deleted " + deletedCount
                + " legacy-root region file(s)"
                + (failedCount > 0 ? " (" + failedCount + " failed; files left in place)" : ""));
    }

    private static int mergeLegacyRootRegionDataAndDeleteSource(
            @NotNull Path legacyRootRegionFolder, @NotNull Path inWorldFolder,
            @NotNull String worldName, @NotNull Logger logger) {
        final List<Path> legacyRegionFiles = listRegionFiles(legacyRootRegionFolder);
        if (legacyRegionFiles.isEmpty()) {
            return 0;
        }
        logger.fine(MIGRATION_LOG_TAG + " world '" + worldName
                + "': migration reconcile START - merging " + legacyRegionFiles.size()
                + " legacy-root region file(s) from " + legacyRootRegionFolder + " into "
                + inWorldFolder + " and deleting source files (no archive)");
        try {
            Files.createDirectories(inWorldFolder);
        } catch (IOException ioException) {
            logger.log(Level.SEVERE, MIGRATION_LOG_TAG + " could not create in-world folder "
                    + inWorldFolder + " during legacy-root reconcile", ioException);
            return 0;
        }
        int mergedCount = 0;
        int failedCount = 0;
        for (Path source : legacyRegionFiles) {
            final Path destination = inWorldFolder.resolve(source.getFileName());
            try {
                copyOrMergeRegionFile(source, destination);
                Files.deleteIfExists(source);
                mergedCount++;
            } catch (IOException | RuntimeException failure) {
                failedCount++;
                logger.log(Level.WARNING, MIGRATION_LOG_TAG + " failed to reconcile legacy-root file "
                        + source.getFileName() + " for world '" + worldName + "'", failure);
            }
        }
        if (failedCount == 0) {
            deleteRecursivelyQuietly(legacyRootRegionFolder);
        }
        logger.fine(MIGRATION_LOG_TAG + " world '" + worldName
                + "': migration reconcile COMPLETE - merged " + mergedCount
                + " legacy-root region file(s)"
                + (failedCount > 0 ? " (" + failedCount + " failed; files left in place)" : ""));
        return mergedCount;
    }

    private static boolean hasRestorableContent(@NotNull Path worldBackupRoot) {
        return newestCompleteSnapshot(worldBackupRoot) != null
                || !listRegionFiles(worldBackupRoot).isEmpty();
    }

    private static void archiveRestoredBackupStore(@NotNull Path backupStoreRoot,
            @NotNull Path worldBackupRoot, @NotNull String worldName, @NotNull Logger logger) {
        final Path archiveRoot = backupStoreRoot
                .resolve(ARCHIVE_ROOT_FOLDER_NAME)
                .resolve(worldName);
        try {
            Files.createDirectories(archiveRoot);
        } catch (IOException ioException) {
            logger.log(Level.WARNING, MIGRATION_LOG_TAG + " could not create archive root "
                + archiveRoot + " for world '" + worldName + "'", ioException);
            return;
        }
        final String archiveName = SNAPSHOT_TIMESTAMP_FORMAT.format(Instant.now());
        final Path archiveDestination = archiveRoot.resolve(archiveName);
        if (Files.exists(archiveDestination)) {
            logger.fine(MIGRATION_LOG_TAG + " world '" + worldName + "': archive destination "
                + archiveDestination + " already exists; leaving restored data in place");
            return;
        }
        try {
            Files.move(worldBackupRoot, archiveDestination);
        } catch (IOException ioException) {
            logger.log(Level.WARNING, MIGRATION_LOG_TAG + " could not archive restored backup store "
                + worldBackupRoot + " to " + archiveDestination, ioException);
            return;
        }
        logger.fine(MIGRATION_LOG_TAG + " world '" + worldName
            + "': migration backup archive COMPLETE - saved previous migration backup data to "
            + archiveDestination);
    }

    /** Copies or union-merges every region file from {@code snapshot} into {@code inWorldFolder}. */
    private static int restoreSnapshot(@NotNull Path snapshot, @NotNull Path inWorldFolder,
            @NotNull String worldName, @NotNull Logger logger) {
        final List<Path> regionFiles;
        try (Stream<Path> stream = Files.list(snapshot)) {
            regionFiles = stream
                    .filter(Files::isRegularFile)
                    .filter(McMMORegionBackupStore::isRegionFile)
                    .sorted()
                    .toList();
        } catch (IOException ioException) {
            logger.log(Level.WARNING, MIGRATION_LOG_TAG + " could not list snapshot " + snapshot,
                    ioException);
            return 0;
        }
        int restored = 0;
        for (Path source : regionFiles) {
            final Path destination = inWorldFolder.resolve(source.getFileName());
            try {
                copyOrMergeRegionFile(source, destination);
                restored++;
            } catch (IOException | RuntimeException failure) {
                logger.log(Level.WARNING, MIGRATION_LOG_TAG + " failed to restore "
                        + source.getFileName() + " for world '" + worldName + "'", failure);
            }
        }
        return restored;
    }

    /**
     * Copies {@code source} to {@code destination}. If {@code destination} already exists,
     * union-merges the two region files chunk-by-chunk via
     * {@link #mergeRegionFile(Path, Path, int, int)} so that no placed-block record from
     * either side is lost.
     */
    static void copyOrMergeRegionFile(@NotNull Path source, @NotNull Path destination)
            throws IOException {
        if (!Files.exists(destination)) {
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            return;
        }
        final Matcher matcher = REGION_FILE_PATTERN.matcher(source.getFileName().toString());
        if (!matcher.matches()) {
            return;
        }
        final int regionX = Integer.parseInt(matcher.group(1));
        final int regionZ = Integer.parseInt(matcher.group(2));
        mergeRegionFile(source, destination, regionX, regionZ);
    }

    /**
     * Prunes snapshot folders that lack the {@value #BACKUP_COMPLETE_SENTINEL} completion stamp
     * (including {@code *.tmp} staging folders left by a crashed prior backup). Package-private
     * for testing.
     */
    static void pruneIncompleteSnapshots(@NotNull Path worldBackupRoot, @NotNull Logger logger) {
        if (!Files.isDirectory(worldBackupRoot)) {
            return;
        }
        final List<Path> children;
        try (Stream<Path> stream = Files.list(worldBackupRoot)) {
            children = stream.filter(Files::isDirectory).toList();
        } catch (IOException ioException) {
            logger.log(Level.FINE, BACKUP_LOG_TAG + " could not list backup-store root "
                    + worldBackupRoot + " for cleanup", ioException);
            return;
        }
        for (Path child : children) {
            final String name = child.getFileName().toString();
            final boolean isTemp = name.endsWith(IN_PROGRESS_SUFFIX);
            final boolean isSnapshot = SNAPSHOT_DIR_PATTERN.matcher(name).matches();
            if (!isTemp && !isSnapshot) {
                continue;
            }
            if (isSnapshot && Files.isRegularFile(child.resolve(BACKUP_COMPLETE_SENTINEL))) {
                continue;
            }
            logger.fine(BACKUP_LOG_TAG + " pruning incomplete snapshot " + child);
            deleteRecursivelyQuietly(child);
        }
    }

    /**
     * Trims the world backup-store root to the {@value #MAX_BACKUPS_RETAINED} newest complete
     * snapshots, deleting the oldest extras. Package-private for testing.
     */
    static void pruneOldSnapshots(@NotNull Path worldBackupRoot, @NotNull Logger logger) {
        final List<Path> complete = listCompleteSnapshots(worldBackupRoot);
        if (complete.size() <= MAX_BACKUPS_RETAINED) {
            return;
        }
        // Sort oldest-first (timestamp folder names sort lexically A→Z = oldest→newest).
        complete.sort(Comparator.comparing(p -> p.getFileName().toString()));
        final int dropCount = complete.size() - MAX_BACKUPS_RETAINED;
        for (int i = 0; i < dropCount; i++) {
            final Path drop = complete.get(i);
            LogUtils.debug(logger, BACKUP_LOG_TAG + " pruning old snapshot " + drop.getFileName()
                    + " (mcMMO only keeps the " + MAX_BACKUPS_RETAINED + " newest backups)");
            deleteRecursivelyQuietly(drop);
        }
    }

    /**
     * Returns the lexicographically newest complete snapshot (i.e., one that contains a
     * {@value #BACKUP_COMPLETE_SENTINEL} file) under {@code worldBackupRoot}, or {@code null} if
     * none exists. Package-private for testing.
     */
    static @Nullable Path newestCompleteSnapshot(@NotNull Path worldBackupRoot) {
        final List<Path> complete = listCompleteSnapshots(worldBackupRoot);
        if (complete.isEmpty()) {
            return null;
        }
        complete.sort(Comparator.comparing(p -> p.getFileName().toString()));
        return complete.get(complete.size() - 1);
    }

    private static @NotNull List<Path> listCompleteSnapshots(@NotNull Path worldBackupRoot) {
        if (!Files.isDirectory(worldBackupRoot)) {
            return Collections.emptyList();
        }
        try (Stream<Path> stream = Files.list(worldBackupRoot)) {
            return stream
                    .filter(Files::isDirectory)
                    .filter(p -> SNAPSHOT_DIR_PATTERN.matcher(p.getFileName().toString()).matches())
                    .filter(p -> Files.isRegularFile(p.resolve(BACKUP_COMPLETE_SENTINEL)))
                    .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        } catch (IOException ioException) {
            return Collections.emptyList();
        }
    }

    private static @NotNull List<Path> listRegionFiles(@NotNull Path folder) {
        if (!Files.isDirectory(folder)) {
            return Collections.emptyList();
        }
        try (Stream<Path> stream = Files.list(folder)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(McMMORegionBackupStore::isRegionFile)
                    .sorted()
                    .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        } catch (IOException ioException) {
            return Collections.emptyList();
        }
    }

    private static boolean isRegionFile(@NotNull Path path) {
        return REGION_FILE_PATTERN.matcher(path.getFileName().toString()).matches();
    }

    private static void writeSentinel(@NotNull Path snapshot, @NotNull String worldName,
            int fileCount, @NotNull Instant timestamp) throws IOException {
        final String body = "timestamp=" + timestamp + '\n'
                + "world_name=" + worldName + '\n'
                + "file_count=" + fileCount + '\n'
                + "format_version=1\n";
        Files.writeString(snapshot.resolve(BACKUP_COMPLETE_SENTINEL), body,
                StandardCharsets.UTF_8);
    }

    /**
     * Writes a README into {@code backupStoreRoot} if one is not already present. The README
     * explains the purpose of the backup store and gives operators guidance on edge cases.
     * This method is idempotent and silently ignores write failures — the README is
     * documentation, not data.
     */
    public static void writeReadme(@NotNull Path backupStoreRoot, @NotNull Logger logger) {
        final Path readme = backupStoreRoot.resolve(README_FILE_NAME);
        if (Files.exists(readme)) {
            return;
        }
        try {
            Files.writeString(readme, README_BODY, StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            logger.log(Level.FINE, BACKUP_LOG_TAG + " could not write README in "
                    + backupStoreRoot, ioException);
        }
    }

    private static void deleteRecursivelyQuietly(@NotNull Path root) {
        if (!Files.exists(root)) {
            return;
        }
        try (Stream<Path> stream = Files.walk(root)) {
            stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (DirectoryNotEmptyException ignored) {
                    // Another writer added a file mid-walk; leave it for the next cleanup pass.
                } catch (IOException ignored) {
                    // Best-effort cleanup; swallow all other I/O errors.
                }
            });
        } catch (IOException ignored) {
            // Best-effort cleanup; swallow.
        }
    }

    private static @NotNull Path normalize(@NotNull Path path) {
        return path.toAbsolutePath().normalize();
    }

    private static @NotNull String formatDurationNanos(long elapsedNanos) {
        final long sanitizedNanos = Math.max(0L, elapsedNanos);
        final long totalMillis = java.time.Duration.ofNanos(sanitizedNanos).toMillis();
        if (totalMillis < 1000L) {
            return totalMillis + "ms";
        }

        final long totalSeconds = sanitizedNanos / 1_000_000_000L;
        final long hours = totalSeconds / 3600L;
        final long minutes = (totalSeconds % 3600L) / 60L;
        final long seconds = totalSeconds % 60L;

        final StringBuilder displayBuilder = new StringBuilder();
        if (hours > 0L) {
            displayBuilder.append(hours).append("h");
        }
        if (minutes > 0L) {
            if (displayBuilder.length() > 0) {
                displayBuilder.append(' ');
            }
            displayBuilder.append(minutes).append("m");
        }
        if (seconds > 0L) {
            if (displayBuilder.length() > 0) {
                displayBuilder.append(' ');
            }
            displayBuilder.append(seconds).append("s");
        }

        return displayBuilder.length() == 0 ? totalMillis + "ms" : displayBuilder.toString();
    }

    /**
     * Merges a single region file from {@code source} into an existing {@code destination}.
     * For every 32×32 chunk slot in the region:
     * <ul>
     *   <li>If only {@code source} has data for the slot, write it into {@code destination}.</li>
     *   <li>If both have data, OR the placed-block bits from {@code source} into
     *       {@code destination} via {@link BitSetChunkStore#mergeFrom}. No placed-block record
     *       is ever discarded.</li>
     *   <li>If only {@code destination} has data, leave it untouched.</li>
     * </ul>
     *
     * @return the number of chunk slots written or merged into {@code destination}
     */
    static int mergeRegionFile(@NotNull Path source, @NotNull Path destination,
            int regionX, int regionZ) throws IOException {
        int affected = 0;
        final McMMOSimpleRegionFile sourceRegion = new McMMOSimpleRegionFile(
                source.toFile(), regionX, regionZ);
        try {
            final McMMOSimpleRegionFile destinationRegion = new McMMOSimpleRegionFile(
                    destination.toFile(), regionX, regionZ);
            try {
                final int chunkOriginX = regionX << 5;
                final int chunkOriginZ = regionZ << 5;
                for (int dx = 0; dx < 32; dx++) {
                    for (int dz = 0; dz < 32; dz++) {
                        final int chunkX = chunkOriginX + dx;
                        final int chunkZ = chunkOriginZ + dz;
                        final ChunkStore sourceChunk;
                        try (DataInputStream in = sourceRegion.getInputStream(chunkX, chunkZ)) {
                            if (in == null) {
                                continue;
                            }
                            sourceChunk = BitSetChunkStore.Serialization.readChunkStore(in);
                        }
                        if (sourceChunk == null) {
                            continue;
                        }
                        final ChunkStore destinationChunk;
                        try (DataInputStream in = destinationRegion.getInputStream(chunkX, chunkZ)) {
                            destinationChunk = in == null
                                    ? null
                                    : BitSetChunkStore.Serialization.readChunkStore(in);
                        }
                        if (destinationChunk == null) {
                            try (DataOutputStream out = destinationRegion.getOutputStream(chunkX,
                                    chunkZ)) {
                                BitSetChunkStore.Serialization.writeChunkStore(out, sourceChunk);
                            }
                            affected++;
                        } else if (destinationChunk instanceof BitSetChunkStore destinationBitSet
                                && sourceChunk instanceof BitSetChunkStore sourceBitSet) {
                            destinationBitSet.mergeFrom(sourceBitSet);
                            if (destinationBitSet.isDirty()) {
                                try (DataOutputStream out = destinationRegion.getOutputStream(
                                        chunkX, chunkZ)) {
                                    BitSetChunkStore.Serialization.writeChunkStore(out,
                                            destinationBitSet);
                                }
                                affected++;
                            }
                        }
                    }
                }
            } finally {
                destinationRegion.close();
            }
        } finally {
            sourceRegion.close();
        }
        return affected;
    }
}
