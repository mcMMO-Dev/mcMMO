package com.gmail.nossr50.placeholders;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Resolves {@code %mcmmo_mctop_name_<skill>:<position>%} and
 * {@code %mcmmo_mctop_name_overall:<position>%}.
 */
public class McTopNamePlaceholder implements Placeholder {
    private final @Nullable PrimarySkillType skill;
    private final String overallToken;
    private final LeaderboardPlaceholderCache leaderboardCache;

    /**
     * @param skill Skill scope, or {@code null} for overall leaderboard.
     * @param leaderboardCache Shared leaderboard snapshot cache.
     */
    public McTopNamePlaceholder(@Nullable PrimarySkillType skill,
            LeaderboardPlaceholderCache leaderboardCache) {
        this(skill, "overall", leaderboardCache);
    }

    /**
     * @param skill Skill scope, or {@code null} for overall leaderboard.
     * @param overallToken Token suffix used when {@code skill == null}, for example
     *                     {@code overall}, {@code all}, or {@code powerlevel}.
     * @param leaderboardCache Shared leaderboard snapshot cache.
     */
    public McTopNamePlaceholder(@Nullable PrimarySkillType skill,
            String overallToken,
            LeaderboardPlaceholderCache leaderboardCache) {
        this.skill = skill;
        this.overallToken = overallToken;
        this.leaderboardCache = leaderboardCache;
    }

    @Override
    public String process(Player player, String params) {
        final int position = LeaderboardPlaceholderInputParser.parsePositivePositionOrInvalid(
                params);
        if (position < 0) {
            return "";
        }

        return leaderboardCache.getPlayerName(skill, position);
    }

    @Override
    public String getName() {
        if (skill == null) {
            return "mctop_name_" + overallToken;
        }

        return "mctop_name_" + skill.toString().toLowerCase();
    }
}
