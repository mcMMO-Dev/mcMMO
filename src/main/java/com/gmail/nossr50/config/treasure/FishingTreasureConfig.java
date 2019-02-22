package com.gmail.nossr50.config.treasure;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.Registers;
import com.gmail.nossr50.config.UnsafeValueValidation;
import com.gmail.nossr50.datatypes.treasure.EnchantmentTreasure;
import com.gmail.nossr50.datatypes.treasure.FishingTreasure;
import com.gmail.nossr50.datatypes.treasure.Rarity;
import com.gmail.nossr50.datatypes.treasure.ShakeTreasure;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EnchantmentUtils;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FishingTreasureConfig extends Config implements UnsafeValueValidation, Registers {
    public HashMap<EntityType, List<ShakeTreasure>> shakeMap = new HashMap<EntityType, List<ShakeTreasure>>();
    public HashMap<Rarity, List<FishingTreasure>> fishingRewards = new HashMap<Rarity, List<FishingTreasure>>();
    public HashMap<Rarity, List<EnchantmentTreasure>> fishingEnchantments = new HashMap<Rarity, List<EnchantmentTreasure>>();

    public static final String ITEM_DROP_RATES = "Item_Drop_Rates";
    public static final String FISHING = "Fishing";
    public static final String ENCHANTMENT_DROP_RATES = "Enchantment_Drop_Rates";
    public static final String SHAKE = "Shake";

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
