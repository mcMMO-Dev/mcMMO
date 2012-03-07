package com.gmail.nossr50.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class FakeBlockBreakEvent extends BlockBreakEvent {

	public FakeBlockBreakEvent(Block theBlock, Player player) {
		super(theBlock, player);
	}
}