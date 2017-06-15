package com.gmail.nossr50.config.treasure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.Dye;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import com.gmail.nossr50.config.ConfigLoader;
import com.gmail.nossr50.datatypes.treasure.EnchantmentTreasure;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.datatypes.treasure.FishingTreasure;
import com.gmail.nossr50.datatypes.treasure.HylianTreasure;
import com.gmail.nossr50.datatypes.treasure.Rarity;
import com.gmail.nossr50.datatypes.treasure.ShakeTreasure;
import com.gmail.nossr50.util.EnchantmentUtils;
import com.gmail.nossr50.util.StringUtils;

public class TreasureConfig extends ConfigLoader {

    private static TreasureConfig instance;

    public HashMap<String, List<ExcavationTreasure>> excavationMap = new HashMap<String, List<ExcavationTreasure>>();

    public HashMap<EntityType, List<ShakeTreasure>> shakeMap  = new HashMap<EntityType, List<ShakeTreasure>>();
    public HashMap<String, List<HylianTreasure>>    hylianMap = new HashMap<String, List<HylianTreasure>>();

    public HashMap<Rarity, List<FishingTreasure>>     fishingRewards      = new HashMap<Rarity, List<FishingTreasure>>();
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

        loadTreasures("Fishing");
        loadTreasures("Excavation");
        loadTreasures("Hylian_Luck");
        loadEnchantments();

