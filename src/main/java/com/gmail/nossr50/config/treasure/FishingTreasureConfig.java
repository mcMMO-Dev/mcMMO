package com.gmail.nossr50.config.treasure;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.Registers;
import com.gmail.nossr50.config.UnsafeValueValidation;
import com.gmail.nossr50.datatypes.treasure.*;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EnchantmentUtils;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FishingTreasureConfig extends Config implements UnsafeValueValidation, Registers {
    public static final String PLAYER = "PLAYER";
    public static final String INVENTORY = "INVENTORY";
    public static final String WHOLE_STACKS = "Whole_Stacks";
    public static final String DROP_CHANCE = "Drop_Chance";
    public static final String DROP_LEVEL = "Drop_Level";
    public static final String TIER = "Tier_";
    public static final String ENCHANTMENTS_RARITY = "Enchantments_Rarity";
    public static final String ITEM_DROP_RATES = "Item_Drop_Rates";
    public static final String FISHING = "Fishing";
    public static final String ENCHANTMENT_DROP_RATES = "Enchantment_Drop_Rates";
    public static final String SHAKE = "Shake";
    public static final String AMOUNT = "Amount";
    public static final String XP = "XP";
    public static final String CUSTOM_NAME = "Custom_Name";
    public static final String LORE = "Lore";
    public static final String RARITY = "Rarity";
    public static final String DROPS_FROM = "Drops_From";

    public HashMap<EntityType, List<ShakeTreasure>> shakeMap = new HashMap<EntityType, List<ShakeTreasure>>();
    public HashMap<Rarity, List<FishingTreasure>> fishingRewards = new HashMap<Rarity, List<FishingTreasure>>();
    public HashMap<Rarity, List<EnchantmentTreasure>> fishingEnchantments = new HashMap<Rarity, List<EnchantmentTreasure>>();

    /**
     * This grabs an instance of this config class from the Config Manager
     * This method is deprecated and will be removed in the future
     * @see mcMMO#getConfigManager()
     * @return the instance of this config
     * @deprecated Please use mcMMO.getConfigManager() to grab a specific config instead
     */
    @Deprecated
    public static FishingTreasureConfig getInstance() {
        return mcMMO.getConfigManager().getFishingTreasureConfig();
    }

    public FishingTreasureConfig() {
        super(mcMMO.p.getDataFolder().getAbsoluteFile(), "fishing_treasures.yml", false, true, false);
        register();
    }

    /**
     * Register stuff
     */
    @Override
    public void register() {
        /* FISHING TREASURES */

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
                //Treasure Material Definition
                Material treasureMaterial = Material.matchMaterial(treasureName.toUpperCase());

                if(treasureMaterial != null)
                {
                    ConfigurationNode currentTreasure = fishingTreasureNode.getNode(treasureName);

                    //TODO: Rewrite the entire treasure system because it sucks

                    /*
                     * TREASURE PARAMETERS
                     */
                    int amount = currentTreasure.getNode(AMOUNT).getInt();
                    int xp = currentTreasure.getNode(XP).getInt();
                    String customName = null;

                    /*
                     * PARAMETER INIT
                     */

                    ArrayList<String> dropsFrom = new ArrayList(currentTreasure.getNode(DROPS_FROM).getList(TypeToken.of(String.class)));

                    //VALIDATE AMOUNT
                    if(amount <= 0)
                    {
                        mcMMO.p.getLogger().severe("Excavation Treasure named "+treasureName+" in the config has an amount of 0 or below, is this intentional?");
                        mcMMO.p.getLogger().severe("Skipping "+treasureName+" for being invalid");
                        continue;
                    }

                    //VALIDATE XP
                    if(xp <= 0)
                    {
                        mcMMO.p.getLogger().info("Excavation Treasure named "+treasureName+" in the config has xp set to 0 or below, is this intentional?");
                        xp = 0;
                    }

                    //VALIDATE DROP SOURCES
                    if(dropsFrom == null || dropsFrom.isEmpty())
                    {
                        mcMMO.p.getLogger().severe("Excavation Treasure named "+treasureName+" in the config has no drop targets, which would make it impossible to obtain, is this intentional?");
                        mcMMO.p.getLogger().severe("Skipping "+treasureName+" for being invalid");
                        continue;
                    }

                    /* OPTIONAL PARAMETERS */

                    //Custom Name

                    if(currentTreasure.getNode(CUSTOM_NAME) != null && !currentTreasure.getNode(CUSTOM_NAME).getString().equalsIgnoreCase("ChangeMe"))
                    {
                        customName = currentTreasure.getNode(CUSTOM_NAME).getString();
                    }

                    /*
                     * REGISTER TREASURE
                     */

                    FishingTreasure fishingTreasure = TreasureFactory.makeFishingTreasure(treasureMaterial, amount, xp, customName, currentTreasure.getNode(LORE));

                    /*
                     * Add to map
                     */

                    String configRarity = currentTreasure.getNode(RARITY).getString();

                    for(Rarity rarity : Rarity.values())
                    {
                        if(rarity.toString().equalsIgnoreCase(configRarity))
                        {
                            /*if(fishingRewards.get(rarity) == null)
                                fishingRewards.put(rarity, new ArrayList<>());*/

                            fishingRewards.get(rarity).add(fishingTreasure);
                        }
                    }

                } else {
                    mcMMO.p.getLogger().severe("Excavation Treasure Config - Material named "+treasureName+" does not match any known material.");
                }
            }
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }

        //Shake
        for (EntityType entity : EntityType.values()) {
            if (entity.isAlive()) {
                loadShake(entity);
            }
        }

        //Enchantments
        loadEnchantments();
    }

    private void loadShake(EntityType entityType)
    {
        ConfigurationNode shakeTreasureNode = getUserRootNode().getNode(SHAKE, entityType.toString());

        if(shakeTreasureNode != null)
            return;

        try {
            for (String treasureName : shakeTreasureNode.getList(TypeToken.of(String.class))) {
                //Treasure Material Definition
                Material treasureMaterial = Material.matchMaterial(treasureName.toUpperCase());

                if(treasureMaterial != null)
                {
                    ConfigurationNode currentTreasure = shakeTreasureNode.getNode(treasureName);

                    //TODO: Rewrite the entire treasure system because it sucks

                    /*
                     * TREASURE PARAMETERS
                     */
                    int amount = currentTreasure.getNode(AMOUNT).getInt();
                    int xp = currentTreasure.getNode(XP).getInt();
                    double dropChance = currentTreasure.getNode(DROP_CHANCE).getDouble();
                    int dropLevel = currentTreasure.getNode(DROP_LEVEL).getInt();
                    String customName = null;

                    /*
                     * PARAMETER INIT
                     */

                    ArrayList<String> dropsFrom = new ArrayList(currentTreasure.getNode(DROPS_FROM).getList(TypeToken.of(String.class)));

                    //VALIDATE AMOUNT
                    if(amount <= 0)
                    {
                        mcMMO.p.getLogger().severe("Excavation Treasure named "+treasureName+" in the config has an amount of 0 or below, is this intentional?");
                        mcMMO.p.getLogger().severe("Skipping "+treasureName+" for being invalid");
                        continue;
                    }

                    //VALIDATE XP
                    if(xp <= 0)
                    {
                        mcMMO.p.getLogger().info("Excavation Treasure named "+treasureName+" in the config has xp set to 0 or below, is this intentional?");
                        xp = 0;
                    }

                    //VALIDATE DROP CHANCE
                    if(dropChance <= 0)
                    {
                        mcMMO.p.getLogger().severe("Excavation Treasure named "+treasureName+" in the config has a drop chance of 0 or below, is this intentional?");
                        mcMMO.p.getLogger().severe("Skipping "+treasureName+" for being invalid");
                        continue;
                    }

                    //VALIDATE DROP LEVEL
                    if(dropLevel < 0)
                    {
                        mcMMO.p.getLogger().info("Excavation Treasure named "+treasureName+" in the config has a drop level below 0, is this intentional?");
                        dropLevel = 0;
                    }

                    //VALIDATE DROP SOURCES
                    if(dropsFrom == null || dropsFrom.isEmpty())
                    {
                        mcMMO.p.getLogger().severe("Excavation Treasure named "+treasureName+" in the config has no drop targets, which would make it impossible to obtain, is this intentional?");
                        mcMMO.p.getLogger().severe("Skipping "+treasureName+" for being invalid");
                        continue;
                    }

                    /* OPTIONAL PARAMETERS */

                    //Custom Name

                    if(currentTreasure.getNode(CUSTOM_NAME) != null && !currentTreasure.getNode(CUSTOM_NAME).getString().equalsIgnoreCase("ChangeMe"))
                    {
                        customName = currentTreasure.getNode(CUSTOM_NAME).getString();
                    }

                    /*
                     * REGISTER TREASURE
                     */

                    ShakeTreasure shakeTreasure = TreasureFactory.makeShakeTreasure(treasureMaterial, amount, xp, dropChance, dropLevel, customName, currentTreasure.getNode(LORE));

                    /*
                     * Add to map
                     */
                    if(shakeMap.get(entityType) == null)
                        shakeMap.put(entityType, new ArrayList<>());

                    shakeMap.get(entityType).add(shakeTreasure);

                } else {
                    mcMMO.p.getLogger().severe("Excavation Treasure Config - Material named "+treasureName+" does not match any known material.");
                }
            }
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    private void loadEnchantments() {
        for (Rarity rarity : Rarity.values()) {
            if (rarity == Rarity.RECORD) {
                continue;
            }

            if (!fishingEnchantments.containsKey(rarity)) {
                fishingEnchantments.put(rarity, (new ArrayList<EnchantmentTreasure>()));
            }

            ConfigurationSection enchantmentSection = config.getConfigurationSection(ENCHANTMENTS_RARITY, rarity.toString());

            if (enchantmentSection == null) {
                return;
            }

            for (String enchantmentName : enchantmentSection.getKeys(false)) {
                int level = getIntValue(ENCHANTMENTS_RARITY, rarity.toString(), enchantmentName);
                Enchantment enchantment = EnchantmentUtils.getByName(enchantmentName);

                if (enchantment == null) {
                    plugin.getLogger().warning("Skipping invalid enchantment in treasures.yml: " + enchantmentName);
                    continue;
                }

                fishingEnchantments.get(rarity).add(new EnchantmentTreasure(enchantment, level));
            }
        }
    }

    @Override
    public void unload() {
        shakeMap.clear();
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

    /**
     * The version of this config
     *
     * @return
     */
    @Override
    public double getConfigVersion() {
        return 1;
    }

    public boolean getInventoryStealEnabled() {
        return hasNode(SHAKE, PLAYER, INVENTORY);
    }

    public boolean getInventoryStealStacks() {
        return getBooleanValue(SHAKE, PLAYER, INVENTORY, WHOLE_STACKS);
    }

    public double getInventoryStealDropChance() {
        return getDoubleValue(SHAKE, PLAYER, INVENTORY, DROP_CHANCE);
    }

    public int getInventoryStealDropLevel() {
        return getIntValue(SHAKE, PLAYER, INVENTORY, DROP_LEVEL);
    }

    public double getItemDropRate(int tier, Rarity rarity) {
        return getDoubleValue(ITEM_DROP_RATES, TIER + tier, rarity.toString());
    }

    public double getEnchantmentDropRate(int tier, Rarity rarity) {
        return getDoubleValue(ENCHANTMENT_DROP_RATES, TIER + tier, rarity.toString());
    }
}
