package com.gmail.nossr50.placeholders;

import org.bukkit.entity.Player;

public interface Placeholder {

    /**
     * @param player the player to process the placeholder for
     * @param params the paramaters to be passed to the placeholder
     * @return the value of the placeholder
     */
    String process(Player player, String params);

    /**
     * @return the name of the placeholder
     */
    String getName();
}
