package com.gmail.nossr50.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.datatypes.treasure.FishingTreasure;
import com.gmail.nossr50.datatypes.treasure.Treasure;

public class LoadTreasures {

	public static List<ExcavationTreasure> excavationFromDirt = new ArrayList<ExcavationTreasure>();
	public static List<ExcavationTreasure> excavationFromGrass = new ArrayList<ExcavationTreasure>();
	public static List<ExcavationTreasure> excavationFromSand = new ArrayList<ExcavationTreasure>();
	public static List<ExcavationTreasure> excavationFromGravel = new ArrayList<ExcavationTreasure>();
	public static List<ExcavationTreasure> excavationFromClay = new ArrayList<ExcavationTreasure>();
	public static List<ExcavationTreasure> excavationFromMycel = new ArrayList<ExcavationTreasure>();
	public static List<ExcavationTreasure> excavationFromSoulSand = new ArrayList<ExcavationTreasure>();
	public static List<FishingTreasure> fishingRewardsTier1 = new ArrayList<FishingTreasure>();
	public static List<FishingTreasure> fishingRewardsTier2 = new ArrayList<FishingTreasure>();
	public static List<FishingTreasure> fishingRewardsTier3 = new ArrayList<FishingTreasure>();
	public static List<FishingTreasure> fishingRewardsTier4 = new ArrayList<FishingTreasure>();
	public static List<FishingTreasure> fishingRewardsTier5 = new ArrayList<FishingTreasure>();
	
	protected static File configFile;
	protected static File dataFolder;
	protected final mcMMO plugin;
	protected static FileConfiguration config;

	public LoadTreasures(mcMMO plugin) {
		this.plugin = plugin;
		dataFolder = plugin.getDataFolder();
		configFile = new File(dataFolder, File.separator + "treasures.yml");
		config = plugin.getTreasuresConfig();
	}

	public void load() {
		// If not exist, copy from the jar
		if (!configFile.exists()) {
			dataFolder.mkdir();
			plugin.saveTreasuresConfig();
		}
		addDefaults();
		loadKeys();
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
	
	private Boolean readBoolean(String root, Boolean def) {
		Boolean result = config.getBoolean(root, def);
		return result;
	}
	
	private void loadKeys()
	{
		plugin.getLogger().info("Loading mcMMO treasures.yml File...");
		
		// Load treasures
		Map<String, Treasure> treasures = new HashMap<String, Treasure>();

		ConfigurationSection treasureSection = config.getConfigurationSection("Treasures");
        Set<String> treasureConfigSet = treasureSection.getKeys(false);
        Iterator<String> iterator = treasureConfigSet.iterator();
		while(iterator.hasNext())
		{
			String treasureName = iterator.next();

			// Validate all the things!
			List<String> reason = new ArrayList<String>();

			if(!config.contains("Treasures." + treasureName + ".ID")) reason.add("Missing ID");
			if(!config.contains("Treasures." + treasureName + ".Amount")) reason.add("Missing Amount");
			if(!config.contains("Treasures." + treasureName + ".Data")) reason.add("Missing Data");

			int id = config.getInt("Treasures." + treasureName + ".ID");
			int amount = config.getInt("Treasures." + treasureName + ".Amount");
			int data = config.getInt("Treasures." + treasureName + ".Data");

			if(Material.getMaterial(id) == null) reason.add("Invlid id: " + id);
			if(amount < 1) reason.add("Invalid amount: " + amount);
			if(data > 127 || data < -128) reason.add("Invalid data: " + data);

			if(!config.contains("Treasures." + treasureName + ".XP")) reason.add("Missing XP");
			if(!config.contains("Treasures." + treasureName + ".Drop_Chance")) reason.add("Missing Drop_Chance");
			if(!config.contains("Treasures." + treasureName + ".Drop_Level")) reason.add("Missing Drop_Level");

			int xp = config.getInt("Treasures." + treasureName + ".XP");
			Double dropChance = config.getDouble("Treasures." + treasureName + ".Drop_Chance");
			int dropLevel = config.getInt("Treasures." + treasureName + ".Drop_Level");

			if(xp < 0) reason.add("Invalid xp: " + xp);
			if(dropChance < 0) reason.add("Invalid Drop_Chance: " + dropChance);
			if(dropLevel < 0) reason.add("Invalid Drop_Level: " + dropLevel);

			ItemStack item = new ItemStack(id, amount, (byte) 0, (byte) data);

			if(readBoolean("Treasures." + treasureName + ".Drops_From.Fishing", false)) {
				if(config.getConfigurationSection("Treasures." + treasureName + ".Drops_From").getKeys(false).size() != 1)
					reason.add("Fishing drops cannot also be excavation drops");

				if(!config.contains("Treasures." + treasureName + ".Max_Level")) reason.add("Missing Max_Level");
				int maxLevel = config.getInt("Treasures." + treasureName + ".Max_Level");

				if(maxLevel < 0) reason.add("Invalid Max_Level: " + maxLevel);

				if(noErrorsInTreasure(reason)) {
					FishingTreasure fTreasure = new FishingTreasure(item, xp, dropChance, dropLevel, maxLevel);
					treasures.put(treasureName, fTreasure);
				}
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

				if(readBoolean("Treasures." + treasureName + ".Drops_From.Fishing", false)) {
					reason.add("Excavation drops cannot also be fishing drops");
				}

				if(noErrorsInTreasure(reason)) {
					treasures.put(treasureName, eTreasure);
				}
			}
		}

		List<String> excavationTreasures = config.getStringList("Excavation.Treasure");
		List<String> fishingTreasures = config.getStringList("Fishing.Treasure");

		Iterator<String> treasureIterator = treasures.keySet().iterator();
		while(treasureIterator.hasNext()) {
			String treasureKey = treasureIterator.next();
			Treasure treasure = treasures.get(treasureKey);

			if(treasure instanceof FishingTreasure) {
				if(!fishingTreasures.contains(treasureKey)) continue;
				
				FishingTreasure fTreasure = (FishingTreasure) treasure;
				int dropLevel = fTreasure.getDropLevel();
				int maxLevel = fTreasure.getMaxLevel();
				
				if(dropLevel <= LoadProperties.fishingTier1 && maxLevel >= LoadProperties.fishingTier1)
					fishingRewardsTier1.add(fTreasure);
				if(dropLevel <= LoadProperties.fishingTier2 && maxLevel >= LoadProperties.fishingTier2)
					fishingRewardsTier2.add(fTreasure);
				if(dropLevel <= LoadProperties.fishingTier3 && maxLevel >= LoadProperties.fishingTier3)
					fishingRewardsTier3.add(fTreasure);
				if(dropLevel <= LoadProperties.fishingTier4 && maxLevel >= LoadProperties.fishingTier4)
					fishingRewardsTier4.add(fTreasure);
				if(dropLevel <= LoadProperties.fishingTier5 && maxLevel >= LoadProperties.fishingTier5)
					fishingRewardsTier5.add(fTreasure);
				
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

	private boolean noErrorsInTreasure(List<String> issues) {
		if(issues.isEmpty()) return true;

		for(String issue : issues) {
		    Bukkit.getLogger().warning(issue);
		}

		return false;
	}
	
}
