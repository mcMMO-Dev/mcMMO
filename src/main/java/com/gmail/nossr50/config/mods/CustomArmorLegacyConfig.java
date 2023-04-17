package com.gmail.nossr50.config.mods;

import com.gmail.nossr50.config.LegacyConfigLoader;
import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.skills.repair.repairables.RepairableFactory;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CustomArmorLegacyConfig extends LegacyConfigLoader {
    public List<Material> customBoots = new ArrayList<>();
    public List<Material> customChestplates = new ArrayList<>();
    public List<Material> customHelmets = new ArrayList<>();
    public List<Material> customLeggings = new ArrayList<>();
    public List<Repairable> repairables = new ArrayList<>();
    private boolean needsUpdate = false;

    protected CustomArmorLegacyConfig(String fileName) {
        super("mods", fileName);
        loadKeys();
    }

    @Override
    protected void loadKeys() {
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
                mcMMO.p.getLogger().warning("Invalid material name. This item will be skipped. - " + armorName);
                continue;
            }

            boolean repairable = config.getBoolean(armorType + "." + armorName + ".Repairable");
            Material repairMaterial = Material.matchMaterial(config.getString(armorType + "." + armorName + ".Repair_Material", ""));

            if (repairable && (repairMaterial == null)) {
                mcMMO.p.getLogger().warning("Incomplete repair information. This item will be unrepairable. - " + armorName);
                repairable = false;
            }

            if (repairable) {
                String repairItemName = config.getString(armorType + "." + armorName + ".Repair_Material_Pretty_Name");
                int repairMinimumLevel = config.getInt(armorType + "." + armorName + ".Repair_MinimumLevel", 0);
                double repairXpMultiplier = config.getDouble(armorType + "." + armorName + ".Repair_XpMultiplier", 1);

                short durability = armorMaterial.getMaxDurability();

                if (durability == 0) {
                    durability = (short) config.getInt(armorType + "." + armorName + ".Durability", 70);
                }

                repairables.add(RepairableFactory.getRepairable(armorMaterial, repairMaterial, repairItemName, repairMinimumLevel, durability, ItemType.ARMOR, MaterialType.OTHER, repairXpMultiplier));
            }

            materialList.add(armorMaterial);
        }
    }
}
