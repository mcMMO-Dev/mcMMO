package com.gmail.nossr50.util.scoreboards;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;

/**
 * Applies pending below-name power level tag refreshes.
 * <p>
 * Keeping every online player's score written matters for display correctness: clients older
 * than Minecraft 26.2 render a below-name objective under players that have no score at all as
 * a default value of 0, so a pending refresh must never be dropped while the player is online.
 */
final class PowerLevelTagUpdater {
    private PowerLevelTagUpdater() {
    }

    /**
     * Writes power levels for every pending player whose profile is loaded and removes those
     * names from the pending set. A name that cannot be resolved yet stays pending while the
     * player is online (their async profile load has not finished) and is discarded once the
     * player is offline. A name marked pending while a pass is running survives to the next
     * pass, even when it is a re-mark of the name currently being processed.
     *
     * <p>Each name is removed from the set before it is resolved. Removing afterwards would
     * erase a concurrent re-mark of the same name (a level-up landing between the resolve and
     * the removal), leaving a stale tag until that player's next level-up.
     *
     * @param pendingPlayerNames names of players whose power level tag needs a refresh
     * @param powerLevelResolver resolves a player's current power level, or null while the
     *                           player's profile has not loaded
     * @param onlineCheck whether the named player is still online
     * @param powerLevelWriter writes a resolved power level to the scoreboard backend
     */
    static void applyPending(final Set<String> pendingPlayerNames,
            final Function<String, Integer> powerLevelResolver, final Predicate<String> onlineCheck,
            final ObjIntConsumer<String> powerLevelWriter) {
        for (final Iterator<String> it = pendingPlayerNames.iterator(); it.hasNext(); ) {
            final String playerName = it.next();
            it.remove();

            final Integer powerLevel = powerLevelResolver.apply(playerName);

            if (powerLevel == null) {
                if (onlineCheck.test(playerName)) {
                    pendingPlayerNames.add(playerName);
                }

                continue;
            }

            powerLevelWriter.accept(playerName, powerLevel);
        }
    }
}
