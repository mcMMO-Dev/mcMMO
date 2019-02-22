package com.gmail.nossr50.config.treasure;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.Registers;
import com.gmail.nossr50.config.UnsafeValueValidation;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.mcMMO;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class ExcavationTreasureConfig extends Config implements UnsafeValueValidation, Registers {
    public static final String EXCAVATION = "Archaeology";
    public HashMap<String, List<ExcavationTreasure>> excavationMap = new HashMap<String, List<ExcavationTreasure>>();

    public ExcavationTreasureConfig() {
        super(mcMMO.p.getDataFolder().getAbsoluteFile(), "excavation_treasures.yml", false, true, false);
        register();
    }

    /**
     * Register stuff
     */
    @Override
    public void register() {
        ConfigurationNode excavationTreasureNode = getUserRootNode().getNode(EXCAVATION);

        if(excavationTreasureNode == null)
        {
            mcMMO.p.getLogger().info("Excavation treasures in treasures config not defined");
            return;
        }

        try {
            for (String treasureName : excavationTreasureNode.getList(TypeToken.of(String.class))) {

            }
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unload() {

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
