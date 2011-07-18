package com.gmail.nossr50.config;

import java.io.File;
import java.util.List;
import org.bukkit.util.config.Configuration;

public class LoadProperties 
{
	public static Boolean chimaeraWingEnable=true, xpGainsMobSpawners=false, myspawnEnable = true, mccEnable = true, mcmmoEnable = true, partyEnable = true, inviteEnable = true, acceptEnable = true, whoisEnable = true, statsEnable = true, addxpEnable = true, ptpEnable = true, mmoeditEnable = true, clearmyspawnEnable = true, mcgodEnable = true, mcabilityEnable = true, mctopEnable = true, mcrefreshEnable = true, enableMotd, enableMySpawn, enableRegen, enableCobbleToMossy, useMySQL, cocoabeans, archeryFireRateLimit, mushrooms, toolsLoseDurabilityFromAbilities, pvpxp, miningrequirespickaxe, woodcuttingrequiresaxe, eggs, apples, cake, music, diamond, glowstone, slowsand, sulphur, netherrack, bones, coal, clay, anvilmessages;
	public static String MySQLtablePrefix, MySQLuserName, MySQLserverName, MySQLdbName, MySQLdbPass, mctop, addxp, mcability, mcmmo, mcc, mcrefresh, mcgod, stats, mmoedit, ptp, party, myspawn, whois, invite, accept, clearmyspawn, nWood, nStone, nIron, nGold, nDiamond, locale;
	public static int chimaeraId=288, msandstone, mcocoa = 10, water_thunder = 75, cure_self = 5, cure_other = 5, mbones, msulphur, mslowsand, mmushroom2, mglowstone2, mmusic, mdiamond2, mbase, mapple, meggs, mcake, mpine, mbirch, mspruce, mcactus, mmushroom, mflower, msugar, mpumpkin, mwheat, mgold, mdiamond, miron, mredstone, mlapus, mobsidian, mnetherrack, mglowstone, mcoal, mstone, MySQLport, xpGainMultiplier, superBreakerCooldown = 240, greenTerraCooldown = 240, gigaDrillBreakerCooldown = 240, treeFellerCooldown = 240, berserkCooldown = 240, serratedStrikeCooldown = 240, skullSplitterCooldown = 240, abilityDurabilityLoss, feathersConsumedByChimaeraWing, pvpxprewardmodifier, repairdiamondlevel, globalxpmodifier, tamingxpmodifier, miningxpmodifier, repairxpmodifier, woodcuttingxpmodifier, sorceryxpmodifier = 2, unarmedxpmodifier, herbalismxpmodifier, excavationxpmodifier, archeryxpmodifier, swordsxpmodifier, axesxpmodifier, acrobaticsxpmodifier, rWood, rStone, rIron, rGold, rDiamond;
	
	public String directory = "plugins/mcMMO/"; 
	File file = new File(directory + File.separator + "config.yml");
	
	public void configCheck()
	{
		new File(directory).mkdir();
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
	        Configuration config = load();
	        config.setProperty(root, x);
	        config.save();
	    }
	    private Boolean readBoolean(String root)
	    {
	        Configuration config = load();
	        return config.getBoolean(root, false);
	    }
	    private Integer readInteger(String root)
	    {
	    	Configuration config = load();
	    	return config.getInt(root, 0);
	    }

	    @SuppressWarnings("unused")
		private Double readDouble(String root)
	    {
	        Configuration config = load();
	        return config.getDouble(root, 0);
	    }
	    @SuppressWarnings("unused")
		private List<String> readStringList(String root)
	    {
	        Configuration config = load();
	        return config.getKeys(root);
	    }
	    
