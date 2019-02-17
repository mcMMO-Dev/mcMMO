package com.gmail.nossr50.core.config.party;

import com.gmail.nossr50.core.McmmoCore;
import com.gmail.nossr50.core.config.Config;
import com.gmail.nossr50.core.util.StringUtils;

public class ItemWeightConfig extends Config {
    private static ItemWeightConfig instance;

    private ItemWeightConfig() {
        super(McmmoCore.getDataFolderPath().getAbsoluteFile(), "itemweights.yml");
    }

    public static ItemWeightConfig getInstance() {
        if (instance == null) {
            instance = new ItemWeightConfig();
        }

        return instance;
    }

    public int getItemWeight(Material material) {
        return getIntValue("Item_Weights." + StringUtils.getPrettyItemString(material).replace(" ", "_"), getIntValue("Item_Weights.Default"));
    }

    public HashSet<Material> getMiscItems() {
        HashSet<Material> miscItems = new HashSet<Material>();

        for (String item : getStringValueList("Party_Shareables.Misc_Items")) {
            Material material = Material.getMaterial(item.toUpperCase());

            if (material != null) {
                miscItems.add(material);
            }
        }
        return miscItems;
    }

    @Override
    protected void loadKeys() {
    }
}
