package com.gmail.nossr50.config.treasure;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.Registers;
import com.gmail.nossr50.config.UnsafeValueValidation;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.mcMMO;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExcavationTreasureConfig extends Config implements UnsafeValueValidation, Registers {
    public static final String EXCAVATION = "Archaeology";
    public static final String AMOUNT = "Amount";
    public static final String XP = "XP";
    public static final String DROP_CHANCE = "Drop_Chance";
    public static final String DROP_LEVEL = "Drop_Level";
    public static final String CUSTOM_NAME = "Custom_Name";
    public static final String LORE = "Lore";
    public HashMap<String, List<ExcavationTreasure>> excavationMap = new HashMap<String, List<ExcavationTreasure>>();

    public ExcavationTreasureConfig() {
        super(mcMMO.p.getDataFolder().getAbsoluteFile(), "excavation_treasures.yml", false, true, false);
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
    public static ExcavationTreasureConfig getInstance() {
        return mcMMO.getConfigManager().getExcavationTreasureConfig();
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
                //Treasure Material Definition
                Material treasureMaterial = Material.matchMaterial(treasureName.toUpperCase());

                if(treasureMaterial != null)
                {
                    ConfigurationNode currentTreasure = excavationTreasureNode.getNode(treasureName);

                    //TODO: Rewrite the entire treasure system because it sucks

                    /*
                     * TREASURE PARAMETERS
                     */
                    int amount = currentTreasure.getNode(AMOUNT).getInt();
                    int xp = currentTreasure.getNode(XP).getInt();
                    double dropChance = currentTreasure.getNode(DROP_CHANCE).getDouble();
                    int dropLevel = currentTreasure.getNode(DROP_LEVEL).getInt();
                    String customName = null;
                    String lore;

                    /*
                     * PARAMETER INIT
                     */

                    ArrayList<String> dropsFrom = new ArrayList(currentTreasure.getNode("Drops_From").getList(TypeToken.of(String.class)));

                    if(amount <= 0)
                    {
                        mcMMO.p.getLogger().severe("Excavation Treasure named "+treasureName+" in the config has an amount of 0 or below, is this intentional?");
                        mcMMO.p.getLogger().severe("Skipping "+treasureName+" for being invalid");
                        continue;
                    }

                    if(xp <= 0)
                    {
                        mcMMO.p.getLogger().info("Excavation Treasure named "+treasureName+" in the config has xp set to 0 or below, is this intentional?");
                        xp = 0;
                    }

                    if(dropChance <= 0)
                    {
                        mcMMO.p.getLogger().severe("Excavation Treasure named "+treasureName+" in the config has a drop chance of 0 or below, is this intentional?");
                        mcMMO.p.getLogger().severe("Skipping "+treasureName+" for being invalid");
                        continue;
                    }

                    if(dropLevel < 0)
                    {
                        mcMMO.p.getLogger().info("Excavation Treasure named "+treasureName+" in the config has a drop level below 0, is this intentional?");
                        dropLevel = 0;
                    }

                    if(dropsFrom == null || dropsFrom.isEmpty())
                    {
                        mcMMO.p.getLogger().severe("Excavation Treasure named "+treasureName+" in the config has no drop targets, which would make it impossible to obtain, is this intentional?");
                        mcMMO.p.getLogger().severe("Skipping "+treasureName+" for being invalid");
                        continue;
                    }

                    /* OPTIONAL PARAMETERS */

                    //Custom Name

                    if(currentTreasure.getNode(CUSTOM_NAME) != null && !currentTreasure.getNode(CUSTOM_NAME).getString().equalsIgnoreCase("ChangeMe"))
                    {
                        customName = currentTreasure.getNode(CUSTOM_NAME).getString();
                    }

                    //Lore
                    if(currentTreasure.getNode(LORE) != null && !currentTreasure.getNode(LORE).getString().equalsIgnoreCase("ChangeMe"))
                    {
                        lore = currentTreasure.getNode(LORE).getString();
                    }

                    /*
                     * REGISTER TREASURE
                     */

                    ExcavationTreasure excavationTreasure = TreasureFactory.makeExcavationTreasure(treasureMaterial, amount, xp, dropChance, dropLevel, customName, currentTreasure.getNode(LORE));

                    /*
                     * Add to map
                     */
                    for(String dropBlock : dropsFrom)
                    {
                        if(excavationMap.get(dropBlock) == null)
                            excavationMap.put(dropBlock, new ArrayList<>());

                        excavationMap.get(dropBlock).add(excavationTreasure);
                    }

                } else {
                    mcMMO.p.getLogger().severe("Excavation Treasure Config - Material named "+treasureName+" does not match any known material.");
                }
            }
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unload() {
        excavationMap.clear();
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
