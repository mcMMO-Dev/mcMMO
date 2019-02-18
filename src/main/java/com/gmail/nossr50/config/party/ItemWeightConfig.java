package com.gmail.nossr50.config.party;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.MainConfig;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.StringUtils;
import org.bukkit.Material;

import java.util.HashSet;

public class ItemWeightConfig extends Config {
    public static final String ITEM_WEIGHTS = "Item_Weights";
    public static final String DEFAULT = "Default";
    public static final String PARTY_SHAREABLES = "Party_Shareables";
    public static final String MISC_ITEMS = "Misc_Items";
    private static ItemWeightConfig instance;

    public ItemWeightConfig() {
        //super(McmmoCore.getDataFolderPath().getAbsoluteFile(), "itemweights.yml");
        super(mcMMO.p.getDataFolder().getAbsoluteFile(), "itemweights.yml", true);
    }

    /**
     * This grabs an instance of this config class from the Config Manager
     * This method is deprecated and will be removed in the future
     * @see mcMMO#getConfigManager()
     * @return the instance of this config
     * @deprecated Please use mcMMO.getConfigManager() to grab a specific config instead
     */
    @Deprecated
    public static ItemWeightConfig getInstance() {
        return mcMMO.getConfigManager().getIte();
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

    /*public static ItemWeightConfig getInstance() {
        if (instance == null) {
            instance = new ItemWeightConfig();
        }

        return instance;
    }*/

    public int getItemWeight(Material material) {
        String[] keyPath = {ITEM_WEIGHTS, StringUtils.getPrettyItemString(material).replace(" ", "_")};
        if(hasNode(keyPath))
            return getIntValue(keyPath);
        else
            return getIntValue(ITEM_WEIGHTS, DEFAULT);
    }

    public HashSet<Material> getMiscItems() {
        HashSet<Material> miscItems = new HashSet<Material>();

        for (String item : getStringValueList(PARTY_SHAREABLES, MISC_ITEMS)) {
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
