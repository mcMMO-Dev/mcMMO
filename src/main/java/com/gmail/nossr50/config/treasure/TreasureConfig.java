package com.gmail.nossr50.config.treasure;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.ConfigCollection;
import com.gmail.nossr50.config.Registers;
import com.gmail.nossr50.config.UnsafeValueValidation;
import com.gmail.nossr50.datatypes.treasure.*;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EnchantmentUtils;
import com.gmail.nossr50.util.StringUtils;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//TODO: Need to rewrite this too
public class TreasureConfig extends Config implements UnsafeValueValidation, Registers {

    public static final String ENCHANTMENT_DROP_RATES = "Enchantment_Drop_Rates";
    public static final String ITEM_DROP_RATES = "Item_Drop_Rates";
    public static final String FISHING = "Fishing";
    public static final String EXCAVATION = "Excavation";
    public static final String SHAKE = "Shake";
    public static final String HYLIAN_LUCK = "Hylian_Luck";

    public HashMap<String, List<ExcavationTreasure>> excavationMap = new HashMap<String, List<ExcavationTreasure>>();

    public HashMap<EntityType, List<ShakeTreasure>> shakeMap = new HashMap<EntityType, List<ShakeTreasure>>();
    public HashMap<String, List<HylianTreasure>> hylianMap = new HashMap<String, List<HylianTreasure>>();

    public HashMap<Rarity, List<FishingTreasure>> fishingRewards = new HashMap<Rarity, List<FishingTreasure>>();
    public HashMap<Rarity, List<EnchantmentTreasure>> fishingEnchantments = new HashMap<Rarity, List<EnchantmentTreasure>>();

    public TreasureConfig() {
        //super(McmmoCore.getDataFolderPath().getAbsoluteFile(),"treasures.yml");
        super(mcMMO.p.getDataFolder().getAbsoluteFile(), "treasures.yml", false, true, false);
        register();
        validateEntries();
    }

    /**
     * This grabs an instance of this config class from the Config Manager
     * This method is deprecated and will be removed in the future
     * @see mcMMO#getConfigManager()
     * @return the instance of this config
     * @deprecated Please use mcMMO.getConfigManager() to grab a specific config instead
     */
    @Deprecated
    public static TreasureConfig getInstance() {
        return mcMMO.getConfigManager().getTreasureConfig();
    }

    /**
     * The version of this config
     *
     * @return
     */
    @Override
    public double getConfigVersion() {
        return 1;
    }

    @Override
    public void unload() {
        excavationMap.clear();

        shakeMap.clear();

        hylianMap.clear();

        fishingRewards.clear();

        fishingEnchantments.clear();
    }

    @Override
    public List<String> validateKeys() {
        // Validate all the settings!
        List<String> errorMessages = new ArrayList<String>();
        try {
            for (String tier : getUserRootNode().getNode(ENCHANTMENT_DROP_RATES).getList(TypeToken.of(String.class))) {
                /*double totalEnchantDropRate = 0;
                double totalItemDropRate = 0;*/

                for (Rarity rarity : Rarity.values()) {
                    double enchantDropRate = getDoubleValue(ENCHANTMENT_DROP_RATES, tier, rarity.toString());
                    double itemDropRate = getDoubleValue(ITEM_DROP_RATES, tier, rarity.toString());

                    if ((enchantDropRate < 0.0 || enchantDropRate > 100.0) && rarity != Rarity.RECORD) {
                        errorMessages.add("The enchant drop rate for " + tier + " items that are " + rarity.toString() + "should be between 0.0 and 100.0!");

                        //Bound Values
                        /*enchantDropRate = boundValues(enchantDropRate, 0.0D, 100.0D);*/
                    }

                    if (itemDropRate < 0.0 || itemDropRate > 100.0) {
                        errorMessages.add("The item drop rate for " + tier + " items that are " + rarity.toString() + "should be between 0.0 and 100.0!");

                        //Bound Values
                        /*itemDropRate = boundValues(itemDropRate, 0.0D, 100.0D);*/
                    }

                    /*totalEnchantDropRate += enchantDropRate;
                    totalItemDropRate += itemDropRate;*/
                }

                //TODO: Why does it matter what the total item/enchant drop rate is?

                /*if (totalEnchantDropRate < 0 || totalEnchantDropRate > 100.0) {
                    errorMessages.add("The total enchant drop rate for " + tier + " should be between 0.0 and 100.0!");
                }

                if (totalItemDropRate < 0 || totalItemDropRate > 100.0) {
                    errorMessages.add("The total item drop rate for " + tier + " should be between 0.0 and 100.0!");
                }*/
            }
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }

        return errorMessages;
    }

    private double boundValues(double valueRef, double min, double max)
    {
        if(valueRef < min)
            valueRef = min;
        else if(valueRef > max)
            valueRef = max;

        return valueRef;
    }

    @Override
    public void register() {
        /*loadTreasures("Fishing");
        loadTreasures("Excavation");
        loadTreasures("Hylian_Luck");*/

        initRegisters();

        loadFishing();
        loadExcavation();
        loadHerbalism();

        loadEnchantments();

        for (EntityType entity : EntityType.values()) {
            if (entity.isAlive()) {
                loadShake(entity);
            }
        }
    }

