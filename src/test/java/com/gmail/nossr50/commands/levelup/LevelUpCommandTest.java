package com.gmail.nossr50.commands.levelup;

import com.gmail.nossr50.MMOTestEnvironmentBasic;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.mcMMO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LevelUpCommandTest extends MMOTestEnvironmentBasic {

    @BeforeEach
    void setUp() {
        mockBaseEnvironment();
    }

    @AfterEach
    void tearDown() {
        cleanupBaseEnvironment();
    }

    @Test
    void levelInMiningShouldRunCommandFiveTimes() {
        // GIVEN level up command for Mining should always execute for Mining level up
        assert mcMMO.p.getLevelUpCommandManager().isEmpty();
        final PrimarySkillType skillType = PrimarySkillType.MINING;
        final Predicate<Integer> predicate = (i) -> true;
        final LevelUpCommand levelUpCommand = spy(new LevelUpCommandImpl(
                predicate,
                "say hello",
                Set.of(skillType),
                true));
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);

        int levelsGained = 5;
        // WHEN player gains 5 levels in mining
        McMMOPlayerLevelUpEvent event = new McMMOPlayerLevelUpEvent(player, PrimarySkillType.MINING, levelsGained, XPGainReason.PVE);
        selfListener.onPlayerLevelUp(event);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager).apply(any(), any(), any());
        verify(levelUpCommand).process(any(), any(), any());
        // THEN the command should have executed
        verify(levelUpCommand, times(levelsGained)).executeCommand();
    }

    @Test
    void levelInMiningShouldRunCommandAtLeastOnce() {
        // GIVEN level up command for Mining should always execute for Mining level up
        assert mcMMO.p.getLevelUpCommandManager().isEmpty();
        final PrimarySkillType skillType = PrimarySkillType.MINING;
        final Predicate<Integer> predicate = (i) -> true;
        final LevelUpCommand levelUpCommand = spy(new LevelUpCommandImpl(
                predicate,
                "say hello",
                Set.of(skillType),
                true));
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);

        int levelsGained = 1;
        // WHEN player gains 5 levels in mining
        McMMOPlayerLevelUpEvent event = new McMMOPlayerLevelUpEvent(player, PrimarySkillType.MINING, levelsGained, XPGainReason.PVE);
        selfListener.onPlayerLevelUp(event);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager).apply(any(), any(), any());
        verify(levelUpCommand).process(any(), any(), any());
        // THEN the command should have executed
        verify(levelUpCommand).executeCommand();
    }

    @Test
    void levelInMiningShouldNotRunCommand() {
        // GIVEN level up command for Woodcutting should not execute for Mining level up
        assert mcMMO.p.getLevelUpCommandManager().isEmpty();
        final PrimarySkillType skillType = PrimarySkillType.WOODCUTTING;
        final Predicate<Integer> predicate = (i) -> true;
        final LevelUpCommand levelUpCommand = spy(new LevelUpCommandImpl(
                predicate,
                "say hello",
                Set.of(skillType),
                true));
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);


        int levelsGained = 5;
        // WHEN player gains 5 levels in mining
        McMMOPlayerLevelUpEvent event = new McMMOPlayerLevelUpEvent(player, PrimarySkillType.MINING, levelsGained, XPGainReason.PVE);
        selfListener.onPlayerLevelUp(event);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager).apply(any(), any(), any());
        verify(levelUpCommand).process(any(), any(), any());
        // THEN the command should not be run
        verify(levelUpCommand, never()).executeCommand();
    }
}