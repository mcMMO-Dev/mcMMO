package com.gmail.nossr50.config.skills.salvage;

import com.gmail.nossr50.config.BukkitConfig;
import com.gmail.nossr50.datatypes.database.UpgradeType;
import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import com.gmail.nossr50.skills.salvage.salvageables.SalvageableFactory;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.LogUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class SalvageConfig extends BukkitConfig {
    private final HashSet<String> notSupported;
    private Set<Salvageable> salvageables;

    public SalvageConfig(String fileName, boolean copyDefaults) {
        super(fileName, copyDefaults);
        notSupported = new HashSet<>();
        loadKeys();
    }

    @Override
    protected void loadKeys() {
        salvageables = new HashSet<>();

        if (!config.isConfigurationSection("Salvageables")) {
            mcMMO.p.getLogger().severe("Could not find Salvageables section in " + fileName);
            return;
        }

        ConfigurationSection section = config.getConfigurationSection("Salvageables");
        Set<String> keys = section.getKeys(false);

        //Original version of 1.16 support had maximum quantities that were bad, this fixes it
        if (mcMMO.getUpgradeManager().shouldUpgrade(UpgradeType.FIX_NETHERITE_SALVAGE_QUANTITIES)) {
            mcMMO.p.getLogger().log(Level.INFO, "Fixing incorrect Salvage quantities on Netherite gear, this will only run once...");
            for (String namespacedkey : mcMMO.getMaterialMapStore().getNetheriteArmor()) {
                config.set("Salvageables." + namespacedkey.toUpperCase(Locale.ENGLISH) + ".MaximumQuantity", 4); //TODO: Doesn't make sense to default to 4 for everything
            }

            try {
                config.save(getFile());
                mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.FIX_NETHERITE_SALVAGE_QUANTITIES);
                LogUtils.debug(mcMMO.p.getLogger(), "Fixed incorrect Salvage quantities for Netherite gear!");
            } catch (IOException e) {
                LogUtils.debug(mcMMO.p.getLogger(), "Unable to fix Salvage config, please delete the salvage yml file to generate a new one.");
                e.printStackTrace();
            }
        }

        for (String key : keys) {
            // Validate all the things!
            List<String> reason = new ArrayList<>();

            // Item Material
            Material itemMaterial = Material.matchMaterial(key);

            if (itemMaterial == null) {
                notSupported.add(key);
                continue;
            }

            // Salvage Material Type
            MaterialType salvageMaterialType = MaterialType.OTHER;
            String salvageMaterialTypeString = config.getString("Salvageables." + key + ".MaterialType", "OTHER");

            if (!config.contains("Salvageables." + key + ".MaterialType") && itemMaterial != null) {
                ItemStack salvageItem = new ItemStack(itemMaterial);

                if (ItemUtils.isWoodTool(salvageItem)) {
                    salvageMaterialType = MaterialType.WOOD;
                } else if (ItemUtils.isStoneTool(salvageItem)) {
                    salvageMaterialType = MaterialType.STONE;
                } else if (ItemUtils.isStringTool(salvageItem)) {
                    salvageMaterialType = MaterialType.STRING;
                } else if (ItemUtils.isPrismarineTool(salvageItem)) {
                    salvageMaterialType = MaterialType.PRISMARINE;
                } else if (ItemUtils.isLeatherArmor(salvageItem)) {
                    salvageMaterialType = MaterialType.LEATHER;
                } else if (ItemUtils.isIronArmor(salvageItem) || ItemUtils.isIronTool(salvageItem)) {
                    salvageMaterialType = MaterialType.IRON;
                } else if (ItemUtils.isGoldArmor(salvageItem) || ItemUtils.isGoldTool(salvageItem)) {
                    salvageMaterialType = MaterialType.GOLD;
                } else if (ItemUtils.isDiamondArmor(salvageItem) || ItemUtils.isDiamondTool(salvageItem)) {
                    salvageMaterialType = MaterialType.DIAMOND;
                } else if (ItemUtils.isNetheriteTool(salvageItem) || ItemUtils.isNetheriteArmor(salvageItem)) {
                    salvageMaterialType = MaterialType.NETHERITE;
                }
            } else {
                try {
                    salvageMaterialType = MaterialType.valueOf(salvageMaterialTypeString.replace(" ", "_").toUpperCase(Locale.ENGLISH));
                } catch (IllegalArgumentException ex) {
                    reason.add(key + " has an invalid MaterialType of " + salvageMaterialTypeString);
                }
            }

            // Salvage Material
            String salvageMaterialName = config.getString("Salvageables." + key + ".SalvageMaterial");
            Material salvageMaterial = (salvageMaterialName == null ? salvageMaterialType.getDefaultMaterial() : Material.matchMaterial(salvageMaterialName));

            if (salvageMaterial == null) {
                notSupported.add(key);
                continue;
            }

            // Maximum Durability
            short maximumDurability = (itemMaterial != null ? itemMaterial.getMaxDurability() : (short) config.getInt("Salvageables." + key + ".MaximumDurability"));

            // Item Type
            ItemType salvageItemType = ItemType.OTHER;
            String salvageItemTypeString = config.getString("Salvageables." + key + ".ItemType", "OTHER");

            if (!config.contains("Salvageables." + key + ".ItemType") && itemMaterial != null) {
                ItemStack salvageItem = new ItemStack(itemMaterial);

                if (ItemUtils.isMinecraftTool(salvageItem)) {
                    salvageItemType = ItemType.TOOL;
                } else if (ItemUtils.isArmor(salvageItem)) {
                    salvageItemType = ItemType.ARMOR;
                }
            } else {
                try {
                    salvageItemType = ItemType.valueOf(salvageItemTypeString.replace(" ", "_").toUpperCase(Locale.ENGLISH));
                } catch (IllegalArgumentException ex) {
                    reason.add(key + " has an invalid ItemType of " + salvageItemTypeString);
                }
            }

            int minimumLevel = config.getInt("Salvageables." + key + ".MinimumLevel");
            double xpMultiplier = config.getDouble("Salvageables." + key + ".XpMultiplier", 1);

            if (minimumLevel < 0) {
                reason.add(key + " has an invalid MinimumLevel of " + minimumLevel);
            }

            // Maximum Quantity
            int maximumQuantity = itemMaterial != null
                    ? SkillUtils.getRepairAndSalvageQuantities(itemMaterial, salvageMaterial)
                    : config.getInt("Salvageables." + key + ".MaximumQuantity", 1);

            if (maximumQuantity <= 0 && itemMaterial != null) {
                maximumQuantity = config.getInt("Salvageables." + key + ".MaximumQuantity", 1);
            }

            int configMaximumQuantity = config.getInt("Salvageables." + key + ".MaximumQuantity", -1);

            if (configMaximumQuantity > 0) {
                maximumQuantity = configMaximumQuantity;
            }

            if (maximumQuantity <= 0) {
                reason.add("Maximum quantity of " + key + " must be greater than 0!");
            }

            if (noErrorsInSalvageable(reason)) {
                Salvageable salvageable = SalvageableFactory.getSalvageable(itemMaterial, salvageMaterial, minimumLevel, maximumQuantity, maximumDurability, salvageItemType, salvageMaterialType, xpMultiplier);
                salvageables.add(salvageable);
            }
        }
        //Report unsupported
        StringBuilder stringBuilder = new StringBuilder();

        if (notSupported.size() > 0) {
            stringBuilder.append("mcMMO found the following materials in the Salvage config that are not supported by the version of Minecraft running on this server: ");

            for (Iterator<String> iterator = notSupported.iterator(); iterator.hasNext(); ) {
                String unsupportedMaterial = iterator.next();

                if (!iterator.hasNext()) {
                    stringBuilder.append(unsupportedMaterial);
                } else {
                    stringBuilder.append(unsupportedMaterial).append(", ");
                }
            }

            LogUtils.debug(mcMMO.p.getLogger(), stringBuilder.toString());
            LogUtils.debug(mcMMO.p.getLogger(), "Items using materials that are not supported will simply be skipped.");
        }
    }

    protected Collection<Salvageable> getLoadedSalvageables() {
        return salvageables == null ? new HashSet<>() : salvageables;
    }

    private boolean noErrorsInSalvageable(List<String> issues) {
        if (!issues.isEmpty()) {
            mcMMO.p.getLogger().warning("Errors have been found in: " + fileName);
            mcMMO.p.getLogger().warning("The following issues were found:");
        }

        for (String issue : issues) {
            mcMMO.p.getLogger().warning(issue);
        }

        return issues.isEmpty();
    }
}
