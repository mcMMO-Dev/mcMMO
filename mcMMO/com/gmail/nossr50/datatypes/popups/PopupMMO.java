package com.gmail.nossr50.datatypes.popups;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.buttons.ButtonHUDStyle;

public class PopupMMO extends GenericPopup
{
	ButtonHUDStyle HUDButton = null;
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
		
		this.attachWidget(plugin, HUDButton);
		this.attachWidget(plugin, mcMMO_label);
		this.attachWidget(plugin, tip_escape);
		
		this.setDirty(true);
	}
	
	public void updateButtons(PlayerProfile PP)
	{
		HUDButton.updateText(PP);
		this.setDirty(true);
	}
}
