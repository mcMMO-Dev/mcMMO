package com.gmail.nossr50.events;

import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class McMMOReplaceVanillaTreasureEvent extends Event {
    private @NotNull ItemStack replacementItemStack;
    private final @NotNull Item originalItem;

    public McMMOReplaceVanillaTreasureEvent(@NotNull Item originalItem, @NotNull ItemStack replacementItemStack) {
        this.originalItem = originalItem;
        this.replacementItemStack = replacementItemStack;
    }

    /** Rest of file is required boilerplate for custom events **/
    private static final @NotNull HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }

    public @NotNull ItemStack getReplacementItemStack() {
        return replacementItemStack;
    }

    public void setReplacementItemStack(@NotNull ItemStack replacementItemStack) {
        this.replacementItemStack = replacementItemStack;
    }

    public @NotNull Item getOriginalItem() {
        return originalItem;
    }
}
