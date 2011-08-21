package com.gmail.nossr50.spout;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundEffect;
import org.getspout.spoutapi.sound.SoundManager;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.HUDType;
import com.gmail.nossr50.datatypes.HUDmmo;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.popups.PopupMMO;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.datatypes.HealthBarMMO;
import com.gmail.nossr50.listeners.mcSpoutInputListener;
import com.gmail.nossr50.listeners.mcSpoutListener;
import com.gmail.nossr50.listeners.mcSpoutScreenListener;
import com.gmail.nossr50.party.Party;

public class SpoutStuff 
{
	static mcMMO plugin = (mcMMO) Bukkit.getServer().getPluginManager().getPlugin("mcMMO");
	
	private final static mcSpoutListener spoutListener = new mcSpoutListener();
	private final static mcSpoutInputListener spoutInputListener = new mcSpoutInputListener(plugin);
	private final static mcSpoutScreenListener spoutScreenListener = new mcSpoutScreenListener(plugin);
	
	public static HashMap<Player, HUDmmo> playerHUDs = new HashMap<Player, HUDmmo>();
	public static HashMap<Player, ArrayList<HealthBarMMO>> partyHealthBars = new HashMap<Player, ArrayList<HealthBarMMO>>();
	public static HashMap<SpoutPlayer, PopupMMO> playerScreens = new HashMap<SpoutPlayer, PopupMMO>();
	
	public static Keyboard keypress;
	
	public static void setupSpoutConfigs()
	{
		 String temp = LoadProperties.readString("Spout.Menu.Key", "KEY_M");
	        
	        for(Keyboard x : Keyboard.values())
	        {
	        	if(x.toString().equalsIgnoreCase(temp))
	        	{
	        		keypress = x;
	        	}
	        }
	        
     	if(keypress == null)
     	{
     		System.out.println("Invalid KEY for Spout.Menu.Key, using KEY_M");
     		keypress = Keyboard.KEY_M;
     	}
	}
	public static void registerCustomEvent()
	{
		Bukkit.getServer().getPluginManager().registerEvent(Event.Type.CUSTOM_EVENT, spoutListener, Priority.Normal, plugin);
		Bukkit.getServer().getPluginManager().registerEvent(Event.Type.CUSTOM_EVENT, spoutInputListener, Priority.Normal, plugin);
		Bukkit.getServer().getPluginManager().registerEvent(Event.Type.CUSTOM_EVENT, spoutScreenListener, Priority.Normal, plugin);
	}
	
	public static Color getRetroColor(SkillType type)
	{
		switch(type)
		{
			case ACROBATICS:
				return new Color((float) LoadProperties.acrobatics_r, (float) LoadProperties.acrobatics_g, (float) LoadProperties.acrobatics_b, 1);
			case ARCHERY:
				return new Color((float) LoadProperties.archery_r, (float)LoadProperties.archery_g, (float)LoadProperties.archery_b, 1f);
			case AXES:
				return new Color((float) LoadProperties.axes_r, (float)LoadProperties.axes_g, (float)LoadProperties.axes_b, 1f);
			case EXCAVATION:
				return new Color((float)LoadProperties.excavation_r, (float)LoadProperties.excavation_g, (float)LoadProperties.excavation_b, 1f);
			case HERBALISM:
				return new Color((float)LoadProperties.herbalism_r, (float)LoadProperties.herbalism_g, (float)LoadProperties.herbalism_b, 1f);
			case MINING:
				return new Color((float)LoadProperties.mining_r, (float)LoadProperties.mining_g, (float)LoadProperties.mining_b, 1f);
			case REPAIR:
				return new Color((float)LoadProperties.repair_r, (float)LoadProperties.repair_g, (float)LoadProperties.repair_b, 1f);
			case SORCERY:
				return new Color(0.3f, 0.3f, 0.75f, 1f);
			case SWORDS:
				return new Color((float)LoadProperties.swords_r, (float)LoadProperties.swords_g, (float)LoadProperties.swords_b, 1f);
			case TAMING:
				return new Color((float)LoadProperties.taming_r, (float)LoadProperties.taming_g, (float)LoadProperties.taming_b, 1f);
			case UNARMED:
				return new Color((float)LoadProperties.unarmed_r, (float)LoadProperties.unarmed_g, (float)LoadProperties.unarmed_b, 1f);
			case WOODCUTTING:
				return new Color((float)LoadProperties.woodcutting_r, (float)LoadProperties.woodcutting_g, (float)LoadProperties.woodcutting_b, 1f);
			default:
				return new Color(0.3f, 0.3f, 0.75f, 1f);
		}
	}
	public static SpoutPlayer getSpoutPlayer(String playerName)
	{
		for(Player x : Bukkit.getServer().getOnlinePlayers())
		{
			if(x.getName().equalsIgnoreCase(playerName))
			{
				return SpoutManager.getPlayer(x);
			}
		}
		return null;
	}
	
