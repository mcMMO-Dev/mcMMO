package com.gmail.nossr50.util.scoreboards;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Covers the pending power level tag refresh pass that runs on the power level heartbeat.
 * <p>
 * The refresh pass must never lose a pending refresh for an online player: clients older than
 * Minecraft 26.2 render players without a below-name score as 0, so a dropped refresh shows up
 * in game as a power level tag that is stuck at 0.
 */
class PowerLevelTagUpdaterTest {
    private static Set<String> pendingSetOf(final String... names) {
        // Mirrors the runtime collection type used for the dirty power level set
        final Set<String> pending = ConcurrentHashMap.newKeySet();
        pending.addAll(java.util.List.of(names));
        return pending;
    }

    @Test
    void applyPendingShouldWriteResolvedPowerLevelsAndRemoveProcessedNames() {
        // Given - two online players with loaded profiles are pending a tag refresh
        final Set<String> pending = pendingSetOf("Momshroom", "mrfloris");
        final Map<String, Integer> powerLevels = Map.of("Momshroom", 1500, "mrfloris", 42);
        final Map<String, Integer> written = new HashMap<>();

        // When - the pending refreshes are applied
        PowerLevelTagUpdater.applyPending(pending, powerLevels::get, name -> true, written::put);

        // Then - both power levels are written and no refresh remains pending
        assertThat(written).containsExactlyInAnyOrderEntriesOf(powerLevels);
        assertThat(pending).isEmpty();
    }

    /**
     * Gotcha coverage: a player can be marked pending before their profile finishes its async
     * load. The refresh must stay pending until the profile resolves instead of being dropped,
     * otherwise the player's tag is never written and renders as 0 on pre-26.2 clients.
     */
    @ParameterizedTest
    @MethodSource("unresolvedPlayerCases")
    void applyPendingShouldOnlyDropUnresolvedPlayersWhenOffline(final boolean online,
            final boolean expectedStillPending) {
        // Given - a pending player whose profile has not loaded yet
        final Set<String> pending = pendingSetOf("SlowLoader");
        final Map<String, Integer> written = new HashMap<>();

        // When - the pending refreshes are applied while the profile is still unresolved
        PowerLevelTagUpdater.applyPending(pending, name -> null, name -> online, written::put);

        // Then - nothing is written, and the refresh survives only while the player is online
        assertThat(written).isEmpty();
        assertThat(pending.contains("SlowLoader")).isEqualTo(expectedStillPending);
    }

    private static Stream<Arguments> unresolvedPlayerCases() {
        return Stream.of(
                Arguments.of(true, true),
                Arguments.of(false, false));
    }

    /**
     * Gotcha coverage: level-ups can mark players pending while a refresh pass is running. A
     * name added mid-pass must survive to the next pass rather than being wiped wholesale.
     */
    @Test
    void applyPendingShouldKeepNamesMarkedPendingDuringProcessing() {
        // Given - one resolvable pending player, and a level-up that lands mid-pass
        final Set<String> pending = pendingSetOf("Momshroom");
        final Function<String, Integer> resolver = name ->
                "Momshroom".equals(name) ? 1500 : null;

        // When - applying the pass marks another (not yet resolvable) player pending mid-write
        PowerLevelTagUpdater.applyPending(pending, resolver, name -> true,
                (name, powerLevel) -> pending.add("Newcomer"));

        // Then - the mid-pass arrival is still pending for the next pass
        assertThat(pending).containsExactly("Newcomer");
    }
}
