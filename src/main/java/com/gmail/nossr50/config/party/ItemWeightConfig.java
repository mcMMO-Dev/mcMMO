package com.gmail.nossr50.config.party;

import java.util.HashSet;
import java.util.List;

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

    public HashSet<Material> getMiscItems() {
        HashSet<Material> miscItems = new HashSet<Material>();

        List<String> itemList = config.getStringList("Party_Shareables.Misc_Items");

        for (String item : itemList) {
            String materialName = item.toUpperCase();
            Material material = Material.getMaterial(materialName);

            if (material != null) {
                miscItems.add(material);
            }
        }
        return miscItems;
    }

    @Override
    protected void loadKeys() {}
}
