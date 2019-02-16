package com.gmail.nossr50.core.events.fake;


public class FakeBrewEvent extends BrewEvent {
    public FakeBrewEvent(Block brewer, BrewerInventory contents, int fuelLevel) {
        super(brewer, contents, fuelLevel);
    }
}
