package com.gmail.nossr50.skills.alchemy;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Covers the shift-click ingredient transfers into the brewing stand ingredient slot:
 * shift-left moves the whole stack (merging with what is already there), shift-right moves
 * exactly one item. The stack-size boundary cases matter because getting them wrong either
 * refuses legal transfers or over-stacks the ingredient slot past the item's max stack size.
 */
class AlchemyPotionBrewerTransferTest extends MMOTestEnvironment {
    private static final Logger logger =
            getLogger(AlchemyPotionBrewerTransferTest.class.getName());

    private static final int FROM_SLOT = 10;
    private static final int MAX_STACK_SIZE = 64;

    private InventoryView view;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);
        view = mock(InventoryView.class);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    /**
     * The working copy the transfer logic operates on: the slot item's clone. The slot item
     * itself only answers {@code clone()}.
     */
    private ItemStack slotWithWorkingCopy(int slot, ItemStack workingCopy) {
        final ItemStack slotItem = mock(ItemStack.class);
        when(slotItem.clone()).thenReturn(workingCopy);
        when(view.getItem(slot)).thenReturn(slotItem);
        return slotItem;
    }

    private ItemStack workingItem(Material material, int amount) {
        final ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(material);
        when(item.getAmount()).thenReturn(amount);
        when(item.getMaxStackSize()).thenReturn(MAX_STACK_SIZE);
        return item;
    }

    private ItemStack emptySlotItem() {
        return workingItem(Material.AIR, 0);
    }

    private void wireSlots(ItemStack fromWorking, ItemStack ingredientWorking) {
        slotWithWorkingCopy(FROM_SLOT, fromWorking);
        slotWithWorkingCopy(Alchemy.INGREDIENT_SLOT, ingredientWorking);
    }

    @Nested
    class ShiftRightMovesOneItem {
        @Test
        void shouldMoveASingleItemOntoAnEmptyIngredientSlot() {
            // Given - five nether wart in the clicked slot and an empty ingredient slot
            final ItemStack fromWorking = workingItem(Material.NETHER_WART, 5);
            final ItemStack movedItem = workingItem(Material.NETHER_WART, 1);
            when(fromWorking.clone()).thenReturn(movedItem);
            wireSlots(fromWorking, emptySlotItem());

            // When - one item is transferred
            final boolean transferred =
                    AlchemyPotionBrewer.transferItems(view, FROM_SLOT, ClickType.SHIFT_RIGHT);

            // Then - a single item lands in the ingredient slot and the source shrinks by one
            assertThat(transferred).isTrue();
            verify(movedItem).setAmount(1);
            verify(fromWorking).setAmount(4);
            verify(view).setItem(Alchemy.INGREDIENT_SLOT, movedItem);
            verify(view).setItem(FROM_SLOT, fromWorking);
        }

        @Test
        void shouldAddOneToASimilarIngredientStack() {
            // Given - five nether wart clicked onto three similar wart in the ingredient slot
            final ItemStack fromWorking = workingItem(Material.NETHER_WART, 5);
            final ItemStack ingredientWorking = workingItem(Material.NETHER_WART, 3);
            when(fromWorking.isSimilar(ingredientWorking)).thenReturn(true);
            wireSlots(fromWorking, ingredientWorking);

            // When - one item is transferred
            final boolean transferred =
                    AlchemyPotionBrewer.transferItems(view, FROM_SLOT, ClickType.SHIFT_RIGHT);

            // Then - the ingredient stack grows by one and the source shrinks by one
            assertThat(transferred).isTrue();
            verify(ingredientWorking).setAmount(4);
            verify(fromWorking).setAmount(4);
            verify(view).setItem(Alchemy.INGREDIENT_SLOT, ingredientWorking);
            verify(view).setItem(FROM_SLOT, fromWorking);
        }

        /**
         * Boundary: the ingredient slot already holds a full stack. Adding one more would
         * over-stack the slot past the item's max stack size, so the transfer must refuse.
         */
        @Test
        void shouldRefuseWhenTheIngredientStackIsFull() {
            // Given - a full ingredient stack and a similar source stack
            final ItemStack fromWorking = workingItem(Material.NETHER_WART, 2);
            final ItemStack ingredientWorking =
                    workingItem(Material.NETHER_WART, MAX_STACK_SIZE);
            when(fromWorking.isSimilar(ingredientWorking)).thenReturn(true);
            wireSlots(fromWorking, ingredientWorking);

            // When - one item is transferred
            final boolean transferred =
                    AlchemyPotionBrewer.transferItems(view, FROM_SLOT, ClickType.SHIFT_RIGHT);

            // Then - the transfer refuses instead of over-stacking the ingredient slot
            assertThat(transferred).isFalse();
            verify(view, never()).setItem(anyInt(), any());
        }

        /**
         * Boundary: a full stack in the clicked slot is a legal source - only the ingredient
         * slot's remaining room decides whether one more item fits.
         */
        @Test
        void shouldAllowAFullSourceStackWhenTheIngredientSlotHasRoom() {
            // Given - a full source stack and a single similar item in the ingredient slot
            final ItemStack fromWorking = workingItem(Material.NETHER_WART, MAX_STACK_SIZE);
            final ItemStack ingredientWorking = workingItem(Material.NETHER_WART, 1);
            when(fromWorking.isSimilar(ingredientWorking)).thenReturn(true);
            wireSlots(fromWorking, ingredientWorking);

            // When - one item is transferred
            final boolean transferred =
                    AlchemyPotionBrewer.transferItems(view, FROM_SLOT, ClickType.SHIFT_RIGHT);

            // Then - the transfer proceeds one item at a time
            assertThat(transferred).isTrue();
            verify(ingredientWorking).setAmount(2);
            verify(fromWorking).setAmount(MAX_STACK_SIZE - 1);
        }

        @Test
        void shouldRefuseDissimilarStacks() {
            // Given - potatoes clicked onto a wart ingredient stack
            final ItemStack fromWorking = workingItem(Material.POTATO, 5);
            final ItemStack ingredientWorking = workingItem(Material.NETHER_WART, 3);
            wireSlots(fromWorking, ingredientWorking);

            // When - one item is transferred
            final boolean transferred =
                    AlchemyPotionBrewer.transferItems(view, FROM_SLOT, ClickType.SHIFT_RIGHT);

            // Then - nothing moves
            assertThat(transferred).isFalse();
            verify(view, never()).setItem(anyInt(), any());
        }

        @Test
        void shouldRefuseAnEmptySource() {
            // Given - an empty clicked slot
            wireSlots(emptySlotItem(), emptySlotItem());

            // When - one item is transferred
            final boolean transferred =
                    AlchemyPotionBrewer.transferItems(view, FROM_SLOT, ClickType.SHIFT_RIGHT);

            // Then - nothing moves
            assertThat(transferred).isFalse();
            verify(view, never()).setItem(anyInt(), any());
        }
    }

    @Nested
    class ShiftLeftMovesTheStack {
        @Test
        void shouldMoveTheWholeStackIntoAnEmptyIngredientSlot() {
            // Given - five nether wart in the clicked slot and an empty ingredient slot
            final ItemStack fromWorking = workingItem(Material.NETHER_WART, 5);
            wireSlots(fromWorking, emptySlotItem());

            // When - the stack is transferred
            final boolean transferred =
                    AlchemyPotionBrewer.transferItems(view, FROM_SLOT, ClickType.SHIFT_LEFT);

            // Then - the whole stack moves and the source slot empties
            assertThat(transferred).isTrue();
            verify(view).setItem(Alchemy.INGREDIENT_SLOT, fromWorking);
            verify(view).setItem(FROM_SLOT, null);
        }

        @Test
        void shouldMergeSimilarStacksThatFitUnderTheMaxStackSize() {
            // Given - five wart clicked onto three similar wart in the ingredient slot
            final ItemStack fromWorking = workingItem(Material.NETHER_WART, 5);
            final ItemStack ingredientWorking = workingItem(Material.NETHER_WART, 3);
            when(fromWorking.isSimilar(ingredientWorking)).thenReturn(true);
            wireSlots(fromWorking, ingredientWorking);

            // When - the stack is transferred
            final boolean transferred =
                    AlchemyPotionBrewer.transferItems(view, FROM_SLOT, ClickType.SHIFT_LEFT);

            // Then - the stacks merge and the source slot empties
            assertThat(transferred).isTrue();
            verify(ingredientWorking).setAmount(8);
            verify(view).setItem(FROM_SLOT, null);
            verify(view).setItem(Alchemy.INGREDIENT_SLOT, ingredientWorking);
        }

        @Test
        void shouldLeaveTheOverflowInTheSourceSlot() {
            // Given - ten wart clicked onto sixty similar wart in the ingredient slot
            final ItemStack fromWorking = workingItem(Material.NETHER_WART, 10);
            final ItemStack ingredientWorking = workingItem(Material.NETHER_WART, 60);
            when(fromWorking.isSimilar(ingredientWorking)).thenReturn(true);
            wireSlots(fromWorking, ingredientWorking);

            // When - the stack is transferred
            final boolean transferred =
                    AlchemyPotionBrewer.transferItems(view, FROM_SLOT, ClickType.SHIFT_LEFT);

            // Then - the ingredient slot fills to the max and the overflow stays behind
            assertThat(transferred).isTrue();
            verify(ingredientWorking).setAmount(MAX_STACK_SIZE);
            verify(fromWorking).setAmount(6);
            verify(view).setItem(Alchemy.INGREDIENT_SLOT, ingredientWorking);
            verify(view).setItem(FROM_SLOT, fromWorking);
        }

        @Test
        void shouldRefuseDissimilarStacks() {
            // Given - potatoes clicked onto a wart ingredient stack
            final ItemStack fromWorking = workingItem(Material.POTATO, 5);
            final ItemStack ingredientWorking = workingItem(Material.NETHER_WART, 3);
            wireSlots(fromWorking, ingredientWorking);

            // When - the stack is transferred
            final boolean transferred =
                    AlchemyPotionBrewer.transferItems(view, FROM_SLOT, ClickType.SHIFT_LEFT);

            // Then - nothing moves
            assertThat(transferred).isFalse();
            verify(view, never()).setItem(anyInt(), any());
        }

        @Test
        void shouldRefuseAnEmptySource() {
            // Given - an empty clicked slot
            wireSlots(emptySlotItem(), emptySlotItem());

            // When - the stack is transferred
            final boolean transferred =
                    AlchemyPotionBrewer.transferItems(view, FROM_SLOT, ClickType.SHIFT_LEFT);

            // Then - nothing moves
            assertThat(transferred).isFalse();
            verify(view, never()).setItem(anyInt(), any());
        }
    }
}
