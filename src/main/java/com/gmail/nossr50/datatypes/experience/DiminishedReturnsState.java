package com.gmail.nossr50.datatypes.experience;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import org.jetbrains.annotations.NotNull;

/**
 * Thread-safe container for a player's Diminished Returns tracking data.
 *
 * <p>Stored in {@link DiminishedReturnsCache} keyed by player UUID so that the DR window
 * survives a disconnect/reconnect cycle. Each skill's rolling XP total and the expiry
 * queue are held here rather than directly in {@code PlayerProfile}.</p>
 */
public final class DiminishedReturnsState {

    private final DelayQueue<SkillXpGain> gainedSkillsXp = new DelayQueue<>();
    private final ConcurrentHashMap<PrimarySkillType, Float> rollingSkillsXp = new ConcurrentHashMap<>();

    /**
     * The epoch-millisecond timestamp of the last-expiring {@link SkillXpGain} that has been
     * registered. When the current time exceeds this value every entry in the queue has expired
     * and this state can safely be evicted from the cache without losing any DR data.
     *
     * <p>{@code volatile} ensures the write from one thread (e.g. the main thread registering XP)
     * is visible to the scheduler thread that runs {@link DiminishedReturnsCache#evictExpired()}.
     */
    private volatile long latestExpiryTimeMillis = 0L;

    // Package-private: only DiminishedReturnsCache (same package) and PlayerProfile
    // (via DiminishedReturnsCache.getOrCreate) should instantiate this class.
    DiminishedReturnsState() {}

    /**
     * Returns {@code true} if at least one DR entry is still within its time window.
     *
     * <p>This is intentionally distinct from {@link #isEvictable()}: a freshly constructed state
     * that has never registered any XP has no active entries but is also <em>not</em> evictable,
     * because its owning {@code PlayerProfile} still holds a reference and may yet register XP
     * on it. Removing it from the cache prematurely would break the disconnect/reconnect
     * bypass protection.</p>
     */
    public boolean hasActiveEntries() {
        return latestExpiryTimeMillis != 0L && System.currentTimeMillis() < latestExpiryTimeMillis;
    }

    /**
     * Returns {@code true} if this state has held DR entries and they have all expired.
     * Fresh states (no XP ever registered) are never evictable.
     */
    public boolean isEvictable() {
        return latestExpiryTimeMillis != 0L && System.currentTimeMillis() >= latestExpiryTimeMillis;
    }

    /**
     * Returns the rolling XP total recorded for the given skill within the current DR window.
     * Returns {@code 0} if no XP has been registered or all entries have expired.
     *
     * @param skill the skill to query
     * @return total registered XP for the skill, or {@code 0}
     */
    public float getRegisteredXpGain(@NotNull final PrimarySkillType skill) {
        return rollingSkillsXp.getOrDefault(skill, 0F);
    }

    /**
     * Records an XP gain for DR tracking and advances the latest-expiry ceiling accordingly.
     *
     * @param skill the skill that gained XP
     * @param xp   the amount of XP gained
     */
    public void registerXpGain(@NotNull final PrimarySkillType skill, final float xp) {
        final SkillXpGain gain = new SkillXpGain(skill, xp);
        gainedSkillsXp.add(gain);
        rollingSkillsXp.merge(skill, xp, Float::sum);

        // Advance the expiry ceiling so we know the exact moment all entries will have expired.
        final long entryExpiry = gain.getExpiryTimeMillis();
        if (entryExpiry > latestExpiryTimeMillis) {
            latestExpiryTimeMillis = entryExpiry;
        }
    }

    /**
     * Polls the delay queue for expired entries and decrements the rolling skill totals.
     * Called periodically by
     * {@link com.gmail.nossr50.runnables.player.ClearRegisteredXPGainTask}.
     */
    public void purgeExpiredXpGains() {
        SkillXpGain gain;
        while ((gain = gainedSkillsXp.poll()) != null) {
            final PrimarySkillType skill = gain.getSkill();
            final float expiredXp = gain.getXp();
            rollingSkillsXp.compute(skill, (k, existing) -> {
                if (existing == null) {
                    return null;
                }
                final float updated = existing - expiredXp;
                return updated <= 0F ? null : updated;
            });
        }
    }
}
