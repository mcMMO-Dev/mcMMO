package com.gmail.nossr50.config.treasure;

import com.gmail.nossr50.util.LogUtils;
import com.gmail.nossr50.util.PotionUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Loading logic shared by {@link TreasureConfig} and {@link FishingTreasureConfig}, which read
 * differently-shaped sections from different files but parse, build, and report entries the same
 * way. Everything here is static and takes the config, file name, and logger as parameters so the
 * two configs cannot drift apart and the pieces used by entry classification stay unit-testable.
 */
final class TreasureEntryLoader {

    private TreasureEntryLoader() {
    }

    /**
     * Reads the legacy data value: from the {@code NAME|data} key suffix when present, otherwise
     * from the entry's {@code Data} field.
     *
     * @throws NumberFormatException when the key suffix does not parse as a short
     */
    static short parseData(final @NotNull String treasureName,
            final @NotNull YamlConfiguration config, final @NotNull String base) {
        final String[] parts = treasureName.split("[|]");
        return (parts.length == 2)
                ? Short.parseShort(parts[1])
                : (short) config.getInt(base + ".Data");
    }

    static void logInvalidTreasure(final @NotNull Logger logger, final @NotNull String fileName,
            final @NotNull String treasureName, final @NotNull String detail) {
        logger.warning("Skipping invalid treasure '" + treasureName + "' in " + fileName + ": "
                + detail);
    }

    /**
     * Whether the entry's {@code PotionData.PotionType} resolves to a potion type in this
     * Minecraft version. An unresolvable potion type is treated like an unknown material —
     * incompatible rather than misconfigured — because newer game versions add new potion types
     * and shipped configs may reference them; admins on older versions must not be warned over it.
     */
    static boolean isPotionTypeResolvable(final @NotNull YamlConfiguration config,
            final @NotNull String base) {
        final String potionTypeStr = config.getString(base + ".PotionData.PotionType", "WATER");
        final boolean extended = config.getBoolean(base + ".PotionData.Extended", false);
        final boolean upgraded = config.getBoolean(base + ".PotionData.Upgraded", false);
        return PotionUtil.matchPotionType(potionTypeStr, upgraded, extended) != null;
    }

    /**
     * Debug-logs the entries a section skipped as incompatible, in one line. Deliberately debug
     * level: shipped default configs contain content from newer Minecraft versions, so on older
     * servers these skips are normal and must not read as errors.
     */
    static void logIncompatibleSummary(final @NotNull Logger logger,
            final @NotNull String fileName, final @NotNull String type,
            final @NotNull List<String> skippedNames) {
        if (skippedNames.isEmpty()) {
            return;
        }

        LogUtils.debug(logger, "Treasures in " + fileName + " section '" + type
                + "' skipped because this Minecraft version does not support them: "
                + String.join(", ", skippedNames));
    }

    /**
     * Builds a potion treasure item, or returns null (after logging a warning) when the potion
     * metadata or potion type cannot be resolved.
     */
    static @Nullable ItemStack buildPotionItem(final @NotNull YamlConfiguration config,
            final @NotNull String type, final @NotNull String treasureName,
            final @NotNull Material material, final int amount, final short data,
            final @NotNull String fileName, final @NotNull Logger logger) {
        final ItemStack item = new ItemStack(material, amount, data);
        final PotionMeta potionMeta = (PotionMeta) item.getItemMeta();

        if (potionMeta == null) {
            logger.warning("Skipping treasure '" + treasureName + "' in " + fileName
                    + ": could not read potion metadata");
            return null;
        }

        final String base = type + "." + treasureName;
        final String potionTypeStr = config.getString(base + ".PotionData.PotionType", "WATER");
        final boolean extended = config.getBoolean(base + ".PotionData.Extended", false);
        final boolean upgraded = config.getBoolean(base + ".PotionData.Upgraded", false);
        final PotionType potionType = PotionUtil.matchPotionType(potionTypeStr, upgraded, extended);

        if (potionType == null) {
            logger.warning("Skipping treasure '" + treasureName + "' in " + fileName
                    + ": unknown potion type '" + potionTypeStr + "'");
            return null;
        }

        // NOTE: extended/upgraded are ignored in 1.20.5 and later
        PotionUtil.setBasePotionType(potionMeta, potionType, extended, upgraded);

        if (config.contains(base + ".Custom_Name")) {
            potionMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                    config.getString(base + ".Custom_Name")));
        }

        if (config.contains(base + ".Lore")) {
            final List<String> lore = new ArrayList<>();
            for (final String s : config.getStringList(base + ".Lore")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            potionMeta.setLore(lore);
        }

        item.setItemMeta(potionMeta);
        return item;
    }

    static void applyCustomNameAndLore(final @NotNull YamlConfiguration config,
            final @NotNull String type, final @NotNull String treasureName,
            final @NotNull ItemStack item) {
        final String base = type + "." + treasureName;
        final boolean hasCustomName = config.contains(base + ".Custom_Name");
        final boolean hasLore = config.contains(base + ".Lore");

        if (!hasCustomName && !hasLore) {
            return;
        }

        // Some materials have no item meta; the treasure still loads, just without name/lore
        final ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return;
        }

        if (hasCustomName) {
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                    config.getString(base + ".Custom_Name")));
        }

        if (hasLore) {
            final List<String> lore = new ArrayList<>();
            for (final String s : config.getStringList(base + ".Lore")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            itemMeta.setLore(lore);
        }

        item.setItemMeta(itemMeta);
    }

    static void logLoadSummary(final @NotNull Logger logger, final @NotNull String fileName,
            final @NotNull String type, final @NotNull TreasureLoadTally tally) {
        logger.info("Loaded " + tally.loaded() + " of " + tally.attempted() + " " + type
                + " treasures from " + fileName + ".");

        if (tally.incompatible() > 0) {
            logger.info("Skipped " + tally.incompatible() + " " + type + " treasure(s) in "
                    + fileName + " that require a newer Minecraft version.");
        }

        if (tally.invalid() > 0) {
            logger.warning("Failed to load " + tally.invalid() + " misconfigured " + type
                    + " treasure(s) in " + fileName + " (see warnings above).");
        }
    }
}
