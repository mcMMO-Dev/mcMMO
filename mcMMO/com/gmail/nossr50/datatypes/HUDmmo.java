package com.gmail.nossr50.datatypes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.spout.SpoutStuff;

public class HUDmmo 
{
	String playerName = null;
	Widget xpbar = null;
	GenericGradient xpfill = null;
	GenericGradient xpbg = null;
	GenericTexture xpicon = null;
	mcMMO plugin = (mcMMO) Bukkit.getServer().getPluginManager().getPlugin("mcMMO");
	
	public HUDmmo(Player player)
	{
		playerName = player.getName();
		initializeHUD(player);
	}
	public void initializeHUD(Player player)
	{
		HUDType type = Users.getProfile(player).getHUDType();
		switch(type)
		{
			case RETRO:
			{
				initializeXpBarDisplayRetro(SpoutManager.getPlayer(player));
				break;
			}
			case STANDARD:
			{
				initializeXpBarDisplayStandard(SpoutManager.getPlayer(player));
				break;
			}
			case SMALL:
			{
				initializeXpBarDisplaySmall(SpoutManager.getPlayer(player));
				break;
			}
		}
	}
	
	public void updateXpBarDisplay(HUDType type, Player player)
	{
		switch(type)
		{
		case RETRO:
		{
			updateXpBarRetro(player, Users.getProfile(player));
			break;
		}
		case STANDARD:
		{
			updateXpBarStandard(player, Users.getProfile(player));
			break;
		}
		case SMALL:
		{
			break;
		}
		}
	}
	
	public void resetHUD()
	{
		SpoutPlayer sPlayer = SpoutStuff.getSpoutPlayer(playerName);
		if(sPlayer != null)
		{
			sPlayer.getMainScreen().removeWidgets(plugin);
			
			//Reset the objects
			xpbar = null;
			xpfill = null;
			xpbg = null;
			xpicon = null;
			
			sPlayer.getMainScreen().setDirty(true);
		}
	}
	
	private void initializeXpBarDisplayRetro(SpoutPlayer sPlayer)
	{
		Color black = new Color(0, 0, 0, 1f);
		Color green = new Color(0, 1f, 0, 1f);
		Color gray = new Color(0.75f, 0.75f, 0.75f, 1f);
		
		xpicon = new GenericTexture();
		xpbar = new GenericGradient();
		xpfill = new GenericGradient();
		xpbg = new GenericGradient();
		
		xpicon.setWidth(6).setHeight(6).setX(149-6).setY(9).setDirty(true);
		xpicon.setUrl(LoadProperties.web_url+"HUD/Retro/Icon_r.png");
		
		xpbar.setWidth(128).setHeight(4).setX(149).setY(10);
		((GenericGradient) xpbar).setBottomColor(black).setTopColor(black).setPriority(RenderPriority.Highest).setDirty(true);
		
		xpfill.setWidth(0).setHeight(2).setX(150).setY(11);
		xpfill.setBottomColor(green).setTopColor(green).setPriority(RenderPriority.Lowest).setDirty(true);
		
		xpbg.setWidth(126).setHeight(2).setX(150).setY(11);
		xpbg.setBottomColor(gray).setTopColor(gray).setPriority(RenderPriority.Low).setDirty(true);
		
		sPlayer.getMainScreen().attachWidget(plugin, (GenericGradient)xpbar);
		sPlayer.getMainScreen().attachWidget(plugin, (GenericGradient)xpfill);
		sPlayer.getMainScreen().attachWidget(plugin, (GenericGradient)xpbg);
		sPlayer.getMainScreen().attachWidget(plugin, (GenericTexture)xpicon);
		
		sPlayer.getMainScreen().setDirty(true);
	}
	
	private void initializeXpBarDisplayStandard(SpoutPlayer sPlayer)
	{
		//Setup xp bar
		xpbar = new GenericTexture();
		
		if(LoadProperties.xpicon)
		{
			xpicon = new GenericTexture();
			
			xpicon.setUrl(LoadProperties.web_url+"HUD/Standard/Icon.png");
			
			xpicon.setHeight(16).setWidth(32).setX(LoadProperties.xpicon_x).setY(LoadProperties.xpicon_y);
			
			xpicon.setDirty(true);
			
			sPlayer.getMainScreen().attachWidget(plugin, xpicon);
		}
		
		((GenericTexture)xpbar).setUrl(LoadProperties.web_url+"HUD/Standard/xpbar_inc000.png");
		xpbar.setX(LoadProperties.xpbar_x).setY(LoadProperties.xpbar_y).setHeight(8).setWidth(256);
		
		sPlayer.getMainScreen().attachWidget(plugin, xpbar);
		
		sPlayer.getMainScreen().setDirty(true);
	}
	
	private void initializeXpBarDisplaySmall(SpoutPlayer sPlayer)
	{
		//Coordinates 240, 427 are the bottom right.
		GenericTexture xpbar = new GenericTexture();
		GenericTexture xpbar_fill = new GenericTexture();
		
		xpbar.setUrl("http://dl.dropbox.com/u/18212134/xpbar/mini/bar.png");
		xpbar_fill.setUrl("http://dl.dropbox.com/u/18212134/xpbar/mini/bar_fill.png");
		
		xpbar.setWidth(128).setHeight(4).setX(149).setY(10).setDirty(true);
		xpbar_fill.setWidth(2).setHeight(2).setX(150).setY(11).setPriority(RenderPriority.High).setDirty(true);
		
		sPlayer.getMainScreen().attachWidget(plugin, xpbar);
		sPlayer.getMainScreen().attachWidget(plugin, xpbar_fill);
		
		sPlayer.getMainScreen().setDirty(true);
	}
	
	private void updateXpBarStandard(Player player, PlayerProfile PP)
	{
		SkillType theType = null;
		
		if(PP.getXpBarLocked())
			theType=PP.getSkillLock();
		else
			theType=PP.getLastGained();
		
		((GenericTexture) xpicon).setUrl(LoadProperties.web_url+"HUD/Standard/"+m.getCapitalized(theType.toString())+".png");
		xpicon.setDirty(true);

		((GenericTexture) xpbar).setUrl(SpoutStuff.getUrlBar(SpoutStuff.getXpInc(PP.getSkillXpLevel(theType), PP.getXpToLevel(theType), HUDType.STANDARD)));
		xpbar.setDirty(true);
		
		SpoutManager.getPlayer(player).getMainScreen().setDirty(true);
	}
	private void updateXpBarRetro(Player player, PlayerProfile PP)
	{
		SkillType theType = null;
		
		if(PP.getXpBarLocked() && PP.getSkillLock() != null)
			theType=PP.getSkillLock();
		else
			theType=PP.getLastGained();
		
		Color color = new Color(0.3f, 0.3f, 0.75f, 1f);
		
		if(xpicon != null && theType != null)
			xpicon.setUrl(LoadProperties.web_url+"HUD/Retro/"+m.getCapitalized(theType.toString())+"_r.png");
		else
		{
			if(xpicon == null)
				System.out.println("xpicon was null!");
			if(theType == null)
				System.out.println("theType was null!");
		}
		
		if(theType != null)
			xpfill.setBottomColor(color).setTopColor(color).setWidth(SpoutStuff.getXpInc(PP.getSkillXpLevel(theType), PP.getXpToLevel(theType), HUDType.RETRO)).setDirty(true);
		else
			System.out.println("theType was null!");
		
		SpoutManager.getPlayer(player).getMainScreen().setDirty(true);
	}
}