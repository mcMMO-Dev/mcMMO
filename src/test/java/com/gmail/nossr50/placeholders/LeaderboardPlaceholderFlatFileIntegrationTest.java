package com.gmail.nossr50.placeholders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.database.FlatFileDatabaseManager;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.TestFileCleanup;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class LeaderboardPlaceholderFlatFileIntegrationTest {
    private static final long PURGE_TIME = 2_630_000_000L;
    private static final @NotNull Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static MockedStatic<ExperienceConfig> mockedExperienceConfig;

    private File tempDir;

    @BeforeAll
    static void setUpClass() {
        ExperienceConfig experienceConfig = mock(ExperienceConfig.class);
        when(experienceConfig.getDiminishedReturnsEnabled()).thenReturn(false);
        mockedExperienceConfig = Mockito.mockStatic(ExperienceConfig.class);
        mockedExperienceConfig.when(ExperienceConfig::getInstance).thenReturn(experienceConfig);
    }

    @AfterAll
    static void tearDownClass() {
        if (mockedExperienceConfig != null) {
            mockedExperienceConfig.close();
        }
    }

    @AfterEach
    void tearDown() {
        if (tempDir != null) {
            TestFileCleanup.deleteRecursively(tempDir);
        }
    }

    @Test
    void placeholdersShouldResolveExpectedRanksWhenCacheRefreshesFromFlatFileData() throws Exception {
        // Given - a pseudo-real flatfile backend seeded with ranked users.
        FlatFileDatabaseManager databaseManager = createFlatFileManager();

        seedUserWithMiningLevel(databaseManager, "flat_leader", 300);
        seedUserWithMiningLevel(databaseManager, "flat_runner_up", 200);
        seedUserWithMiningLevel(databaseManager, "flat_third", 100);

        // When - refreshing the cache and resolving mining/overall placeholders.
        LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(3,
                databaseManager::readLeaderboardSnapshot, LOGGER);
        assertThat(cache.refreshNow()).isTrue();

        McTopPlaceholder miningName = McTopPlaceholder.name(PrimarySkillType.MINING, cache);
        McTopPlaceholder miningValue = McTopPlaceholder.value(PrimarySkillType.MINING, cache);
        McTopPlaceholder overallName = McTopPlaceholder.name(null, cache);
        McTopPlaceholder overallValue = McTopPlaceholder.value(null, cache);

        // Then - each placeholder should resolve the expected rank entry.
        assertThat(miningName.process(null, "1")).isEqualTo("flat_leader");
        assertThat(miningValue.process(null, "1")).isEqualTo("300");
        assertThat(miningName.process(null, "2")).isEqualTo("flat_runner_up");
        assertThat(miningValue.process(null, "2")).isEqualTo("200");
        assertThat(overallName.process(null, "3")).isEqualTo("flat_third");
        assertThat(overallValue.process(null, "3")).isEqualTo("100");
    }

    /**
     * Gotcha coverage: all overall aliases must stay behaviorally identical for the same position.
     */
    @ParameterizedTest(name = "{0} at position {1} should match overall")
    @MethodSource("aliasAndPositionProvider")
    void aliasPlaceholdersShouldMatchOverallWhenUsingFlatFileData(String aliasToken, String position)
            throws Exception {
        // Given - a pseudo-real flatfile backend and alias token under test.
        FlatFileDatabaseManager databaseManager = createFlatFileManager();

        seedUserWithMiningLevel(databaseManager, "alias_top", 500);
        seedUserWithMiningLevel(databaseManager, "alias_mid", 400);
        seedUserWithMiningLevel(databaseManager, "alias_low", 300);

        // When - resolving both canonical overall and alias placeholders for the same position.
        LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(3,
                databaseManager::readLeaderboardSnapshot, LOGGER);
        assertThat(cache.refreshNow()).isTrue();

        McTopPlaceholder canonicalName = McTopPlaceholder.name(null, cache);
        McTopPlaceholder canonicalValue = McTopPlaceholder.value(null, cache);
        McTopPlaceholder aliasName = McTopPlaceholder.name(null, aliasToken, cache);
        McTopPlaceholder aliasValue = McTopPlaceholder.value(null, aliasToken, cache);

        // Then - alias output should match canonical overall output.
        assertThat(aliasName.process(null, position)).isEqualTo(canonicalName.process(null, position));
        assertThat(aliasValue.process(null, position)).isEqualTo(canonicalValue.process(null, position));
    }

    @ParameterizedTest(name = "invalid position \"{0}\" should return empty")
    @MethodSource("invalidPositionProvider")
    void placeholdersShouldReturnEmptyWhenPositionInputIsInvalid(String position) throws Exception {
        // Given - a valid cached leaderboard snapshot.
        FlatFileDatabaseManager databaseManager = createFlatFileManager();

        seedUserWithMiningLevel(databaseManager, "invalid_case_user", 250);

        // When - resolving a placeholder with an invalid position value.
        LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(3,
                databaseManager::readLeaderboardSnapshot, LOGGER);
        assertThat(cache.refreshNow()).isTrue();

        // Then - resolution should return an empty result.
        McTopPlaceholder placeholder = McTopPlaceholder.name(PrimarySkillType.MINING, cache);
        assertThat(placeholder.process(null, position)).isEmpty();
    }

    @Test
    void cacheShouldNotPerformLeaderboardReadsWhenPlaceholderRequestsAreServedFromSnapshot()
            throws Exception {
        // Given - a cache that records backend leaderboard reads.
        FlatFileDatabaseManager databaseManager = createFlatFileManager();

        seedUserWithMiningLevel(databaseManager, "snapshot_top", 700);
        seedUserWithMiningLevel(databaseManager, "snapshot_low", 600);

        AtomicInteger readCount = new AtomicInteger(0);
        LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(5, perScopeLimit -> {
            readCount.incrementAndGet();
            return databaseManager.readLeaderboardSnapshot(perScopeLimit);
        }, LOGGER);

        // When - refreshing once and then resolving multiple placeholders.
        assertThat(cache.refreshNow()).isTrue();
        int readsAfterRefresh = readCount.get();
        // A refresh performs exactly one bulk backend read covering all scopes.
        assertThat(readsAfterRefresh).isEqualTo(1);

        McTopPlaceholder namePlaceholder = McTopPlaceholder.name(PrimarySkillType.MINING, cache);
        McTopPlaceholder valuePlaceholder = McTopPlaceholder.value(PrimarySkillType.MINING, cache);

        // Then - all post-refresh placeholder requests should be served without new backend reads.
        assertThat(namePlaceholder.process(null, "1")).isEqualTo("snapshot_top");
        assertThat(valuePlaceholder.process(null, "1")).isEqualTo("700");
        assertThat(namePlaceholder.process(null, "2")).isEqualTo("snapshot_low");
        assertThat(valuePlaceholder.process(null, "2")).isEqualTo("600");

        assertThat(readCount.get()).isEqualTo(readsAfterRefresh);
    }

    @Test
    void refreshNowShouldAllowOnlyOneInFlightRefreshWhenCallsOverlap() throws Exception {
        // Given - a refresh data source that blocks the first refresh call.
        FlatFileDatabaseManager databaseManager = createFlatFileManager();
        seedUserWithMiningLevel(databaseManager, "single_flight_user", 900);

        AtomicBoolean firstCall = new AtomicBoolean(true);
        CountDownLatch enteredRefresh = new CountDownLatch(1);
        CountDownLatch releaseRefresh = new CountDownLatch(1);

        LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(3, perScopeLimit -> {
            if (firstCall.compareAndSet(true, false)) {
                enteredRefresh.countDown();
                try {
                    boolean released = releaseRefresh.await(5, TimeUnit.SECONDS);
                    assertThat(released).isTrue();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while coordinating test refresh",
                            exception);
                }
            }
            return databaseManager.readLeaderboardSnapshot(perScopeLimit);
        }, LOGGER);

        // When - a second refresh is attempted while the first refresh is still running.
        AtomicBoolean firstResult = new AtomicBoolean(false);
        Thread firstRefresh = new Thread(() -> firstResult.set(cache.refreshNow()));
        firstRefresh.start();

        // Then - the overlapping refresh attempt should be rejected and the original refresh should complete.
        assertThat(enteredRefresh.await(5, TimeUnit.SECONDS)).isTrue();
        // A lookup keeps the cache active so the overlap is rejected by the single-flight
        // guard, not the idle skip.
        cache.getPlayerName(PrimarySkillType.MINING, 1);
        assertThat(cache.refreshNow()).isFalse();

        releaseRefresh.countDown();
        firstRefresh.join(5_000L);

        assertThat(firstResult.get()).isTrue();
    }

    private static Stream<Arguments> aliasAndPositionProvider() {
        return Stream.of(
                Arguments.of("overall", "1"),
                Arguments.of("all", "1"),
                Arguments.of("powerlevel", "1"),
                Arguments.of("overall", "2"),
                Arguments.of("all", "2"),
                Arguments.of("powerlevel", "3")
        );
    }

    private static Stream<String> invalidPositionProvider() {
        return Stream.of(null, "", " ", "0", "-5", "abc", "9999");
    }

    private FlatFileDatabaseManager createFlatFileManager() throws Exception {
        tempDir = Files.createTempDirectory("mcmmo-flatfile-placeholder-it-").toFile();
        File usersFile = new File(tempDir, "users.db");

        Constructor<FlatFileDatabaseManager> constructor = FlatFileDatabaseManager.class
                .getDeclaredConstructor(File.class, Logger.class, long.class, int.class,
                        boolean.class);
        constructor.setAccessible(true);

        return constructor.newInstance(usersFile, LOGGER, PURGE_TIME, 0, true);
    }

    private static void seedUserWithMiningLevel(FlatFileDatabaseManager databaseManager,
            String playerName, int miningLevel) {
        UUID uuid = UUID.randomUUID();
        databaseManager.newUser(playerName, uuid);
        PlayerProfile profile = databaseManager.loadPlayerProfile(uuid);
        profile.modifySkill(PrimarySkillType.MINING, miningLevel);
        assertThat(databaseManager.saveUser(profile)).isTrue();
    }
}
