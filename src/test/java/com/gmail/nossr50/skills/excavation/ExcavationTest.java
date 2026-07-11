package com.gmail.nossr50.skills.excavation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
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
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.mockito.MockedStatic;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ExcavationTest extends MMOTestEnvironment {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            ExcavationTest.class.getName());

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        when(rankConfig.getSubSkillUnlockLevel(SubSkillType.EXCAVATION_ARCHAEOLOGY, 1)).thenReturn(
                1);
        when(rankConfig.getSubSkillUnlockLevel(SubSkillType.EXCAVATION_GIGA_DRILL_BREAKER,
                1)).thenReturn(1);

        // wire advanced config

        when(RankUtils.getRankUnlockLevel(SubSkillType.EXCAVATION_ARCHAEOLOGY, 1)).thenReturn(
                1); // needed?
        when(RankUtils.getRankUnlockLevel(SubSkillType.EXCAVATION_GIGA_DRILL_BREAKER,
                1)).thenReturn(1); // needed?
        when(RankUtils.hasReachedRank(eq(1), any(Player.class),
                eq(SubSkillType.EXCAVATION_ARCHAEOLOGY))).thenReturn(true);
        when(RankUtils.hasReachedRank(eq(1), any(Player.class),
                eq(SubSkillType.EXCAVATION_GIGA_DRILL_BREAKER))).thenReturn(true);

        // setup player and player related mocks after everything else
        this.player = Mockito.mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerUUID);

        // wire inventory
        this.itemInMainHand = new ItemStack(Material.DIAMOND_SHOVEL);
        when(playerInventory.getItemInMainHand()).thenReturn(itemInMainHand);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void excavationShouldHaveTreasureDrops() {
        // Given: player has enough skill and a guaranteed treasure
        mmoPlayer.modifySkill(PrimarySkillType.EXCAVATION, 1000);

        final Block block = Mockito.mock(Block.class);
        when(block.getDrops(any())).thenReturn(null);
        when(block.getLocation()).thenReturn(new Location(world, 0, 64, 0));

        final ExcavationManager excavationManager = Mockito.spy(new ExcavationManager(mmoPlayer));
        doReturn(getGuaranteedTreasureDrops()).when(excavationManager).getTreasures(Material.SAND);

        // When: treasure roll happens inside BlockDropItemEvent;
        // Material.SAND simulates what event.getBlockState().getType() returns
        final List<ItemStack> drops = excavationManager.rollAndCollectTreasureDrops(block,
                Material.SAND);

        // Then: at least one treasure was returned for injection into the event
        verify(excavationManager, atLeastOnce()).getTreasures(Material.SAND);
        org.junit.jupiter.api.Assertions.assertFalse(drops.isEmpty(),
                "Expected at least one treasure drop from a guaranteed roll");
    }

    @Test
    void excavationShouldNotDropTreasure() {
        // Given: a treasure that can never drop (0% chance)
        mmoPlayer.modifySkill(PrimarySkillType.EXCAVATION, 1000);

        final Block block = Mockito.mock(Block.class);
        when(block.getDrops(any())).thenReturn(null);
        when(block.getLocation()).thenReturn(new Location(world, 0, 64, 0));

        final ExcavationManager excavationManager = Mockito.spy(new ExcavationManager(mmoPlayer));
        doReturn(getImpossibleTreasureDrops()).when(excavationManager).getTreasures(Material.SAND);

        // When
        final List<ItemStack> drops = excavationManager.rollAndCollectTreasureDrops(block,
                Material.SAND);

        // Then: no treasure was returned
        org.junit.jupiter.api.Assertions.assertTrue(drops.isEmpty(),
                "Expected no treasure drops from a 0%% chance roll");
    }

    private List<ExcavationTreasure> getGuaranteedTreasureDrops() {
        List<ExcavationTreasure> treasures = new ArrayList<>();
        treasures.add(new ExcavationTreasure(new ItemStack(Material.CAKE), 1, 100, 1));
        return treasures;
    }

    private List<ExcavationTreasure> getImpossibleTreasureDrops() {
        List<ExcavationTreasure> treasures = new ArrayList<>();
        treasures.add(new ExcavationTreasure(new ItemStack(Material.CAKE), 1, 0, 1));
        return treasures;
    }

    @Nested
    class ExcavationDropRouting {
        // These tests verify that rollAndCollectTreasureDrops() returns ItemStacks
        // for injection into BlockDropItemEvent, rather than spawning them directly.
        // This makes excavation treasure drops visible to Telekinesis-style enchant plugins.

        @Test
        void successfulTreasureRoll_returnsTreasureForEventInjection() {
            // Given: player has enough skill and a guaranteed treasure exists for this block
            mmoPlayer.modifySkill(PrimarySkillType.EXCAVATION, 1000);

            final Block block = Mockito.mock(Block.class);
            when(block.getLocation()).thenReturn(new Location(world, 1, 64, 1));

            final ExcavationManager excavationManager = Mockito.spy(
                    new ExcavationManager(mmoPlayer));
            doReturn(getGuaranteedTreasureDrops()).when(excavationManager)
                    .getTreasures(Material.SAND);

            // When: treasure roll happens with the pre-break material threaded from the listener
            final List<ItemStack> drops = excavationManager.rollAndCollectTreasureDrops(block,
                    Material.SAND);

            // Then: at least one treasure was returned for injection into the event,
            // not spawned directly via world.dropItem()
            org.junit.jupiter.api.Assertions.assertFalse(drops.isEmpty(),
                    "Expected treasure drops to be returned for event injection");
            verify(excavationManager, never()).processExcavationBonusesOnBlock(
                    any(ExcavationTreasure.class), any(Location.class));
        }

        @Test
        void failedTreasureRoll_returnsEmptyList() {
            // Given: a treasure that can never drop (0% chance)
            mmoPlayer.modifySkill(PrimarySkillType.EXCAVATION, 1000);

            final Block block = Mockito.mock(Block.class);
            when(block.getLocation()).thenReturn(new Location(world, 2, 64, 2));

            final ExcavationManager excavationManager = Mockito.spy(
                    new ExcavationManager(mmoPlayer));
            doReturn(getImpossibleTreasureDrops()).when(excavationManager)
                    .getTreasures(Material.SAND);

            // When
            final List<ItemStack> drops = excavationManager.rollAndCollectTreasureDrops(block,
                    Material.SAND);

            // Then: empty list — impossible treasure never triggers
            org.junit.jupiter.api.Assertions.assertTrue(drops.isEmpty(),
                    "Expected no drops from a 0%% chance treasure");
        }
    }

    @Nested
    class XpAndAbilities {
        private ExcavationManager excavationManager;
        private Block block;

        @BeforeEach
        void setUpManager() {
            excavationManager = Mockito.spy(new ExcavationManager(mmoPlayer));
            block = Mockito.mock(Block.class);
            when(block.getType()).thenReturn(Material.SAND);
            when(block.getLocation()).thenReturn(new Location(world, 0, 64, 0));
        }

        @Test
        void excavationBlockCheckShouldPayTheConfiguredXp() {
            // Given - digging sand pays 40 XP
            when(ExperienceConfig.getInstance().getXp(PrimarySkillType.EXCAVATION,
                    Material.SAND)).thenReturn(40);
            doNothing().when(excavationManager).applyXpGain(anyFloat(), any(), any());

            // When - the dug block is processed
            excavationManager.excavationBlockCheck(block);

            // Then - the XP lands as self-inflicted PVE XP
            verify(excavationManager).applyXpGain(40f, XPGainReason.PVE, XPGainSource.SELF);
        }

        @Test
        void gigaDrillBreakerShouldPayDoubleChecksAndWearTheTool() {
            try (MockedStatic<SkillUtils> skillUtils = mockStatic(SkillUtils.class)) {
                // Given - a configured ability tool damage
                doNothing().when(excavationManager).excavationBlockCheck(block);
                when(generalConfig.getAbilityToolDamage()).thenReturn(1);

                // When - Giga Drill Breaker processes the block
                excavationManager.gigaDrillBreaker(block);

                // Then - the block pays double XP checks and the shovel wears down
                verify(excavationManager, Mockito.times(2)).excavationBlockCheck(block);
                skillUtils.verify(() -> SkillUtils.handleDurabilityChange(itemInMainHand, 1));
            }
        }

        @Test
        void gigaDrillBreakerShouldTripleTheTreasureRolls() {
            // Given - an active Giga Drill Breaker and a guaranteed treasure
            mmoPlayer.modifySkill(PrimarySkillType.EXCAVATION, 1000);
            doReturn(true).when(mmoPlayer).getAbilityMode(SuperAbilityType.GIGA_DRILL_BREAKER);
            doReturn(getGuaranteedTreasureDrops()).when(excavationManager)
                    .getTreasures(Material.SAND);

            // When - the treasure roll runs
            final List<ItemStack> drops = excavationManager.rollAndCollectTreasureDrops(block,
                    Material.SAND);

            // Then - the guaranteed treasure lands three times
            org.junit.jupiter.api.Assertions.assertEquals(3, drops.size());
        }

        @Test
        void lowSkillShouldNotDropHighLevelTreasures() {
            // Given - a treasure requiring far more skill than the player has
            mmoPlayer.modifySkill(PrimarySkillType.EXCAVATION, 50);
            final List<ExcavationTreasure> highLevelTreasure = new ArrayList<>();
            highLevelTreasure.add(
                    new ExcavationTreasure(new ItemStack(Material.CAKE), 1, 100, 500));
            doReturn(highLevelTreasure).when(excavationManager).getTreasures(Material.SAND);

            // When - the treasure roll runs
            final List<ItemStack> drops = excavationManager.rollAndCollectTreasureDrops(block,
                    Material.SAND);

            // Then - nothing drops
            org.junit.jupiter.api.Assertions.assertTrue(drops.isEmpty());
        }

        @Test
        void treasureXpShouldBePaidPerSuccessfulRoll() {
            // Given - a guaranteed treasure worth 1 XP
            mmoPlayer.modifySkill(PrimarySkillType.EXCAVATION, 1000);
            doReturn(getGuaranteedTreasureDrops()).when(excavationManager)
                    .getTreasures(Material.SAND);
            doNothing().when(excavationManager).applyXpGain(anyFloat(), any(), any());

            // When - the treasure roll runs
            excavationManager.rollAndCollectTreasureDrops(block, Material.SAND);

            // Then - the treasure XP is paid
            verify(excavationManager).applyXpGain(1f, XPGainReason.PVE, XPGainSource.SELF);
        }

        @Test
        void archaeologyOrbsShouldSpawnOnWinningRolls() {
            // Given - archaeology rank 50 (100% orb chance) and a guaranteed treasure
            mmoPlayer.modifySkill(PrimarySkillType.EXCAVATION, 1000);
            final Player managerPlayer = mmoPlayer.getPlayer();
            when(RankUtils.getRank(managerPlayer, SubSkillType.EXCAVATION_ARCHAEOLOGY))
                    .thenReturn(50);
            doReturn(getGuaranteedTreasureDrops()).when(excavationManager)
                    .getTreasures(Material.SAND);
            doNothing().when(excavationManager).applyXpGain(anyFloat(), any(), any());

            // When - the treasure roll runs
            excavationManager.rollAndCollectTreasureDrops(block, Material.SAND);

            // Then - a vanilla XP orb spawns worth the archaeology rank
            mockedMisc.verify(() -> Misc.spawnExperienceOrb(any(Location.class), eq(50)));
        }

        @Test
        void missingArchaeologyPermissionShouldRollNothing() {
            // Given - no archaeology permission
            final Player managerPlayer = mmoPlayer.getPlayer();
            when(Permissions.isSubSkillEnabled(managerPlayer,
                    SubSkillType.EXCAVATION_ARCHAEOLOGY)).thenReturn(false);

            // When - the treasure roll runs
            final List<ItemStack> drops = excavationManager.rollAndCollectTreasureDrops(block,
                    Material.SAND);

            // Then - nothing drops and no treasure lookup happens
            org.junit.jupiter.api.Assertions.assertTrue(drops.isEmpty());
            verify(excavationManager, never()).getTreasures(any(Material.class));
        }

        @Test
        void orbRewardAndChanceShouldScaleWithArchaeologyRank() {
            // Given - archaeology rank 3
            final Player managerPlayer = mmoPlayer.getPlayer();
            when(RankUtils.getRank(managerPlayer, SubSkillType.EXCAVATION_ARCHAEOLOGY))
                    .thenReturn(3);

            // When / Then - the orb reward is the rank and the chance is twice the rank
            org.junit.jupiter.api.Assertions.assertEquals(3,
                    excavationManager.getExperienceOrbsReward());
            org.junit.jupiter.api.Assertions.assertEquals(6.0,
                    excavationManager.getArchaelogyExperienceOrbChance());
        }
    }
}
