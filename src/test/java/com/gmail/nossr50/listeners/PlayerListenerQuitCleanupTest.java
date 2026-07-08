package com.gmail.nossr50.listeners;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.mcMMO;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerListenerQuitCleanupTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(
            PlayerListenerQuitCleanupTest.class.getName());

    private PlayerListener playerListener;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);
        playerListener = new PlayerListener(mcMMO.p);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    /**
     * Regression coverage for the fishing hand tracker: entries were never removed, so the map
     * grew by one entry per player that ever fished until server restart.
     */
    @Test
    void quittingShouldForgetTrackedFishingHand() throws Exception {
        // Given - the player's fishing hand was tracked during a fishing event
        trackedFishingHands().put(playerUUID, EquipmentSlot.HAND);

        // When - the player quits
        playerListener.onPlayerQuit(new PlayerQuitEvent(player, "quit"));

        // Then - the tracked hand entry is gone
        assertThat(trackedFishingHands()).isEmpty();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, EquipmentSlot> trackedFishingHands() throws Exception {
        final Field field = PlayerListener.class.getDeclaredField("fishingHandsByPlayer");
        field.setAccessible(true);
        return (Map<UUID, EquipmentSlot>) field.get(playerListener);
    }
}
