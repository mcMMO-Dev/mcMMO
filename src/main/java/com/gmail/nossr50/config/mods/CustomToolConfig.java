package com.gmail.nossr50.config.mods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.ConfigLoader;
import com.gmail.nossr50.datatypes.mods.CustomTool;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.repair.RepairItemType;
import com.gmail.nossr50.skills.repair.RepairMaterialType;
import com.gmail.nossr50.skills.repair.Repairable;
import com.gmail.nossr50.skills.repair.RepairableFactory;

public class CustomToolConfig extends ConfigLoader {
    private static CustomToolConfig instance;
    private List<Repairable> repairables;

    private List<Material> customAxes     = new ArrayList<Material>();
    private List<Material> customBows     = new ArrayList<Material>();
    private List<Material> customHoes     = new ArrayList<Material>();
    private List<Material> customPickaxes = new ArrayList<Material>();
    private List<Material> customShovels  = new ArrayList<Material>();
    private List<Material> customSwords   = new ArrayList<Material>();

    private HashMap<Material, CustomTool> customToolMap = new HashMap<Material, CustomTool>();

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

        loadTool("Axes", customAxes);
        loadTool("Bows", customBows);
        loadTool("Hoes", customHoes);
        loadTool("Pickaxes", customPickaxes);
        loadTool("Shovels", customShovels);
        loadTool("Swords", customSwords);
    }

    private void loadTool(String toolType, List<Material> materialList) {
        ConfigurationSection toolSection = config.getConfigurationSection(toolType);

        if (toolSection == null) {
            return;
        }

        Set<String> toolConfigSet = toolSection.getKeys(false);

        for (String toolName : toolConfigSet) {
            Material toolMaterial = Material.matchMaterial(toolName);

            if (toolMaterial == null) {
                plugin.getLogger().warning("Invalid material name. This item will be skipped. - " + toolName);
                continue;
            }

            boolean repairable = config.getBoolean(toolType + "." + toolName + ".Repairable");
            Material repairMaterial = Material.matchMaterial(config.getString(toolType + "." + toolName + ".Repair_Material", ""));

            if (repairMaterial == null) {
                plugin.getLogger().warning("Incomplete repair information. This item will be unrepairable. - " + toolName);
                repairable = false;
            }

            if (repairable) {
                byte repairData = (byte) config.getInt(toolType + "." + toolName + ".Repair_Material_Data_Value", -1);
                int repairQuantity = Repair.getRepairAndSalvageQuantities(new ItemStack(toolMaterial), repairMaterial, repairData);

                if (repairQuantity == 0) {
                    repairQuantity = config.getInt(toolType + "." + toolName + ".Repair_Material_Data_Quantity", 2);
                }

                short durability = toolMaterial.getMaxDurability();

                if (durability == 0) {
                    durability = (short) config.getInt(toolType + "." + toolName + ".Durability", 60);
                }

                repairables.add(RepairableFactory.getRepairable(toolMaterial, repairMaterial, repairData, 0, repairQuantity, durability, RepairItemType.TOOL, RepairMaterialType.OTHER, 1.0));
            }

            double multiplier = config.getDouble(toolType + "." + toolName + ".XP_Modifier", 1.0);
            boolean abilityEnabled = config.getBoolean(toolType + "." + toolName + ".Ability_Enabled", true);
            int tier = config.getInt(toolType + "." + toolName + ".Tier", 1);

            CustomTool tool = new CustomTool(tier, abilityEnabled, multiplier);

            materialList.add(toolMaterial);
            customToolMap.put(toolMaterial, tool);
        }
    }

    public boolean isCustomAxe(Material material) {
        return customAxes.contains(material);
    }

    public boolean isCustomBow(Material material) {
        return customBows.contains(material);
    }

    public boolean isCustomHoe(Material material) {
        return customHoes.contains(material);
    }

    public boolean isCustomPickaxe(Material material) {
        return customPickaxes.contains(material);
    }

    public boolean isCustomShovel(Material material) {
        return customShovels.contains(material);
    }

    public boolean isCustomSword(Material material) {
        return customSwords.contains(material);
    }

    public boolean isCustomTool(Material material) {
        return customToolMap.containsKey(material);
    }

    public CustomTool getCustomTool(Material material) {
        return customToolMap.get(material);
    }
}
