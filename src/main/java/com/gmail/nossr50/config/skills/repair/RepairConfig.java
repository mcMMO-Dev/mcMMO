package com.gmail.nossr50.config.skills.repair;

import com.gmail.nossr50.config.BukkitConfig;
import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.skills.repair.repairables.RepairableFactory;
import com.gmail.nossr50.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RepairConfig extends BukkitConfig {
    private final HashSet<String> notSupported;
    private List<Repairable> repairables;

    public RepairConfig(String fileName) {
        super(fileName, false);
        notSupported = new HashSet<>();
        loadKeys();
    }

    @Override
    protected void loadKeys() {
        repairables = new ArrayList<>();

        if (!config.isConfigurationSection("Repairables")) {
            mcMMO.p.getLogger().severe("Could not find Repairables section in " + fileName);
            return;
        }

        ConfigurationSection section = config.getConfigurationSection("Repairables");
        Set<String> keys = section.getKeys(false);

        for (String key : keys) {
            if (config.contains("Repairables." + key + ".ItemId")) {
                backup();
                return;
            }

            // Validate all the things!
            List<String> reason = new ArrayList<>();

            // Item Material
            Material itemMaterial = Material.matchMaterial(key);

            if (itemMaterial == null) {
                //mcMMO.p.getLogger().info("No support for repair item "+key+ " in this version of Minecraft, skipping.");
                notSupported.add(key); //Collect names of unsupported items
                continue;
            }

            // Repair Material Type
            MaterialType repairMaterialType = MaterialType.OTHER;
            String repairMaterialTypeString = config.getString("Repairables." + key + ".MaterialType", "OTHER");

            if (!config.contains("Repairables." + key + ".MaterialType") && itemMaterial != null) {
                ItemStack repairItem = new ItemStack(itemMaterial);

                if (ItemUtils.isWoodTool(repairItem)) {
                    repairMaterialType = MaterialType.WOOD;
                } else if (ItemUtils.isStoneTool(repairItem)) {
                    repairMaterialType = MaterialType.STONE;
                } else if (ItemUtils.isStringTool(repairItem)) {
                    repairMaterialType = MaterialType.STRING;
                } else if (ItemUtils.isLeatherArmor(repairItem)) {
                    repairMaterialType = MaterialType.LEATHER;
                } else if (ItemUtils.isIronArmor(repairItem) || ItemUtils.isIronTool(repairItem)) {
                    repairMaterialType = MaterialType.IRON;
                } else if (ItemUtils.isGoldArmor(repairItem) || ItemUtils.isGoldTool(repairItem)) {
                    repairMaterialType = MaterialType.GOLD;
                } else if (ItemUtils.isDiamondArmor(repairItem) || ItemUtils.isDiamondTool(repairItem)) {
                    repairMaterialType = MaterialType.DIAMOND;
                } else if (ItemUtils.isNetheriteArmor(repairItem) || ItemUtils.isNetheriteTool(repairItem)) {
                    repairMaterialType = MaterialType.NETHERITE;
                }
            } else {
                try {
                    repairMaterialType = MaterialType.valueOf(repairMaterialTypeString);
                } catch (IllegalArgumentException ex) {
                    reason.add(key + " has an invalid MaterialType of " + repairMaterialTypeString);
                }
            }

            // Repair Material
            String repairMaterialName = config.getString("Repairables." + key + ".RepairMaterial");
            Material repairMaterial = (repairMaterialName == null ? repairMaterialType.getDefaultMaterial() : Material.matchMaterial(repairMaterialName));

            if (repairMaterial == null) {
                notSupported.add(key); //Collect names of unsupported items
                continue;
            }

            // Maximum Durability
            short maximumDurability = (itemMaterial != null ? itemMaterial.getMaxDurability() : (short) config.getInt("Repairables." + key + ".MaximumDurability"));

            if (maximumDurability <= 0) {
                maximumDurability = (short) config.getInt("Repairables." + key + ".MaximumDurability");
            }

            if (maximumDurability <= 0) {
                reason.add("Maximum durability of " + key + " must be greater than 0!");
            }

            // Item Type
            ItemType repairItemType = ItemType.OTHER;
            String repairItemTypeString = config.getString("Repairables." + key + ".ItemType", "OTHER");

            if (!config.contains("Repairables." + key + ".ItemType") && itemMaterial != null) {
                ItemStack repairItem = new ItemStack(itemMaterial);

                if (ItemUtils.isMinecraftTool(repairItem)) {
                    repairItemType = ItemType.TOOL;
                } else if (ItemUtils.isArmor(repairItem)) {
                    repairItemType = ItemType.ARMOR;
                }
            } else {
                try {
                    repairItemType = ItemType.valueOf(repairItemTypeString);
                } catch (IllegalArgumentException ex) {
                    reason.add(key + " has an invalid ItemType of " + repairItemTypeString);
                }
            }

            int minimumLevel = config.getInt("Repairables." + key + ".MinimumLevel");
            double xpMultiplier = config.getDouble("Repairables." + key + ".XpMultiplier", 1);

            if (minimumLevel < 0) {
                reason.add(key + " has an invalid MinimumLevel of " + minimumLevel);
            }

            // Minimum Quantity
            int minimumQuantity = config.getInt("Repairables." + key + ".MinimumQuantity");

            if (minimumQuantity == 0) {
                minimumQuantity = -1;
            }

            if (noErrorsInRepairable(reason)) {
                Repairable repairable = RepairableFactory.getRepairable(itemMaterial, repairMaterial, null, minimumLevel, maximumDurability, repairItemType, repairMaterialType, xpMultiplier, minimumQuantity);
                repairables.add(repairable);
            }
        }
        //Report unsupported
        StringBuilder stringBuilder = new StringBuilder();

        if (notSupported.size() > 0) {
            stringBuilder.append("mcMMO found the following materials in the Repair config that are not supported by the version of Minecraft running on this server: ");

            for (Iterator<String> iterator = notSupported.iterator(); iterator.hasNext(); ) {
                String unsupportedMaterial = iterator.next();

                if (!iterator.hasNext()) {
                    stringBuilder.append(unsupportedMaterial);
                } else {
                    stringBuilder.append(unsupportedMaterial).append(", ");
                }
            }

            mcMMO.p.getLogger().info(stringBuilder.toString());
            mcMMO.p.getLogger().info("Items using materials that are not supported will simply be skipped.");
        }
    }

    protected List<Repairable> getLoadedRepairables() {
        return repairables == null ? new ArrayList<>() : repairables;
    }

    private boolean noErrorsInRepairable(List<String> issues) {
        for (String issue : issues) {
            mcMMO.p.getLogger().warning(issue);
        }

        return issues.isEmpty();
    }
}
