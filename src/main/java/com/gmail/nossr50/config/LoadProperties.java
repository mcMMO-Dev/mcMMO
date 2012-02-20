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

import com.gmail.nossr50.mcMMO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.datatypes.HUDType;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.datatypes.treasure.FishingTreasure;
import com.gmail.nossr50.datatypes.treasure.Treasure;

public class LoadProperties {
	public static Boolean enableOnlyActivateWhenSneaking,
			enableAbilityMessages, enableAbilities, showDisplayName, showFaces,
			xplockEnable, xpbar, xpicon, partybar, xprateEnable, spoutEnabled,
			donateMessage, chimaeraWingEnable, xpGainsMobSpawners, myspawnEnable,
			mccEnable, mcmmoEnable, partyEnable, inviteEnable, acceptEnable,
			whoisEnable, statsEnable, addxpEnable, ptpEnable, mmoeditEnable,
			clearmyspawnEnable, mcgodEnable, mcabilityEnable, mctopEnable,
			mcrefreshEnable, aEnable, pEnable, enableMotd, enableMySpawn,
			enableCobbleToMossy, useMySQL, toolsLoseDurabilityFromAbilities,
			pvpxp, miningrequirespickaxe, excavationRequiresShovel,
			woodcuttingrequiresaxe, anvilmessages, mayDowngradeEnchants,
			mayLoseEnchants, fishingDrops, leatherArmor, ironArmor, goldArmor,
			diamondArmor, woodenTools, stoneTools, ironTools, goldTools,
			diamondTools, enderPearl, blazeRod, records, glowstoneDust,
			fishingDiamonds, aDisplayNames, pDisplayNames, enableSmoothToMossy,
			enableDirtToGrass, statsTracking, eventCallback, herbalismHungerBonus;

	public static String MySQLtablePrefix, MySQLuserName,
			MySQLserverName, MySQLdbName, MySQLdbPass, nWood, nStone, 
			nIron, nGold, nDiamond, locale, nString, nLeather;

	public static int mfishing, xpbar_x, xpbar_y, xpicon_x, xpicon_y,
			chimaeraId, msandstone, mbase, mpine, mbirch, mspruce, mmelon,
			mcactus, mmushroom, mflower, msugar, mpumpkin, mwheat, mgold,
			mdiamond, miron, mredstone, mlapis, mobsidian, mnetherrack,
			mglowstone, mcoal, mstone, MySQLport, xpGainMultiplier,
			superBreakerCooldown, greenTerraCooldown, gigaDrillBreakerCooldown,
			treeFellerCooldown, berserkCooldown, serratedStrikeCooldown,
			skullSplitterCooldown, abilityDurabilityLoss,
			feathersConsumedByChimaeraWing, bonesConsumedByCOTW,
			repairdiamondlevel, rWood, rStone, rIron, rGold, rDiamond, rString,
			rLeather, downgradeRank1, downgradeRank2, downgradeRank3,
			downgradeRank4, keepEnchantsRank1, keepEnchantsRank2,
			keepEnchantsRank3, keepEnchantsRank4, fishingDropChanceTier1,
			fishingDropChanceTier2, fishingDropChanceTier3,
			fishingDropChanceTier4, fishingDropChanceTier5, mnetherwart,
			mvines, mlilypad, mnetherbrick, mendstone, mmossstone, mstonebrick,
			levelCapAcrobatics, levelCapArchery, levelCapAxes, levelCapExcavation,
			levelCapFishing, levelCapHerbalism, levelCapMining, levelCapRepair,
			levelCapSwords, levelCapTaming, levelCapUnarmed, levelCapWoodcutting,
			anvilID, saveInterval;

