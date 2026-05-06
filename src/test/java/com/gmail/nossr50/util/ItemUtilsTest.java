package com.gmail.nossr50.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ItemUtilsTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(ItemUtilsTest.class.getName());

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        ItemUtils.clearCompatibilityRoutingCache();
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void craftBukkitStylePlainSeedRemovalDoesNotMatchRenamedSeedStack() {
        // CraftInventory.removeItem(ItemStack...) matches with ItemStack.isSimilar rather than
        // with the broader material-only check used by Inventory.contains(Material).
        final ItemStack renamedSeed = createRenamedSeedStack();
        final ItemStack plainSeedRequest = new ItemStack(Material.WHEAT_SEEDS, 1);

        assertEquals(Material.WHEAT_SEEDS, renamedSeed.getType(),
                "Renamed seeds still satisfy material-only inventory checks.");
        assertFalse(renamedSeed.isSimilar(plainSeedRequest),
                "A plain seed removal request does not match a renamed seed under CraftBukkit similarity rules.");
    }

    @Test
    void removeItemIncludingOffHandConsumesRenamedSeedStackFromStorage() {
        final AtomicReference<ItemStack[]> storageContents = new AtomicReference<>(
                new ItemStack[]{createRenamedSeedStack()});

        when(playerInventory.contains(Material.WHEAT_SEEDS)).thenReturn(true);
        when(playerInventory.getStorageContents()).thenAnswer(invocation -> storageContents.get());
        doAnswer(invocation -> {
            final ItemStack[] updatedStorage = invocation.getArgument(0);
            storageContents.set(updatedStorage);
            return null;
        }).when(playerInventory).setStorageContents(any(ItemStack[].class));
        when(playerInventory.getItemInOffHand()).thenReturn(new ItemStack(Material.AIR));

        // The helper should consume by material directly from storage so renamed seeds are not
        // skipped by CraftBukkit's stricter ItemStack similarity matcher.
        ItemUtils.removeItemIncludingOffHand(player, Material.WHEAT_SEEDS, 1);

        assertNull(storageContents.get()[0],
                "Material-based removal should consume the renamed seed from storage.");
        verify(playerInventory, never()).removeItem(any(ItemStack.class));
    }

    @Test
    void routeBlockDropsShouldRemoveItemsConsumedBySyntheticBlockDropEvent() {
        final Block block = mock(Block.class);
        final BlockState blockState = mock(BlockState.class);
        final Location location = new Location(world, 1.0, 2.0, 3.0);
        final Item keptItem = mock(Item.class);
        final Item telekinizedItem = mock(Item.class);
        final Location blockLocation = new Location(world, 10.0, 64.0, 10.0);

        when(block.getState()).thenReturn(blockState);
        when(block.getLocation()).thenReturn(blockLocation);
        when(pluginManager.isPluginEnabled("ExcellentEnchants")).thenReturn(true);
        when(pluginManager.isPluginEnabled("EcoEnchants")).thenReturn(true);
        when(world.createEntity(eq(blockLocation), eq(Item.class))).thenReturn(keptItem,
                telekinizedItem);
        when(keptItem.getItemStack()).thenReturn(new ItemStack(Material.OAK_LOG));
        when(telekinizedItem.getItemStack()).thenReturn(new ItemStack(Material.STICK));
        doAnswer(invocation -> {
            final Event event = invocation.getArgument(0);

            if (event instanceof BlockDropItemEvent blockDropItemEvent) {
                blockDropItemEvent.getItems().remove(telekinizedItem);
            }

            return event;
        }).when(pluginManager).callEvent(any(Event.class));

        ItemUtils.routeBlockDrops(player, block, location,
                List.of(new ItemStack(Material.OAK_LOG), new ItemStack(Material.STICK)),
                ItemSpawnReason.TREE_FELLER_DISPLACED_BLOCK);

        verify(world, times(1)).dropItem(eq(location), any(ItemStack.class));
    }

    @Test
    void routeBlockDropsShouldNotSpawnItemsWhenSyntheticBlockDropEventIsCancelled() {
        final Block block = mock(Block.class);
        final BlockState blockState = mock(BlockState.class);
        final Location location = new Location(world, 4.0, 5.0, 6.0);
        final Item firstItem = mock(Item.class);
        final Item secondItem = mock(Item.class);
        final Location blockLocation = new Location(world, 11.0, 64.0, 11.0);

        when(block.getState()).thenReturn(blockState);
        when(block.getLocation()).thenReturn(blockLocation);
        when(pluginManager.isPluginEnabled("ExcellentEnchants")).thenReturn(true);
        when(world.createEntity(eq(blockLocation), eq(Item.class))).thenReturn(firstItem,
                secondItem);
        doAnswer(invocation -> {
            final Event event = invocation.getArgument(0);

            if (event instanceof BlockDropItemEvent blockDropItemEvent) {
                blockDropItemEvent.setCancelled(true);
            }

            return event;
        }).when(pluginManager).callEvent(any(Event.class));

        ItemUtils.routeBlockDrops(player, block, location,
                List.of(new ItemStack(Material.OAK_LOG), new ItemStack(Material.STICK)),
                ItemSpawnReason.TREE_FELLER_DISPLACED_BLOCK);

        verify(world, never()).dropItem(eq(location), any(ItemStack.class));
    }

    @Test
    void routeBlockDropsShouldSkipSyntheticBlockDropEventWithoutCompatibilityPlugins() {
        final Block block = mock(Block.class);
        final Location location = new Location(world, 7.0, 8.0, 9.0);
        final AtomicBoolean sawSyntheticBlockDropEvent = new AtomicBoolean(false);

        doAnswer(invocation -> {
            final Event event = invocation.getArgument(0);

            if (event instanceof BlockDropItemEvent) {
                sawSyntheticBlockDropEvent.set(true);
            }

            return event;
        }).when(pluginManager).callEvent(any(Event.class));

        ItemUtils.routeBlockDrops(player, block, location,
                List.of(new ItemStack(Material.OAK_LOG)), ItemSpawnReason.TREE_FELLER_DISPLACED_BLOCK);

        assertFalse(sawSyntheticBlockDropEvent.get(),
                "Synthetic BlockDropItemEvent routing should only run for supported compatibility plugins.");
    }

    private ItemStack createRenamedSeedStack() {
        // This models the only behavior that matters for the regression: the stack has the
        // correct material, but CraftBukkit's similarity matcher treats it as different from a
        // fresh plain seed ItemStack.
        final ItemStack seedStack = mock(ItemStack.class);

        when(seedStack.getType()).thenReturn(Material.WHEAT_SEEDS);
        when(seedStack.getAmount()).thenReturn(1);
        when(seedStack.isSimilar(any(ItemStack.class))).thenReturn(false);

        return seedStack;
    }
}
