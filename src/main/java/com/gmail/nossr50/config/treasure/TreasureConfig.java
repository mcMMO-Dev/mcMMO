package com.gmail.nossr50.config.treasure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import com.gmail.nossr50.config.ConfigLoader;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.datatypes.treasure.FishingTreasure;
import com.gmail.nossr50.datatypes.treasure.HylianTreasure;
import com.gmail.nossr50.datatypes.treasure.ShakeTreasure;
import com.gmail.nossr50.datatypes.treasure.Treasure;

public class TreasureConfig extends ConfigLoader {
    private static TreasureConfig instance;

    public List<ExcavationTreasure> excavationFromDirt     = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromGrass    = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromSand     = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromGravel   = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromClay     = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromMycel    = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromSoulSand = new ArrayList<ExcavationTreasure>();

    public List<HylianTreasure> hylianFromBushes  = new ArrayList<HylianTreasure>();
    public List<HylianTreasure> hylianFromFlowers = new ArrayList<HylianTreasure>();
    public List<HylianTreasure> hylianFromPots    = new ArrayList<HylianTreasure>();

    public List<ShakeTreasure> shakeFromBlaze       = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromCaveSpider  = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromSpider      = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromChicken     = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromCow         = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromCreeper     = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromEnderman    = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromGhast       = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromIronGolem   = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromMagmaCube   = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromMushroomCow = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromPig         = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromPigZombie   = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromSheep       = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromSkeleton    = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromSlime       = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromSnowman     = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromSquid       = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromWitch       = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromZombie      = new ArrayList<ShakeTreasure>();

    public List<FishingTreasure> fishingRewards = new ArrayList<FishingTreasure>();

    private TreasureConfig() {
        super("treasures.yml");
        loadKeys();
    }

    public static TreasureConfig getInstance() {
        if (instance == null) {
            instance = new TreasureConfig();
        }

        return instance;
    }