	    private String readString(String root)
	    {
	        Configuration config = load();
	        return config.getString(root);
	    }
	    private Configuration load()
	    {

	        try {
	            Configuration config = new Configuration(file);
	            config.load();
	            return config;

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
	    	write("XP.Mining.Lapus", 40);
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
	    	
	    	write("Commands.mctop.Name", "mctop");
	    	write("Commands.mctop.Enabled", true);
	    	write("Commands.addxp.Name", "addxp");
	    	write("Commands.addxp.Enabled", true);
	    	write("Commands.mcability.Name", "mcability");
	    	write("Commands.mcability.Enabled", true);
	    	write("Commands.mcrefresh.Name", "mcrefresh");
	    	write("Commands.mcrefresh.Enabled", true);
	    	write("Commands.mcmmo.Name", "mcmmo");
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
	    	write("Skills.Repair.Diamond.Name", "Diamond Ore");
	    	write("Skills.Repair.Diamond.Level_Required", 50);
	    	write("Skills.Repair.Iron.ID", 265);
	    	write("Skills.Repair.Iron.Name", "Iron Bars");
	    	write("Skills.Herbalism.Green_Thumb.Cobble_To_Mossy", true);
	    	write("Skills.Archery.Fire_Rate_Limiter", true);
	    	write("Skills.Mining.Requires_Pickaxe", true);
	    	write("Skills.Woodcutting.Requires_Axe", true);
	    	
	     loadkeys();
	    }
	    private void loadkeys()
	    {
	        System.out.println("Loading Config File...");
	        
	        xpGainsMobSpawners = readBoolean("XP.Gains.Mobspawners.Enabled");
	        
	        //cure_self = readInteger("Sorcery.Spells.Curative.Cure_Self.Mana_Cost");
	        //cure_other = readInteger("Sorcery.Spells.Curative.Cure_Other.Mana_Cost");
	        //water_thunder = readInteger("Sorcery.Spells.Water.Thunder");
	        
	        msulphur = readInteger("XP.Excavation.Sulphur");
	        mbones = readInteger("XP.Excavation.Bones");
	        mbase = readInteger("XP.Excavation.Base");
	        mmushroom2 = readInteger("XP.Excavation.Mushroom");
	    	mslowsand = readInteger("XP.Excavation.Slowsand");
	    	mglowstone2 = readInteger("XP.Excavation.Glowstone");
	    	mmusic = readInteger("XP.Excavation.Music");
	    	mdiamond2 = readInteger("XP.Excavation.Diamond");
	    	mapple = readInteger("XP.Excavation.Apple");
	    	meggs = readInteger("XP.Excavation.Eggs");
	    	mcake = readInteger("XP.Excavation.Cake");
	    	mcocoa = readInteger("XP.Excavation.Cocoa_Beans");
	        
	        msugar = readInteger("XP.Herbalism.Sugar_Cane");
	        mwheat = readInteger("XP.Herbalism.Wheat");
	    	mcactus = readInteger("XP.Herbalism.Cactus");
	    	mpumpkin = readInteger("XP.Herbalism.Pumpkin");
	    	mflower = readInteger("XP.Herbalism.Flowers");
	    	mmushroom = readInteger("XP.Herbalism.Mushrooms");
	    	
	    	mpine = readInteger("XP.Woodcutting.Pine");
	    	mbirch = readInteger("XP.Woodcutting.Birch");
	    	mspruce = readInteger("XP.Woodcutting.Spruce");
	        
	        mgold = readInteger("XP.Mining.Gold");
	        mdiamond = readInteger("XP.Mining.Diamond");
	        miron = readInteger("XP.Mining.Iron");
	        mredstone = readInteger("XP.Mining.Redstone");
	        mlapus = readInteger("XP.Mining.Lapus");
	        mobsidian = readInteger("XP.Mining.Obsidian");
	        mnetherrack = readInteger("XP.Mining.Netherrack");
	        mglowstone = readInteger("XP.Mining.Glowstone");
	        mcoal = readInteger("XP.Mining.Coal");
	        mstone = readInteger("XP.Mining.Stone");
	        msandstone = readInteger("XP.Mining.Sandstone");
	        
	        enableMotd = readBoolean("enableMOTD");
	        
	        greenTerraCooldown = readInteger("Abilities.Cooldowns.Green_Terra");
	    	superBreakerCooldown = readInteger("Abilities.Cooldowns.Super_Breaker");
	    	gigaDrillBreakerCooldown = readInteger("Abilities.Cooldowns.Giga_Drill_Breaker");
	    	treeFellerCooldown = readInteger("Abilities.Cooldowns.Tree_Feller");
	    	berserkCooldown = readInteger("Abilities.Cooldowns.Berserk");
	    	serratedStrikeCooldown = readInteger("Abilities.Cooldowns.Serrated_Strikes");
	    	skullSplitterCooldown = readInteger("Abilities.Cooldowns.Skull_Splitter");
	    	
	    	MySQLserverName = readString("MySQL.Server.Address");
	    	
	    	if(readString("MySQL.Database.User.Password") != null)
	    		MySQLdbPass = readString("MySQL.Database.User.Password");
	    	else
	    		MySQLdbPass = "";
	    	
	    	MySQLdbName = readString("MySQL.Database.Name");
	    	MySQLuserName = readString("MySQL.Database.User.Name");
	    	MySQLtablePrefix = readString("MySQL.Database.TablePrefix");
	    	MySQLport = readInteger("MySQL.Server.Port");
	    	useMySQL = readBoolean("MySQL.Enabled");
	    	
	    	locale = readString("General.Locale");
	    	enableMotd = readBoolean("General.MOTD.Enabled");
	    	enableMySpawn = readBoolean("General.MySpawn.Enabled");
	    	enableRegen = readBoolean("General.HP_Regeneration.Enabled");
	    	
	    	enableCobbleToMossy = readBoolean("Skills.Herbalism.Green_Thumb.Cobble_To_Mossy");
	    	archeryFireRateLimit = readBoolean("Skills.Archery.Fire_Rate_Limiter");
	    	
	    	xpGainMultiplier = readInteger("XP.Gains.Multiplier.Global");
	    	toolsLoseDurabilityFromAbilities = readBoolean("Abilities.Tools.Durability_Loss_Enabled");
	    	abilityDurabilityLoss = readInteger("Abilities.Tools.Durability_Loss");
	    	
	    	feathersConsumedByChimaeraWing = readInteger("Items.Chimaera_Wing.Feather_Cost");
	    	chimaeraId = readInteger("Items.Chimaera_Wing.Item_ID");
	    	chimaeraWingEnable = readBoolean("Items.Chimaera_Wing.Enabled");
	    	
	    	pvpxp = readBoolean("XP.PVP.Rewards");
	    	pvpxprewardmodifier = readInteger("XP.Gains.Multiplier.PVP");
	    	miningrequirespickaxe = readBoolean("Skills.Mining.Requires_Pickaxe");
	    	woodcuttingrequiresaxe = readBoolean("Skills.Woodcutting.Requires_Axe");
	    	repairdiamondlevel = readInteger("Skills.Repair.Diamond.Level_Required");

	    	globalxpmodifier = readInteger("XP.Formula.Multiplier.Global");
	    	if(readInteger("XP.Formula.Multiplier.Sorcery") != null)
	    		sorceryxpmodifier = readInteger("XP.Formula.Multiplier.Sorcery");
	    	else
	    		sorceryxpmodifier = 2;
	    	tamingxpmodifier = readInteger("XP.Formula.Multiplier.Taming");
	    	miningxpmodifier = readInteger("XP.Formula.Multiplier.Mining");
	    	repairxpmodifier = readInteger("XP.Formula.Multiplier.Repair");
	    	woodcuttingxpmodifier = readInteger("XP.Formula.Multiplier.Woodcutting");
	    	unarmedxpmodifier = readInteger("XP.Formula.Multiplier.Unarmed");
	    	herbalismxpmodifier = readInteger("XP.Formula.Multiplier.Herbalism");
	    	excavationxpmodifier = readInteger("XP.Formula.Multiplier.Excavation");
	    	archeryxpmodifier = readInteger("XP.Formula.Multiplier.Archery");
	    	swordsxpmodifier = readInteger("XP.Formula.Multiplier.Swords");
	    	axesxpmodifier = readInteger("XP.Formula.Multiplier.Axes");
	    	acrobaticsxpmodifier = readInteger("XP.Formula.Multiplier.Acrobatics");

	    	anvilmessages = readBoolean("Skills.Repair.Anvil_Messages");
	    	
	        rGold =  readInteger("Skills.Repair.Gold.ID");
	        nGold =  readString("Skills.Repair.Gold.Name");      
	        rStone =  readInteger("Skills.Repair.Stone.ID");
	        nStone =  readString("Skills.Repair.Stone.Name");     
	        rWood =  readInteger("Skills.Repair.Wood.ID");
	        nWood =  readString("Skills.Repair.Wood.Name");        
	        rDiamond =   readInteger("Skills.Repair.Diamond.ID");
	        nDiamond =  readString("Skills.Repair.Diamond.Name");          
	        rIron =   readInteger("Skills.Repair.Iron.ID");
	        nIron =  readString("Skills.Repair.Iron.Name");  

	    	cocoabeans = readBoolean("Excavation.Drops.Cocoa_Beans");
	    	mushrooms = readBoolean("Excavation.Drops.Mushrooms");
	    	glowstone = readBoolean("Excavation.Drops.Glowstone");
	    	eggs = readBoolean("Excavation.Drops.Eggs");
	    	apples = readBoolean("Excavation.Drops.Apples");
	    	cake = readBoolean("Excavation.Drops.Cake");
	    	music = readBoolean("Excavation.Drops.Music");
	    	diamond = readBoolean("Excavation.Drops.Diamond");
	    	slowsand = readBoolean("Excavation.Drops.Slowsand");
	    	sulphur = readBoolean("Excavation.Drops.Sulphur");
	    	netherrack = readBoolean("Excavation.Drops.Netherrack");
	    	bones = readBoolean("Excavation.Drops.Bones");
	    	
	    	mctop = readString("Commands.mctop.Name");
	    	mctopEnable = readBoolean("Commands.mctop.Enabled");
	    	
	    	addxp = readString("Commands.addxp.Name");
	    	addxpEnable = readBoolean("Commands.addxp.Enabled");
	    	
	    	mcability = readString("Commands.mcability.Name");
	    	mcabilityEnable = readBoolean("Commands.mcability.Enabled");
	    	
	    	mcrefresh = readString("Commands.mcrefresh.Name");
	    	mcrefreshEnable = readBoolean("Commands.mcrefresh.Enabled");
	    	
	    	mcmmo = readString("Commands.mcmmo.Name");
	    	mcmmoEnable = readBoolean("Commands.mcmmo.Enabled");
	    	
	    	mcc = readString("Commands.mcc.Name");
	    	mccEnable = readBoolean("Commands.mcc.Enabled");
	    	
	    	mcgod = readString("Commands.mcgod.Name");
	    	mcgodEnable = readBoolean("Commands.mcgod.Enabled");
	    	
	    	stats = readString("Commands.stats.Name");
	    	statsEnable = readBoolean("Commands.stats.Enabled");
	    	
	    	mmoedit = readString("Commands.mmoedit.Name");
	    	mmoeditEnable = readBoolean("Commands.mmoedit.Enabled");
	    	
	    	ptp = readString("Commands.ptp.Name");
	    	ptpEnable = readBoolean("Commands.ptp.Enabled");
	    	
	    	party = readString("Commands.party.Name");
	    	partyEnable = readBoolean("Commands.party.Enabled");
	    	
	    	myspawn = readString("Commands.myspawn.Name");
	    	myspawnEnable = readBoolean("Commands.myspawn.Enabled");
	    	
	    	whois = readString("Commands.whois.Name");
	    	whoisEnable = readBoolean("Commands.whois.Enabled");
	    	
	    	invite = readString("Commands.invite.Name");
	    	inviteEnable = readBoolean("Commands.invite.Enabled");
	    	
	    	accept = readString("Commands.accept.Name");
	    	acceptEnable = readBoolean("Commands.accept.Enabled");
	    	
	    	clearmyspawn = readString("Commands.clearmyspawn.Name");
	    	clearmyspawnEnable = readBoolean("Commands.clearmyspawn.Enabled");
	        }
	}