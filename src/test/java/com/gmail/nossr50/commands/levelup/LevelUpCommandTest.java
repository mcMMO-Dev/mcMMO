package com.gmail.nossr50.commands.levelup;

import com.gmail.nossr50.MMOTestEnvironmentBasic;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventUtils;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LevelUpCommandTest extends MMOTestEnvironmentBasic {
    private final PrimarySkillType mining = PrimarySkillType.MINING;
    private final PrimarySkillType woodcutting = PrimarySkillType.WOODCUTTING;
    private McMMOPlayer mmoPlayer;
    private final String playerName = "Momshroom";

    @BeforeEach
    void beforeEach() {
        mcMMO.p.getLevelUpCommandManager().getSkillLevelCommands().clear();
        mcMMO.p.getLevelUpCommandManager().getPowerLevelUpCommands().clear();

        this.mmoPlayer = getMMOPlayer(UUID.randomUUID(), playerName, 0);
    }

    @Test
    void skillLevelUpShouldRunFiveTimes() {
        // GIVEN level up command for Mining should always execute for Mining level up
        assert mcMMO.p.getLevelUpCommandManager().getSkillLevelCommands().isEmpty();
        final String commandStr = "say hello";
        final SkillLevelUpCommand levelUpCommand
                = buildSkillLevelUpCommand(commandStr, (s, ignored) -> s == mining);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);

        // WHEN player gains 5 levels in mining via command
        levelPlayerViaXP(mmoPlayer, mining, 5);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager, atLeastOnce()).applySkillLevelUp(any(), any(), any());
        verify(levelUpCommand, atLeastOnce()).process(any(), any(), any());

        // THEN the command should have executed
        verify(levelUpCommand, times(5)).executeCommand(any(McMMOPlayer.class), any(PrimarySkillType.class), anyInt());
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), any()), atLeast(5));
    }

    @Test
    void skillLevelUpViaXPGainShouldRunFiveTimes() {
        // GIVEN level up command for Mining should always execute for Mining level up
        assert mcMMO.p.getLevelUpCommandManager().getSkillLevelCommands().isEmpty();
        final String commandStr = "say hello";
        final SkillLevelUpCommand levelUpCommand
                = buildSkillLevelUpCommand(commandStr, (s, ignored) -> s == mining);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);

        // WHEN player gains 5 levels in mining via command
        levelPlayerViaXP(mmoPlayer, mining, 5);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager, times(5)).applySkillLevelUp(any(), any(), any());
        verify(levelUpCommand, times(5)).process(any(), any(), any());

        // THEN the command should have executed
        verify(levelUpCommand, times(5)).executeCommand(any(McMMOPlayer.class), any(PrimarySkillType.class), anyInt());
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), any()), atLeast(5));
    }

    @Test
    void skillLevelUpViaXPGainShouldRunCommandFiveTimesWithPlaceholders() {
        // GIVEN level up command for Mining should always execute for Mining level up
        assert mcMMO.p.getLevelUpCommandManager().getSkillLevelCommands().isEmpty();
        assertEquals(mmoPlayer.getPlayer().getName(), playerName);
        final String commandStr = "say hello %player%, you have reached level %level%";
        final String expectedStr1 = "say hello " + playerName + ", you have reached level 1";
        final String expectedStr2 = "say hello " + playerName + ", you have reached level 2";
        final String expectedStr3 = "say hello " + playerName + ", you have reached level 3";
        final String expectedStr4 = "say hello " + playerName + ", you have reached level 4";
        final String expectedStr5 = "say hello " + playerName + ", you have reached level 5";
        final SkillLevelUpCommand levelUpCommand
                = buildSkillLevelUpCommand(commandStr, (s, ignored) -> s == mining);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);

        // WHEN player gains 5 levels in mining via command
        assertEquals(0, mmoPlayer.getSkillLevel(mining));
        int levelsGained = 5;
        for (int i = 0; i < 5; i++) {
            mmoPlayer.applyXpGain(mining, mmoPlayer.getProfile().getXpToLevel(mining), XPGainReason.COMMAND, XPGainSource.COMMAND);
        }

        // THEN the command should be checked for execution
        verify(levelUpCommandManager, times(levelsGained)).applySkillLevelUp(any(), any(), any());
        verify(levelUpCommand, times(levelsGained)).process(any(), any(), any());

        // THEN the command should have executed
        verify(levelUpCommand, times(levelsGained)).executeCommand(any(McMMOPlayer.class), any(PrimarySkillType.class), anyInt());
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), any()), atLeast(5));
        // AND THEN the message for each level up should have happened at least once
        // verify that Bukkit.dispatchCommand got executed at least 5 times with the correct injectedCommand
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), eq(expectedStr1)));
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), eq(expectedStr2)));
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), eq(expectedStr3)));
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), eq(expectedStr4)));
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), eq(expectedStr5)));
    }

    @Test
    void skillLevelUpShouldRunCommandFiveTimesWithPlaceholders() {
        // GIVEN level up command for Mining should always execute for Mining level up
        assert mcMMO.p.getLevelUpCommandManager().getSkillLevelCommands().isEmpty();
        assertEquals(mmoPlayer.getPlayer().getName(), playerName);

        final String commandStr = "say hello %player%";
        final String expectedStr = "say hello " + playerName;
        final SkillLevelUpCommand levelUpCommand
                = buildSkillLevelUpCommand(commandStr, (s, ignored) -> s == mining);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);
        int levelsGained = 5;

        // WHEN player gains 5 levels in mining
        McMMOPlayerLevelUpEvent event = new McMMOPlayerLevelUpEvent(mmoPlayer.getPlayer(), mining, levelsGained, XPGainReason.PVE);
        selfListener.onPlayerLevelUp(event);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager).applySkillLevelUp(any(), any(), any());
        verify(levelUpCommand).process(any(), any(), any());
        // THEN the command should have executed
        verify(levelUpCommand, times(levelsGained)).executeCommand(any(McMMOPlayer.class), any(PrimarySkillType.class), anyInt());
        // verify that Bukkit.dispatchCommand got executed at least 5 times with the correct injectedCommand
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), eq(expectedStr)), atLeast(5));
    }

    @Test
    void skillLevelUpViaAddLevelsShouldRunCommandFiveTimesWithPlaceholdersForLevel() {
        // GIVEN level up command for Mining should always execute for Mining level up
        assert mcMMO.p.getLevelUpCommandManager().getSkillLevelCommands().isEmpty();
        assertEquals(mmoPlayer.getPlayer().getName(), playerName);

        final String commandStr = "say hello %player%, you have reached level %level%";
        final String expectedStr1 = "say hello " + playerName + ", you have reached level 1";
        final String expectedStr2 = "say hello " + playerName + ", you have reached level 2";
        final String expectedStr3 = "say hello " + playerName + ", you have reached level 3";
        final String expectedStr4 = "say hello " + playerName + ", you have reached level 4";
        final String expectedStr5 = "say hello " + playerName + ", you have reached level 5";

        final SkillLevelUpCommand levelUpCommand
                = buildSkillLevelUpCommand(commandStr, (s, ignored) -> s == mining);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);

        // WHEN player gains 5 levels in mining
        int levelsGained = 5;
        mmoPlayer.getProfile().addLevels(mining, levelsGained);
        EventUtils.tryLevelChangeEvent(
                mmoPlayer.getPlayer(),
                mining,
                levelsGained,
                mmoPlayer.getProfile().getSkillXpLevelRaw(mining),
                true,
                XPGainReason.COMMAND);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager).applySkillLevelUp(any(), any(), any());
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
    void skillLevelUpShouldRunCommandAtLeastOnce() {
        // GIVEN level up command for Mining should always execute for Mining level up
        assert mcMMO.p.getLevelUpCommandManager().getSkillLevelCommands().isEmpty();
        final String commandStr = "say hello";
        final SkillLevelUpCommand levelUpCommand
                = buildSkillLevelUpCommand(commandStr, (s, ignored) -> s == mining);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);

        int levelsGained = 1;
        // WHEN player gains 5 levels in mining
        McMMOPlayerLevelUpEvent event = new McMMOPlayerLevelUpEvent(mmoPlayer.getPlayer(), mining, levelsGained, XPGainReason.PVE);
        selfListener.onPlayerLevelUp(event);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager).applySkillLevelUp(any(), any(), any());
        verify(levelUpCommand).process(any(), any(), any());
        // THEN the command should have executed
        verify(levelUpCommand).executeCommand(any(McMMOPlayer.class), any(PrimarySkillType.class), anyInt());
    }

    @Test
    void skillLevelUpShouldNotRunCommand() {
        // GIVEN level up command for Woodcutting should not execute for Mining level up
        assert mcMMO.p.getLevelUpCommandManager().getSkillLevelCommands().isEmpty();
        final String commandStr = "say hello";
        final SkillLevelUpCommand levelUpCommand
                = buildSkillLevelUpCommand(commandStr, (s, ignored) -> s == woodcutting);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);


        int levelsGained = 5;
        // WHEN player gains 5 levels in mining
        McMMOPlayerLevelUpEvent event = new McMMOPlayerLevelUpEvent(mmoPlayer.getPlayer(), mining, levelsGained, XPGainReason.PVE);
        selfListener.onPlayerLevelUp(event);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager).applySkillLevelUp(any(), any(), any());
        verify(levelUpCommand).process(any(), any(), any());
        // THEN the command should not be run
        verify(levelUpCommand, never()).executeCommand(any(McMMOPlayer.class), any(PrimarySkillType.class), anyInt());
    }

    @Test
    public void skillLevelUpShouldAlwaysRunPowerlevelCommand() {
        // GIVEN level up command for power level should always execute for any level up
        assert mcMMO.p.getLevelUpCommandManager().getSkillLevelCommands().isEmpty();
        final String commandStr = "say hello";
        final PowerLevelUpCommand powerLevelUpCommand
                = buildPowerLevelUpCommand(commandStr, (i) -> true);
        mcMMO.p.getLevelUpCommandManager().registerCommand(powerLevelUpCommand);

        // WHEN player gains 10 levels
        levelPlayerViaXP(mmoPlayer, mining, 10);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager, atLeastOnce()).applyPowerLevelUp(any(), any());
        verify(powerLevelUpCommand, atLeastOnce()).process(any(), any());
        // THEN the command should have executed
        verify(powerLevelUpCommand, times(10)).executeCommand(any(McMMOPlayer.class), anyInt());
    }

    @Test
    public void skillLevelUpShouldRunPowerlevelCommandOnce() {
        // GIVEN level up command for power level should always execute for any level up
        assert mcMMO.p.getLevelUpCommandManager().getSkillLevelCommands().isEmpty();
        final String commandStr = "say hello";
        final PowerLevelUpCommand powerLevelUpCommand
                = buildPowerLevelUpCommand(commandStr, (i) -> i == 5);
        mcMMO.p.getLevelUpCommandManager().registerCommand(powerLevelUpCommand);

        // WHEN player gains 5 levels
        levelPlayerViaXP(mmoPlayer, mining, 5);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager, atLeastOnce()).applyPowerLevelUp(any(), any());
        verify(powerLevelUpCommand, atLeastOnce()).process(any(), any());

        // THEN the command should have executed
        verify(powerLevelUpCommand, times(1)).executeCommand(any(McMMOPlayer.class), anyInt());
    }

    private SkillLevelUpCommand buildSkillLevelUpCommand(String commandStr, BiPredicate<PrimarySkillType, Integer> predicate) {
        final SkillLevelUpCommandBuilder builder = new SkillLevelUpCommandBuilder();
        builder.command(commandStr)
                .withPredicate(predicate)
                .withLogInfo(true);
        return Mockito.spy(builder.build());
    }

    private PowerLevelUpCommand buildPowerLevelUpCommand(String commandStr, Predicate<Integer> predicate) {
        final PowerLevelUpCommandBuilder builder = new PowerLevelUpCommandBuilder();
        builder.command(commandStr)
                .withPredicate(predicate)
                .withLogInfo(true);
        return Mockito.spy(builder.build());
    }

    private void levelPlayerViaXP(McMMOPlayer mmoPlayer, PrimarySkillType skill, int levelsGained) {
        assertEquals(0, mmoPlayer.getSkillLevel(skill));
        for (int i = 0; i < levelsGained; i++) {
            mmoPlayer.applyXpGain(mining, mmoPlayer.getProfile().getXpToLevel(skill), XPGainReason.COMMAND, XPGainSource.COMMAND);
        }
        assertEquals(levelsGained, mmoPlayer.getSkillLevel(skill));
    }

    private void levelPlayerViaLevelChangeEvent(McMMOPlayer mmoPlayer, PrimarySkillType skill, int levelsGained) {
        assertEquals(0, mmoPlayer.getSkillLevel(skill));
        EventUtils.tryLevelChangeEvent(
                mmoPlayer.getPlayer(),
                skill,
                levelsGained,
                mmoPlayer.getProfile().getSkillXpLevelRaw(skill),
                true,
                XPGainReason.COMMAND);
        assertEquals(levelsGained, mmoPlayer.getSkillLevel(skill));
    }
}