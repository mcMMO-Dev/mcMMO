package com.gmail.nossr50.events.fake;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Called when mcMMO damages a block due to a special ability.
 */
public class FakeBlockDamageEvent extends BlockDamageEvent {
    public FakeBlockDamageEvent(Player player, Block block, ItemStack itemInHand, boolean instaBreak) {
        super(player, block, itemInHand, instaBreak);
    }
}
