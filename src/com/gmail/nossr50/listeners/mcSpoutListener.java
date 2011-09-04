package com.gmail.nossr50.listeners;

import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.HUDmmo;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.spout.mmoHelper;

public class mcSpoutListener extends SpoutListener
{
	mcMMO plugin = null;
	
	public mcSpoutListener(mcMMO pluginx)
	{
		plugin = pluginx;
	}
	
	public void onSpoutCraftEnable(SpoutCraftEnableEvent event) 
	{
		SpoutPlayer sPlayer = event.getPlayer();
		if(sPlayer.isSpoutCraftEnabled())
		{
			//Setup Party HUD stuff
			SpoutStuff.playerHUDs.put(sPlayer, new HUDmmo(sPlayer));
			
			//if(LoadProperties.partybar && Users.getProfile(sPlayer).inParty())
				//SpoutStuff.initializePartyTracking(sPlayer);
			
			mmoHelper.initialize(sPlayer, plugin);
			
			//Party.update(sPlayer);
			Users.getProfile(sPlayer).toggleSpoutEnabled();
		}
	}
}