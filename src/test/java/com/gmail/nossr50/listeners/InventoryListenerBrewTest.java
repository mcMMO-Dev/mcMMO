package com.gmail.nossr50.listeners;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.events.fake.FakeBrewEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.skills.AlchemyBrewTask;
import com.gmail.nossr50.skills.alchemy.Alchemy;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.BrewerInventory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Covers the vanilla-brew race handler: when vanilla brewing completes before mcMMO's own
 * brew task, the vanilla event must be cancelled and the mcMMO brew finished in its place -
 * but mcMMO's own fake brew events must never be treated as vanilla brews.
 */
class InventoryListenerBrewTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(InventoryListenerBrewTest.class.getName());

    private InventoryListener inventoryListener;
    private Block standBlock;
    private Location standLocation;
    private BrewerInventory brewerInventory;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);
        inventoryListener = new InventoryListener(mcMMO.p);

        standBlock = mock(Block.class);
        standLocation = new Location(world, 12, 64, -7);
        when(standBlock.getLocation()).thenReturn(standLocation);
        when(standBlock.getWorld()).thenReturn(world);
        brewerInventory = mock(BrewerInventory.class);
    }

    @AfterEach
    void tearDown() {
        Alchemy.brewingStandMap.clear();
        cleanUpStaticMocks();
    }

    @Test
    void vanillaBrewOverAnActiveMcMMOBrewShouldBeCancelledAndFinishedByMcMMO() {
        // Given - a stand with an mcMMO brew in flight
        final AlchemyBrewTask activeBrew = mock(AlchemyBrewTask.class);
        Alchemy.brewingStandMap.put(standLocation, activeBrew);
        final BrewEvent event = new BrewEvent(standBlock, brewerInventory, new ArrayList<>(), 3);

        // When - vanilla brewing beats the mcMMO task to completion
        inventoryListener.onBrew(event);

        // Then - the vanilla brew is cancelled and the mcMMO brew finishes instead
        assertThat(event.isCancelled()).isTrue();
        verify(activeBrew).finishImmediately();
    }

    @Test
    void vanillaBrewWithoutATrackedBrewShouldBeLeftAlone() {
        // Given - a stand mcMMO is not brewing on
        final BrewEvent event = new BrewEvent(standBlock, brewerInventory, new ArrayList<>(), 3);

        // When - vanilla brewing completes
        inventoryListener.onBrew(event);

        // Then - the vanilla brew proceeds untouched
        assertThat(event.isCancelled()).isFalse();
    }

    @Test
    void mcMMOsOwnFakeBrewEventShouldBeIgnored() {
        // Given - a stand with an mcMMO brew in flight and mcMMO's own fake brew event
        final AlchemyBrewTask activeBrew = mock(AlchemyBrewTask.class);
        Alchemy.brewingStandMap.put(standLocation, activeBrew);
        final FakeBrewEvent event =
                new FakeBrewEvent(standBlock, brewerInventory, new ArrayList<>(), 3);

        // When - the fake event reaches the handler
        inventoryListener.onBrew(event);

        // Then - the handler does not re-finish the brew or cancel its own event
        assertThat(event.isCancelled()).isFalse();
        verify(activeBrew, never()).finishImmediately();
    }
}
