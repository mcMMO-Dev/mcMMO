package com.gmail.nossr50.config.treasure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link TreasureLoadTally}, which aggregates per-section load counts so that many
 * treasure sections (for example all of the {@code Shake.*} entity sections) are reported in a
 * single startup summary line rather than one line each.
 */
class TreasureLoadTallyTest {

    @Test
    void emptyShouldHaveZeroCounts() {
        // Given - a fresh empty tally
        // When
        final TreasureLoadTally tally = TreasureLoadTally.empty();

        // Then
        assertThat(tally.loaded()).isZero();
        assertThat(tally.incompatible()).isZero();
        assertThat(tally.invalid()).isZero();
        assertThat(tally.attempted()).isZero();
    }

    @Test
    void mergeShouldSumEachCategory() {
        // Given - two tallies representing two treasure sections
        final TreasureLoadTally a = new TreasureLoadTally(3, 1, 2);
        final TreasureLoadTally b = new TreasureLoadTally(5, 0, 1);

        // When - they are merged (as the Shake.* sections are merged into one summary)
        final TreasureLoadTally merged = a.merge(b);

        // Then - each category is summed independently
        assertThat(merged.loaded()).isEqualTo(8);
        assertThat(merged.incompatible()).isEqualTo(1);
        assertThat(merged.invalid()).isEqualTo(3);
    }

    @Test
    void attemptedShouldCountLoadedPlusInvalidButNotIncompatible() {
        /*
         * Intent: attempted() drives the "Loaded X of Y" summary. Incompatible entries (material
         * absent in this MC version) are harmless and must be excluded from the attempted total.
         */

        // Given - a tally with loaded, incompatible, and invalid entries
        final TreasureLoadTally tally = new TreasureLoadTally(7, 4, 2);

        // When / Then - only loaded + invalid count toward the attempted total
        assertThat(tally.attempted()).isEqualTo(9);
    }
}
