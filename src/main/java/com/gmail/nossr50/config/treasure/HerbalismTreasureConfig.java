package com.gmail.nossr50.config.treasure;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.Registers;
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

public class HerbalismTreasureConfig extends Config implements UnsafeValueValidation, Registers {
    public static final String HYLIAN_LUCK = "Hylian_Luck";
    public static final String AMOUNT = "Amount";
    public static final String XP = "XP";
    public static final String DROP_CHANCE = "Drop_Chance";
    public static final String DROP_LEVEL = "Drop_Level";
    public static final String CUSTOM_NAME = "Custom_Name";
    public static final String LORE = "Lore";

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
            mcMMO.p.getLogger().info("Excavation treasures in treasures config not defined");
            return;
        }

        try {
            for (String treasureName : herbalismTreasureNode.getList(TypeToken.of(String.class))) {
                //Treasure Material Definition
                Material treasureMaterial = Material.matchMaterial(treasureName.toUpperCase());

                if(treasureMaterial != null)
                {
                    ConfigurationNode currentTreasure = herbalismTreasureNode.getNode(treasureName);

                    //TODO: Rewrite the entire treasure system because it sucks

                    /*
                     * TREASURE PARAMETERS
                     */
                    int amount = currentTreasure.getNode(AMOUNT).getInt();
                    int xp = currentTreasure.getNode(XP).getInt();
                    double dropChance = currentTreasure.getNode(DROP_CHANCE).getDouble();
                    int dropLevel = currentTreasure.getNode(DROP_LEVEL).getInt();
                    String customName = null;

                    /*
                     * PARAMETER INIT
                     */

                    ArrayList<String> dropsFrom = new ArrayList(currentTreasure.getNode("Drops_From").getList(TypeToken.of(String.class)));

                    //VALIDATE AMOUNT
                    if(amount <= 0)
                    {
                        mcMMO.p.getLogger().severe("Herbalism Hylian Luck Treasure named "+treasureName+" in the config has an amount of 0 or below, is this intentional?");
                        mcMMO.p.getLogger().severe("Skipping "+treasureName+" for being invalid");
                        continue;
                    }

                    //VALIDATE XP
                    if(xp <= 0)
                    {
                        mcMMO.p.getLogger().info("Herbalism Hylian Luck Treasure named "+treasureName+" in the config has xp set to 0 or below, is this intentional?");
                        xp = 0;
                    }

                    //VALIDATE DROP CHANCE
                    if(dropChance <= 0)
                    {
                        mcMMO.p.getLogger().severe("Herbalism Hylian Luck Treasure named "+treasureName+" in the config has a drop chance of 0 or below, is this intentional?");
                        mcMMO.p.getLogger().severe("Skipping "+treasureName+" for being invalid");
                        continue;
                    }

                    //VALIDATE DROP LEVEL
                    if(dropLevel < 0)
                    {
                        mcMMO.p.getLogger().info("Herbalism Hylian Luck Treasure named "+treasureName+" in the config has a drop level below 0, is this intentional?");
                        dropLevel = 0;
                    }

                    //VALIDATE DROP SOURCES
                    if(dropsFrom == null || dropsFrom.isEmpty())
                    {
                        mcMMO.p.getLogger().severe("Herbalism Hylian Luck Treasure named "+treasureName+" in the config has no drop targets, which would make it impossible to obtain, is this intentional?");
                        mcMMO.p.getLogger().severe("Skipping "+treasureName+" for being invalid");
                        continue;
                    }

                    /* OPTIONAL PARAMETERS */

                    //Custom Name

                    if(currentTreasure.getNode(CUSTOM_NAME) != null && !currentTreasure.getNode(CUSTOM_NAME).getString().equalsIgnoreCase("ChangeMe"))
                    {
                        customName = currentTreasure.getNode(CUSTOM_NAME).getString();
                    }

                    /*
                     * REGISTER TREASURE
                     */

                    HylianTreasure hylianTreasure = TreasureFactory.makeHylianTreasure(treasureMaterial, amount, xp, dropChance, dropLevel, customName, currentTreasure.getNode(LORE));

                    /*
                     * Add to map
                     */
                    for(String dropBlock : dropsFrom)
                    {
                        if (dropBlock.equals("Bushes")) {
                            addHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.FERN), hylianTreasure);
                            addHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.TALL_GRASS), hylianTreasure);
                            for (Material species : Tag.SAPLINGS.getValues())
                                addHylianTreasure(StringUtils.getFriendlyConfigMaterialString(species), hylianTreasure);

                            addHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.DEAD_BUSH), hylianTreasure);
                            continue;
                        }
                        if (dropBlock.equals("Flowers")) {
                            addHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.POPPY), hylianTreasure);
                            addHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.DANDELION), hylianTreasure);
                            addHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.BLUE_ORCHID), hylianTreasure);
                            addHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.ALLIUM), hylianTreasure);
                            addHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.AZURE_BLUET), hylianTreasure);
                            addHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.ORANGE_TULIP), hylianTreasure);
                            addHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.PINK_TULIP), hylianTreasure);
                            addHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.RED_TULIP), hylianTreasure);
                            addHylianTreasure(StringUtils.getFriendlyConfigMaterialString(Material.WHITE_TULIP), hylianTreasure);
                            continue;
                        }
                        if (dropBlock.equals("Pots")) {
                            for (Material species : Tag.FLOWER_POTS.getValues())
                                addHylianTreasure(StringUtils.getFriendlyConfigMaterialString(species), hylianTreasure);
                            continue;
                        }


                        addHylianTreasure(dropBlock, hylianTreasure);
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

    private void addHylianTreasure(String dropper, HylianTreasure treasure) {
        if (!hylianMap.containsKey(dropper))
            hylianMap.put(dropper, new ArrayList<HylianTreasure>());

        hylianMap.get(dropper).add(treasure);
    }
}
