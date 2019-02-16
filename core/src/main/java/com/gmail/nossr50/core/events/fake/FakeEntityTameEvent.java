package com.gmail.nossr50.core.events.fake;


/**
 * Called when mcMMO tames an animal via Call of the Wild
 */
public class FakeEntityTameEvent extends EntityTameEvent {
    public FakeEntityTameEvent(LivingEntity entity, AnimalTamer owner) {
        super(entity, owner);
    }
}
