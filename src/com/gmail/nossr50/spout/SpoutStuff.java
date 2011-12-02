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
package com.gmail.nossr50.spout;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.Color;
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
import com.gmail.nossr50.listeners.mcSpoutInputListener;
import com.gmail.nossr50.listeners.mcSpoutListener;
import com.gmail.nossr50.listeners.mcSpoutScreenListener;

public class SpoutStuff 
{
	static mcMMO plugin = (mcMMO) Bukkit.getServer().getPluginManager().getPlugin("mcMMO");
	
	private final static mcSpoutListener spoutListener = new mcSpoutListener(plugin);
	private final static mcSpoutInputListener spoutInputListener = new mcSpoutInputListener(plugin);
	private final static mcSpoutScreenListener spoutScreenListener = new mcSpoutScreenListener(plugin);
	
	public static HashMap<Player, HUDmmo> playerHUDs = new HashMap<Player, HUDmmo>();
	public static HashMap<SpoutPlayer, PopupMMO> playerScreens = new HashMap<SpoutPlayer, PopupMMO>();
	
	public static Keyboard keypress;
	
	public static void writeFile(String theFileName, String theFilePath)
	{
	    try {
	        File currentFile = new File("plugins/mcMMO/Resources/"+theFilePath+theFileName);
	        @SuppressWarnings("static-access")
	        JarFile jar = new JarFile(plugin.mcmmo);
	        JarEntry entry = jar.getJarEntry("resources/"+theFileName);
	        InputStream is = jar.getInputStream(entry);
	        byte[] buf = new byte[2048];
	        int nbRead;
	        OutputStream os = new BufferedOutputStream(new FileOutputStream(currentFile));
	        while((nbRead = is.read(buf)) != -1) {
	            os.write(buf, 0, nbRead);
	        }
	        os.flush();
	        os.close();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public static void extractFiles()
	{
		//Setup directories
		new File("plugins/mcMMO/Resources/").mkdir();
		new File("plugins/mcMMO/Resources/HUD/").mkdir();
		new File("plugins/mcMMO/Resources/HUD/Standard/").mkdir();
		new File("plugins/mcMMO/Resources/HUD/Retro/").mkdir();
		new File("plugins/mcMMO/Resources/Sound/").mkdir();
		
		//Xp Bar images
		for(int x =0; x < 255; x++)
		{
			//String s = File.separator;
			String theFilePath = "HUD/Standard/";
			if(x < 10)
			{
				String theFileName = "xpbar_inc00"+x+".png";
				writeFile(theFileName, theFilePath);
			} else if (x < 100)
			{
				String theFileName = "xpbar_inc0"+x+".png";
				writeFile(theFileName, theFilePath);
			} else 
			{
				String theFileName = "xpbar_inc"+x+".png";
				writeFile(theFileName, theFilePath);
			}
		}
		
		//Standard XP Icons
		
		String theFilePathA = "HUD/Standard/";
		String theFilePathB = "HUD/Retro/";
		
		for(SkillType y : SkillType.values())
		{
			if(y == SkillType.ALL || y == SkillType.ENCHANTING || y == SkillType.ALCHEMY)
				continue;
			
			String theFileNameA = m.getCapitalized(y.toString())+".png";
			String theFileNameB = m.getCapitalized(y.toString())+"_r.png";
			
			writeFile(theFileNameA, theFilePathA);
			writeFile(theFileNameB, theFilePathB);
		}
		
		//Blank icons
		
		writeFile("Icon.png", theFilePathA);
		writeFile("Icon_r.png", theFilePathB);
		
		String theSoundFilePath = "Sound/";
		//Repair SFX
		writeFile("repair.wav", theSoundFilePath);
		writeFile("level.wav", theSoundFilePath);
	}
	
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
	public static ArrayList<File> getFiles()
	{
		ArrayList<File> files = new ArrayList<File>();
		String dir = "plugins/mcMMO/Resources/";
		int x = 0;
		
		//XP BAR
		while(x < 255)
		{
			if(x < 10)
			{
				files.add(new File(dir+"HUD/Standard/xpbar_inc00"+x+".png"));
			} else if (x < 100)
			{
				files.add(new File(dir+"HUD/Standard/xpbar_inc0"+x+".png"));
			} else 
			{
				files.add(new File(dir+"HUD/Standard/xpbar_inc"+x+".png"));
			}
			x++;
		}
		
		//Standard XP Icons
		for(SkillType y : SkillType.values())
		{
			if(y == SkillType.ALL || y == SkillType.ENCHANTING || y == SkillType.ALCHEMY)
				continue;
			files.add(new File(dir+"HUD/Standard/"+m.getCapitalized(y.toString())+".png"));
			files.add(new File(dir+"HUD/Retro/"+m.getCapitalized(y.toString())+"_r.png"));
		}
		
		//Blank icons
		files.add(new File(dir+"HUD/Standard/Icon.png"));
		files.add(new File(dir+"HUD/Retro/Icon_r.png"));
		//Repair SFX
		files.add(new File(dir+"Sound/repair.wav"));
		//Level SFX
		files.add(new File(dir+"Sound/level.wav"));
		
		return files;
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
			case SWORDS:
				return new Color((float)LoadProperties.swords_r, (float)LoadProperties.swords_g, (float)LoadProperties.swords_b, 1f);
			case TAMING:
				return new Color((float)LoadProperties.taming_r, (float)LoadProperties.taming_g, (float)LoadProperties.taming_b, 1f);
			case UNARMED:
				return new Color((float)LoadProperties.unarmed_r, (float)LoadProperties.unarmed_g, (float)LoadProperties.unarmed_b, 1f);
			case WOODCUTTING:
				return new Color((float)LoadProperties.woodcutting_r, (float)LoadProperties.woodcutting_g, (float)LoadProperties.woodcutting_b, 1f);
			case FISHING:
				return new Color((float)LoadProperties.fishing_r, (float)LoadProperties.fishing_g, (float)LoadProperties.fishing_b, 1f);
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
	
	public static void playSoundForPlayer(SoundEffect effect, Player player, Location location)
	{
		//Contrib stuff
		SoundManager SM = SpoutManager.getSoundManager();
		SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
		SM.playSoundEffect(sPlayer, effect, location);
	}
	
	public static void playRepairNoise(Player player)
	{
		SoundManager SM = SpoutManager.getSoundManager();
		SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
		SM.playCustomSoundEffect(Bukkit.getServer().getPluginManager().getPlugin("mcMMO"), sPlayer, "http://mcmmo.rycochet.net/mcmmo/Sound/repair.wav", false);
	}
	
	public static void playLevelUpNoise(Player player)
	{
		SoundManager SM = SpoutManager.getSoundManager();
		SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
		SM.playCustomSoundEffect(Bukkit.getServer().getPluginManager().getPlugin("mcMMO"), sPlayer, "http://mcmmo.rycochet.net/mcmmo/Sound/level.wav", false);
	}
	
	public static void levelUpNotification(SkillType skillType, SpoutPlayer sPlayer)
	{
		PlayerProfile PP = Users.getProfile(sPlayer);
		
		Material mat = null;
		switch(skillType)
		{
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
		case FISHING:
			switch(getNotificationTier(PP.getSkillLevel(skillType)))
			{
			case 1:
				mat = Material.RAW_FISH;
				break;
			case 2:
				mat = Material.RAW_FISH;
				break;
			case 3:
				mat = Material.COOKED_FISH;
				break;
			case 4:
				mat = Material.COOKED_FISH;
				break;
			case 5:
				mat = Material.FISHING_ROD;
				break;
			}
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
			return "xpbar_inc00"+number+".png";
		} else if (number.toString().toCharArray().length == 2)
		{
			return "xpbar_inc0"+number+".png";
		} else {
			return "xpbar_inc"+number+".png";
		}
	}
	
	public static String getUrlIcon(SkillType skillType)
	{
		return m.getCapitalized(skillType.toString())+".png";
	}
	public static boolean shouldBeFilled(PlayerProfile PP)
	{
		return PP.getXpBarInc() < getXpInc(PP.getSkillXpLevel(PP.getLastGained()), PP.getXpToLevel(PP.getLastGained()), HUDType.STANDARD);
	}
}
