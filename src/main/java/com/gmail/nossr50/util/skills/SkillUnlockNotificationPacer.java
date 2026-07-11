package com.gmail.nossr50.util.skills;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

/**
 * Paces skill unlock notifications into a quick steady stream per player. Every notification
 * is delivered; each player has a rolling schedule of slots spaced
 * {@link #SLOT_SPACING_MILLIS} apart, so a burst that unlocks many ranks at once (like
 * /mmoedit all) arrives as an even drip over a few seconds instead of dumping in one tick or
 * dripping out for minutes.
 * <p>
 * Thread safety: regionized servers process level ups for different players on different
 * threads. Slot reservations go through {@link ConcurrentHashMap#compute}, which is atomic
 * per player. The stale-entry sweep runs at most once per sweep window behind a CAS gate,
 * and eviction is conditional on the observed value so it cannot clobber a concurrent
 * reservation.
 */
public final class SkillUnlockNotificationPacer {
    @VisibleForTesting
    static final long SLOT_SPACING_MILLIS = 250;
    @VisibleForTesting
    static final long SWEEP_INTERVAL_MILLIS = 10_000;
    private static final long MILLIS_PER_TICK = 50;

    private static final ConcurrentHashMap<UUID, Long> LAST_SLOT_BY_PLAYER =
            new ConcurrentHashMap<>();
    private static final AtomicLong LAST_SWEEP_TIME = new AtomicLong();

    private SkillUnlockNotificationPacer() {
    }

    /**
     * Reserves the next free notification slot for a player.
     *
     * @param playerId the player receiving the notification
     * @return the delay in ticks until the reserved slot
     */
    public static long reserveSlotDelayTicks(@NotNull UUID playerId) {
        return reserveSlotDelayTicks(playerId, System.currentTimeMillis());
    }

    @VisibleForTesting
    static long reserveSlotDelayTicks(@NotNull UUID playerId, long now) {
        sweepStaleEntries(now);

        final Long reservedSlot = LAST_SLOT_BY_PLAYER.compute(playerId,
                (id, lastSlot) -> (lastSlot == null || lastSlot + SLOT_SPACING_MILLIS <= now)
                        ? now
                        : lastSlot + SLOT_SPACING_MILLIS);

        return (reservedSlot - now) / MILLIS_PER_TICK;
    }

    /**
     * Drops players whose schedule has fully drained. Entries are only read while their last
     * slot still influences the next reservation, so anything older than one spacing window
     * is dead weight.
     */
    private static void sweepStaleEntries(long now) {
        final long lastSweepTime = LAST_SWEEP_TIME.get();
        if (now - lastSweepTime < SWEEP_INTERVAL_MILLIS
                || !LAST_SWEEP_TIME.compareAndSet(lastSweepTime, now)) {
            return;
        }

        LAST_SLOT_BY_PLAYER.values().removeIf(lastSlot -> lastSlot + SLOT_SPACING_MILLIS <= now);
    }

    @VisibleForTesting
    static int trackedPlayerCount() {
        return LAST_SLOT_BY_PLAYER.size();
    }

    @VisibleForTesting
    static void clearAll() {
        LAST_SLOT_BY_PLAYER.clear();
        LAST_SWEEP_TIME.set(0);
    }
}