	public static double xpbackground_r, xpbackground_g, xpbackground_b,
			xpborder_r, xpborder_g, xpborder_b, fishing_r, fishing_g,
			fishing_b, acrobatics_r, acrobatics_g, acrobatics_b, archery_r,
			archery_g, archery_b, axes_r, axes_g, axes_b, excavation_r,
			excavation_g, excavation_b, herbalism_r, herbalism_g, herbalism_b,
			mining_r, mining_g, mining_b, repair_r, repair_g, repair_b,
			swords_r, swords_g, swords_b, taming_r, taming_g, taming_b,
			unarmed_r, unarmed_g, unarmed_b, woodcutting_r, woodcutting_g,
			woodcutting_b, pvpxprewardmodifier, tamingxpmodifier,
			miningxpmodifier, repairxpmodifier, woodcuttingxpmodifier,
			sorceryxpmodifier, unarmedxpmodifier, herbalismxpmodifier,
			excavationxpmodifier, archeryxpmodifier, swordsxpmodifier,
			axesxpmodifier, acrobaticsxpmodifier;

	public static List<ExcavationTreasure> excavationFromDirt = new ArrayList<ExcavationTreasure>();
	public static List<ExcavationTreasure> excavationFromGrass = new ArrayList<ExcavationTreasure>();
	public static List<ExcavationTreasure> excavationFromSand = new ArrayList<ExcavationTreasure>();
	public static List<ExcavationTreasure> excavationFromGravel = new ArrayList<ExcavationTreasure>();
	public static List<ExcavationTreasure> excavationFromClay = new ArrayList<ExcavationTreasure>();
	public static List<ExcavationTreasure> excavationFromMycel = new ArrayList<ExcavationTreasure>();
	public static List<ExcavationTreasure> excavationFromSoulSand = new ArrayList<ExcavationTreasure>();

	public static HUDType defaulthud;
	protected static File configFile;
	protected static File dataFolder;
	protected final mcMMO plugin;
	protected static FileConfiguration config;

	public LoadProperties(mcMMO plugin) {
		this.plugin = plugin;
		dataFolder = plugin.getDataFolder();
		configFile = new File(dataFolder, File.separator + "config.yml");
		config = plugin.getConfig();
	}

	public void load() {
		// If not exist, copy from the jar
		if (!configFile.exists()) {
			dataFolder.mkdir();
			plugin.saveDefaultConfig();
		}
		addDefaults();
		loadKeys();
	}

	private Boolean readBoolean(String root, Boolean def) {
		Boolean result = config.getBoolean(root, def);
		return result;
	}

	private Double readDouble(String root, Double def) {
		Double result = config.getDouble(root, def);
		return result;
	}

	private Integer readInteger(String root, Integer def) {
		Integer result = config.getInt(root, def);
		return result;
	}

	public static String readString(String root, String def) {
		String result = config.getString(root, def);
		return result;
	}

