package com.gmail.nossr50.listeners;

import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.popups.PopupMMO;
import com.gmail.nossr50.spout.SpoutStuff;

public class mcSpoutInputListener extends InputListener
{
	mcMMO plugin = null;
	
	public mcSpoutInputListener(mcMMO pluginx)
	{
		plugin = pluginx;
	}
	
	public void onKeyPressedEvent(KeyPressedEvent event) 
	{
		if(!event.getPlayer().isSpoutCraftEnabled() || event.getPlayer().getMainScreen().getActivePopup() != null)
			return;
		if(event.getScreenType() != ScreenType.GAME_SCREEN)
			return;
		
		SpoutPlayer sPlayer = event.getPlayer();
		
		if(event.getKey() == LoadProperties.keypress)
		{
			if(!SpoutStuff.playerScreens.containsKey(sPlayer))
			{
				PopupMMO mmoPop = new PopupMMO(sPlayer, Users.getProfile(sPlayer), plugin);
				SpoutStuff.playerScreens.put(sPlayer, mmoPop);
				sPlayer.getMainScreen().attachPopupScreen(SpoutStuff.playerScreens.get(sPlayer));	
				sPlayer.getMainScreen().setDirty(true);
			} else {
				sPlayer.getMainScreen().attachPopupScreen(SpoutStuff.playerScreens.get(sPlayer));	
				sPlayer.getMainScreen().setDirty(true);
			}
		}
	}
}