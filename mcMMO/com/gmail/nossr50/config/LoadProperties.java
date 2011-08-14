package com.gmail.nossr50.config;

import java.io.File;
import org.bukkit.util.config.Configuration;

public class LoadProperties 
{
	public static Boolean slimeballs, spoutEnabled, donateMessage, chimaeraWingEnable, xpGainsMobSpawners, myspawnEnable, mccEnable, mcmmoEnable, partyEnable, inviteEnable, acceptEnable, whoisEnable, statsEnable, addxpEnable, ptpEnable, mmoeditEnable, clearmyspawnEnable, mcgodEnable, mcabilityEnable, mctopEnable, mcrefreshEnable, enableMotd, enableMySpawn, enableRegen, enableCobbleToMossy, useMySQL, cocoabeans, archeryFireRateLimit, mushrooms, toolsLoseDurabilityFromAbilities, pvpxp, miningrequirespickaxe, woodcuttingrequiresaxe, eggs, apples, cake, music, diamond, glowstone, slowsand, sulphur, netherrack, bones, coal, clay, anvilmessages;
	public static String MySQLtablePrefix, MySQLuserName, MySQLserverName, MySQLdbName, MySQLdbPass, mctop, addxp, mcability, mcmmo, mcc, mcrefresh, mcgod, stats, mmoedit, ptp, party, myspawn, whois, invite, accept, clearmyspawn, nWood, nStone, nIron, nGold, nDiamond, locale;
	public static int archeryLimit, chimaeraId, msandstone, mcocoa, water_thunder, cure_self, cure_other, mslimeballs, mbones, msulphur, mslowsand, mmushroom2, mglowstone2, mmusic, mdiamond2, mbase, mapple, meggs, mcake, mpine, mbirch, mspruce, mcactus, mmushroom, mflower, msugar, mpumpkin, mwheat, mgold, mdiamond, miron, mredstone, mlapis, mobsidian, mnetherrack, mglowstone, mcoal, mstone, MySQLport, xpGainMultiplier, superBreakerCooldown = 240, greenTerraCooldown = 240, gigaDrillBreakerCooldown = 240, treeFellerCooldown = 240, berserkCooldown = 240, serratedStrikeCooldown = 240, skullSplitterCooldown = 240, abilityDurabilityLoss, feathersConsumedByChimaeraWing, pvpxprewardmodifier, repairdiamondlevel, globalxpmodifier, tamingxpmodifier, miningxpmodifier, repairxpmodifier, woodcuttingxpmodifier, sorceryxpmodifier = 2, unarmedxpmodifier, herbalismxpmodifier, excavationxpmodifier, archeryxpmodifier, swordsxpmodifier, axesxpmodifier, acrobaticsxpmodifier, rWood, rStone, rIron, rGold, rDiamond;
	public String directory = "plugins/mcMMO/"; 
	File file = new File(directory + File.separator + "config.yml");
	Configuration config = null;
	
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
	    private Integer readInteger(String root, Integer def)
	    {
	    	//Configuration config = load();
	    	Integer result = config.getInt(root, def);
	    	config.save();
	        return result;
	    }
	    
	    private String readString(String root, String def)
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
	    	
