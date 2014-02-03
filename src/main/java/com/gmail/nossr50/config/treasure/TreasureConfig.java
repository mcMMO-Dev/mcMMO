package com.gmail.nossr50.config.treasure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import com.gmail.nossr50.config.ConfigLoader;
import com.gmail.nossr50.datatypes.treasure.EnchantmentTreasure;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.datatypes.treasure.FishingTreasure;
import com.gmail.nossr50.datatypes.treasure.HylianTreasure;
import com.gmail.nossr50.datatypes.treasure.Rarity;
import com.gmail.nossr50.datatypes.treasure.ShakeTreasure;
import com.gmail.nossr50.util.EnchantmentUtils;

public class TreasureConfig extends ConfigLoader {
    private static TreasureConfig instance;

    public List<ExcavationTreasure> excavationFromDirt      = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromGrass     = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromSand      = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromGravel    = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromClay      = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromMycel     = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromSoulSand  = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromSnow      = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromRedSand   = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromPodzol    = new ArrayList<ExcavationTreasure>();

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
    public List<ShakeTreasure> shakeFromHorse       = new ArrayList<ShakeTreasure>();
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

    public HashMap<Rarity, List<FishingTreasure>> fishingRewards = new HashMap<Rarity, List<FishingTreasure>>();
    public HashMap<Rarity, List<EnchantmentTreasure>> fishingEnchantments = new HashMap<Rarity, List<EnchantmentTreasure>>();

    private TreasureConfig() {
        super("treasures.yml");
        loadKeys();
        validate();
    }

    public static TreasureConfig getInstance() {
        if (instance == null) {
            instance = new TreasureConfig();
        }

        return instance;
    }

    @Override
    protected boolean validateKeys() {
        // Validate all the settings!
        List<String> reason = new ArrayList<String>();

        for (String tier : config.getConfigurationSection("Enchantment_Drop_Rates").getKeys(false)) {
            double totalEnchantDropRate = 0;
            double totalItemDropRate = 0;

            for (Rarity rarity : Rarity.values()) {
                double enchantDropRate = config.getDouble("Enchantment_Drop_Rates." + tier + "." + rarity.toString());
                double itemDropRate = config.getDouble("Item_Drop_Rates." + tier + "." + rarity.toString());

                if ((enchantDropRate < 0.0 || enchantDropRate > 100.0) && rarity != Rarity.TRAP && rarity != Rarity.RECORD) {
                    reason.add("The enchant drop rate for " + tier + " items that are " + rarity.toString() + "should be between 0.0 and 100.0!");
                }

                if (itemDropRate < 0.0 || itemDropRate > 100.0) {
                    reason.add("The item drop rate for " + tier + " items that are " + rarity.toString() + "should be between 0.0 and 100.0!");
                }

                totalEnchantDropRate += enchantDropRate;
                totalItemDropRate += itemDropRate;
            }

            if (totalEnchantDropRate < 0 || totalEnchantDropRate > 100.0) {
                reason.add("The total enchant drop rate for " + tier + " should be between 0.0 and 100.0!");
            }

            if (totalItemDropRate < 0 || totalItemDropRate > 100.0) {
                reason.add("The total item drop rate for " + tier + " should be between 0.0 and 100.0!");
            }
        }

        return noErrorsInConfig(reason);
    }

    @Override
    protected void loadKeys() {
        if (config.getConfigurationSection("Treasures") != null) {
            backup();
            return;
        }

        loadTreaures("Fishing");
        loadTreaures("Excavation");
        loadTreaures("Hylian_Luck");
        loadEnchantments();

        for (EntityType entity : EntityType.values()) {
            if (entity.isAlive()) {
                loadTreaures("Shake." + entity.toString());
            }
        }
    }

