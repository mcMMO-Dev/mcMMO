package com.gmail.nossr50.placeholders;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LeaderboardPlaceholderInputParserTest {
    @Test
    void parsePositivePositionOrInvalidShouldReturnPositiveNumbers() {
        // Given - valid positive position strings.
        // When - parsing the placeholder position input.
        // Then - the parser should return the corresponding positive integers.
        assertThat(LeaderboardPlaceholderInputParser.parsePositivePositionOrInvalid("1")).isEqualTo(
                1);
        assertThat(
                LeaderboardPlaceholderInputParser.parsePositivePositionOrInvalid("25")).isEqualTo(
                25);
    }

    @Test
    void parsePositivePositionOrInvalidShouldRejectInvalidInput() {
        // Given - invalid position strings.
        // When - parsing the placeholder position input.
        // Then - the parser should return -1 for every invalid case.
        assertThat(
                LeaderboardPlaceholderInputParser.parsePositivePositionOrInvalid(null)).isEqualTo(
                -1);
        assertThat(LeaderboardPlaceholderInputParser.parsePositivePositionOrInvalid("")).isEqualTo(
                -1);
        assertThat(
                LeaderboardPlaceholderInputParser.parsePositivePositionOrInvalid("  ")).isEqualTo(
                -1);
        assertThat(LeaderboardPlaceholderInputParser.parsePositivePositionOrInvalid("0")).isEqualTo(
                -1);
        assertThat(
                LeaderboardPlaceholderInputParser.parsePositivePositionOrInvalid("-4")).isEqualTo(
                -1);
        assertThat(
                LeaderboardPlaceholderInputParser.parsePositivePositionOrInvalid("abc")).isEqualTo(
                -1);
    }
}
