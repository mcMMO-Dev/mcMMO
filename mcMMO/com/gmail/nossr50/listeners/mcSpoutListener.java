package com.gmail.nossr50.listeners;

import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.spout.SpoutStuff;

public class mcSpoutListener extends SpoutListener
{
	public void onSpoutCraftEnable(SpoutCraftEnableEvent event) 
	{
		SpoutPlayer sPlayer = event.getPlayer();
		if(sPlayer.isSpoutCraftEnabled())
		{
			//Setup xp bar
			GenericTexture xpbar = new GenericTexture();
			GenericTexture xpicon = new GenericTexture();
			
			//Setup Party HUD stuff
			if(Users.getProfile(sPlayer).inParty())
				SpoutStuff.initializePartyTracking(sPlayer);
			
			xpicon.setUrl("http://dl.dropbox.com/u/18212134/xpbar/icon.png");
			
			xpicon.setHeight(16).setWidth(32).setX(93).setY(2);
			
			xpbar.setUrl("http://dl.dropbox.com/u/18212134/xpbar/xpbar_inc000.png");
			xpbar.setX(110).setY(6).setHeight(8).setWidth(256);
			
			SpoutStuff.xpbars.put(sPlayer, xpbar);
			SpoutStuff.xpicons.put(sPlayer, xpicon);
			
			sPlayer.getMainScreen().attachWidget(SpoutStuff.xpbars.get(sPlayer));
			sPlayer.getMainScreen().attachWidget(SpoutStuff.xpicons.get(sPlayer));
			sPlayer.getMainScreen().setDirty(true);
		}
	}
	/*
	public void onServerTick(ServerTickEvent event) 
	{
		for(Player x : SpoutStuff.xpbars.keySet())
		{
			PlayerProfile PP = Users.getProfile(x);
			if(PP.getLastGained() != null)
			{
				if(SpoutStuff.shouldBeFilled(PP))
					SpoutStuff.updateXpBarFill(x);
			}
		}
	}
	*/
}