package com.gmail.nossr50.skills.woodcutting;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class WoodcuttingTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(WoodcuttingTest.class.getName());

    private WoodcuttingManager woodcuttingManager;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        Mockito.when(rankConfig.getSubSkillUnlockLevel(SubSkillType.WOODCUTTING_HARVEST_LUMBER, 1)).thenReturn(1);

        // wire advanced config
        Mockito.when(advancedConfig.getMaximumProbability(SubSkillType.WOODCUTTING_HARVEST_LUMBER)).thenReturn(100D);
        Mockito.when(advancedConfig.getMaximumProbability(SubSkillType.WOODCUTTING_CLEAN_CUTS)).thenReturn(10D);
        Mockito.when(advancedConfig.getMaxBonusLevel(SubSkillType.WOODCUTTING_HARVEST_LUMBER)).thenReturn(1000);
        Mockito.when(advancedConfig.getMaxBonusLevel(SubSkillType.WOODCUTTING_CLEAN_CUTS)).thenReturn(10000);

        Mockito.when(RankUtils.getRankUnlockLevel(SubSkillType.WOODCUTTING_HARVEST_LUMBER, 1)).thenReturn(1); // needed?
        Mockito.when(RankUtils.getRankUnlockLevel(SubSkillType.WOODCUTTING_CLEAN_CUTS, 1)).thenReturn(1000); // needed?
        Mockito.when(RankUtils.hasReachedRank(eq(1), any(Player.class), eq(SubSkillType.WOODCUTTING_HARVEST_LUMBER))).thenReturn(true);
        Mockito.when(RankUtils.hasReachedRank(eq(1), any(Player.class), eq(SubSkillType.WOODCUTTING_CLEAN_CUTS))).thenReturn(true);

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

        Block block = Mockito.mock(Block.class);
        // return empty collection if ItemStack
        Mockito.when(block.getDrops(any())).thenReturn(Collections.emptyList());
        Mockito.when(block.getType()).thenReturn(Material.OAK_LOG);
        woodcuttingManager.processBonusDropCheck(block);

        // verify bonus drops were spawned
        // TODO: using at least once since triple drops can also happen
        // TODO: Change the test env to disallow triple drop in the future
        Mockito.verify(woodcuttingManager, Mockito.atLeastOnce()).spawnHarvestLumberBonusDrops(block);
    }

    @Test
    void harvestLumberShouldNotDoubleDrop() {
        mmoPlayer.modifySkill(PrimarySkillType.WOODCUTTING, 0);

        Block block = Mockito.mock(Block.class);
        // wire block

        Mockito.when(block.getDrops(any())).thenReturn(null);
        Mockito.when(block.getType()).thenReturn(Material.OAK_LOG);
        woodcuttingManager.processBonusDropCheck(block);

        // verify bonus drops were not spawned
        Mockito.verify(woodcuttingManager, Mockito.times(0)).spawnHarvestLumberBonusDrops(block);
    }

    @Test
    void testProcessWoodcuttingBlockXP() {
        Block targetBlock = Mockito.mock(Block.class);
        Mockito.when(targetBlock.getType()).thenReturn(Material.OAK_LOG);
        // wire XP
        Mockito.when(ExperienceConfig.getInstance().getXp(PrimarySkillType.WOODCUTTING, Material.OAK_LOG)).thenReturn(5);

        // Verify XP increased by 5 when processing XP
        woodcuttingManager.processWoodcuttingBlockXP(targetBlock);
        Mockito.verify(mmoPlayer, Mockito.times(1)).beginXpGain(eq(PrimarySkillType.WOODCUTTING), eq(5F), any(), any());
    }
}
