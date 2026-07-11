package com.gmail.nossr50.datatypes.database;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * The top rows of every leaderboard scope (each non-child skill plus the power level
 * leaderboard), read in one bulk operation.
 *
 * @param skillLeaderboards Top rows per non-child skill, ordered leader-first.
 * @param powerLevels Top rows of the power level (overall) leaderboard, ordered leader-first.
 */
public record LeaderboardSnapshot(
        @NotNull Map<PrimarySkillType, List<PlayerStat>> skillLeaderboards,
        @NotNull List<PlayerStat> powerLevels) {
    public LeaderboardSnapshot {
        skillLeaderboards = Map.copyOf(skillLeaderboards);
        powerLevels = List.copyOf(powerLevels);
    }
}
