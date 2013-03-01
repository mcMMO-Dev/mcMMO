package com.gmail.nossr50.config.mods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import com.gmail.nossr50.config.ConfigLoader;
import com.gmail.nossr50.datatypes.mods.CustomTool;
import com.gmail.nossr50.skills.repair.Repairable;
import com.gmail.nossr50.skills.repair.RepairableFactory;

public class CustomToolConfig extends ConfigLoader {
    private static CustomToolConfig instance;
    private List<Repairable> repairables;
    public List<Integer> customAxeIDs = new ArrayList<Integer>();
    public List<Integer> customBowIDs = new ArrayList<Integer>();
    public List<Integer> customHoeIDs = new ArrayList<Integer>();
    public List<Integer> customPickaxeIDs = new ArrayList<Integer>();
    public List<Integer> customShovelIDs = new ArrayList<Integer>();
    public List<Integer> customSwordIDs = new ArrayList<Integer>();
    public List<Integer> customIDs = new ArrayList<Integer>();
    public List<CustomTool> customToolList = new ArrayList<CustomTool>();
    public HashMap<Integer, CustomTool> customTools = new HashMap<Integer, CustomTool>();

    private CustomToolConfig() {
        super("ModConfigs", "tools.yml");
        loadKeys();
    }

    public static CustomToolConfig getInstance() {
        if (instance == null) {
            instance = new CustomToolConfig();
        }

        return instance;
    }

    public List<Repairable> getLoadedRepairables() {
        if (repairables == null) {
            return new ArrayList<Repairable>();
        }

        return repairables;
    }

    @Override
    protected void loadKeys() {
        repairables = new ArrayList<Repairable>();

        loadTool("Axes", customAxeIDs);
        loadTool("Bows", customBowIDs);
        loadTool("Hoes", customHoeIDs);
        loadTool("Pickaxes", customPickaxeIDs);
        loadTool("Shovels", customShovelIDs);
        loadTool("Swords", customSwordIDs);
    }

    private void loadTool(String toolType, List<Integer> idList) {
        ConfigurationSection toolSection = config.getConfigurationSection(toolType);

        if (toolSection == null) {
            return;
        }

        Set<String> toolConfigSet = toolSection.getKeys(false);

        for (String toolName : toolConfigSet) {
            int id = config.getInt(toolType + "." + toolName + ".ID", 0);
            double multiplier = config.getDouble(toolType + "." + toolName + ".XP_Modifier", 1.0);
            boolean abilityEnabled = config.getBoolean(toolType + "." + toolName + ".Ability_Enabled", true);
            int tier = config.getInt(toolType + "." + toolName + ".Tier", 1);
            boolean repairable = config.getBoolean(toolType + "." + toolName + ".Repairable");
            int repairID = config.getInt(toolType + "." + toolName + ".Repair_Material_ID", 0);
            byte repairData = (byte) config.getInt(toolType + "." + toolName + ".Repair_Material_Data_Value", 0);
            int repairQuantity = config.getInt(toolType + "." + toolName + ".Repair_Material_Quantity", 0);
            short durability = (short) config.getInt(toolType + "." + toolName + ".Durability", 0);

            if (id == 0) {
                plugin.getLogger().warning("Missing ID. This item will be skipped.");
                continue;
            }

            if (repairable && (repairID == 0 || repairQuantity == 0 || durability == 0)) {
                plugin.getLogger().warning("Incomplete repair information. This item will be unrepairable.");
                repairable = false;
            }

            CustomTool tool;

            if (repairable) {
                repairables.add(RepairableFactory.getRepairable(id, repairID, repairData, repairQuantity, durability));
            }

            tool = new CustomTool(tier, abilityEnabled, multiplier, durability, id);

            idList.add(id);
            customIDs.add(id);
            customToolList.add(tool);
            customTools.put(id, tool);
        }
    }
}
