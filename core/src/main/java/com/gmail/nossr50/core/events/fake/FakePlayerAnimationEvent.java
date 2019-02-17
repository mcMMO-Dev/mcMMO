package com.gmail.nossr50.core.events.fake;


import com.gmail.nossr50.core.mcmmo.entity.Player;

/**
 * Called when handling extra drops to avoid issues with NoCheat.
 */
public class FakePlayerAnimationEvent extends PlayerAnimationEvent {
    public FakePlayerAnimationEvent(Player player) {
        super(player);
    }
}
