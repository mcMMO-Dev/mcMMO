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
package com.gmail.nossr50.datatypes.buttons;

import org.getspout.spoutapi.gui.GenericButton;

import com.gmail.nossr50.datatypes.PlayerProfile;

public class ButtonHUDStyle extends GenericButton
{
	public ButtonHUDStyle(PlayerProfile PP)
	{
		this.setText("HUD Type: "+PP.getHUDType().toString());
		this.setTooltip("Change your HUD style!");
		this.setWidth(120).setHeight(20);
		this.setDirty(true);
	}
	public void updateText(PlayerProfile PP)
	{
		this.setText("HUD Type: "+PP.getHUDType().toString());
		this.setDirty(true);
	}
}