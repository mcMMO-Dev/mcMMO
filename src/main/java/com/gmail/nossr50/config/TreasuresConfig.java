package com.gmail.nossr50.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.datatypes.treasure.FishingTreasure;
import com.gmail.nossr50.datatypes.treasure.Treasure;

public class TreasuresConfig extends ConfigLoader{
    private static TreasuresConfig instance;
    public List<ExcavationTreasure> excavationFromDirt = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromGrass = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromSand = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromGravel = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromClay = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromMycel = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromSoulSand = new ArrayList<ExcavationTreasure>();
    public List<FishingTreasure> fishingRewardsTier1 = new ArrayList<FishingTreasure>();
    public List<FishingTreasure> fishingRewardsTier2 = new ArrayList<FishingTreasure>();
    public List<FishingTreasure> fishingRewardsTier3 = new ArrayList<FishingTreasure>();
    public List<FishingTreasure> fishingRewardsTier4 = new ArrayList<FishingTreasure>();
    public List<FishingTreasure> fishingRewardsTier5 = new ArrayList<FishingTreasure>();

    private TreasuresConfig() {
        super("treasures.yml");
        loadKeys();
    }

    public static TreasuresConfig getInstance() {
        if (instance == null) {
            instance = new TreasuresConfig();
        }

        return instance;
    }

