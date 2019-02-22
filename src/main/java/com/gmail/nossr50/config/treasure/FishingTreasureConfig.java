package com.gmail.nossr50.config.treasure;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.Registers;
import com.gmail.nossr50.config.UnsafeValueValidation;
import com.gmail.nossr50.datatypes.treasure.EnchantmentTreasure;
import com.gmail.nossr50.datatypes.treasure.FishingTreasure;
import com.gmail.nossr50.datatypes.treasure.Rarity;
import com.gmail.nossr50.datatypes.treasure.ShakeTreasure;
import com.gmail.nossr50.mcMMO;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
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

    @Override
    public void unload() {
        shakeMap.clear();
        fishingRewards.clear();
        fishingEnchantments.clear();
    }

    @Override
    public List<String> validateKeys() {
        return null;
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
}
