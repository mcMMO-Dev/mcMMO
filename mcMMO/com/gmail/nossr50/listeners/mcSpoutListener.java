package com.gmail.nossr50.listeners;

import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.spout.SpoutStuff;

public class mcSpoutListener extends SpoutListener
{
	public void onSpoutCraftEnable(SpoutCraftEnableEvent event) 
	{
		SpoutPlayer sPlayer = event.getPlayer();
		if(sPlayer.isSpoutCraftEnabled())
		{
			//Setup Party HUD stuff
			if(LoadProperties.partybar && Users.getProfile(sPlayer).inParty())
				SpoutStuff.initializePartyTracking(sPlayer);
			
			//Setup player XP-Bar & XP-Icon
			if(LoadProperties.xpbar)
				SpoutStuff.initializeXpBarDisplay(sPlayer);
		}
	}
}