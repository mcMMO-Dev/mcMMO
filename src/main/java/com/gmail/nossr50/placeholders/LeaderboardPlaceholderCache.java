package com.gmail.nossr50.placeholders;

import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.skills.SkillTools;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Immutable-snapshot cache for leaderboard PlaceholderAPI lookups.
 * <p>
 * Placeholder resolution reads from an atomically swapped in-memory snapshot, while snapshot
 * rebuilds happen asynchronously and in bulk.
 */
public class LeaderboardPlaceholderCache {
    private static final int FIRST_PAGE = 1;

    private final int maxTrackedRank;
    private final @NotNull LeaderboardDataSource dataSource;
    private final @NotNull Logger logger;
    private final @Nullable mcMMO plugin;
    private final long refreshIntervalTicks;

    // Readers always observe a fully built snapshot (old or new), never partial state.
    private final AtomicReference<LeaderboardSnapshot> snapshot;
    // Single-flight guard to avoid overlapping rebuild work.
    private final AtomicBoolean refreshInProgress = new AtomicBoolean(false);
    private volatile @Nullable WrappedTask refreshTask;

    /**
     * Constructor.
     *
     * @param plugin Plugin reference for scheduler + logger access.
     * @param maxTrackedRank Highest rank position to keep cached.
     * @param refreshIntervalTicks Periodic async refresh interval in ticks.
     */
    public LeaderboardPlaceholderCache(@NotNull mcMMO plugin, int maxTrackedRank,
            long refreshIntervalTicks) {
        this(maxTrackedRank, (skill, pageNumber, statsPerPage) -> mcMMO.getDatabaseManager()
                        .readLeaderboard(skill, pageNumber, statsPerPage),
                plugin.getLogger(), plugin, refreshIntervalTicks);
    }

    LeaderboardPlaceholderCache(int maxTrackedRank, @NotNull LeaderboardDataSource dataSource,
            @NotNull Logger logger) {
        this(maxTrackedRank, dataSource, logger, null, 0);
    }

    private LeaderboardPlaceholderCache(int maxTrackedRank,
            @NotNull LeaderboardDataSource dataSource, @NotNull Logger logger,
            @Nullable mcMMO plugin, long refreshIntervalTicks) {
        this.maxTrackedRank = Math.max(maxTrackedRank, 1);
        this.dataSource = dataSource;
        this.logger = logger;
        this.plugin = plugin;
        this.refreshIntervalTicks = refreshIntervalTicks;
        this.snapshot = new AtomicReference<>(LeaderboardSnapshot.empty(this.maxTrackedRank));
    }

    private static @NotNull List<LeaderboardEntry> toEntries(@NotNull List<PlayerStat> stats) {
        if (stats.isEmpty()) {
            return List.of();
        }

        final List<LeaderboardEntry> converted = new ArrayList<>(stats.size());
        for (PlayerStat stat : stats) {
            final String playerName = stat.playerName() == null ? "" : stat.playerName();
            converted.add(new LeaderboardEntry(playerName, String.valueOf(stat.value())));
        }
        return Collections.unmodifiableList(converted);
    }

    /**
     * Starts periodic async refreshes and schedules an async warm-up refresh.
     * <p>
     * No-op when running under a test constructor without plugin scheduler access.
     * <p>
     * Cost note: the warm-up refresh triggers full top-N leaderboard reads for every non-child
     * skill plus overall. This is intentionally done off both the placeholder request path and
     * the main thread.
     */
    public void start() {
        if (plugin == null) {
            return;
        }

        plugin.getFoliaLib().getScheduler().runAsync(task -> refreshNow());

        if (refreshIntervalTicks > 0) {
            refreshTask = plugin.getFoliaLib().getScheduler()
                    .runTimerAsync(this::refreshNow, refreshIntervalTicks, refreshIntervalTicks);
        }
    }

    private boolean isDatabaseReady() {
        return mcMMO.getDatabaseManager() != null;
    }

    /**
     * Stops the periodic refresh task if active.
     */
    public void shutdown() {
        final WrappedTask localTask = refreshTask;
        if (localTask != null) {
            localTask.cancel();
            refreshTask = null;
        }
    }

    /**
     * @param skill Skill scope, or {@code null} for overall leaderboard.
     * @param position 1-based leaderboard position.
     * @return Cached player name for the position, or empty string when unavailable.
     */
    public String getPlayerName(@Nullable PrimarySkillType skill, int position) {
        return snapshot.get().nameAt(skill, position);
    }

