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
package com.gmail.nossr50.config;

import java.io.File;
import org.bukkit.util.config.Configuration;

import com.gmail.nossr50.datatypes.HUDType;

public class LoadProperties 
{
	public static Boolean showDisplayName, showFaces, watch, xplockEnable, xpbar, xpicon, partybar, string, bucket, web, xprateEnable, slimeballs, spoutEnabled, 
	donateMessage, chimaeraWingEnable, xpGainsMobSpawners, myspawnEnable, mccEnable, mcmmoEnable, partyEnable, inviteEnable, acceptEnable, 
	whoisEnable, statsEnable, addxpEnable, ptpEnable, mmoeditEnable, clearmyspawnEnable, mcgodEnable, mcabilityEnable, mctopEnable, 
	mcrefreshEnable, enableMotd, enableMySpawn, enableRegen, enableCobbleToMossy, useMySQL, cocoabeans, mushrooms, 
	toolsLoseDurabilityFromAbilities, pvpxp, miningrequirespickaxe, excavationRequiresShovel, woodcuttingrequiresaxe, eggs, apples, cake, music, diamond, glowstone, 
	slowsand, sulphur, netherrack, bones, coal, clay, anvilmessages;
	
	public static String xplock, MySQLtablePrefix, MySQLuserName, MySQLserverName, MySQLdbName, MySQLdbPass, mctop, addxp,
	xprate, mcability, mcmmo, mcc, mcrefresh, mcgod, stats, mmoedit, ptp, party, myspawn, whois, invite, accept, clearmyspawn, nWood,
	nStone, nIron, nGold, nDiamond, locale;
	
	public static int mwatch, xpbar_x, xpbar_y, xpicon_x, xpicon_y, mstring, mbucket, mweb,
	chimaeraId, msandstone, mcocoa, water_thunder, cure_self, cure_other, mslimeballs, mbones, msulphur, mslowsand,
	mmushroom2, mglowstone2, mmusic, mdiamond2, mbase, mapple, meggs, mcake, mpine, mbirch, mspruce, mcactus, mmushroom, mflower,
	msugar, mpumpkin, mwheat, mgold, mdiamond, miron, mredstone, mlapis, mobsidian, mnetherrack, mglowstone, mcoal, mstone, MySQLport,
	xpGainMultiplier, superBreakerCooldown, greenTerraCooldown, gigaDrillBreakerCooldown, treeFellerCooldown,
	berserkCooldown, serratedStrikeCooldown, skullSplitterCooldown, abilityDurabilityLoss,
	feathersConsumedByChimaeraWing, repairdiamondlevel, rWood, rStone, rIron, rGold, rDiamond;
	
	public static double xpbackground_r, xpbackground_g, xpbackground_b, xpborder_r, xpborder_g, xpborder_b, acrobatics_r, acrobatics_g, acrobatics_b, archery_r, archery_g, archery_b, axes_r, axes_g, axes_b,
	excavation_r, excavation_g, excavation_b, herbalism_r, herbalism_g, herbalism_b, mining_r, mining_g, mining_b,
	repair_r, repair_g, repair_b, swords_r, swords_g, swords_b, taming_r, taming_g, taming_b, unarmed_r, unarmed_g, unarmed_b,
	woodcutting_r, woodcutting_g, woodcutting_b, pvpxprewardmodifier, tamingxpmodifier, miningxpmodifier,
	repairxpmodifier, woodcuttingxpmodifier, sorceryxpmodifier, unarmedxpmodifier, herbalismxpmodifier, excavationxpmodifier,
	archeryxpmodifier, swordsxpmodifier, axesxpmodifier, acrobaticsxpmodifier;
	
	public static HUDType defaulthud;
	
	public String directory = "plugins/mcMMO/"; 
	
	File file = new File(directory + File.separator + "config.yml");
	static Configuration config = null;
	
	public void configCheck()
	{
		new File(directory).mkdir();
		config = load();
		if(!file.exists())
		{
			try 
			{
				file.createNewFile();
				addDefaults();
			} 
			catch (Exception ex) 
			{
				ex.printStackTrace();
			}
		} 
		else 
		{
			loadkeys();
		}
	}
	    private void write(String root, Object x)
	    {
	    	//Configuration config = load();
	        config.setProperty(root, x);
	        config.save();
	    }
	    private Boolean readBoolean(String root, Boolean def)
	    {
	    	//Configuration config = load();
	    	Boolean result = config.getBoolean(root, def);
	    	config.save();
	        return result;
	    }
	    private Double readDouble(String root, Double def)
	    {
	    	Double result = config.getDouble(root, def);
	    	config.save();
	    	return result;
	    }
	    private Integer readInteger(String root, Integer def)
	    {
	    	//Configuration config = load();
	    	Integer result = config.getInt(root, def);
	    	config.save();
	        return result;
	    }
	    
