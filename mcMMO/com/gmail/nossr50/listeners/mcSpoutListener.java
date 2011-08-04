package com.gmail.nossr50.listeners;

import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.gui.GenericTexture;

import com.gmail.nossr50.mcMMO;

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
			//The bottom right of the screen is x=240, y=427
			
			GenericTexture xpbar = new GenericTexture();
			xpbar.setUrl("http://dl.dropbox.com/u/18212134/xpbar/xpbar_inc000.png");
			
			event.getPlayer().getMainScreen().attachWidget(xpbar.setX(0).setY(240));
		}
	}
}
