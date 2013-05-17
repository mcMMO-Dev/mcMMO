package com.gmail.nossr50.events.skills.fishing;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;

public class McMMOPlayerShakeEvent extends McMMOPlayerSkillEvent implements Cancellable {

    private boolean cancelled = false;
    private ItemStack drop;

    public McMMOPlayerShakeEvent(Player player, ItemStack drop) {
        super(player, SkillType.FISHING);
        this.drop = drop;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean newValue) {
        this.cancelled  = newValue;
    }

    public ItemStack getDrop() {
        return drop;
    }

    public void setDrop(ItemStack drop) {
        this.drop = drop;
    }

}
