package com.gmail.nossr50.listeners;

import static java.util.logging.Logger.getLogger;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.config.PersistentDataConfig;
import com.gmail.nossr50.mcMMO;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Chunk;
import org.bukkit.block.BlockState;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

/**
 * Covers the world lifecycle wiring: structure growth resetting placed-block eligibility, and
 * chunk/world unloads flushing their placed-block data.
 */
class WorldListenerTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(WorldListenerTest.class.getName());

    private MockedStatic<PersistentDataConfig> persistentDataConfigMock;
    private PlatformScheduler scheduler;
    private WorldListener worldListener;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);
        persistentDataConfigMock = mockStatic(PersistentDataConfig.class);
        persistentDataConfigMock.when(PersistentDataConfig::getInstance)
                .thenReturn(mock(PersistentDataConfig.class));
        final FoliaLib foliaLib = mock(FoliaLib.class);
        scheduler = mock(PlatformScheduler.class);
        when(mcMMO.p.getFoliaLib()).thenReturn(foliaLib);
        when(foliaLib.getScheduler()).thenReturn(scheduler);
        mockedMcMMO.when(mcMMO::getChunkManager).thenReturn(chunkManager);
        worldListener = new WorldListener(mcMMO.p);
    }

    @AfterEach
    void tearDown() {
        persistentDataConfigMock.close();
        cleanUpStaticMocks();
    }

    /**
     * Structure growth (trees, huge fungi) is an outcome event that must clear placed-block
     * tracking for every block the structure claims, so grown blocks reward XP again. The
     * reset runs a tick later because the grown blocks do not exist yet while the event fires.
     */
    @Test
    void structureGrowShouldMakeEveryGrownBlockEligibleOneTickLater() {
        // Given - a grown structure spanning two blocks
        final BlockState firstBlock = mock(BlockState.class);
        final BlockState secondBlock = mock(BlockState.class);
        final StructureGrowEvent event = mock(StructureGrowEvent.class);
        when(event.getWorld()).thenReturn(world);
        when(event.getBlocks()).thenReturn(List.of(firstBlock, secondBlock));

        // When - the growth is handled
        worldListener.onStructureGrow(event);

        // Then - nothing is marked until the scheduled task runs
        verify(chunkManager, never()).setEligible(any(BlockState.class));

        // And - once the scheduled task runs, every grown block becomes eligible again
        final ArgumentCaptor<Runnable> scheduledTask = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler).runLater(scheduledTask.capture(), eq(1L));
        scheduledTask.getValue().run();
        verify(chunkManager).setEligible(firstBlock);
        verify(chunkManager).setEligible(secondBlock);
    }

    @Test
    void chunkUnloadShouldFlushTheChunksPlacedBlockData() {
        // Given - an unloading chunk
        final Chunk chunk = mock(Chunk.class);
        when(chunk.getX()).thenReturn(3);
        when(chunk.getZ()).thenReturn(-7);
        final ChunkUnloadEvent event = mock(ChunkUnloadEvent.class);
        when(event.getWorld()).thenReturn(world);
        when(event.getChunk()).thenReturn(chunk);

        // When - the unload is handled
        worldListener.onChunkUnload(event);

        // Then - the chunk's placed-block data is flushed
        verify(chunkManager).chunkUnloaded(3, -7, world);
    }

    @Test
    void worldUnloadShouldFlushTheWorldsPlacedBlockData() {
        // Given - an unloading world (block tracker persistence disabled, so no backup runs)
        final WorldUnloadEvent event = mock(WorldUnloadEvent.class);
        when(event.getWorld()).thenReturn(world);

        // When - the unload is handled
        worldListener.onWorldUnload(event);

        // Then - the world's placed-block data is flushed
        verify(chunkManager).unloadWorld(world);
    }
}
