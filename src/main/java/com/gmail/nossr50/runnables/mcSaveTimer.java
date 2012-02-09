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
package com.gmail.nossr50.runnables;

import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;

public class mcSaveTimer implements Runnable {
	private final mcMMO plugin;
	
	public mcSaveTimer(final mcMMO plugin) 
    {
        this.plugin = plugin;
    }
	
	public void run() 
	{
		//All player data will be saved periodically through this
		for(Player player : plugin.getServer().getOnlinePlayers())
		{
			Users.getProfile(player).save();
		}
	}
}