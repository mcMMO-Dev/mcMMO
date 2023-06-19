package com.gmail.nossr50.api;

import com.gmail.nossr50.events.fake.FakeBlockBreakEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TreeFellerBlockBreakEvent extends FakeBlockBreakEvent {
    public TreeFellerBlockBreakEvent(@NotNull Block theBlock, @NotNull Player player) {
        super(theBlock, player);
    }
}
