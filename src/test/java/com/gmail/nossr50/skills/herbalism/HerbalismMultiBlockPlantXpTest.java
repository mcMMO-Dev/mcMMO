package com.gmail.nossr50.skills.herbalism;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.skills.PlantCollapseXpTask;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.block.BlockBreakEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * Covers XP rewards for multi-block plants (bamboo, sugar cane, kelp, cactus, chorus, hanging
 * vines). Vanilla only destroys the connected plant blocks via scheduled block ticks on later
 * ticks, so mcMMO must not pay XP for those blocks until they have actually broken. Paying at
 * break time allows an infinite XP exploit: break a middle segment, get paid for the whole
 * column, replace the segment before the scheduled tick runs, repeat (GitHub issue #5311).
 */
class HerbalismMultiBlockPlantXpTest extends MMOTestEnvironment {
    private static final java.util.logging.Logger logger =
            getLogger(HerbalismMultiBlockPlantXpTest.class.getName());

    private static final int XP_PER_BLOCK = 10;
    private static final int MAX_VERIFICATION_RUNS = 64;

    private HerbalismManager herbalismManager;
    private FoliaLib foliaLib;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);

        foliaLib = mock(FoliaLib.class, Mockito.RETURNS_DEEP_STUBS);
        when(mcMMO.p.getFoliaLib()).thenReturn(foliaLib);
        when(world.isChunkLoaded(anyInt(), anyInt())).thenReturn(true);
        when(world.getUID()).thenReturn(UUID.randomUUID());
        when(ExperienceConfig.getInstance()
                .getXp(eq(PrimarySkillType.HERBALISM), any(Material.class)))
                .thenReturn(XP_PER_BLOCK);

        herbalismManager = new HerbalismManager(mmoPlayer);
        // XP application is observed through the spy; the real pipeline needs a live server
        doNothing().when(mmoPlayer)
                .beginXpGain(any(PrimarySkillType.class), anyFloat(), any(), any());
    }

    @AfterEach
    void tearDown() {
        PlantCollapseXpTask.clearPendingVerifications();
        cleanUpStaticMocks();
    }

    @ParameterizedTest(name = "{0} column of {2} never breaks -> only origin XP")
    @MethodSource("verticalPlantColumns")
    void columnXpShouldNotBePaidWhenColumnNeverBreaks(Material plantType, BlockFace growthFace,
            int columnLength) {
        // Given - a natural multi-block plant column whose connected blocks never actually
        // break (the exploit: the broken segment is replaced before vanilla's scheduled tick)
        final MockPlantColumn column = mockVerticalPlantColumn(plantType, growthFace,
                columnLength);

        // When - the origin block is broken and the settle window fully elapses with every
        // connected block still present
        herbalismManager.processHerbalismBlockBreakEvent(column.event);
        driveScheduledVerification();

        // Then - only the origin block pays XP; the surviving column pays nothing
        assertThat(totalHerbalismXpPaid()).isEqualTo(XP_PER_BLOCK);
    }

    @ParameterizedTest(name = "{0} column of {2} collapses -> full XP")
    @MethodSource("verticalPlantColumnsWithCollapseMaterial")
    void columnXpShouldBePaidOnceColumnActuallyBreaks(Material plantType, BlockFace growthFace,
            int columnLength, Material collapseMaterial) {
        // Given - a natural multi-block plant column that genuinely collapses after the break
        // (kelp positions turn to water rather than air)
        final MockPlantColumn column = mockVerticalPlantColumn(plantType, growthFace,
                columnLength);

        // When - the origin block is broken and the column collapses during the settle window
        herbalismManager.processHerbalismBlockBreakEvent(column.event);
        column.collapse(collapseMaterial);
        driveScheduledVerification();

        // Then - every plant block pays XP, exactly as a legitimate harvest always has
        assertThat(totalHerbalismXpPaid()).isEqualTo((columnLength + 1) * XP_PER_BLOCK);
    }

    @Test
    void cactusColumnShouldNotPayXpWhenColumnNeverBreaks() {
        // Given - a natural three-tall cactus whose upper blocks never actually break
        final MockPlantColumn column = mockCactusColumn(2);

        // When - the bottom block is broken and the settle window elapses with the rest intact
        herbalismManager.processHerbalismBlockBreakEvent(column.event);
        driveScheduledVerification();

        // Then - only the broken block pays XP
        assertThat(totalHerbalismXpPaid()).isEqualTo(XP_PER_BLOCK);
    }

    @Test
    void cactusColumnShouldPayFullXpOnceColumnActuallyBreaks() {
        // Given - a natural three-tall cactus that genuinely collapses after the break
        final MockPlantColumn column = mockCactusColumn(2);

        // When - the bottom block is broken and the rest pops during the settle window
        herbalismManager.processHerbalismBlockBreakEvent(column.event);
        column.collapse(Material.AIR);
        driveScheduledVerification();

        // Then - all three cactus blocks pay XP
        assertThat(totalHerbalismXpPaid()).isEqualTo(3 * XP_PER_BLOCK);
    }

    @Test
    void chorusPlantShouldPayFullXpOnceTreeActuallyBreaks() {
        // Given - a chorus plant with one connected block above the broken origin
        final MockPlantColumn column = mockChorusPair();

        // When - the origin is broken and the connected block pops during the settle window
        herbalismManager.processHerbalismBlockBreakEvent(column.event);
        column.collapse(Material.AIR);
        driveScheduledVerification();

        // Then - both chorus blocks pay XP
        assertThat(totalHerbalismXpPaid()).isEqualTo(2 * XP_PER_BLOCK);
    }

    @Test
    void rapidRebreakShouldNotClaimColumnBlocksThatAreAlreadyPending() {
        // Given - a bamboo column already awaiting collapse verification from a first break
        final MockPlantColumn column = mockVerticalPlantColumn(Material.BAMBOO, BlockFace.UP, 3);
        herbalismManager.processHerbalismBlockBreakEvent(column.event);

        // When - the replaced origin is broken again before the column resolves, and the
        // column then genuinely collapses
        herbalismManager.processHerbalismBlockBreakEvent(column.event);
        column.collapse(Material.AIR);
        driveScheduledVerification();

        // Then - both breaks pay origin XP, but the column pays exactly once
        assertThat(totalHerbalismXpPaid()).isEqualTo(2 * XP_PER_BLOCK + 3 * XP_PER_BLOCK);
    }

    @Test
    void directBreakOfPendingBlockShouldRevokeItsClaimAndNeverPayTwice() {
        // Given - a bamboo column awaiting collapse verification
        final MockPlantColumn column = mockVerticalPlantColumn(Material.BAMBOO, BlockFace.UP, 2);
        herbalismManager.processHerbalismBlockBreakEvent(column.event);

        // And - the first pending column block, which can act as the origin of its own break
        final MockBlock pendingBlock = column.columnBlocks.get(0);
        final MockBlock blockAbovePending = column.columnBlocks.get(1);
        when(pendingBlock.block().getRelative(BlockFace.UP, 0))
                .thenReturn(pendingBlock.block());
        when(pendingBlock.block().getRelative(BlockFace.UP, 1))
                .thenReturn(blockAbovePending.block());
        when(pendingBlock.block().getRelative(BlockFace.UP, 2))
                .thenReturn(column.beyondColumn.block());

        // When - a new break event breaks that pending block directly, then the rest collapses
        herbalismManager.processHerbalismBlockBreakEvent(mockBreakEventFor(pendingBlock.block()));
        column.collapse(Material.AIR);
        driveScheduledVerification();

        // Then - each block pays exactly once: the two event origins immediately, the last
        // column block through verification; the revoked claim pays nothing
        assertThat(totalHerbalismXpPaid()).isEqualTo(3 * XP_PER_BLOCK);
    }

    @Test
    void tallPlantXpLimitShouldSpanOriginAndCollapsedColumn() {
        // Given - the tall-plant XP limit is enabled (20 blocks worth of XP for bamboo) and an
        // unnaturally tall bamboo column of 26 total blocks
        when(ExperienceConfig.getInstance().limitXPOnTallPlants()).thenReturn(true);
        final MockPlantColumn column = mockVerticalPlantColumn(Material.BAMBOO, BlockFace.UP, 25);

        // When - the column genuinely collapses after the break
        herbalismManager.processHerbalismBlockBreakEvent(column.event);
        column.collapse(Material.AIR);
        driveScheduledVerification();

        // Then - the combined immediate and verified XP is capped at the limit
        assertThat(totalHerbalismXpPaid()).isEqualTo(20 * XP_PER_BLOCK);
    }

    @Test
    void placedColumnBlockShouldBeUnmarkedAndUnpaidWhenItActuallyBreaks() {
        // Given - a bamboo column whose middle block was placed by a player
        final MockPlantColumn column = mockVerticalPlantColumn(Material.BAMBOO, BlockFace.UP, 3);
        final MockBlock placedBlock = column.columnBlocks.get(1);
        when(chunkManager.isIneligible(placedBlock.block().getState())).thenReturn(true);

        // When - the column genuinely collapses after the break
        herbalismManager.processHerbalismBlockBreakEvent(column.event);
        column.collapse(Material.AIR);
        driveScheduledVerification();

        // Then - the placed block pays nothing but its position is marked natural again
        assertThat(totalHerbalismXpPaid()).isEqualTo(3 * XP_PER_BLOCK);
        verify(chunkManager).setEligible(placedBlock.block());
    }

    @Test
    void placedColumnBlockShouldStayMarkedWhenColumnSurvives() {
        // Given - a bamboo column whose middle block was placed by a player
        final MockPlantColumn column = mockVerticalPlantColumn(Material.BAMBOO, BlockFace.UP, 3);
        final MockBlock placedBlock = column.columnBlocks.get(1);
        when(chunkManager.isIneligible(placedBlock.block().getState())).thenReturn(true);

        // When - the column survives the settle window (the exploit pattern)
        herbalismManager.processHerbalismBlockBreakEvent(column.event);
        driveScheduledVerification();

        // Then - the block is still in the world, so it stays marked as player-placed
        verify(chunkManager, never()).setEligible(placedBlock.block());
    }

    @Test
    void columnXpShouldNotBePaidWhenChunkUnloadsBeforeVerification() {
        // Given - a bamboo column awaiting collapse verification
        final MockPlantColumn column = mockVerticalPlantColumn(Material.BAMBOO, BlockFace.UP, 3);
        herbalismManager.processHerbalismBlockBreakEvent(column.event);

        // When - the chunk unloads before anything can be verified, even though the column
        // does collapse
        when(world.isChunkLoaded(anyInt(), anyInt())).thenReturn(false);
        column.collapse(Material.AIR);
        driveScheduledVerification();

        // Then - nothing can be verified in an unloaded chunk, so only the origin pays
        assertThat(totalHerbalismXpPaid()).isEqualTo(XP_PER_BLOCK);
    }

    @Test
    void verificationTimerShouldCancelItselfOnceResolved() {
        // Given - a bamboo column that survives the settle window
        final MockPlantColumn column = mockVerticalPlantColumn(Material.BAMBOO, BlockFace.UP, 3);

        // When - the origin is broken and the settle window fully elapses
        herbalismManager.processHerbalismBlockBreakEvent(column.event);
        final WrappedTask timerHandle = driveScheduledVerification();

        // Then - the repeating timer cancelled itself instead of polling forever
        assertThat(timerHandle).isNotNull();
        verify(timerHandle, atLeastOnce()).cancel();
    }

    /* Test scaffolding */

    private static Stream<Arguments> verticalPlantColumns() {
        return Stream.of(
                Arguments.of(Material.BAMBOO, BlockFace.UP, 3),
                Arguments.of(Material.SUGAR_CANE, BlockFace.UP, 2),
                Arguments.of(Material.KELP_PLANT, BlockFace.UP, 3),
                Arguments.of(Material.WEEPING_VINES_PLANT, BlockFace.DOWN, 2)
        );
    }

    private static Stream<Arguments> verticalPlantColumnsWithCollapseMaterial() {
        return Stream.of(
                Arguments.of(Material.BAMBOO, BlockFace.UP, 3, Material.AIR),
                Arguments.of(Material.SUGAR_CANE, BlockFace.UP, 2, Material.AIR),
                Arguments.of(Material.KELP_PLANT, BlockFace.UP, 3, Material.WATER),
                Arguments.of(Material.WEEPING_VINES_PLANT, BlockFace.DOWN, 2, Material.AIR)
        );
    }

    /**
     * A mocked plant column: the origin block that the break event targets plus the connected
     * blocks vanilla would pop on later ticks. Column block types are mutable so tests can
     * simulate the column collapsing or surviving.
     */
    private static final class MockPlantColumn {
        final BlockBreakEvent event;
        final List<MockBlock> columnBlocks;
        final MockBlock beyondColumn;

        MockPlantColumn(BlockBreakEvent event, List<MockBlock> columnBlocks,
                MockBlock beyondColumn) {
            this.event = event;
            this.columnBlocks = columnBlocks;
            this.beyondColumn = beyondColumn;
        }

        void collapse(Material collapseMaterial) {
            for (MockBlock columnBlock : columnBlocks) {
                columnBlock.typeRef().set(collapseMaterial);
            }
        }
    }

    private record MockBlock(Block block, AtomicReference<Material> typeRef) {
    }

    private MockPlantColumn mockVerticalPlantColumn(Material plantType, BlockFace growthFace,
            int columnLength) {
        final int originY = 64;
        final int yStep = growthFace == BlockFace.UP ? 1 : -1;
        final MockBlock origin = mockPlantBlock(plantType, new Location(world, 10, originY, 10));
        final List<MockBlock> columnBlocks = new ArrayList<>();

        when(origin.block().getRelative(growthFace, 0)).thenReturn(origin.block());
        for (int i = 1; i <= columnLength; i++) {
            final MockBlock columnBlock = mockPlantBlock(plantType,
                    new Location(world, 10, originY + (i * yStep), 10));
            columnBlocks.add(columnBlock);
            when(origin.block().getRelative(growthFace, i)).thenReturn(columnBlock.block());
        }
        final MockBlock beyondColumn = mockPlantBlock(Material.AIR,
                new Location(world, 10, originY + ((columnLength + 1) * yStep), 10));
        when(origin.block().getRelative(growthFace, columnLength + 1))
                .thenReturn(beyondColumn.block());

        return new MockPlantColumn(mockBreakEventFor(origin.block()), columnBlocks,
                beyondColumn);
    }

    private MockPlantColumn mockCactusColumn(int columnLength) {
        final int originY = 64;
        final MockBlock origin = mockPlantBlock(Material.CACTUS,
                new Location(world, 10, originY, 10));
        final MockBlock ground = mockPlantBlock(Material.SAND,
                new Location(world, 10, originY - 1, 10));
        final List<MockBlock> columnBlocks = new ArrayList<>();

        // Cactus traversal walks block-by-block in both directions, so each mock is linked to
        // its vertical neighbours
        MockBlock below = ground;
        MockBlock current = origin;
        MockBlock beyondColumn = null;
        for (int i = 1; i <= columnLength + 1; i++) {
            final Material aboveType = i <= columnLength ? Material.CACTUS : Material.AIR;
            final MockBlock above = mockPlantBlock(aboveType,
                    new Location(world, 10, originY + i, 10));
            if (aboveType == Material.CACTUS) {
                columnBlocks.add(above);
            } else {
                beyondColumn = above;
            }
            when(current.block().getRelative(BlockFace.UP)).thenReturn(above.block());
            when(current.block().getRelative(BlockFace.DOWN)).thenReturn(below.block());
            below = current;
            current = above;
        }
        when(current.block().getRelative(BlockFace.DOWN)).thenReturn(below.block());

        return new MockPlantColumn(mockBreakEventFor(origin.block()), columnBlocks,
                beyondColumn);
    }

    private MockPlantColumn mockChorusPair() {
        final int originY = 64;
        final MockBlock origin = mockPlantBlock(Material.CHORUS_PLANT,
                new Location(world, 10, originY, 10));
        final MockBlock above = mockPlantBlock(Material.CHORUS_PLANT,
                new Location(world, 10, originY + 1, 10));

        // The chorus traversal fans out in five directions from every visited block
        linkChorusNeighbours(origin, 10, originY, 10, above.block());
        final MockBlock beyondColumn = linkChorusNeighbours(above, 10, originY + 1, 10, null);

        return new MockPlantColumn(mockBreakEventFor(origin.block()), List.of(above),
                beyondColumn);
    }

    /**
     * Stubs the five chorus traversal directions with air neighbours, optionally overriding the
     * upward neighbour. Returns the upward air neighbour when no override was given.
     */
    private MockBlock linkChorusNeighbours(MockBlock source, int x, int y, int z,
            Block upOverride) {
        final BlockFace[] faces = {BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST,
                BlockFace.WEST};
        MockBlock upNeighbour = null;
        for (BlockFace face : faces) {
            if (face == BlockFace.UP && upOverride != null) {
                when(source.block().getRelative(BlockFace.UP, 1)).thenReturn(upOverride);
                continue;
            }
            final Location neighbourLocation = new Location(world, x + face.getModX(),
                    y + face.getModY(), z + face.getModZ());
            final MockBlock airNeighbour = mockPlantBlock(Material.AIR, neighbourLocation);
            if (face == BlockFace.UP) {
                upNeighbour = airNeighbour;
            }
            when(source.block().getRelative(face, 1)).thenReturn(airNeighbour.block());
        }
        return upNeighbour;
    }

    private MockBlock mockPlantBlock(Material initialType, Location location) {
        final AtomicReference<Material> typeRef = new AtomicReference<>(initialType);
        final Block block = mock(Block.class);
        final BlockState blockState = mock(BlockState.class);
        final BlockData blockData = mock(BlockData.class);

        when(block.getType()).thenAnswer(invocation -> typeRef.get());
        when(block.getState()).thenReturn(blockState);
        when(block.getLocation()).thenReturn(location);
        when(block.getWorld()).thenReturn(world);
        when(block.getX()).thenReturn(location.getBlockX());
        when(block.getY()).thenReturn(location.getBlockY());
        when(block.getZ()).thenReturn(location.getBlockZ());
        when(block.getBlockData()).thenReturn(blockData);
        when(blockState.getType()).thenAnswer(invocation -> typeRef.get());
        when(blockState.getBlock()).thenReturn(block);
        when(blockState.getBlockData()).thenReturn(blockData);
        when(blockState.getLocation()).thenReturn(location);
        when(blockData.getMaterial()).thenAnswer(invocation -> typeRef.get());

        return new MockBlock(block, typeRef);
    }

    private BlockBreakEvent mockBreakEventFor(Block originBlock) {
        final BlockBreakEvent event = mock(BlockBreakEvent.class);
        when(event.getBlock()).thenReturn(originBlock);
        when(event.getPlayer()).thenReturn(player);
        when(event.isCancelled()).thenReturn(false);
        return event;
    }

    /**
     * Runs any scheduled deferred-verification task through enough ticks for the settle window
     * to fully elapse. A no-op when nothing was scheduled, so exploit tests fail loudly on code
     * that pays everything up front instead of erroring here.
     *
     * @return the mocked timer handle passed to the task, or null when nothing was scheduled
     */
    @SuppressWarnings("unchecked")
    private WrappedTask driveScheduledVerification() {
        final ArgumentCaptor<Consumer<WrappedTask>> taskCaptor =
                ArgumentCaptor.forClass(Consumer.class);
        verify(foliaLib.getScheduler(), atMost(1))
                .runAtLocationTimer(any(Location.class), taskCaptor.capture(), anyLong(),
                        anyLong());

        if (taskCaptor.getAllValues().isEmpty()) {
            return null;
        }

        final Consumer<WrappedTask> verificationTask = taskCaptor.getValue();
        final WrappedTask taskHandle = mock(WrappedTask.class);
        for (int i = 0; i < MAX_VERIFICATION_RUNS; i++) {
            verificationTask.accept(taskHandle);
        }
        return taskHandle;
    }

    private float totalHerbalismXpPaid() {
        final ArgumentCaptor<Float> xpCaptor = ArgumentCaptor.forClass(Float.class);
        verify(mmoPlayer, atLeast(0)).beginXpGain(eq(PrimarySkillType.HERBALISM),
                xpCaptor.capture(), any(), any());

        float total = 0;
        for (float xp : xpCaptor.getAllValues()) {
            total += xp;
        }
        return total;
    }
}
