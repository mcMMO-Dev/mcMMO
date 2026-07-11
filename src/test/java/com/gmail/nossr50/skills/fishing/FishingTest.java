package com.gmail.nossr50.skills.fishing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import java.util.logging.Logger;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

public class FishingTest extends MMOTestEnvironment {
    private static final Logger LOGGER = Logger.getLogger(FishingTest.class.getName());

    private FishingManager fishingManager;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(LOGGER);

        fishingManager = Mockito.spy(new FishingManager(mmoPlayer));
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void testExploitFishingTooOften() {
        assertFalse(fishingManager.isFishingTooOften());
        assertFalse(fishingManager.lastWarned > 0);

        // Since we called the method again within a second, this will now consider the player to be fishing too often.
        assertTrue(fishingManager.isFishingTooOften());

        // Ensure that the player was warned about the exploit fishing.
        verify(player, times(1)).sendMessage(anyString());
        final long lastWarningTime = fishingManager.lastWarned;
        assertTrue(lastWarningTime > 0);

        // Still fishing too often, but make sure another warning doesn't get sent
        assertTrue(fishingManager.isFishingTooOften());
        assertEquals(lastWarningTime, fishingManager.lastWarned);
        verify(player, times(1)).sendMessage(anyString()); // still only called from the previous invocation

        // Manually decrement the last catch timestamp to simulate time passing
        fishingManager.lastFishCaughtTimestamp -= 1000;
        assertFalse(fishingManager.isFishingTooOften());
    }

    /**
     * The recorded spam verdict lets the reward-gating handler re-read the outcome of the
     * LOWEST-priority spam check without re-stamping the catch timestamp; a mutating re-check
     * would flag every catch as a repeat.
     */
    @Test
    void wasFishingTooOftenShouldMirrorTheLastSpamCheckWithoutMutatingState() {
        // Given - no spam check has run yet
        // Then - the recorded verdict starts clean
        assertThat(fishingManager.wasFishingTooOften()).isFalse();

        // When - the first catch is checked
        // Then - it is not spam and the verdict says so
        assertThat(fishingManager.isFishingTooOften()).isFalse();
        assertThat(fishingManager.wasFishingTooOften()).isFalse();

        // When - another catch is checked within a second
        // Then - it is spam and the recorded verdict flips
        assertThat(fishingManager.isFishingTooOften()).isTrue();
        assertThat(fishingManager.wasFishingTooOften()).isTrue();

        // And - re-reading the verdict leaves the catch timestamp untouched
        final long timestampAfterCheck = fishingManager.lastFishCaughtTimestamp;
        assertThat(fishingManager.wasFishingTooOften()).isTrue();
        assertThat(fishingManager.wasFishingTooOften()).isTrue();
        assertThat(fishingManager.lastFishCaughtTimestamp).isEqualTo(timestampAfterCheck);

        // When - enough time passes before the next catch is checked
        fishingManager.lastFishCaughtTimestamp -= 1000;
        // Then - the next check clears the verdict
        assertThat(fishingManager.isFishingTooOften()).isFalse();
        assertThat(fishingManager.wasFishingTooOften()).isFalse();
    }

    /**
     * Covers the same-spot overfishing detection feeding the anti-exploit handlers. Each cast
     * is compared through a bounding box around the hook, so casts landing close together must
     * count as the same spot while casts beyond the move range must reset the count.
     */
    @Nested
    class ExploitDetection {
        private static final int OVER_FISH_LIMIT = 3;
        private static final int MOVE_RANGE = 3;

        @BeforeEach
        void configureExploitPrevention() {
            when(ExperienceConfig.getInstance().getFishingExploitingOptionOverFishLimit())
                    .thenReturn(OVER_FISH_LIMIT);
            when(ExperienceConfig.getInstance().getFishingExploitingOptionMoveRange())
                    .thenReturn(MOVE_RANGE);
        }

        @Test
        void repeatCatchesAtTheSameSpotFlagExploitingAtTheLimit() {
            // Given - a player casting at the same spot repeatedly
            final Vector castSpot = new Vector(100, 64, 100);

            // When - their catches are still under the overfish limit
            fishingManager.processExploiting(castSpot);
            fishingManager.processExploiting(castSpot);

            // Then - the player is not flagged yet
            assertThat(fishingManager.isExploitingFishing()).isFalse();

            // When - the catch that reaches the limit lands
            fishingManager.processExploiting(castSpot);

            // Then - the player is flagged as exploiting
            assertThat(fishingManager.isExploitingFishing()).isTrue();
        }

        @Test
        void movingBeyondTheMoveRangeResetsTheOverfishCount() {
            // Given - a player flagged for overfishing one spot
            final Vector overfishedSpot = new Vector(100, 64, 100);
            fishingManager.processExploiting(overfishedSpot);
            fishingManager.processExploiting(overfishedSpot);
            fishingManager.processExploiting(overfishedSpot);
            assertThat(fishingManager.isExploitingFishing()).isTrue();

            // When - they move beyond the configured move range and catch again
            fishingManager.processExploiting(new Vector(110, 64, 100));

            // Then - the overfish count starts over at the new spot
            assertThat(fishingManager.isExploitingFishing()).isFalse();
        }

        /**
         * With a move range of 3 each cast spans 1.5 blocks around the hook, so two casts
         * 3 blocks apart produce boxes that only touch — and touching boxes do not overlap.
         * This pins the exact distance a player must move to escape the overfish count.
         */
        @ParameterizedTest
        @CsvSource({
                "2.9, true",
                "3.0, false",
        })
        void castsCountAsTheSameSpotOnlyWhileTheirRangesOverlap(double xOffset,
                boolean sameSpot) {
            // Given - a first cast anchoring the tracked fishing spot
            fishingManager.processExploiting(new Vector(100, 64, 100));

            // When - the next two catches land the given distance away
            final Vector nearbyCast = new Vector(100 + xOffset, 64, 100);
            fishingManager.processExploiting(nearbyCast);
            fishingManager.processExploiting(nearbyCast);

            // Then - only overlapping casts accumulate toward the overfish limit
            assertThat(fishingManager.isExploitingFishing()).isEqualTo(sameSpot);
        }

        @Test
        void playersAreWarnedOneCatchBeforeTheOverfishLimit() {
            // Given - a player catching fish at one spot
            final Vector castSpot = new Vector(100, 64, 100);
            fishingManager.processExploiting(castSpot);
            verify(player, never()).sendMessage(anyString());

            // When - the next catch puts them one catch away from the limit
            fishingManager.processExploiting(castSpot);

            // Then - the low resources tip warns them to move
            verify(player, times(1)).sendMessage(anyString());

            // And - the catch that reaches the limit does not repeat the warning
            fishingManager.processExploiting(castSpot);
            verify(player, times(1)).sendMessage(anyString());
        }

        @Test
        void makeBoundingBoxSpansTheConfiguredMoveRange() {
            // Given - a move range of 3 blocks
            // When - a bounding box is built around a cast
            final BoundingBox box = FishingManager.makeBoundingBox(new Vector(100, 64, 100));

            // Then - the box spans half the range in each horizontal direction and one block
            // vertically
            assertThat(box).isEqualTo(BoundingBox.of(new Vector(100, 64, 100), 1.5, 1, 1.5));
        }
    }
}
