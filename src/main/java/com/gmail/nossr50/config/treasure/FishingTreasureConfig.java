package com.gmail.nossr50.config.treasure;

import static com.gmail.nossr50.config.treasure.TreasureEntryLoader.applyCustomNameAndLore;
import static com.gmail.nossr50.config.treasure.TreasureEntryLoader.buildPotionItem;
import static com.gmail.nossr50.config.treasure.TreasureEntryLoader.isPotionTypeResolvable;
import static com.gmail.nossr50.config.treasure.TreasureEntryLoader.logIncompatibleSummary;
import static com.gmail.nossr50.config.treasure.TreasureEntryLoader.logInvalidTreasure;
import static com.gmail.nossr50.config.treasure.TreasureEntryLoader.logLoadSummary;
import static com.gmail.nossr50.config.treasure.TreasureEntryLoader.parseData;

import com.gmail.nossr50.config.BukkitConfig;
import com.gmail.nossr50.datatypes.database.UpgradeType;
import com.gmail.nossr50.datatypes.treasure.EnchantmentTreasure;
import com.gmail.nossr50.datatypes.treasure.FishingTreasure;
import com.gmail.nossr50.datatypes.treasure.FishingTreasureBook;
import com.gmail.nossr50.datatypes.treasure.Rarity;
import com.gmail.nossr50.datatypes.treasure.ShakeTreasure;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EnchantmentUtils;
import com.gmail.nossr50.util.LogUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

public class FishingTreasureConfig extends BukkitConfig {

    private static final Logger LOGGER = Logger.getLogger(FishingTreasureConfig.class.getName());

    public static final String FILENAME = "fishing_treasures.yml";
    private static FishingTreasureConfig instance;

    public @NotNull HashMap<Rarity, List<FishingTreasure>> fishingRewards = new HashMap<>();
    public @NotNull HashMap<Rarity, List<EnchantmentTreasure>> fishingEnchantments = new HashMap<>();
    public @NotNull HashMap<EntityType, List<ShakeTreasure>> shakeMap = new HashMap<>();

    private FishingTreasureConfig() {
        super(FILENAME, false);
        loadKeys();
        validate();
    }

    public static FishingTreasureConfig getInstance() {
        if (instance == null) {
            instance = new FishingTreasureConfig();
        }

        return instance;
    }

    @Override
    protected boolean validateKeys() {
        final ConfigurationSection dropRates = config.getConfigurationSection(
                "Enchantment_Drop_Rates");

        if (dropRates == null) {
            mcMMO.p.getLogger().warning("Your fishing treasures config is empty, is this"
                    + " intentional? Delete it to regenerate.");
            return true;
        }

        for (final String problem : collectDropRateProblems(config)) {
            mcMMO.p.getLogger().warning("Drop rate issue in " + FILENAME + ": " + problem);
        }

        // Treasure configs never fail startup on invalid config; problems are reported, not fatal.
        return true;
    }

    /**
     * Collects human-readable problems with the {@code Enchantment_Drop_Rates} and
     * {@code Item_Drop_Rates} sections without mutating state, so it can be unit-tested against a
     * {@link YamlConfiguration}. Each problem names the offending key so an admin can find and fix
     * it. Returns an empty list when the rates are valid or absent.
     *
     * @param config the loaded fishing treasure configuration
     * @return a list of problem descriptions, each identifying the offending key
     */
    static @NotNull List<String> collectDropRateProblems(final @NotNull YamlConfiguration config) {
        final List<String> problems = new ArrayList<>();
        final ConfigurationSection dropRates = config.getConfigurationSection(
                "Enchantment_Drop_Rates");

        if (dropRates == null) {
            return problems;
        }

        for (final String tier : dropRates.getKeys(false)) {
            double totalEnchantDropRate = 0;
            double totalItemDropRate = 0;

            for (final Rarity rarity : Rarity.values()) {
                final double enchantDropRate = config.getDouble(
                        "Enchantment_Drop_Rates." + tier + "." + rarity);
                final double itemDropRate = config.getDouble(
                        "Item_Drop_Rates." + tier + "." + rarity);

                if (enchantDropRate < 0.0 || enchantDropRate > 100.0) {
                    problems.add("Enchantment_Drop_Rates." + tier + "." + rarity
                            + " should be between 0.0 and 100.0 (found " + enchantDropRate + ")");
                }

                if (itemDropRate < 0.0 || itemDropRate > 100.0) {
                    problems.add("Item_Drop_Rates." + tier + "." + rarity
                            + " should be between 0.0 and 100.0 (found " + itemDropRate + ")");
                }

                totalEnchantDropRate += enchantDropRate;
                totalItemDropRate += itemDropRate;
            }

            if (totalEnchantDropRate < 0 || totalEnchantDropRate > 100.0) {
                problems.add("Enchantment_Drop_Rates." + tier + " total should be between 0.0 and"
                        + " 100.0 (found " + totalEnchantDropRate + ")");
            }

            if (totalItemDropRate < 0 || totalItemDropRate > 100.0) {
                problems.add("Item_Drop_Rates." + tier + " total should be between 0.0 and 100.0"
                        + " (found " + totalItemDropRate + ")");
            }
        }

        return problems;
    }

