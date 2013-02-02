package com.gmail.nossr50.skills.taming;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

import com.gmail.nossr50.locale.LocaleLoader;

public class BeastLoreEventHandler {
    private Player player;
    private LivingEntity livingEntity;
    private Tameable beast;

    protected BeastLoreEventHandler(Player player, LivingEntity livingEntity) {
        this.player = player;
        this.livingEntity = livingEntity;
        this.beast = (Tameable) livingEntity;
    }

    protected void sendInspectMessage() {
        if (player == null)
            return;

        String message = LocaleLoader.getString("Combat.BeastLore") + " ";

        if (beast.isTamed()) {
            message = message.concat(LocaleLoader.getString("Combat.BeastLoreOwner", getOwnerName()) + " ");
        }

        message = message.concat(LocaleLoader.getString("Combat.BeastLoreHealth", livingEntity.getHealth(), livingEntity.getMaxHealth()));
        player.sendMessage(message);
    }

    /**
     * Get the name of a tameable animal's owner.
     *
     * @return the name of the animal's owner
     */
    private String getOwnerName() {
        AnimalTamer tamer = beast.getOwner();

        if (tamer instanceof Player) {
            return ((Player) tamer).getName();
        }
        else if (tamer instanceof OfflinePlayer) {
            return ((OfflinePlayer) tamer).getName();
        }

        return "Unknown Master";
    }
}
