package com.gmail.nossr50.placeholders;

import org.jetbrains.annotations.Nullable;

/**
 * Shared parser for leaderboard placeholders that accept a {@code :<position>} suffix.
 */
final class LeaderboardPlaceholderInputParser {
    private LeaderboardPlaceholderInputParser() {
    }

    /**
     * Parses a positive integer position.
     *
     * @param params Placeholder parameter segment after ':'.
     * @return Parsed rank position, or {@code -1} when invalid.
     */
    static int parsePositivePositionOrInvalid(@Nullable String params) {
        if (params == null || params.isBlank()) {
            return -1;
        }

        try {
            final int parsed = Integer.parseInt(params);
            return parsed > 0 ? parsed : -1;
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }
}
