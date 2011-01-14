package com.bukkit.nossr50.vPlayersOnline;

import org.bukkit.event.block.BlockListener;


/**
 * vPlayersOnline block listener
 * @author nossr50
 */
public class POBlockListener extends BlockListener {
    private final vPlayersOnline plugin;

    public POBlockListener(final vPlayersOnline plugin) {
        this.plugin = plugin;
    }

    //put all Block related code here
}
