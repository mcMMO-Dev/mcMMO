package com.gmail.nossr50.commands.levelup;

import static com.gmail.nossr50.datatypes.skills.PrimarySkillType.MINING;
import static com.gmail.nossr50.datatypes.skills.PrimarySkillType.WOODCUTTING;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.gmail.nossr50.MMOTestEnvironmentBasic;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventUtils;
import java.util.List;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class LevelUpCommandTest extends MMOTestEnvironmentBasic {
    private static final BiPredicate<PrimarySkillType, Integer> ALWAYS_TRUE = (skill, level) -> true;
    private McMMOPlayer mmoPlayer;
    private final String playerName = "Momshroom";

    @BeforeEach
    void beforeEach() {
        mcMMO.p.getLevelUpCommandManager().getLevelUpCommands().clear();

        this.mmoPlayer = getMMOPlayer(UUID.randomUUID(), playerName, 0);
    }

    @Test
    void skillLevelUpShouldRunFiveTimes() {
        // GIVEN level up command for Mining should always execute for Mining level up
        assert mcMMO.p.getLevelUpCommandManager().getLevelUpCommands().isEmpty();
        final String commandStr = "say hello";
        final LevelUpCommand levelUpCommand = buildLevelUpCommand(commandStr,
                (skill, skillLevel) -> skill == MINING && skillLevel >= 1 && skillLevel <= 5);

        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);

        // WHEN player gains 5 levels in mining via command
        levelPlayerViaXP(mmoPlayer, MINING, 5);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager, atLeastOnce()).applySkillLevelUp(any(), any(), any(), any());
        verify(levelUpCommand, atLeastOnce()).process(any(), any(), any(), any());

        // THEN the command should have executed
        verify(levelUpCommand, times(5)).executeCommand(any(McMMOPlayer.class));
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), any()), atLeast(5));
    }

    @Test
    void dualRequirementsShouldRunOnce() {
        // GIVEN
        assert mcMMO.p.getLevelUpCommandManager().getLevelUpCommands().isEmpty();
        final String commandStr = "say hello";
        BiPredicate<PrimarySkillType, Integer> predicate = (skill, skillLevel) -> skill == MINING
                && skillLevel == 3;
        BiPredicate<PrimarySkillType, Integer> predicate2 = (skill, skillLevel) ->
                skill == WOODCUTTING && skillLevel == 3;
        final LevelUpCommand levelUpCommand = buildLevelUpCommand(commandStr,
                List.of(predicate, predicate2));

        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);

        // WHEN player gains 5 levels in mining and woodcutting via command
        levelPlayerViaXP(mmoPlayer, MINING, 5);
        levelPlayerViaXP(mmoPlayer, WOODCUTTING, 5);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager, atLeastOnce()).applySkillLevelUp(any(), any(), any(), any());
        verify(levelUpCommand, times(10)).process(any(), any(), any(), any());

        // THEN the command should have executed
        verify(levelUpCommand, times(1)).executeCommand(any(McMMOPlayer.class));
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), any()), atLeast(1));
    }

    @Test
    void skillLevelUpViaXPGainShouldRunFiveTimes() {
        // GIVEN level up command for Mining should always execute for Mining level up
        assert mcMMO.p.getLevelUpCommandManager().getLevelUpCommands().isEmpty();
        final String commandStr = "say hello";
        final LevelUpCommand levelUpCommand = buildLevelUpCommand(commandStr,
                (skill, skillLevel) -> skill == MINING && skillLevel >= 1 && skillLevel <= 5);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);

        // WHEN player gains 5 levels in mining via command
        levelPlayerViaXP(mmoPlayer, MINING, 5);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager, times(5)).applySkillLevelUp(any(), any(), any(), any());
        verify(levelUpCommand, times(5)).process(any(), any(), any(), any());

        // THEN the command should have executed
        verify(levelUpCommand, times(5)).executeCommand(any(McMMOPlayer.class));
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), any()), atLeast(5));
    }

    @Test
    void skillLevelUpViaXPGainShouldRunCommandFiveTimesWithPlaceholders() {
        // GIVEN level up command for Mining should always execute for Mining level up
        assert mcMMO.p.getLevelUpCommandManager().getLevelUpCommands().isEmpty();
        assertEquals(mmoPlayer.getPlayer().getName(), playerName);
        final String commandStr = "say hello {@player}, you have reached level {@mining_level}";
        final String expectedStr1 = "say hello " + playerName + ", you have reached level 1";
        final String expectedStr2 = "say hello " + playerName + ", you have reached level 2";
        final String expectedStr3 = "say hello " + playerName + ", you have reached level 3";
        final String expectedStr4 = "say hello " + playerName + ", you have reached level 4";
        final String expectedStr5 = "say hello " + playerName + ", you have reached level 5";
        final LevelUpCommand levelUpCommand = buildLevelUpCommand(commandStr,
                (skill, skillLevel) -> skill == MINING && skillLevel >= 1 && skillLevel <= 5);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);

        // WHEN player gains 5 levels in mining via command
        assertEquals(0, mmoPlayer.getSkillLevel(MINING));
        int levelsGained = 5;
        levelPlayerViaXP(mmoPlayer, MINING, levelsGained);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager, times(levelsGained)).applySkillLevelUp(any(), any(), any(),
                any());
        verify(levelUpCommand, times(levelsGained)).process(any(), any(), any(), any());

        // THEN the command should have executed
        verify(levelUpCommand, times(levelsGained)).executeCommand(any(McMMOPlayer.class));
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
    void skillLevelUpShouldRunCommandThreeTimesWithPlaceholders() {
        /*
            This test executes a player leveling up 5 times.
            With level 3 separate registered level up commands.
            Each registered command runs only once.
         */
        // GIVEN level up command for Mining should always execute for Mining level up
        assert mcMMO.p.getLevelUpCommandManager().getLevelUpCommands().isEmpty();
        assertEquals(mmoPlayer.getPlayer().getName(), playerName);

        final String commandStr = "say hello {@player}";
        final String expectedStr = "say hello " + playerName;
        final LevelUpCommand levelUpCommandOne = buildLevelUpCommand(commandStr,
                (skill, level) -> skill == MINING && level == 1);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommandOne);
        final LevelUpCommand levelUpCommandTwo = buildLevelUpCommand(commandStr,
                (skill, level) -> skill == MINING && level == 2);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommandTwo);
        final LevelUpCommand levelUpCommandThree = buildLevelUpCommand(commandStr,
                (skill, level) -> skill == MINING && level == 3);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommandThree);
        int levelsGained = 5;

        // WHEN player gains 5 levels in mining
        levelPlayerViaXP(mmoPlayer, MINING, levelsGained);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager, times(levelsGained)).applySkillLevelUp(any(), any(), any(),
                any());
        verify(levelUpCommandOne, times(levelsGained)).process(any(), any(), any(), any());
        verify(levelUpCommandTwo, times(levelsGained)).process(any(), any(), any(), any());
        verify(levelUpCommandThree, times(levelsGained)).process(any(), any(), any(), any());
        // THEN the command should have executed
        verify(levelUpCommandOne, times(1)).executeCommand(any(McMMOPlayer.class));
        verify(levelUpCommandTwo, times(1)).executeCommand(any(McMMOPlayer.class));
        verify(levelUpCommandThree, times(1)).executeCommand(any(McMMOPlayer.class));
        // verify that Bukkit.dispatchCommand got executed at least 20 times with the correct injectedCommand
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), eq(expectedStr)), atLeast(3));
    }

    @Test
    void skillLevelUpShouldRunCommandFourTimesWithPlaceholders() {
        /*
            This test executes a player leveling up 5 times.
            With level 3 separate registered level up commands.
            One command runs twice, the others run once.
         */
        // GIVEN level up command for Mining should always execute for Mining level up
        assert mcMMO.p.getLevelUpCommandManager().getLevelUpCommands().isEmpty();
        assertEquals(mmoPlayer.getPlayer().getName(), playerName);

        final String commandStr = "say hello {@player}";
        final String expectedStr = "say hello " + playerName;
        final LevelUpCommand levelUpCommandOne = buildLevelUpCommand(commandStr,
                (skill, level) -> skill == MINING && (level == 1 || level == 4));
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommandOne);
        final LevelUpCommand levelUpCommandTwo = buildLevelUpCommand(commandStr,
                (skill, level) -> skill == MINING && level == 2);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommandTwo);
        final LevelUpCommand levelUpCommandThree = buildLevelUpCommand(commandStr,
                (skill, level) -> skill == MINING && level == 3);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommandThree);
        int levelsGained = 5;

        // WHEN player gains 5 levels in mining
        levelPlayerViaXP(mmoPlayer, MINING, levelsGained);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager, times(levelsGained)).applySkillLevelUp(any(), any(), any(),
                any());
        verify(levelUpCommandOne, times(levelsGained)).process(any(), any(), any(), any());
        verify(levelUpCommandTwo, times(levelsGained)).process(any(), any(), any(), any());
        verify(levelUpCommandThree, times(levelsGained)).process(any(), any(), any(), any());
        // THEN the command should have executed
        verify(levelUpCommandOne, times(2)).executeCommand(any(McMMOPlayer.class));
        verify(levelUpCommandTwo, times(1)).executeCommand(any(McMMOPlayer.class));
        verify(levelUpCommandThree, times(1)).executeCommand(any(McMMOPlayer.class));
        // verify that Bukkit.dispatchCommand got executed at least 20 times with the correct injectedCommand
        mockedBukkit.verify(() -> Bukkit.dispatchCommand(any(), eq(expectedStr)), atLeast(3));
    }

    @Test
    void addLevelsShouldRunCommandFiveTimesWithPlaceholdersForLevel() {
        // GIVEN level up command for Mining should always execute for Mining level up
        assert mcMMO.p.getLevelUpCommandManager().getLevelUpCommands().isEmpty();
        assertEquals(mmoPlayer.getPlayer().getName(), playerName);

        final String commandStr = "say hello {@player}, you have reached level {@mining_level}";
        final String expectedStr1 = "say hello " + playerName + ", you have reached level 1";
        final String expectedStr2 = "say hello " + playerName + ", you have reached level 2";
        final String expectedStr3 = "say hello " + playerName + ", you have reached level 3";
        final String expectedStr4 = "say hello " + playerName + ", you have reached level 4";
        final String expectedStr5 = "say hello " + playerName + ", you have reached level 5";

        final LevelUpCommand levelUpCommand = buildLevelUpCommand(commandStr,
                (skill, ignored) -> skill == MINING);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);

        // WHEN player gains 5 levels in mining
        int levelsGained = 5;
        mmoPlayer.getProfile().addLevels(MINING, levelsGained);
        EventUtils.tryLevelChangeEvent(
                mmoPlayer.getPlayer(),
                MINING,
                levelsGained,
                mmoPlayer.getProfile().getSkillXpLevelRaw(MINING),
                true,
                XPGainReason.COMMAND);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager).applySkillLevelUp(any(), any(), any(), any());
        verify(levelUpCommand).process(any(), any(), any(), any());
        // THEN the command should have executed
        verify(levelUpCommand, times(1)).executeCommand(any(McMMOPlayer.class));
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
        assert mcMMO.p.getLevelUpCommandManager().getLevelUpCommands().isEmpty();
        final String commandStr = "say hello";
        final LevelUpCommand levelUpCommand = buildLevelUpCommand(commandStr,
                (skill, ignored) -> skill == MINING);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);

        int levelsGained = 1;
        // WHEN player gains 5 levels in mining
        McMMOPlayerLevelUpEvent event = new McMMOPlayerLevelUpEvent(mmoPlayer.getPlayer(), MINING,
                levelsGained, XPGainReason.PVE);
        selfListener.onPlayerLevelUp(event);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager).applySkillLevelUp(any(), any(), any(), any());
        verify(levelUpCommand).process(any(), any(), any(), any());
        // THEN the command should have executed
        verify(levelUpCommand).executeCommand(any(McMMOPlayer.class));
    }

    @Test
    void skillLevelUpShouldNotRunCommand() {
        // GIVEN level up command for Woodcutting should not execute for Mining level up
        assert mcMMO.p.getLevelUpCommandManager().getLevelUpCommands().isEmpty();
        final String commandStr = "say hello";
        final LevelUpCommand levelUpCommand = buildLevelUpCommand(commandStr,
                (skill, ignored) -> skill == WOODCUTTING);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);

        int levelsGained = 5;
        // WHEN player gains 5 levels in mining
        McMMOPlayerLevelUpEvent event = new McMMOPlayerLevelUpEvent(mmoPlayer.getPlayer(), MINING,
                levelsGained, XPGainReason.PVE);
        selfListener.onPlayerLevelUp(event);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager).applySkillLevelUp(any(), any(), any(), any());
        verify(levelUpCommand).process(any(), any(), any(), any());
        // THEN the command should not be run
        verify(levelUpCommand, never()).executeCommand(any(McMMOPlayer.class));
    }

    @Test
    public void levelUpShouldAlwaysRunCommand() {
        // GIVEN level up command should always execute for any level up
        assert mcMMO.p.getLevelUpCommandManager().getLevelUpCommands().isEmpty();
        final String commandStr = "say hello";
        final LevelUpCommand levelUpCommand = buildLevelUpCommand(commandStr, ALWAYS_TRUE);
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);

        // WHEN player gains 10 levels
        levelPlayerViaXP(mmoPlayer, MINING, 10);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager, atLeastOnce()).applySkillLevelUp(any(), eq(MINING), any(),
                any());
        verify(levelUpCommand, atLeastOnce()).process(any(), any(), any(), any());
        // THEN the command should have executed
        verify(levelUpCommand, times(10)).executeCommand(any(McMMOPlayer.class));
    }

    @Test
    public void skillLevelUpShouldRunPowerlevelCommandOnce() {
        // GIVEN level up command for power level should always execute for any level up
        assert mcMMO.p.getLevelUpCommandManager().getLevelUpCommands().isEmpty();
        final String commandStr = "say hello";
        final LevelUpCommand powerLevelUpCommand = buildLevelUpCommand(commandStr,
                (ignoredA, ignoredB) -> true, (powerlevel) -> powerlevel == 3);
        mcMMO.p.getLevelUpCommandManager().registerCommand(powerLevelUpCommand);

        // WHEN player gains 5 levels
        levelPlayerViaXP(mmoPlayer, MINING, 5);

        // THEN the command should be checked for execution
        verify(levelUpCommandManager, atLeastOnce()).applySkillLevelUp(any(), any(), any(), any());
        verify(powerLevelUpCommand, atLeastOnce()).process(any(), any(), any(), any());

        // THEN the command should have executed
        verify(powerLevelUpCommand, times(1)).executeCommand(any(McMMOPlayer.class));
    }

    private LevelUpCommand buildLevelUpCommand(@NotNull String commandStr,
            @NotNull List<BiPredicate<PrimarySkillType, Integer>> conditions,
            @Nullable Predicate<Integer> powerLevelCondition) {
        requireNonNull(commandStr, "commandStr cannot be null");
        requireNonNull(conditions, "conditions cannot be null");
        final var builder = new LevelUpCommandBuilder();
        if (powerLevelCondition != null) {
            builder.withPowerLevelCondition(powerLevelCondition);
        }
        builder.command(commandStr)
                .withConditions(conditions)
                .withLogInfo(true);
        return Mockito.spy(builder.build());
    }

    private LevelUpCommand buildLevelUpCommand(@NotNull String commandStr,
            @NotNull List<BiPredicate<PrimarySkillType, Integer>> conditions) {
        return buildLevelUpCommand(commandStr, conditions, null);
    }

    private LevelUpCommand buildLevelUpCommand(@NotNull String commandStr,
            @NotNull BiPredicate<PrimarySkillType, Integer> predicate,
            @Nullable Predicate<Integer> powerLevelCondition) {
        requireNonNull(commandStr, "commandStr cannot be null");
        requireNonNull(predicate, "predicate cannot be null");
        final var builder = new LevelUpCommandBuilder();
        if (powerLevelCondition != null) {
            builder.withPowerLevelCondition(powerLevelCondition);
        }
        builder.command(commandStr)
                .withPredicate(predicate)
                .withLogInfo(true);
        return Mockito.spy(builder.build());
    }

    private LevelUpCommand buildLevelUpCommand(@NotNull String commandStr,
            @NotNull BiPredicate<PrimarySkillType, Integer> predicate) {
        return buildLevelUpCommand(commandStr, predicate, null);
    }

    private void levelPlayerViaXP(@NotNull McMMOPlayer mmoPlayer, @NotNull PrimarySkillType skill,
            int levelsGained) {
        System.out.println("Leveling " + mmoPlayer.getPlayer().getName() + " up " + levelsGained
                + " levels in " + skill.getName());
        assertEquals(0, mmoPlayer.getSkillLevel(skill));
        for (int i = 0; i < levelsGained; i++) {
            mmoPlayer.applyXpGain(skill, mmoPlayer.getProfile().getXpToLevel(skill),
                    XPGainReason.COMMAND, XPGainSource.COMMAND);
        }
        assertEquals(levelsGained, mmoPlayer.getSkillLevel(skill));
    }
}