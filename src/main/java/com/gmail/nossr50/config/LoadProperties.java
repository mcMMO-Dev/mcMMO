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

import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.nossr50.datatypes.HUDType;

public class LoadProperties {
	public static Boolean enableOnlyActivateWhenSneaking,
			enableAbilityMessages, enableAbilities, showDisplayName, showFaces,
			watch, xplockEnable, xpbar, xpicon, partybar, string, bucket, web,
			xprateEnable, slimeballs, spoutEnabled, donateMessage,
			chimaeraWingEnable, xpGainsMobSpawners, myspawnEnable, mccEnable,
			mcmmoEnable, partyEnable, inviteEnable, acceptEnable, whoisEnable,
			statsEnable, addxpEnable, ptpEnable, mmoeditEnable,
			clearmyspawnEnable, mcgodEnable, mcabilityEnable, mctopEnable,
			mcrefreshEnable, enableMotd, enableMySpawn, enableRegen,
			enableCobbleToMossy, useMySQL, cocoabeans, mushrooms,
			toolsLoseDurabilityFromAbilities, pvpxp, miningrequirespickaxe,
			excavationRequiresShovel, woodcuttingrequiresaxe, eggs, apples,
			cake, music, diamond, glowstone, slowsand, sulphur, netherrack,
			bones, coal, clay, anvilmessages, mayDowngradeEnchants,
			mayLoseEnchants, fishingDrops, leatherArmor, ironArmor, goldArmor,
			diamondArmor, woodenTools, stoneTools, ironTools, goldTools,
			diamondTools, enderPearl, blazeRod, records, glowstoneDust,
			fishingDiamonds;

	public static String xplock, MySQLtablePrefix, MySQLuserName,
			MySQLserverName, MySQLdbName, MySQLdbPass, mctop, addxp, xprate,
			mcability, mcmmo, mcc, mcrefresh, mcgod, stats, mmoedit, ptp,
			party, myspawn, whois, invite, accept, clearmyspawn, nWood, nStone,
			nIron, nGold, nDiamond, locale, nString, nLeather;

	public static int mfishing, mwatch, xpbar_x, xpbar_y, xpicon_x, xpicon_y,
			mstring, mbucket, mweb, chimaeraId, msandstone, mcocoa,
			water_thunder, cure_self, cure_other, mslimeballs, mbones,
			msulphur, mslowsand, mmushroom2, mglowstone2, mmelon, mmusic,
			mdiamond2, mbase, mapple, meggs, mcake, mpine, mbirch, mspruce,
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
			mvines, mlilypad;

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

	private void writeDefault(String root, Object x) {
		config.addDefault(root, x);
	}

	private Boolean readBoolean(String root, Boolean def) {
		// Configuration config = load();
		Boolean result = config.getBoolean(root, def);
		saveConfig();	// Why?
		return result;
	}

	private Double readDouble(String root, Double def) {
		Double result = config.getDouble(root, def);
		saveConfig();	// Why?
		return result;
	}

	private Integer readInteger(String root, Integer def) {
		// Configuration config = load();
		Integer result = config.getInt(root, def);
		saveConfig();	// Why?
		return result;
	}

