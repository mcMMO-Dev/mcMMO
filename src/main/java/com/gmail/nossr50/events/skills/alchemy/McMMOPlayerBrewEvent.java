package com.gmail.nossr50.events.skills.alchemy;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;

public class McMMOPlayerBrewEvent extends McMMOPlayerSkillEvent implements Cancellable {
    private BlockState brewingStand;

    private boolean cancelled;

    public McMMOPlayerBrewEvent(Player player, BlockState brewingStand) {
        super(player, SkillType.ALCHEMY);
        this.brewingStand = brewingStand;
        cancelled = false;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean newValue) {
        this.cancelled = newValue;
    }

    public Block getBrewingStandBlock() {
        return brewingStand.getBlock();
    }

    public BrewingStand getBrewingStand() {
        return (BrewingStand) brewingStand;
    }
}
