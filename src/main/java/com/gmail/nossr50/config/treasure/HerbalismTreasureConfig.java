package com.gmail.nossr50.config.treasure;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.ConfigConstants;
import com.gmail.nossr50.config.UnsafeValueValidation;
import com.gmail.nossr50.datatypes.treasure.HylianTreasure;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.StringUtils;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HerbalismTreasureConfig extends Config implements UnsafeValueValidation {
    public static final String HYLIAN_LUCK = "Hylian_Luck";
    public static final String AMOUNT = "Amount";
    public static final String XP = "XP";
    public static final String DROP_CHANCE = "Drop_Chance";
    public static final String DROP_LEVEL = "Drop_Level";
    public static final String CUSTOM_NAME = "Custom_Name";
    public static final String LORE = "Lore";

    public HashMap<String, List<HylianTreasure>> hylianMap = new HashMap<>();

    public HerbalismTreasureConfig() {
        super("hylian_luck_drops", mcMMO.p.getDataFolder().getAbsoluteFile(), ConfigConstants.RELATIVE_PATH_CONFIG_DIR, true, false, true, false);
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
    public static HerbalismTreasureConfig getInstance() {
        return mcMMO.getConfigManager().getHerbalismTreasureConfig();
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

    private void addHylianTreasure(String dropper, HylianTreasure treasure) {
        if (!hylianMap.containsKey(dropper))
            hylianMap.put(dropper, new ArrayList<>());

        hylianMap.get(dropper).add(treasure);
    }
}
