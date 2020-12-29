package com.gmail.nossr50.config.treasure;

import com.gmail.nossr50.config.ConfigLoader;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.datatypes.treasure.HylianTreasure;
import com.gmail.nossr50.util.text.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TreasureConfig extends ConfigLoader {

    public static final String FILENAME = "treasures.yml";
    private static TreasureConfig instance;

    public HashMap<String, List<ExcavationTreasure>> excavationMap = new HashMap<>();
    public HashMap<String, List<HylianTreasure>>    hylianMap = new HashMap<>();

    private TreasureConfig() {
        super(FILENAME);
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
        List<String> reason = new ArrayList<>();

        return noErrorsInConfig(reason);
    }

    @Override
    protected void loadKeys() {
        if (config.getConfigurationSection("Treasures") != null) {
            backup();
            return;
        }

        loadTreasures("Excavation");
        loadTreasures("Hylian_Luck");

        for (EntityType entity : EntityType.values()) {
            if (entity.isAlive()) {
                loadTreasures("Shake." + entity.toString());
            }
        }
    }

    private void loadTreasures(String type) {
        boolean isExcavation = type.equals("Excavation");
        boolean isHylian = type.equals("Hylian_Luck");

        ConfigurationSection treasureSection = config.getConfigurationSection(type);

        if (treasureSection == null) {
            return;
        }

        for (String treasureName : treasureSection.getKeys(false)) {
            // Validate all the things!
            List<String> reason = new ArrayList<>();

            String[] treasureInfo = treasureName.split("[|]");
            String materialName = treasureInfo[0];

            /*
             * Material, Amount, and Data
             */
            Material material;
            material = Material.matchMaterial(materialName);

            int amount = config.getInt(type + "." + treasureName + ".Amount");
            short data = (treasureInfo.length == 2) ? Short.parseShort(treasureInfo[1]) : (short) config.getInt(type + "." + treasureName + ".Data");

            if (material == null) {
                reason.add("Invalid material: " + materialName);
            }

            if (amount <= 0) {
                amount = 1;
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
             * Itemstack
             */
            ItemStack item = null;

            if (materialName.contains("POTION")) {
                Material mat = Material.matchMaterial(materialName);
                if (mat == null) {
                    reason.add("Potion format for " + FILENAME + " has changed");
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
                        List<String> lore = new ArrayList<>();
                        for (String s : config.getStringList(type + "." + treasureName + ".Lore")) {
                            lore.add(ChatColor.translateAlternateColorCodes('&', s));
                        }
                        itemMeta.setLore(lore);
                    }
                    item.setItemMeta(itemMeta);
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
                    List<String> lore = new ArrayList<>();
                    for (String s : config.getStringList(type + "." + treasureName + ".Lore")) {
                        lore.add(ChatColor.translateAlternateColorCodes('&', s));
                    }
                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                }
            }

            if (noErrorsInConfig(reason)) {
                if (isExcavation) {
                    ExcavationTreasure excavationTreasure = new ExcavationTreasure(item, xp, dropChance, dropLevel);
                    List<String> dropList = config.getStringList(type + "." + treasureName + ".Drops_From");

                    for (String blockType : dropList) {
                        if (!excavationMap.containsKey(blockType))
                            excavationMap.put(blockType, new ArrayList<>());
                        excavationMap.get(blockType).add(excavationTreasure);
                    }
                } else if (isHylian) {
                    HylianTreasure hylianTreasure = new HylianTreasure(item, xp, dropChance, dropLevel);
                    List<String> dropList = config.getStringList(type + "." + treasureName + ".Drops_From");

                    for (String dropper : dropList) {
                        if (dropper.equals("Bushes")) {
                            AddHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.FERN), hylianTreasure);
                            AddHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.TALL_GRASS), hylianTreasure);
                            for (Material species : Tag.SAPLINGS.getValues())
                                AddHylianTreasure(StringUtils.getFriendlyConfigMaterialString(species), hylianTreasure);

                            AddHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.DEAD_BUSH), hylianTreasure);
                            continue;
                        }
                        if (dropper.equals("Flowers")) {
                            AddHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.POPPY), hylianTreasure);
                            AddHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.DANDELION), hylianTreasure);
                            AddHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.BLUE_ORCHID), hylianTreasure);
                            AddHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.ALLIUM), hylianTreasure);
                            AddHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.AZURE_BLUET), hylianTreasure);
                            AddHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.ORANGE_TULIP), hylianTreasure);
                            AddHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.PINK_TULIP), hylianTreasure);
                            AddHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.RED_TULIP), hylianTreasure);
                            AddHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.WHITE_TULIP), hylianTreasure);
                            continue;
                        }
                        if (dropper.equals("Pots")) {
                            for (Material species : Tag.FLOWER_POTS.getValues())
                                AddHylianTreasure(StringUtils.getFriendlyConfigMaterialString(species), hylianTreasure);
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
            hylianMap.put(dropper, new ArrayList<>());
        hylianMap.get(dropper).add(treasure);
    }
}
