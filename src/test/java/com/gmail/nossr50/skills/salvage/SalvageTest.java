package com.gmail.nossr50.skills.salvage;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Covers {@link Salvage#calculateSalvageableAmount(int, short, int)}. The yield deliberately
 * scales with remaining durability and floors to zero: full materials only come back from a
 * fully repaired item. Guaranteeing a minimum yield for damaged items would let players
 * re-craft a brand-new full-durability item from a nearly broken one.
 */
class SalvageTest extends MMOTestEnvironment {
    private static final java.util.logging.Logger logger = getLogger(SalvageTest.class.getName());

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @ParameterizedTest(name = "damage={0}/{1}, maxQuantity={2}")
    @MethodSource("yieldFlooredToZeroCases")
    void salvageableAmountShouldBeZeroWhenDamageFloorsYieldBelowOneMaterial(int damage,
            int maxDurability, int maxQuantity) {
        // Given - a damaged item whose proportional yield is less than one whole material
        // When - the salvageable amount is calculated
        final int yield = Salvage.calculateSalvageableAmount(damage, (short) maxDurability,
                maxQuantity);

        // Then - nothing is salvageable; a guaranteed material would allow re-crafting a
        // brand-new item from a nearly broken one
        assertThat(yield).isZero();
    }

    @ParameterizedTest(name = "damage={0}/{1}, maxQuantity={2} -> {3}")
    @MethodSource("proportionalYieldCases")
    void salvageableAmountShouldScaleWithRemainingDurability(int damage, int maxDurability,
            int maxQuantity, int expectedYield) {
        // Given - a salvageable item with a known damage value and configured maximum quantity
        // When - the salvageable amount is calculated
        final int yield = Salvage.calculateSalvageableAmount(damage, (short) maxDurability,
                maxQuantity);

        // Then - the yield scales down with lost durability, full yield only at full durability
        assertThat(yield).isEqualTo(expectedYield);
    }

    @ParameterizedTest(name = "damage={0}/{1}, maxQuantity={2}")
    @MethodSource("fullyBrokenCases")
    void salvageableAmountShouldBeZeroWhenItemHasNoDurabilityLeft(int damage, int maxDurability,
            int maxQuantity) {
        // Given - an item with no durability remaining
        // When - the salvageable amount is calculated
        final int yield = Salvage.calculateSalvageableAmount(damage, (short) maxDurability,
                maxQuantity);

        // Then - nothing is salvageable, so the "too damaged" failure path still applies
        assertThat(yield).isZero();
    }

    // Rows where damage scales the proportional yield below one material, including any damage
    // at all on MaximumQuantity 1 items such as wooden shovels
    private static Stream<Arguments> yieldFlooredToZeroCases() {
        return Stream.of(
                Arguments.of(1, 59, 1),
                Arguments.of(30, 59, 1),
                Arguments.of(58, 59, 1),
                Arguments.of(249, 250, 2),
                Arguments.of(2030, 2031, 3)
        );
    }

    // Rows covering the proportional floor, including the full yield at full durability
    private static Stream<Arguments> proportionalYieldCases() {
        return Stream.of(
                Arguments.of(0, 59, 1, 1),
                Arguments.of(0, 250, 2, 2),
                Arguments.of(125, 250, 2, 1),
                Arguments.of(125, 250, 3, 1),
                Arguments.of(62, 250, 4, 3),
                Arguments.of(0, 0, 2, 2),
                Arguments.of(0, -1, 5, 5)
        );
    }

    // Rows where the item is completely broken and salvage should fail outright
    private static Stream<Arguments> fullyBrokenCases() {
        return Stream.of(
                Arguments.of(59, 59, 1),
                Arguments.of(250, 250, 2),
                Arguments.of(300, 250, 4)
        );
    }
}
