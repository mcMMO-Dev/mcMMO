package com.gmail.nossr50.events;

import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class FakeBlockBreakEvent extends BlockBreakEvent {

	public FakeBlockBreakEvent(Block theBlock, Player player) {
		super(theBlock, player, new ArrayList<ItemStack>(theBlock.getDrops()));
	}
}