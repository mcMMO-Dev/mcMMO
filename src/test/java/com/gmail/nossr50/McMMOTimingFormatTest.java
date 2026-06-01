package com.gmail.nossr50;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class McMMOTimingFormatTest {

    @Nested
    class FormatDurationHms {

        @ParameterizedTest
        @CsvSource({
            "0,0ms",
            "999000000,999ms",
            "1000000000,1s",
            "60000000000,1m",
            "3600000000000,1h",
            "3661000000000,1h 1m 1s",
            "3605000000000,1h 5s",
            "65000000000,1m 5s",
                "3726000000000,1h 2m 6s"
        })
        void formatsElapsedNanosecondsInHumanReadableForm(long elapsedNanos,
                String expectedDisplay) {
            // Given a measured elapsed duration in nanoseconds

            // When the duration formatter is invoked
            final String actualDisplay = mcMMO.formatDurationHms(elapsedNanos);

            // Then the output is formatted as human-readable hours, minutes, and seconds
            assertThat(actualDisplay).isEqualTo(expectedDisplay);
        }

        @ParameterizedTest
        @CsvSource({ "-1", "-1000000" })
        void clampsNegativeElapsedNanosecondsToZero(long elapsedNanos) {
            // Given a negative elapsed value from a bad caller

            // When the duration formatter is invoked
            final String actualDisplay = mcMMO.formatDurationHms(elapsedNanos);

            // Then the output is clamped to a zero-duration representation
            assertThat(actualDisplay).isEqualTo("0ms");
        }
    }
}
