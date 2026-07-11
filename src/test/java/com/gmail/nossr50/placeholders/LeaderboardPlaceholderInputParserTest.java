package com.gmail.nossr50.placeholders;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class LeaderboardPlaceholderInputParserTest {
    @ParameterizedTest(name = "\"{0}\" should parse to {1}")
    @CsvSource({
            "1, 1",
            "25, 25"
    })
    void parsePositivePositionOrInvalidShouldReturnPositiveNumbers(String input, int expected) {
        // Given - a valid positive position string.
        // When - parsing the placeholder position input.
        // Then - the parser should return the corresponding positive integer.
        assertThat(LeaderboardPlaceholderInputParser.parsePositivePositionOrInvalid(input))
                .isEqualTo(expected);
    }

    /**
     * Gotcha coverage: zero, negatives, blanks, and non-numeric input must all collapse to the
     * single invalid marker so placeholder resolution can fall back to an empty result.
     */
    @ParameterizedTest(name = "invalid input \"{0}\" should return -1")
    @NullSource
    @ValueSource(strings = {"", "  ", "0", "-4", "abc"})
    void parsePositivePositionOrInvalidShouldRejectInvalidInput(String input) {
        // Given - an invalid position string.
        // When - parsing the placeholder position input.
        // Then - the parser should return -1.
        assertThat(LeaderboardPlaceholderInputParser.parsePositivePositionOrInvalid(input))
                .isEqualTo(-1);
    }
}
