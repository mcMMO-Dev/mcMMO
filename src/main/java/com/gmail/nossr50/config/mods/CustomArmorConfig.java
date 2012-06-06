package com.gmail.nossr50.config.mods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import com.gmail.nossr50.McMMO;
import com.gmail.nossr50.datatypes.mods.CustomItem;
import com.gmail.nossr50.skills.repair.Repairable;
import com.gmail.nossr50.skills.repair.RepairableFactory;

public class CustomArmorConfig extends ModConfigLoader{
    private static CustomArmorConfig instance;

    public static CustomArmorConfig getInstance() {
        if (instance == null) {
            instance = new CustomArmorConfig(McMMO.p);
        }

        return instance;
    }

    private List<Repairable> repairables;

    public List<Integer> customBootIDs = new ArrayList<Integer>();
    public List<Integer> customChestplateIDs = new ArrayList<Integer>();
    public List<Integer> customHelmetIDs = new ArrayList<Integer>();
    public List<Integer> customLeggingIDs = new ArrayList<Integer>();

    public List<Integer> customIDs = new ArrayList<Integer>();
    public List<CustomItem> customArmorList = new ArrayList<CustomItem>();
    public HashMap<Integer, CustomItem> customArmor = new HashMap<Integer, CustomItem>();

    public CustomArmorConfig(McMMO plugin) {
        super(plugin, "armor.yml");
    }

    @Override
    protected void loadKeys() {
        plugin.getLogger().info("Loading mcMMO armor.yml File...");
        repairables = new ArrayList<Repairable>();

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
                repairables.add(RepairableFactory.getRepairable(id, repairID, repairData, repairQuantity, durability));
            }

            armor = new CustomItem(id, durability);

            idList.add(id);
            customIDs.add(id);
            customArmorList.add(armor);
            customArmor.put(id, armor);
        }
    }

    public List<Repairable> getLoadedRepairables() {
        if(repairables == null) return new ArrayList<Repairable>();
        return repairables;
    }
}
