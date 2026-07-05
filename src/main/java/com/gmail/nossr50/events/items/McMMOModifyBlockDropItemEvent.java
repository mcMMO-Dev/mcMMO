package com.gmail.nossr50.events.items;

import static java.util.Objects.requireNonNull;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockDropItemEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when mcMMO is modifying the amount of bonus drops to add to an Item involved in a {@link BlockDropItemEvent}.
 * <p>
 * This event is called before mcMMO has modified the ItemStack quantity on the {@link Item} entity.
 * <p>
 * This event is called once per Item entity that is involved in the {@link BlockDropItemEvent}.
 * <p>
 * This event is called during mcMMO logic on the {@link BlockDropItemEvent}, and can be used to
 * modify the quantity that mcMMO will add to the ItemStack.
 * <p>
 * This event is considered cancelled if it is either cancelled directly or if bonus drops are 0 or
 * less.
 */
public class McMMOModifyBlockDropItemEvent extends Event implements Cancellable {
    private final @NotNull BlockDropItemEvent blockDropItemEvent;
    private final int originalBonusAmountToAdd;
    private int modifiedItemStackQuantity;
    private final @NotNull Item itemThatHasBonusDrops;
    private boolean isCancelled = false;
    private final int originalItemStackQuantity;

    public McMMOModifyBlockDropItemEvent(@NotNull BlockDropItemEvent blockDropItemEvent,
            @NotNull Item itemThatHasBonusDrops, int bonusDropsToAdd) {
        super(false);
        requireNonNull(blockDropItemEvent, "blockDropItemEvent cannot be null");
        requireNonNull(itemThatHasBonusDrops, "itemThatHasBonusDrops cannot be null");
        if (bonusDropsToAdd <= 0) {
            throw new IllegalArgumentException("cannot instantiate a new"
                    + " McMMOModifyBlockDropItemEvent with a bonusDropsToAdd that is <= 0");
        }
        this.blockDropItemEvent = blockDropItemEvent;
        this.itemThatHasBonusDrops = itemThatHasBonusDrops;
        this.originalItemStackQuantity = itemThatHasBonusDrops.getItemStack().getAmount();
        this.originalBonusAmountToAdd = bonusDropsToAdd;
        this.modifiedItemStackQuantity = itemThatHasBonusDrops.getItemStack().getAmount()
                + bonusDropsToAdd;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    /**
     * The original BlockDropItemEvent which caused this event to be fired.
     * @return the original BlockDropItemEvent
     */
    public @NotNull BlockDropItemEvent getBlockDropItemEvent() {
        return blockDropItemEvent;
    }

    /**
     * The original bonus mcMMO would have added before any modifications to this event from
     * other plugins.
     * @return the original bonus amount to add
     */
    public int getOriginalBonusAmountToAdd() {
        return originalBonusAmountToAdd;
    }

    /**
     * The Item entity that is being modified by this event.
     * This item returned by this call should not be modified, it is provided as a convenience.
     * @return the Item entity that is having bonus drops added to it.
     */
    public @NotNull Item getItem() {
        return itemThatHasBonusDrops;
    }


    /**
     * The modified ItemStack quantity that will be set on the Item entity if this event is not
     * cancelled.
     *
     * @return the modified ItemStack quantity that will be set on the Item entity
     */
    public int getModifiedItemStackQuantity() {
        return modifiedItemStackQuantity;
    }

    /**
     * The original ItemStack quantity of the Item entity before any modifications from this event.
     * This is a reflection of the state of the Item when mcMMO fired this event.
     * It is possible it has modified since then, so do not rely on this value to be the current.
     * @return the original ItemStack quantity of the Item entity before any modifications from this event
     */
    public int getOriginalItemStackQuantity() {
        return originalItemStackQuantity;
    }

    /**
     * The amount of bonus that will be added to the ItemStack quantity if this event is not
     * cancelled.
     * @return the amount of bonus that will be added to the ItemStack quantity
     */
    public int getBonusAmountToAdd() {
        return Math.max(0, modifiedItemStackQuantity - originalItemStackQuantity);
    }

    /**
     * Set the amount of bonus that will be added to the ItemStack quantity if this event is not
     * cancelled.
     * @param bonus the amount of bonus that will be added to the ItemStack quantity
     * @throws IllegalArgumentException if bonus is less than 0
     */
    public void setBonusAmountToAdd(int bonus) {
        if (bonus < 0) throw new IllegalArgumentException("bonus must be >= 0");
        this.modifiedItemStackQuantity = originalItemStackQuantity + bonus;
    }

    /**
     * Set the modified ItemStack quantity that will be set on the Item entity if this event is not
     * cancelled. This CANNOT be lower than the original quantity of the ItemStack.
     * @param modifiedItemStackQuantity the modified ItemStack quantity that will be set on the Item entity
     * @throws IllegalArgumentException if modifiedItemStackQuantity is less than originalItemStackQuantity
     */
    public void setModifiedItemStackQuantity(int modifiedItemStackQuantity) {
        if (modifiedItemStackQuantity < originalItemStackQuantity) {
            throw new IllegalArgumentException(
                    "modifiedItemStackQuantity cannot be less than the originalItemStackQuantity");
        }
        this.modifiedItemStackQuantity = modifiedItemStackQuantity;
    }

    public boolean isEffectivelyNoBonus() {
        return modifiedItemStackQuantity == originalItemStackQuantity;
    }

    /**
     * Delegate method for {@link BlockDropItemEvent}, gets the Player that is breaking the block
     * involved in this event.
     *
     * @return The Player that is breaking the block involved in this event
     */
    public @NotNull Player getPlayer() {
        return blockDropItemEvent.getPlayer();
    }

    /**
     * Delegate method for {@link BlockDropItemEvent#getBlock()}.
     * Gets the Block involved in this event.
     *
     * @return the Block involved in this event
     */
    public @NotNull Block getBlock() {
        return blockDropItemEvent.getBlock();
    }

    /**
     * Delegate method for {@link BlockDropItemEvent#getBlockState()}.
     * Gets the BlockState of the block involved in this event.
     *
     * @return the BlockState of the block involved in this event
     */
    public @NotNull BlockState getBlockState() {
        return blockDropItemEvent.getBlockState();
    }

    private static final @NotNull HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull String toString() {
        return "McMMOModifyBlockDropItemEvent{" +
                "blockDropItemEvent=" + blockDropItemEvent +
                ", originalBonusAmountToAdd=" + originalBonusAmountToAdd +
                ", modifiedItemStackQuantity=" + modifiedItemStackQuantity +
                ", itemThatHasBonusDrops=" + itemThatHasBonusDrops +
                ", isCancelled=" + isCancelled +
                ", originalItemStackQuantity=" + originalItemStackQuantity +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof McMMOModifyBlockDropItemEvent that)) {
            return false;
        }

        return originalBonusAmountToAdd == that.originalBonusAmountToAdd
                && modifiedItemStackQuantity == that.modifiedItemStackQuantity
                && isCancelled == that.isCancelled
                && originalItemStackQuantity == that.originalItemStackQuantity
                && blockDropItemEvent.equals(that.blockDropItemEvent)
                && itemThatHasBonusDrops.equals(
                that.itemThatHasBonusDrops);
    }

    @Override
    public int hashCode() {
        int result = blockDropItemEvent.hashCode();
        result = 31 * result + originalBonusAmountToAdd;
        result = 31 * result + modifiedItemStackQuantity;
        result = 31 * result + itemThatHasBonusDrops.hashCode();
        result = 31 * result + Boolean.hashCode(isCancelled);
        result = 31 * result + originalItemStackQuantity;
        return result;
    }
}
