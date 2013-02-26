package com.gmail.nossr50.config;

import org.bukkit.Material;

import com.gmail.nossr50.util.StringUtils;

public class ItemWeightsConfig extends ConfigLoader {
    private static ItemWeightsConfig instance;

    private ItemWeightsConfig() {
        super("itemweights.yml");
    }

    public static ItemWeightsConfig getInstance() {
        if (instance == null) {
            instance = new ItemWeightsConfig();
        }

        return instance;
    }

    @Override
    protected void loadKeys() {}

    public int getItemWeight(Material material) {
        String materialName = StringUtils.getPrettyItemString(material).replace(" ", "_");
        int itemWeight = config.getInt("Item_Weights.Default");

        if (config.getInt("Item_Weights." + materialName) > 0) {
            itemWeight = config.getInt("Item_Weights." + materialName);
        }
        return itemWeight;
    }
}