    @Override
    protected void loadKeys() {
        if (config.getConfigurationSection("Treasures") != null) {
            backup();
            return;
        }

        if (mcMMO.getUpgradeManager().shouldUpgrade(UpgradeType.FIX_MOOSHROOM_ENTITY_ID)) {
            mcMMO.p.getLogger().log(Level.INFO,
                    "Fixing incorrect Mooshroom entity ID in fishing_treasures.yml,"
                            + " this will only run once...");
            final boolean patched = fixMooshroomEntityId(config);
            if (patched) {
                try {
                    config.save(getFile());
                    mcMMO.getUpgradeManager().setUpgradeCompleted(
                            UpgradeType.FIX_MOOSHROOM_ENTITY_ID);
                } catch (IOException e) {
                    mcMMO.p.getLogger().log(Level.SEVERE,
                            "Failed to save fishing_treasures.yml after patching Mooshroom entity"
                                    + " ID. You may manually rename the 'Shake.MUSHROOM_COW'"
                                    + " section to 'Shake.MOOSHROOM' in fishing_treasures.yml.", e);
                }
            } else {
                // Nothing to patch (key absent or already correct) — mark complete so we
                // don't check again on the next server startup.
                mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.FIX_MOOSHROOM_ENTITY_ID);
            }
        }

        logLoadSummary(mcMMO.p.getLogger(), FILENAME, "Fishing", loadTreasures("Fishing"));
        loadEnchantments();