        for (EntityType entity : EntityType.values()) {
            if (entity.isAlive()) {
                loadTreasures("Shake." + entity.toString());
            }
        }
    }

    private void loadTreasures(String type) {
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

            String[] treasureInfo = treasureName.split("[|]");
            String materialName = treasureInfo[0];

            /*
             * Material, Amount, and Data
             */
            Material material;

            if (materialName.contains("INK_SACK")) {
                material = Material.INK_SACK;
            } else if (materialName.contains("COAL")) {
                material = Material.COAL;
            } else if (materialName.contains("INVENTORY")) {
                // Use magic material BED_BLOCK to know that we're grabbing something from the inventory and not a normal treasure
                if (!shakeMap.containsKey(EntityType.PLAYER))
                    shakeMap.put(EntityType.PLAYER, new ArrayList<ShakeTreasure>());
                shakeMap.get(EntityType.PLAYER).add(new ShakeTreasure(new ItemStack(Material.BED_BLOCK, 1, (byte) 0), 1, getInventoryStealDropChance(), getInventoryStealDropLevel()));
                continue;
            } else {
                material = Material.matchMaterial(materialName);
            }

            int amount = config.getInt(type + "." + treasureName + ".Amount");
            short data = (treasureInfo.length == 2) ? Short.parseShort(treasureInfo[1]) : (short) config.getInt(type + "." + treasureName + ".Data");

            if (material == null) {
                reason.add("Invalid material: " + materialName);
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

            if (materialName.contains("POTION")) {
                Material mat = Material.matchMaterial(materialName);
                if (mat == null) {
                    reason.add("Potion format for Treasures.yml has changed");
                } else {
                    item = new ItemStack(mat, amount, data);
                    PotionMeta itemMeta = (PotionMeta) item.getItemMeta();

                    PotionType potionType = null;
                    try {
                        potionType = PotionType.valueOf(config.getString(type + "." + treasureName + ".PotionData.PotionType", "WATER"));
                    } catch (IllegalArgumentException ex) {
                        reason.add("Invalid Potion_Type: " + config.getString(type + "." + treasureName + ".PotionData.PotionType", "WATER"));
                    }
                    boolean extended = config.getBoolean(type + "." + treasureName + ".PotionData.Extended", false);
                    boolean upgraded = config.getBoolean(type + "." + treasureName + ".PotionData.Upgraded", false);
                    itemMeta.setBasePotionData(new PotionData(potionType, extended, upgraded));

                    if (config.contains(type + "." + treasureName + ".Custom_Name")) {
                        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString(type + "." + treasureName + ".Custom_Name")));
                    }

                    if (config.contains(type + "." + treasureName + ".Lore")) {
                        List<String> lore = new ArrayList<String>();
                        for (String s : config.getStringList(type + "." + treasureName + ".Lore")) {
                            lore.add(ChatColor.translateAlternateColorCodes('&', s));
                        }
                        itemMeta.setLore(lore);
                    }
                    item.setItemMeta(itemMeta);
                }
            } else if (materialName.contains("INK_SACK")) {
                String color = materialName.substring(9);

                try {
                    Dye dye = new Dye();
                    dye.setColor(DyeColor.valueOf(color.toUpperCase().trim()));

                    item = dye.toItemStack(amount);

                    if (config.contains(type + "." + treasureName + ".Custom_Name")) {
                        ItemMeta itemMeta = item.getItemMeta();
                        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString(type + "." + treasureName + ".Custom_Name")));
                        item.setItemMeta(itemMeta);
                    }

                    if (config.contains(type + "." + treasureName + ".Lore")) {
                        ItemMeta itemMeta = item.getItemMeta();
                        List<String> lore = new ArrayList<String>();
                        for (String s : config.getStringList(type + "." + treasureName + ".Lore")) {
                            lore.add(ChatColor.translateAlternateColorCodes('&', s));
                        }
                        itemMeta.setLore(lore);
                        item.setItemMeta(itemMeta);
                    }
                } catch (IllegalArgumentException ex) {
                    reason.add("Invalid Dye_Color: " + color);
                }
            } else if (material != null) {
                item = new ItemStack(material, amount, data);

                if (config.contains(type + "." + treasureName + ".Custom_Name")) {
                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString(type + "." + treasureName + ".Custom_Name")));
                    item.setItemMeta(itemMeta);
                }

                if (config.contains(type + "." + treasureName + ".Lore")) {
                    ItemMeta itemMeta = item.getItemMeta();
                    List<String> lore = new ArrayList<String>();
                    for (String s : config.getStringList(type + "." + treasureName + ".Lore")) {
                        lore.add(ChatColor.translateAlternateColorCodes('&', s));
                    }
                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                }
            }

            if (noErrorsInConfig(reason)) {
                if (isFishing) {
                    fishingRewards.get(rarity).add(new FishingTreasure(item, xp));
                } else if (isShake) {
                    ShakeTreasure shakeTreasure = new ShakeTreasure(item, xp, dropChance, dropLevel);

                    EntityType entityType = EntityType.valueOf(type.substring(6));
                    if (!shakeMap.containsKey(entityType))
                        shakeMap.put(entityType, new ArrayList<ShakeTreasure>());
                    shakeMap.get(entityType).add(shakeTreasure);
                } else if (isExcavation) {
                    ExcavationTreasure excavationTreasure = new ExcavationTreasure(item, xp, dropChance, dropLevel);
                    List<String> dropList = config.getStringList(type + "." + treasureName + ".Drops_From");

                    for (String blockType : dropList) {
                        if (!excavationMap.containsKey(blockType))
                            excavationMap.put(blockType, new ArrayList<ExcavationTreasure>());
                        excavationMap.get(blockType).add(excavationTreasure);
                    }
                } else if (isHylian) {
                    HylianTreasure hylianTreasure = new HylianTreasure(item, xp, dropChance, dropLevel);
                    List<String> dropList = config.getStringList(type + "." + treasureName + ".Drops_From");

                    for (String dropper : dropList) {
                        if (dropper.equals("Bushes")) {
                            AddHylianTreasure("Small_Fern", hylianTreasure);
                            AddHylianTreasure("Small_Grass", hylianTreasure);
                            for (TreeSpecies species : TreeSpecies.values()) {
                                AddHylianTreasure(StringUtils.getPrettyTreeSpeciesString(species) + "_Sapling", hylianTreasure);
                            }

                            AddHylianTreasure(StringUtils.getPrettyItemString(Material.DEAD_BUSH), hylianTreasure);
                            continue;
                        }
                        if (dropper.equals("Flowers")) {
                            for (int i = 0; i < 9; i++) {
                                AddHylianTreasure(StringUtils.getFriendlyConfigMaterialDataString(new MaterialData(Material.RED_ROSE, (byte) i)), hylianTreasure);
                            }
                            AddHylianTreasure(StringUtils.getPrettyItemString(Material.YELLOW_FLOWER), hylianTreasure);
                            continue;
                        }
                        if (dropper.equals("Pots")) {
                            for (int i = 0; i < 14; i++) {
                                AddHylianTreasure(StringUtils.getFriendlyConfigMaterialDataString(new MaterialData(Material.FLOWER_POT, (byte) i)), hylianTreasure);
                            }
                            continue;
                        }
                        AddHylianTreasure(dropper, hylianTreasure);
                    }
                }
            }
        }
    }

    private void AddHylianTreasure(String dropper, HylianTreasure treasure) {
        if (!hylianMap.containsKey(dropper))
            hylianMap.put(dropper, new ArrayList<HylianTreasure>());
        hylianMap.get(dropper).add(treasure);
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

    public boolean getInventoryStealEnabled() {
        return config.contains("Shake.PLAYER.INVENTORY");
    }

    public boolean getInventoryStealStacks() {
        return config.getBoolean("Shake.PLAYER.INVENTORY.Whole_Stacks");
    }

    public double getInventoryStealDropChance() {
        return config.getDouble("Shake.PLAYER.INVENTORY.Drop_Chance");
    }

    public int getInventoryStealDropLevel() {
        return config.getInt("Shake.PLAYER.INVENTORY.Drop_Level");
    }

    public double getItemDropRate(int tier, Rarity rarity) {
        return config.getDouble("Item_Drop_Rates.Tier_" + tier + "." + rarity.toString());
    }

    public double getEnchantmentDropRate(int tier, Rarity rarity) {
        return config.getDouble("Enchantment_Drop_Rates.Tier_" + tier + "." + rarity.toString());
    }
}
