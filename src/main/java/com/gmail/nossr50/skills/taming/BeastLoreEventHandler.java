package com.gmail.nossr50.skills.taming;

import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

import com.gmail.nossr50.locale.LocaleLoader;

public class BeastLoreEventHandler {
    private Player player;
    private LivingEntity livingEntity;
    private Tameable beast;

    protected BeastLoreEventHandler (Player player, LivingEntity livingEntity) {
        this.player = player;
        this.livingEntity = livingEntity;
        this.beast = (Tameable) livingEntity;
    }

    protected void sendInspectMessage() {
        String message = LocaleLoader.getString("Combat.BeastLore") + " ";

        if (beast.isTamed()) {
            message = message.concat(LocaleLoader.getString("Combat.BeastLoreOwner", new Object[] { getOwnerName() }) + " ");
        }

        message = message.concat(LocaleLoader.getString("Combat.BeastLoreHealth", new Object[] { livingEntity.getHealth(), livingEntity.getMaxHealth() }));
        player.sendMessage(message);
    }

    /**
     * Get the name of a tameable animal's owner.
     *
     * @param beast The animal whose owner's name to get
     * @return the name of the animal's owner, or "Offline Master" if the owner is offline
     */
    private String getOwnerName() {
        AnimalTamer tamer = beast.getOwner();

        if (tamer instanceof Player) {
            return ((Player) tamer).getName();
        }
        else {
            return "Offline Master";
        }
    }
}