	    	write("XP.PVP.Rewards", true);
	    	write("XP.Gains.Multiplier.PVP", 1);
	    	write("XP.Gains.Mobspawners.Enabled", false);
	    	write("XP.Gains.Multiplier.Global", 1);
	    	write("XP.Formula.Multiplier.Global", 1);
	    	write("XP.Formula.Multiplier.Taming", 2);
	    	write("XP.Formula.Multiplier.Mining", 2);
	    	write("XP.Formula.Multiplier.Repair", 2);
	    	write("XP.Formula.Multiplier.Woodcutting", 2);
	    	write("XP.Formula.Multiplier.Unarmed", 2);
	    	write("XP.Formula.Multiplier.Herbalism", 2);
	    	write("XP.Formula.Multiplier.Excavation", 2);
	    	write("XP.Formula.Multiplier.Swords", 2);
	    	write("XP.Formula.Multiplier.Archery", 2);
	    	write("XP.Formula.Multiplier.Axes", 2);
	    	write("XP.Formula.Multiplier.Sorcery", 2);
	    	write("XP.Formula.Multiplier.Acrobatics", 2);
	    	write("XP.Mining.Gold", 35);
	    	write("XP.Mining.Diamond", 75);
	    	write("XP.Mining.Iron", 25);
	    	write("XP.Mining.Redstone", 15);
	    	write("XP.Mining.lapis", 40);
	    	write("XP.Mining.Obsidian", 15);
	    	write("XP.Mining.Netherrack", 3);
	    	write("XP.Mining.Glowstone", 3);
	    	write("XP.Mining.Coal", 10);
	    	write("XP.Mining.Stone", 3);
	    	write("XP.Mining.Sandstone", 3);
	    	write("XP.Herbalism.Sugar_Cane", 3);
	    	write("XP.Herbalism.Cactus", 3);
	    	write("XP.Herbalism.Pumpkin", 55);
	    	write("XP.Herbalism.Flowers", 10);
	    	write("XP.Herbalism.Wheat", 5);
	    	write("XP.Herbalism.Mushrooms", 15);
	    	write("XP.Woodcutting.Pine", 9);
	    	write("XP.Woodcutting.Birch", 7);
	    	write("XP.Woodcutting.Spruce", 8);
	    	write("XP.Excavation.Base", 4);
	    	write("XP.Excavation.Mushroom", 8);
	    	write("XP.Excavation.Sulphur", 3);
	    	write("XP.Excavation.Slowsand", 8);
	    	write("XP.Excavation.Glowstone", 8);
	    	write("XP.Excavation.Music", 300);
	    	write("XP.Excavation.Bones", 3);
	    	write("XP.Excavation.Diamond", 100);
	    	write("XP.Excavation.Apple", 10);
	    	write("XP.Excavation.Eggs", 10);
	    	write("XP.Excavation.Cake", 300);
	    	write("XP.Excavation.Slimeballs", 10);
	    	write("XP.Excavation.Cocoa_Beans", 10);
	    	
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
	    	write("Skills.Archery.Fire_Rate_Limiter.Enabled", true);
	    	write("Skills.Archery.Fire_Rate_Limiter.Interval", 1000);
	    	write("Skills.Mining.Requires_Pickaxe", true);
	    	write("Skills.Woodcutting.Requires_Axe", true);
	    	
