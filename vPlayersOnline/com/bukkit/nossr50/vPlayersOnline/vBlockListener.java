package com.bukkit.nossr50.vPlayersOnline;

import org.bukkit.Block;
import org.bukkit.BlockFace;
import org.bukkit.Material;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;

/**
 * vPlayersOnline block listener
 * @author nossr50
 */
public class vBlockListener extends BlockListener {
    private final vPlayersOnline plugin;

    public vBlockListener(final vPlayersOnline plugin) {
        this.plugin = plugin;
    }

    //put all Block related code here
}

