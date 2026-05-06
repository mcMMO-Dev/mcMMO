package com.gmail.nossr50.skills.excavation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.skills.RankUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.bukkit.metadata.FixedMetadataValue;

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
        mmoPlayer.modifySkill(PrimarySkillType.EXCAVATION, 1000);

        // Wire block
        Block block = Mockito.mock(Block.class);
        when(block.getType()).thenReturn(Material.SAND);
        when(block.getDrops(any())).thenReturn(null);

        ExcavationManager excavationManager = Mockito.spy(new ExcavationManager(mmoPlayer));
        doReturn(getGuaranteedTreasureDrops()).when(excavationManager).getTreasures(block);
        excavationManager.excavationBlockCheck(block);

        // verify ExcavationManager.processExcavationBonusesOnBlock was called
        verify(excavationManager, atLeastOnce()).processExcavationBonusesOnBlock(
                eq(block), any(ExcavationTreasure.class), any(Location.class));
    }

    @Test
    void excavationShouldNotDropTreasure() {
        mmoPlayer.modifySkill(PrimarySkillType.EXCAVATION, 1000);

        // Wire block
        Block block = Mockito.mock(Block.class);
        when(block.getType()).thenReturn(Material.SAND);
        when(block.getDrops(any())).thenReturn(null);

        ExcavationManager excavationManager = Mockito.spy(new ExcavationManager(mmoPlayer));
        doReturn(getImpossibleTreasureDrops()).when(excavationManager).getTreasures(block);
        excavationManager.excavationBlockCheck(block);

        // verify ExcavationManager.processExcavationBonusesOnBlock was called
        verify(excavationManager, never()).processExcavationBonusesOnBlock(
                eq(block),
                any(ExcavationTreasure.class),
                any(Location.class));
    }

    @Test
    void excavationBonusesShouldQueueTreasureOnBlockMetadata() {
        Block block = Mockito.mock(Block.class);
        when(block.hasMetadata(MetadataConstants.METADATA_KEY_QUEUED_BLOCK_DROPS)).thenReturn(
                false);
        ExcavationTreasure treasure = new ExcavationTreasure(new ItemStack(Material.CAKE), 1, 100,
                1);
        ExcavationManager excavationManager = new ExcavationManager(mmoPlayer);
        Location location = new Location(world, 0, 0, 0);

        excavationManager.processExcavationBonusesOnBlock(block, treasure, location);

        ArgumentCaptor<FixedMetadataValue> metadataCaptor = ArgumentCaptor.forClass(
                FixedMetadataValue.class);
        verify(block).setMetadata(eq(MetadataConstants.METADATA_KEY_QUEUED_BLOCK_DROPS),
                metadataCaptor.capture());

        Object metadataValue = metadataCaptor.getValue().value();
        Assertions.assertInstanceOf(List.class, metadataValue);
        @SuppressWarnings("unchecked")
        List<ItemStack> queuedDrops = (List<ItemStack>) metadataValue;
        org.junit.jupiter.api.Assertions.assertEquals(1, queuedDrops.size());
        org.junit.jupiter.api.Assertions.assertEquals(Material.CAKE, queuedDrops.get(0).getType());
    }

    @Test
    void excavationBonusesShouldUseEcoDropQueueWhenAvailable() {
        Block block = Mockito.mock(Block.class);
        ExcavationTreasure treasure = new ExcavationTreasure(new ItemStack(Material.CAKE), 1, 100,
                1);
        ExcavationManager excavationManager = new ExcavationManager(mmoPlayer);
        Location location = new Location(world, 0, 0, 0);

        try (MockedStatic<ItemUtils> mockedItemUtils = Mockito.mockStatic(ItemUtils.class)) {
            mockedItemUtils.when(ItemUtils::shouldUseDropQueueRouting).thenReturn(true);
            mockedItemUtils.when(() -> ItemUtils.pushDropQueueIfPresent(any(), any(), any()))
                    .thenReturn(true);

            excavationManager.processExcavationBonusesOnBlock(block, treasure, location);
        }

        verify(block, never()).setMetadata(eq(MetadataConstants.METADATA_KEY_QUEUED_BLOCK_DROPS),
                any());
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
}
