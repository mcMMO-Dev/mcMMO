package com.gmail.nossr50.listeners;

import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.gui.GenericTexture;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.contrib.SpoutStuff;

public class mcSpoutListener extends SpoutListener
{
	mcMMO plugin = null;
	public mcSpoutListener(mcMMO pluginx) 
	{
		plugin = pluginx;
	}
	public void onSpoutCraftEnable(SpoutCraftEnableEvent event) 
	{
		if(event.getPlayer().isSpoutCraftEnabled())
		{
			GenericTexture xpbar = new GenericTexture();
			GenericTexture xpicon = new GenericTexture();
			
			xpicon.setUrl("http://dl.dropbox.com/u/18212134/xpbar/icon.png");
			xpicon.setHeight(16).setWidth(32).setX(93).setY(2);
			
			xpbar.setUrl("http://dl.dropbox.com/u/18212134/xpbar/xpbar_inc000.png");
			xpbar.setX(110).setY(6).setHeight(8).setWidth(256);
			
			SpoutStuff.xpbars.put(event.getPlayer(), xpbar);
			SpoutStuff.xpicons.put(event.getPlayer(), xpicon);
			
			event.getPlayer().getMainScreen().attachWidget(SpoutStuff.xpbars.get(event.getPlayer()));
			event.getPlayer().getMainScreen().attachWidget(SpoutStuff.xpicons.get(event.getPlayer()));
			event.getPlayer().getMainScreen().setDirty(true);
			
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