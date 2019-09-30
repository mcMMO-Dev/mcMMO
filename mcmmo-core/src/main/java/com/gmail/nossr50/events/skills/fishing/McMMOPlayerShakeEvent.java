package com.gmail.nossr50.events.skills.fishing;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class McMMOPlayerShakeEvent extends McMMOPlayerFishingEvent {
    private ItemStack drop;

    public McMMOPlayerShakeEvent(Player player, ItemStack drop) {
        super(player);
        this.drop = drop;
    }

    public ItemStack getDrop() {
        return drop;
    }

    public void setDrop(ItemStack drop) {
        this.drop = drop;
    }
}
