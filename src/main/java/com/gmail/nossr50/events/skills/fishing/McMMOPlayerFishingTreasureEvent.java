package com.gmail.nossr50.events.skills.fishing;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;

public class McMMOPlayerFishingTreasureEvent extends McMMOPlayerSkillEvent implements Cancellable {

    private boolean cancelled = false;
    private ItemStack treasure;
    private int xp;

    public McMMOPlayerFishingTreasureEvent(Player player, ItemStack treasure, int xp) {
        super(player, SkillType.FISHING);
        this.treasure = treasure;
        this.xp = xp;
    }

    public ItemStack getTreasure() {
        return treasure;
    }

    public void setTreasure(ItemStack item) {
        this.treasure = item;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean newValue) {
        this.cancelled = newValue;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }
}
