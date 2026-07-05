package com.gmail.nossr50.placeholders;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import java.util.function.IntFunction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Resolves leaderboard-by-position placeholders backed by the shared leaderboard cache.
 * <p>
 * Value placeholders resolve {@code %mcmmo_mctop_<skill>:<position>%} and overall aliases like
 * {@code %mcmmo_mctop_overall:<position>%}, {@code %mcmmo_mctop_all:<position>%}, and
 * {@code %mcmmo_mctop_powerlevel:<position>%}. Name placeholders resolve the matching
 * {@code %mcmmo_mctop_name_...%} tokens.
 */
public class McTopPlaceholder implements Placeholder {
    private static final String DEFAULT_OVERALL_TOKEN = "overall";

    private final String name;
    private final IntFunction<String> lookup;

    private McTopPlaceholder(String name, IntFunction<String> lookup) {
        this.name = name;
        this.lookup = lookup;
    }

    /**
     * Creates a {@code mctop_<token>} placeholder resolving cached leaderboard values.
     *
     * @param skill Skill scope, or {@code null} for overall leaderboard.
     * @param leaderboardCache Shared leaderboard snapshot cache.
     */
    public static McTopPlaceholder value(@Nullable PrimarySkillType skill,
            LeaderboardPlaceholderCache leaderboardCache) {
        return value(skill, DEFAULT_OVERALL_TOKEN, leaderboardCache);
    }

    /**
     * Creates a {@code mctop_<token>} placeholder resolving cached leaderboard values.
     *
     * @param skill Skill scope, or {@code null} for overall leaderboard.
     * @param overallToken Token suffix used when {@code skill == null}, for example
     *                     {@code overall}, {@code all}, or {@code powerlevel}.
     * @param leaderboardCache Shared leaderboard snapshot cache.
     */
    public static McTopPlaceholder value(@Nullable PrimarySkillType skill, String overallToken,
            LeaderboardPlaceholderCache leaderboardCache) {
        return new McTopPlaceholder("mctop_" + token(skill, overallToken),
                position -> leaderboardCache.getValue(skill, position));
    }

    /**
     * Creates a {@code mctop_name_<token>} placeholder resolving cached player names.
     *
     * @param skill Skill scope, or {@code null} for overall leaderboard.
     * @param leaderboardCache Shared leaderboard snapshot cache.
     */
    public static McTopPlaceholder name(@Nullable PrimarySkillType skill,
            LeaderboardPlaceholderCache leaderboardCache) {
        return name(skill, DEFAULT_OVERALL_TOKEN, leaderboardCache);
    }

    /**
     * Creates a {@code mctop_name_<token>} placeholder resolving cached player names.
     *
     * @param skill Skill scope, or {@code null} for overall leaderboard.
     * @param overallToken Token suffix used when {@code skill == null}, for example
     *                     {@code overall}, {@code all}, or {@code powerlevel}.
     * @param leaderboardCache Shared leaderboard snapshot cache.
     */
    public static McTopPlaceholder name(@Nullable PrimarySkillType skill, String overallToken,
            LeaderboardPlaceholderCache leaderboardCache) {
        return new McTopPlaceholder("mctop_name_" + token(skill, overallToken),
                position -> leaderboardCache.getPlayerName(skill, position));
    }

    private static String token(@Nullable PrimarySkillType skill, String overallToken) {
        return skill == null ? overallToken : skill.toString().toLowerCase();
    }

    @Override
    public String process(Player player, String params) {
        final int position = LeaderboardPlaceholderInputParser.parsePositivePositionOrInvalid(
                params);
        if (position < 0) {
            return "";
        }

        return lookup.apply(position);
    }

    @Override
    public String getName() {
        return name;
    }
}
