package com.gmail.nossr50.core.events.skills.alchemy;

import com.gmail.nossr50.core.mcmmo.block.BlockState;
import com.gmail.nossr50.core.mcmmo.entity.Player;
import com.gmail.nossr50.core.skills.PrimarySkillType;
import com.gmail.nossr50.core.events.skills.McMMOPlayerSkillEvent;

public class McMMOPlayerBrewEvent extends McMMOPlayerSkillEvent implements Cancellable {
    private BlockState brewingStand;

    private boolean cancelled;

    public McMMOPlayerBrewEvent(Player player, BlockState brewingStand) {
        super(player, PrimarySkillType.ALCHEMY);
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