    private void loadTreaures(String type) {
        boolean isFishing = type.equals("Fishing");
        boolean isShake = type.contains("Shake");
        boolean isExcavation = type.equals("Excavation");
        boolean isHylian = type.equals("Hylian_Luck");

        ConfigurationSection treasureSection = config.getConfigurationSection(type);

        if (treasureSection == null) {
            return;
        }

        // Initialize fishing HashMap
        for (Rarity rarity : Rarity.values()) {
            if (!fishingRewards.containsKey(rarity)) {
                fishingRewards.put(rarity, (new ArrayList<FishingTreasure>()));
            }
        }

        for (String treasureName : treasureSection.getKeys(false)) {
            // Validate all the things!
            List<String> reason = new ArrayList<String>();

            /*
             * Material, Amount, and Data
             */
            Material material;

            if (treasureName.contains("POTION")) {
                material = Material.POTION;
            }
            else if (treasureName.contains("INK_SACK")) {
                material = Material.INK_SACK;
            }
            else {
                material = Material.matchMaterial(treasureName);
            }

            int amount = config.getInt(type + "." + treasureName + ".Amount");
            int data = config.getInt(type + "." + treasureName + ".Data");

            if (material == null) {
                reason.add("Invalid material: " + treasureName);
            }

            if (amount <= 0) {
                reason.add("Amount of " + treasureName + " must be greater than 0! " + amount);
            }

            if (material != null && material.isBlock() && (data > 127 || data < -128)) {
                reason.add("Data of " + treasureName + " is invalid! " + data);
            }

            /*
             * XP, Drop Chance, and Drop Level
             */

            int xp = config.getInt(type + "." + treasureName + ".XP");
            double dropChance = config.getDouble(type + "." + treasureName + ".Drop_Chance");
            int dropLevel = config.getInt(type + "." + treasureName + ".Drop_Level");

            if (xp < 0) {
                reason.add(treasureName + " has an invalid XP value: " + xp);
            }

            if (dropChance < 0.0D) {
                reason.add(treasureName + " has an invalid Drop_Chance: " + dropChance);
            }

            if (dropLevel < 0) {
                reason.add(treasureName + " has an invalid Drop_Level: " + dropLevel);
            }

            /*
             * Specific Types
             */
            Rarity rarity = null;

            if (isFishing) {
                rarity = Rarity.getRarity(config.getString(type + "." + treasureName + ".Rarity"));

                if (rarity == null) {
                    reason.add("Invalid Rarity for item: " + treasureName);
                }
            }

            /*
             * Itemstack
             */
            ItemStack item = null;

            if (treasureName.contains("POTION")) {
                String potionType = treasureName.substring(7);

                try {
                    item = new Potion(PotionType.valueOf(potionType.toUpperCase().trim())).toItemStack(amount);
                }
                catch (IllegalArgumentException ex) {
                    reason.add("Invalid Potion_Type: " + potionType);
                }
            }
            else if (treasureName.contains("INK_SACK")) {
                String color = treasureName.substring(9);

                try {
                    Dye dye = new Dye();
                    dye.setColor(DyeColor.valueOf(color.toUpperCase().trim()));

                    item = dye.toItemStack(amount);
                }
                catch (IllegalArgumentException ex) {
                    reason.add("Invalid Dye_Color: " + color);
                }
            }
            else if (material != null) {
                item = new ItemStack(material, amount, (short) data);

                if (config.contains(type + "." + treasureName + ".Custom_Name")) {
                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.setDisplayName(config.getString(type + "." + treasureName + "Custom_Name"));
                    item.setItemMeta(itemMeta);
                }

                if (config.contains(type + "." + treasureName + ".Lore")) {
                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.setLore(config.getStringList(type + "." + treasureName + "Custom_Name"));
                    item.setItemMeta(itemMeta);
                }
            }

            if (noErrorsInConfig(reason)) {
                if (isFishing) {
                    fishingRewards.get(rarity).add(new FishingTreasure(item, xp));
                }
                else if (isShake) {
                    ShakeTreasure shakeTreasure = new ShakeTreasure(item, xp, dropChance, dropLevel);

                    if (type.equals("Shake.BLAZE")) {
                        shakeFromBlaze.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.CAVE_SPIDER")) {
                        shakeFromCaveSpider.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.CHICKEN")) {
                        shakeFromChicken.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.COW")) {
                        shakeFromCow.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.CREEPER")) {
                        shakeFromCreeper.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.ENDERMAN")) {
                        shakeFromEnderman.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.GHAST")) {
                        shakeFromGhast.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.HORSE")) {
                        shakeFromHorse.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.IRON_GOLEM")) {
                        shakeFromIronGolem.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.MAGMA_CUBE")) {
                        shakeFromMagmaCube.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.MUSHROOM_COW")) {
                        shakeFromMushroomCow.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.PIG")) {
                        shakeFromPig.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.PIG_ZOMBIE")) {
                        shakeFromPigZombie.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.SHEEP")) {
                        shakeFromSheep.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.SKELETON")) {
                        shakeFromSkeleton.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.SLIME")) {
                        shakeFromSlime.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.SPIDER")) {
                        shakeFromSpider.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.SNOWMAN")) {
                        shakeFromSnowman.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.SQUID")) {
                        shakeFromSquid.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.WITCH")) {
                        shakeFromWitch.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.ZOMBIE")) {
                        shakeFromZombie.add(shakeTreasure);
                    }
                }
                else if (isExcavation) {
                    ExcavationTreasure excavationTreasure = new ExcavationTreasure(item, xp, dropChance, dropLevel);
                    List<String> dropList = config.getStringList(type + "." + treasureName + ".Drops_From");

                    if (dropList.contains("Dirt")) {
                        excavationFromDirt.add(excavationTreasure);
                    }

                    if (dropList.contains("Grass")) {
                        excavationFromGrass.add(excavationTreasure);
                    }

                    if (dropList.contains("Sand")) {
                        excavationFromSand.add(excavationTreasure);
                    }

                    if (dropList.contains("Gravel")) {
                        excavationFromGravel.add(excavationTreasure);
                    }

                    if (dropList.contains("Clay")) {
                        excavationFromClay.add(excavationTreasure);
                    }

                    if (dropList.contains("Mycelium")) {
                        excavationFromMycel.add(excavationTreasure);
                    }

                    if (dropList.contains("Soul_Sand")) {
                        excavationFromSoulSand.add(excavationTreasure);
                    }

                    if (dropList.contains("Snow")) {
                        excavationFromSnow.add(excavationTreasure);
                    }

                    if (dropList.contains("Red_Sand")) {
                        excavationFromRedSand.add(excavationTreasure);
                    }

                    if (dropList.contains("Podzol")) {
                        excavationFromPodzol.add(excavationTreasure);
                    }
                }
                else if (isHylian) {
                    HylianTreasure hylianTreasure = new HylianTreasure(item, xp, dropChance, dropLevel);
                    List<String> dropList = config.getStringList(type + "." + treasureName + ".Drops_From");

                    if (dropList.contains("Bushes")) {
                        hylianFromBushes.add(hylianTreasure);
                    }

                    if (dropList.contains("Flowers")) {
                        hylianFromFlowers.add(hylianTreasure);
                    }

                    if (dropList.contains("Pots")) {
                        hylianFromPots.add(hylianTreasure);
                    }
                }
            }
        }
    }

