package com.gmail.nossr50.config.mods;

import com.gmail.nossr50.config.LegacyConfigLoader;
import com.gmail.nossr50.datatypes.mods.CustomTool;
import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.skills.repair.repairables.RepairableFactory;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CustomToolLegacyConfig extends LegacyConfigLoader {
    public List<Material> customAxes = new ArrayList<>();
    public List<Material> customBows = new ArrayList<>();
    public List<Material> customHoes = new ArrayList<>();
    public List<Material> customPickaxes = new ArrayList<>();
    public List<Material> customShovels = new ArrayList<>();
    public List<Material> customSwords = new ArrayList<>();
    public HashMap<Material, CustomTool> customToolMap = new HashMap<>();
    public List<Repairable> repairables = new ArrayList<>();
    private boolean needsUpdate = false;

    protected CustomToolLegacyConfig(String fileName) {
        super("mods", fileName);
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
                mcMMO.p.getLogger().warning("Invalid material name. This item will be skipped. - " + toolName);
                continue;
            }

            boolean repairable = config.getBoolean(toolType + "." + toolName + ".Repairable");
            Material repairMaterial = Material.matchMaterial(config.getString(toolType + "." + toolName + ".Repair_Material", ""));

            if (repairable && (repairMaterial == null)) {
                mcMMO.p.getLogger().warning("Incomplete repair information. This item will be unrepairable. - " + toolName);
                repairable = false;
            }

            if (repairable) {
                String repairItemName = config.getString(toolType + "." + toolName + ".Repair_Material_Pretty_Name");
                int repairMinimumLevel = config.getInt(toolType + "." + toolName + ".Repair_MinimumLevel", 0);
                double repairXpMultiplier = config.getDouble(toolType + "." + toolName + ".Repair_XpMultiplier", 1);

                short durability = toolMaterial.getMaxDurability();

                if (durability == 0) {
                    durability = (short) config.getInt(toolType + "." + toolName + ".Durability", 60);
                }

                repairables.add(RepairableFactory.getRepairable(toolMaterial, repairMaterial, repairItemName, repairMinimumLevel, durability, ItemType.TOOL, MaterialType.OTHER, repairXpMultiplier));
            }

            double multiplier = config.getDouble(toolType + "." + toolName + ".XP_Modifier", 1.0);
            boolean abilityEnabled = config.getBoolean(toolType + "." + toolName + ".Ability_Enabled", true);
            int tier = config.getInt(toolType + "." + toolName + ".Tier", 1);

            CustomTool tool = new CustomTool(tier, abilityEnabled, multiplier);

            materialList.add(toolMaterial);
            customToolMap.put(toolMaterial, tool);
        }
    }
}