        TreasureLoadTally shakeTally = TreasureLoadTally.empty();
        for (EntityType entity : EntityType.values()) {
            if (entity.isAlive()) {
                shakeTally = shakeTally.merge(loadTreasures("Shake." + entity));
            }
        }
        logLoadSummary(mcMMO.p.getLogger(), FILENAME, "Shake", shakeTally);
    }

    /**
     * Renames the {@code Shake.MUSHROOM_COW} section to {@code Shake.MOOSHROOM} in the supplied
     * config. {@code MUSHROOM_COW} was shipped as the entity key in early versions of
     * {@code fishing_treasures.yml} but is not a valid Spigot {@link EntityType} name; the correct
     * name is {@code MOOSHROOM}.
     *
     * <p>The rename is skipped if {@code Shake.MUSHROOM_COW} is absent (already patched or never
     * present) or if {@code Shake.MOOSHROOM} already exists (user has manual config).
     *
     * @param configuration the loaded YAML configuration to patch in-memory
     * @return {@code true} if the section was renamed, {@code false} if no change was needed
     */
    @VisibleForTesting
    static boolean fixMooshroomEntityId(
            final @NotNull org.bukkit.configuration.file.YamlConfiguration configuration) {
        final ConfigurationSection oldSection = configuration.getConfigurationSection(
                "Shake.MUSHROOM_COW");
        if (oldSection == null) {
            return false;
        }

        // Don't overwrite intentional user config for the correct key.
        if (configuration.getConfigurationSection("Shake.MOOSHROOM") == null) {
            for (final String key : oldSection.getKeys(false)) {
                configuration.set("Shake.MOOSHROOM." + key, oldSection.get(key));
            }
            LOGGER.info("Renamed Shake.MUSHROOM_COW to Shake.MOOSHROOM in fishing_treasures.yml.");
        }
        configuration.set("Shake.MUSHROOM_COW", null);
        return true;
    }

    private TreasureLoadTally loadTreasures(@NotNull String type) {
        final boolean isFishing = type.equals("Fishing");
        final boolean isShake = type.contains("Shake");

        final ConfigurationSection treasureSection = config.getConfigurationSection(type);
        if (treasureSection == null) {
            return TreasureLoadTally.empty();
        }

        // Initialize fishing reward buckets
        for (final Rarity rarity : Rarity.values()) {
            fishingRewards.computeIfAbsent(rarity, k -> new ArrayList<>());
        }

        int loaded = 0;
        int invalid = 0;
        final List<String> incompatibleNames = new ArrayList<>();

        for (final String treasureName : treasureSection.getKeys(false)) {
            try {
                final TreasureLoadResult result = classifyFishingTreasure(
                        config, type, treasureName, isFishing, mcMMO.p.getLogger());

                switch (result) {
                    case INCOMPATIBLE -> incompatibleNames.add(treasureName);
                    case INVALID -> invalid++;
                    case LOADED -> {
                        if (buildAndRegisterTreasure(type, treasureName, isFishing, isShake)) {
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

        return new TreasureLoadTally(loaded, incompatibleNames.size(), invalid);
    }

    /**
     * Classifies a single fishing/shake treasure entry without mutating state or building an
     * {@link ItemStack}, so it can be unit-tested directly against a {@link YamlConfiguration}.
     *
     * @param config       the loaded treasure configuration
     * @param type         the treasure section (for example {@code Fishing} or {@code Shake.COW})
     * @param treasureName the entry key, optionally suffixed with {@code |data}
     * @param isFishing    whether this is a Fishing entry (which requires a Rarity)
     * @param logger       logger used to report skipped entries
     * @return whether the entry is loadable, incompatible with this MC version, or invalid
     */
    static @NotNull TreasureLoadResult classifyFishingTreasure(
            final @NotNull YamlConfiguration config, final @NotNull String type,
            final @NotNull String treasureName, final boolean isFishing,
            final @NotNull Logger logger) {
        final String materialName = treasureName.split("[|]")[0];

        // Magic INVENTORY entry: a special shake treasure that steals from a player's inventory.
        if (materialName.contains("INVENTORY")) {
            return TreasureLoadResult.LOADED;
        }

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

        final int dropLevel = config.getInt(base + ".Drop_Level");
        if (dropLevel < 0) {
            logInvalidTreasure(logger, FILENAME, treasureName, "Drop_Level " + dropLevel + " is negative");
            return TreasureLoadResult.INVALID;
        }

        if (isFishing) {
            final String rarityStr = config.getString(base + ".Rarity");
            if (rarityStr == null) {
                logInvalidTreasure(logger, FILENAME, treasureName, "missing Rarity");
                return TreasureLoadResult.INVALID;
            }
            if (Rarity.tryMatch(rarityStr) == null) {
                logInvalidTreasure(logger, FILENAME, treasureName, "unknown Rarity '" + rarityStr + "'");
                return TreasureLoadResult.INVALID;
            }
        }

        return TreasureLoadResult.LOADED;
    }

    private boolean buildAndRegisterTreasure(final @NotNull String type,
            final @NotNull String treasureName, final boolean isFishing, final boolean isShake) {
        final String base = type + "." + treasureName;
        final String materialName = treasureName.split("[|]")[0];

        // Magic INVENTORY entry: steal from a player's inventory (uses BEDROCK as a sentinel).
        if (materialName.contains("INVENTORY")) {
            addShakeTreasure(new ShakeTreasure(new ItemStack(Material.BEDROCK, 1, (byte) 0), 1,
                    getInventoryStealDropChance(), getInventoryStealDropLevel()), EntityType.PLAYER);
            return true;
        }

        final Material material = Material.matchMaterial(materialName);
        int amount = config.getInt(base + ".Amount");
        if (amount <= 0) {
            amount = 1;
        }
        final short data = parseData(treasureName, config, base);
        final int xp = config.getInt(base + ".XP");
        final double dropChance = config.getDouble(base + ".Drop_Chance");
        final int dropLevel = config.getInt(base + ".Drop_Level");
        final Rarity rarity = isFishing
                ? Rarity.getRarity(config.getString(base + ".Rarity"))
                : null;

        if (isFishing && material == Material.ENCHANTED_BOOK) {
            registerEnchantedBook(base, xp, rarity);
            return true;
        }

        final ItemStack item;
        if (materialName.contains("POTION")) {
            item = buildPotionItem(config, type, treasureName, material, amount, data, FILENAME,
                    mcMMO.p.getLogger());
            if (item == null) {
                return false;
            }
        } else {
            item = new ItemStack(material, amount, data);
            applyCustomNameAndLore(config, type, treasureName, item);
        }

        if (isFishing) {
            addFishingTreasure(rarity, new FishingTreasure(item, xp));
        } else if (isShake) {
            addShakeTreasure(new ShakeTreasure(item, xp, dropChance, dropLevel),
                    EntityType.valueOf(type.substring(6)));
        }

        return true;
    }

    private void registerEnchantedBook(final @NotNull String base, final int xp,
            final @NotNull Rarity rarity) {
        final ItemStack item = new ItemStack(Material.ENCHANTED_BOOK, 1);
        final ItemMeta itemMeta = item.getItemMeta();

        final Set<Enchantment> blackListedEnchants = new HashSet<>();
        final Set<Enchantment> whiteListedEnchants = new HashSet<>();
        matchAndFillSet(config.getStringList(base + ".Enchantments_Blacklist"), blackListedEnchants);
        matchAndFillSet(config.getStringList(base + ".Enchantments_Whitelist"), whiteListedEnchants);

        if (config.contains(base + ".Custom_Name") && itemMeta != null) {
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                    config.getString(base + ".Custom_Name")));
            item.setItemMeta(itemMeta);
        }

        addFishingTreasure(rarity,
                new FishingTreasureBook(item, xp, blackListedEnchants, whiteListedEnchants));
    }

    private void addShakeTreasure(@NotNull ShakeTreasure shakeTreasure,
            @NotNull EntityType entityType) {
        if (!shakeMap.containsKey(entityType)) {
            shakeMap.put(entityType, new ArrayList<>());
        }
        shakeMap.get(entityType).add(shakeTreasure);
    }

    private void addFishingTreasure(@NotNull Rarity rarity,
            @NotNull FishingTreasure fishingTreasure) {
        fishingRewards.get(rarity).add(fishingTreasure);
    }

    /**
     * Matches enchantments on a list (user provided string) to known enchantments in the Spigot API
     * Any matches are added to the passed set
     *
     * @param enchantListStr the users string list of enchantments
     * @param permissiveList the permissive list of enchantments
     */
    private void matchAndFillSet(@NotNull List<String> enchantListStr,
            @NotNull Set<Enchantment> permissiveList) {
        if (enchantListStr.isEmpty()) {
            return;
        }

        for (String str : enchantListStr) {
            boolean foundMatch = false;
            for (Enchantment enchantment : Enchantment.values()) {
                if (enchantment.getKey().getKey().equalsIgnoreCase(str)) {
                    permissiveList.add(enchantment);
                    foundMatch = true;
                    break;
                }
            }

            if (!foundMatch) {
                LogUtils.debug(
                        mcMMO.p.getLogger(),
                        "[Fishing Treasure Init] Could not find any enchantments which matched the user defined enchantment named: "
                                + str);
            }
        }
    }

    private void loadEnchantments() {
        for (Rarity rarity : Rarity.values()) {
            if (!fishingEnchantments.containsKey(rarity)) {
                fishingEnchantments.put(rarity, (new ArrayList<>()));
            }

            ConfigurationSection enchantmentSection = config.getConfigurationSection(
                    "Enchantments_Rarity." + rarity.toString());

            if (enchantmentSection == null) {
                continue;
            }

            for (String enchantmentName : enchantmentSection.getKeys(false)) {
                int level = config.getInt("Enchantments_Rarity." + rarity + "." + enchantmentName);
                Enchantment enchantment = EnchantmentUtils.getByName(enchantmentName);

                if (enchantment == null) {
                    mcMMO.p.getLogger().info(
                            "Skipping invalid enchantment in '" + FILENAME + "', named:"
                                    + enchantmentName);
                    continue;
                }

                fishingEnchantments.get(rarity).add(new EnchantmentTreasure(enchantment, level));
            }
        }
    }

    public boolean getInventoryStealEnabled() {
        return config.contains("Shake.PLAYER.INVENTORY");
    }

    public boolean getInventoryStealStacks() {
        return config.getBoolean("Shake.PLAYER.INVENTORY.Whole_Stacks");
    }

    public double getInventoryStealDropChance() {
        return config.getDouble("Shake.PLAYER.INVENTORY.Drop_Chance");
    }

    public int getInventoryStealDropLevel() {
        return config.getInt("Shake.PLAYER.INVENTORY.Drop_Level");
    }

    public double getItemDropRate(int tier, @NotNull Rarity rarity) {
        return config.getDouble("Item_Drop_Rates.Tier_" + tier + "." + rarity);
    }

    public double getEnchantmentDropRate(int tier, @NotNull Rarity rarity) {
        return config.getDouble("Enchantment_Drop_Rates.Tier_" + tier + "." + rarity);
    }
}
