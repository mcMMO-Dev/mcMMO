package com.gmail.nossr50.events.fake;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerAnimationEvent;

public class FakePlayerAnimationEvent extends PlayerAnimationEvent{

    public FakePlayerAnimationEvent(Player player) {
        super(player);
    }

}
