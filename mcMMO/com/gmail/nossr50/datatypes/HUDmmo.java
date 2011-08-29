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
import com.gmail.nossr50.spout.mmoHelper;

public class HUDmmo 
{
	int center_x = 427/2;
	int center_y = 240/2;
	
	String playerName = null;
	Widget xpbar = null;
	GenericGradient xpfill = null;
	GenericGradient xpbg = null;
	GenericGradient xpicon_bg = null;
	GenericGradient xpicon_border = null;
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
			updateXpBarStandard(player, Users.getProfile(player));
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
			
			mmoHelper.initialize(sPlayer, plugin);
			sPlayer.getMainScreen().setDirty(true);
		}
	}
	
	private void initializeXpBarDisplayRetro(SpoutPlayer sPlayer)
	{
		Color border = new Color((float)LoadProperties.xpborder_r, (float)LoadProperties.xpborder_g, (float)LoadProperties.xpborder_b, 1f);
		Color green = new Color(0, 1f, 0, 1f);
		Color background = new Color((float)LoadProperties.xpbackground_r, (float)LoadProperties.xpbackground_g, (float)LoadProperties.xpbackground_b, 1f);
		Color darkbg = new Color(0.2f, 0.2f, 0.2f, 1f);
		
		xpicon = new GenericTexture();
		xpbar = new GenericGradient();
		xpfill = new GenericGradient();
		xpbg = new GenericGradient();
		
		xpicon_bg = new GenericGradient();
		xpicon_border = new GenericGradient();
		
		xpicon_bg.setBottomColor(darkbg).setTopColor(darkbg).setWidth(4).setHeight(4).setPriority(RenderPriority.High).setX(142).setY(10).setDirty(true);
		xpicon_border.setBottomColor(border).setTopColor(border).setWidth(6).setHeight(6).setPriority(RenderPriority.Highest).setX(141).setY(9).setDirty(true);
		
		xpicon.setWidth(6).setHeight(6).setX(141).setY(9).setPriority(RenderPriority.Normal).setDirty(true);
		xpicon.setUrl(LoadProperties.web_url+"HUD/Retro/Icon_r.png");
		
		xpbar.setWidth(128).setHeight(4).setX(149).setY(10);
		((GenericGradient) xpbar).setBottomColor(border).setTopColor(border).setPriority(RenderPriority.Highest).setDirty(true);
		
		xpfill.setWidth(0).setHeight(2).setX(150).setY(11);
		xpfill.setBottomColor(green).setTopColor(green).setPriority(RenderPriority.Lowest).setDirty(true);
		
		xpbg.setWidth(126).setHeight(2).setX(150).setY(11);
		xpbg.setBottomColor(background).setTopColor(background).setPriority(RenderPriority.Low).setDirty(true);
		
		if(LoadProperties.xpbar)
		{
			sPlayer.getMainScreen().attachWidget(plugin, (GenericGradient)xpbar);
			sPlayer.getMainScreen().attachWidget(plugin, (GenericGradient)xpfill);
			sPlayer.getMainScreen().attachWidget(plugin, (GenericGradient)xpbg);
			if(LoadProperties.xpicon)
			{
				sPlayer.getMainScreen().attachWidget(plugin, (GenericTexture)xpicon);
				sPlayer.getMainScreen().attachWidget(plugin, (GenericGradient)xpicon_bg);
			}
			sPlayer.getMainScreen().attachWidget(plugin, (GenericGradient)xpicon_border);
		}
		
		sPlayer.getMainScreen().setDirty(true);
	}
	
	private void initializeXpBarDisplayStandard(SpoutPlayer sPlayer)
	{
		//Setup xp bar
		xpbar = new GenericTexture();
		
		if(LoadProperties.xpbar && LoadProperties.xpicon)
		{
			xpicon = new GenericTexture();
			
			xpicon.setUrl(LoadProperties.web_url+"HUD/Standard/Icon.png");
			
			xpicon.setHeight(16).setWidth(32).setX(LoadProperties.xpicon_x).setY(LoadProperties.xpicon_y);
			
			xpicon.setDirty(true);
			
			sPlayer.getMainScreen().attachWidget(plugin, xpicon);
		}
		
		if(LoadProperties.xpbar)
		{
			((GenericTexture)xpbar).setUrl(LoadProperties.web_url+"HUD/Standard/xpbar_inc000.png");
			xpbar.setX(LoadProperties.xpbar_x).setY(LoadProperties.xpbar_y).setHeight(8).setWidth(256);
		
			sPlayer.getMainScreen().attachWidget(plugin, xpbar);
		}
		sPlayer.getMainScreen().setDirty(true);
	}
	
	private void initializeXpBarDisplaySmall(SpoutPlayer sPlayer)
	{
		//Setup xp bar
		xpbar = new GenericTexture();
		
		if(LoadProperties.xpbar && LoadProperties.xpicon)
		{
			xpicon = new GenericTexture();
			
			xpicon.setUrl(LoadProperties.web_url+"HUD/Standard/Icon.png");
			
			xpicon.setHeight(8).setWidth(16).setX(center_x-(8+64)).setY(LoadProperties.xpicon_y+2);
			
			xpicon.setDirty(true);
			
			sPlayer.getMainScreen().attachWidget(plugin, xpicon);
		}
		
		if(LoadProperties.xpbar)
		{
			((GenericTexture)xpbar).setUrl(LoadProperties.web_url+"HUD/Standard/xpbar_inc000.png");
			xpbar.setX(center_x-64).setY(LoadProperties.xpbar_y).setHeight(4).setWidth(128);
		
			sPlayer.getMainScreen().attachWidget(plugin, xpbar);
		}
		
		sPlayer.getMainScreen().setDirty(true);
	}
	
	private void updateXpBarStandard(Player player, PlayerProfile PP)
	{
		if(!LoadProperties.xpbar)
			return;
		
		SkillType theType = null;
		
		if(PP.getXpBarLocked())
			theType=PP.getSkillLock();
		else
			theType=PP.getLastGained();
		
		if(theType == null)
			return;
		
		((GenericTexture) xpicon).setUrl(LoadProperties.web_url+"HUD/Standard/"+m.getCapitalized(theType.toString())+".png");
		xpicon.setDirty(true);

		((GenericTexture) xpbar).setUrl(SpoutStuff.getUrlBar(SpoutStuff.getXpInc(PP.getSkillXpLevel(theType), PP.getXpToLevel(theType), HUDType.STANDARD)));
		xpbar.setDirty(true);
		
		SpoutManager.getPlayer(player).getMainScreen().setDirty(true);
	}
	
	private void updateXpBarRetro(Player player, PlayerProfile PP)
	{
		if(!LoadProperties.xpbar)
			return;
		SkillType theType = null;
		
		if(PP.getXpBarLocked() && PP.getSkillLock() != null)
			theType=PP.getSkillLock();
		else
			theType=PP.getLastGained();
		
		if(theType == null)
			return;
		
		Color color = SpoutStuff.getRetroColor(theType);
		
		if(xpicon != null && theType != null)
			xpicon.setUrl(LoadProperties.web_url+"HUD/Retro/"+m.getCapitalized(theType.toString())+"_r.png");
		
		if(theType != null)
			xpfill.setBottomColor(color).setTopColor(color).setWidth(SpoutStuff.getXpInc(PP.getSkillXpLevel(theType), PP.getXpToLevel(theType), HUDType.RETRO)).setDirty(true);
		else
			System.out.println("theType was null!");
		
		SpoutManager.getPlayer(player).getMainScreen().setDirty(true);
	}
}