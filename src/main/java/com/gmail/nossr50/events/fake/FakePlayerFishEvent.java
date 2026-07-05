package com.gmail.nossr50.events.fake;

import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

public class FakePlayerFishEvent extends PlayerFishEvent implements FakeEvent {
    /**
     * @deprecated since 2.2.052 for removal. Use
     * {@link #FakePlayerFishEvent(Player, Entity, FishHook, EquipmentSlot, State)} instead.
     * @see #FakePlayerFishEvent(Player, Entity, FishHook, EquipmentSlot, State)
     */
    @Deprecated(since = "2.2.052", forRemoval = true)
    public FakePlayerFishEvent(Player player, Entity entity, FishHook hookEntity, State state) {
        super(player, entity, hookEntity, state);
    }

    public FakePlayerFishEvent(Player player, Entity entity, FishHook hookEntity,
            @Nullable EquipmentSlot hand, State state) {
        super(player, entity, hookEntity, hand, state);
    }
}
