package com.gmail.nossr50.skills.taming;

import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.locale.LocaleLoader;

public class EnvironmentallyAwareEventHandler {
    private Player player;
    private EntityDamageEvent event;
    private Wolf wolf;

    protected EnvironmentallyAwareEventHandler(TamingManager manager, EntityDamageEvent event) {
        this.player = manager.getPlayer();
        this.event = event;
        this.wolf = (Wolf) event.getEntity();
    }

    protected void teleportWolf() {
        if (event.getDamage() > wolf.getHealth()) {
            return;
        }

        wolf.teleport(player.getLocation());
    }

    protected void sendAbilityMessage() {
        player.sendMessage(LocaleLoader.getString("Taming.Listener.Wolf"));
    }

    protected void cancelEvent() {
        event.setCancelled(true);
    }
}
