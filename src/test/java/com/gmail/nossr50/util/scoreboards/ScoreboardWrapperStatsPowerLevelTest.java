package com.gmail.nossr50.util.scoreboards;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.scoreboards.backend.PlayerBoard;
import com.gmail.nossr50.util.scoreboards.backend.SidebarLine;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Covers the power level line on the stats sidebar. Skill rows are already filtered by skill
 * permission, so the power level line must be the sum of the rows that are actually shown;
 * otherwise a player who lost a skill permission (for example per-world permissions) sees a
 * power level that includes skills the board itself hides, which also disagrees with the
 * permission-aware total used by chat stats and the power level cap.
 */
class ScoreboardWrapperStatsPowerLevelTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(
            ScoreboardWrapperStatsPowerLevelTest.class.getName());

    private RecordingPlayerBoard playerBoard;

    /** Captures the rows pushed to the backend so assertions can inspect the rendered board. */
    private static final class RecordingPlayerBoard implements PlayerBoard {
        private List<SidebarLine> lastDrawnLines = List.of();

        @Override
        public @Nullable Scoreboard show() {
            return null;
        }

        @Override
        public void hide(@NotNull Player targetPlayer, @Nullable Scoreboard targetBoard) {
        }

        @Override
        public boolean isShown() {
            return false;
        }

        @Override
        public void setTitle(@NotNull String displayName) {
        }

        @Override
        public void draw(@NotNull List<SidebarLine> lines) {
            lastDrawnLines = List.copyOf(lines);
        }

        @Override
        public void close() {
        }
    }

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        when(player.getName()).thenReturn("testPlayer");
        when(server.getPlayerExact("testPlayer")).thenReturn(player);
        playerBoard = new RecordingPlayerBoard();
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void statsBoardPowerLevelShouldMatchSkillTotalWhenLeveledSkillsArePermitted() {
        // Given - a player permitted to use the two skills they have levels in
        when(Permissions.skillEnabled(any(Player.class), any(PrimarySkillType.class)))
                .thenReturn(false);
        when(Permissions.skillEnabled(player, PrimarySkillType.MINING)).thenReturn(true);
        when(Permissions.skillEnabled(player, PrimarySkillType.HERBALISM)).thenReturn(true);
        mmoPlayer.modifySkill(PrimarySkillType.MINING, 600);
        mmoPlayer.modifySkill(PrimarySkillType.HERBALISM, 400);

        // When - the self stats board is rendered
        final ScoreboardWrapper wrapper = new ScoreboardWrapper(player, playerBoard);
        wrapper.setTypeSelfStats();

        // Then - both skill rows are shown and the power level line is their sum
        assertThat(skillRowValue(PrimarySkillType.MINING)).isEqualTo(600);
        assertThat(skillRowValue(PrimarySkillType.HERBALISM)).isEqualTo(400);
        assertThat(powerLevelLineValue()).isEqualTo(1000);
    }

    @Test
    void statsBoardPowerLevelShouldExcludeSkillsHiddenByMissingPermission() {
        // Given - the player lacks the herbalism skill permission but has levels in it,
        // mirroring per-world permission setups that disable a skill in one world
        when(Permissions.skillEnabled(any(Player.class), any(PrimarySkillType.class)))
                .thenReturn(false);
        when(Permissions.skillEnabled(player, PrimarySkillType.MINING)).thenReturn(true);
        mmoPlayer.modifySkill(PrimarySkillType.MINING, 600);
        mmoPlayer.modifySkill(PrimarySkillType.HERBALISM, 400);

        // When - the self stats board is rendered
        final ScoreboardWrapper wrapper = new ScoreboardWrapper(player, playerBoard);
        wrapper.setTypeSelfStats();

        // Then - the herbalism row is hidden and the power level line matches the shown rows
        assertThat(findSkillRow(PrimarySkillType.HERBALISM)).isNull();
        assertThat(skillRowValue(PrimarySkillType.MINING)).isEqualTo(600);
        assertThat(powerLevelLineValue()).isEqualTo(600);
    }

    /**
     * Gotcha coverage: with every skill permitted (the default server setup) the board has 17
     * skill rows plus the power level line, which exceeds the 15-line sidebar limit. The power
     * level line must survive that truncation - on the old score-sorted Bukkit board it always
     * ranked first because the sum can never be smaller than any single row.
     */
    @Test
    void statsBoardShouldStillShowPowerLevelLineWhenAllSkillRowsExceedTheLineLimit() {
        // Given - a player permitted to use every skill, with levels in two of them
        when(Permissions.skillEnabled(any(Player.class), any(PrimarySkillType.class)))
                .thenReturn(true);
        mmoPlayer.modifySkill(PrimarySkillType.MINING, 600);
        mmoPlayer.modifySkill(PrimarySkillType.HERBALISM, 400);

        // When - the self stats board is rendered
        final ScoreboardWrapper wrapper = new ScoreboardWrapper(player, playerBoard);
        wrapper.setTypeSelfStats();

        // Then - the board is capped at 15 lines and the power level line is one of them
        assertThat(playerBoard.lastDrawnLines).hasSizeLessThanOrEqualTo(15);
        assertThat(skillRowValue(PrimarySkillType.MINING)).isEqualTo(600);
        assertThat(powerLevelLineValue()).isEqualTo(1000);
    }

    private int powerLevelLineValue() {
        return playerBoard.lastDrawnLines.stream()
                .filter(line -> line.label().equals(ScoreboardManager.LABEL_POWER_LEVEL))
                .findFirst().orElseThrow().value();
    }

    private @Nullable SidebarLine findSkillRow(PrimarySkillType skill) {
        final String label = ScoreboardManager.skillLabels.get(skill);
        return playerBoard.lastDrawnLines.stream()
                .filter(line -> line.label().equals(label))
                .findFirst().orElse(null);
    }

    private int skillRowValue(PrimarySkillType skill) {
        final SidebarLine row = findSkillRow(skill);
        assertThat(row).isNotNull();
        return row.value();
    }
}
