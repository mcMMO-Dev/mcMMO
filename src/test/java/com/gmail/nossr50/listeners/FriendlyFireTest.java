package com.gmail.nossr50.listeners;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Contract coverage for the pure friendly-fire decision. The outcome drives both the damage
 * path (anything except PROCESS skips skill processing) and the combust path (only SELF and
 * CANCEL_FRIENDLY_FIRE cancel), so every gate ordering here is load-bearing.
 */
class FriendlyFireTest {

    @ParameterizedTest
    @CsvSource({
            // samePlayer, partyEnabled, dataLoaded, ffAllowed, samePartyOrAllied, bothPerms, expected
            "true,  false, false, false, false, false, SELF",
            "true,  true,  true,  false, true,  false, SELF",
            "false, false, false, false, false, false, PROCESS",
            "false, true,  false, false, true,  false, DATA_NOT_LOADED",
            "false, true,  true,  true,  true,  false, PROCESS",
            "false, true,  true,  false, false, false, PROCESS",
            "false, true,  true,  false, true,  true,  PROCESS",
            "false, true,  true,  false, true,  false, CANCEL_FRIENDLY_FIRE",
    })
    void resolveShouldMatchTheFriendlyFireContract(boolean samePlayer, boolean partyEnabled,
            boolean dataLoaded, boolean ffAllowed, boolean samePartyOrAllied, boolean bothPerms,
            FriendlyFire.Outcome expected) {
        // Given - a combination of friendly-fire inputs
        // When - the outcome is resolved
        final FriendlyFire.Outcome outcome = FriendlyFire.resolve(samePlayer, partyEnabled,
                () -> dataLoaded, () -> ffAllowed, () -> samePartyOrAllied, () -> bothPerms);

        // Then - it matches the contract
        assertThat(outcome).isEqualTo(expected);
    }
}
