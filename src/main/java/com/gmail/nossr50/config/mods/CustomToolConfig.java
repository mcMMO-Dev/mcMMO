package com.gmail.nossr50.config.mods;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.mods.CustomTool;
import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.skills.repair.repairables.RepairableFactory;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CustomToolConfig extends Config {
    //TODO: Disabled until modded servers come back
    /*public List<Material> customAxes = new ArrayList<Material>();
    public List<Material> customBows = new ArrayList<Material>();
    public List<Material> customHoes = new ArrayList<Material>();
    public List<Material> customPickaxes = new ArrayList<Material>();
    public List<Material> customShovels = new ArrayList<Material>();
    public List<Material> customSwords = new ArrayList<Material>();
    public HashMap<Material, CustomTool> customToolMap = new HashMap<Material, CustomTool>();
    public List<Repairable> repairables = new ArrayList<Repairable>();
    private boolean needsUpdate = false;

    protected CustomToolConfig(String fileName) {
        //super(McmmoCore.getDataFolderPath().getPath() + "mods", fileName, false);
        super(mcMMO.p.getDataFolder().getPath() + "mods", fileName, false);
        loadKeys();
    }

    @Override
    protected void loadKeys() {
        loadTool("Axes", customAxes);
        loadTool("Bows", customBows);
        loadTool("Hoes", customHoes);
        loadTool("Pickaxes", customPickaxes);
        loadTool("Shovels", customShovels);
        loadTool("Swords", customSwords);

        if (needsUpdate) {
            needsUpdate = false;
            backup();
        }
    }

    private void loadTool(String toolType, List<Material> materialList) {
        if (needsUpdate) {
            return;
        }

        ConfigurationSection toolSection = config.getConfigurationSection(toolType);

        if (toolSection == null) {
            return;
        }

        Set<String> toolConfigSet = toolSection.getKeys(false);

        for (String toolName : toolConfigSet) {
            if (config.contains(toolType + "." + toolName + "." + ".ID")) {
                needsUpdate = true;
                return;
            }

            Material toolMaterial = Material.matchMaterial(toolName);

            if (toolMaterial == null) {
                plugin.getLogger().warning("Invalid material name. This item will be skipped. - " + toolName);
                continue;
            }

            boolean repairable = getBooleanValue(toolType + "." + toolName + ".Repairable");
            Material repairMaterial = Material.matchMaterial(getStringValue(toolType + "." + toolName + ".Repair_Material", ""));

            if (repairable && (repairMaterial == null)) {
                plugin.getLogger().warning("Incomplete repair information. This item will be unrepairable. - " + toolName);
                repairable = false;
            }

            if (repairable) {
                byte repairData = (byte) getIntValue(toolType + "." + toolName + ".Repair_Material_Data_Value", -1);
                int repairQuantity = SkillUtils.getRepairAndSalvageQuantities(new ItemStack(toolMaterial), repairMaterial, repairData);

                if (repairQuantity == 0) {
                    repairQuantity = getIntValue(toolType + "." + toolName + ".Repair_Material_Quantity", 2);
                }

                String repairItemName = getStringValue(toolType + "." + toolName + ".Repair_Material_Pretty_Name");
                int repairMinimumLevel = getIntValue(toolType + "." + toolName + ".Repair_MinimumLevel", 0);
                double repairXpMultiplier = getDoubleValue(toolType + "." + toolName + ".Repair_XpMultiplier", 1);

                short durability = toolMaterial.getMaxDurability();

                if (durability == 0) {
                    durability = (short) getIntValue(toolType + "." + toolName + ".Durability", 60);
                }

                repairables.add(RepairableFactory.getRepairable(toolMaterial, repairMaterial, repairData, repairItemName, repairMinimumLevel, repairQuantity, durability, ItemType.TOOL, MaterialType.OTHER, repairXpMultiplier));
            }

            double multiplier = getDoubleValue(toolType + "." + toolName + ".XP_Modifier", 1.0);
            boolean abilityEnabled = getBooleanValue(toolType + "." + toolName + ".Ability_Enabled", true);
            int tier = getIntValue(toolType + "." + toolName + ".Tier", 1);

            CustomTool tool = new CustomTool(tier, abilityEnabled, multiplier);

            materialList.add(toolMaterial);
            customToolMap.put(toolMaterial, tool);
        }
    }*/
}
