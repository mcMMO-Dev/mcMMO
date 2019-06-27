package com.gmail.nossr50.config.treasure;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.ConfigConstants;
import com.gmail.nossr50.config.UnsafeValueValidation;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.mcMMO;

import java.util.HashMap;
import java.util.List;

public class ExcavationTreasureConfig extends Config implements UnsafeValueValidation {
    public static final String EXCAVATION = "Archaeology";
    public static final String AMOUNT = "Amount";
    public static final String XP = "XP";
    public static final String DROP_CHANCE = "Drop_Chance";
    public static final String DROP_LEVEL = "Drop_Level";
    public static final String CUSTOM_NAME = "Custom_Name";
    public static final String LORE = "Lore";
    public HashMap<String, List<ExcavationTreasure>> excavationMap = new HashMap<>();

    public ExcavationTreasureConfig() {
        super("excavation_drops", pluginRef.getDataFolder().getAbsoluteFile(), ConfigConstants.RELATIVE_PATH_CONFIG_DIR, true, false, true, false);
    }

    /**
     * This grabs an instance of this config class from the Config Manager
     * This method is deprecated and will be removed in the future
     *
     * @return the instance of this config
     * @see mcMMO#getConfigManager()
     * @deprecated Please use mcMMO.getConfigManager() to grab a specific config instead
     */
    @Deprecated
    public static ExcavationTreasureConfig getInstance() {
        return pluginRef.getConfigManager().getExcavationTreasureConfig();
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