	    	loadkeys();
	    }
	    private void loadkeys()
	    {
	        System.out.println("Loading Config File...");
	        
	        donateMessage = readBoolean("Commands.mcmmo.Donate_Message", true);
	        xpGainsMobSpawners = readBoolean("XP.Gains.Mobspawners.Enabled", false);
	        
	        msulphur = readInteger("XP.Excavation.Sulphur", 3);
	        mbones = readInteger("XP.Excavation.Bones", 3);
	        mbase = readInteger("XP.Excavation.Base", 4);
	        mmushroom2 = readInteger("XP.Excavation.Mushroom", 8);
	    	mslowsand = readInteger("XP.Excavation.Slowsand", 8);
	    	mglowstone2 = readInteger("XP.Excavation.Glowstone", 8);
	    	mmusic = readInteger("XP.Excavation.Music", 300);
	    	mdiamond2 = readInteger("XP.Excavation.Diamond", 100);
	    	mapple = readInteger("XP.Excavation.Apple", 10);
	    	meggs = readInteger("XP.Excavation.Eggs", 10);
	    	mcake = readInteger("XP.Excavation.Cake", 300);
	    	mcocoa = readInteger("XP.Excavation.Cocoa_Beans", 10);
	    	mslimeballs = readInteger("XP.Excavation.Slimeballs", 10);
	    	
	        msugar = readInteger("XP.Herbalism.Sugar_Cane", 3);
	        mwheat = readInteger("XP.Herbalism.Wheat", 5);
	    	mcactus = readInteger("XP.Herbalism.Cactus", 3);
	    	mpumpkin = readInteger("XP.Herbalism.Pumpkin", 55);
	    	mflower = readInteger("XP.Herbalism.Flowers", 10);
	    	mmushroom = readInteger("XP.Herbalism.Mushrooms", 15);
	    	
	    	mpine = readInteger("XP.Woodcutting.Pine", 7);
	    	mbirch = readInteger("XP.Woodcutting.Birch", 8);
	    	mspruce = readInteger("XP.Woodcutting.Spruce", 9);
	        
	        mgold = readInteger("XP.Mining.Gold", 25);
	        mdiamond = readInteger("XP.Mining.Diamond", 75);
	        miron = readInteger("XP.Mining.Iron", 25);
	        mredstone = readInteger("XP.Mining.Redstone", 15);
	        mlapis = readInteger("XP.Mining.lapis", 40);
	        mobsidian = readInteger("XP.Mining.Obsidian", 15);
	        mnetherrack = readInteger("XP.Mining.Netherrack", 3);
	        mglowstone = readInteger("XP.Mining.Glowstone", 3);
	        mcoal = readInteger("XP.Mining.Coal", 10);
	        mstone = readInteger("XP.Mining.Stone", 3);
	        msandstone = readInteger("XP.Mining.Sandstone", 3);
	        
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
	    	archeryFireRateLimit = readBoolean("Skills.Archery.Fire_Rate_Limiter.Enabled", true);
	    	archeryLimit = readInteger("Skills.Archery.Fire_Rate_Limiter.Interval", 1000);
	    	
	    	xpGainMultiplier = readInteger("XP.Gains.Multiplier.Global", 1);
	    	toolsLoseDurabilityFromAbilities = readBoolean("Abilities.Tools.Durability_Loss_Enabled", true);
	    	abilityDurabilityLoss = readInteger("Abilities.Tools.Durability_Loss", 2);
	    	
	    	feathersConsumedByChimaeraWing = readInteger("Items.Chimaera_Wing.Feather_Cost", 10);
	    	chimaeraId = readInteger("Items.Chimaera_Wing.Item_ID", 288);
	    	chimaeraWingEnable = readBoolean("Items.Chimaera_Wing.Enabled", true);
	    	
	    	pvpxp = readBoolean("XP.PVP.Rewards", true);
	    	pvpxprewardmodifier = readInteger("XP.Gains.Multiplier.PVP", 1);
	    	miningrequirespickaxe = readBoolean("Skills.Mining.Requires_Pickaxe", true);
	    	woodcuttingrequiresaxe = readBoolean("Skills.Woodcutting.Requires_Axe", true);
	    	repairdiamondlevel = readInteger("Skills.Repair.Diamond.Level_Required", 50);

	    	globalxpmodifier = readInteger("XP.Formula.Multiplier.Global", 1);
	    	sorceryxpmodifier = readInteger("XP.Formula.Multiplier.Sorcery", 2);
	    	tamingxpmodifier = readInteger("XP.Formula.Multiplier.Taming", 2);
	    	miningxpmodifier = readInteger("XP.Formula.Multiplier.Mining", 2);
	    	repairxpmodifier = readInteger("XP.Formula.Multiplier.Repair", 2);
	    	woodcuttingxpmodifier = readInteger("XP.Formula.Multiplier.Woodcutting", 2);
	    	unarmedxpmodifier = readInteger("XP.Formula.Multiplier.Unarmed", 2);
	    	herbalismxpmodifier = readInteger("XP.Formula.Multiplier.Herbalism", 2);
	    	excavationxpmodifier = readInteger("XP.Formula.Multiplier.Excavation", 2);
	    	archeryxpmodifier = readInteger("XP.Formula.Multiplier.Archery", 2);
	    	swordsxpmodifier = readInteger("XP.Formula.Multiplier.Swords", 2);
	    	axesxpmodifier = readInteger("XP.Formula.Multiplier.Axes", 2);
	    	acrobaticsxpmodifier = readInteger("XP.Formula.Multiplier.Acrobatics", 2);

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
	        }
	}