	private static void saveConfig() {
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addDefaults() {
		// Load from included config.yml
		config.options().copyDefaults(true);
		saveConfig();
	}

	private void loadKeys() {
		plugin.getLogger().info("Loading Config File...");

		// Setup default HUD
		String temp = readString("Spout.HUD.Default", "STANDARD");
		for (HUDType x : HUDType.values()) {
			if (x.toString().equalsIgnoreCase(temp)) {
				defaulthud = x;
			}
		}

		enableAbilityMessages = readBoolean("Abilities.Messages", true);
		enableAbilities = readBoolean("Abilities.Enabled", true);

		donateMessage = readBoolean("Commands.mcmmo.Donate_Message", true);
		xpGainsMobSpawners = readBoolean("Experience.Gains.Mobspawners.Enabled", false);

		bonesConsumedByCOTW = readInteger("Skills.Taming.Call_Of_The_Wild.Bones_Required", 10);

		xpbar = readBoolean("Spout.XP.Bar.Enabled", true);
		// web_url = readString("Spout.Images.URL_DIR",
		// "http://mcmmo.rycochet.net/mcmmo/");
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
		fishing_r = readDouble("Spout.HUD.Retro.Colors.Fishing.RED", 0.3);
		fishing_g = readDouble("Spout.HUD.Retro.Colors.Fishing.GREEN", 0.3);
		fishing_b = readDouble("Spout.HUD.Retro.Colors.Fishing.BLUE", 0.75);

		xpborder_r = readDouble("Spout.HUD.Retro.Colors.Border.RED", 0.0);
		xpborder_g = readDouble("Spout.HUD.Retro.Colors.Border.GREEN", 0.0);
		xpborder_b = readDouble("Spout.HUD.Retro.Colors.Border.BLUE", 0.0);
		xpbackground_r = readDouble("Spout.HUD.Retro.Colors.Background.RED", 0.75);
		xpbackground_g = readDouble("Spout.HUD.Retro.Colors.Background.GREEN", 0.75);
		xpbackground_b = readDouble("Spout.HUD.Retro.Colors.Background.BLUE", 0.75);

		mbase = readInteger("Experience.Excavation.Base", 40);

		msugar = readInteger("Experience.Herbalism.Sugar_Cane", 30);
		mwheat = readInteger("Experience.Herbalism.Wheat", 50);
		mcactus = readInteger("Experience.Herbalism.Cactus", 30);
		mpumpkin = readInteger("Experience.Herbalism.Pumpkin", 20);
		mflower = readInteger("Experience.Herbalism.Flowers", 100);
		mmushroom = readInteger("Experience.Herbalism.Mushrooms", 150);
		mmelon = readInteger("Experience.Herbalism.Melon", 20);
		mnetherwart = readInteger("Experience.Herbalism.Nether_Wart", 50);
		mlilypad = readInteger("Experience.Herbalism.Lily_Pads", 100);
		mvines = readInteger("Experience.Herbalism.Vines", 10);
		herbalismHungerBonus = readBoolean("Skills.Herbalism.Hunger_Bonus", true);

		mpine = readInteger("Experience.Woodcutting.Pine", 70);
		mbirch = readInteger("Experience.Woodcutting.Birch", 80);
		mspruce = readInteger("Experience.Woodcutting.Spruce", 90);

		mgold = readInteger("Experience.Mining.Gold", 250);
		mdiamond = readInteger("Experience.Mining.Diamond", 750);
		miron = readInteger("Experience.Mining.Iron", 250);
		mredstone = readInteger("Experience.Mining.Redstone", 150);
		mlapis = readInteger("Experience.Mining.Lapis", 400);
		mobsidian = readInteger("Experience.Mining.Obsidian", 150);
		mnetherrack = readInteger("Experience.Mining.Netherrack", 30);
		mglowstone = readInteger("Experience.Mining.Glowstone", 30);
		mcoal = readInteger("Experience.Mining.Coal", 100);
		mstone = readInteger("Experience.Mining.Stone", 30);
		msandstone = readInteger("Experience.Mining.Sandstone", 30);
		mnetherbrick = readInteger("Experience.Mining.Nether_Brick", 30);
		mendstone = readInteger("Experience.Mining.End_Stone", 150);
		mmossstone = readInteger("Experience.Mining.Moss_Stone", 30);
		mstonebrick = readInteger("Experience.Mining.Stone_Brick", 30);

		mfishing = readInteger("Experience.Fishing.Base", 800);

		enableOnlyActivateWhenSneaking = readBoolean("Abilities.Activation.Only_Activate_When_Sneaking", false);

		greenTerraCooldown = readInteger("Abilities.Cooldowns.Green_Terra", 240);
		superBreakerCooldown = readInteger("Abilities.Cooldowns.Super_Breaker", 240);
		gigaDrillBreakerCooldown = readInteger("Abilities.Cooldowns.Giga_Drill_Breaker", 240);
		treeFellerCooldown = readInteger("Abilities.Cooldowns.Tree_Feller", 240);
		berserkCooldown = readInteger("Abilities.Cooldowns.Berserk", 240);
		serratedStrikeCooldown = readInteger("Abilities.Cooldowns.Serrated_Strikes", 240);
		skullSplitterCooldown = readInteger("Abilities.Cooldowns.Skull_Splitter", 240);

		MySQLserverName = readString("MySQL.Server.Address", "localhost");
		if (readString("MySQL.Database.User.Password", null) != null)
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
		saveInterval = readInteger("General.Save_Interval", 10);
		statsTracking = readBoolean("General.Stats_Tracking", true);
		eventCallback = readBoolean("General.Event_Callback", true);

		enableCobbleToMossy = readBoolean("Skills.Herbalism.Green_Thumb.Cobble_To_Mossy", true);
		enableSmoothToMossy = readBoolean("Skills.Herbalism.Green_Thumb.SmoothBrick_To_MossyBrick", true);
		enableDirtToGrass = readBoolean("Skills.Herbalism.Green_Thumb.Dirt_To_Grass", true);

		xpGainMultiplier = readInteger("Experience.Gains.Multiplier.Global", 1);
		toolsLoseDurabilityFromAbilities = readBoolean("Abilities.Tools.Durability_Loss_Enabled", true);
		abilityDurabilityLoss = readInteger("Abilities.Tools.Durability_Loss", 2);

		feathersConsumedByChimaeraWing = readInteger("Items.Chimaera_Wing.Feather_Cost", 10);
		chimaeraId = readInteger("Items.Chimaera_Wing.Item_ID", 288);
		chimaeraWingEnable = readBoolean("Items.Chimaera_Wing.Enabled", true);

		pvpxp = readBoolean("Experience.PVP.Rewards", true);
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
		anvilID = readInteger("Skills.Repair.Anvil_ID", 42);

		rGold = readInteger("Skills.Repair.Gold.ID", 266);
		nGold = readString("Skills.Repair.Gold.Name", "Gold Bars");
		rStone = readInteger("Skills.Repair.Stone.ID", 4);
		nStone = readString("Skills.Repair.Stone.Name", "Cobblestone");
		rWood = readInteger("Skills.Repair.Wood.ID", 5);
		nWood = readString("Skills.Repair.Wood.Name", "Wood Planks");
		rDiamond = readInteger("Skills.Repair.Diamond.ID", 264);
		nDiamond = readString("Skills.Repair.Diamond.Name", "Diamond");
		rIron = readInteger("Skills.Repair.Iron.ID", 265);
		nIron = readString("Skills.Repair.Iron.Name", "Iron Bars");
		rString = readInteger("Skills.Repair.String.ID", 287);
		nString = readString("Skills.Repair.String.Name", "String");
		rLeather = readInteger("Skills.Repair.Leather.ID", 334);
		nLeather = readString("Skills.Repair.Leather.Name", "Leather");
		
		levelCapAcrobatics = readInteger("Skills.Acrobatics.Level_Cap", 0);
		levelCapArchery = readInteger("Skills.Archery.Level_Cap", 0);
		levelCapAxes = readInteger("Skills.Axes.Level_Cap", 0);
		levelCapExcavation = readInteger("Skills.Excavation.Level_Cap", 0);
		levelCapFishing = readInteger("Skills.Fishing.Level_Cap", 0);
		levelCapHerbalism = readInteger("Skills.Herbalism.Level_Cap", 0);
		levelCapMining = readInteger("Skills.Mining.Level_Cap", 0);
		levelCapRepair = readInteger("Skills.Repair.Level_Cap", 0);
		levelCapSwords = readInteger("Skills.Swords.Level_Cap", 0);
		levelCapTaming = readInteger("Skills.Taming.Level_Cap", 0);
		levelCapUnarmed = readInteger("Skills.Unarmed.Level_Cap", 0);
		levelCapWoodcutting = readInteger("Skills.Woodcutting.Level_Cap", 0);

		mayDowngradeEnchants = readBoolean("Arcane_Forging.Downgrades.Enabled", true);
		downgradeRank1 = readInteger("Arcane_Forging.Downgrades.Chance.Rank_1", 75);
		downgradeRank2 = readInteger("Arcane_Forging.Downgrades.Chance.Rank_2", 50);
		downgradeRank3 = readInteger("Arcane_Forging.Downgrades.Chance.Rank_3", 25);
		downgradeRank4 = readInteger("Arcane_Forging.Downgrades.Chance.Rank_4", 15);
		mayLoseEnchants = readBoolean("Arcane_Forging.May_Lose_Enchants.Enabled", true);
		keepEnchantsRank1 = readInteger("Arcane_Forging.Keep_Enchants.Chance.Rank_1", 10);
		keepEnchantsRank2 = readInteger("Arcane_Forging.Keep_Enchants.Chance.Rank_2", 20);
		keepEnchantsRank3 = readInteger("Arcane_Forging.Keep_Enchants.Chance.Rank_3", 30);
		keepEnchantsRank4 = readInteger("Arcane_Forging.Keep_Enchants.Chance.Rank_4", 40);

		fishingDrops = readBoolean("Fishing.Drops.Item_Drops_Enabled", true);
		fishingDropChanceTier1 = readInteger("Fishing.Drops.Drop_Chance.Tier_1", 20);
		fishingDropChanceTier2 = readInteger("Fishing.Drops.Drop_Chance.Tier_2", 25);
		fishingDropChanceTier3 = readInteger("Fishing.Drops.Drop_Chance.Tier_3", 30);
		fishingDropChanceTier4 = readInteger("Fishing.Drops.Drop_Chance.Tier_4", 35);
		fishingDropChanceTier5 = readInteger("Fishing.Drops.Drop_Chance.Tier_5", 40);
		leatherArmor = readBoolean("Fishing.Drops.Leather_Armor", true);
		ironArmor = readBoolean("Fishing.Drops.Iron_Armor", true);
		goldArmor = readBoolean("Fishing.Drops.Gold_Armor", true);
		diamondArmor = readBoolean("Fishing.Drops.Diamond_Armor", true);
		woodenTools = readBoolean("Fishing.Drops.Wooden_Tools", true);
		stoneTools = readBoolean("Fishing.Drops.Stone_Tools", true);
		ironTools = readBoolean("Fishing.Drops.Iron_Tools", true);
		goldTools = readBoolean("Fishing.Drops.Gold_Tools", true);
		diamondTools = readBoolean("Fishing.Drops.Diamond_Tools", true);
		enderPearl = readBoolean("Fishing.Drops.Ender_Pearl", true);
		blazeRod = readBoolean("Fishing.Drops.Blaze_Rod", true);
		records = readBoolean("Fishing.Drops.Records", true);
		glowstoneDust = readBoolean("Fishing.Drops.Glowstone_Dust", true);
		fishingDiamonds = readBoolean("Fishing.Drops.Diamonds", true);

		xplockEnable = readBoolean("Commands.xplock.Enabled", true);
		xprateEnable = readBoolean("Commands.xprate.Enabled", true);
		mctopEnable = readBoolean("Commands.mctop.Enabled", true);
		addxpEnable = readBoolean("Commands.addxp.Enabled", true);
		mcabilityEnable = readBoolean("Commands.mcability.Enabled", true);
		mcrefreshEnable = readBoolean("Commands.mcrefresh.Enabled", true);
		mcmmoEnable = readBoolean("Commands.mcmmo.Enabled", true);
		mccEnable = readBoolean("Commands.mcc.Enabled", true);
		mcgodEnable = readBoolean("Commands.mcgod.Enabled", true);
		statsEnable = readBoolean("Commands.stats.Enabled", true);
		mmoeditEnable = readBoolean("Commands.mmoedit.Enabled", true);
		ptpEnable = readBoolean("Commands.ptp.Enabled", true);
		partyEnable = readBoolean("Commands.party.Enabled", true);
		myspawnEnable = readBoolean("Commands.myspawn.Enabled", true);
		whoisEnable = readBoolean("Commands.whois.Enabled", true);
		inviteEnable = readBoolean("Commands.invite.Enabled", true);
		acceptEnable = readBoolean("Commands.accept.Enabled", true);
		clearmyspawnEnable = readBoolean("Commands.clearmyspawn.Enabled", true);
		aEnable = readBoolean("Commands.a.Enabled", true);
		pEnable = readBoolean("Commands.p.Enabled", true);
		
		aDisplayNames = readBoolean("Commands.a.Display_Names", true);
		pDisplayNames = readBoolean("Commands.p.Display_Names", true);
		
		// Load treasures
		Map<String, Treasure> treasures = new HashMap<String, Treasure>();

		ConfigurationSection treasureSection = config.getConfigurationSection("Treasures");
		Set<String> treasureConfigSet = treasureSection.getKeys(false);
		Iterator<String> iterator = treasureConfigSet.iterator();
		while(iterator.hasNext())
		{
			String treasureName = iterator.next();

			int id = config.getInt("Treasures." + treasureName + ".ID");
			int amount = config.getInt("Treasures." + treasureName + ".Amount");
			int data = config.getInt("Treasures." + treasureName + ".Data");

			int xp = config.getInt("Treasures." + treasureName + ".XP");
			Double dropChance = config.getDouble("Treasures." + treasureName + ".Drop_Chance");
			int dropLevel = config.getInt("Treasures." + treasureName + ".Drop_Level");

			ItemStack item = new ItemStack(id, amount, (byte) 0, (byte) data);

			if(readBoolean("Treasures." + treasureName + ".Drops_From.Fishing", false)) {
				// TODO: Fishing
			} else {
				ExcavationTreasure eTreasure = new ExcavationTreasure(item, xp, dropChance, dropLevel);
				if(readBoolean("Treasures." + treasureName + ".Drops_From.Dirt", false))
					eTreasure.setDropsFromDirt();
				if(readBoolean("Treasures." + treasureName + ".Drops_From.Grass", false))
					eTreasure.setDropsFromGrass();
				if(readBoolean("Treasures." + treasureName + ".Drops_From.Sand", false))
					eTreasure.setDropsFromSand();
				if(readBoolean("Treasures." + treasureName + ".Drops_From.Gravel", false))
					eTreasure.setDropsFromGravel();
				if(readBoolean("Treasures." + treasureName + ".Drops_From.Clay", false))
					eTreasure.setDropsFromClay();
				if(readBoolean("Treasures." + treasureName + ".Drops_From.Mycelium", false))
					eTreasure.setDropsFromMycel();
				if(readBoolean("Treasures." + treasureName + ".Drops_From.Soul_Sand", false))
					eTreasure.setDropsFromSoulSand();

				treasures.put(treasureName, eTreasure);
			}
		}

		List<String> excavationTreasures = config.getStringList("Excavation.Treasure");

		Iterator<String> treasureIterator = treasures.keySet().iterator();
		while(treasureIterator.hasNext()) {
			String treasureKey = treasureIterator.next();
			Treasure treasure = treasures.get(treasureKey);

			if(treasure instanceof FishingTreasure) {
				// TODO: Fishing
			} else if(treasure instanceof ExcavationTreasure) {
				if(!excavationTreasures.contains(treasureKey)) continue;

				ExcavationTreasure eTreasure = (ExcavationTreasure) treasure;
				if(eTreasure.getDropsFromDirt())
					excavationFromDirt.add(eTreasure);
				if(eTreasure.getDropsFromGrass())
					excavationFromGrass.add(eTreasure);
				if(eTreasure.getDropsFromSand())
					excavationFromSand.add(eTreasure);
				if(eTreasure.getDropsFromGravel())
					excavationFromGravel.add(eTreasure);
				if(eTreasure.getDropsFromClay())
					excavationFromClay.add(eTreasure);
				if(eTreasure.getDropsFromMycel())
					excavationFromMycel.add(eTreasure);
				if(eTreasure.getDropsFromSoulSand())
					excavationFromSoulSand.add(eTreasure);
			}
		}
	}
}