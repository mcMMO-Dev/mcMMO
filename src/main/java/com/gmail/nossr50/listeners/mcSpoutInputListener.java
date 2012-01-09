/*
	This file is part of mcMMO.

    mcMMO is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    mcMMO is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with mcMMO.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.gmail.nossr50.listeners;

import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
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
		
		if(event.getKey() == SpoutStuff.keypress)
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