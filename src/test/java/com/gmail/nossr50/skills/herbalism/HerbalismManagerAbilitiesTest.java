package com.gmail.nossr50.skills.herbalism;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
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
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Covers the Herbalism abilities not exercised by the Green Thumb consumption and
 * multi-block XP suites: ability gates, Farmer's Diet, sweet berry bush harvesting, plant
 * maturity rules, and the bonus drop marking that decides double versus triple drops.
 */
class HerbalismManagerAbilitiesTest extends MMOTestEnvironment {
    private static final Logger logger =
            getLogger(HerbalismManagerAbilitiesTest.class.getName());

    private HerbalismManager herbalismManager;
    private PlatformScheduler scheduler;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);

        final FoliaLib foliaLib = mock(FoliaLib.class);
        scheduler = mock(PlatformScheduler.class);
        when(mcMMO.p.getFoliaLib()).thenReturn(foliaLib);
        when(foliaLib.getScheduler()).thenReturn(scheduler);

        herbalismManager = Mockito.spy(new HerbalismManager(mmoPlayer));
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    private Ageable mockAgeable(Material material, int age, int maximumAge) {
        final Ageable ageable = mock(Ageable.class);
        when(ageable.getMaterial()).thenReturn(material);
        when(ageable.getAge()).thenReturn(age);
        when(ageable.getMaximumAge()).thenReturn(maximumAge);
        return ageable;
    }

    @Nested
    class AbilityGates {
        @Test
        void greenThumbBlocksShouldRequireSeedsInHand() {
            try (MockedStatic<BlockUtils> blockUtils = mockStatic(BlockUtils.class)) {
                // Given - Green Thumb unlocked, a mossy-able block, and seeds in hand
                when(RankUtils.hasUnlockedSubskill(player, SubSkillType.HERBALISM_GREEN_THUMB))
                        .thenReturn(true);
                final BlockState cobblestone = mock(BlockState.class);
                when(cobblestone.getBlock()).thenReturn(mock(Block.class));
                when(cobblestone.getType()).thenReturn(Material.COBBLESTONE);
                blockUtils.when(() -> BlockUtils.canMakeMossy(any(Block.class)))
                        .thenReturn(true);
                when(Permissions.greenThumbBlock(eq(player), any(Material.class)))
                        .thenReturn(true);

                final ItemStack seeds = mock(ItemStack.class);
                when(seeds.getType()).thenReturn(Material.WHEAT_SEEDS);
                when(seeds.getAmount()).thenReturn(3);
                when(playerInventory.getItemInMainHand()).thenReturn(seeds);

                // When / Then - seeds allow the conversion, anything else does not
                assertThat(herbalismManager.canGreenThumbBlock(cobblestone)).isTrue();
                when(seeds.getType()).thenReturn(Material.STICK);
                assertThat(herbalismManager.canGreenThumbBlock(cobblestone)).isFalse();
            }
        }

        @Test
        void shroomThumbShouldRequireBothMushroomTypes() {
            try (MockedStatic<BlockUtils> blockUtils = mockStatic(BlockUtils.class)) {
                // Given - Shroom Thumb unlocked against a shroomy-able block, holding a
                // brown mushroom
                when(RankUtils.hasUnlockedSubskill(player,
                        SubSkillType.HERBALISM_SHROOM_THUMB)).thenReturn(true);
                final BlockState dirt = mock(BlockState.class);
                blockUtils.when(() -> BlockUtils.canMakeShroomy(dirt)).thenReturn(true);

                final ItemStack brownMushroom = mock(ItemStack.class);
                when(brownMushroom.getType()).thenReturn(Material.BROWN_MUSHROOM);
                when(playerInventory.getItemInMainHand()).thenReturn(brownMushroom);
                when(playerInventory.contains(Material.BROWN_MUSHROOM, 1)).thenReturn(true);
                when(playerInventory.contains(Material.RED_MUSHROOM, 1)).thenReturn(true);

                // When / Then - both mushroom types in the inventory are required
                assertThat(herbalismManager.canUseShroomThumb(dirt)).isTrue();
                when(playerInventory.contains(Material.RED_MUSHROOM, 1)).thenReturn(false);
                assertThat(herbalismManager.canUseShroomThumb(dirt)).isFalse();
            }
        }

        @Test
        void hylianLuckShouldRequireItsUnlock() {
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.HERBALISM_HYLIAN_LUCK))
                    .thenReturn(false);
            assertThat(herbalismManager.canUseHylianLuck()).isFalse();

            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.HERBALISM_HYLIAN_LUCK))
                    .thenReturn(true);
            assertThat(herbalismManager.canUseHylianLuck()).isTrue();
        }

        @Test
        void abilityActivationShouldRequireAReadiedHoe() {
            when(Permissions.greenTerra(player)).thenReturn(true);

            doReturn(false).when(mmoPlayer).getToolPreparationMode(ToolType.HOE);
            assertThat(herbalismManager.canActivateAbility()).isFalse();

            doReturn(true).when(mmoPlayer).getToolPreparationMode(ToolType.HOE);
            assertThat(herbalismManager.canActivateAbility()).isTrue();
        }
    }

    @Test
    void farmersDietShouldDelegateToTheSharedFoodSkill() {
        try (MockedStatic<SkillUtils> skillUtils = mockStatic(SkillUtils.class)) {
            // Given - the shared food skill handler boosts the hunger restored
            skillUtils.when(() -> SkillUtils.handleFoodSkills(player, 4,
                    SubSkillType.HERBALISM_FARMERS_DIET)).thenReturn(6);

            // When / Then - Farmer's Diet returns the boosted value
            assertThat(herbalismManager.farmersDiet(4)).isEqualTo(6);
        }
    }

    @Nested
    class BerryBushHarvesting {
        private BlockState bushState;
        private Block bushBlock;

        @BeforeEach
        void setUpBush() {
            bushBlock = mock(Block.class);
            bushState = mock(BlockState.class);
            when(bushState.getType()).thenReturn(Material.SWEET_BERRY_BUSH);
            when(bushState.getBlock()).thenReturn(bushBlock);
            when(bushState.getLocation()).thenReturn(new Location(world, 0, 64, 0));
            when(ExperienceConfig.getInstance().getXp(PrimarySkillType.HERBALISM,
                    Material.SWEET_BERRY_BUSH)).thenReturn(50);
            doNothing().when(herbalismManager).applyXpGain(anyFloat(), any(), any());
        }

        private void wireBushAge(int age) {
            final Ageable bushAge = mockAgeable(Material.SWEET_BERRY_BUSH, age, 3);
            when(bushState.getBlockData()).thenReturn(bushAge);
        }

        private CancellableRunnable scheduledBushCheck() {
            final ArgumentCaptor<CancellableRunnable> bushCheck =
                    ArgumentCaptor.forClass(CancellableRunnable.class);
            verify(scheduler).runAtLocationLater(any(Location.class), bushCheck.capture(),
                    anyLong());
            return bushCheck.getValue();
        }

        /**
         * The berry bush pays XP one tick later, only if the harvest actually reset the
         * bush's age (a cancelled harvest leaves the age unchanged).
         */
        @Test
        void fullyGrownBushShouldPayDoubleXpAfterTheHarvestIsVerified() {
            // Given - a fully grown bush (age 3, double XP)
            wireBushAge(3);
            herbalismManager.processBerryBushHarvesting(bushState);

            // When - the scheduled verification finds the bush reset to age 0
            when(bushBlock.getState()).thenReturn(bushState);
            wireBushAge(0);
            scheduledBushCheck().run();

            // Then - double XP lands
            verify(herbalismManager).applyXpGain(100f, XPGainReason.PVE, XPGainSource.SELF);
        }

        @Test
        void harvestableBushShouldPayNormalXp() {
            // Given - a harvestable bush (age 2, normal XP)
            wireBushAge(2);
            herbalismManager.processBerryBushHarvesting(bushState);

            // When - the verification finds the harvest went through
            when(bushBlock.getState()).thenReturn(bushState);
            wireBushAge(1);
            scheduledBushCheck().run();

            // Then - the base XP lands
            verify(herbalismManager).applyXpGain(50f, XPGainReason.PVE, XPGainSource.SELF);
        }

        @Test
        void youngBushShouldPayNothing() {
            // Given - a bush too young to drop berries
            wireBushAge(1);

            // When - the harvest processes
            herbalismManager.processBerryBushHarvesting(bushState);

            // Then - no verification is even scheduled
            verify(scheduler, never()).runAtLocationLater(any(Location.class),
                    any(CancellableRunnable.class), anyLong());
        }

        @Test
        void cancelledHarvestShouldPayNothing() {
            // Given - a fully grown bush whose harvest gets cancelled
            wireBushAge(3);
            herbalismManager.processBerryBushHarvesting(bushState);

            // When - the verification finds the bush still fully grown
            when(bushBlock.getState()).thenReturn(bushState);
            scheduledBushCheck().run();

            // Then - no XP lands
            verify(herbalismManager, never()).applyXpGain(anyFloat(), any(), any());
        }
    }

    @Nested
    class PlantMaturity {
        @Test
        void matureCropsShouldBeDetected() {
            assertThat(herbalismManager.isAgeableMature(
                    mockAgeable(Material.WHEAT, 7, 7))).isTrue();
            assertThat(herbalismManager.isAgeableMature(
                    mockAgeable(Material.WHEAT, 3, 7))).isFalse();
            assertThat(herbalismManager.isAgeableMature(
                    mockAgeable(Material.WHEAT, 0, 0))).isFalse();
        }

        @Test
        void berryBushesShouldCountAsMatureFromAgeTwo() {
            assertThat(herbalismManager.isAgeableMature(
                    mockAgeable(Material.SWEET_BERRY_BUSH, 2, 3))).isTrue();
            assertThat(herbalismManager.isAgeableMature(
                    mockAgeable(Material.SWEET_BERRY_BUSH, 1, 3))).isFalse();
        }

        /**
         * Cactus, kelp, sugar cane, and bamboo report ages that say nothing about growth, so
         * their age can never gate Herbalism rewards.
         */
        @ParameterizedTest
        @EnumSource(names = {"CACTUS", "KELP", "SUGAR_CANE", "BAMBOO"})
        void untrustworthyAgeablesShouldBeBizarre(Material material) {
            assertThat(herbalismManager.isBizarreAgeable(mockAgeable(material, 0, 15)))
                    .isTrue();
        }

        @Test
        void ordinaryCropsShouldNotBeBizarre() {
            assertThat(herbalismManager.isBizarreAgeable(mockAgeable(Material.WHEAT, 3, 7)))
                    .isFalse();
            assertThat(herbalismManager.isBizarreAgeable(mock(BlockData.class))).isFalse();
        }
    }

    @Nested
    class BonusDropMarking {
        private Block plant;

        @BeforeEach
        void setUpPlant() {
            plant = mock(Block.class);
        }

        @Test
        void greenTerraShouldAlwaysMarkTripleDrops() {
            try (MockedStatic<BlockUtils> blockUtils = mockStatic(BlockUtils.class);
                    MockedStatic<ProbabilityUtil> ignored = mockStatic(ProbabilityUtil.class)) {
                // Given - an active Green Terra
                doReturn(true).when(mmoPlayer).getAbilityMode(SuperAbilityType.GREEN_TERRA);

                // When - the plant is marked
                herbalismManager.markForBonusDrops(plant);

                // Then - triple drops
                blockUtils.verify(() -> BlockUtils.markDropsAsBonus(plant, true));
            }
        }

        @Test
        void verdantBountyWinsShouldMarkTripleDrops() {
            try (MockedStatic<BlockUtils> blockUtils = mockStatic(BlockUtils.class);
                    MockedStatic<ProbabilityUtil> probabilityUtil =
                            mockStatic(ProbabilityUtil.class)) {
                // Given - a winning Verdant Bounty roll without Green Terra
                probabilityUtil.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.HERBALISM_VERDANT_BOUNTY, mmoPlayer)).thenReturn(true);

                // When - the plant is marked
                herbalismManager.markForBonusDrops(plant);

                // Then - triple drops
                blockUtils.verify(() -> BlockUtils.markDropsAsBonus(plant, true));
            }
        }

        @Test
        void ordinaryRollsShouldMarkDoubleDrops() {
            try (MockedStatic<BlockUtils> blockUtils = mockStatic(BlockUtils.class);
                    MockedStatic<ProbabilityUtil> ignored = mockStatic(ProbabilityUtil.class)) {
                // Given - no Green Terra and a losing Verdant Bounty roll (mock default)
                // When - the plant is marked
                herbalismManager.markForBonusDrops(plant);

                // Then - double drops
                blockUtils.verify(() -> BlockUtils.markDropsAsBonus(plant, false));
            }
        }
    }

    @Nested
    class DoubleDropChecks {
        private Block plant;
        private BlockState plantState;

        @BeforeEach
        void setUpPlant() {
            plant = mock(Block.class);
            plantState = mock(BlockState.class);
            when(plant.getState()).thenReturn(plantState);
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.HERBALISM_DOUBLE_DROPS))
                    .thenReturn(true);
        }

        @Test
        void naturalMatureCropsShouldRollForBonusDrops() {
            try (MockedStatic<BlockUtils> blockUtils = mockStatic(BlockUtils.class);
                    MockedStatic<ProbabilityUtil> ignored = mockStatic(ProbabilityUtil.class)) {
                // Given - a natural, fully mature wheat crop whose double drop roll wins
                final Ageable matureWheat = mockAgeable(Material.WHEAT, 7, 7);
                when(plantState.getBlockData()).thenReturn(matureWheat);
                blockUtils.when(() -> BlockUtils.checkDoubleDrops(mmoPlayer, plant,
                        SubSkillType.HERBALISM_DOUBLE_DROPS)).thenReturn(true);

                // When - the broken plants are checked
                herbalismManager.checkDoubleDropsOnBrokenPlants(player, List.of(plant));

                // Then - the plant is marked for bonus drops
                blockUtils.verify(() -> BlockUtils.markDropsAsBonus(eq(plant),
                        org.mockito.ArgumentMatchers.anyBoolean()));
            }
        }

        @Test
        void immatureNaturalCropsShouldNotRoll() {
            try (MockedStatic<BlockUtils> blockUtils = mockStatic(BlockUtils.class)) {
                // Given - a natural, half-grown wheat crop
                final Ageable halfGrownWheat = mockAgeable(Material.WHEAT, 3, 7);
                when(plantState.getBlockData()).thenReturn(halfGrownWheat);

                // When - the broken plants are checked
                herbalismManager.checkDoubleDropsOnBrokenPlants(player, List.of(plant));

                // Then - no bonus drops are marked
                blockUtils.verify(() -> BlockUtils.markDropsAsBonus(eq(plant),
                        org.mockito.ArgumentMatchers.anyBoolean()), never());
            }
        }

        @Test
        void playerPlacedMatureCropsShouldMarkWithoutARoll() {
            try (MockedStatic<BlockUtils> blockUtils = mockStatic(BlockUtils.class);
                    MockedStatic<ProbabilityUtil> ignored = mockStatic(ProbabilityUtil.class)) {
                // Given - a player-placed crop that has fully grown back
                when(chunkManager.isIneligible(plant)).thenReturn(true);
                final Ageable regrownWheat = mockAgeable(Material.WHEAT, 7, 7);
                when(plantState.getBlockData()).thenReturn(regrownWheat);

                // When - the broken plants are checked
                herbalismManager.checkDoubleDropsOnBrokenPlants(player, List.of(plant));

                // Then - the regrown crop is marked without consulting the double drop roll
                blockUtils.verify(() -> BlockUtils.markDropsAsBonus(eq(plant),
                        org.mockito.ArgumentMatchers.anyBoolean()));
                blockUtils.verify(() -> BlockUtils.checkDoubleDrops(any(), any(Block.class),
                        any()), never());
            }
        }

        @Test
        void playerPlacedBizarreAgeablesShouldNeverMark() {
            try (MockedStatic<BlockUtils> blockUtils = mockStatic(BlockUtils.class)) {
                // Given - player-placed sugar cane (whose age cannot be trusted)
                when(chunkManager.isIneligible(plant)).thenReturn(true);
                final Ageable sugarCane = mockAgeable(Material.SUGAR_CANE, 15, 15);
                when(plantState.getBlockData()).thenReturn(sugarCane);

                // When - the broken plants are checked
                herbalismManager.checkDoubleDropsOnBrokenPlants(player, List.of(plant));

                // Then - no bonus drops are marked
                blockUtils.verify(() -> BlockUtils.markDropsAsBonus(eq(plant),
                        org.mockito.ArgumentMatchers.anyBoolean()), never());
            }
        }
    }
}
