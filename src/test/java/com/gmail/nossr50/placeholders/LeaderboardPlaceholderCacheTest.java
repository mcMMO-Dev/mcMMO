package com.gmail.nossr50.placeholders;

import static org.assertj.core.api.Assertions.assertThat;

import com.gmail.nossr50.datatypes.database.LeaderboardSnapshot;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

class LeaderboardPlaceholderCacheTest {
    @Test
    void refreshNowShouldPerformOneBulkReadWithConfiguredTopCount() {
        // Given - a recording data source and a cache with max rank 5.
        RecordingDataSource dataSource = new RecordingDataSource();
        dataSource.overall = List.of(new PlayerStat("overall", 999));
        dataSource.mining = List.of(new PlayerStat("miner", 123));

        LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(5, dataSource,
                Logger.getAnonymousLogger());

        // When - performing a refresh.
        assertThat(cache.refreshNow()).isTrue();

        // Then - all scopes should be read in exactly one bulk call limited to the top 5 rows.
        assertThat(dataSource.perScopeLimits).containsExactly(5);
    }

    @Test
    void shouldReturnLeaderboardRowsByExactPosition() {
        // Given - a cache snapshot with deterministic mining and overall entries.
        RecordingDataSource dataSource = new RecordingDataSource();
        dataSource.overall = List.of(
                new PlayerStat("alpha", 4000),
                new PlayerStat("beta", 3000),
                new PlayerStat("gamma", 2000)
        );
        dataSource.mining = List.of(
                new PlayerStat("minerOne", 500),
                new PlayerStat("minerTwo", 450),
                new PlayerStat("minerThree", 430)
        );

        // When - refreshing and reading specific leaderboard positions.
        LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(10, dataSource,
                Logger.getAnonymousLogger());
        cache.refreshNow();

        // Then - the name/value lookups should return the exact rank rows.
        assertThat(cache.getPlayerName(PrimarySkillType.MINING, 2)).isEqualTo("minerTwo");
        assertThat(cache.getValue(PrimarySkillType.MINING, 2)).isEqualTo("450");
        assertThat(cache.getPlayerName(null, 3)).isEqualTo("gamma");
        assertThat(cache.getValue(null, 3)).isEqualTo("2000");
    }

    @Test
    void shouldReturnEmptyForInvalidOrOutOfBoundsPosition() {
        // Given - a snapshot with only one overall entry and max rank 3.
        RecordingDataSource dataSource = new RecordingDataSource();
        dataSource.overall = List.of(new PlayerStat("alpha", 1000));

        // When - requesting invalid or out-of-bounds positions.
        LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(3, dataSource,
                Logger.getAnonymousLogger());
        cache.refreshNow();

        // Then - lookups should return empty strings.
        assertThat(cache.getPlayerName(null, 0)).isEmpty();
        assertThat(cache.getPlayerName(null, -1)).isEmpty();
        assertThat(cache.getPlayerName(null, 4)).isEmpty();
        assertThat(cache.getPlayerName(null, 2)).isEmpty();
        assertThat(cache.getValue(null, 2)).isEmpty();
    }

    /**
     * Gotcha coverage: a data source returning more rows than the requested per-scope limit
     * must not deepen the snapshot — extra rows are truncated at build time so lookups past
     * the configured max tracked rank stay empty.
     */
    @Test
    void refreshNowShouldTruncateEntriesAtMaxTrackedRankWhenDataSourceReturnsExtraRows() {
        // Given - a cache tracking 3 ranks whose data source returns 5 rows per scope.
        LeaderboardPlaceholderCache.LeaderboardDataSource dataSource = perScopeLimit ->
                new LeaderboardSnapshot(
                        Map.of(PrimarySkillType.MINING, List.of(
                                new PlayerStat("minerOne", 500),
                                new PlayerStat("minerTwo", 400),
                                new PlayerStat("minerThree", 300),
                                new PlayerStat("minerFour", 200),
                                new PlayerStat("minerFive", 100)
                        )),
                        List.of(
                                new PlayerStat("alpha", 5000),
                                new PlayerStat("beta", 4000),
                                new PlayerStat("gamma", 3000),
                                new PlayerStat("delta", 2000),
                                new PlayerStat("epsilon", 1000)
                        )
                );

        LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(3, dataSource,
                Logger.getAnonymousLogger());

        // When - refreshing from the over-long results.
        assertThat(cache.refreshNow()).isTrue();

        // Then - the deepest tracked rank still resolves to the truncated last entry.
        assertThat(cache.getPlayerName(PrimarySkillType.MINING, 3)).isEqualTo("minerThree");
        assertThat(cache.getValue(PrimarySkillType.MINING, 3)).isEqualTo("300");
        assertThat(cache.getPlayerName(null, 3)).isEqualTo("gamma");
        assertThat(cache.getValue(null, 3)).isEqualTo("3000");

        // And - positions past the tracked depth stay empty despite the extra source rows.
        assertThat(cache.getPlayerName(PrimarySkillType.MINING, 4)).isEmpty();
        assertThat(cache.getValue(PrimarySkillType.MINING, 4)).isEmpty();
        assertThat(cache.getPlayerName(null, 4)).isEmpty();
        assertThat(cache.getValue(null, 5)).isEmpty();
    }