    private void initRegisters()
    {
        if(excavationMap == null)
            excavationMap = new HashMap<>();

        if(shakeMap == null)
            shakeMap = new HashMap<>();

        if(hylianMap == null)
            hylianMap = new HashMap<>();

        if(fishingRewards == null)
            fishingRewards = new HashMap<>();

        if(fishingEnchantments == null)
            fishingEnchantments = new HashMap<>();
    }

    private void loadFishing()
    {
        ConfigurationNode fishingTreasureNode = getUserRootNode().getNode(FISHING);

        if(fishingTreasureNode == null)
        {
            mcMMO.p.getLogger().info("Fishing treasures in treasures config not defined");
            return;
        }

        // Initialize fishing HashMap
        for (Rarity rarity : Rarity.values()) {
            if (!fishingRewards.containsKey(rarity)) {
                fishingRewards.put(rarity, (new ArrayList<FishingTreasure>()));
            }
        }

        try {
            for (String treasureName : fishingTreasureNode.getList(TypeToken.of(String.class))) {

            }
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }

    }

    private void loadExcavation()
    {
        ConfigurationNode excavationTreasureNode = getUserRootNode().getNode(EXCAVATION);

        if(excavationTreasureNode == null)
        {
            mcMMO.p.getLogger().info("Excavation treasures in treasures config not defined");
            return;
        }

        try {
            for (String treasureName : excavationTreasureNode.getList(TypeToken.of(String.class))) {

            }
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    private void loadHerbalism()
    {
        ConfigurationNode herbalismTreasureNode = getUserRootNode().getNode(HYLIAN_LUCK);

        if(herbalismTreasureNode == null)
        {
            mcMMO.p.getLogger().info("Hylian_Luck in treasures config not defined");
            return;
        }

        try {
            for (String treasureName : herbalismTreasureNode.getList(TypeToken.of(String.class))) {

            }
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    private void loadShake(EntityType entityType)
    {
        ConfigurationNode shakeTreasureNode = getUserRootNode().getNode(SHAKE, entityType.toString());

        if(shakeTreasureNode != null)
            return;

        try {
            for (String treasureName : shakeTreasureNode.getList(TypeToken.of(String.class))) {

            }
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    private void loadTreasures(ConfigurationNode treasureChildNode) {
        if (treasureChildNode == null) {
            return;
        }



        for (String treasureName : treasureChildNode.getKeys(false)) {
            // Validate all the things!
            List<String> errorMessages = new ArrayList<String>();

            String[] treasureInfo = treasureName.split("[|]");
            String materialName = treasureInfo[0];

            /*
             * Material, Amount, and Data
             */
            Material material;

            if (materialName.contains("INVENTORY")) {
                // Use magic material BEDROCK to know that we're grabbing something from the inventory and not a normal treasure
                if (!shakeMap.containsKey(EntityType.PLAYER))
                    shakeMap.put(EntityType.PLAYER, new ArrayList<ShakeTreasure>());
                shakeMap.get(EntityType.PLAYER).add(new ShakeTreasure(new ItemStack(Material.BEDROCK, 1, (byte) 0), 1, getInventoryStealDropChance(), getInventoryStealDropLevel()));
                continue;
            } else {
                material = Material.matchMaterial(materialName);
            }

            int amount = getIntValue(treasureChildNodeAddress + "." + treasureName + ".Amount");
            short data = (treasureInfo.length == 2) ? Short.parseShort(treasureInfo[1]) : (short) getIntValue(treasureChildNodeAddress + "." + treasureName + ".Data");

            if (material == null) {
                errorMessages.add("Invalid material: " + materialName);
            }

            if (amount <= 0) {
                errorMessages.add("Amount of " + treasureName + " must be greater than 0! " + amount);
            }

            if (material != null && material.isBlock() && (data > 127 || data < -128)) {
                errorMessages.add("Data of " + treasureName + " is invalid! " + data);
            }

            /*
             * XP, Drop Chance, and Drop Level
             */

            int xp = getIntValue(treasureChildNodeAddress + "." + treasureName + ".XP");
            double dropChance = getDoubleValue(treasureChildNodeAddress + "." + treasureName + ".Drop_Chance");
            int dropLevel = getIntValue(treasureChildNodeAddress + "." + treasureName + ".Drop_Level");

            if (xp < 0) {
                errorMessages.add(treasureName + " has an invalid XP value: " + xp);
            }

            if (dropChance < 0.0D) {
                errorMessages.add(treasureName + " has an invalid Drop_Chance: " + dropChance);
            }

            if (dropLevel < 0) {
                errorMessages.add(treasureName + " has an invalid Drop_Level: " + dropLevel);
            }

            /*
             * Specific Types
             */
            Rarity rarity = null;

            if (isFishing) {
                rarity = Rarity.getRarity(getStringValue(treasureChildNodeAddress + "." + treasureName + ".Rarity"));

                if (rarity == null) {
                    errorMessages.add("Invalid Rarity for item: " + treasureName);
                }
            }

            /*
             * Itemstack
             */
            ItemStack item = null;

            if (materialName.contains("POTION")) {
                Material mat = Material.matchMaterial(materialName);
                if (mat == null) {
                    errorMessages.add("Potion format for Treasures.yml has changed");
                } else {
                    item = new ItemStack(mat, amount, data);
                    PotionMeta itemMeta = (PotionMeta) item.getItemMeta();

                    PotionType potionType = null;
                    try {
                        potionType = PotionType.valueOf(getStringValue(treasureChildNodeAddress + "." + treasureName + ".PotionData.PotionType", "WATER"));
                    } catch (IllegalArgumentException ex) {
                        errorMessages.add("Invalid Potion_Type: " + getStringValue(treasureChildNodeAddress + "." + treasureName + ".PotionData.PotionType", "WATER"));
                    }
                    boolean extended = getBooleanValue(treasureChildNodeAddress + "." + treasureName + ".PotionData.Extended", false);
                    boolean upgraded = getBooleanValue(treasureChildNodeAddress + "." + treasureName + ".PotionData.Upgraded", false);
                    itemMeta.setBasePotionData(new PotionData(potionType, extended, upgraded));

                    if (config.contains(treasureChildNodeAddress + "." + treasureName + ".Custom_Name")) {
                        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', getStringValue(treasureChildNodeAddress + "." + treasureName + ".Custom_Name")));
                    }

                    if (config.contains(treasureChildNodeAddress + "." + treasureName + ".Lore")) {
                        List<String> lore = new ArrayList<String>();
                        for (String s : getStringValueList(treasureChildNodeAddress + "." + treasureName + ".Lore")) {
                            lore.add(ChatColor.translateAlternateColorCodes('&', s));
                        }
                        itemMeta.setLore(lore);
                    }
                    item.setItemMeta(itemMeta);
                }
            } else if (material != null) {
                item = new ItemStack(material, amount, data);

                if (config.contains(treasureChildNodeAddress + "." + treasureName + ".Custom_Name")) {
                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', getStringValue(treasureChildNodeAddress + "." + treasureName + ".Custom_Name")));
                    item.setItemMeta(itemMeta);
                }

                if (config.contains(treasureChildNodeAddress + "." + treasureName + ".Lore")) {
                    ItemMeta itemMeta = item.getItemMeta();
                    List<String> lore = new ArrayList<String>();
                    for (String s : getStringValueList(treasureChildNodeAddress + "." + treasureName + ".Lore")) {
                        lore.add(ChatColor.translateAlternateColorCodes('&', s));
                    }
                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                }
            }

            if (noErrorsInConfig(errorMessages)) {
                if (isFishing) {
                    fishingRewards.get(rarity).add(new FishingTreasure(item, xp));
                } else if (isShake) {
                    ShakeTreasure shakeTreasure = new ShakeTreasure(item, xp, dropChance, dropLevel);

                    EntityType entityType = EntityType.valueOf(treasureChildNodeAddress.substring(6));
                    if (!shakeMap.containsKey(entityType))
                        shakeMap.put(entityType, new ArrayList<ShakeTreasure>());
                    shakeMap.get(entityType).add(shakeTreasure);
                } else if (isExcavation) {
                    ExcavationTreasure excavationTreasure = new ExcavationTreasure(item, xp, dropChance, dropLevel);
                    List<String> dropList = getStringValueList(treasureChildNodeAddress + "." + treasureName + ".Drops_From");

                    for (String blockType : dropList) {
                        if (!excavationMap.containsKey(blockType))
                            excavationMap.put(blockType, new ArrayList<ExcavationTreasure>());
                        excavationMap.get(blockType).add(excavationTreasure);
                    }
                } else if (isHylian) {
                    HylianTreasure hylianTreasure = new HylianTreasure(item, xp, dropChance, dropLevel);
                    List<String> dropList = getStringValueList(treasureChildNodeAddress + "." + treasureName + ".Drops_From");

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
            hylianMap.put(dropper, new ArrayList<HylianTreasure>());
        hylianMap.get(dropper).add(treasure);
    }

    private void loadEnchantments() {
        for (Rarity rarity : Rarity.values()) {
            if (rarity == Rarity.RECORD) {
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
                int level = getIntValue("Enchantments_Rarity." + rarity.toString() + "." + enchantmentName);
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
        return getBooleanValue("Shake.PLAYER.INVENTORY.Whole_Stacks");
    }

    public double getInventoryStealDropChance() {
        return getDoubleValue("Shake.PLAYER.INVENTORY.Drop_Chance");
    }

    public int getInventoryStealDropLevel() {
        return getIntValue("Shake.PLAYER.INVENTORY.Drop_Level");
    }

    public double getItemDropRate(int tier, Rarity rarity) {
        return getDoubleValue(ITEM_DROP_RATES + ".Tier_" + tier + "." + rarity.toString());
    }

    public double getEnchantmentDropRate(int tier, Rarity rarity) {
        return getDoubleValue("Enchantment_Drop_Rates.Tier_" + tier + "." + rarity.toString());
    }
}