    @Override
    protected void loadKeys() {
        Map<String, Treasure> treasures = new HashMap<String, Treasure>();
        ConfigurationSection treasureSection = config.getConfigurationSection("Treasures");

        if (treasureSection == null) {
            return;
        }

        Set<String> treasureConfigSet = treasureSection.getKeys(false);

        for (String treasureName : treasureConfigSet) {

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

            if (id < 256 && (data > 127 || data < -128)) {
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
             * Potions
             */

            ItemStack item = null;

            if (config.contains("Treasures." + treasureName + ".Potion_Type")) {
                String potionType = config.getString("Treasures." + treasureName + ".Potion_Type");
                try {
                    item = new Potion(PotionType.valueOf(potionType.toUpperCase())).toItemStack(amount);
                }
                catch (IllegalArgumentException ex) {
                    reason.add("Invalid Potion_Type: " + potionType);
                }
            }
            else {
                item = (new MaterialData(id, (byte) data)).toItemStack(amount);
            }

            /*
             * Drops From & Max Level
             */

            if (config.getBoolean("Treasures." + treasureName + ".Drops_From.Fishing", false)) {
                if (config.getConfigurationSection("Treasures." + treasureName + ".Drops_From").getKeys(false).size() != 1) {
                    reason.add("This can only be a fishing drop.");
                }

                if (!config.contains("Treasures." + treasureName + ".Max_Level")) {
                    reason.add("Missing Max_Level");
                }

                int maxLevel = config.getInt("Treasures." + treasureName + ".Max_Level");

                if (noErrorsInTreasure(reason)) {
                    FishingTreasure fTreasure = new FishingTreasure(item, xp, dropChance, dropLevel, maxLevel);
                    treasures.put(treasureName, fTreasure);
                }
            }
            else if (config.getBoolean("Treasures." + treasureName + ".Drops_From.Shake", false)) {
                if (config.getConfigurationSection("Treasures." + treasureName + ".Drops_From").getKeys(false).size() != 1) {
                    reason.add("This can only be a shake drop.");
                }

                if (!config.contains("Treasures." + treasureName + ".Mob")) {
                    reason.add("Missing Mob");
                }

                String mobType = config.getString("Treasures." + treasureName + ".Mob");
                EntityType mob = null;

                try {
                     mob = EntityType.valueOf(mobType.toUpperCase().trim());
                }
                catch (IllegalArgumentException ex){
                    reason.add("Invalid Mob: " + mobType);
                }

                if (noErrorsInTreasure(reason)) {
                    ShakeTreasure sTreasure = new ShakeTreasure(item, xp, dropChance, dropLevel, mob);
                    treasures.put(treasureName, sTreasure);
                }
            }
            else {
                ExcavationTreasure eTreasure = new ExcavationTreasure(item, xp, dropChance, dropLevel);
                HylianTreasure hTreasure = new HylianTreasure(item, xp, dropChance, dropLevel);

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

                if (config.getBoolean("Treasures." + treasureName + ".Drops_From.Bushes", false)) {
                    hTreasure.setDropsFromBushes();
                }

                if (config.getBoolean("Treasures." + treasureName + ".Drops_From.Flowers", false)) {
                    hTreasure.setDropsFromFlowers();
                }

                if (config.getBoolean("Treasures." + treasureName + ".Drops_From.Pots", false)) {
                    hTreasure.setDropsFromPots();
                }

                if (config.getBoolean("Treasures." + treasureName + ".Drops_From.Fishing", false)) {
                    reason.add("This cannot also be a fishing drop.");
                }

                if (config.getBoolean("Treasures." + treasureName + ".Drops_From.Shake", false)) {
                    reason.add("This cannot also be a shake drop.");
                }

                if (noErrorsInTreasure(reason) && hTreasure.getDropsFrom() == (byte) 0x0) {
                    treasures.put(treasureName, eTreasure);
                }
                else if (noErrorsInTreasure(reason) && eTreasure.getDropsFrom() == (byte) 0x0) {
                    treasures.put(treasureName, hTreasure);
                }
            }
        }

        List<String> excavationTreasures = config.getStringList("Excavation.Treasure");
        List<String> fishingTreasures = config.getStringList("Fishing.Treasure");
        List<String> hylianTreasures = config.getStringList("Hylian_Luck.Treasure");
        List<String> shakeTreasures = config.getStringList("Shake.Treasure");

        for (Entry<String, Treasure> nextEntry : treasures.entrySet()) {
            String treasureKey = nextEntry.getKey();
            Treasure treasure = nextEntry.getValue();

            if (treasure instanceof FishingTreasure) {
                if (fishingTreasures == null || !fishingTreasures.contains(treasureKey)) {
                    continue;
                }

                fishingRewards.add((FishingTreasure) treasure);
            }
            else if (treasure instanceof ShakeTreasure) {
                if (shakeTreasures == null || !shakeTreasures.contains(treasureKey)) {
                    continue;
                }

                ShakeTreasure e = (ShakeTreasure) treasure;
                switch (e.getMob()) {
                    case BLAZE:
                        shakeFromBlaze.add(e);
                        break;

                    case CAVE_SPIDER:
                        shakeFromCaveSpider.add(e);
                        break;

                    case CHICKEN:
                        shakeFromChicken.add(e);
                        break;

                    case COW:
                        shakeFromCow.add(e);
                        break;

                    case CREEPER:
                        shakeFromCreeper.add(e);
                        break;

                    case ENDERMAN:
                        shakeFromEnderman.add(e);
                        break;

                    case GHAST:
                        shakeFromGhast.add(e);
                        break;

                    case IRON_GOLEM:
                        shakeFromIronGolem.add(e);
                        break;

                    case MAGMA_CUBE:
                        shakeFromMagmaCube.add(e);
                        break;

                    case MUSHROOM_COW:
                        shakeFromMushroomCow.add(e);
                        break;

                    case PIG:
                        shakeFromPig.add(e);
                        break;

                    case PIG_ZOMBIE:
                        shakeFromPigZombie.add(e);
                        break;

                    case SHEEP:
                        shakeFromSheep.add(e);
                        break;

                    case SKELETON:
                        shakeFromSkeleton.add(e);
                        break;

                    case SLIME:
                        shakeFromSlime.add(e);
                        break;

                    case SPIDER:
                        shakeFromSpider.add(e);
                        break;

                    case SNOWMAN:
                        shakeFromSnowman.add(e);
                        break;

                    case SQUID:
                        shakeFromSquid.add(e);
                        break;

                    case WITCH:
                        shakeFromWitch.add(e);
                        break;

                    case ZOMBIE:
                        shakeFromZombie.add(e);
                        break;

                    default:
                        break;
                }
            }
            else if (treasure instanceof HylianTreasure) {
                if (hylianTreasures == null || !hylianTreasures.contains(treasureKey)) {
                    continue;
                }

                HylianTreasure hTreasure = (HylianTreasure) treasure;

                if (hTreasure.getDropsFromBushes()) {
                    hylianFromBushes.add(hTreasure);
                }

                if (hTreasure.getDropsFromFlowers()) {
                    hylianFromFlowers.add(hTreasure);
                }

                if (hTreasure.getDropsFromPots()) {
                    hylianFromPots.add(hTreasure);
                }
            }
            else if (treasure instanceof ExcavationTreasure) {
                if (excavationTreasures == null || !excavationTreasures.contains(treasureKey)) {
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

        for (String issue : issues) {
            plugin.getLogger().warning(issue);
        }
        return false;
    }
}
