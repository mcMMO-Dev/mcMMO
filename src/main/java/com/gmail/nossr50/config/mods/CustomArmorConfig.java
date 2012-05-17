package com.gmail.nossr50.config.mods;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.mods.CustomItem;

public class CustomArmorConfig extends ModConfigLoader{
    private static CustomArmorConfig instance;

    public static CustomArmorConfig getInstance() {
        if (instance == null) {
            instance = new CustomArmorConfig(mcMMO.p);
        }

        return instance;
    }

    public List<Integer> customBootIDs = new ArrayList<Integer>();
    public List<Integer> customChestplateIDs = new ArrayList<Integer>();
    public List<Integer> customHelmetIDs = new ArrayList<Integer>();
    public List<Integer> customLeggingIDs = new ArrayList<Integer>();

    public List<Integer> customIDs = new ArrayList<Integer>();
    public List<CustomItem> customItems = new ArrayList<CustomItem>();

    public CustomArmorConfig(mcMMO plugin) {
        super(plugin, "armor.yml");
        config = plugin.getArmorConfig();
    }

    @Override
    public void load() {
        if (!configFile.exists()) {
            dataFolder.mkdir();
            plugin.saveArmorConfig();
        }

        addDefaults();
        loadKeys();
    }

    @Override
    protected void loadKeys() {
        plugin.getLogger().info("Loading mcMMO armor.yml File...");

        loadArmor("Boots", customBootIDs);
        loadArmor("Chestplates", customChestplateIDs);
        loadArmor("Helmets", customHelmetIDs);
        loadArmor("Leggings", customLeggingIDs);
    }

    private void loadArmor(String armorType, List<Integer> idList) {
        ConfigurationSection armorSection = config.getConfigurationSection(armorType);
        Set<String> armorConfigSet = armorSection.getKeys(false);
        Iterator<String> iterator = armorConfigSet.iterator();

        while (iterator.hasNext()) {
            String armorName = iterator.next();

            int id = config.getInt(armorType + "." + armorName + ".ID", 0);
            boolean repairable = config.getBoolean(armorType + "." + armorName + ".Repairable");
            int repairID = config.getInt(armorType + "." + armorName + ".Repair_Material_ID", 0);
            byte repairData = (byte) config.getInt(armorType + "." + armorName + ".Repair_Material_Data_Value", 0);
            int repairQuantity = config.getInt(armorType + "." + armorName + ".Repair_Material_Quantity", 0);
            short durability = (short) config.getInt(armorType + "." + armorName + ".Durability", 0);

            if (id == 0) {
                plugin.getLogger().warning("Missing ID. This item will be skipped.");
                continue;
            }

            if (repairable && (repairID == 0 || repairQuantity == 0 || durability == 0)) {
                plugin.getLogger().warning("Incomplete repair information. This item will be unrepairable.");
                repairable = false;
            }

            CustomItem armor;

            if (repairable) {
                ItemStack repairMaterial = new ItemStack(repairID, 1, (short) 0, repairData);
                armor = new CustomItem(durability, repairMaterial, repairQuantity, repairable, id);
            }
            else {
                armor = new CustomItem(durability, null, 0, repairable, id);
            }

            idList.add(id);
            customIDs.add(id);
            customItems.add(armor);
        }
    }
}
