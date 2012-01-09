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

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenCloseEvent;
import org.getspout.spoutapi.event.screen.ScreenListener;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.HUDType;
import com.gmail.nossr50.datatypes.HUDmmo;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.buttons.ButtonEscape;
import com.gmail.nossr50.datatypes.buttons.ButtonHUDStyle;
import com.gmail.nossr50.datatypes.buttons.ButtonPartyToggle;
import com.gmail.nossr50.datatypes.popups.PopupMMO;
import com.gmail.nossr50.spout.SpoutStuff;

public class mcSpoutScreenListener extends ScreenListener
{
	mcMMO plugin = null;
	public mcSpoutScreenListener(mcMMO pluginx)
	{
		plugin = pluginx;
	}
	public void onButtonClick(ButtonClickEvent event) 
	{
		SpoutPlayer sPlayer = event.getPlayer();
		PlayerProfile PP = Users.getProfile(sPlayer);
		
		if(event.getButton() instanceof ButtonHUDStyle)
		{
			if(SpoutStuff.playerHUDs.containsKey(sPlayer))
			{
				SpoutStuff.playerHUDs.get(sPlayer).resetHUD();
				SpoutStuff.playerHUDs.remove(sPlayer);
				
				switch(PP.getHUDType())
				{
				case RETRO:
					PP.setHUDType(HUDType.STANDARD);
					break;
				case STANDARD:
					PP.setHUDType(HUDType.SMALL);
					break;
				case SMALL:
					PP.setHUDType(HUDType.DISABLED);
					break;
				case DISABLED:
					PP.setHUDType(HUDType.RETRO);
				}
				
				SpoutStuff.playerHUDs.put(sPlayer, new HUDmmo(sPlayer));
				
				SpoutStuff.playerScreens.get(sPlayer).updateButtons(PP);
			}
		} else if (event.getButton() instanceof ButtonEscape)
		{
			sPlayer.getMainScreen().closePopup();
		} else if (event.getButton() instanceof ButtonPartyToggle)
		{
			PP.togglePartyHUD();
			ButtonPartyToggle bpt = (ButtonPartyToggle)event.getButton();
			bpt.updateText(PP);
			SpoutStuff.playerHUDs.get(sPlayer).resetHUD();
			SpoutStuff.playerHUDs.get(sPlayer).initializeHUD(sPlayer);
		}
	}
	
	public void onScreenClose(ScreenCloseEvent event) 
	{
		if(event.getScreen() instanceof PopupMMO)
		{
			SpoutStuff.playerScreens.remove(event.getPlayer());
		}
	}
}