package com.gmail.nossr50.config.mods;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.ConfigLoader;
import com.gmail.nossr50.datatypes.mods.CustomTool;

public class LoadCustomTools extends ConfigLoader {
    private static LoadCustomTools instance;

    public static LoadCustomTools getInstance() {
        if (instance == null) {
            instance = new LoadCustomTools(mcMMO.p);
        }

        return instance;
    }

    public List<CustomTool> customAxes = new ArrayList<CustomTool>();
    public List<CustomTool> customBows = new ArrayList<CustomTool>();
    public List<CustomTool> customHoes = new ArrayList<CustomTool>();
    public List<CustomTool> customPickaxes = new ArrayList<CustomTool>();
    public List<CustomTool> customShovels = new ArrayList<CustomTool>();
    public List<CustomTool> customSwords = new ArrayList<CustomTool>();

    public List<Integer> customAxeIDs = new ArrayList<Integer>();
    public List<Integer> customBowIDs = new ArrayList<Integer>();
    public List<Integer> customHoeIDs = new ArrayList<Integer>();
    public List<Integer> customPickaxeIDs = new ArrayList<Integer>();
    public List<Integer> customShovelIDs = new ArrayList<Integer>();
    public List<Integer> customSwordIDs = new ArrayList<Integer>();

    private LoadCustomTools(mcMMO plugin) {
        super(plugin, "ModConfigs" + File.separator + "tools.yml");
        config = plugin.getToolsConfig();
    }

    @Override
    public void load() {
        if (!configFile.exists()) {
            dataFolder.mkdir();
            plugin.saveToolsConfig();
        }

        addDefaults();
        loadKeys();
    }

    @Override
    protected void loadKeys() {
        plugin.getLogger().info("Loading mcMMO tools.yml File...");

        loadTool("Axes", customAxes, customAxeIDs);
        loadTool("Bows", customBows, customBowIDs);
        loadTool("Hoes", customHoes, customHoeIDs);
        loadTool("Pickaxes", customPickaxes, customPickaxeIDs);
        loadTool("Shovels", customShovels, customShovelIDs);
        loadTool("Swords", customSwords, customSwordIDs);
    }

    private void loadTool(String toolType, List<CustomTool> toolList, List<Integer> idList) {
        ConfigurationSection toolSection = config.getConfigurationSection(toolType);
        Set<String> toolConfigSet = toolSection.getKeys(false);
        Iterator<String> iterator = toolConfigSet.iterator();

        while (iterator.hasNext()) {
            String toolName = iterator.next();

            int id = config.getInt(toolType + "." + toolName + ".ID");
            double multiplier = config.getDouble(toolType + "." + toolName + ".XP_Modifier", 1.0);
            boolean repairable = config.getBoolean(toolType + "." + toolName + ".Repairable");
            int repairID = config.getInt(toolType + "." + toolName + ".Repair_Material_ID");
            byte repairData = (byte) config.getInt(toolType + "." + toolName + ".Repair_Material_Data_Value");
            short durability = (short) config.getInt(toolType + "." + toolName + ".Durability");

            if (id == 0) {
                plugin.getLogger().warning("Missing ID. This item will be skipped.");
                continue;
            }

            if (repairable && (repairID == 0 || durability == 0)) {
                plugin.getLogger().warning("Incomplete repair information. This item will be unrepairable.");
                repairable = false;
            }

            CustomTool tool;

            if (repairable) {
                ItemStack repairMaterial = new ItemStack(repairID, 1, (short) 0, repairData);
                tool = new CustomTool(durability, repairMaterial, repairable, multiplier, id);
            }
            else {
                tool = new CustomTool(durability, null, repairable, multiplier, id);
            }

            toolList.add(tool);
            idList.add(id);
        }
    }
}
