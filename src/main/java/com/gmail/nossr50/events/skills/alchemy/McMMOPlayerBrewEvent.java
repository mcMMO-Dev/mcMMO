package com.gmail.nossr50.events.skills.alchemy;

import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;

public class McMMOPlayerBrewEvent extends McMMOPlayerSkillEvent implements Cancellable {
    private Block brewingStand;
    
    private boolean cancelled;

    public McMMOPlayerBrewEvent(Player player, Block brewingStand) {
        super(player, SkillType.ALCHEMY);
        this.setBrewingStandBlock(brewingStand);
        cancelled = false;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean newValue) {
        this.cancelled = newValue;
    }

    public Block getBrewingStandBlock() {
        return brewingStand;
    }

    public void setBrewingStandBlock(Block brewingStand) {
        this.brewingStand = brewingStand;
    }
    
    public BrewingStand getBrewingStand() {
        return brewingStand.getState() instanceof BrewingStand ? (BrewingStand) brewingStand.getState() : null;
    }
}