    private void loadEnchantments() {
        for (Rarity rarity : Rarity.values()) {
            if (rarity == Rarity.TRAP || rarity == Rarity.RECORD) {
                continue;
            }

            if (!fishingEnchantments.containsKey(rarity)) {
                fishingEnchantments.put(rarity, (new ArrayList<EnchantmentTreasure>()));
            }

            ConfigurationSection enchantmentSection = config.getConfigurationSection("Enchantments_Rarity." + rarity.toString());

            if (enchantmentSection == null) {
                return;
            }

            for (String enchantmentName : enchantmentSection.getKeys(false)) {
                int level = config.getInt("Enchantments_Rarity." + rarity.toString() + "." + enchantmentName);
                Enchantment enchantment = EnchantmentUtils.getByName(enchantmentName);

                if (enchantment == null) {
                    plugin.getLogger().warning("Skipping invalid enchantment in treasures.yml: " + enchantmentName);
                    continue;
                }

                fishingEnchantments.get(rarity).add(new EnchantmentTreasure(enchantment, level));
            }
        }
    }

    public double getItemDropRate(int tier, Rarity rarity) { return config.getDouble("Item_Drop_Rates.Tier_" + tier + "." + rarity.toString()); }
    public double getEnchantmentDropRate(int tier, Rarity rarity) { return config.getDouble("Enchantment_Drop_Rates.Tier_" + tier + "." + rarity.toString()); }
}
