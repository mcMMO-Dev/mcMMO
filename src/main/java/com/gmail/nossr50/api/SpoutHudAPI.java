package com.gmail.nossr50.api;

import org.bukkit.entity.Player;

import com.gmail.nossr50.spout.SpoutConfig;
import com.gmail.nossr50.spout.huds.HudType;
import com.gmail.nossr50.util.Users;

public class SpoutHudAPI {

    /**
     * Disable the mcMMO XP bar for a player.
     * </br>
     * This function is designed for API usage.
     */
    public static void disableXpBar(Player player) {
        Users.getPlayer(player).getProfile().setHudType(HudType.DISABLED);
    }

    /**
     * Disable the mcMMO XP bar for the server.
     * </br>
     * This function is designed for API usage.
     */
    public static void disableXpBar() {
        SpoutConfig.getInstance().setXPBarEnabled(false);
    }
}
