package com.gmail.nossr50.skills.fishing;

import com.gmail.nossr50.MMOTestEnvironment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
}
