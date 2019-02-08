package com.gmail.nossr50.events.fake;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Called when mcMMO breaks a block due to a special ability.
 */
public class FakeBlockBreakEvent extends BlockBreakEvent {
    public FakeBlockBreakEvent(Block theBlock, Player player) {
        super(theBlock, player);
    }
}
