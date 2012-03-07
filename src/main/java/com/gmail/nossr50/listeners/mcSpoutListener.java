package com.gmail.nossr50.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.HUDmmo;
import com.gmail.nossr50.spout.SpoutStuff;

public class mcSpoutListener implements Listener
{
	mcMMO plugin = null;
	
	public mcSpoutListener(mcMMO pluginx)
	{
		plugin = pluginx;
	}
	
	@EventHandler
	public void onSpoutCraftEnable(SpoutCraftEnableEvent event) 
	{
		SpoutPlayer sPlayer = event.getPlayer();
		if(sPlayer.isSpoutCraftEnabled())
		{
			//Setup Party HUD stuff
			SpoutStuff.playerHUDs.put(sPlayer, new HUDmmo(sPlayer));

			//Party.update(sPlayer);
			Users.getProfile(sPlayer).toggleSpoutEnabled();
		}
	}
}