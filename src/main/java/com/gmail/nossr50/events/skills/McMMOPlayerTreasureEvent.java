package com.gmail.nossr50.events.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.datatypes.skills.SkillType;

public abstract class McMMOPlayerTreasureEvent extends McMMOPlayerSkillEvent implements Cancellable {
    private boolean cancelled;
    private ItemStack treasure;
    private int xpGained;

    protected McMMOPlayerTreasureEvent(Player player, SkillType skill, ItemStack treasure, int xpGained) {
        super(player, skill);
        this.treasure = treasure;
        this.xpGained = xpGained;
        this.cancelled = false;
    }

    public ItemStack getTreasure() {
        return treasure;
    }

    public void setTreasure(ItemStack item) {
        this.treasure = item;
    }

    public int getXpGained() {
        return xpGained;
    }

    public void setXpGained(int xpGained) {
        this.xpGained = xpGained;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean newValue) {
        this.cancelled = newValue;
    }
}
