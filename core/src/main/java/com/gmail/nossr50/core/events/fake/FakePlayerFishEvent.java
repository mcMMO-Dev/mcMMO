package com.gmail.nossr50.core.events.fake;


import com.gmail.nossr50.core.mcmmo.entity.Player;

public class FakePlayerFishEvent extends PlayerFishEvent {
    public FakePlayerFishEvent(Player player, Entity entity, FishHook hookEntity, State state) {
        super(player, entity, hookEntity, state);
    }
}
