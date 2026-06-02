package com.gmail.nossr50.datatypes.experience;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Server-side cache that keeps {@link DiminishedReturnsState} alive across a player's
 * disconnect/reconnect cycle, preventing players from resetting their DR window by logging out.
 *
 * <p>Entries are keyed by player UUID. When the DR time window elapses the entry becomes
 * eligible for removal; call {@link #evictExpired()} periodically (e.g. from
 * {@link com.gmail.nossr50.runnables.player.ClearRegisteredXPGainTask}) to release stale entries
 * and prevent unbounded cache growth.</p>
 *
 * <p>All public methods are safe to call from multiple threads.</p>
 */
public final class DiminishedReturnsCache {

    private static final ConcurrentHashMap<UUID, DiminishedReturnsState> cache =
            new ConcurrentHashMap<>();

    private DiminishedReturnsCache() {}

    /**
     * Returns the existing {@link DiminishedReturnsState} for {@code uuid} if it still has active
     * DR entries, or atomically replaces/creates a fresh one.
     *
     * <p>Passing {@code null} (offline or legacy profiles without a UUID) always returns a fresh,
     * uncached state — those profiles never participate in the disconnect/reconnect bypass.</p>
     *
     * @param uuid the player's unique identifier, or {@code null} for uncached profiles
     * @return a non-null {@link DiminishedReturnsState} for this player
     */
    public static @NotNull DiminishedReturnsState getOrCreate(@Nullable final UUID uuid) {
        if (uuid == null || !ExperienceConfig.getInstance().getDiminishedReturnsEnabled()) {
            return new DiminishedReturnsState();
        }
        return cache.compute(uuid, (k, existing) -> {
            // Keep the existing instance if it is either still active OR fresh (never used).
            // A fresh existing state means the player's PlayerProfile (which holds a strong
            // reference to it) has not yet gained any XP — replacing it now would orphan
            // that reference and break the disconnect/reconnect bypass protection on the
            // next XP gain.
            if (existing != null && !existing.isEvictable()) {
                return existing;
            }
            return new DiminishedReturnsState();
        });
    }

    /**
     * Removes all cache entries whose DR time window has fully elapsed. Fresh entries that
     * have never registered XP are preserved — see {@link DiminishedReturnsState#isEvictable()}.
     * Safe to call from any thread; intended for use in the periodic cleanup task.
     */
    public static void evictExpired() {
        cache.values().removeIf(DiminishedReturnsState::isEvictable);
    }

    /** Removes the cache entry for {@code uuid}. For test teardown only. */
    static void remove(@Nullable final UUID uuid) {
        if (uuid != null) {
            cache.remove(uuid);
        }
    }

    /** Clears every entry from the cache. For test teardown only. */
    static void clearAll() {
        cache.clear();
    }

    /** Returns the number of entries currently held in the cache. For test assertions only. */
    static int size() {
        return cache.size();
    }

    /** Returns {@code true} if the cache contains an entry for {@code uuid}. For test assertions only. */
    static boolean contains(@NotNull final UUID uuid) {
        return cache.containsKey(uuid);
    }
}
