package com.gmail.nossr50.events.fake;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

public class FakeBlockDamageEvent extends BlockDamageEvent {

    public FakeBlockDamageEvent(Player player, Block block,
            ItemStack itemInHand, boolean instaBreak) {
        super(player, block, itemInHand, instaBreak);
    }
}