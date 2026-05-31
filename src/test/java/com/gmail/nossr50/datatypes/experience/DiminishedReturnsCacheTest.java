package com.gmail.nossr50.datatypes.experience;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import java.lang.reflect.Field;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/**
 * Unit tests for {@link DiminishedReturnsCache}.
 *
 * <p>These tests verify the cache's UUID-keyed persistence semantics and eviction logic.
 * {@link ExperienceConfig} is mocked so tests do not require a running Minecraft server.
 * By default DR is enabled; individual tests that need DR disabled override that mock.
 */
class DiminishedReturnsCacheTest {

    private MockedStatic<ExperienceConfig> mockedExperienceConfig;
    private ExperienceConfig experienceConfigMock;

    @BeforeEach
    void setupMocks() {
        experienceConfigMock = mock(ExperienceConfig.class);
        mockedExperienceConfig = mockStatic(ExperienceConfig.class);
        mockedExperienceConfig.when(ExperienceConfig::getInstance).thenReturn(experienceConfigMock);
        // Default: DR is enabled so UUID-based caching is exercised
        when(experienceConfigMock.getDiminishedReturnsEnabled()).thenReturn(true);
    }

    @AfterEach
    void teardown() {
        mockedExperienceConfig.close();
        DiminishedReturnsCache.clearAll();
    }

    /**
     * Uses reflection to force the {@code latestExpiryTimeMillis} field to an arbitrary value.
     * This lets tests drive the {@code hasActiveEntries()} return value without waiting for real
     * time to elapse or calling into ExperienceConfig.
     */
    private static void setLatestExpiry(final DiminishedReturnsState state, final long epochMillis)
            throws Exception {
        final Field field = DiminishedReturnsState.class.getDeclaredField("latestExpiryTimeMillis");
        field.setAccessible(true);
        field.setLong(state, epochMillis);
    }

    @Nested
    class DisabledBehavior {

        @Test
        void returnsUncachedStateWhenDrDisabled() {
            // Given - DR is disabled in config
            when(experienceConfigMock.getDiminishedReturnsEnabled()).thenReturn(false);
            final UUID playerUuid = UUID.randomUUID();

            // When - two getOrCreate calls for the same UUID
            final DiminishedReturnsState firstState = DiminishedReturnsCache.getOrCreate(playerUuid);
            final DiminishedReturnsState secondState = DiminishedReturnsCache.getOrCreate(playerUuid);

            // Then - a fresh uncached state is returned each time; no bypass protection needed
            assertNotSame(firstState, secondState,
                    "DR disabled: states must not be cached across calls");
            assertEquals(0, DiminishedReturnsCache.size(),
                    "DR disabled: cache must remain empty");
        }

        @Test
        void cacheRemainsEmptyWhenDrDisabled() {
            // Given - DR is disabled
            when(experienceConfigMock.getDiminishedReturnsEnabled()).thenReturn(false);

            // When - several players look up their DR state
            DiminishedReturnsCache.getOrCreate(UUID.randomUUID());
            DiminishedReturnsCache.getOrCreate(UUID.randomUUID());
            DiminishedReturnsCache.getOrCreate(UUID.randomUUID());

            // Then - nothing is stored
            assertEquals(0, DiminishedReturnsCache.size(),
                    "DR disabled: cache must never grow");
        }
    }

    @Nested
    class NullUuidBehavior {

        @Test
        void alwaysReturnsFreshInstance() {
            // Given - two calls with null (offline / legacy profile)
            // When
            final DiminishedReturnsState firstState = DiminishedReturnsCache.getOrCreate(null);
            final DiminishedReturnsState secondState = DiminishedReturnsCache.getOrCreate(null);

            // Then - every call returns a distinct, uncached object
            assertNotNull(firstState);
            assertNotNull(secondState);
            assertNotSame(firstState, secondState,
                    "null-UUID calls must never share a cached state");
        }

        @Test
        void doesNotGrowCache() {
            // Given - several calls with null
            // When
            DiminishedReturnsCache.getOrCreate(null);
            DiminishedReturnsCache.getOrCreate(null);
            DiminishedReturnsCache.getOrCreate(null);

            // Then - cache should be empty (null profiles are never stored)
            assertEquals(0, DiminishedReturnsCache.size(),
                    "null-UUID states must not be stored in the cache");
        }
    }

    @Nested
    class SameUuidPersistence {

        @Test
        void returnsSameInstanceForSameUuidWhileActive() throws Exception {
            // Given - a UUID with an active DR state (expiry set far in the future)
            final UUID playerUuid = UUID.randomUUID();
            final DiminishedReturnsState firstState = DiminishedReturnsCache.getOrCreate(playerUuid);
            setLatestExpiry(firstState, System.currentTimeMillis() + 600_000L); // 10 min ahead

            // When - a second lookup with the same UUID (simulates reconnect while DR is active)
            final DiminishedReturnsState secondState = DiminishedReturnsCache.getOrCreate(playerUuid);

            // Then - identical instance returned, so DR window is preserved
            assertSame(firstState, secondState,
                    "active DR state must be returned as-is on reconnect to prevent bypass");
        }

        @Test
        void returnsFreshInstanceWhenPreviousStateHasExpired() throws Exception {
            // Given - a UUID whose cached state has fully elapsed (latestExpiry in the past, non-zero)
            final UUID playerUuid = UUID.randomUUID();
            final DiminishedReturnsState expiredState = DiminishedReturnsCache.getOrCreate(playerUuid);
            // 1L = had XP at some point AND that XP has now expired
            setLatestExpiry(expiredState, 1L);

            // When - player reconnects after their DR window has elapsed
            final DiminishedReturnsState freshState = DiminishedReturnsCache.getOrCreate(playerUuid);

            // Then - a new state is created; no stale XP data carries over
            assertNotSame(expiredState, freshState,
                    "expired state should be replaced with a fresh one on next getOrCreate");
        }
    }

