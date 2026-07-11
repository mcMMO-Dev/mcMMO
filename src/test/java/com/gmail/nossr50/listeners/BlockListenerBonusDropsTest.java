package com.gmail.nossr50.listeners;

import static com.gmail.nossr50.util.MetadataConstants.METADATA_KEY_BONUS_DROPS;
import static java.util.logging.Logger.getLogger;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BlockListenerBonusDropsTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(BlockListenerBonusDropsTest.class.getName());

    private BlockListener blockListener;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);
        blockListener = new BlockListener(mcMMO.p);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    /**
     * Regression coverage for beetroot double drops (issue #4667): by the time
     * BlockDropItemEvent fires the block is already broken and its live type reads as AIR, so
     * the beetroot tolerance must be decided from the event's captured pre-break BlockState.
     * Reading the live block type silently re-disabled beetroot bonus drops, because AIR never
     * matches BEETROOTS and the two legitimate beetroot drop materials then tripped the
     * tile-entity heuristic.
     */
    @Test
    void beetrootDoubleDropsShouldApplyWhenBlockAlreadyReadsAsAir() {
        // Given - a broken beetroot crop whose live block already reads as AIR
        final Block block = mockBrokenBlock();
        final BlockState preBreakState = mock(BlockState.class);
        when(preBreakState.getType()).thenReturn(Material.BEETROOTS);

        // And - a pending bonus drop awarded by the herbalism double-drop roll
        final MetadataValue pendingBonusDrops = new FixedMetadataValue(mcMMO.p, 1);
        when(block.hasMetadata(METADATA_KEY_BONUS_DROPS)).thenReturn(true);
        when(block.getMetadata(METADATA_KEY_BONUS_DROPS)).thenReturn(List.of(pendingBonusDrops));

        // And - the crop dropping its two legitimate materials
        final Item beetrootDrop = mockDroppedItem(Material.BEETROOT, 1);
        final Item seedsDrop = mockDroppedItem(Material.BEETROOT_SEEDS, 2);
        final BlockDropItemEvent event = mockDropEvent(block, preBreakState,
                List.of(beetrootDrop, seedsDrop));

        when(generalConfig.getDoubleDropsEnabled(PrimarySkillType.HERBALISM, Material.BEETROOT))
                .thenReturn(true);
        when(generalConfig.getDoubleDropsEnabled(PrimarySkillType.HERBALISM,
                Material.BEETROOT_SEEDS)).thenReturn(true);

        // When - the drop event is handled
        blockListener.onBlockDropItemEvent(event);

        // Then - both beetroot drop stacks receive the bonus drop
        verify(beetrootDrop.getItemStack()).setAmount(2);
        verify(seedsDrop.getItemStack()).setAmount(3);
    }

    /**
     * The tile-entity heuristic must keep protecting against dupes: a non-beetroot break whose
     * drop list mixes several materials only rewards block items, so spilled container contents
     * are never doubled.
     */
    @Test
    void bonusDropsShouldStillSkipSuspectedTileEntityContents() {
        // Given - a broken stone block whose drop list also contains spilled container contents
        final Block block = mockBrokenBlock();
        final BlockState preBreakState = mock(BlockState.class);
        when(preBreakState.getType()).thenReturn(Material.STONE);

        final MetadataValue pendingBonusDrops = new FixedMetadataValue(mcMMO.p, 1);
        when(block.hasMetadata(METADATA_KEY_BONUS_DROPS)).thenReturn(true);
        when(block.getMetadata(METADATA_KEY_BONUS_DROPS)).thenReturn(List.of(pendingBonusDrops));

        final Item blockDrop = mockDroppedItem(Material.COBBLESTONE, 1);
        final Item spilledContents = mockDroppedItem(Material.COAL, 3);
        final BlockDropItemEvent event = mockDropEvent(block, preBreakState,
                List.of(blockDrop, spilledContents));

        when(generalConfig.getDoubleDropsEnabled(PrimarySkillType.MINING, Material.COBBLESTONE))
                .thenReturn(true);
        when(generalConfig.getDoubleDropsEnabled(PrimarySkillType.MINING, Material.COAL))
                .thenReturn(true);

        // When - the drop event is handled
        blockListener.onBlockDropItemEvent(event);

        // Then - only the block item is rewarded and the suspected contents stay untouched
        verify(blockDrop.getItemStack()).setAmount(2);
        verify(spilledContents.getItemStack(), never()).setAmount(anyInt());
    }

    private Block mockBrokenBlock() {
        final Block block = mock(Block.class);
        when(block.getType()).thenReturn(Material.AIR);
        return block;
    }

    private BlockDropItemEvent mockDropEvent(Block block, BlockState preBreakState,
            List<Item> drops) {
        final BlockDropItemEvent event = mock(BlockDropItemEvent.class);
        when(event.getBlock()).thenReturn(block);
        when(event.getBlockState()).thenReturn(preBreakState);
        when(event.getItems()).thenReturn(drops);
        return event;
    }

    private Item mockDroppedItem(Material material, int amount) {
        final Item item = mock(Item.class);
        final ItemStack itemStack = mock(ItemStack.class);
        when(item.getItemStack()).thenReturn(itemStack);
        when(itemStack.getType()).thenReturn(material);
        when(itemStack.getAmount()).thenReturn(amount);
        when(itemStack.getMaxStackSize()).thenReturn(64);
        return item;
    }
}
