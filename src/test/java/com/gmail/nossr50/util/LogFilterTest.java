package com.gmail.nossr50.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.mcMMO;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.jupiter.api.Test;

class LogFilterTest {

    private LogFilter newFilter(boolean verboseLogging) {
        final mcMMO plugin = mock(mcMMO.class);
        final FileConfiguration config = mock(FileConfiguration.class);
        when(plugin.getConfig()).thenReturn(config);
        when(config.getBoolean("General.Verbose_Logging")).thenReturn(verboseLogging);
        return new LogFilter(plugin);
    }

    private LogRecord record(String message) {
        return new LogRecord(Level.INFO, message);
    }

    @Test
    void debugMessagesShouldBeHiddenWhenVerboseLoggingIsOff() {
        // Given - verbose logging is off
        // When / Then - a debug-prefixed message is filtered out
        assertThat(newFilter(false).isLoggable(record(LogUtils.DEBUG_STR + "debug detail")))
                .isFalse();
    }

    @Test
    void debugMessagesShouldBeLoggableWhenVerboseLoggingIsOn() {
        // Given - verbose logging is on
        // When / Then - a debug-prefixed message is logged
        assertThat(newFilter(true).isLoggable(record(LogUtils.DEBUG_STR + "debug detail")))
                .isTrue();
    }

    /**
     * Regression coverage: the filter used contains(), so ordinary messages that merely mention
     * the debug marker anywhere (such as player names) were silently hidden.
     */
    @Test
    void ordinaryMessagesContainingTheDebugMarkerShouldBeLoggable() {
        // Given - verbose logging is off
        // When / Then - a normal message containing the marker mid-string is still logged
        assertThat(newFilter(false).isLoggable(record("User [D] Dave joined the game")))
                .isTrue();
    }

    /** Regression coverage: log records may carry a null message, which crashed the filter. */
    @Test
    void nullMessagesShouldBeLoggableInsteadOfCrashing() {
        // Given - verbose logging is off
        // When / Then - a null message is logged without an exception
        assertThatCode(() -> assertThat(newFilter(false).isLoggable(record(null))).isTrue())
                .doesNotThrowAnyException();
    }
}
