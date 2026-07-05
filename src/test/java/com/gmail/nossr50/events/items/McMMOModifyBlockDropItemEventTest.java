package com.gmail.nossr50.events.items;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class McMMOModifyBlockDropItemEventTest {
    private BlockDropItemEvent blockDropItemEvent;
    private Item itemEntity;
    private ItemStack itemStack;
    private Player player;
    private Block block;
    private BlockState blockState;

    @BeforeEach
    void setUp() {
        // Mocks for delegate passthroughs
        player = mock(Player.class, RETURNS_DEEP_STUBS);
        block = mock(Block.class, RETURNS_DEEP_STUBS);
        blockState = mock(BlockState.class, RETURNS_DEEP_STUBS);

        // Primary Bukkit event mock
        blockDropItemEvent = mock(BlockDropItemEvent.class, RETURNS_DEEP_STUBS);
        when(blockDropItemEvent.getPlayer()).thenReturn(player);
        when(blockDropItemEvent.getBlock()).thenReturn(block);
        when(blockDropItemEvent.getBlockState()).thenReturn(blockState);

        // Item + ItemStack mock
        itemStack = mock(ItemStack.class);
        when(itemStack.getAmount()).thenReturn(3); // original count
        itemEntity = mock(Item.class);
        when(itemEntity.getItemStack()).thenReturn(itemStack);
    }

    private McMMOModifyBlockDropItemEvent newEvent(int bonus) {
        return new McMMOModifyBlockDropItemEvent(blockDropItemEvent, itemEntity, bonus);
    }

    @Nested
    @DisplayName("Constructor & validation")
    class ConstructorValidation {

        @Test
        void ctorNullEventThrows() {
            assertThrows(NullPointerException.class,
                    () -> new McMMOModifyBlockDropItemEvent(null, itemEntity, 1));
        }

        @Test
        void ctorNullItemThrows() {
            assertThrows(NullPointerException.class,
                    () -> new McMMOModifyBlockDropItemEvent(blockDropItemEvent, null, 1));
        }

        @Test
        void ctorZeroBonusThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> new McMMOModifyBlockDropItemEvent(blockDropItemEvent, itemEntity, 0));
        }

        @Test
        void ctorNegativeBonusThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> new McMMOModifyBlockDropItemEvent(blockDropItemEvent, itemEntity, -5));
        }

        @Test
        void ctorSetsOriginalsAndModifiedCorrectly() {
            // original amount = 3, bonus = 2
            McMMOModifyBlockDropItemEvent ev = newEvent(2);
            assertEquals(3, ev.getOriginalItemStackQuantity());
            assertEquals(2, ev.getOriginalBonusAmountToAdd());
            assertEquals(5, ev.getModifiedItemStackQuantity());
            assertFalse(ev.isCancelled());
            assertFalse(ev.isEffectivelyNoBonus());
        }
    }

    @Nested
    @DisplayName("Cancellable contract")
    class Cancellation {
        @Test
        void cancelAndUncancel() {
            McMMOModifyBlockDropItemEvent ev = newEvent(1);
            assertFalse(ev.isCancelled());
            ev.setCancelled(true);
            assertTrue(ev.isCancelled());
            ev.setCancelled(false);
            assertFalse(ev.isCancelled());
        }
    }

    @Nested
    @DisplayName("Delta & absolute quantity semantics")
    class DeltaAndAbsolute {

        @Test
        void getBonusAmountToAddReflectsDifferenceFromOriginal() {
            // original 3, bonus 4 => modified 7
            McMMOModifyBlockDropItemEvent ev = newEvent(4);
            assertEquals(4, ev.getBonusAmountToAdd());
            assertEquals(7, ev.getModifiedItemStackQuantity());
        }

        @Test
        void setBonusAmountToAddUpdatesModifiedQuantity() {
            McMMOModifyBlockDropItemEvent ev = newEvent(2); // original 3 -> modified 5
            ev.setBonusAmountToAdd(10); // new modified should be 13
            assertEquals(13, ev.getModifiedItemStackQuantity());
            assertEquals(10, ev.getBonusAmountToAdd());
            assertFalse(ev.isEffectivelyNoBonus());
        }

        @Test
        void setBonusAmountToAddNegativeThrows() {
            McMMOModifyBlockDropItemEvent ev = newEvent(1);
            assertThrows(IllegalArgumentException.class, () -> ev.setBonusAmountToAdd(-1));
        }

        @Test
        void setModifiedItemStackQuantityEqualToOriginalIsNoBonus() {
            McMMOModifyBlockDropItemEvent ev = newEvent(2); // 3 -> 5
            ev.setModifiedItemStackQuantity(3); // back to original => no bonus
            assertEquals(3, ev.getModifiedItemStackQuantity());
            assertEquals(0, ev.getBonusAmountToAdd());
            assertTrue(ev.isEffectivelyNoBonus());
        }

        @Test
        void setModifiedItemStackQuantityLessThanOriginalThrows() {
            McMMOModifyBlockDropItemEvent ev = newEvent(1);
            assertThrows(IllegalArgumentException.class, () -> ev.setModifiedItemStackQuantity(2)); // original is 3
        }

        @Test
        void setModifiedItemStackQuantityGreaterThanOriginalUpdatesBonus() {
            McMMOModifyBlockDropItemEvent ev = newEvent(1); // original 3 -> modified 4
            ev.setModifiedItemStackQuantity(12);
            assertEquals(12, ev.getModifiedItemStackQuantity());
            assertEquals(9, ev.getBonusAmountToAdd()); // 12 - 3
            assertFalse(ev.isEffectivelyNoBonus());
        }
    }

    @Nested
    @DisplayName("Delegate passthroughs")
    class Delegates {

        @Test
        void getPlayerPassthrough() {
            McMMOModifyBlockDropItemEvent ev = newEvent(1);
            assertSame(player, ev.getPlayer());
            verify(blockDropItemEvent, atLeastOnce()).getPlayer();
        }

        @Test
        void getBlockPassthrough() {
            McMMOModifyBlockDropItemEvent ev = newEvent(1);
            assertSame(block, ev.getBlock());
            verify(blockDropItemEvent, atLeastOnce()).getBlock();
        }

        @Test
        void getBlockStatePassthrough() {
            McMMOModifyBlockDropItemEvent ev = newEvent(1);
            assertSame(blockState, ev.getBlockState());
            verify(blockDropItemEvent, atLeastOnce()).getBlockState();
        }

        @Test
        void getItemReturnsOriginalItemEntity() {
            McMMOModifyBlockDropItemEvent ev = newEvent(1);
            assertSame(itemEntity, ev.getItem());
        }
    }

    @Nested
    @DisplayName("HandlerList plumbing")
    class HandlerListTests {
        @Test
        void handlerList_isNonNull_andShared() {
            McMMOModifyBlockDropItemEvent ev = newEvent(1);
            HandlerList fromInstance = ev.getHandlers();
            HandlerList fromStatic = McMMOModifyBlockDropItemEvent.getHandlerList();
            assertNotNull(fromInstance);
            assertNotNull(fromStatic);
            // Bukkit convention: same static instance
            assertSame(fromStatic, fromInstance);
        }
    }

    @Nested
    @DisplayName("Object contracts")
    class ObjectContracts {

        @Test
        void toStringContainsKeyFields() {
            McMMOModifyBlockDropItemEvent ev = newEvent(2);
            String s = ev.toString();
            assertNotNull(s);
            assertTrue(s.contains("originalBonusAmountToAdd=2"));
            assertTrue(s.contains("modifiedItemStackQuantity=5"));
        }

        @Test
        void equalsAndHashCodeReflectState() {
            // Same inputs => equal (mocks are same instances)
            McMMOModifyBlockDropItemEvent a = newEvent(2);
            McMMOModifyBlockDropItemEvent b = newEvent(2);
            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());

            // Change cancellation and modified quantity => not equal
            McMMOModifyBlockDropItemEvent c = newEvent(2);
            c.setCancelled(true);
            assertNotEquals(a, c);

            McMMOModifyBlockDropItemEvent d = newEvent(2);
            d.setModifiedItemStackQuantity(99);
            assertNotEquals(a, d);

            // Different underlying mocks => not equal
            BlockDropItemEvent otherEvent = mock(BlockDropItemEvent.class, RETURNS_DEEP_STUBS);
            when(otherEvent.getPlayer()).thenReturn(player);
            when(otherEvent.getBlock()).thenReturn(block);
            when(otherEvent.getBlockState()).thenReturn(blockState);

            ItemStack otherStack = mock(ItemStack.class);
            when(otherStack.getAmount()).thenReturn(3);
            Item otherItem = mock(Item.class);
            when(otherItem.getItemStack()).thenReturn(otherStack);

            McMMOModifyBlockDropItemEvent e = new McMMOModifyBlockDropItemEvent(otherEvent, otherItem, 2);
            assertNotEquals(a, e);
        }
    }
}