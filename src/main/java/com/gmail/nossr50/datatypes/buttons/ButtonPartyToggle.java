package com.gmail.nossr50.datatypes.buttons;

import org.getspout.spoutapi.gui.GenericButton;

import com.gmail.nossr50.datatypes.PlayerProfile;

public class ButtonPartyToggle extends GenericButton
{
	public ButtonPartyToggle(PlayerProfile PP)
	{
		this.setText("Party HUD: "+PP.getPartyHUD());
		this.setTooltip("Toggle the Party HUD!");
		this.setWidth(120).setHeight(20);
		this.setDirty(true);
	}
	public void updateText(PlayerProfile PP)
	{
		this.setText("Party HUD: "+PP.getPartyHUD());
		this.setDirty(true);
	}
}