package com.gmail.nossr50.datatypes;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class FakeBlockBreakEvent extends BlockBreakEvent {
	private static final long serialVersionUID = 1L;

	public FakeBlockBreakEvent(Block theBlock, Player player) {
		super(theBlock, player);
	}
}