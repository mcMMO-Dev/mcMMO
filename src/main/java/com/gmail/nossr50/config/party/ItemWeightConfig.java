package com.gmail.nossr50.config.party;

import java.util.HashSet;

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
        return config.getInt("Item_Weights." + StringUtils.getPrettyItemString(material).replace(" ", "_"), config.getInt("Item_Weights.Default"));
    }

    public HashSet<Material> getMiscItems() {
        HashSet<Material> miscItems = new HashSet<Material>();

        for (String item : config.getStringList("Party_Shareables.Misc_Items")) {
            Material material = Material.getMaterial(item.toUpperCase());

            if (material != null) {
                miscItems.add(material);
            }
        }
        return miscItems;
    }

    @Override
    protected void loadKeys() {}
}
