package com.gmail.nossr50.commands.levelup;

import com.gmail.nossr50.MMOTestEnvironmentBasic;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventUtils;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;
import java.util.function.BiPredicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LevelUpCommandTest extends MMOTestEnvironmentBasic {
    private final PrimarySkillType skill = PrimarySkillType.MINING;
    private final PrimarySkillType otherSkill = PrimarySkillType.WOODCUTTING;

    @Test
    void levelUpShouldRunCommandFiveTimes() {
        // GIVEN level up command for Mining should always execute for Mining level up
        assert mcMMO.p.getLevelUpCommandManager().isEmpty();
        final String commandStr = "say hello";
        final LevelUpCommand levelUpCommand
                = buildLevelUpCommand(commandStr, (s, ignored) -> s == skill);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);

        // WHEN player gains 5 levels in mining via command
        assertEquals(0, mmoPlayer.getSkillLevel(skill));
        int levelsGained = 5;
        EventUtils.tryLevelChangeEvent(
                player,
                skill,
                levelsGained,
                mmoPlayer.getProfile().getSkillXpLevelRaw(skill),
                true,
                XPGainReason.COMMAND);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager).apply(any(), any(), any());
        verify(levelUpCommand).process(any(), any(), any());
        // THEN the command should have executed
        verify(levelUpCommand, times(levelsGained)).executeCommand(any(McMMOPlayer.class), any(PrimarySkillType.class), anyInt());
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), any()), atLeast(5));
    }

    @Test
    void levelUpShouldRunCommandFiveTimesWithPlaceholders() {
        // GIVEN level up command for Mining should always execute for Mining level up
        assert mcMMO.p.getLevelUpCommandManager().isEmpty();
        String playerName = "Momshroom";
        when (player.getName()).thenReturn(playerName);
        assertEquals(player.getName(), playerName);

        final String commandStr = "say hello %player%";
        final String expectedStr = "say hello " + playerName;
        final LevelUpCommand levelUpCommand
                = buildLevelUpCommand(commandStr, (s, ignored) -> s == skill);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);
        int levelsGained = 5;

        // WHEN player gains 5 levels in mining
        McMMOPlayerLevelUpEvent event = new McMMOPlayerLevelUpEvent(player, skill, levelsGained, XPGainReason.PVE);
        selfListener.onPlayerLevelUp(event);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager).apply(any(), any(), any());
        verify(levelUpCommand).process(any(), any(), any());
        // THEN the command should have executed
        verify(levelUpCommand, times(levelsGained)).executeCommand(any(McMMOPlayer.class), any(PrimarySkillType.class), anyInt());
        // verify that Bukkit.dispatchCommand got executed at least 5 times with the correct injectedCommand
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), eq(expectedStr)), atLeast(5));
    }

    @Test
    void levelUpShouldRunCommandFiveTimesWithPlaceholdersForLevel() {
        // GIVEN level up command for Mining should always execute for Mining level up
        assert mcMMO.p.getLevelUpCommandManager().isEmpty();
        String playerName = "Momshroom";
        when (player.getName()).thenReturn(playerName);
        assertEquals(player.getName(), playerName);

        final String commandStr = "say hello %player%, you have reached level %level%";
        final String expectedStr1 = "say hello " + playerName + ", you have reached level 1";
        final String expectedStr2 = "say hello " + playerName + ", you have reached level 2";
        final String expectedStr3 = "say hello " + playerName + ", you have reached level 3";
        final String expectedStr4 = "say hello " + playerName + ", you have reached level 4";
        final String expectedStr5 = "say hello " + playerName + ", you have reached level 5";

        final LevelUpCommand levelUpCommand
                = buildLevelUpCommand(commandStr, (s, ignored) -> s == skill);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);
        int levelsGained = 5;

        // WHEN player gains 5 levels in mining
        McMMOPlayerLevelUpEvent event = new McMMOPlayerLevelUpEvent(player, skill, levelsGained, XPGainReason.PVE);
        selfListener.onPlayerLevelUp(event);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager).apply(any(), any(), any());
        verify(levelUpCommand).process(any(), any(), any());
        // THEN the command should have executed
        verify(levelUpCommand, times(levelsGained)).executeCommand(any(McMMOPlayer.class), any(PrimarySkillType.class), anyInt());
        // verify that Bukkit.dispatchCommand got executed at least 5 times with the correct injectedCommand
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), eq(expectedStr1)));
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), eq(expectedStr2)));
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), eq(expectedStr3)));
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), eq(expectedStr4)));
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), eq(expectedStr5)));
    }

    @Test
    void levelUpShouldRunCommandAtLeastOnce() {
        // GIVEN level up command for Mining should always execute for Mining level up
        assert mcMMO.p.getLevelUpCommandManager().isEmpty();
        final String commandStr = "say hello";
        final LevelUpCommand levelUpCommand
                = buildLevelUpCommand(commandStr, (s, ignored) -> s == skill);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);

        int levelsGained = 1;
        // WHEN player gains 5 levels in mining
        McMMOPlayerLevelUpEvent event = new McMMOPlayerLevelUpEvent(player, skill, levelsGained, XPGainReason.PVE);
        selfListener.onPlayerLevelUp(event);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager).apply(any(), any(), any());
        verify(levelUpCommand).process(any(), any(), any());
        // THEN the command should have executed
        verify(levelUpCommand).executeCommand(any(McMMOPlayer.class), any(PrimarySkillType.class), anyInt());
    }

    @Test
    void levelUpShouldNotRunCommand() {
        // GIVEN level up command for Woodcutting should not execute for Mining level up
        assert mcMMO.p.getLevelUpCommandManager().isEmpty();
        final String commandStr = "say hello";
        final LevelUpCommand levelUpCommand
                = buildLevelUpCommand(commandStr, (s, ignored) -> s == otherSkill);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);


        int levelsGained = 5;
        // WHEN player gains 5 levels in mining
        McMMOPlayerLevelUpEvent event = new McMMOPlayerLevelUpEvent(player, skill, levelsGained, XPGainReason.PVE);
        selfListener.onPlayerLevelUp(event);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager).apply(any(), any(), any());
        verify(levelUpCommand).process(any(), any(), any());
        // THEN the command should not be run
        verify(levelUpCommand, never()).executeCommand(any(McMMOPlayer.class), any(PrimarySkillType.class), anyInt());
    }

    private LevelUpCommand buildLevelUpCommand(String commandStr, BiPredicate<PrimarySkillType, Integer> predicate) {
        LevelUpCommand.LevelUpCommandBuilder builder = new LevelUpCommand.LevelUpCommandBuilder();
        builder.command(commandStr)
                .withPredicate(predicate)
                .withLogInfo(true);
        return Mockito.spy(builder.build());
    }
}