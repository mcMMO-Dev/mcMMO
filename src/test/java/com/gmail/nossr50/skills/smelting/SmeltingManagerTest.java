package com.gmail.nossr50.skills.smelting;

import static java.util.logging.Logger.getLogger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import java.util.stream.Stream;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
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

    /**
     * The Fuel Efficiency multiplier tiers: ranks 1-3 stretch fuel by x2 to x4; anything
     * else burns at the vanilla rate.
     */
    @ParameterizedTest(name = "rank={0} -> multiplier={1}")
    @CsvSource({
            "0, 1",
            "1, 2",
            "2, 3",
            "3, 4",
    })
    void fuelEfficiencyMultiplierShouldFollowTheRankTiers(int rank, int expectedMultiplier) {
        Mockito.when(RankUtils.getRank(any(Player.class),
                Mockito.eq(SubSkillType.SMELTING_FUEL_EFFICIENCY))).thenReturn(rank);

        assertEquals(expectedMultiplier, smeltingManager.getFuelEfficiencyMultiplier());
    }

    @Nested
    class FuelEfficiency {
        @BeforeEach
        void wireRankTwo() {
            Mockito.when(RankUtils.getRank(any(Player.class),
                    Mockito.eq(SubSkillType.SMELTING_FUEL_EFFICIENCY))).thenReturn(2);
        }

        @Test
        void burnTimeShouldStretchByTheMultiplier() {
            assertEquals(600, smeltingManager.fuelEfficiency(200));
        }

        @Test
        void spentFuelShouldStaySpent() {
            assertEquals(0, smeltingManager.fuelEfficiency(0));
            assertEquals(0, smeltingManager.fuelEfficiency(-5));
        }

        @Test
        void stretchedBurnTimeShouldClampAtShortMax() {
            assertEquals(Short.MAX_VALUE, smeltingManager.fuelEfficiency(30000));
        }
    }

    @Nested
    class SecondSmelt {
        private FurnaceSmeltEvent smeltEvent;
        private Furnace furnace;
        private FurnaceInventory furnaceInventory;
        private ItemStack smeltResult;

        @BeforeEach
        void setUpFurnace() {
            smeltingManager = Mockito.spy(new SmeltingManager(mmoPlayer));
            doNothing().when(smeltingManager).applyXpGain(anyFloat(), any(), any());

            smeltEvent = Mockito.mock(FurnaceSmeltEvent.class);
            final ItemStack source = Mockito.mock(ItemStack.class);
            when(source.getType()).thenReturn(Material.IRON_ORE);
            when(smeltEvent.getSource()).thenReturn(source);
            when(ExperienceConfig.getInstance().getXp(PrimarySkillType.SMELTING,
                    Material.IRON_ORE)).thenReturn(25);

            smeltResult = Mockito.mock(ItemStack.class);
            when(smeltResult.getType()).thenReturn(Material.IRON_INGOT);
            when(smeltResult.getAmount()).thenReturn(1);
            when(smeltEvent.getResult()).thenReturn(smeltResult);
            when(generalConfig.getDoubleDropsEnabled(PrimarySkillType.SMELTING,
                    Material.IRON_INGOT)).thenReturn(true);

            furnace = Mockito.mock(Furnace.class);
            furnaceInventory = Mockito.mock(FurnaceInventory.class);
            when(furnace.getInventory()).thenReturn(furnaceInventory);
        }

        @Test
        void smeltingShouldPayTheConfiguredXp() {
            try (MockedStatic<ProbabilityUtil> ignored = mockStatic(ProbabilityUtil.class)) {
                // When - the smelt processes (double smelt roll fails by default)
                smeltingManager.smeltProcessing(smeltEvent, furnace);

                // Then - the smelted ore pays passive XP
                verify(smeltingManager).applyXpGain(25f, XPGainReason.PVE,
                        XPGainSource.PASSIVE);
            }
        }

        @Test
        void winningSecondSmeltShouldAddOneToTheResult() {
            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - a winning second smelt roll and an empty furnace result slot
                probabilityUtil.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.SMELTING_SECOND_SMELT, mmoPlayer)).thenReturn(true);
                final ItemStack doubledResult = Mockito.mock(ItemStack.class);
                when(smeltResult.clone()).thenReturn(doubledResult);

                // When - the smelt processes
                smeltingManager.smeltProcessing(smeltEvent, furnace);

                // Then - the event result grows by one ingot
                verify(doubledResult).setAmount(2);
                verify(smeltEvent).setResult(doubledResult);
            }
        }

        @Test
        void nearlyFullResultSlotShouldBlockTheSecondSmelt() {
            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - a furnace result slot too full to take an extra ingot safely
                probabilityUtil.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.SMELTING_SECOND_SMELT, mmoPlayer)).thenReturn(true);
                final ItemStack existingResult = Mockito.mock(ItemStack.class);
                when(existingResult.getAmount()).thenReturn(63);
                when(existingResult.getMaxStackSize()).thenReturn(64);
                when(furnaceInventory.getResult()).thenReturn(existingResult);

                // When - the smelt processes
                smeltingManager.smeltProcessing(smeltEvent, furnace);

                // Then - the result is left alone
                verify(smeltEvent, never()).setResult(any(ItemStack.class));
            }
        }

        @Test
        void disabledDoubleDropsForTheResultShouldBlockTheSecondSmelt() {
            try (MockedStatic<ProbabilityUtil> ignored = mockStatic(ProbabilityUtil.class)) {
                // Given - double drops disabled for iron ingots
                when(generalConfig.getDoubleDropsEnabled(PrimarySkillType.SMELTING,
                        Material.IRON_INGOT)).thenReturn(false);

                // When - the smelt processes
                smeltingManager.smeltProcessing(smeltEvent, furnace);

                // Then - the result is left alone
                verify(smeltEvent, never()).setResult(any(ItemStack.class));
            }
        }
    }
}