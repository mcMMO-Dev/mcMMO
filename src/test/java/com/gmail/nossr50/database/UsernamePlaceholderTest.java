package com.gmail.nossr50.database;

import static com.gmail.nossr50.database.UsernamePlaceholder.INVALID_OLD_USERNAME;
import static com.gmail.nossr50.database.UsernamePlaceholder.LEGACY_FLATFILE_INVALID_OLD_USERNAME;
import static com.gmail.nossr50.database.UsernamePlaceholder.isInvalidOldUsername;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Every lookup by name refuses the placeholder through this check, so it has to accept every
 * form a stored row or a typed command can hold, and nothing else.
 */
class UsernamePlaceholderTest {

    /** Both spellings in the case they are stored in, and in lower case as a command may. */
    static Stream<String> placeholderSpellings() {
        return Stream.of(INVALID_OLD_USERNAME, INVALID_OLD_USERNAME.toLowerCase(Locale.ROOT),
                LEGACY_FLATFILE_INVALID_OLD_USERNAME,
                LEGACY_FLATFILE_INVALID_OLD_USERNAME.toLowerCase(Locale.ROOT));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("placeholderSpellings")
    void placeholderShouldBeRecognizedInEitherSpellingAndAnyCase(String placeholder) {
        // Given - a name that is the placeholder

        // When - it is checked
        final boolean recognized = isInvalidOldUsername(placeholder);

        // Then - it is the placeholder
        assertThat(recognized).isTrue();
    }

    /** Near misses must stay usable as lookups, however unlikely they are as player names. */
    @ParameterizedTest(name = "\"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {"nossr50", "_INVALID_OLD_USERNAME", "INVALID_OLD_USERNAME_",
            "_INVALID_OLD_USERNAME_''", " _INVALID_OLD_USERNAME_"})
    void otherNamesShouldNotBeThePlaceholder(String playerName) {
        // Given - a name that is not the placeholder

        // When - it is checked
        final boolean recognized = isInvalidOldUsername(playerName);

        // Then - it is not the placeholder
        assertThat(recognized).isFalse();
    }
}
