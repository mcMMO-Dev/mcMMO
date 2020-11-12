package com.gmail.nossr50.events.fake;

import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;

public class FakePlayerFishEvent extends PlayerFishEvent implements FakeEvent {
    public FakePlayerFishEvent(Player player, Entity entity, FishHook hookEntity, State state) {
        super(player, entity, hookEntity, state);
    }
}
