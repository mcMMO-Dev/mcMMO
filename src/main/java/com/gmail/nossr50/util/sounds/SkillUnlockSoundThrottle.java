package com.gmail.nossr50.util.sounds;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

/**
 * Decides whether a sub-skill unlock notification may play the unlock sound. Mass level
 * changes (for example /mmoedit all) queue long streams of unlock notifications; only the
 * first notification of such a stream plays the sound, and the stream keeps its own quiet
 * window open for as long as notifications keep arriving.
 * <p>
 * The quiet window is anchored on the last notification, not the last played sound, so a
 * batch staggered five seconds apart suppresses itself end to end. The window is wall clock
 * time and slightly longer than the scheduler stagger so mild server lag cannot break a
 * batch apart.
 * <p>
 * Entries whose quiet window has passed are swept out opportunistically, at most once per
 * window. Thread safe: on Folia, notification tasks run on region threads.
 */
public final class SkillUnlockSoundThrottle {
    @VisibleForTesting
    static final long QUIET_WINDOW_MILLIS = 10_000;

    private static final ConcurrentHashMap<UUID, Long> LAST_NOTIFICATION_BY_PLAYER =
            new ConcurrentHashMap<>();
    private static final AtomicLong LAST_SWEEP_TIME = new AtomicLong();

    private SkillUnlockSoundThrottle() {
    }

    /**
     * Records an unlock notification for the given player and reports whether it may play
     * the unlock sound.
     *
     * @param playerId the player receiving the notification
     * @return true when no other unlock notification arrived within the quiet window
     */
    public static boolean tryPlaySound(@NotNull UUID playerId) {
        return tryPlaySound(playerId, System.currentTimeMillis());
    }

    @VisibleForTesting
    static boolean tryPlaySound(@NotNull UUID playerId, long now) {
        final Long lastNotificationTime = LAST_NOTIFICATION_BY_PLAYER.put(playerId, now);
        sweepStaleEntries(now);
        return lastNotificationTime == null
                || now - lastNotificationTime >= QUIET_WINDOW_MILLIS;
    }

    /**
     * Removes entries whose quiet window has passed, at most once per window. Fresh entries
     * are always kept; there is no size-based eviction.
     */
    private static void sweepStaleEntries(long now) {
        final long lastSweepTime = LAST_SWEEP_TIME.get();
        if (now - lastSweepTime < QUIET_WINDOW_MILLIS
                || !LAST_SWEEP_TIME.compareAndSet(lastSweepTime, now)) {
            return;
        }

        LAST_NOTIFICATION_BY_PLAYER.values()
                .removeIf(lastTime -> now - lastTime >= QUIET_WINDOW_MILLIS);
    }

    @VisibleForTesting
    static int trackedPlayerCount() {
        return LAST_NOTIFICATION_BY_PLAYER.size();
    }

    @VisibleForTesting
    public static void clearAll() {
        LAST_NOTIFICATION_BY_PLAYER.clear();
        LAST_SWEEP_TIME.set(0);
    }
}