    @Override
    protected void loadKeys() {
        Map<String, Treasure> treasures = new HashMap<String, Treasure>();
        ConfigurationSection treasureSection = config.getConfigurationSection("Treasures");
        Set<String> treasureConfigSet = treasureSection.getKeys(false);
        Iterator<String> iterator = treasureConfigSet.iterator();

        while (iterator.hasNext()) {
            String treasureName = iterator.next();

            // Validate all the things!
            List<String> reason = new ArrayList<String>();

            /*
             * ID, Amount, and Data
             */

            if (!config.contains("Treasures." + treasureName + ".ID")) {
                reason.add("Missing ID");
            }

            if (!config.contains("Treasures." + treasureName + ".Amount")) {
                reason.add("Missing Amount");
            }

            if (!config.contains("Treasures." + treasureName + ".Data")) {
                reason.add("Missing Data");
            }

            int id = config.getInt("Treasures." + treasureName + ".ID");
            int amount = config.getInt("Treasures." + treasureName + ".Amount");
            int data = config.getInt("Treasures." + treasureName + ".Data");

            if (Material.getMaterial(id) == null) {
                reason.add("Invalid id: " + id);
            }

            if (amount < 1) {
                reason.add("Invalid amount: " + amount);
            }

            if (data > 127 || data < -128) {
                reason.add("Invalid data: " + data);
            }

            /*
             * XP, Drop Chance, and Drop Level
             */

            if (!config.contains("Treasures." + treasureName + ".XP")) {
                reason.add("Missing XP");
            }

            if (!config.contains("Treasures." + treasureName + ".Drop_Chance")) {
                reason.add("Missing Drop_Chance");
            }

            if (!config.contains("Treasures." + treasureName + ".Drop_Level")) {
                reason.add("Missing Drop_Level");
            }

            int xp = config.getInt("Treasures." + treasureName + ".XP");
            Double dropChance = config.getDouble("Treasures." + treasureName + ".Drop_Chance");
            int dropLevel = config.getInt("Treasures." + treasureName + ".Drop_Level");

            if (xp < 0) {
                reason.add("Invalid xp: " + xp);
            }

            if (dropChance < 0) {
                reason.add("Invalid Drop_Chance: " + dropChance);
            }

            if (dropLevel < 0) {
                reason.add("Invalid Drop_Level: " + dropLevel);
            }

            /*
             * Drops From & Max Level
             */

            ItemStack item = new ItemStack(id, amount, (short) 0, (byte) data);

            if (config.getBoolean("Treasures." + treasureName + ".Drops_From.Fishing", false)) {
                if (config.getConfigurationSection("Treasures." + treasureName + ".Drops_From").getKeys(false).size() != 1) {
                    reason.add("Fishing drops cannot also be excavation drops");
                }

                if (!config.contains("Treasures." + treasureName + ".Max_Level")) {
                    reason.add("Missing Max_Level");
                }

                int maxLevel = config.getInt("Treasures." + treasureName + ".Max_Level");

                if (maxLevel < 0) {
                    reason.add("Invalid Max_Level: " + maxLevel);
                }

                if (noErrorsInTreasure(reason)) {
                    FishingTreasure fTreasure = new FishingTreasure(item, xp, dropChance, dropLevel, maxLevel);
                    treasures.put(treasureName, fTreasure);
                }
            }
            else {
                ExcavationTreasure eTreasure = new ExcavationTreasure(item, xp, dropChance, dropLevel);

                if (config.getBoolean("Treasures." + treasureName + ".Drops_From.Dirt", false)) {
                    eTreasure.setDropsFromDirt();
                }

                if (config.getBoolean("Treasures." + treasureName + ".Drops_From.Grass", false)) {
                    eTreasure.setDropsFromGrass();
                }

                if (config.getBoolean("Treasures." + treasureName + ".Drops_From.Sand", false)) {
                    eTreasure.setDropsFromSand();
                }

                if (config.getBoolean("Treasures." + treasureName + ".Drops_From.Gravel", false)) {
                    eTreasure.setDropsFromGravel();
                }

                if (config.getBoolean("Treasures." + treasureName + ".Drops_From.Clay", false)) {
                    eTreasure.setDropsFromClay();
                }

                if (config.getBoolean("Treasures." + treasureName + ".Drops_From.Mycelium", false)) {
                    eTreasure.setDropsFromMycel();
                }

                if (config.getBoolean("Treasures." + treasureName + ".Drops_From.Soul_Sand", false)) {
                    eTreasure.setDropsFromSoulSand();
                }

                if (config.getBoolean("Treasures." + treasureName + ".Drops_From.Fishing", false)) {
                    reason.add("Excavation drops cannot also be fishing drops");
                }

                if (noErrorsInTreasure(reason)) {
                    treasures.put(treasureName, eTreasure);
                }
            }
        }

        List<String> excavationTreasures = config.getStringList("Excavation.Treasure");
        List<String> fishingTreasures = config.getStringList("Fishing.Treasure");
//        Iterator<String> treasureIterator = treasures.keySet().iterator();
        Iterator<Entry<String,Treasure>> treasureIterator = treasures.entrySet().iterator();

        while (treasureIterator.hasNext()) {
            Entry<String,Treasure> nextEntry = treasureIterator.next();
            String treasureKey = nextEntry.getKey();
            Treasure treasure = nextEntry.getValue();

            if (treasure instanceof FishingTreasure) {
                if (!fishingTreasures.contains(treasureKey)) {
                    continue;
                }

                FishingTreasure fTreasure = (FishingTreasure) treasure;
                int dropLevel = fTreasure.getDropLevel();
                int maxLevel = fTreasure.getMaxLevel();

                if(dropLevel <= Config.getInstance().getFishingTierLevelsTier1() && maxLevel >= Config.getInstance().getFishingTierLevelsTier1()) {
                    fishingRewardsTier1.add(fTreasure);
                }

                if(dropLevel <= Config.getInstance().getFishingTierLevelsTier2() && maxLevel >= Config.getInstance().getFishingTierLevelsTier2()) {
                    fishingRewardsTier2.add(fTreasure);
                }

                if (dropLevel <= Config.getInstance().getFishingTierLevelsTier3() && maxLevel >= Config.getInstance().getFishingTierLevelsTier3()) {
                    fishingRewardsTier3.add(fTreasure);
                }

                if (dropLevel <= Config.getInstance().getFishingTierLevelsTier4() && maxLevel >= Config.getInstance().getFishingTierLevelsTier4()) {
                    fishingRewardsTier4.add(fTreasure);
                }

                if (dropLevel <= Config.getInstance().getFishingTierLevelsTier5() && maxLevel >= Config.getInstance().getFishingTierLevelsTier5()) {
                    fishingRewardsTier5.add(fTreasure);
                }

            }
            else if (treasure instanceof ExcavationTreasure) {
                if (!excavationTreasures.contains(treasureKey)) {
                    continue;
                }

                ExcavationTreasure eTreasure = (ExcavationTreasure) treasure;

                if (eTreasure.getDropsFromDirt()) {
                    excavationFromDirt.add(eTreasure);
                }

                if (eTreasure.getDropsFromGrass()) {
                    excavationFromGrass.add(eTreasure);
                }

                if (eTreasure.getDropsFromSand()) {
                    excavationFromSand.add(eTreasure);
                }

                if (eTreasure.getDropsFromGravel()) {
                    excavationFromGravel.add(eTreasure);
                }

                if (eTreasure.getDropsFromClay()) {
                    excavationFromClay.add(eTreasure);
                }

                if (eTreasure.getDropsFromMycel()) {
                    excavationFromMycel.add(eTreasure);
                }

                if (eTreasure.getDropsFromSoulSand()) {
                    excavationFromSoulSand.add(eTreasure);
                }
            }
        }
    }

    private boolean noErrorsInTreasure(List<String> issues) {
        if (issues.isEmpty()) {
            return true;
        }
        else {
            for (String issue : issues) {
                plugin.getLogger().warning(issue);
            }
            return false;
        }
    }
}
