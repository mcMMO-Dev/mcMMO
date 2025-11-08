package com.gmail.nossr50.config.skills.salvage;

import static com.gmail.nossr50.util.ItemUtils.isCopperArmor;
import static com.gmail.nossr50.util.ItemUtils.isCopperTool;
import static com.gmail.nossr50.util.ItemUtils.isDiamondArmor;
import static com.gmail.nossr50.util.ItemUtils.isDiamondTool;
import static com.gmail.nossr50.util.ItemUtils.isGoldArmor;
import static com.gmail.nossr50.util.ItemUtils.isGoldTool;
import static com.gmail.nossr50.util.ItemUtils.isIronArmor;
import static com.gmail.nossr50.util.ItemUtils.isIronTool;
import static com.gmail.nossr50.util.ItemUtils.isLeatherArmor;
import static com.gmail.nossr50.util.ItemUtils.isNetheriteArmor;
import static com.gmail.nossr50.util.ItemUtils.isNetheriteTool;
import static com.gmail.nossr50.util.ItemUtils.isPrismarineTool;
import static com.gmail.nossr50.util.ItemUtils.isStoneTool;
import static com.gmail.nossr50.util.ItemUtils.isStringTool;
import static com.gmail.nossr50.util.ItemUtils.isWoodTool;

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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class SalvageConfig extends BukkitConfig {
    private final HashSet<String> notSupported;
    private Set<Salvageable> salvageables;

    public SalvageConfig(String fileName) {
        super(fileName, false);
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
            mcMMO.p.getLogger().log(
                    Level.INFO,
                    "Fixing incorrect Salvage quantities on Netherite gear, this will only run once...");
            for (String namespacedKey : mcMMO.getMaterialMapStore().getNetheriteArmor()) {
                config.set(
                        "Salvageables." + namespacedKey.toUpperCase(Locale.ENGLISH)
                                + ".MaximumQuantity",
                        4); //TODO: Doesn't make sense to default to 4 for everything
            }

            try {
                config.save(getFile());
                mcMMO.getUpgradeManager()
                        .setUpgradeCompleted(UpgradeType.FIX_NETHERITE_SALVAGE_QUANTITIES);
                LogUtils.debug(mcMMO.p.getLogger(),
                        "Fixed incorrect Salvage quantities for Netherite gear!");
            } catch (IOException e) {
                mcMMO.p.getLogger().log(Level.SEVERE,
                        "Unable to fix Salvage config, please delete the salvage yml file"
                                + " to generate a new one.", e);
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
            final String salvageMaterialTypeString = config.getString(
                    "Salvageables." + key + ".MaterialType", "OTHER");

            if (!config.contains("Salvageables." + key + ".MaterialType")) {
                final ItemStack salvageItem = new ItemStack(itemMaterial);

                if (isWoodTool(salvageItem)) {
                    salvageMaterialType = MaterialType.WOOD;
                } else if (isStoneTool(salvageItem)) {
                    salvageMaterialType = MaterialType.STONE;
                } else if (isStringTool(salvageItem)) {
                    salvageMaterialType = MaterialType.STRING;
                } else if (isPrismarineTool(salvageItem)) {
                    salvageMaterialType = MaterialType.PRISMARINE;
                } else if (isLeatherArmor(salvageItem)) {
                    salvageMaterialType = MaterialType.LEATHER;
                } else if (isIronArmor(salvageItem) || isIronTool(salvageItem)) {
                    salvageMaterialType = MaterialType.IRON;
                } else if (isGoldArmor(salvageItem) || isGoldTool(salvageItem)) {
                    salvageMaterialType = MaterialType.GOLD;
                } else if (isDiamondArmor(salvageItem) || isDiamondTool(salvageItem)) {
                    salvageMaterialType = MaterialType.DIAMOND;
                } else if (isNetheriteTool(salvageItem) || isNetheriteArmor(salvageItem)) {
                    salvageMaterialType = MaterialType.NETHERITE;
                } else if (isCopperTool(salvageItem) || isCopperArmor(salvageItem)) {
                    salvageMaterialType = MaterialType.COPPER;
                }
            } else {
                try {
                    salvageMaterialType = MaterialType.valueOf(
                            salvageMaterialTypeString.replace(" ", "_")
                                    .toUpperCase(Locale.ENGLISH));
                } catch (IllegalArgumentException ex) {
                    reason.add(key + " has an invalid MaterialType of " + salvageMaterialTypeString);
                }
            }

            // Salvage Material
            String salvageMaterialName = config.getString("Salvageables." + key + ".SalvageMaterial");
            Material salvageMaterial = (salvageMaterialName == null
                    ? salvageMaterialType.getDefaultMaterial()
                    : Material.matchMaterial(salvageMaterialName));

            if (salvageMaterial == null) {
                notSupported.add(key);
                continue;
            }

            // Maximum Durability
            short maximumDurability = itemMaterial.getMaxDurability();

            // Item Type
            ItemType salvageItemType = ItemType.OTHER;
            String salvageItemTypeString = config.getString("Salvageables." + key + ".ItemType",
                    "OTHER");

            if (!config.contains("Salvageables." + key + ".ItemType")) {
                ItemStack salvageItem = new ItemStack(itemMaterial);

                if (ItemUtils.isMinecraftTool(salvageItem)) {
                    salvageItemType = ItemType.TOOL;
                } else if (ItemUtils.isArmor(salvageItem)) {
                    salvageItemType = ItemType.ARMOR;
                }
            } else {
                try {
                    salvageItemType = ItemType.valueOf(
                            salvageItemTypeString.replace(" ", "_").toUpperCase(Locale.ENGLISH));
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
            int maximumQuantity = SkillUtils.getRepairAndSalvageQuantities(
                                itemMaterial,
                                salvageMaterial);

            if (maximumQuantity <= 0) {
                maximumQuantity = config.getInt("Salvageables." + key + ".MaximumQuantity", 1);
            }

            int configMaximumQuantity = config.getInt("Salvageables." + key + ".MaximumQuantity",
                    -1);

            if (configMaximumQuantity > 0) {
                maximumQuantity = configMaximumQuantity;
            }

            if (maximumQuantity <= 0) {
                reason.add("Maximum quantity of " + key + " must be greater than 0!");
            }

            if (noErrorsInSalvageable(reason)) {
                try {
                    final Salvageable salvageable = SalvageableFactory.getSalvageable(
                            itemMaterial, salvageMaterial, minimumLevel,
                            maximumQuantity, maximumDurability, salvageItemType,
                            salvageMaterialType,
                            xpMultiplier);
                    salvageables.add(salvageable);
                } catch (Exception e) {
                    mcMMO.p.getLogger().log(Level.SEVERE,
                            "Error loading salvageable from config entry: " + key, e);
                }
            }
        }
        //Report unsupported
        StringBuilder stringBuilder = new StringBuilder();

        if (!notSupported.isEmpty()) {
            stringBuilder.append(
                    "mcMMO found the following materials in the Salvage config that are not supported by the version of Minecraft running on this server: ");

            for (Iterator<String> iterator = notSupported.iterator(); iterator.hasNext(); ) {
                String unsupportedMaterial = iterator.next();

                if (!iterator.hasNext()) {
                    stringBuilder.append(unsupportedMaterial);
                } else {
                    stringBuilder.append(unsupportedMaterial).append(", ");
                }
            }

            LogUtils.debug(mcMMO.p.getLogger(), stringBuilder.toString());
            LogUtils.debug(mcMMO.p.getLogger(),
                    "Items using materials that are not supported will simply be skipped.");
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
