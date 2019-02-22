package com.gmail.nossr50.config.treasure;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.Registers;
import com.gmail.nossr50.config.UnsafeValueValidation;
import com.gmail.nossr50.datatypes.treasure.HylianTreasure;
import com.gmail.nossr50.mcMMO;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HerbalismTreasureConfig extends Config implements UnsafeValueValidation, Registers {
    public static final String HYLIAN_LUCK = "Hylian_Luck";
    public HashMap<String, List<HylianTreasure>> hylianMap = new HashMap<String, List<HylianTreasure>>();

    public HerbalismTreasureConfig() {
        super(mcMMO.p.getDataFolder().getAbsoluteFile(), "herbalism_treasures.yml", false, true, false);
        register();
    }

    /**
     * This grabs an instance of this config class from the Config Manager
     * This method is deprecated and will be removed in the future
     * @see mcMMO#getConfigManager()
     * @return the instance of this config
     * @deprecated Please use mcMMO.getConfigManager() to grab a specific config instead
     */
    @Deprecated
    public static HerbalismTreasureConfig getInstance() {
        return mcMMO.getConfigManager().getHerbalismTreasureConfig();
    }

    /**
     * Register stuff
     */
    @Override
    public void register() {
        ConfigurationNode herbalismTreasureNode = getUserRootNode().getNode(HYLIAN_LUCK);

        if(herbalismTreasureNode == null)
        {
            mcMMO.p.getLogger().info("Hylian_Luck in treasures config not defined");
            return;
        }

        try {
            for (String treasureName : herbalismTreasureNode.getList(TypeToken.of(String.class))) {

            }
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unload() {
        hylianMap.clear();
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

    private void AddHylianTreasure(String dropper, HylianTreasure treasure) {
        if (!hylianMap.containsKey(dropper))
            hylianMap.put(dropper, new ArrayList<HylianTreasure>());
        hylianMap.get(dropper).add(treasure);
    }
}
