package com.gmail.nossr50.listeners;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.MetadataConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class BlockListenerTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(BlockListenerTest.class.getName());

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void queuedDropsShouldBeAddedToBlockDropEventAndMetadataRemoved() {
        Block block = mock(Block.class);
        BlockState blockState = mock(BlockState.class);
        BlockDropItemEvent event = mock(BlockDropItemEvent.class);
        ItemStack queuedDrop = new ItemStack(Material.CAKE);
        Item queuedItemEntity = mock(Item.class);
        List<Item> eventItems = new ArrayList<>();

        when(event.isCancelled()).thenReturn(false);
        when(event.getBlock()).thenReturn(block);
        when(event.getBlockState()).thenReturn(blockState);
        when(event.getPlayer()).thenReturn(player);
        when(event.getItems()).thenReturn(eventItems);
        when(block.getType()).thenReturn(Material.SAND);
        when(block.getMetadata(MetadataConstants.METADATA_KEY_QUEUED_BLOCK_DROPS)).thenReturn(
                List.of(new FixedMetadataValue(mcMMO.p, List.of(queuedDrop))));
        when(block.getMetadata(MetadataConstants.METADATA_KEY_BONUS_DROPS)).thenReturn(List.of());
        when(block.hasMetadata(MetadataConstants.METADATA_KEY_QUEUED_BLOCK_DROPS)).thenReturn(
                true);
        when(block.hasMetadata(MetadataConstants.METADATA_KEY_BONUS_DROPS)).thenReturn(false);
        when(queuedItemEntity.getItemStack()).thenReturn(queuedDrop);

        try (MockedStatic<ItemUtils> mockedItemUtils = org.mockito.Mockito.mockStatic(
                ItemUtils.class)) {
            mockedItemUtils.when(() -> ItemUtils.appendBlockDropEventItem(eq(event), eq(queuedDrop)))
                    .thenAnswer(invocation -> {
                        eventItems.add(queuedItemEntity);
                        return true;
                    });

            new BlockListener(mcMMO.p).onBlockDropItemEvent(event);
        }

        assertSame(queuedItemEntity, eventItems.get(0));
        verify(block).removeMetadata(MetadataConstants.METADATA_KEY_QUEUED_BLOCK_DROPS, mcMMO.p);
        verify(queuedItemEntity, never()).remove();
    }
}
