package com.gmail.nossr50.runnables;

import com.gmail.nossr50.datatypes.meta.HealthbarSnapshot;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.MobHealthbarUtils;
import java.util.function.LongSupplier;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class MobHealthDisplayUpdaterTask extends CancellableRunnable {

    /**
     * Polling interval after the initial display window elapses, in ticks.
     * Balances responsiveness against redundant checks.
     */
    public static final int POLL_INTERVAL_TICKS = 5;

    /**
     * Number of consecutive polls with an unchanged {@code lastHitMs} before the task forcibly
     * restores and exits. At 5 ticks/poll (250 ms each) this is 25 seconds of no-hit activity.
     *
     * <p>This is a semantic failsafe rather than a raw count: the counter resets whenever a
     * re-hit updates {@code lastHitMs} in the snapshot, so a legitimately long boss fight cannot
     * trigger early cancellation as long as the mob keeps being hit. Only truly idle tasks — where
     * nothing is updating the snapshot but the elapsed check has not yet fired — will hit this
     * limit.
     */
    static final int STALE_POLL_LIMIT = 100;

    private final @NotNull LivingEntity target;
    private final long displayTimeMs;
    private final @NotNull LongSupplier timeSource;
    private int stalePollCount = 0;
    private long lastObservedLastHitMs = Long.MIN_VALUE;

    public MobHealthDisplayUpdaterTask(@NotNull LivingEntity target, long displayTimeMs) {
        this(target, displayTimeMs, System::currentTimeMillis);
    }

    /** Package-private — allows unit tests to inject a controllable time source. */
    MobHealthDisplayUpdaterTask(@NotNull LivingEntity target, long displayTimeMs,
            @NotNull LongSupplier timeSource) {
        this.target = target;
        this.displayTimeMs = displayTimeMs;
        this.timeSource = timeSource;
    }

    @Override
    public void run() {
        // Entity left the world — entity-removal cleanup handles metadata via
        // TransientMetadataTools. No restore needed here.
        if (!target.isValid()) {
            this.cancel();
            return;
        }

        // Metadata already removed — another code path (lethal damage handler, entity cleanup)
        // already restored the name. Nothing to do.
        final HealthbarSnapshot snapshot = MobHealthbarUtils.getHealthbarSnapshot(target);
        if (snapshot == null) {
            this.cancel();
            return;
        }

        // Primary exit: display time elapsed since the most recent hit.
        final long elapsed = timeSource.getAsLong() - snapshot.lastHitMs();
        if (elapsed >= displayTimeMs) {
            MobHealthbarUtils.restoreNameFromSnapshot(target);
            this.cancel();
            return;
        }

        // Stale-activity failsafe: track whether lastHitMs has changed since the previous poll.
        // A change means the mob was re-hit — reset the counter. No change means idle — increment.
        // After STALE_POLL_LIMIT consecutive idle polls the task has overstayed its welcome:
        // restore and exit. This is strictly a fallback for configs with unusually long display
        // times where the elapsed check alone would take many minutes to fire.
        if (snapshot.lastHitMs() == lastObservedLastHitMs) {
            stalePollCount++;
        } else {
            stalePollCount = 0;
            lastObservedLastHitMs = snapshot.lastHitMs();
        }

        if (stalePollCount >= STALE_POLL_LIMIT) {
            MobHealthbarUtils.restoreNameFromSnapshot(target);
            this.cancel();
        }
    }
}
