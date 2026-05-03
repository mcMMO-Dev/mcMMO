package com.gmail.nossr50.skills.smelting;

import static java.util.logging.Logger.getLogger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.skills.RankUtils;
import java.util.stream.Stream;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

class SmeltingManagerTest extends MMOTestEnvironment {
    private static final java.util.logging.Logger logger = getLogger(
            SmeltingManagerTest.class.getName());

    private SmeltingManager smeltingManager;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        smeltingManager = new SmeltingManager(mmoPlayer);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @ParameterizedTest(name = "rank={0}, configuredMultiplier={1} -> {2}")
    @MethodSource("vanillaXpMultiplierCases")
    void getVanillaXpMultiplierShouldUseConfiguredSmeltingMultiplier(
            int understandingTheArtRank,
            int configuredSmeltingVanillaXpMultiplier,
            int expectedVanillaXpMultiplier) {
        // Given
        final int rankLookupResult = understandingTheArtRank;
        final int effectiveRankForConfigLookup = Math.max(1, understandingTheArtRank);

        Mockito.when(RankUtils.getRank(any(Player.class),
                Mockito.eq(SubSkillType.SMELTING_UNDERSTANDING_THE_ART))).thenReturn(
                rankLookupResult);
        Mockito.when(advancedConfig.getSmeltingVanillaXPModifier(effectiveRankForConfigLookup))
                .thenReturn(configuredSmeltingVanillaXpMultiplier);

        // When
        final int actualVanillaXpMultiplier = smeltingManager.getVanillaXpMultiplier();

        // Then
        assertEquals(expectedVanillaXpMultiplier, actualVanillaXpMultiplier);
    }

    @ParameterizedTest(name = "experience={0}, rank={1}, configuredMultiplier={2} -> {3}")
    @MethodSource("vanillaXpBoostCases")
    void vanillaXpBoostShouldApplyConfiguredMultiplier(
            int vanillaExperienceFromFurnaceEvent,
            int understandingTheArtRank,
            int configuredSmeltingVanillaXpMultiplier,
            int expectedBoostedVanillaExperience) {
        /*
         * Intent:
         * This test verifies end-to-end multiplier application for vanilla smelting XP.
         * It protects against two regression classes:
         * 1) Returning raw rank instead of configured Smelting multiplier values.
         * 2) Skipping the rank floor behavior that guarantees rank >= 1 for config lookup.
         */

        // Given
        final int rankLookupResult = understandingTheArtRank;
        final int effectiveRankForConfigLookup = Math.max(1, understandingTheArtRank);

        Mockito.when(RankUtils.getRank(any(Player.class),
                Mockito.eq(SubSkillType.SMELTING_UNDERSTANDING_THE_ART))).thenReturn(
                rankLookupResult);
        Mockito.when(advancedConfig.getSmeltingVanillaXPModifier(effectiveRankForConfigLookup))
                .thenReturn(configuredSmeltingVanillaXpMultiplier);

        // When
        final int actualBoostedVanillaExperience = smeltingManager.vanillaXPBoost(
                vanillaExperienceFromFurnaceEvent);

        // Then
        assertEquals(expectedBoostedVanillaExperience, actualBoostedVanillaExperience);
    }

    private static Stream<Arguments> vanillaXpMultiplierCases() {
        return Stream.of(
                Arguments.of(1, 1, 1),
                Arguments.of(2, 2, 2),
                Arguments.of(5, 4, 4),
                Arguments.of(8, 5, 5),
                Arguments.of(0, 1, 1),
                Arguments.of(3, 0, 1)
        );
    }

    private static Stream<Arguments> vanillaXpBoostCases() {
        return Stream.of(
                Arguments.of(5, 1, 1, 5),
                Arguments.of(5, 2, 2, 10),
                Arguments.of(7, 5, 4, 28),
                Arguments.of(6, 8, 5, 30),
                Arguments.of(4, 0, 1, 4)
        );
    }
}