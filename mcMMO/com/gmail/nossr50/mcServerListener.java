package com.gmail.nossr50;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListener;

public class mcServerListener extends ServerListener{
	protected static final Logger log = Logger.getLogger("Minecraft");
	public Location spawn = null;
    private mcMMO plugin;

    public mcServerListener(mcMMO instance) {
    	plugin = instance;
    }
    public void onServerCommand(ServerCommandEvent event) {
    }
}
