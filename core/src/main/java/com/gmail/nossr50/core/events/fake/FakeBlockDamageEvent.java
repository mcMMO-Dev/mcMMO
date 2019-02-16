package com.gmail.nossr50.core.events.fake;

import com.gmail.nossr50.core.mcmmo.entity.Player;
import com.gmail.nossr50.core.mcmmo.item.ItemStack;

/**
 * Called when mcMMO damages a block due to a special ability.
 */
public class FakeBlockDamageEvent extends BlockDamageEvent {
    public FakeBlockDamageEvent(Player player, Block block, ItemStack itemInHand, boolean instaBreak) {
        super(player, block, itemInHand, instaBreak);
    }
}
