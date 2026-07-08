package com.gmail.nossr50.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ChimaeraWingTest {

    /**
     * Regression coverage for the underground use penalty: the damage roll used the player's
     * health minus 10 as an exclusive random bound, which crashes for any player at or below
     * 10.9 health (5.5 hearts). The roll must never crash and never deal negative damage.
     */
    @ParameterizedTest
    @ValueSource(doubles = {0.5, 1.0, 5.0, 10.0, 10.9, 11.0, 15.5, 20.0})
    void undergroundPenaltyRollShouldNeverCrashRegardlessOfHealth(double health) {
        // Given - a player health value, including values at or below the 10 HP threshold
        // When - the underground penalty damage is rolled
        // Then - the roll does not throw and stays within the intended range
        assertThatCode(() -> {
            final int damage = ChimaeraWing.rollUndergroundPenaltyDamage(health);
            assertThat(damage).isGreaterThanOrEqualTo(0);
            assertThat(damage).isLessThan(Math.max(1, (int) (health - 10)) + 1);
        }).doesNotThrowAnyException();
    }
}
