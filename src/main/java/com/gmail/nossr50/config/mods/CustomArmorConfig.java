package com.gmail.nossr50.config.mods;

import com.gmail.nossr50.config.ConfigCollection;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.skills.repair.repairables.RepairableFactory;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CustomArmorConfig extends ConfigCollection {
    public List<Material> customBoots = new ArrayList<Material>();
    public List<Material> customChestplates = new ArrayList<Material>();
    public List<Material> customHelmets = new ArrayList<Material>();
    public List<Material> customLeggings = new ArrayList<Material>();
    public List<Repairable> repairables = new ArrayList<Repairable>();
    private boolean needsUpdate = false;

    protected CustomArmorConfig(String fileName) {
        //super(McmmoCore.getDataFolderPath().getPath() + "mods", fileName, false);
        super(mcMMO.p.getDataFolder().getPath() + "mods", fileName, false);        register();
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

    @Override
    public void register() {
        loadArmor("Boots", customBoots);
        loadArmor("Chestplates", customChestplates);
        loadArmor("Helmets", customHelmets);
        loadArmor("Leggings", customLeggings);

        if (needsUpdate) {
            needsUpdate = false;
            backup();
        }
    }

    private void loadArmor(String armorType, List<Material> materialList) {
        if (needsUpdate) {
            return;
        }

        ConfigurationSection armorSection = config.getConfigurationSection(armorType);

        if (armorSection == null) {
            return;
        }

        Set<String> armorConfigSet = armorSection.getKeys(false);

        for (String armorName : armorConfigSet) {
            if (config.contains(armorType + "." + armorName + "." + ".ID")) {
                needsUpdate = true;
                return;
            }

            Material armorMaterial = Material.matchMaterial(armorName);

            if (armorMaterial == null) {
                plugin.getLogger().warning("Invalid material name. This item will be skipped. - " + armorName);
                continue;
            }

            boolean repairable = getBooleanValue(armorType + "." + armorName + ".Repairable");
            Material repairMaterial = Material.matchMaterial(getStringValue(armorType + "." + armorName + ".Repair_Material", ""));

            if (repairable && (repairMaterial == null)) {
                plugin.getLogger().warning("Incomplete repair information. This item will be unrepairable. - " + armorName);
                repairable = false;
            }

            if (repairable) {
                byte repairData = (byte) getIntValue(armorType + "." + armorName + ".Repair_Material_Data_Value", -1);
                int repairQuantity = SkillUtils.getRepairAndSalvageQuantities(new ItemStack(armorMaterial), repairMaterial, repairData);

                if (repairQuantity == 0) {
                    repairQuantity = getIntValue(armorType + "." + armorName + ".Repair_Material_Quantity", 2);
                }

                String repairItemName = getStringValue(armorType + "." + armorName + ".Repair_Material_Pretty_Name");
                int repairMinimumLevel = getIntValue(armorType + "." + armorName + ".Repair_MinimumLevel", 0);
                double repairXpMultiplier = getDoubleValue(armorType + "." + armorName + ".Repair_XpMultiplier", 1);

                short durability = armorMaterial.getMaxDurability();

                if (durability == 0) {
                    durability = (short) getIntValue(armorType + "." + armorName + ".Durability", 70);
                }

                repairables.add(RepairableFactory.getRepairable(armorMaterial, repairMaterial, repairData, repairItemName, repairMinimumLevel, repairQuantity, durability, ConfigItemCategory.ARMOR, MaterialType.OTHER, repairXpMultiplier));
            }

            materialList.add(armorMaterial);
        }
    }
}
