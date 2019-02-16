package com.gmail.nossr50.core.events.skills.fishing;


import com.gmail.nossr50.core.mcmmo.entity.Player;
import com.gmail.nossr50.core.mcmmo.item.ItemStack;

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
