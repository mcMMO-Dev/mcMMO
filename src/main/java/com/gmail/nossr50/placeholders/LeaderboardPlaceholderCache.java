package com.gmail.nossr50.placeholders;

import com.gmail.nossr50.datatypes.database.LeaderboardSnapshot;
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
    private final int maxTrackedRank;
    private final @NotNull LeaderboardDataSource dataSource;
    private final @NotNull Logger logger;
    private final @Nullable mcMMO plugin;
    private final long refreshIntervalTicks;

    // Readers always observe a fully built snapshot (old or new), never partial state.
    private final AtomicReference<CachedLeaderboards> snapshot;
    // Single-flight guard to avoid overlapping rebuild work.
    private final AtomicBoolean refreshInProgress = new AtomicBoolean(false);
    // Set by placeholder lookups, read-and-cleared by refreshNow(). When no placeholder was
    // resolved during a whole refresh interval there is nobody to serve fresh data to, so the
    // periodic refresh skips its backend read. Starts true so the startup warm-up always runs.
    private volatile boolean lookedUpSinceRefresh = true;
    // Flips true when a periodic refresh was skipped for idleness; the first lookup afterwards
    // schedules one immediate async refresh so freshness recovers without waiting out the rest
    // of the timer interval.
    private final AtomicBoolean idle = new AtomicBoolean(false);
    private volatile @Nullable WrappedTask refreshTask;
    // Once stopped, late-firing refreshes become no-ops so they cannot race database shutdown
    // during plugin disable.
    private volatile boolean stopped;

    /**
     * Constructor.
     *
     * @param plugin Plugin reference for scheduler + logger access.
     * @param maxTrackedRank Highest rank position to keep cached.
     * @param refreshIntervalTicks Periodic async refresh interval in ticks.
     */
    public LeaderboardPlaceholderCache(@NotNull mcMMO plugin, int maxTrackedRank,
            long refreshIntervalTicks) {
        // readLeaderboardSnapshot propagates backend failures, so a refresh during a database
        // outage keeps the last good snapshot instead of swapping in empty results.
        this(maxTrackedRank, perScopeLimit -> mcMMO.getDatabaseManager()
                        .readLeaderboardSnapshot(perScopeLimit),
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
        this.snapshot = new AtomicReference<>(CachedLeaderboards.empty());
    }

    private @NotNull List<LeaderboardEntry> toEntries(@NotNull List<PlayerStat> stats) {
        if (stats.isEmpty()) {
            return List.of();
        }

        // Truncate at the configured snapshot depth so a data source returning more rows than
        // requested can never make lookups past that depth succeed.
        final int size = Math.min(stats.size(), maxTrackedRank);
        final List<LeaderboardEntry> converted = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            final PlayerStat stat = stats.get(i);
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
     * Cost note: the warm-up refresh triggers one bulk top-N leaderboard read covering every
     * non-child skill plus overall. This is intentionally done off both the placeholder request
     * path and the main thread.
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
     * Stops the periodic refresh task if active and turns any further refresh attempts into
     * no-ops.
     */
    public void shutdown() {
        stopped = true;
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
        noteLookup();
        return snapshot.get().nameAt(skill, position);
    }

    /**
     * @param skill Skill scope, or {@code null} for overall leaderboard.
     * @param position 1-based leaderboard position.
     * @return Cached numeric value for the position, or empty string when unavailable.
     */
    public String getValue(@Nullable PrimarySkillType skill, int position) {
        noteLookup();
        return snapshot.get().valueAt(skill, position);
    }

    /**
     * Records that a placeholder lookup happened, keeping the periodic refresh alive. The first
     * lookup after an idle stretch also schedules one immediate async refresh so the snapshot
     * catches back up right away; the lookup itself still returns the current snapshot without
     * blocking.
     */
    private void noteLookup() {
        lookedUpSinceRefresh = true;

        if (idle.compareAndSet(true, false) && plugin != null && !stopped) {
            plugin.getFoliaLib().getScheduler().runAsync(task -> refreshNow());
        }
    }

    /**
     * Rebuilds and swaps the snapshot in one pass.
     * <p>
     * This performs one bulk leaderboard read covering all tracked scopes and must only run
     * from async scheduled refreshes (never from placeholder request handling). A call on the
     * primary thread or before the mcMMO database manager is initialized is a caller bug: it
     * is rejected with a warning instead of being retried or rescheduled here.
     *
     * @return {@code true} if a new snapshot was built and swapped, {@code false} if the refresh
     * was skipped (already running, or no placeholder was resolved since the previous refresh)
     * or failed.
     */
    boolean refreshNow() {
        // A stopped cache never refreshes; its snapshot only serves until the plugin disables.
        if (stopped) {
            return false;
        }

        // Fail fast: every production refresh is dispatched through the async scheduler, so a
        // primary-thread call means a caller skipped that dispatch.
        if (plugin != null && Bukkit.isPrimaryThread()) {
            logger.warning(
                    "Rejected PlaceholderAPI leaderboard cache refresh on the primary thread; "
                            + "refreshes must be dispatched asynchronously");
            return false;
        }

        // Fail fast: the cache only starts after mcMMO initializes its database manager, so a
        // refresh arriving earlier means a caller broke that startup ordering.
        if (plugin != null && !isDatabaseReady()) {
            logger.warning(
                    "Rejected PlaceholderAPI leaderboard cache refresh before the mcMMO database "
                            + "manager was initialized");
            return false;
        }

        // Idle skip: nobody resolved a placeholder since the previous refresh, so there is no
        // audience for fresh data. Park instead of hitting the backend; the next lookup
        // schedules one immediate refresh to catch back up.
        if (!lookedUpSinceRefresh) {
            idle.set(true);
            return false;
        }
        lookedUpSinceRefresh = false;

        if (!refreshInProgress.compareAndSet(false, true)) {
            return false;
        }

        try {
            snapshot.set(buildSnapshot());
            return true;
        } catch (RuntimeException e) {
            // A refresh interrupted by plugin disable is expected; don't warn about it.
            if (!stopped) {
                logger.log(Level.WARNING, "Failed to refresh PlaceholderAPI leaderboard cache", e);
            }
            return false;
        } finally {
            refreshInProgress.set(false);
        }
    }

    private @NotNull CachedLeaderboards buildSnapshot() {
        // One bulk backend read (a single file scan for FlatFile, one query batch for SQL)
        // covers every tracked scope.
        final LeaderboardSnapshot leaderboards = dataSource.readLeaderboards(maxTrackedRank);

        final Map<PrimarySkillType, List<LeaderboardEntry>> skillEntries = new EnumMap<>(
                PrimarySkillType.class);
        for (PrimarySkillType skill : SkillTools.NON_CHILD_SKILLS) {
            skillEntries.put(skill, toEntries(
                    leaderboards.skillLeaderboards().getOrDefault(skill, List.of())));
        }

        return new CachedLeaderboards(Map.copyOf(skillEntries),
                toEntries(leaderboards.powerLevels()));
    }

    @FunctionalInterface
    interface LeaderboardDataSource {
        @NotNull LeaderboardSnapshot readLeaderboards(int perScopeLimit);
    }

    private record LeaderboardEntry(@NotNull String playerName, @NotNull String value) {
    }

    private record CachedLeaderboards(
            @NotNull Map<PrimarySkillType, List<LeaderboardEntry>> skillEntries,
            @NotNull List<LeaderboardEntry> overallEntries) {
        private static @NotNull CachedLeaderboards empty() {
            return new CachedLeaderboards(Map.of(), List.of());
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
            if (position < 1) {
                return null;
            }

            final List<LeaderboardEntry> entries =
                    (skill == null) ? overallEntries : skillEntries.get(skill);
            if (entries == null) {
                return null;
            }

            // Entry lists are truncated to the configured snapshot depth at build time, so this
            // bounds check also caps lookups past the deepest tracked rank.
            final int index = position - 1;
            if (index >= entries.size()) {
                return null;
            }

            return entries.get(index);
        }
    }
}
