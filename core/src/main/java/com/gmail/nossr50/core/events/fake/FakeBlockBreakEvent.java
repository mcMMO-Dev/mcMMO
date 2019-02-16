package com.gmail.nossr50.core.events.fake;

import com.gmail.nossr50.core.mcmmo.entity.Player;

/**
 * Called when mcMMO breaks a block due to a special ability.
 */
public class FakeBlockBreakEvent extends BlockBreakEvent {
    public FakeBlockBreakEvent(Block theBlock, Player player) {
        super(theBlock, player);
    }
}
