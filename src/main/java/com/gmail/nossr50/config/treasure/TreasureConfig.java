package com.gmail.nossr50.config.treasure;

import com.gmail.nossr50.config.BukkitConfig;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.datatypes.treasure.HylianTreasure;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.text.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TreasureConfig extends BukkitConfig {

    public static final String FILENAME = "treasures.yml";
    public static final String LEVEL_REQUIREMENT_RETRO_MODE = ".Level_Requirement.Retro_Mode";
    public static final String LEVEL_REQUIREMENT_STANDARD_MODE = ".Level_Requirement.Standard_Mode";
    public static final String WRONG_KEY_VALUE_STANDARD = ".Drop_Level.Standard_Mode";
    public static final String WRONG_KEY_VALUE_RETRO = ".Drop_Level.Retro_Mode";
    public static final String LEGACY_DROP_LEVEL = ".Drop_Level";
    public static final String WRONG_KEY_ROOT = ".Drop_Level";
    private static TreasureConfig instance;

    public HashMap<String, List<ExcavationTreasure>> excavationMap = new HashMap<>();
    public HashMap<String, List<HylianTreasure>> hylianMap = new HashMap<>();

    private TreasureConfig() {
        super(FILENAME);
        loadKeys();
    }

    public static TreasureConfig getInstance() {
        if (instance == null) {
            instance = new TreasureConfig();
        }

        return instance;
    }

    @Override
    protected void validateConfigKeys() {
        //TODO: Rewrite legacy validation code
    }

    @Override
    protected void loadKeys() {
        if (config.getConfigurationSection("Treasures") != null) {
            backup();
            return;
        }

        loadTreasures("Excavation");
        loadTreasures("Hylian_Luck");
    }

    private void loadTreasures(String type) {
        boolean shouldWeUpdateFile = false;
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
                mcMMO.p.getLogger().warning("Invalid material: " + materialName);
            }

            if (amount <= 0) {
                amount = 1;
            }

            if (material != null && material.isBlock() && (data > 127 || data < -128)) {
                mcMMO.p.getLogger().warning("Data of " + treasureName + " is invalid! " + data);
            }

            /*
             * XP, Drop Chance, and Drop Level
             */

            int xp = config.getInt(type + "." + treasureName + ".XP");
            double dropChance = config.getDouble(type + "." + treasureName + ".Drop_Chance");
            DropLevelKeyConversionType conversionType;

            //Check for legacy drop level values and convert
            if (getWrongKeyValue(type, treasureName, DropLevelKeyConversionType.LEGACY) != -1) {
                //Legacy Drop level, needs to be converted
                shouldWeUpdateFile = processAutomaticKeyConversion(type, shouldWeUpdateFile, treasureName, DropLevelKeyConversionType.LEGACY);
            }

            //Check for a bad key that was accidentally shipped out to some users
            if (getWrongKeyValue(type, treasureName, DropLevelKeyConversionType.WRONG_KEY_STANDARD) != -1) {
                //Partially converted to the new system, I had a dyslexic moment so some configs have this
                shouldWeUpdateFile = processAutomaticKeyConversion(type, shouldWeUpdateFile, treasureName, DropLevelKeyConversionType.WRONG_KEY_STANDARD);
            }

            //Check for a bad key that was accidentally shipped out to some users
            if (getWrongKeyValue(type, treasureName, DropLevelKeyConversionType.WRONG_KEY_RETRO) != -1) {
                //Partially converted to the new system, I had a dyslexic moment so some configs have this
                shouldWeUpdateFile = processAutomaticKeyConversion(type, shouldWeUpdateFile, treasureName, DropLevelKeyConversionType.WRONG_KEY_RETRO);
            }

            int dropLevel = -1;

            if (mcMMO.isRetroModeEnabled()) {
                dropLevel = config.getInt(type + "." + treasureName + LEVEL_REQUIREMENT_RETRO_MODE, -1);
            } else {
                dropLevel = config.getInt(type + "." + treasureName + LEVEL_REQUIREMENT_STANDARD_MODE, -1);
            }

            if (dropLevel == -1) {
                mcMMO.p.getLogger().severe("Could not find a Level_Requirement entry for treasure " + treasureName);
                mcMMO.p.getLogger().severe("Skipping treasure");
                continue;
            }

            if (xp < 0) {
                mcMMO.p.getLogger().warning(treasureName + " has an invalid XP value: " + xp);
            }

            if (dropChance < 0.0D) {
                mcMMO.p.getLogger().warning(treasureName + " has an invalid Drop_Chance: " + dropChance);
            }

            /*
             * Itemstack
             */
            ItemStack item = null;

            if (materialName.contains("POTION")) {
                Material mat = Material.matchMaterial(materialName);
                if (mat == null) {
                    mcMMO.p.getLogger().warning("Potion format for " + FILENAME + " has changed");
                } else {
                    item = new ItemStack(mat, amount, data);
                    PotionMeta itemMeta = (PotionMeta) item.getItemMeta();

                    PotionType potionType = null;
                    try {
                        potionType = PotionType.valueOf(config.getString(type + "." + treasureName + ".PotionData.PotionType", "WATER"));
                    } catch (IllegalArgumentException ex) {
                        mcMMO.p.getLogger().warning("Invalid Potion_Type: " + config.getString(type + "." + treasureName + ".PotionData.PotionType", "WATER"));
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

            //TODO: Rewrite legacy validation code
            // Look into what needs to change for this
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

        //Apply our fix
        if (shouldWeUpdateFile) {
            try {
                config.save(getFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean processAutomaticKeyConversion(String type, boolean shouldWeUpdateTheFile, String treasureName, DropLevelKeyConversionType conversionType) {
        switch (conversionType) {
            case LEGACY:
                int legacyDropLevel = getWrongKeyValue(type, treasureName, conversionType); //Legacy only had one value, Retro Mode didn't have a setting
                //Config needs to be updated to be more specific
                mcMMO.p.getLogger().info("(" + treasureName + ") [Fixing bad address: Legacy] Converting Drop_Level to Level_Requirement in treasures.yml for treasure to match new expected format");
                config.set(type + "." + treasureName + LEGACY_DROP_LEVEL, null); //Remove legacy entry
                config.set(type + "." + treasureName + LEVEL_REQUIREMENT_RETRO_MODE, legacyDropLevel * 10); //Multiply by 10 for Retro
                config.set(type + "." + treasureName + LEVEL_REQUIREMENT_STANDARD_MODE, legacyDropLevel);
                shouldWeUpdateTheFile = true;
                break;
            case WRONG_KEY_STANDARD:
                mcMMO.p.getLogger().info("(" + treasureName + ") [Fixing bad address: STANDARD] Converting Drop_Level to Level_Requirement in treasures.yml for treasure to match new expected format");
                int wrongKeyValueStandard = getWrongKeyValue(type, treasureName, conversionType);
                config.set(type + "." + treasureName + WRONG_KEY_ROOT, null); //We also kill the Retro key here as we have enough information for setting in values if needed

                if (wrongKeyValueStandard != -1) {
                    config.set(type + "." + treasureName + LEVEL_REQUIREMENT_STANDARD_MODE, wrongKeyValueStandard);
                    config.set(type + "." + treasureName + LEVEL_REQUIREMENT_RETRO_MODE, wrongKeyValueStandard * 10); //Multiply by 10 for Retro
                }

                shouldWeUpdateTheFile = true;
                break;
            case WRONG_KEY_RETRO:
                mcMMO.p.getLogger().info("(" + treasureName + ") [Fixing bad address: RETRO] Converting Drop_Level to Level_Requirement in treasures.yml for treasure to match new expected format");
                int wrongKeyValueRetro = getWrongKeyValue(type, treasureName, conversionType);
                config.set(type + "." + treasureName + WRONG_KEY_ROOT, null); //We also kill the Retro key here as we have enough information for setting in values if needed

                if (wrongKeyValueRetro != -1) {
                    config.set(type + "." + treasureName + LEVEL_REQUIREMENT_RETRO_MODE, wrongKeyValueRetro);
                }

                shouldWeUpdateTheFile = true;
                break;
        }
        return shouldWeUpdateTheFile;
    }

    private int getWrongKeyValue(String type, String treasureName, DropLevelKeyConversionType dropLevelKeyConversionType) {
        return switch (dropLevelKeyConversionType) {
            case LEGACY -> config.getInt(type + "." + treasureName + LEGACY_DROP_LEVEL, -1);
            case WRONG_KEY_STANDARD -> config.getInt(type + "." + treasureName + WRONG_KEY_VALUE_STANDARD, -1);
            case WRONG_KEY_RETRO -> config.getInt(type + "." + treasureName + WRONG_KEY_VALUE_RETRO, -1);
        };

    }

    private void AddHylianTreasure(String dropper, HylianTreasure treasure) {
        if (!hylianMap.containsKey(dropper))
            hylianMap.put(dropper, new ArrayList<>());
        hylianMap.get(dropper).add(treasure);
    }

    private enum DropLevelKeyConversionType {
        LEGACY,
        WRONG_KEY_STANDARD,
        WRONG_KEY_RETRO
    }
}
