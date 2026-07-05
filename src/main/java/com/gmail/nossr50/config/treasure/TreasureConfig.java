package com.gmail.nossr50.config.treasure;

import static com.gmail.nossr50.config.treasure.TreasureEntryLoader.applyCustomNameAndLore;
import static com.gmail.nossr50.config.treasure.TreasureEntryLoader.buildPotionItem;
import static com.gmail.nossr50.config.treasure.TreasureEntryLoader.isPotionTypeResolvable;
import static com.gmail.nossr50.config.treasure.TreasureEntryLoader.logIncompatibleSummary;
import static com.gmail.nossr50.config.treasure.TreasureEntryLoader.logInvalidTreasure;
import static com.gmail.nossr50.config.treasure.TreasureEntryLoader.logLoadSummary;
import static com.gmail.nossr50.config.treasure.TreasureEntryLoader.parseData;
import static com.gmail.nossr50.util.text.ConfigStringUtils.getMaterialConfigString;

import com.gmail.nossr50.config.BukkitConfig;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.datatypes.treasure.HylianTreasure;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.LogUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TreasureConfig extends BukkitConfig {

    public static final String FILENAME = "treasures.yml";
    public static final String LEVEL_REQUIREMENT_RETRO_MODE = ".Level_Requirement.Retro_Mode";
    public static final String LEVEL_REQUIREMENT_STANDARD_MODE = ".Level_Requirement.Standard_Mode";
    public static final String WRONG_KEY_VALUE_STANDARD = ".Drop_Level.Standard_Mode";
    public static final String WRONG_KEY_VALUE_RETRO = ".Drop_Level.Retro_Mode";
    public static final String LEGACY_DROP_LEVEL = ".Drop_Level";
    public static final String WRONG_KEY_ROOT = ".Drop_Level";
    private static TreasureConfig instance;

    public HashMap<String, List<ExcavationTreasure>> excavationMap = new HashMap<>();
    public HashMap<String, List<HylianTreasure>> hylianMap = new HashMap<>();

    private TreasureConfig() {
        super(FILENAME, false);
        loadKeys();
        validate();
    }

    public static TreasureConfig getInstance() {
        if (instance == null) {
            instance = new TreasureConfig();
        }

        return instance;
    }

    @Override
    protected boolean validateKeys() {
        // Treasure entries are validated individually while loading; a bad entry is skipped rather
        // than failing startup, so there is nothing to validate here that could disable the plugin.
        return true;
    }

    @Override
    protected void loadKeys() {
        if (config.getConfigurationSection("Treasures") != null) {
            backup();
            return;
        }

        logLoadSummary(mcMMO.p.getLogger(), FILENAME, "Excavation", loadTreasures("Excavation"));
        logLoadSummary(mcMMO.p.getLogger(), FILENAME, "Hylian_Luck", loadTreasures("Hylian_Luck"));
    }

    private TreasureLoadTally loadTreasures(@NotNull String type) {
        final boolean isExcavation = type.equals("Excavation");
        final boolean isHylian = type.equals("Hylian_Luck");

        final ConfigurationSection treasureSection = config.getConfigurationSection(type);
        if (treasureSection == null) {
            return TreasureLoadTally.empty();
        }

        boolean fileNeedsUpdate = false;
        int loaded = 0;
        int invalid = 0;
        final List<String> incompatibleNames = new ArrayList<>();

        for (final String treasureName : treasureSection.getKeys(false)) {
            try {
                fileNeedsUpdate |= migrateLegacyDropLevel(type, treasureName);

                final TreasureLoadResult result = classifyExcavationTreasure(
                        config, type, treasureName, mcMMO.isRetroModeEnabled(),
                        mcMMO.p.getLogger());

                switch (result) {
                    case INCOMPATIBLE -> incompatibleNames.add(treasureName);
                    case INVALID -> invalid++;
                    case LOADED -> {
                        if (buildAndRegisterTreasure(type, treasureName, isExcavation, isHylian)) {
                            loaded++;
                        } else {
                            invalid++;
                        }
                    }
                }
            } catch (Exception e) {
                mcMMO.p.getLogger().warning("Skipping malformed treasure '" + treasureName + "' in "
                        + FILENAME + ": " + e.getMessage());
                invalid++;
            }
        }

        logIncompatibleSummary(mcMMO.p.getLogger(), FILENAME, type, incompatibleNames);

        if (fileNeedsUpdate) {
            try {
                config.save(getFile());
            } catch (IOException e) {
                mcMMO.p.getLogger().log(Level.WARNING, "Unable to save " + FILENAME, e);
            }
        }

        return new TreasureLoadTally(loaded, incompatibleNames.size(), invalid);
    }

    /**
     * Classifies a single treasure entry without mutating state or building an {@link ItemStack},
     * so it can be unit-tested directly against a {@link YamlConfiguration}.
     *
     * @param config      the loaded treasure configuration
     * @param type        the treasure section (for example {@code Excavation})
     * @param treasureName the entry key, optionally suffixed with {@code |data}
     * @param retroMode   whether the server is running in Retro mode
     * @param logger      logger used to report skipped entries
     * @return whether the entry is loadable, incompatible with this MC version, or invalid
     */
    static @NotNull TreasureLoadResult classifyExcavationTreasure(
            final @NotNull YamlConfiguration config, final @NotNull String type,
            final @NotNull String treasureName, final boolean retroMode,
            final @NotNull Logger logger) {
        final String materialName = treasureName.split("[|]")[0];
        final Material material = Material.matchMaterial(materialName);

        if (material == null) {
            LogUtils.debug(logger, "Skipping treasure '" + treasureName + "' in " + FILENAME
                    + " because material '" + materialName
                    + "' does not exist in this Minecraft version");
            return TreasureLoadResult.INCOMPATIBLE;
        }

        final String base = type + "." + treasureName;

        if (materialName.contains("POTION") && !isPotionTypeResolvable(config, base)) {
            LogUtils.debug(logger, "Skipping treasure '" + treasureName + "' in " + FILENAME
                    + " because its potion type does not exist in this Minecraft version");
            return TreasureLoadResult.INCOMPATIBLE;
        }

        final short data;
        try {
            data = parseData(treasureName, config, base);
        } catch (NumberFormatException e) {
            logInvalidTreasure(logger, FILENAME, treasureName, "data suffix '"
                    + treasureName.split("[|]")[1] + "' is not a number");
            return TreasureLoadResult.INVALID;
        }

        if (material.isBlock() && (data > Byte.MAX_VALUE || data < Byte.MIN_VALUE)) {
            logInvalidTreasure(logger, FILENAME, treasureName, "data value " + data + " is out of range");
            return TreasureLoadResult.INVALID;
        }

        final int dropLevel = retroMode
                ? config.getInt(base + LEVEL_REQUIREMENT_RETRO_MODE, -1)
                : config.getInt(base + LEVEL_REQUIREMENT_STANDARD_MODE, -1);
        if (dropLevel < 0) {
            logInvalidTreasure(logger, FILENAME, treasureName, "missing or invalid Level_Requirement");
            return TreasureLoadResult.INVALID;
        }

        final int xp = config.getInt(base + ".XP");
        if (xp < 0) {
            logInvalidTreasure(logger, FILENAME, treasureName, "XP value " + xp + " is negative");
            return TreasureLoadResult.INVALID;
        }

        final double dropChance = config.getDouble(base + ".Drop_Chance");
        if (dropChance < 0.0D) {
            logInvalidTreasure(logger, FILENAME, treasureName, "Drop_Chance " + dropChance + " is negative");
            return TreasureLoadResult.INVALID;
        }

        return TreasureLoadResult.LOADED;
    }

    private boolean buildAndRegisterTreasure(final @NotNull String type,
            final @NotNull String treasureName, final boolean isExcavation,
            final boolean isHylian) {
        final String base = type + "." + treasureName;
        final String materialName = treasureName.split("[|]")[0];
        final Material material = Material.matchMaterial(materialName);

        int amount = config.getInt(base + ".Amount");
        if (amount <= 0) {
            amount = 1;
        }
        final short data = parseData(treasureName, config, base);
        final int xp = config.getInt(base + ".XP");
        final double dropChance = config.getDouble(base + ".Drop_Chance");
        final int dropLevel = mcMMO.isRetroModeEnabled()
                ? config.getInt(base + LEVEL_REQUIREMENT_RETRO_MODE, -1)
                : config.getInt(base + LEVEL_REQUIREMENT_STANDARD_MODE, -1);

        final ItemStack item = buildItem(type, treasureName, materialName, material, amount, data);
        if (item == null) {
            return false;
        }

        if (isExcavation) {
            final ExcavationTreasure treasure = new ExcavationTreasure(item, xp, dropChance,
                    dropLevel);
            for (final String blockType : config.getStringList(base + ".Drops_From")) {
                excavationMap.computeIfAbsent(blockType, k -> new ArrayList<>()).add(treasure);
            }
        } else if (isHylian) {
            registerHylianDrops(base, new HylianTreasure(item, xp, dropChance, dropLevel));
        }

        return true;
    }

    private ItemStack buildItem(final @NotNull String type, final @NotNull String treasureName,
            final @NotNull String materialName, final @NotNull Material material, final int amount,
            final short data) {
        if (materialName.contains("POTION")) {
            return buildPotionItem(config, type, treasureName, material, amount, data, FILENAME,
                    mcMMO.p.getLogger());
        }

        final ItemStack item = new ItemStack(material, amount, data);
        applyCustomNameAndLore(config, type, treasureName, item);
        return item;
    }

    private void registerHylianDrops(final @NotNull String base,
            final @NotNull HylianTreasure treasure) {
        for (final String dropper : config.getStringList(base + ".Drops_From")) {
            switch (dropper) {
                case "Bushes" -> {
                    AddHylianTreasure(getMaterialConfigString(Material.FERN), treasure);
                    AddHylianTreasure(getMaterialConfigString(BlockUtils.getShortGrass()), treasure);
                    for (final Material species : Tag.SAPLINGS.getValues()) {
                        AddHylianTreasure(getMaterialConfigString(species), treasure);
                    }
                    AddHylianTreasure(getMaterialConfigString(Material.DEAD_BUSH), treasure);
                }
                case "Flowers" -> {
                    AddHylianTreasure(getMaterialConfigString(Material.POPPY), treasure);
                    AddHylianTreasure(getMaterialConfigString(Material.DANDELION), treasure);
                    AddHylianTreasure(getMaterialConfigString(Material.BLUE_ORCHID), treasure);
                    AddHylianTreasure(getMaterialConfigString(Material.ALLIUM), treasure);
                    AddHylianTreasure(getMaterialConfigString(Material.AZURE_BLUET), treasure);
                    AddHylianTreasure(getMaterialConfigString(Material.ORANGE_TULIP), treasure);
                    AddHylianTreasure(getMaterialConfigString(Material.PINK_TULIP), treasure);
                    AddHylianTreasure(getMaterialConfigString(Material.RED_TULIP), treasure);
                    AddHylianTreasure(getMaterialConfigString(Material.WHITE_TULIP), treasure);
                }
                case "Pots" -> {
                    for (final Material species : Tag.FLOWER_POTS.getValues()) {
                        AddHylianTreasure(getMaterialConfigString(species), treasure);
                    }
                }
                default -> AddHylianTreasure(dropper, treasure);
            }
        }
    }

    private boolean migrateLegacyDropLevel(final @NotNull String type,
            final @NotNull String treasureName) {
        boolean updated = false;

        // Legacy Drop_Level, needs to be converted
        if (getWrongKeyValue(type, treasureName, DropLevelKeyConversionType.LEGACY) != -1) {
            updated = processAutomaticKeyConversion(type, updated, treasureName,
                    DropLevelKeyConversionType.LEGACY);
        }

        // Partially converted to the new system, some configs shipped with this bad key
        if (getWrongKeyValue(type, treasureName, DropLevelKeyConversionType.WRONG_KEY_STANDARD)
                != -1) {
            updated = processAutomaticKeyConversion(type, updated, treasureName,
                    DropLevelKeyConversionType.WRONG_KEY_STANDARD);
        }

        if (getWrongKeyValue(type, treasureName, DropLevelKeyConversionType.WRONG_KEY_RETRO) != -1) {
            updated = processAutomaticKeyConversion(type, updated, treasureName,
                    DropLevelKeyConversionType.WRONG_KEY_RETRO);
        }

        return updated;
    }

    private boolean processAutomaticKeyConversion(String type, boolean shouldWeUpdateTheFile,
            String treasureName,
            DropLevelKeyConversionType conversionType) {
        switch (conversionType) {
            case LEGACY:
                int legacyDropLevel = getWrongKeyValue(
                        type, treasureName,
                        conversionType); //Legacy only had one value, Retro Mode didn't have a setting
                //Config needs to be updated to be more specific
                LogUtils.debug(
                        mcMMO.p.getLogger(),
                        "(" + treasureName
                                + ") [Fixing bad address: Legacy] Converting Drop_Level to Level_Requirement in treasures.yml for treasure to match new expected format");
                config.set(type + "." + treasureName + LEGACY_DROP_LEVEL,
                        null); //Remove legacy entry
                config.set(
                        type + "." + treasureName + LEVEL_REQUIREMENT_RETRO_MODE,
                        legacyDropLevel * 10); //Multiply by 10 for Retro
                config.set(type + "." + treasureName + LEVEL_REQUIREMENT_STANDARD_MODE,
                        legacyDropLevel);
                shouldWeUpdateTheFile = true;
                break;
            case WRONG_KEY_STANDARD:
                LogUtils.debug(
                        mcMMO.p.getLogger(),
                        "(" + treasureName
                                + ") [Fixing bad address: STANDARD] Converting Drop_Level to Level_Requirement in treasures.yml for treasure to match new expected format");
                int wrongKeyValueStandard = getWrongKeyValue(type, treasureName, conversionType);
                config.set(
                        type + "." + treasureName + WRONG_KEY_ROOT,
                        null); //We also kill the Retro key here as we have enough information for setting in values if needed

                if (wrongKeyValueStandard != -1) {
                    config.set(type + "." + treasureName + LEVEL_REQUIREMENT_STANDARD_MODE,
                            wrongKeyValueStandard);
                    config.set(
                            type + "." + treasureName + LEVEL_REQUIREMENT_RETRO_MODE,
                            wrongKeyValueStandard * 10); //Multiply by 10 for Retro
                }

                shouldWeUpdateTheFile = true;
                break;
            case WRONG_KEY_RETRO:
                LogUtils.debug(
                        mcMMO.p.getLogger(),
                        "(" + treasureName
                                + ") [Fixing bad address: RETRO] Converting Drop_Level to Level_Requirement in treasures.yml for treasure to match new expected format");
                int wrongKeyValueRetro = getWrongKeyValue(type, treasureName, conversionType);
                config.set(
                        type + "." + treasureName + WRONG_KEY_ROOT,
                        null); //We also kill the Retro key here as we have enough information for setting in values if needed

                if (wrongKeyValueRetro != -1) {
                    config.set(type + "." + treasureName + LEVEL_REQUIREMENT_RETRO_MODE,
                            wrongKeyValueRetro);
                }

                shouldWeUpdateTheFile = true;
                break;
        }
        return shouldWeUpdateTheFile;
    }

    private int getWrongKeyValue(String type, String treasureName,
            DropLevelKeyConversionType dropLevelKeyConversionType) {
        return switch (dropLevelKeyConversionType) {
            case LEGACY -> config.getInt(type + "." + treasureName + LEGACY_DROP_LEVEL, -1);
            case WRONG_KEY_STANDARD ->
                    config.getInt(type + "." + treasureName + WRONG_KEY_VALUE_STANDARD, -1);
            case WRONG_KEY_RETRO ->
                    config.getInt(type + "." + treasureName + WRONG_KEY_VALUE_RETRO, -1);
        };

    }

    private void AddHylianTreasure(String dropper, HylianTreasure treasure) {
        if (!hylianMap.containsKey(dropper)) {
            hylianMap.put(dropper, new ArrayList<>());
        }
        hylianMap.get(dropper).add(treasure);
    }

    private enum DropLevelKeyConversionType {
        LEGACY,
        WRONG_KEY_STANDARD,
        WRONG_KEY_RETRO
    }
}
