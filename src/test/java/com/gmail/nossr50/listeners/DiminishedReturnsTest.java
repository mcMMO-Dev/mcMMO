package com.gmail.nossr50.listeners;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Contract coverage for the diminished-returns XP math. Gains under the (modified) threshold
 * pass untouched, gains over it shrink proportionally, the configured cap guarantees a minimum
 * fraction, and with no cap a deep overshoot cancels the gain entirely.
 */
class DiminishedReturnsTest {

    @ParameterizedTest
    @CsvSource({
            // registeredXp, threshold, modifier, global, cap, expectChanged, expectedXp
            "500,  1000, 1.0, 1.0, 0,    false, 0",   // under threshold - untouched
            "1000, 1000, 1.0, 1.0, 0,    false, 0",   // exactly at threshold - untouched
            "1500, 1000, 1.0, 1.0, 0,    true,  50",  // 50% over - halved
            "1900, 1000, 1.0, 1.0, 0.05, true,  10",  // reduced value above the minimum - kept
            "1990, 1000, 1.0, 1.0, 0.05, true,  5",   // reduced value below the minimum - clamped
            "3000, 1000, 1.0, 1.0, 0.05, true,  5",   // overshoot with a cap - minimum, not cancel
            "750,  1000, 2.0, 1.0, 0,    true,  50",  // skill modifier halves the threshold
            "750,  1000, 1.0, 0.5, 0,    true,  50",  // global multiplier halves the threshold
    })
    void applyShouldMatchTheDiminishedReturnsContract(float registeredXp, int threshold,
            double modifier, double global, float cap, boolean expectChanged, float expectedXp) {
        // Given - a 100 XP gain against the configured diminished-returns state
        // When - the formula is applied
        final DiminishedReturns.Result result = DiminishedReturns.apply(100F, registeredXp,
                threshold, modifier, global, cap);

        // Then - the outcome matches the contract
        assertThat(result.cancelled()).isFalse();
        assertThat(result.changed()).isEqualTo(expectChanged);
        if (expectChanged) {
            assertThat(result.rawXp()).isEqualTo(expectedXp, within(0.01F));
        }
    }

    @Test
    void applyShouldCancelTheGainWhenFullyDiminishedWithNoGuaranteedMinimum() {
        // Given - registered XP at triple the threshold and no guaranteed minimum configured
        // When - the formula is applied to a 100 XP gain
        final DiminishedReturns.Result result =
                DiminishedReturns.apply(100F, 3000F, 1000, 1.0, 1.0, 0F);

        // Then - the gain is cancelled outright
        assertThat(result.cancelled()).isTrue();
        assertThat(result.changed()).isFalse();
    }
}
