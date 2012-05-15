package com.gmail.nossr50.config.mods;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.mods.CustomTool;

public class LoadCustomTools extends ModConfigLoader {
    private static LoadCustomTools instance;

    public static LoadCustomTools getInstance() {
        if (instance == null) {
            instance = new LoadCustomTools(mcMMO.p);
        }

        return instance;
    }

    public List<Integer> customAxeIDs = new ArrayList<Integer>();
    public List<Integer> customBowIDs = new ArrayList<Integer>();
    public List<Integer> customHoeIDs = new ArrayList<Integer>();
    public List<Integer> customPickaxeIDs = new ArrayList<Integer>();
    public List<Integer> customShovelIDs = new ArrayList<Integer>();
    public List<Integer> customSwordIDs = new ArrayList<Integer>();

    private LoadCustomTools(mcMMO plugin) {
        super(plugin, "tools.yml");
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

        loadTool("Axes", customAxeIDs);
        loadTool("Bows", customBowIDs);
        loadTool("Hoes", customHoeIDs);
        loadTool("Pickaxes", customPickaxeIDs);
        loadTool("Shovels", customShovelIDs);
        loadTool("Swords", customSwordIDs);
    }

    private void loadTool(String toolType, List<Integer> idList) {
        ConfigurationSection toolSection = config.getConfigurationSection(toolType);
        Set<String> toolConfigSet = toolSection.getKeys(false);
        Iterator<String> iterator = toolConfigSet.iterator();

        while (iterator.hasNext()) {
            String toolName = iterator.next();

            int id = config.getInt(toolType + "." + toolName + ".ID", 0);
            double multiplier = config.getDouble(toolType + "." + toolName + ".XP_Modifier", 1.0);
            boolean abilityEnabled = config.getBoolean(toolType + "." + toolName + ".Ability_Enabled", true);
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
                ItemStack repairMaterial = new ItemStack(repairID, 1, (short) 0, repairData);
                tool = new CustomTool(durability, repairMaterial, repairQuantity, repairable, abilityEnabled, multiplier, id);
            }
            else {
                tool = new CustomTool(durability, null, 0, repairable, abilityEnabled, multiplier, id);
            }

            idList.add(id);
            customIDs.add(id);
            customItems.add(tool);
        }
    }
}
