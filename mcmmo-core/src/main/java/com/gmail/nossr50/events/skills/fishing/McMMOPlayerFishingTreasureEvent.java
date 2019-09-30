package com.gmail.nossr50.events.skills.fishing;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class McMMOPlayerFishingTreasureEvent extends McMMOPlayerFishingEvent {
    private ItemStack treasure;
    private int xp;

    public McMMOPlayerFishingTreasureEvent(Player player, ItemStack treasure, int xp) {
        super(player);
        this.treasure = treasure;
        this.xp = xp;
    }

    public ItemStack getTreasure() {
        return treasure;
    }

    public void setTreasure(ItemStack item) {
        this.treasure = item;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }
}