    @Test
    void shouldKeepLastGoodSnapshotWhenRefreshFails() {
        // Given - a data source that succeeds once and then fails.
        AtomicBoolean failRefresh = new AtomicBoolean(false);
        LeaderboardPlaceholderCache.LeaderboardDataSource dataSource = perScopeLimit -> {
            if (failRefresh.get()) {
                throw new RuntimeException("simulated failure");
            }

            return new LeaderboardSnapshot(Map.of(), List.of(new PlayerStat("stable", 1000)));
        };

        // When - the first refresh succeeds.
        LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(5, dataSource,
                Logger.getAnonymousLogger());
        assertThat(cache.refreshNow()).isTrue();
        assertThat(cache.getPlayerName(null, 1)).isEqualTo("stable");
        assertThat(cache.getValue(null, 1)).isEqualTo("1000");

        // And - a later refresh fails.
        failRefresh.set(true);
        assertThat(cache.refreshNow()).isFalse();

        // Then - the cache should keep serving the last good snapshot.
        assertThat(cache.getPlayerName(null, 1)).isEqualTo("stable");
        assertThat(cache.getValue(null, 1)).isEqualTo("1000");
    }

    /**
     * Idle servers must not pay for leaderboard refreshes nobody reads: when no placeholder was
     * resolved since the previous refresh, the next refresh skips its backend read entirely.
     */
    @Test
    void refreshNowShouldSkipBackendReadWhenNoLookupsHappenedSinceLastRefresh() {
        // Given - a cache that has completed its warm-up refresh.
        RecordingDataSource dataSource = new RecordingDataSource();
        dataSource.overall = List.of(new PlayerStat("sleeper", 1000));

        LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(5, dataSource,
                Logger.getAnonymousLogger());
        assertThat(cache.refreshNow()).isTrue();

        // When - the next periodic refresh fires without any placeholder lookups in between.
        boolean refreshed = cache.refreshNow();

        // Then - the refresh is skipped and the backend is not read again.
        assertThat(refreshed).isFalse();
        assertThat(dataSource.perScopeLimits).hasSize(1);

        // And - the last snapshot still serves lookups.
        assertThat(cache.getPlayerName(null, 1)).isEqualTo("sleeper");
    }

    /**
     * Gotcha coverage: idleness must not become permanent — a lookup after an idle skip has to
     * bring the periodic refresh back to life.
     */
    @Test
    void refreshNowShouldResumeBackendReadsWhenLookupHappensAfterIdleSkip() {
        // Given - a cache whose periodic refresh was skipped for idleness.
        RecordingDataSource dataSource = new RecordingDataSource();
        dataSource.overall = List.of(new PlayerStat("awakener", 1000));

        LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(5, dataSource,
                Logger.getAnonymousLogger());
        assertThat(cache.refreshNow()).isTrue();
        assertThat(cache.refreshNow()).isFalse();
        assertThat(dataSource.perScopeLimits).hasSize(1);

        // When - a placeholder lookup happens and the next periodic refresh fires.
        cache.getPlayerName(null, 1);
        boolean refreshed = cache.refreshNow();

        // Then - the refresh performs a backend read again.
        assertThat(refreshed).isTrue();
        assertThat(dataSource.perScopeLimits).hasSize(2);
    }

    /**
     * A cache that has been shut down must stop touching its data source entirely: a refresh
     * firing during plugin disable would otherwise race database shutdown.
     */
    @Test
    void refreshNowShouldNotReadDataSourceWhenCacheIsShutDown() {
        // Given - a cache with one good snapshot that is then shut down.
        RecordingDataSource dataSource = new RecordingDataSource();
        dataSource.overall = List.of(new PlayerStat("survivor", 1000));

        LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(5, dataSource,
                Logger.getAnonymousLogger());
        assertThat(cache.refreshNow()).isTrue();
        int readsBeforeShutdown = dataSource.perScopeLimits.size();

        // When - shutting down and attempting another refresh.
        cache.shutdown();
        boolean refreshed = cache.refreshNow();

        // Then - the refresh is rejected and no further data source reads happen.
        assertThat(refreshed).isFalse();
        assertThat(dataSource.perScopeLimits).hasSize(readsBeforeShutdown);

        // And - the existing snapshot still serves lookups until the plugin fully disables.
        assertThat(cache.getPlayerName(null, 1)).isEqualTo("survivor");
    }

    private static final class RecordingDataSource implements
            LeaderboardPlaceholderCache.LeaderboardDataSource {
        private final List<Integer> perScopeLimits = new CopyOnWriteArrayList<>();
        private List<PlayerStat> overall = List.of();
        private List<PlayerStat> mining = List.of();

        @Override
        public LeaderboardSnapshot readLeaderboards(int perScopeLimit) {
            perScopeLimits.add(perScopeLimit);
            return new LeaderboardSnapshot(Map.of(PrimarySkillType.MINING, mining), overall);
        }
    }
}