	public static String getHealthBarURL(Integer hp)
	{
		String url = "";
		
		if(hp.toString().toCharArray().length > 1)
			url = LoadProperties.web_url+"HUD/Standard/health_inc"+hp+".png";
		else
			url = LoadProperties.web_url+"HUD/Standard/health_inc0"+hp+".png";
		
		return url;
	}
	
	public static void playSoundForPlayer(SoundEffect effect, Player player, Location location)
	{
		//Contrib stuff
		SoundManager SM = SpoutManager.getSoundManager();
		SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
		SM.playSoundEffect(sPlayer, effect, location);
	}
	
	public static void initializePartyTracking(SpoutPlayer player)
	{
		if(Users.getProfile(player).inParty())
		{
			int pos = LoadProperties.partybar_y;
			
			ArrayList<HealthBarMMO> hpbars = new ArrayList<HealthBarMMO>();
			for(Player x : Party.getInstance().getPartyMembers(player))
			{
				if(x.isOnline())
				{
					HealthBarMMO hpbar = new HealthBarMMO(x, x.getName());
					hpbar.health_name.setX(LoadProperties.partybar_x+11).setY(pos);
					hpbar.health_bar.setX(LoadProperties.partybar_x).setY(pos+8);
					hpbars.add(hpbar);
					pos+=LoadProperties.partybar_spacing;
				}
			}
			
			if(hpbars.size() >= 1)
				partyHealthBars.put(player, hpbars);
			
			if(partyHealthBars.get(player) != null)
			{
				for(HealthBarMMO x : partyHealthBars.get(player))
				{
					if(x != null)
					{
						player.getMainScreen().attachWidget(plugin, x.health_bar);
						player.getMainScreen().attachWidget(plugin, x.health_name);
					}
				}
				
				player.getMainScreen().setDirty(true);
			}
		}
	}
	public static void resetPartyHealthBarDisplays(final ArrayList<Player> players)
	{
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
		new Runnable() 
		{
			public void run() 
			{
				
				for (Player x : players) 
				{
					if(partyHealthBars.get(x) != null)
					{
						final SpoutPlayer sPlayer = SpoutManager.getPlayer(x);
						if (sPlayer.isSpoutCraftEnabled()) 
						{
							ArrayList<Widget> widgets = new ArrayList<Widget>();
							for (Widget w : sPlayer.getMainScreen().getAttachedWidgets()) 
							{
								for (HealthBarMMO hp : partyHealthBars.get(x))
								{
									if(w.getId() == hp.health_bar.getId() || w.getId() == hp.health_name.getId())
									{
										widgets.add(w);
									}
								}
							}
							for (Widget w : widgets) 
							{
								sPlayer.getMainScreen().removeWidget(w);
							}
	
							sPlayer.getMainScreen().setDirty(true);
							partyHealthBars.get(x).clear();
	
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
									new Runnable() 
									{
										public void run() {
											initializePartyTracking(sPlayer);
										}
									}, 1);
						}
					} else if (SpoutManager.getPlayer(x).isSpoutCraftEnabled())
					{
						initializePartyTracking(SpoutManager.getPlayer(x));
					}
				}
			}
		}, 1);
	}
	
	public static void resetPartyHealthBarDisplays(final Player player)
	{
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
		new Runnable() 
		{
			public void run() 
			{
				if(partyHealthBars.get(player) != null)
				{
					SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
					if (sPlayer.isSpoutCraftEnabled()) 
					{
						System.out.println("Resetting health bars for "+player.getName());
						ArrayList<Widget> widgets = new ArrayList<Widget>();
						for (Widget w : sPlayer.getMainScreen().getAttachedWidgets()) 
						{
							for (HealthBarMMO hp : partyHealthBars.get(player))
							{
								if(w.getId() == hp.health_bar.getId() || w.getId() == hp.health_name.getId())
								{
									widgets.add(w);
								}
							}
						}
						for (Widget w : widgets) 
						{
							System.out.println("Removing hpbar for "+sPlayer.getName());
							sPlayer.getMainScreen().removeWidget(w);
						}
						sPlayer.getMainScreen().setDirty(true);
						partyHealthBars.get(player).clear();
						
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
						new Runnable() 
						{
							public void run() {
								initializePartyTracking(SpoutManager
										.getPlayer(player));
							}
						}, 1);
					}
				} else if (SpoutManager.getPlayer(player).isSpoutCraftEnabled())
				{
					initializePartyTracking(SpoutManager.getPlayer(player));
				}
			}
		}, 1);
	}
	
	public static void updatePartyHealthBarDisplay(Player player, Integer hp)
	{
		for(Player x : Party.getInstance().getPartyMembers(player))
		{
			SpoutPlayer sPlayer = SpoutManager.getPlayer(x);
			if(sPlayer.isSpoutCraftEnabled())
			{
				for(HealthBarMMO y : partyHealthBars.get(x))
				{
					if(y.playerName.equalsIgnoreCase(player.getName()))
					{
						y.health_bar.setUrl(getHealthBarURL(hp)).setDirty(true);
						sPlayer.getMainScreen().setDirty(true);
					}
				}
			}
		}
	}
	
	public static void playRepairNoise(Player player)
	{
		SoundManager SM = SpoutManager.getSoundManager();
		SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
		SM.playCustomSoundEffect(Bukkit.getServer().getPluginManager().getPlugin("mcMMO"), sPlayer, LoadProperties.web_url+"/Sound/repair.wav", false);
	}
	public static void playLevelUpNoise(Player player)
	{
		SoundManager SM = SpoutManager.getSoundManager();
		SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
		SM.playCustomSoundEffect(Bukkit.getServer().getPluginManager().getPlugin("mcMMO"), sPlayer, LoadProperties.web_url+"/Sound/level.wav", false);
	}
	
	public static void levelUpNotification(SkillType skillType, SpoutPlayer sPlayer)
	{
		PlayerProfile PP = Users.getProfile(sPlayer);
		
		Material mat = null;
		switch(skillType)
		{
		case SORCERY:
			mat = Material.PORTAL;
			break;
		case TAMING:
			switch(getNotificationTier(PP.getSkillLevel(skillType)))
			{
			case 1:
				mat = Material.PORK;
				break;
			case 2:
				mat = Material.PORK;
				break;
			case 3:
				mat = Material.GRILLED_PORK;
				break;
			case 4:
				mat = Material.GRILLED_PORK;
				break;
			case 5:
				mat = Material.BONE;
				break;
			}
			break;
		case MINING:
			switch(getNotificationTier(PP.getSkillLevel(skillType)))
			{
			case 1:
				mat = Material.COAL_ORE;
				break;
			case 2:
				mat = Material.IRON_ORE;
				break;
			case 3:
				mat = Material.GOLD_ORE;
				break;
			case 4:
				mat = Material.LAPIS_ORE;
				break;
			case 5:
				mat = Material.DIAMOND_ORE;
				break;
			}
			break;
		case WOODCUTTING:
			switch(getNotificationTier(PP.getSkillLevel(skillType)))
			{
			case 1:
				mat = Material.WOOD;
				break;
			case 2:
				mat = Material.WOOD;
				break;
			case 3:
				mat = Material.WOOD;
				break;
			case 4:
				mat = Material.LOG;
				break;
			case 5:
				mat = Material.LOG;
				break;
			}
			break;
		case REPAIR:
			switch(getNotificationTier(PP.getSkillLevel(skillType)))
			{
			case 1:
				mat = Material.COBBLESTONE;
				break;
			case 2:
				mat = Material.IRON_BLOCK;
				break;
			case 3:
				mat = Material.GOLD_BLOCK;
				break;
			case 4:
				mat = Material.LAPIS_BLOCK;
				break;
			case 5:
				mat = Material.DIAMOND_BLOCK;
				break;
			}
			break;
		case HERBALISM:
			switch(getNotificationTier(PP.getSkillLevel(skillType)))
			{
			case 1:
				mat = Material.YELLOW_FLOWER;
				break;
			case 2:
				mat = Material.RED_ROSE;
				break;
			case 3:
				mat = Material.BROWN_MUSHROOM;
				break;
			case 4:
				mat = Material.RED_MUSHROOM;
				break;
			case 5:
				mat = Material.PUMPKIN;
				break;
			}
			break;
		case ACROBATICS:
			switch(getNotificationTier(PP.getSkillLevel(skillType)))
			{
			case 1:
				mat = Material.LEATHER_BOOTS;
				break;
			case 2:
				mat = Material.CHAINMAIL_BOOTS;
				break;
			case 3:
				mat = Material.IRON_BOOTS;
				break;
			case 4:
				mat = Material.GOLD_BOOTS;
				break;
			case 5:
				mat = Material.DIAMOND_BOOTS;
				break;
			}
			break;
		case SWORDS:
			switch(getNotificationTier(PP.getSkillLevel(skillType)))
			{
			case 1:
				mat = Material.WOOD_SWORD;
				break;
			case 2:
				mat = Material.STONE_SWORD;
				break;
			case 3:
				mat = Material.IRON_SWORD;
				break;
			case 4:
				mat = Material.GOLD_SWORD;
				break;
			case 5:
				mat = Material.DIAMOND_SWORD;
				break;
			}
			break;
		case ARCHERY:
			mat = Material.ARROW;
			break;
		case UNARMED:
			switch(getNotificationTier(PP.getSkillLevel(skillType)))
			{
			case 1:
				mat = Material.LEATHER_HELMET;
				break;
			case 2:
				mat = Material.CHAINMAIL_HELMET;
				break;
			case 3:
				mat = Material.IRON_HELMET;
				break;
			case 4:
				mat = Material.GOLD_HELMET;
				break;
			case 5:
				mat = Material.DIAMOND_HELMET;
				break;
			}
			break;
		case EXCAVATION:
			switch(getNotificationTier(PP.getSkillLevel(skillType)))
			{
			case 1:
				mat = Material.WOOD_SPADE;
				break;
			case 2:
				mat = Material.STONE_SPADE;
				break;
			case 3:
				mat = Material.IRON_SPADE;
				break;
			case 4:
				mat = Material.GOLD_SPADE;
				break;
			case 5:
				mat = Material.DIAMOND_SPADE;
				break;
			}
			break;
		case AXES:
			switch(getNotificationTier(PP.getSkillLevel(skillType)))
			{
			case 1:
				mat = Material.WOOD_AXE;
				break;
			case 2:
				mat = Material.STONE_AXE;
				break;
			case 3:
				mat = Material.IRON_AXE;
				break;
			case 4:
				mat = Material.GOLD_AXE;
				break;
			case 5:
				mat = Material.DIAMOND_AXE;
				break;
			}
			break;
		default:
			mat = Material.WATCH;
			break;
		}
			sPlayer.sendNotification(ChatColor.GREEN+"Level Up!", ChatColor.YELLOW+m.getCapitalized(skillType.toString())+ChatColor.DARK_AQUA+" ("+ChatColor.GREEN+PP.getSkillLevel(skillType)+ChatColor.DARK_AQUA+")", mat);
			playLevelUpNoise(sPlayer);
	}
	
	public static Integer getNotificationTier(Integer level)
	{
		if(level < 200)
			return 1;
		else if (level >= 200 && level < 400)
			return 2;
		else if (level >= 400 && level < 600)
			return 3;
		else if (level >= 600 && level < 800)
			return 4;
		else
			return 5;
	}
	
	public static Integer getXpInc(int skillxp, int xptolevel, HUDType hud)
	{
		if(hud == HUDType.STANDARD)
		{
			double percentage = (double) skillxp/xptolevel;
			double inc = 0.0039370078740157;
			return (int) (percentage/inc);
		} else if (hud == HUDType.RETRO)
		{
			double percentage = (double) skillxp/xptolevel;
			double inc = 0.0079365079365079;
			return (int) (percentage/inc);
		} else {
			return 1;
		}
	}
	
	public static void updateXpBar(Player player)
	{
		playerHUDs.get(player).updateXpBarDisplay(Users.getProfile(player).getHUDType(), player);
	}
	
	public static String getUrlBar(Integer number)
	{
		if(number.toString().toCharArray().length == 1)
		{
			return LoadProperties.web_url+"HUD/Standard/xpbar_inc00"+number+".png";
		} else if (number.toString().toCharArray().length == 2)
		{
			return LoadProperties.web_url+"HUD/Standard/xpbar_inc0"+number+".png";
		} else {
			return LoadProperties.web_url+"HUD/Standard/xpbar_inc"+number+".png";
		}
	}
	public static String getUrlIcon(SkillType skillType)
	{
		return LoadProperties.web_url+"HUD/Standard/"+m.getCapitalized(skillType.toString())+".png";
	}
	public static boolean shouldBeFilled(PlayerProfile PP)
	{
		return PP.getXpBarInc() < getXpInc(PP.getSkillXpLevel(PP.getLastGained()), PP.getXpToLevel(PP.getLastGained()), HUDType.STANDARD);
	}
}