	    public static String readString(String root, String def)
	    {
	    	//Configuration config = load();
	    	String result = config.getString(root, def);
	    	config.save();
	        return result;
	    }
	    
	    private Configuration load()
	    {
	        try {
	            Configuration configx = new Configuration(file);
	            configx.load();
	            return configx;

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
	    private void addDefaults()
	    {
	        System.out.println("Generating Config File...");  	
	    	
	        //Put in defaults
	        write("Spout.HUD.Default", "STANDARD");
	        write("Spout.XP.Bar.Enabled", true);
	        write("Spout.Images.URL_DIR", "http://mcmmo.rycochet.net/mcmmo/");
	        write("Spout.XP.Icon.Enabled", true);
	        write("Spout.XP.Bar.X_POS", 95);
	        write("Spout.XP.Bar.Y_POS", 6);
	        write("Spout.XP.Icon.X_POS", 78);
	        write("Spout.XP.Icon.Y_POS", 2);
	        write("Spout.Party.HUD.Enabled", true);
	        write("Spout.Party.HUD.Show_Faces", true);
	        write("Spout.Party.HUD.Show_Display_Name", false);
	        write("Spout.Menu.Key", "KEY_M");
	        write("Spout.HUD.Retro.Colors.Acrobatics.RED", 0.3);
	        write("Spout.HUD.Retro.Colors.Acrobatics.GREEN", 0.3);
	        write("Spout.HUD.Retro.Colors.Acrobatics.BLUE", 0.75);
	        write("Spout.HUD.Retro.Colors.Archery.RED", 0.3);
	        write("Spout.HUD.Retro.Colors.Archery.GREEN", 0.3);
	        write("Spout.HUD.Retro.Colors.Archery.BLUE", 0.75);
	        write("Spout.HUD.Retro.Colors.Axes.RED", 0.3);
	        write("Spout.HUD.Retro.Colors.Axes.GREEN", 0.3);
	        write("Spout.HUD.Retro.Colors.Axes.BLUE", 0.75);
	        write("Spout.HUD.Retro.Colors.Excavation.RED", 0.3);
	        write("Spout.HUD.Retro.Colors.Excavation.GREEN", 0.3);
	        write("Spout.HUD.Retro.Colors.Excavation.BLUE", 0.75);
	        write("Spout.HUD.Retro.Colors.Herbalism.RED", 0.3);
	        write("Spout.HUD.Retro.Colors.Herbalism.GREEN", 0.3);
	        write("Spout.HUD.Retro.Colors.Herbalism.BLUE", 0.75);
	        write("Spout.HUD.Retro.Colors.Mining.RED", 0.3);
	        write("Spout.HUD.Retro.Colors.Mining.GREEN", 0.3);
	        write("Spout.HUD.Retro.Colors.Mining.BLUE", 0.75);
	        write("Spout.HUD.Retro.Colors.Repair.RED", 0.3);
	        write("Spout.HUD.Retro.Colors.Repair.GREEN", 0.3);
	        write("Spout.HUD.Retro.Colors.Repair.BLUE", 0.75);
	        write("Spout.HUD.Retro.Colors.Swords.RED", 0.3);
	        write("Spout.HUD.Retro.Colors.Swords.GREEN", 0.3);
	        write("Spout.HUD.Retro.Colors.Swords.BLUE", 0.75);
	        write("Spout.HUD.Retro.Colors.Taming.RED", 0.3);
	        write("Spout.HUD.Retro.Colors.Taming.GREEN", 0.3);
	        write("Spout.HUD.Retro.Colors.Taming.BLUE", 0.75);
	        write("Spout.HUD.Retro.Colors.Unarmed.RED", 0.3);
	        write("Spout.HUD.Retro.Colors.Unarmed.GREEN", 0.3);
	        write("Spout.HUD.Retro.Colors.Unarmed.BLUE", 0.75);
	        write("Spout.HUD.Retro.Colors.Woodcutting.RED", 0.3);
	        write("Spout.HUD.Retro.Colors.Woodcutting.GREEN", 0.3);
	        write("Spout.HUD.Retro.Colors.Woodcutting.BLUE", 0.75);
	        write("Spout.HUD.Retro.Colors.Border.RED", 0.0);
	        write("Spout.HUD.Retro.Colors.Border.GREEN", 0.0);
	        write("Spout.HUD.Retro.Colors.Border.BLUE", 0.0);
	        write("Spout.HUD.Retro.Colors.Background.RED", 0.75);
	        write("Spout.HUD.Retro.Colors.Background.GREEN", 0.75);
	        write("Spout.HUD.Retro.Colors.Background.BLUE", 0.75);
	        
	        write("MySQL.Enabled", false);
	        write("MySQL.Server.Address", "localhost");
	        write("MySQL.Server.Port", 3306);
        	write("MySQL.Database.Name", "DataBaseName");
        	write("MySQL.Database.User.Name", "UserName");
        	write("MySQL.Database.TablePrefix", "mcmmo_");
        	write("MySQL.Database.User.Password", "UserPassword");
        	
        	write("General.Locale", "en_us");
	    	write("General.MOTD.Enabled", true);
	    	write("General.MySpawn.Enabled", true);
	    	write("General.HP_Regeneration.Enabled", true);
	    	
	    	write("Items.Chimaera_Wing.Enabled", true);
	    	write("Items.Chimaera_Wing.Feather_Cost", 10);
	    	write("Items.Chimaera_Wing.Item_ID", 288);
	    	
	    	write("Experience.PVP.Rewards", true);
	    	write("Experience.Gains.Multiplier.PVP", 1);
	    	write("Experience.Gains.Mobspawners.Enabled", false);
	    	write("Experience.Gains.Multiplier.Global", 1.0);
	    	write("Experience.Formula.Multiplier.Taming", 1.0);
	    	write("Experience.Formula.Multiplier.Mining", 1.0);
	    	write("Experience.Formula.Multiplier.Repair", 1.0);
	    	write("Experience.Formula.Multiplier.Woodcutting", 1.0);
	    	write("Experience.Formula.Multiplier.Unarmed", 1.0);
	    	write("Experience.Formula.Multiplier.Herbalism", 1.0);
	    	write("Experience.Formula.Multiplier.Excavation", 1.0);
	    	write("Experience.Formula.Multiplier.Swords", 1.0);
	    	write("Experience.Formula.Multiplier.Archery", 1.0);
	    	write("Experience.Formula.Multiplier.Axes", 1.0);
	    	write("Experience.Formula.Multiplier.Sorcery", 1.0);
	    	write("Experience.Formula.Multiplier.Acrobatics", 1.0);
	    	write("Experience.Mining.Gold", 350);
	    	write("Experience.Mining.Diamond", 750);
	    	write("Experience.Mining.Iron", 250);
	    	write("Experience.Mining.Redstone", 150);
	    	write("Experience.Mining.lapis", 400);
	    	write("Experience.Mining.Obsidian", 150);
	    	write("Experience.Mining.Netherrack", 30);
	    	write("Experience.Mining.Glowstone", 30);
	    	write("Experience.Mining.Coal", 100);
	    	write("Experience.Mining.Stone", 30);
	    	write("Experience.Mining.Sandstone", 30);
	    	write("Experience.Herbalism.Sugar_Cane", 30);
	    	write("Experience.Herbalism.Cactus", 30);
	    	write("Experience.Herbalism.Pumpkin", 550);
	    	write("Experience.Herbalism.Flowers", 100);
	    	write("Experience.Herbalism.Wheat", 50);
	    	write("Experience.Herbalism.Mushrooms", 150);
	    	write("Experience.Woodcutting.Pine", 90);
	    	write("Experience.Woodcutting.Birch", 70);
	    	write("Experience.Woodcutting.Spruce", 80);
	    	write("Experience.Excavation.Base", 40);
	    	write("Experience.Excavation.Mushroom", 80);
	    	write("Experience.Excavation.Sulphur", 30);
	    	write("Experience.Excavation.Slowsand", 80);
	    	write("Experience.Excavation.Glowstone", 80);
	    	write("Experience.Excavation.Music", 3000);
	    	write("Experience.Excavation.Bones", 30);
	    	write("Experience.Excavation.Diamond", 1000);
	    	write("Experience.Excavation.Apple", 100);
	    	write("Experience.Excavation.Eggs", 100);
	    	write("Experience.Excavation.Cake", 3000);
	    	write("Experience.Excavation.Slimeballs", 100);
	    	write("Experience.Excavation.Cocoa_Beans", 100);
	    	write("Experience.Excavation.Map", 200);
	    	write("Experience.Excavation.String", 200);
	    	write("Experience.Excavation.Bucket", 100);
	    	write("Experience.Excavation.Web", 150);
	    	
	    	//write("Sorcery.Spells.Water.Thunder", 75);
	    	//write("Sorcery.Spells.Curative.Cure_Self.Mana_Cost", 5);
	    	//write("Sorcery.Spells.Curative.Cure_Other.Mana_Cost", 5);
	    	
	    	write("Excavation.Drops.Cocoa_Beans", true);
	    	write("Excavation.Drops.Mushrooms", true);
	    	write("Excavation.Drops.Glowstone", true);
	    	write("Excavation.Drops.Eggs", true);
	    	write("Excavation.Drops.Apples", true);
	    	write("Excavation.Drops.Cake", true);
	    	write("Excavation.Drops.Music", true);
	    	write("Excavation.Drops.Diamond", true);
	    	write("Excavation.Drops.Slowsand", true);
	    	write("Excavation.Drops.Sulphur", true);
	    	write("Excavation.Drops.Netherrack", true);
	    	write("Excavation.Drops.Bones", true);
	    	write("Excavation.Drops.Slimeballs", true);
	    	write("Excavation.Drops.Map", true);
	    	write("Excavation.Drops.String", true);
	    	write("Excavation.Drops.Bucket", true);
	    	write("Excavation.Drops.Web", true);
	    	
	    	write("Commands.xprate.Name", "xprate");
	    	write("Commands.xprate.Enabled", true);
	    	write("Commands.mctop.Name", "mctop");
	    	write("Commands.mctop.Enabled", true);
	    	write("Commands.addxp.Name", "addxp");
	    	write("Commands.addxp.Enabled", true);
	    	write("Commands.mcability.Name", "mcability");
	    	write("Commands.mcability.Enabled", true);
	    	write("Commands.mcrefresh.Name", "mcrefresh");
	    	write("Commands.mcrefresh.Enabled", true);
	    	write("Commands.mcmmo.Name", "mcmmo");
	    	write("Commands.mcmmo.Donate_Message", true);
	    	write("Commands.mcmmo.Enabled", true);
	    	write("Commands.mcc.Name", "mcc");
	    	write("Commands.mcc.Enabled", true);
	    	write("Commands.mcgod.Name", "mcgod");
	    	write("Commands.mcgod.Enabled", true);
	    	write("Commands.stats.Name", "stats");
	    	write("Commands.stats.Enabled", true);
	    	write("Commands.mmoedit.Name", "mmoedit");
	    	write("Commands.mmoedit.Enabled", true);
	    	write("Commands.ptp.Name", "ptp");
	    	write("Commands.ptp.Enabled", true);
	    	write("Commands.party.Name", "party");
	    	write("Commands.party.Enabled", true);
	    	write("Commands.myspawn.Name", "myspawn");
	    	write("Commands.myspawn.Enabled", true);
	    	write("Commands.whois.Name", "whois");
	    	write("Commands.whois.Enabled", true);
	    	write("Commands.invite.Name", "invite");
	    	write("Commands.invite.Enabled", true);
	    	write("Commands.accept.Name", "accept");
	    	write("Commands.accept.Enabled", true);
	    	write("Commands.clearmyspawn.Name", "clearmyspawn");
	    	write("Commands.clearmyspawn.Enabled", true);
	    	write("Commands.xplock.Enabled", true);
	    	write("Commands.xplock.Name", "xplock");
	    	
	    	write("Abilities.Tools.Durability_Loss_Enabled", true);
	    	write("Abilities.Tools.Durability_Loss", 2);
	    	write("Abilities.Cooldowns.Green_Terra", 240);
	    	write("Abilities.Cooldowns.Super_Breaker", 240);
	    	write("Abilities.Cooldowns.Giga_Drill_Breaker", 240);
	    	write("Abilities.Cooldowns.Tree_Feller", 240);
	    	write("Abilities.Cooldowns.Berserk", 240);
	    	write("Abilities.Cooldowns.Serrated_Strikes", 240);
	    	write("Abilities.Cooldowns.Skull_Splitter", 240);
	    	
	    	write("Skills.Repair.Anvil_Messages", true);
	    	write("Skills.Repair.Gold.ID", 266);
	    	write("Skills.Repair.Gold.Name", "Gold Bars");
	    	write("Skills.Repair.Stone.ID", 4);
	    	write("Skills.Repair.Stone.Name", "Cobblestone");
	    	write("Skills.Repair.Wood.ID", 5);
	    	write("Skills.Repair.Wood.Name", "Wood Planks");
	    	write("Skills.Repair.Diamond.ID", 264);
	    	write("Skills.Repair.Diamond.Name", "Diamond");
	    	write("Skills.Repair.Diamond.Level_Required", 50);
	    	write("Skills.Repair.Iron.ID", 265);
	    	write("Skills.Repair.Iron.Name", "Iron Bars");
	    	write("Skills.Herbalism.Green_Thumb.Cobble_To_Mossy", true);
	    	write("Skills.Excavation.Requires_Shovel", true);
	    	write("Skills.Mining.Requires_Pickaxe", true);
	    	write("Skills.Woodcutting.Requires_Axe", true);
	    	
	    	loadkeys();
	    }
	    private void loadkeys()
	    {
	        System.out.println("Loading Config File...");
	        
	        //Setup default HUD
	        String temp = readString("Spout.HUD.Default", "STANDARD");
	        for(HUDType x : HUDType.values())
	        {
	        	if(x.toString().equalsIgnoreCase(temp))
	        	{
	        		defaulthud = x;
	        	}
	        }
	        
	        donateMessage = readBoolean("Commands.mcmmo.Donate_Message", true);
	        xpGainsMobSpawners = readBoolean("XP.Gains.Mobspawners.Enabled", false);
	        
	        xpbar = readBoolean("Spout.XP.Bar.Enabled", true);
	        //web_url = readString("Spout.Images.URL_DIR", "http://mcmmo.rycochet.net/mcmmo/");
	        xpicon = readBoolean("Spout.XP.Icon.Enabled", true);
	        xpbar_x = readInteger("Spout.XP.Bar.X_POS", 95);
	        xpbar_y = readInteger("Spout.XP.Bar.Y_POS", 6);
	        xpicon_x = readInteger("Spout.XP.Icon.X_POS", 78);
	        xpicon_y = readInteger("Spout.XP.Icon.Y_POS", 2);
	        
	        showFaces = readBoolean("Spout.Party.HUD.Show_Faces", true);
	        showDisplayName = readBoolean("Spout.Party.HUD.Show_Display_Name", false);
	        partybar = readBoolean("Spout.Party.HUD.Enabled", true);
	        
	        acrobatics_r = readDouble("Spout.HUD.Retro.Colors.Acrobatics.RED", 0.3);
	        acrobatics_g = readDouble("Spout.HUD.Retro.Colors.Acrobatics.GREEN", 0.3);
	        acrobatics_b = readDouble("Spout.HUD.Retro.Colors.Acrobatics.BLUE", 0.75);
	        archery_r = readDouble("Spout.HUD.Retro.Colors.Archery.RED", 0.3);
	        archery_g = readDouble("Spout.HUD.Retro.Colors.Archery.GREEN", 0.3);
	        archery_b = readDouble("Spout.HUD.Retro.Colors.Archery.BLUE", 0.75);
	        axes_r = readDouble("Spout.HUD.Retro.Colors.Axes.RED", 0.3);
	        axes_g = readDouble("Spout.HUD.Retro.Colors.Axes.GREEN", 0.3);
	        axes_b = readDouble("Spout.HUD.Retro.Colors.Axes.BLUE", 0.75);
	        excavation_r = readDouble("Spout.HUD.Retro.Colors.Excavation.RED", 0.3);
	        excavation_g = readDouble("Spout.HUD.Retro.Colors.Excavation.GREEN", 0.3);
	        excavation_b = readDouble("Spout.HUD.Retro.Colors.Excavation.BLUE", 0.75);
	        herbalism_r = readDouble("Spout.HUD.Retro.Colors.Herbalism.RED", 0.3);
	        herbalism_g = readDouble("Spout.HUD.Retro.Colors.Herbalism.GREEN", 0.3);
	        herbalism_b = readDouble("Spout.HUD.Retro.Colors.Herbalism.BLUE", 0.75);
	        mining_r = readDouble("Spout.HUD.Retro.Colors.Mining.RED", 0.3);
	        mining_g = readDouble("Spout.HUD.Retro.Colors.Mining.GREEN", 0.3);
	        mining_b = readDouble("Spout.HUD.Retro.Colors.Mining.BLUE", 0.75);
	        repair_r = readDouble("Spout.HUD.Retro.Colors.Repair.RED", 0.3);
	        repair_g = readDouble("Spout.HUD.Retro.Colors.Repair.GREEN", 0.3);
	        repair_b = readDouble("Spout.HUD.Retro.Colors.Repair.BLUE", 0.75);
	        swords_r = readDouble("Spout.HUD.Retro.Colors.Swords.RED", 0.3);
	        swords_g = readDouble("Spout.HUD.Retro.Colors.Swords.GREEN", 0.3);
	        swords_b = readDouble("Spout.HUD.Retro.Colors.Swords.BLUE", 0.75);
	        taming_r = readDouble("Spout.HUD.Retro.Colors.Taming.RED", 0.3);
	        taming_g = readDouble("Spout.HUD.Retro.Colors.Taming.GREEN", 0.3);
	        taming_b = readDouble("Spout.HUD.Retro.Colors.Taming.BLUE", 0.75);
	        unarmed_r = readDouble("Spout.HUD.Retro.Colors.Unarmed.RED", 0.3);
	        unarmed_g = readDouble("Spout.HUD.Retro.Colors.Unarmed.GREEN", 0.3);
	        unarmed_b = readDouble("Spout.HUD.Retro.Colors.Unarmed.BLUE", 0.75);
	        woodcutting_r = readDouble("Spout.HUD.Retro.Colors.Woodcutting.RED", 0.3);
	        woodcutting_g = readDouble("Spout.HUD.Retro.Colors.Woodcutting.GREEN", 0.3);
	        woodcutting_b = readDouble("Spout.HUD.Retro.Colors.Woodcutting.BLUE", 0.75);
	        
	        xpborder_r = readDouble("Spout.HUD.Retro.Colors.Border.RED", 0.0);
	        xpborder_g = readDouble("Spout.HUD.Retro.Colors.Border.GREEN", 0.0);
	        xpborder_b = readDouble("Spout.HUD.Retro.Colors.Border.BLUE", 0.0);
	        xpbackground_r = readDouble("Spout.HUD.Retro.Colors.Background.RED", 0.75);
	        xpbackground_g = readDouble("Spout.HUD.Retro.Colors.Background.GREEN", 0.75);
	        xpbackground_b = readDouble("Spout.HUD.Retro.Colors.Background.BLUE", 0.75);
	        
	        msulphur = readInteger("Experience.Excavation.Sulphur", 30);
	        mbones = readInteger("Experience.Excavation.Bones", 30);
	        mbase = readInteger("Experience.Excavation.Base", 40);
	        mmushroom2 = readInteger("Experience.Excavation.Mushroom", 80);
	    	mslowsand = readInteger("Experience.Excavation.Slowsand", 80);
	    	mglowstone2 = readInteger("Experience.Excavation.Glowstone", 80);
	    	mmusic = readInteger("Experience.Excavation.Music", 3000);
	    	mdiamond2 = readInteger("Experience.Excavation.Diamond", 1000);
	    	mapple = readInteger("Experience.Excavation.Apple", 100);
	    	meggs = readInteger("Experience.Excavation.Eggs", 100);
	    	mcake = readInteger("Experience.Excavation.Cake", 3000);
	    	mcocoa = readInteger("Experience.Excavation.Cocoa_Beans", 100);
	    	mslimeballs = readInteger("Experience.Excavation.Slimeballs", 100);
	    	mstring = readInteger("Experience.Excavation.String", 200);
	    	mbucket = readInteger("Experience.Excavation.Bucket", 100);
	    	mweb = readInteger("Experience.Excavation.Web", 150);
	    	mwatch = readInteger("Experience.Excavation.Watch", 200);
	    	
	        msugar = readInteger("Experience.Herbalism.Sugar_Cane", 30);
	        mwheat = readInteger("Experience.Herbalism.Wheat", 50);
	    	mcactus = readInteger("Experience.Herbalism.Cactus", 30);
	    	mpumpkin = readInteger("Experience.Herbalism.Pumpkin", 550);
	    	mflower = readInteger("Experience.Herbalism.Flowers", 100);
	    	mmushroom = readInteger("Experience.Herbalism.Mushrooms", 150);
	    	
	    	mpine = readInteger("Experience.Woodcutting.Pine", 70);
	    	mbirch = readInteger("Experience.Woodcutting.Birch", 80);
	    	mspruce = readInteger("Experience.Woodcutting.Spruce", 90);
	        
	        mgold = readInteger("Experience.Mining.Gold", 250);
	        mdiamond = readInteger("Experience.Mining.Diamond", 750);
	        miron = readInteger("Experience.Mining.Iron", 250);
	        mredstone = readInteger("Experience.Mining.Redstone", 150);
	        mlapis = readInteger("Experience.Mining.lapis", 400);
	        mobsidian = readInteger("Experience.Mining.Obsidian", 150);
	        mnetherrack = readInteger("Experience.Mining.Netherrack", 30);
	        mglowstone = readInteger("Experience.Mining.Glowstone", 30);
	        mcoal = readInteger("Experience.Mining.Coal", 100);
	        mstone = readInteger("Experience.Mining.Stone", 30);
	        msandstone = readInteger("Experience.Mining.Sandstone", 30);
	        
	        greenTerraCooldown = readInteger("Abilities.Cooldowns.Green_Terra", 240);
	    	superBreakerCooldown = readInteger("Abilities.Cooldowns.Super_Breaker", 240);
	    	gigaDrillBreakerCooldown = readInteger("Abilities.Cooldowns.Giga_Drill_Breaker", 240);
	    	treeFellerCooldown = readInteger("Abilities.Cooldowns.Tree_Feller", 240);
	    	berserkCooldown = readInteger("Abilities.Cooldowns.Berserk", 240);
	    	serratedStrikeCooldown = readInteger("Abilities.Cooldowns.Serrated_Strikes", 240);
	    	skullSplitterCooldown = readInteger("Abilities.Cooldowns.Skull_Splitter", 240);
	    	
	    	MySQLserverName = readString("MySQL.Server.Address", "localhost");
	    	if(readString("MySQL.Database.User.Password", null) != null)
	    		MySQLdbPass = readString("MySQL.Database.User.Password", null);
	    	else
	    		MySQLdbPass = "";
	    	
	    	MySQLdbName = readString("MySQL.Database.Name", "DatabaseName");
	    	MySQLuserName = readString("MySQL.Database.User.Name", "UserName");
	    	MySQLtablePrefix = readString("MySQL.Database.TablePrefix", "mcmmo_");
	    	MySQLport = readInteger("MySQL.Server.Port", 3306);
	    	useMySQL = readBoolean("MySQL.Enabled", false);
	    	
	    	locale = readString("General.Locale", "en_us");
	    	enableMotd = readBoolean("General.MOTD.Enabled", true);
	    	enableMySpawn = readBoolean("General.MySpawn.Enabled", true);
	    	enableRegen = readBoolean("General.HP_Regeneration.Enabled", true);
	    	
	    	enableCobbleToMossy = readBoolean("Skills.Herbalism.Green_Thumb.Cobble_To_Mossy", true);
	    	
	    	xpGainMultiplier = readInteger("Experience.Gains.Multiplier.Global", 1);
	    	toolsLoseDurabilityFromAbilities = readBoolean("Abilities.Tools.Durability_Loss_Enabled", true);
	    	abilityDurabilityLoss = readInteger("Abilities.Tools.Durability_Loss", 2);
	    	
	    	feathersConsumedByChimaeraWing = readInteger("Items.Chimaera_Wing.Feather_Cost", 10);
	    	chimaeraId = readInteger("Items.Chimaera_Wing.Item_ID", 288);
	    	chimaeraWingEnable = readBoolean("Items.Chimaera_Wing.Enabled", true);
	    	
	    	pvpxp = readBoolean("XP.PVP.Rewards", true);
	    	pvpxprewardmodifier = readDouble("Experience.Gains.Multiplier.PVP", 1.0);
	    	miningrequirespickaxe = readBoolean("Skills.Mining.Requires_Pickaxe", true);
	    	excavationRequiresShovel = readBoolean("Skills.Excavation.Requires_Shovel", true);
	    	woodcuttingrequiresaxe = readBoolean("Skills.Woodcutting.Requires_Axe", true);
	    	repairdiamondlevel = readInteger("Skills.Repair.Diamond.Level_Required", 50);

	    	sorceryxpmodifier = readDouble("Experience.Formula.Multiplier.Sorcery", 1.0);
	    	tamingxpmodifier = readDouble("Experience.Formula.Multiplier.Taming", 1.0);
	    	miningxpmodifier = readDouble("Experience.Formula.Multiplier.Mining", 1.0);
	    	repairxpmodifier = readDouble("Experience.Formula.Multiplier.Repair", 1.0);
	    	woodcuttingxpmodifier = readDouble("Experience.Formula.Multiplier.Woodcutting", 1.0);
	    	unarmedxpmodifier = readDouble("Experience.Formula.Multiplier.Unarmed", 1.0);
	    	herbalismxpmodifier = readDouble("Experience.Formula.Multiplier.Herbalism", 1.0);
	    	excavationxpmodifier = readDouble("Experience.Formula.Multiplier.Excavation", 1.0);
	    	archeryxpmodifier = readDouble("Experience.Formula.Multiplier.Archery", 1.0);
	    	swordsxpmodifier = readDouble("Experience.Formula.Multiplier.Swords", 1.0);
	    	axesxpmodifier = readDouble("Experience.Formula.Multiplier.Axes", 1.0);
	    	acrobaticsxpmodifier = readDouble("Experience.Formula.Multiplier.Acrobatics", 1.0);

	    	anvilmessages = readBoolean("Skills.Repair.Anvil_Messages", true);
	    	
	        rGold =  readInteger("Skills.Repair.Gold.ID", 266);
	        nGold =  readString("Skills.Repair.Gold.Name", "Gold Bars");      
	        rStone =  readInteger("Skills.Repair.Stone.ID", 4);
	        nStone =  readString("Skills.Repair.Stone.Name", "Cobblestone");     
	        rWood =  readInteger("Skills.Repair.Wood.ID", 5);
	        nWood =  readString("Skills.Repair.Wood.Name", "Wood Planks");        
	        rDiamond =   readInteger("Skills.Repair.Diamond.ID", 264);
	        nDiamond =  readString("Skills.Repair.Diamond.Name", "Diamond");          
	        rIron =   readInteger("Skills.Repair.Iron.ID", 265);
	        nIron =  readString("Skills.Repair.Iron.Name", "Iron Bars");  

	    	cocoabeans = readBoolean("Excavation.Drops.Cocoa_Beans", true);
	    	mushrooms = readBoolean("Excavation.Drops.Mushrooms", true);
	    	glowstone = readBoolean("Excavation.Drops.Glowstone", true);
	    	eggs = readBoolean("Excavation.Drops.Eggs", true);
	    	apples = readBoolean("Excavation.Drops.Apples", true);
	    	cake = readBoolean("Excavation.Drops.Cake", true);
	    	music = readBoolean("Excavation.Drops.Music", true);
	    	diamond = readBoolean("Excavation.Drops.Diamond", true);
	    	slowsand = readBoolean("Excavation.Drops.Slowsand", true);
	    	sulphur = readBoolean("Excavation.Drops.Sulphur", true);
	    	netherrack = readBoolean("Excavation.Drops.Netherrack", true);
	    	bones = readBoolean("Excavation.Drops.Bones", true);
	    	slimeballs = readBoolean("Excavation.Drops.Slimeballs", true);
	    	watch = readBoolean("Excavation.Drops.Watch", true);
	    	string = readBoolean("Excavation.Drops.String", true);
	    	bucket = readBoolean("Excavation.Drops.Bucket", true);
	    	web = readBoolean("Excavation.Drops.Web", true);
	    	
	    	xprate = readString("Commands.xprate.Name", "xprate");
	    	xprateEnable = readBoolean("Commands.xprate.Enabled", true);
	    	
	    	mctop = readString("Commands.mctop.Name", "mctop");
	    	mctopEnable = readBoolean("Commands.mctop.Enabled", true);
	    	
	    	addxp = readString("Commands.addxp.Name", "addxp");
	    	addxpEnable = readBoolean("Commands.addxp.Enabled", true);
	    	
	    	mcability = readString("Commands.mcability.Name", "mcability");
	    	mcabilityEnable = readBoolean("Commands.mcability.Enabled", true);
	    	
	    	mcrefresh = readString("Commands.mcrefresh.Name", "mcrefresh");
	    	mcrefreshEnable = readBoolean("Commands.mcrefresh.Enabled", true);
	    	
	    	mcmmo = readString("Commands.mcmmo.Name", "mcmmo");
	    	mcmmoEnable = readBoolean("Commands.mcmmo.Enabled", true);
	    	
	    	mcc = readString("Commands.mcc.Name", "mcc");
	    	mccEnable = readBoolean("Commands.mcc.Enabled", true);
	    	
	    	mcgod = readString("Commands.mcgod.Name", "mcgod");
	    	mcgodEnable = readBoolean("Commands.mcgod.Enabled", true);
	    	
	    	stats = readString("Commands.stats.Name", "stats");
	    	statsEnable = readBoolean("Commands.stats.Enabled", true);
	    	
	    	mmoedit = readString("Commands.mmoedit.Name", "mmoedit");
	    	mmoeditEnable = readBoolean("Commands.mmoedit.Enabled", true);
	    	
	    	ptp = readString("Commands.ptp.Name", "ptp");
	    	ptpEnable = readBoolean("Commands.ptp.Enabled", true);
	    	
	    	party = readString("Commands.party.Name", "party");
	    	partyEnable = readBoolean("Commands.party.Enabled", true);
	    	
	    	myspawn = readString("Commands.myspawn.Name", "myspawn");
	    	myspawnEnable = readBoolean("Commands.myspawn.Enabled", true);
	    	
	    	whois = readString("Commands.whois.Name", "whois");
	    	whoisEnable = readBoolean("Commands.whois.Enabled", true);
	    	
	    	invite = readString("Commands.invite.Name", "invite");
	    	inviteEnable = readBoolean("Commands.invite.Enabled", true);
	    	
	    	accept = readString("Commands.accept.Name", "accept");
	    	acceptEnable = readBoolean("Commands.accept.Enabled", true);
	    	
	    	clearmyspawn = readString("Commands.clearmyspawn.Name", "clearmyspawn");
	    	clearmyspawnEnable = readBoolean("Commands.clearmyspawn.Enabled", true);
	    	
	    	xplockEnable = readBoolean("Commands.xplock.Enabled", true);
	    	xplock = readString("Commands.xplock.Name", "xplock");
	        }
	}