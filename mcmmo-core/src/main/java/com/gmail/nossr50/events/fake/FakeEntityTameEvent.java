package com.gmail.nossr50.events.fake;

import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityTameEvent;

/**
 * Called when mcMMO tames an animal via Call of the Wild
 */
public class FakeEntityTameEvent extends EntityTameEvent {
    public FakeEntityTameEvent(LivingEntity entity, AnimalTamer owner) {
        super(entity, owner);
    }
}
