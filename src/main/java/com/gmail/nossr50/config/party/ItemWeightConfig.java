package com.gmail.nossr50.config.party;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.ConfigConstants;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.StringUtils;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.Material;

import java.util.HashSet;

public class ItemWeightConfig extends Config {
    public static final String ITEM_WEIGHTS = "Item_Weights";
    public static final String DEFAULT = "Default";
    public static final String PARTY_SHAREABLES = "Party_Shareables";
    public static final String MISC_ITEMS = "Misc_Items";

    public ItemWeightConfig() {
        //super(McmmoCore.getDataFolderPath().getAbsoluteFile(), "itemweights.yml");
        super("itemweights", mcMMO.p.getDataFolder().getAbsoluteFile(), ConfigConstants.RELATIVE_PATH_CONFIG_DIR, true, true, true, false);
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
        return mcMMO.getConfigManager().getItemWeightConfig();
    }

    @Override
    public void unload() {
        //do nothing
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

        try {
            for (String item : getListFromNode(PARTY_SHAREABLES, MISC_ITEMS)) {
                Material material = Material.getMaterial(item.toUpperCase());

                if (material != null) {
                    miscItems.add(material);
                }
            }
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
        return miscItems;
    }
}
