package com.gmail.nossr50.datatypes;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericTexture;

import com.gmail.nossr50.spout.SpoutStuff;

public class HealthBarMMO
{
	public GenericTexture health_bar = null;
	public GenericLabel health_name = null;
	public String playerName = null;
	
	public HealthBarMMO(LivingEntity entity, String name)
	{
		health_name = new GenericLabel();
		health_name.setText(ChatColor.GREEN+name).setDirty(true);
		
		health_bar = new GenericTexture();
		health_bar.setUrl(SpoutStuff.getHealthBarURL(entity.getHealth())).setHeight(8).setWidth(64).setDirty(true);
		
		playerName = name;
	}
}