    @Nested
    class UuidIsolation {

        @Test
        void differentUuidsGetIndependentStates() throws Exception {
            // Given - two distinct players
            final UUID uuidAlpha = UUID.randomUUID();
            final UUID uuidBeta = UUID.randomUUID();

            // When - both look up their DR state (mark both active so cache keeps them)
            final DiminishedReturnsState stateAlpha = DiminishedReturnsCache.getOrCreate(uuidAlpha);
            final DiminishedReturnsState stateBeta = DiminishedReturnsCache.getOrCreate(uuidBeta);
            setLatestExpiry(stateAlpha, System.currentTimeMillis() + 600_000L);
            setLatestExpiry(stateBeta, System.currentTimeMillis() + 600_000L);

            // Then - they receive independent state containers
            assertNotSame(stateAlpha, stateBeta,
                    "each player UUID must have its own isolated DR state");
        }
    }

    @Nested
    class Eviction {

        @Test
        void evictExpiredRemovesStateWithExpiredEntries() throws Exception {
            // Given - a player whose DR state has expired (latestExpiry in the past, non-zero)
            final UUID playerUuid = UUID.randomUUID();
            final DiminishedReturnsState expiredState = DiminishedReturnsCache.getOrCreate(playerUuid);
            // 1L = "had XP registered" (non-zero) AND "already expired" (< currentTimeMillis)
            setLatestExpiry(expiredState, 1L);
            assertTrue(DiminishedReturnsCache.contains(playerUuid),
                    "state should be in cache before eviction");

            // When
            DiminishedReturnsCache.evictExpired();

            // Then
            assertFalse(DiminishedReturnsCache.contains(playerUuid),
                    "expired state should be removed by evictExpired");
            assertEquals(0, DiminishedReturnsCache.size());
        }

        @Test
        void evictExpiredPreservesFreshStateThatNeverRegisteredXp() {
            // Regression: a player who joined but has not yet gained any XP must keep their cached
            // state. Otherwise the cache entry is evicted, then once XP is registered on the
            // PlayerProfile's orphaned reference, a disconnect/reconnect creates a new state and
            // bypasses the DR window.
            // Given - a fresh state (latestExpiryTimeMillis defaults to 0)
            final UUID playerUuid = UUID.randomUUID();
            DiminishedReturnsCache.getOrCreate(playerUuid);
            assertTrue(DiminishedReturnsCache.contains(playerUuid));

            // When
            DiminishedReturnsCache.evictExpired();

            // Then - fresh state must NOT be evicted
            assertTrue(DiminishedReturnsCache.contains(playerUuid),
                    "fresh state with no registered XP must survive eviction "
                            + "to preserve reconnect bypass protection");
        }

        @Test
        void evictExpiredPreservesStateWithActiveEntries() throws Exception {
            // Given - a player with an active DR state
            final UUID playerUuid = UUID.randomUUID();
            final DiminishedReturnsState activeState = DiminishedReturnsCache.getOrCreate(playerUuid);
            setLatestExpiry(activeState, System.currentTimeMillis() + 600_000L); // active
            assertTrue(activeState.hasActiveEntries());

            // When
            DiminishedReturnsCache.evictExpired();

            // Then - active entry must survive the sweep
            assertTrue(DiminishedReturnsCache.contains(playerUuid),
                    "active DR state must not be evicted");
            assertEquals(1, DiminishedReturnsCache.size());
        }

        @Test
        void evictExpiredOnlyRemovesExpiredEntries() throws Exception {
            // Given - one active player and one whose DR window has elapsed
            final UUID activePlayerUuid = UUID.randomUUID();
            final UUID expiredPlayerUuid = UUID.randomUUID();

            final DiminishedReturnsState activeState = DiminishedReturnsCache.getOrCreate(activePlayerUuid);
            final DiminishedReturnsState expiredState = DiminishedReturnsCache.getOrCreate(expiredPlayerUuid);

            setLatestExpiry(activeState, System.currentTimeMillis() + 600_000L);
            setLatestExpiry(expiredState, 1L);

            // When
            DiminishedReturnsCache.evictExpired();

            // Then - only the expired entry is removed
            assertTrue(DiminishedReturnsCache.contains(activePlayerUuid),
                    "active player state must survive eviction");
            assertFalse(DiminishedReturnsCache.contains(expiredPlayerUuid),
                    "expired player state must be removed");
            assertEquals(1, DiminishedReturnsCache.size());
        }
    }

    @Nested
    class HasActiveEntries {

        @Test
        void freshStateHasNoActiveEntries() {
            // Given - a freshly constructed state (latestExpiryTimeMillis defaults to 0)
            final DiminishedReturnsState freshState = new DiminishedReturnsState();

            // When / Then
            assertFalse(freshState.hasActiveEntries(),
                    "a newly created state with no XP registrations should report no active entries");
        }

        @Test
        void stateWithFutureExpiryHasActiveEntries() throws Exception {
            // Given - a state whose expiry ceiling has been pushed into the future
            final DiminishedReturnsState state = new DiminishedReturnsState();
            setLatestExpiry(state, System.currentTimeMillis() + 600_000L);

            // When / Then
            assertTrue(state.hasActiveEntries(),
                    "state with a future latestExpiryTimeMillis should report active entries");
        }
    }
}
