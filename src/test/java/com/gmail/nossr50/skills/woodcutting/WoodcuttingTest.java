package com.gmail.nossr50.skills.woodcutting;

import static java.util.logging.Logger.getLogger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

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
        mmoPlayer.modifySkill(PrimarySkillType.WOODCUTTING, 1000);

        Block block = mock(Block.class);
        // return empty collection if ItemStack
        Mockito.when(block.getDrops(any())).thenReturn(Collections.emptyList());
        Mockito.when(block.getType()).thenReturn(Material.OAK_LOG);
        woodcuttingManager.processBonusDropCheck(block);

        // verify bonus drops were spawned
        // TODO: using at least once since triple drops can also happen
        // TODO: Change the test env to disallow triple drop in the future
        Mockito.verify(woodcuttingManager, Mockito.atLeastOnce())
                .spawnHarvestLumberBonusDrops(block);
    }

    @Test
    void harvestLumberShouldNotDoubleDrop() {
        mmoPlayer.modifySkill(PrimarySkillType.WOODCUTTING, 0);

        Block block = mock(Block.class);
        // wire block

        Mockito.when(block.getDrops(any())).thenReturn(null);
        Mockito.when(block.getType()).thenReturn(Material.OAK_LOG);
        woodcuttingManager.processBonusDropCheck(block);

        // verify bonus drops were not spawned
        Mockito.verify(woodcuttingManager, Mockito.times(0)).spawnHarvestLumberBonusDrops(block);
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

}
