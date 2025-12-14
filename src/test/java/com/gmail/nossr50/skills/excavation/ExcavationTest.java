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
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.util.TestPlayerMock;
import com.gmail.nossr50.util.skills.RankUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void excavationShouldHaveTreasureDrops() {
        final TestPlayerMock testPlayerMock = mockPlayer();
        final McMMOPlayer mmoPlayer = testPlayerMock.mmoPlayer();
        when(testPlayerMock.playerInventory().getItemInMainHand())
                .thenReturn(new ItemStack(Material.DIAMOND_SHOVEL));
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
                any(ExcavationTreasure.class), any(Location.class));
    }

    @Test
    void excavationShouldNotDropTreasure() {
        final TestPlayerMock testPlayerMock = mockPlayer();
        final McMMOPlayer mmoPlayer = testPlayerMock.mmoPlayer();
        when(testPlayerMock.playerInventory().getItemInMainHand())
                .thenReturn(new ItemStack(Material.DIAMOND_SHOVEL));
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
                any(ExcavationTreasure.class),
                any(Location.class));
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
