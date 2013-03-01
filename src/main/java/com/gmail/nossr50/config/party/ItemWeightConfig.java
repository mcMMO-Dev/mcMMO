package com.gmail.nossr50.config.party;

import org.bukkit.Material;

import com.gmail.nossr50.config.ConfigLoader;
import com.gmail.nossr50.util.StringUtils;

public class ItemWeightConfig extends ConfigLoader {
    private static ItemWeightConfig instance;

    private ItemWeightConfig() {
        super("itemweights.yml");
    }

    public static ItemWeightConfig getInstance() {
        if (instance == null) {
            instance = new ItemWeightConfig();
        }

        return instance;
    }

    public int getItemWeight(Material material) {
        String materialName = StringUtils.getPrettyItemString(material).replace(" ", "_");
        int itemWeight = config.getInt("Item_Weights.Default");

        if (config.getInt("Item_Weights." + materialName) > 0) {
            itemWeight = config.getInt("Item_Weights." + materialName);
        }
        return itemWeight;
    }

    @Override
    protected void loadKeys() {}
}
