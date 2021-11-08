package com.gmail.nossr50.events.fake;

import org.bukkit.block.Block;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FakeBrewEvent extends BrewEvent implements FakeEvent {
    public FakeBrewEvent(Block brewer, BrewerInventory contents, List<ItemStack> results, int fuelLevel) {
        super(brewer, contents, results, fuelLevel);
    }
}
