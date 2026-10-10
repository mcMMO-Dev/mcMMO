package com.gmail.nossr50;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class McMMOShutdownStepTest {

    private final Logger logger = mock(Logger.class);

    @Nested
    class RunShutdownStep {

        @Test
        void laterStepsShouldRunWhenAnEarlierStepThrows() {
            // Given - a step that fails ahead of the block tracker save, as in onDisable
            final List<String> completedSteps = new ArrayList<>();

            // When - the shutdown steps run in order
            mcMMO.runShutdownStep(logger, "saving players",
                    () -> completedSteps.add("saving players"));
            mcMMO.runShutdownStep(logger, "clearing players", () -> {
                throw new IllegalStateException("entity removed off its region thread");
            });
            mcMMO.runShutdownStep(logger, "saving block tracker data",
                    () -> completedSteps.add("saving block tracker data"));

            // Then - the step after the failure still ran
            assertThat(completedSteps)
                    .containsExactly("saving players", "saving block tracker data");
        }

        @Test
        void failureShouldBeLoggedWithTheStepNameWhenAStepThrows() {
            // Given - a step that throws
            final IllegalStateException failure = new IllegalStateException("save failed");

            // When - the step runs
            mcMMO.runShutdownStep(logger, "saving parties", () -> {
                throw failure;
            });

            // Then - the failure is logged once, with the step name and the cause
            verify(logger).log(Level.SEVERE,
                    "An error occurred while disabling mcMMO! Failed step: saving parties",
                    failure);
            verifyNoMoreInteractions(logger);
        }

        @Test
        void nothingShouldBeLoggedWhenAStepSucceeds() {
            // Given - a step that completes normally
            final List<String> completedSteps = new ArrayList<>();

            // When - the step runs
            mcMMO.runShutdownStep(logger, "saving players",
                    () -> completedSteps.add("saving players"));

            // Then - the step ran and nothing was logged
            assertThat(completedSteps).containsExactly("saving players");
            verifyNoInteractions(logger);
        }
    }
}
