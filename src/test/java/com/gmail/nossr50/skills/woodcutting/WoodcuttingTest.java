package com.gmail.nossr50.skills.woodcutting;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mockStatic;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.anyDouble;

class WoodcuttingTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(WoodcuttingTest.class.getName());

    private WoodcuttingManager woodcuttingManager;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        Mockito.when(rankConfig.getSubSkillUnlockLevel(SubSkillType.WOODCUTTING_HARVEST_LUMBER, 1))
                .thenReturn(1);

        // wire advanced config
        Mockito.when(advancedConfig.getMaximumProbability(SubSkillType.WOODCUTTING_HARVEST_LUMBER))
                .thenReturn(100D);
        Mockito.when(advancedConfig.getMaximumProbability(SubSkillType.WOODCUTTING_CLEAN_CUTS))
                .thenReturn(10D);
        Mockito.when(advancedConfig.getMaxBonusLevel(SubSkillType.WOODCUTTING_HARVEST_LUMBER))
                .thenReturn(1000);
        Mockito.when(advancedConfig.getMaxBonusLevel(SubSkillType.WOODCUTTING_CLEAN_CUTS))
                .thenReturn(10000);

        Mockito.when(RankUtils.getRankUnlockLevel(SubSkillType.WOODCUTTING_HARVEST_LUMBER, 1))
                .thenReturn(1); // needed?
        Mockito.when(RankUtils.getRankUnlockLevel(SubSkillType.WOODCUTTING_CLEAN_CUTS, 1))
                .thenReturn(1000); // needed?
        Mockito.when(RankUtils.hasReachedRank(eq(1), any(Player.class),
                eq(SubSkillType.WOODCUTTING_HARVEST_LUMBER))).thenReturn(true);
        Mockito.when(RankUtils.hasReachedRank(eq(1), any(Player.class),
                eq(SubSkillType.WOODCUTTING_CLEAN_CUTS))).thenReturn(true);

        // wire inventory
        this.itemInMainHand = new ItemStack(Material.DIAMOND_AXE);
        Mockito.when(player.getInventory()).thenReturn(playerInventory);
        Mockito.when(playerInventory.getItemInMainHand()).thenReturn(itemInMainHand);

        // Set up spy for WoodcuttingManager
        woodcuttingManager = Mockito.spy(new WoodcuttingManager(mmoPlayer));
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void harvestLumberShouldDoubleDrop() {
        // Given: player has high Woodcutting skill and Tree Feller is not active
        mmoPlayer.modifySkill(PrimarySkillType.WOODCUTTING, 1000);
        mmoPlayer.setAbilityMode(SuperAbilityType.TREE_FELLER, false);

        Block block = mock(Block.class);
        Mockito.when(block.getDrops(any())).thenReturn(Collections.emptyList());
        Mockito.when(block.getType()).thenReturn(Material.OAK_LOG);

        // When
        woodcuttingManager.processBonusDropCheck(block);

        // Then: bonus drops are routed through BlockDropItemEvent via block metadata,
        // NOT spawned directly (allows Telekinesis-style enchant plugins to intercept)
        Mockito.verify(block, Mockito.atLeastOnce()).setMetadata(
                eq(MetadataConstants.METADATA_KEY_BONUS_DROPS), any());
        Mockito.verify(woodcuttingManager, Mockito.never()).spawnHarvestLumberBonusDrops(block);
    }

    @Test
    void harvestLumberShouldNotDoubleDrop() {
        // Given: player has no skill and Tree Feller is not active
        mmoPlayer.modifySkill(PrimarySkillType.WOODCUTTING, 0);
        mmoPlayer.setAbilityMode(SuperAbilityType.TREE_FELLER, false);

        Block block = mock(Block.class);
        Mockito.when(block.getDrops(any())).thenReturn(null);
        Mockito.when(block.getType()).thenReturn(Material.OAK_LOG);

        // When
        woodcuttingManager.processBonusDropCheck(block);

        // Then: no bonus drop path was triggered
        Mockito.verify(woodcuttingManager, Mockito.never()).spawnHarvestLumberBonusDrops(block);
        Mockito.verify(block, Mockito.never()).setMetadata(
                eq(MetadataConstants.METADATA_KEY_BONUS_DROPS), any());
    }

    @Test
    void testProcessWoodcuttingBlockXP() {
        Block targetBlock = mock(Block.class);
        Mockito.when(targetBlock.getType()).thenReturn(Material.OAK_LOG);
        // wire XP
        Mockito.when(ExperienceConfig.getInstance()
                .getXp(PrimarySkillType.WOODCUTTING, Material.OAK_LOG)).thenReturn(5);

        // Verify XP increased by 5 when processing XP
        woodcuttingManager.processWoodcuttingBlockXP(targetBlock);
        Mockito.verify(mmoPlayer, Mockito.times(1))
                .beginXpGain(eq(PrimarySkillType.WOODCUTTING), eq(5F), any(), any());
    }

    @Test
    void treeFellerShouldStopAtThreshold() {
        // Set threshold artificially low
        int fakeThreshold = 3;
        Mockito.when(generalConfig.getTreeFellerThreshold()).thenReturn(fakeThreshold);

        WoodcuttingManager manager = Mockito.spy(new WoodcuttingManager(mmoPlayer));

        // Simulate all blocks are logs with XP
        MockedStatic<BlockUtils> mockedBlockUtils = mockStatic(BlockUtils.class);
        mockedBlockUtils.when(() -> BlockUtils.hasWoodcuttingXP(any(Block.class))).thenReturn(true);
        mockedBlockUtils.when(() -> BlockUtils.isNonWoodPartOfTree(any(Block.class)))
                .thenReturn(false);

        // Simulate that block tracker always allows processing
        Mockito.when(mcMMO.getUserBlockTracker().isIneligible(any(Block.class))).thenReturn(false);

        // Create distinct mocked blocks to simulate recursion
        Block centerBlock = mock(Block.class);
        List<Block> relatives = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Block relative = mock(Block.class, "block_" + i);
            Mockito.when(relative.getRelative(any(BlockFace.class))).thenReturn(relative);
            Mockito.when(relative.getRelative(anyInt(), anyInt(), anyInt())).thenReturn(relative);
            relatives.add(relative);
        }

        // Wire center block to return a different relative each time
        Mockito.when(centerBlock.getRelative(any(BlockFace.class)))
                .thenAnswer(inv -> relatives.get(0));
        Mockito.when(centerBlock.getRelative(anyInt(), anyInt(), anyInt()))
                .thenAnswer(inv -> relatives.get(
                        ThreadLocalRandom.current().nextInt(relatives.size())));

        Set<Block> treeFellerBlocks = new HashSet<>();
        manager.processTree(centerBlock, treeFellerBlocks);

        // --- Assertions ---

        // It processed *at least one* block
        assertFalse(treeFellerBlocks.isEmpty(), "Tree Feller should process at least one block");

        // It reached or slightly exceeded the threshold
        assertTrue(treeFellerBlocks.size() >= fakeThreshold,
                "Tree Feller should process up to the threshold limit");

        // Confirm it stopped due to the threshold
        assertTrue(getPrivateTreeFellerReachedThreshold(manager),
                "Tree Feller should set treeFellerReachedThreshold to true");

        mockedBlockUtils.close();
    }

    private boolean getPrivateTreeFellerReachedThreshold(WoodcuttingManager manager) {
        try {
            Field field = WoodcuttingManager.class.getDeclaredField("treeFellerReachedThreshold");
            field.setAccessible(true);
            return (boolean) field.get(manager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void treeFellerShouldNotReachThreshold() throws NoSuchFieldException, IllegalAccessException {
        int threshold = 10;
        Mockito.when(generalConfig.getTreeFellerThreshold()).thenReturn(threshold);

        WoodcuttingManager manager = Mockito.spy(new WoodcuttingManager(mmoPlayer));

        MockedStatic<BlockUtils> mockedBlockUtils = mockStatic(BlockUtils.class);
        mockedBlockUtils.when(() -> BlockUtils.hasWoodcuttingXP(any(Block.class))).thenReturn(true);
        mockedBlockUtils.when(() -> BlockUtils.isNonWoodPartOfTree(any(Block.class)))
                .thenReturn(false);
        Mockito.when(mcMMO.getUserBlockTracker().isIneligible(any(Block.class))).thenReturn(false);

        // Create 4 blocks (well below threshold)
        Block b0 = mock(Block.class, "b0");
        Block b1 = mock(Block.class, "b1");
        Block b2 = mock(Block.class, "b2");
        Block b3 = mock(Block.class, "b3");

        // Deterministically chain recursion: b0 → b1 → b2 → b3 → null
        Mockito.when(b0.getRelative(anyInt(), anyInt(), anyInt())).thenReturn(b1);
        Mockito.when(b1.getRelative(anyInt(), anyInt(), anyInt())).thenReturn(b2);
        Mockito.when(b2.getRelative(anyInt(), anyInt(), anyInt())).thenReturn(b3);
        Mockito.when(b3.getRelative(anyInt(), anyInt(), anyInt())).thenReturn(null);

        Mockito.when(b0.getRelative(any(BlockFace.class))).thenReturn(b1);
        Mockito.when(b1.getRelative(any(BlockFace.class))).thenReturn(b2);
        Mockito.when(b2.getRelative(any(BlockFace.class))).thenReturn(b3);
        Mockito.when(b3.getRelative(any(BlockFace.class))).thenReturn(null);

        Set<Block> processed = new HashSet<>();
        manager.processTree(b0, processed);

        assertEquals(3, processed.size(), "Should process exactly 4 blocks");
        assertFalse(getPrivateTreeFellerReachedThreshold(manager),
                "treeFellerReachedThreshold should remain false");

        mockedBlockUtils.close();
    }

    @Nested
    class ProcessBonusDropCheckRouting {
        // These tests verify that normal (non-Tree Feller) woodcutting bonus drops are
        // routed through BlockDropItemEvent via BlockUtils.markDropsAsBonus(), so that
        // Telekinesis-style enchant plugins can intercept them. Tree Feller must stay on
        // the legacy spawnHarvestLumberBonusDrops() path because Tree Feller sets blocks
        // to AIR directly and never fires BlockDropItemEvent.

        @Test
        void normalDropSetsBlockMetadataNotDirectlySpawned() {
            // Given: player has Woodcutting skill and Tree Feller is NOT active
            mmoPlayer.modifySkill(PrimarySkillType.WOODCUTTING, 1000);
            mmoPlayer.setAbilityMode(SuperAbilityType.TREE_FELLER, false);

            final Block block = mock(Block.class);
            Mockito.when(block.getType()).thenReturn(Material.OAK_LOG);
            Mockito.when(block.getDrops(any())).thenReturn(Collections.emptyList());

            // When: bonus drop check is processed
            woodcuttingManager.processBonusDropCheck(block);

            // Then: metadata was set on the block (routed through BlockDropItemEvent)
            verify(block, Mockito.atLeastOnce()).setMetadata(
                    eq(MetadataConstants.METADATA_KEY_BONUS_DROPS), any());

            // Then: drops were NOT spawned directly (old path)
            verify(woodcuttingManager, never()).spawnHarvestLumberBonusDrops(block);
        }

        @Test
        void treeFellerDropSpawnsDirectlyWithoutBlockMetadata() {
            // Given: player has Woodcutting skill and Tree Feller IS active
            mmoPlayer.modifySkill(PrimarySkillType.WOODCUTTING, 1000);
            mmoPlayer.setAbilityMode(SuperAbilityType.TREE_FELLER, true);

            final Block block = mock(Block.class);
            Mockito.when(block.getType()).thenReturn(Material.OAK_LOG);
            Mockito.when(block.getDrops(any())).thenReturn(Collections.emptyList());

            // When: bonus drop check is processed during Tree Feller
            woodcuttingManager.processBonusDropCheck(block);

            // Then: drops were spawned directly via the legacy path (Tree Feller cannot use
            // BlockDropItemEvent since it sets blocks to AIR without firing that event)
            verify(woodcuttingManager, Mockito.atLeastOnce()).spawnHarvestLumberBonusDrops(block);

            // Then: metadata was NOT set (would be silently lost since BlockDropItemEvent
            // never fires for Tree Feller blocks)
            verify(block, never()).setMetadata(
                    eq(MetadataConstants.METADATA_KEY_BONUS_DROPS), any());
        }

        @Test
        void noBonusWhenSkillLevelTooLow() {
            // Given: player has no Woodcutting skill and Tree Feller is not active
            mmoPlayer.modifySkill(PrimarySkillType.WOODCUTTING, 0);
            mmoPlayer.setAbilityMode(SuperAbilityType.TREE_FELLER, false);

            final Block block = mock(Block.class);
            Mockito.when(block.getType()).thenReturn(Material.OAK_LOG);
            Mockito.when(block.getDrops(any())).thenReturn(null);

            // When
            woodcuttingManager.processBonusDropCheck(block);

            // Then: neither path was triggered
            verify(woodcuttingManager, never()).spawnHarvestLumberBonusDrops(block);
            verify(block, never()).setMetadata(
                    eq(MetadataConstants.METADATA_KEY_BONUS_DROPS), any());
        }
    }

    @Nested
    class KnockOnWoodXpOrbSpawning {
        // Covers the bug where KnockOnWood XP orbs never spawned for nether tree cap
        // blocks (nether_wart_block, warped_wart_block). Those blocks appear in both
        // experience.yml (hasWoodcuttingXP=true) and the treeFellerDestructibleWhiteList
        // (isNonWoodPartOfTree=true). The old code placed the XP orb logic inside the
        // else-if branch for isNonWoodPartOfTree, so it was unreachable for any block
        // that also had woodcutting XP. The fix extracts the orb logic into a standalone
        // if-block that runs after the if/else-if drop routing.

        @Test
        void netherWartBlockSpawnsOrbWhenRank2Unlocked() {
            // Given: player has KnockOnWood at rank 2 and XP orbs are enabled
            mmoPlayer.modifySkill(PrimarySkillType.WOODCUTTING, 1000);
            Mockito.when(RankUtils.hasUnlockedSubskill(any(Player.class),
                    eq(SubSkillType.WOODCUTTING_KNOCK_ON_WOOD))).thenReturn(true);
            Mockito.when(RankUtils.hasReachedRank(eq(2), any(Player.class),
                    eq(SubSkillType.WOODCUTTING_KNOCK_ON_WOOD))).thenReturn(true);
            Mockito.when(advancedConfig.isKnockOnWoodXPOrbEnabled()).thenReturn(true);

            // Given: block that satisfies BOTH hasWoodcuttingXP AND isNonWoodPartOfTree
            // (matches nether_wart_block / warped_wart_block — the previously broken case)
            final Block netherWartBlock = mock(Block.class, "netherWartBlock");
            final Location blockLocation = new Location(world, 10, 64, 10);
            Mockito.when(netherWartBlock.getDrops(any())).thenReturn(Collections.emptyList());
            Mockito.when(netherWartBlock.getLocation()).thenReturn(blockLocation);

            // Stub processBonusDropCheck so it does not exercise unrelated paths
            Mockito.doNothing().when(woodcuttingManager).processBonusDropCheck(any(Block.class));

            try (MockedStatic<BlockUtils> mockedBlockUtils = mockStatic(BlockUtils.class);
                    MockedStatic<EventUtils> localMockedEventUtils = mockStatic(EventUtils.class);
                    MockedStatic<ProbabilityUtil> mockedProbabilityUtil =
                            mockStatic(ProbabilityUtil.class)) {

                mockedBlockUtils.when(() -> BlockUtils.hasWoodcuttingXP(any(Block.class)))
                        .thenReturn(true);
                mockedBlockUtils.when(() -> BlockUtils.isNonWoodPartOfTree(any(Block.class)))
                        .thenReturn(true);
                localMockedEventUtils.when(() -> EventUtils.simulateBlockBreak(
                        any(Block.class), any(Player.class), any())).thenReturn(true);
                // Force the 10% RNG check to always succeed so the orb always spawns
                mockedProbabilityUtil.when(() -> ProbabilityUtil.isStaticSkillRNGSuccessful(
                        any(PrimarySkillType.class), any(McMMOPlayer.class), anyDouble()))
                        .thenReturn(true);

                // Wire Misc.getRandom() to a predictable stub so the orb count is deterministic
                final Random stubRandom = mock(Random.class);
                Mockito.when(Misc.getRandom()).thenReturn(stubRandom);
                Mockito.when(stubRandom.nextInt(anyInt())).thenReturn(50);

                // When: Tree Feller loot is processed for this block
                invokeDropTreeFellerLootFromBlocks(Set.of(netherWartBlock));

                // Then: an XP orb was spawned — this was the bug; this call was unreachable
                // before the fix because the orb code was inside the else-if for isNonWoodPartOfTree
                mockedMisc.verify(() -> Misc.spawnExperienceOrb(eq(blockLocation), anyInt()));
            }
        }

        @Test
        void regularLeafSpawnsOrbWhenRank2Unlocked() {
            // Given: a regular leaf block — only isNonWoodPartOfTree=true, no woodcutting XP.
            // Regression check: existing leaf behaviour continues to work after the fix.
            mmoPlayer.modifySkill(PrimarySkillType.WOODCUTTING, 1000);
            Mockito.when(RankUtils.hasUnlockedSubskill(any(Player.class),
                    eq(SubSkillType.WOODCUTTING_KNOCK_ON_WOOD))).thenReturn(true);
            Mockito.when(RankUtils.hasReachedRank(eq(2), any(Player.class),
                    eq(SubSkillType.WOODCUTTING_KNOCK_ON_WOOD))).thenReturn(true);
            Mockito.when(advancedConfig.isKnockOnWoodXPOrbEnabled()).thenReturn(true);

            final Block leafBlock = mock(Block.class, "leafBlock");
            final Location blockLocation = new Location(world, 10, 64, 10);
            Mockito.when(leafBlock.getDrops(any())).thenReturn(Collections.emptyList());
            Mockito.when(leafBlock.getLocation()).thenReturn(blockLocation);

            try (MockedStatic<BlockUtils> mockedBlockUtils = mockStatic(BlockUtils.class);
                    MockedStatic<EventUtils> localMockedEventUtils = mockStatic(EventUtils.class);
                    MockedStatic<ProbabilityUtil> mockedProbabilityUtil =
                            mockStatic(ProbabilityUtil.class)) {

                mockedBlockUtils.when(() -> BlockUtils.hasWoodcuttingXP(any(Block.class)))
                        .thenReturn(false);
                mockedBlockUtils.when(() -> BlockUtils.isNonWoodPartOfTree(any(Block.class)))
                        .thenReturn(true);
                localMockedEventUtils.when(() -> EventUtils.simulateBlockBreak(
                        any(Block.class), any(Player.class), any())).thenReturn(true);
                mockedProbabilityUtil.when(() -> ProbabilityUtil.isStaticSkillRNGSuccessful(
                        any(PrimarySkillType.class), any(McMMOPlayer.class), anyDouble()))
                        .thenReturn(true);

                final Random stubRandom = mock(Random.class);
                Mockito.when(Misc.getRandom()).thenReturn(stubRandom);
                Mockito.when(stubRandom.nextInt(anyInt())).thenReturn(50);

                // When
                invokeDropTreeFellerLootFromBlocks(Set.of(leafBlock));

                // Then: orb still spawns for leaves (unchanged behaviour)
                mockedMisc.verify(() -> Misc.spawnExperienceOrb(eq(blockLocation), anyInt()));
            }
        }

        @Test
        void regularLogDoesNotSpawnOrb() {
            // Given: a regular log block (hasWoodcuttingXP=true, isNonWoodPartOfTree=false).
            // XP orbs must NEVER fire for plain logs — only for non-log tree components.
            mmoPlayer.modifySkill(PrimarySkillType.WOODCUTTING, 1000);
            Mockito.when(RankUtils.hasUnlockedSubskill(any(Player.class),
                    eq(SubSkillType.WOODCUTTING_KNOCK_ON_WOOD))).thenReturn(true);
            Mockito.when(RankUtils.hasReachedRank(eq(2), any(Player.class),
                    eq(SubSkillType.WOODCUTTING_KNOCK_ON_WOOD))).thenReturn(true);
            Mockito.when(advancedConfig.isKnockOnWoodXPOrbEnabled()).thenReturn(true);

            final Block logBlock = mock(Block.class, "logBlock");
            Mockito.when(logBlock.getDrops(any())).thenReturn(Collections.emptyList());

            // Stub processBonusDropCheck so it does not exercise unrelated paths
            Mockito.doNothing().when(woodcuttingManager).processBonusDropCheck(any(Block.class));

            try (MockedStatic<BlockUtils> mockedBlockUtils = mockStatic(BlockUtils.class);
                    MockedStatic<EventUtils> localMockedEventUtils = mockStatic(EventUtils.class);
                    MockedStatic<ProbabilityUtil> mockedProbabilityUtil =
                            mockStatic(ProbabilityUtil.class)) {

                mockedBlockUtils.when(() -> BlockUtils.hasWoodcuttingXP(any(Block.class)))
                        .thenReturn(true);
                mockedBlockUtils.when(() -> BlockUtils.isNonWoodPartOfTree(any(Block.class)))
                        .thenReturn(false);
                localMockedEventUtils.when(() -> EventUtils.simulateBlockBreak(
                        any(Block.class), any(Player.class), any())).thenReturn(true);
                mockedProbabilityUtil.when(() -> ProbabilityUtil.isStaticSkillRNGSuccessful(
                        any(PrimarySkillType.class), any(McMMOPlayer.class), anyDouble()))
                        .thenReturn(true);

                // When
                invokeDropTreeFellerLootFromBlocks(Set.of(logBlock));

                // Then: no XP orb spawned for a plain log
                mockedMisc.verify(() -> Misc.spawnExperienceOrb(any(), anyInt()), never());
            }
        }

        private void invokeDropTreeFellerLootFromBlocks(final Set<Block> blocks) {
            try {
                final Method method = WoodcuttingManager.class.getDeclaredMethod(
                        "dropTreeFellerLootFromBlocks", Set.class);
                method.setAccessible(true);
                method.invoke(woodcuttingManager, blocks);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    // Covers #5182: the Tree Feller splinter check must compare damage against the
    // item's effective maximum (max_damage component when present), and must read the
    // damage AFTER the durability loss is applied rather than from a stale ItemMeta copy.
    private static Stream<Arguments> durabilityBoundaryScenarios() {
        return Stream.of(
                Arguments.of("custom max keeps tool alive past vanilla max", true, 3000, 1600,
                        true),
                Arguments.of("custom max reached splinters tool", true, 3000, 3000, false),
                Arguments.of("vanilla tool below max survives", false, 0, 1000, true),
                Arguments.of("vanilla tool at max splinters", false, 0, 1561, false));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("durabilityBoundaryScenarios")
    void handleDurabilityLossShouldCompareDamageAgainstEffectiveMax(String description,
            boolean hasMaxDamage, int maxDamage, int currentDamage, boolean expectedCanSustain) {
        // Given - a diamond axe in the scenario's durability state (vanilla max is 1561)
        final ItemStack axe = mock(ItemStack.class);
        final Damageable damageableMeta = mock(Damageable.class);
        Mockito.when(axe.getItemMeta()).thenReturn(damageableMeta);
        Mockito.when(axe.getType()).thenReturn(Material.DIAMOND_AXE);
        Mockito.when(damageableMeta.isUnbreakable()).thenReturn(false);
        Mockito.when(damageableMeta.hasMaxDamage()).thenReturn(hasMaxDamage);
        Mockito.when(damageableMeta.getMaxDamage()).thenReturn(maxDamage);
        Mockito.when(damageableMeta.getDamage()).thenReturn(currentDamage);

        // Given - durability application is stubbed out so the boundary check is isolated
        try (MockedStatic<SkillUtils> ignoredSkillUtils = mockStatic(SkillUtils.class)) {
            // When - Tree Feller resolves whether the tool survives the durability loss
            final boolean canSustain = invokeHandleDurabilityLoss(Collections.emptySet(), axe);

            // Then - the verdict is based on the item's effective maximum
            assertThat(canSustain).as(description).isEqualTo(expectedCanSustain);
        }
    }

    @Test
    void handleDurabilityLossShouldAlwaysSustainUnbreakableTools() {
        // Given - an unbreakable axe
        final ItemStack axe = mock(ItemStack.class);
        final Damageable damageableMeta = mock(Damageable.class);
        Mockito.when(axe.getItemMeta()).thenReturn(damageableMeta);
        Mockito.when(damageableMeta.isUnbreakable()).thenReturn(true);

        // When - Tree Feller resolves whether the tool survives
        final boolean canSustain = invokeHandleDurabilityLoss(Collections.emptySet(), axe);

        // Then - unbreakable tools never splinter
        assertThat(canSustain).isTrue();
    }

    @Test
    void handleDurabilityLossShouldRespectEventDamageModifiedByPlugins() {
        // Given - one log block that would cost 2 durability
        Mockito.when(generalConfig.getAbilityToolDamage()).thenReturn(2);
        final Block logBlock = mock(Block.class);
        final ItemStack axe = mock(ItemStack.class);
        final Damageable damageableMeta = mock(Damageable.class);
        Mockito.when(axe.getItemMeta()).thenReturn(damageableMeta);
        Mockito.when(axe.getType()).thenReturn(Material.DIAMOND_AXE);
        Mockito.when(damageableMeta.isUnbreakable()).thenReturn(false);

        // Given - a plugin listener that zeroes the damage without cancelling the event
        // (the pattern used by custom-durability plugins such as Oraxen)
        Mockito.doAnswer(invocation -> {
            final PlayerItemDamageEvent event = invocation.getArgument(0);
            event.setDamage(0);
            return null;
        }).when(pluginManager).callEvent(any(PlayerItemDamageEvent.class));

        try (MockedStatic<BlockUtils> mockedBlockUtils = mockStatic(BlockUtils.class);
                MockedStatic<SkillUtils> mockedSkillUtils = mockStatic(SkillUtils.class)) {
            mockedBlockUtils.when(() -> BlockUtils.hasWoodcuttingXP(logBlock)).thenReturn(true);

            // When - Tree Feller applies the durability loss
            invokeHandleDurabilityLoss(Set.of(logBlock), axe);

            // Then - the durability change uses the plugin-modified damage, not the original
            mockedSkillUtils.verify(() -> SkillUtils.handleDurabilityChange(axe, 0));
        }
    }

    private boolean invokeHandleDurabilityLoss(final Set<Block> treeFellerBlocks,
            final ItemStack inHand) {
        try {
            final Method method = WoodcuttingManager.class.getDeclaredMethod(
                    "handleDurabilityLoss", Set.class, ItemStack.class, Player.class);
            method.setAccessible(true);
            return (boolean) method.invoke(null, treeFellerBlocks, inHand, player);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
