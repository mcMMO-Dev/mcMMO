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