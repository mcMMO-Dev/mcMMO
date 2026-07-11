package com.gmail.nossr50.skills.alchemy;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.runnables.skills.AlchemyBrewTask;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Covers the plugin-disable safety net: every brew that is still in flight must be finished
 * immediately so a reload does not leave brewing stands mid-brew with vanilla results.
 */
class AlchemyTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(AlchemyTest.class.getName());

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);
    }

    @AfterEach
    void tearDown() {
        Alchemy.brewingStandMap.clear();
        cleanUpStaticMocks();
    }

    @Test
    void finishAllBrewsShouldFinishEveryTrackedBrew() {
        // Given - two brewing stands with active brew tasks
        final AlchemyBrewTask firstBrew = mock(AlchemyBrewTask.class);
        final AlchemyBrewTask secondBrew = mock(AlchemyBrewTask.class);
        Alchemy.brewingStandMap.put(mock(Location.class), firstBrew);
        Alchemy.brewingStandMap.put(mock(Location.class), secondBrew);

        // When - all brews are finished (plugin disable)
        Alchemy.finishAllBrews();

        // Then - each tracked brew is finished immediately
        verify(firstBrew).finishImmediately();
        verify(secondBrew).finishImmediately();
    }

    @Test
    void finishAllBrewsWithNoTrackedBrewsShouldBeANoOp() {
        // Given - no active brews
        // When - all brews are finished
        Alchemy.finishAllBrews();

        // Then - the tracking map stays empty and nothing blows up
        assertThat(Alchemy.brewingStandMap).isEmpty();
    }
}
