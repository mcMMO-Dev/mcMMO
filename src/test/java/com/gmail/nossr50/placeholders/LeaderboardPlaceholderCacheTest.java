package com.gmail.nossr50.placeholders;

import static org.assertj.core.api.Assertions.assertThat;

import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

class LeaderboardPlaceholderCacheTest {
    @Test
    void refreshNowShouldReadFirstPageWithConfiguredTopCount() {
        // Given - a recording data source and a cache with max rank 5.
        RecordingDataSource dataSource = new RecordingDataSource();
        dataSource.overall = List.of(new PlayerStat("overall", 999));
        dataSource.mining = List.of(new PlayerStat("miner", 123));

        LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(5, dataSource,
                Logger.getAnonymousLogger());

        // When - performing a refresh.
        assertThat(cache.refreshNow()).isTrue();

        // Then - each leaderboard read should request page 1 with statsPerPage 5.
        assertThat(dataSource.calls).isNotEmpty();
        assertThat(dataSource.calls)
                .allSatisfy(call -> {
                    assertThat(call.pageNumber()).isEqualTo(1);
                    assertThat(call.statsPerPage()).isEqualTo(5);
                });
        assertThat(dataSource.calls).anySatisfy(call -> assertThat(call.skill()).isNull());
        assertThat(dataSource.calls).anySatisfy(
                call -> assertThat(call.skill()).isEqualTo(PrimarySkillType.MINING));
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

    @Test
    void shouldKeepLastGoodSnapshotWhenRefreshFails() {
        // Given - a data source that succeeds once and then fails.
        AtomicBoolean failRefresh = new AtomicBoolean(false);
        LeaderboardPlaceholderCache.LeaderboardDataSource dataSource = (skill, pageNumber, statsPerPage) -> {
            if (failRefresh.get()) {
                throw new RuntimeException("simulated failure");
            }

            return List.of(new PlayerStat("stable", 1000));
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

    private static final class RecordingDataSource implements
            LeaderboardPlaceholderCache.LeaderboardDataSource {
        private final List<DataSourceCall> calls = new CopyOnWriteArrayList<>();
        private List<PlayerStat> overall = List.of();
        private List<PlayerStat> mining = List.of();

        @Override
        public @NonNull List<PlayerStat> readLeaderboard(@Nullable PrimarySkillType skill,
                int pageNumber, int statsPerPage)
                throws InvalidSkillException {
            calls.add(new DataSourceCall(skill, pageNumber, statsPerPage));

            if (skill == null) {
                return new ArrayList<>(overall);
            }

            if (skill == PrimarySkillType.MINING) {
                return new ArrayList<>(mining);
            }

            return List.of();
        }
    }

    private record DataSourceCall(@Nullable PrimarySkillType skill, int pageNumber,
                                  int statsPerPage) {
    }
}
