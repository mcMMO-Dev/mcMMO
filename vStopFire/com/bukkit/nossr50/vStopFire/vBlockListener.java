package com.bukkit.nossr50.vStopFire;

import org.bukkit.Block;
import org.bukkit.BlockFace;
import org.bukkit.Material;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;

/**
 * vPlayerSpawn block listener
 * @author nossr50
 */
public class vBlockListener extends BlockListener {
    private final vStopFire plugin;

    public vBlockListener(final vStopFire plugin) {
        this.plugin = plugin;
    }
    //This should stop fire from spreading but still allow players to light stuff up with flint and steel
    public void onBlockIgnite(BlockIgniteEvent event) {
    	String cause = event.getCause().toString();
    	if(cause.equals("SPREAD"))
    		event.setCancelled(true);
    	if(!cause.equals("FLINT_AND_STEEL"))
    		event.setCancelled(true);
    }
    //put all Block related code here
}
