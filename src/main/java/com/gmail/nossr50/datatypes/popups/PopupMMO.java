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
package com.gmail.nossr50.datatypes.popups;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.buttons.ButtonEscape;
import com.gmail.nossr50.datatypes.buttons.ButtonHUDStyle;
import com.gmail.nossr50.datatypes.buttons.ButtonPartyToggle;

public class PopupMMO extends GenericPopup
{
	ButtonHUDStyle HUDButton = null;
	ButtonPartyToggle PartyButton = null;
	ButtonEscape EscapeButton = null;
	GenericLabel mcMMO_label = new GenericLabel();
	GenericLabel tip_escape = new GenericLabel();
	int center_x = 427/2;
	int center_y = 240/2;
	
	public PopupMMO(Player player, PlayerProfile PP, mcMMO plugin)
	{
		//240, 427 are the bottom right
		mcMMO_label.setText(ChatColor.GOLD+"~mcMMO Menu~");
		mcMMO_label.setX(center_x-35).setY((center_y/2)-20).setDirty(true);
		
		tip_escape.setText(ChatColor.GRAY+"Press ESCAPE to exit!");
		tip_escape.setX(mcMMO_label.getX()-15).setY(mcMMO_label.getY()+10).setDirty(true);
		
		HUDButton = new ButtonHUDStyle(PP);
		HUDButton.setX(center_x-(HUDButton.getWidth()/2)).setY(center_y/2).setDirty(true);
		
		if(LoadProperties.partybar)
		{
			PartyButton = new ButtonPartyToggle(PP);
			PartyButton.setX(center_x-(PartyButton.getWidth()/2)).setY(center_y/2+PartyButton.getHeight()).setDirty(true);
			this.attachWidget(plugin, PartyButton);
		}
		
		EscapeButton = new ButtonEscape();
		EscapeButton.setX(center_x-(EscapeButton.getWidth()/2)).setY((center_y/2)+(HUDButton.getHeight()*2)+5).setDirty(true);
		
		this.attachWidget(plugin, HUDButton);
		this.attachWidget(plugin, mcMMO_label);
		this.attachWidget(plugin, tip_escape);
		this.attachWidget(plugin, EscapeButton);
		
		this.setDirty(true);
	}
	
	public void updateButtons(PlayerProfile PP)
	{
		HUDButton.updateText(PP);
		this.setDirty(true);
	}
}
