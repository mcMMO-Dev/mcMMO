package com.gmail.nossr50.core.mcmmo.entity;

import com.gmail.nossr50.core.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.core.mcmmo.Nameable;
import com.gmail.nossr50.core.mcmmo.inventory.InventoryHolder;
import com.gmail.nossr50.core.mcmmo.item.ItemStack;
import com.gmail.nossr50.core.mcmmo.permissions.Permissible;

/**
 * Players
 */
public interface Player extends Living, Nameable, InventoryHolder, Permissible {

    /**
     * Players are not always online
     *
     * @return true if the player is online
     */
    Boolean isOnline();

    /**
     * Gets the McMMOPlayer for this Player
     *
     * @return the associated McMMOPlayer, can be null
     */
    McMMOPlayer getMcMMOPlayer();

    /**
     * Gets the item in the main hand of this player
     * @return the item in the main hand
     */
    ItemStack getItemInMainHand();
}