    /**
     * @param skill Skill scope, or {@code null} for overall leaderboard.
     * @param position 1-based leaderboard position.
     * @return Cached numeric value for the position, or empty string when unavailable.
     */
    public String getValue(@Nullable PrimarySkillType skill, int position) {
        return snapshot.get().valueAt(skill, position);
    }

    /**
     * Rebuilds and swaps the snapshot in one pass.
     * <p>
     * This performs batched leaderboard reads across all tracked skills and should only run from
     * async scheduled refreshes (never from placeholder request handling).
     * The refresh is also gated on database readiness and will no-op until the
     * mcMMO database manager is initialized.
     *
     * @return {@code true} if a new snapshot was built and swapped, {@code false} if the refresh
     * was skipped (already running) or failed.
     */
    boolean refreshNow() {
        // Runtime guard: never execute refresh logic on the primary thread.
        if (plugin != null && Bukkit.isPrimaryThread()) {
            plugin.getFoliaLib().getScheduler().runAsync(task -> refreshNow());
            return false;
        }

        // Runtime guard: do not query leaderboards until mcMMO has initialized its DB manager.
        if (plugin != null && !isDatabaseReady()) {
            return false;
        }

        if (!refreshInProgress.compareAndSet(false, true)) {
            return false;
        }

        try {
            snapshot.set(buildSnapshot());
            return true;
        } catch (RuntimeException e) {
            logger.log(Level.WARNING, "Failed to refresh PlaceholderAPI leaderboard cache", e);
            return false;
        } finally {
            refreshInProgress.set(false);
        }
    }

    private @NotNull LeaderboardSnapshot buildSnapshot() {
        final Map<PrimarySkillType, List<LeaderboardEntry>> skillEntries = new EnumMap<>(
                PrimarySkillType.class);

        for (PrimarySkillType skill : SkillTools.NON_CHILD_SKILLS) {
            final List<PlayerStat> stats = readTopRows(skill);
            skillEntries.put(skill, toEntries(stats));
        }

        final List<PlayerStat> overallStats = readTopRows(null);
        return new LeaderboardSnapshot(Map.copyOf(skillEntries), toEntries(overallStats),
                maxTrackedRank);
    }

    /**
     * Reads the top-N rows in a single query for one leaderboard scope.
     * <p>
     * This is a potentially expensive backend read (SQL/flatfile).
     */
    private @NotNull List<PlayerStat> readTopRows(@Nullable PrimarySkillType skill) {
        try {
            return dataSource.readLeaderboard(skill, FIRST_PAGE, maxTrackedRank);
        } catch (InvalidSkillException e) {
            logger.log(
                    Level.WARNING, "Invalid skill while building leaderboard cache: " + skill, e);
            return List.of();
        }
    }

    @FunctionalInterface
    interface LeaderboardDataSource {
        @NotNull List<PlayerStat> readLeaderboard(@Nullable PrimarySkillType skill, int pageNumber,
                int statsPerPage) throws InvalidSkillException;
    }

    private record LeaderboardEntry(@NotNull String playerName, @NotNull String value) {
    }

    private record LeaderboardSnapshot(
            @NotNull Map<PrimarySkillType, List<LeaderboardEntry>> skillEntries,
            @NotNull List<LeaderboardEntry> overallEntries, int maxTrackedRank) {
        private static @NotNull LeaderboardSnapshot empty(int maxTrackedRank) {
            return new LeaderboardSnapshot(Map.of(), List.of(), maxTrackedRank);
        }

        private @NotNull String nameAt(@Nullable PrimarySkillType skill, int position) {
            final LeaderboardEntry entry = entryAt(skill, position);
            return entry == null ? "" : entry.playerName();
        }

        private @NotNull String valueAt(@Nullable PrimarySkillType skill, int position) {
            final LeaderboardEntry entry = entryAt(skill, position);
            return entry == null ? "" : entry.value();
        }

        private @Nullable LeaderboardEntry entryAt(@Nullable PrimarySkillType skill, int position) {
            // We intentionally cap lookups to the configured snapshot depth.
            if (position < 1 || position > maxTrackedRank) {
                return null;
            }

            final List<LeaderboardEntry> entries =
                    (skill == null) ? overallEntries : skillEntries.get(skill);
            if (entries == null) {
                return null;
            }

            final int index = position - 1;
            if (index >= entries.size()) {
                return null;
            }

            return entries.get(index);
        }
    }
}
