package com.gmail.nossr50.config.treasure;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.Registers;
import com.gmail.nossr50.config.UnsafeValueValidation;
import com.gmail.nossr50.datatypes.treasure.HylianTreasure;
import com.gmail.nossr50.mcMMO;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.File;
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
}
