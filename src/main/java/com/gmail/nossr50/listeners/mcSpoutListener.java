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

import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.HUDmmo;
import com.gmail.nossr50.spout.SpoutStuff;

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

			//Party.update(sPlayer);
			Users.getProfile(sPlayer).toggleSpoutEnabled();
		}
	}
}