	public static String readString(String root, String def) {
		// Configuration config = load();
		String result = config.getString(root, def);
		saveConfig();	// Why?
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

		// Put in defaults
		writeDefault("Spout.HUD.Default", "STANDARD");
		writeDefault("Spout.XP.Bar.Enabled", true);
		writeDefault("Spout.Images.URL_DIR", "http://mcmmo.rycochet.net/mcmmo/");
		writeDefault("Spout.XP.Icon.Enabled", true);
		writeDefault("Spout.XP.Bar.X_POS", 95);
		writeDefault("Spout.XP.Bar.Y_POS", 6);
		writeDefault("Spout.XP.Icon.X_POS", 78);
		writeDefault("Spout.XP.Icon.Y_POS", 2);
		writeDefault("Spout.Party.HUD.Enabled", true);
		writeDefault("Spout.Party.HUD.Show_Faces", true);
		writeDefault("Spout.Party.HUD.Show_Display_Name", false);
		writeDefault("Spout.Menu.Key", "KEY_M");
		writeDefault("Spout.HUD.Retro.Colors.Acrobatics.RED", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Acrobatics.GREEN", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Acrobatics.BLUE", 0.75);
		writeDefault("Spout.HUD.Retro.Colors.Archery.RED", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Archery.GREEN", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Archery.BLUE", 0.75);
		writeDefault("Spout.HUD.Retro.Colors.Axes.RED", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Axes.GREEN", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Axes.BLUE", 0.75);
		writeDefault("Spout.HUD.Retro.Colors.Excavation.RED", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Excavation.GREEN", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Excavation.BLUE", 0.75);
		writeDefault("Spout.HUD.Retro.Colors.Herbalism.RED", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Herbalism.GREEN", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Herbalism.BLUE", 0.75);
		writeDefault("Spout.HUD.Retro.Colors.Mining.RED", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Mining.GREEN", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Mining.BLUE", 0.75);
		writeDefault("Spout.HUD.Retro.Colors.Repair.RED", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Repair.GREEN", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Repair.BLUE", 0.75);
		writeDefault("Spout.HUD.Retro.Colors.Swords.RED", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Swords.GREEN", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Swords.BLUE", 0.75);
		writeDefault("Spout.HUD.Retro.Colors.Taming.RED", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Taming.GREEN", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Taming.BLUE", 0.75);
		writeDefault("Spout.HUD.Retro.Colors.Unarmed.RED", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Unarmed.GREEN", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Unarmed.BLUE", 0.75);
		writeDefault("Spout.HUD.Retro.Colors.Woodcutting.RED", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Woodcutting.GREEN", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Woodcutting.BLUE", 0.75);
		writeDefault("Spout.HUD.Retro.Colors.Fishing.RED", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Fishing.GREEN", 0.3);
		writeDefault("Spout.HUD.Retro.Colors.Fishing.BLUE", 0.75);
		writeDefault("Spout.HUD.Retro.Colors.Border.RED", 0.0);
		writeDefault("Spout.HUD.Retro.Colors.Border.GREEN", 0.0);
		writeDefault("Spout.HUD.Retro.Colors.Border.BLUE", 0.0);
		writeDefault("Spout.HUD.Retro.Colors.Background.RED", 0.75);
		writeDefault("Spout.HUD.Retro.Colors.Background.GREEN", 0.75);
		writeDefault("Spout.HUD.Retro.Colors.Background.BLUE", 0.75);

		writeDefault("MySQL.Enabled", false);
		writeDefault("MySQL.Server.Address", "localhost");
		writeDefault("MySQL.Server.Port", 3306);
		writeDefault("MySQL.Database.Name", "DataBaseName");
		writeDefault("MySQL.Database.User.Name", "UserName");
		writeDefault("MySQL.Database.TablePrefix", "mcmmo_");
		writeDefault("MySQL.Database.User.Password", "UserPassword");

		writeDefault("General.Locale", "en_us");
		writeDefault("General.MOTD.Enabled", true);
		writeDefault("General.MySpawn.Enabled", true);
		writeDefault("General.HP_Regeneration.Enabled", true);

		writeDefault("Items.Chimaera_Wing.Enabled", true);
		writeDefault("Items.Chimaera_Wing.Feather_Cost", 10);
		writeDefault("Items.Chimaera_Wing.Item_ID", 288);

		writeDefault("Experience.PVP.Rewards", true);
		writeDefault("Experience.Gains.Multiplier.PVP", 1);
		writeDefault("Experience.Gains.Mobspawners.Enabled", false);
		writeDefault("Experience.Gains.Multiplier.Global", 1.0);
		writeDefault("Experience.Formula.Multiplier.Taming", 1.0);
		writeDefault("Experience.Formula.Multiplier.Mining", 1.0);
		writeDefault("Experience.Formula.Multiplier.Repair", 1.0);
		writeDefault("Experience.Formula.Multiplier.Woodcutting", 1.0);
		writeDefault("Experience.Formula.Multiplier.Unarmed", 1.0);
		writeDefault("Experience.Formula.Multiplier.Herbalism", 1.0);
		writeDefault("Experience.Formula.Multiplier.Excavation", 1.0);
		writeDefault("Experience.Formula.Multiplier.Swords", 1.0);
		writeDefault("Experience.Formula.Multiplier.Archery", 1.0);
		writeDefault("Experience.Formula.Multiplier.Axes", 1.0);
		writeDefault("Experience.Formula.Multiplier.Sorcery", 1.0);
		writeDefault("Experience.Formula.Multiplier.Acrobatics", 1.0);

		// Mining XP values
		writeDefault("Experience.Mining.Gold", 350);
		writeDefault("Experience.Mining.Diamond", 750);
		writeDefault("Experience.Mining.Iron", 250);
		writeDefault("Experience.Mining.Redstone", 150);
		writeDefault("Experience.Mining.Lapis", 400);
		writeDefault("Experience.Mining.Obsidian", 150);
		writeDefault("Experience.Mining.Netherrack", 30);
		writeDefault("Experience.Mining.Glowstone", 30);
		writeDefault("Experience.Mining.Coal", 100);
		writeDefault("Experience.Mining.Stone", 30);
		writeDefault("Experience.Mining.Sandstone", 30);

		// Herbalism XP values
		writeDefault("Experience.Herbalism.Sugar_Cane", 30);
		writeDefault("Experience.Herbalism.Cactus", 30);
		writeDefault("Experience.Herbalism.Pumpkin", 20);
		writeDefault("Experience.Herbalism.Flowers", 100);
		writeDefault("Experience.Herbalism.Wheat", 50);
		writeDefault("Experience.Herbalism.Mushrooms", 150);
		writeDefault("Experience.Herbalism.Melon", 20);
		writeDefault("Experience.Herbalism.Nether_Wart", 50);
		writeDefault("Experience.Herbalism.Lily_Pads", 100);
		writeDefault("Experience.Herbalism.Vines", 10);

		// Woodcutting XP values
		writeDefault("Experience.Woodcutting.Pine", 90);
		writeDefault("Experience.Woodcutting.Birch", 70);
		writeDefault("Experience.Woodcutting.Spruce", 80);

		// Excavation XP values
		writeDefault("Experience.Excavation.Base", 40);
		writeDefault("Experience.Excavation.Mushroom", 80);
		writeDefault("Experience.Excavation.Sulphur", 30);
		writeDefault("Experience.Excavation.Slowsand", 80);
		writeDefault("Experience.Excavation.Glowstone", 80);
		writeDefault("Experience.Excavation.Music", 3000);
		writeDefault("Experience.Excavation.Bones", 30);
		writeDefault("Experience.Excavation.Diamond", 1000);
		writeDefault("Experience.Excavation.Apple", 100);
		writeDefault("Experience.Excavation.Eggs", 100);
		writeDefault("Experience.Excavation.Cake", 3000);
		writeDefault("Experience.Excavation.Slimeballs", 100);
		writeDefault("Experience.Excavation.Cocoa_Beans", 100);
		writeDefault("Experience.Excavation.Map", 200);
		writeDefault("Experience.Excavation.String", 200);
		writeDefault("Experience.Excavation.Bucket", 100);
		writeDefault("Experience.Excavation.Web", 150);

		// Fishing XP values
		writeDefault("Experience.Fishing.Base", 800);

		// writeDefault("Sorcery.Spells.Water.Thunder", 75);
		// writeDefault("Sorcery.Spells.Curative.Cure_Self.Mana_Cost", 5);
		// writeDefault("Sorcery.Spells.Curative.Cure_Other.Mana_Cost", 5);

		writeDefault("Excavation.Drops.Cocoa_Beans", true);
		writeDefault("Excavation.Drops.Mushrooms", true);
		writeDefault("Excavation.Drops.Glowstone", true);
		writeDefault("Excavation.Drops.Eggs", true);
		writeDefault("Excavation.Drops.Apples", true);
		writeDefault("Excavation.Drops.Cake", true);
		writeDefault("Excavation.Drops.Music", true);
		writeDefault("Excavation.Drops.Diamond", true);
		writeDefault("Excavation.Drops.Slowsand", true);
		writeDefault("Excavation.Drops.Sulphur", true);
		writeDefault("Excavation.Drops.Netherrack", true);
		writeDefault("Excavation.Drops.Bones", true);
		writeDefault("Excavation.Drops.Slimeballs", true);
		writeDefault("Excavation.Drops.Map", true);
		writeDefault("Excavation.Drops.String", true);
		writeDefault("Excavation.Drops.Bucket", true);
		writeDefault("Excavation.Drops.Web", true);

		writeDefault("Commands.xprate.Name", "xprate");
		writeDefault("Commands.xprate.Enabled", true);
		writeDefault("Commands.mctop.Name", "mctop");
		writeDefault("Commands.mctop.Enabled", true);
		writeDefault("Commands.addxp.Name", "addxp");
		writeDefault("Commands.addxp.Enabled", true);
		writeDefault("Commands.mcability.Name", "mcability");
		writeDefault("Commands.mcability.Enabled", true);
		writeDefault("Commands.mcrefresh.Name", "mcrefresh");
		writeDefault("Commands.mcrefresh.Enabled", true);
		writeDefault("Commands.mcmmo.Name", "mcmmo");
		writeDefault("Commands.mcmmo.Donate_Message", true);
		writeDefault("Commands.mcmmo.Enabled", true);
		writeDefault("Commands.mcc.Name", "mcc");
		writeDefault("Commands.mcc.Enabled", true);
		writeDefault("Commands.mcgod.Name", "mcgod");
		writeDefault("Commands.mcgod.Enabled", true);
		writeDefault("Commands.stats.Name", "stats");
		writeDefault("Commands.stats.Enabled", true);
		writeDefault("Commands.mmoedit.Name", "mmoedit");
		writeDefault("Commands.mmoedit.Enabled", true);
		writeDefault("Commands.ptp.Name", "ptp");
		writeDefault("Commands.ptp.Enabled", true);
		writeDefault("Commands.party.Name", "party");
		writeDefault("Commands.party.Enabled", true);
		writeDefault("Commands.myspawn.Name", "myspawn");
		writeDefault("Commands.myspawn.Enabled", true);
		writeDefault("Commands.whois.Name", "whois");
		writeDefault("Commands.whois.Enabled", true);
		writeDefault("Commands.invite.Name", "invite");
		writeDefault("Commands.invite.Enabled", true);
		writeDefault("Commands.accept.Name", "accept");
		writeDefault("Commands.accept.Enabled", true);
		writeDefault("Commands.clearmyspawn.Name", "clearmyspawn");
		writeDefault("Commands.clearmyspawn.Enabled", true);
		writeDefault("Commands.xplock.Enabled", true);
		writeDefault("Commands.xplock.Name", "xplock");

		writeDefault("Abilities.Tools.Durability_Loss_Enabled", true);
		writeDefault("Abilities.Tools.Durability_Loss", 2);
		writeDefault("Abilities.Activation.Only_Activate_When_Sneaking", false);
		writeDefault("Abilities.Cooldowns.Green_Terra", 240);
		writeDefault("Abilities.Cooldowns.Super_Breaker", 240);
		writeDefault("Abilities.Cooldowns.Giga_Drill_Breaker", 240);
		writeDefault("Abilities.Cooldowns.Tree_Feller", 240);
		writeDefault("Abilities.Cooldowns.Berserk", 240);
		writeDefault("Abilities.Cooldowns.Serrated_Strikes", 240);
		writeDefault("Abilities.Cooldowns.Skull_Splitter", 240);
		writeDefault("Abilities.Messages", true);
		writeDefault("Abilities.Enabled", true);

		writeDefault("Skills.Repair.Anvil_Messages", true);
		writeDefault("Skills.Repair.Gold.ID", 266);
		writeDefault("Skills.Repair.Gold.Name", "Gold Bars");
		writeDefault("Skills.Repair.Stone.ID", 4);
		writeDefault("Skills.Repair.Stone.Name", "Cobblestone");
		writeDefault("Skills.Repair.Wood.ID", 5);
		writeDefault("Skills.Repair.Wood.Name", "Wood Planks");
		writeDefault("Skills.Repair.Diamond.ID", 264);
		writeDefault("Skills.Repair.Diamond.Name", "Diamond");
		writeDefault("Skills.Repair.Diamond.Level_Required", 50);
		writeDefault("Skills.Repair.Iron.ID", 265);
		writeDefault("Skills.Repair.Iron.Name", "Iron Bars");
		writeDefault("Skills.Repair.String.ID", 287);
		writeDefault("Skills.Repair.String.Name", "String");
		writeDefault("Skills.Repair.Leather.ID", 334);
		writeDefault("Skills.Repair.String.Name", "Leather");
		writeDefault("Skills.Herbalism.Green_Thumb.Cobble_To_Mossy", true);
		writeDefault("Skills.Excavation.Requires_Shovel", true);
		writeDefault("Skills.Mining.Requires_Pickaxe", true);
		writeDefault("Skills.Woodcutting.Requires_Axe", true);
		writeDefault("Skills.Taming.Call_Of_The_Wild.Bones_Required", 10);

		// Arcane Forging Config Options
		writeDefault("Arcane_Forging.Downgrades.Enabled", true);
		writeDefault("Arcane_Forging.Downgrades.Chance.Rank_1", 75);
		writeDefault("Arcane_Forging.Downgrades.Chance.Rank_2", 50);
		writeDefault("Arcane_Forging.Downgrades.Chance.Rank_3", 25);
		writeDefault("Arcane_Forging.Downgrades.Chance.Rank_4", 15);
		writeDefault("Arcane_Forging.May_Lose_Enchants.Enabled", true);
		writeDefault("Arcane_Forging.Keep_Enchants.Chance.Rank_1", 10);
		writeDefault("Arcane_Forging.Keep_Enchants.Chance.Rank_2", 20);
		writeDefault("Arcane_Forging.Keep_Enchants.Chance.Rank_3", 30);
		writeDefault("Arcane_Forging.Keep_Enchants.Chance.Rank_4", 40);

		// Fishing Config Options
		writeDefault("Fishing.Drops.Item_Drops_Enabled", true);
		writeDefault("Fishing.Drops.Drop_Chance.Tier_1", 20);
		writeDefault("Fishing.Drops.Drop_Chance.Tier_2", 25);
		writeDefault("Fishing.Drops.Drop_Chance.Tier_3", 30);
		writeDefault("Fishing.Drops.Drop_Chance.Tier_4", 35);
		writeDefault("Fishing.Drops.Drop_Chance.Tier_5", 40);
		writeDefault("Fishing.Drops.Leather_Armor", true);
		writeDefault("Fishing.Drops.Iron_Armor", true);
		writeDefault("Fishing.Drops.Gold_Armor", true);
		writeDefault("Fishing.Drops.Diamond_Armor", true);
		writeDefault("Fishing.Drops.Wooden_Tools", true);
		writeDefault("Fishing.Drops.Stone_Tools", true);
		writeDefault("Fishing.Drops.Iron_Tools", true);
		writeDefault("Fishing.Drops.Gold_Tools", true);
		writeDefault("Fishing.Drops.Diamond_Tools", true);
		writeDefault("Fishing.Drops.Ender_Pearl", true);
		writeDefault("Fishing.Drops.Blaze_Rod", true);
		writeDefault("Fishing.Drops.Records", true);
		writeDefault("Fishing.Drops.Glowstone_Dust", true);
		writeDefault("Fishing.Drops.Diamonds", true);

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
		xpGainsMobSpawners = readBoolean("XP.Gains.Mobspawners.Enabled", false);

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
		mpumpkin = readInteger("Experience.Herbalism.Pumpkin", 20);
		mflower = readInteger("Experience.Herbalism.Flowers", 100);
		mmushroom = readInteger("Experience.Herbalism.Mushrooms", 150);
		mmelon = readInteger("Experience.Herbalism.Melon", 20);
		mnetherwart = readInteger("Experience.Herbalism.Nether_Wart", 50);
		mlilypad = readInteger("Experience.Herbalism.Lily_Pads", 100);
		mvines = readInteger("Experience.Herbalism.Vines", 10);

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
		nLeather = readString("Skills.Repair.String.Name", "Leather");

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