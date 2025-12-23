package com.gmail.nossr50.util.blockmeta;

import com.gmail.nossr50.config.experience.CustomBlocksConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Registry for custom blocks from third-party plugins like Oraxen, ItemsAdder, etc.
 * <p>
 * This allows plugins that add custom blocks to integrate with mcMMO's skill system.
 * Custom blocks can be registered to award XP for specific skills when broken.
 * <p>
 * <b>Config Integration:</b>
 * When a block is registered via the API, an entry is created in {@code custom-blocks.yml}.
 * Server owners can then customize XP values or disable specific blocks. Config values
 * always take priority over API-registered values, allowing user customization.
 * <p>
 * Third-party plugins can either:
 * <ul>
 *   <li>Register blocks programmatically using {@link #registerCustomBlock}</li>
 *   <li>Listen to {@link com.gmail.nossr50.events.skills.McMMOCustomBlockBreakEvent}
 *       and handle XP themselves</li>
 * </ul>
 * <p>
 * Example usage for plugin developers:
 * <pre>
 * // Register a custom ore block from your plugin
 * CustomBlockRegistry registry = mcMMO.getCustomBlockRegistry();
 * registry.registerCustomBlock("myplugin", "mythril_ore", PrimarySkillType.MINING, 150);
 * </pre>
 *
 * @since 2.2.026
 */
public class CustomBlockRegistry {

    private final Map<String, CustomBlockDefinition> registeredBlocks = new ConcurrentHashMap<>();

    // Common NamespacedKeys used by popular custom item plugins
    private static final NamespacedKey ORAXEN_KEY = new NamespacedKey("oraxen", "id");
    private static final NamespacedKey ITEMSADDER_KEY = new NamespacedKey("itemsadder", "id");

    /**
     * Loads custom blocks from the config file.
     * <p>
     * This should be called during plugin initialization, after configs are loaded.
     * It loads any previously registered blocks from {@code custom-blocks.yml}.
     */
    public void loadFromConfig() {
        CustomBlocksConfig config = CustomBlocksConfig.getInstance();
        Map<String, CustomBlocksConfig.CustomBlockEntry> configBlocks = config.getAllCustomBlocks();

        for (var entry : configBlocks.entrySet()) {
            CustomBlocksConfig.CustomBlockEntry block = entry.getValue();
            registeredBlocks.put(entry.getKey(), new CustomBlockDefinition(
                    block.pluginName(),
                    block.blockId(),
                    block.skill(),
                    block.xp(),
                    false // Not registered by a plugin this session
            ));
        }

        if (!configBlocks.isEmpty()) {
            mcMMO.p.getLogger().info("Loaded " + configBlocks.size() + " custom block(s) from config");
        }
    }

    /**
     * Registers a custom block that should award mcMMO XP when broken.
     * <p>
     * On first registration ("first contact"), an entry is created in {@code custom-blocks.yml}
     * with the provided default values. If the block already exists in config, the config
     * values are used instead, allowing server owners to customize XP.
     *
     * @param pluginName the name of the plugin registering this block
     * @param blockId    the unique identifier for this block within the plugin
     * @param skill      the mcMMO skill that should receive XP
     * @param xp         the default amount of XP to award
     */
    public void registerCustomBlock(@NotNull String pluginName, @NotNull String blockId,
            @NotNull PrimarySkillType skill, int xp) {
        String fullId = pluginName.toLowerCase() + ":" + blockId.toLowerCase();

        CustomBlocksConfig config = CustomBlocksConfig.getInstance();

        // Register in config if this is first contact (won't overwrite existing)
        boolean isFirstContact = config.registerIfAbsent(pluginName, blockId, skill, xp);

        // Check if user has disabled this block in config
        if (!config.isCustomBlockEnabled(pluginName, blockId)) {
            mcMMO.p.getLogger()
                    .info("Custom block " + fullId + " is disabled in config, skipping registration");
            registeredBlocks.remove(fullId); // Remove if it was loaded from config earlier
            return;
        }

        // Use config values if they exist (allows user overrides), otherwise use provided values
        int effectiveXp = config.getCustomBlockXp(pluginName, blockId);
        PrimarySkillType effectiveSkill = config.getCustomBlockSkill(pluginName, blockId);

        if (effectiveXp <= 0) {
            effectiveXp = xp;
        }
        if (effectiveSkill == null) {
            effectiveSkill = skill;
        }

        registeredBlocks.put(fullId, new CustomBlockDefinition(
                pluginName, blockId, effectiveSkill, effectiveXp, true
        ));

        if (isFirstContact) {
            mcMMO.p.getLogger().info("Registered new custom block: " + fullId
                    + " -> " + effectiveSkill.name() + " (" + effectiveXp + " XP)");
        } else {
            mcMMO.p.getLogger().info("Loaded custom block: " + fullId
                    + " -> " + effectiveSkill.name() + " (" + effectiveXp + " XP)"
                    + (config.hasCustomBlock(pluginName, blockId) ? " [config values]" : ""));
        }
    }

    /**
     * Registers a custom block using a combined identifier.
     *
     * @param fullId the full identifier in "plugin:block" format
     * @param skill  the mcMMO skill that should receive XP
     * @param xp     the amount of XP to award
     */
    public void registerCustomBlock(@NotNull String fullId, @NotNull PrimarySkillType skill, int xp) {
        String[] parts = fullId.split(":", 2);
        if (parts.length != 2) {
            mcMMO.p.getLogger()
                    .warning("Invalid custom block ID format: " + fullId + " (expected 'plugin:block')");
            return;
        }
        registerCustomBlock(parts[0], parts[1], skill, xp);
    }

    /**
     * Unregisters a custom block.
     * <p>
     * Note: This only removes the block from the runtime registry. The config entry
     * is preserved so user customizations are not lost. Use
     * {@link CustomBlocksConfig#removeCustomBlock} to also remove from config.
     *
     * @param pluginName the name of the plugin
     * @param blockId    the block identifier
     * @return true if the block was unregistered, false if it wasn't registered
     */
    public boolean unregisterCustomBlock(@NotNull String pluginName, @NotNull String blockId) {
        String fullId = pluginName.toLowerCase() + ":" + blockId.toLowerCase();
        return registeredBlocks.remove(fullId) != null;
    }

    /**
     * Unregisters all blocks from a specific plugin.
     * <p>
     * Useful when a plugin is disabled. Note: Config entries are preserved.
     *
     * @param pluginName the name of the plugin
     * @return the number of blocks unregistered
     */
    public int unregisterAllFromPlugin(@NotNull String pluginName) {
        String prefix = pluginName.toLowerCase() + ":";
        int count = 0;
        var iterator = registeredBlocks.keySet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().startsWith(prefix)) {
                iterator.remove();
                count++;
            }
        }
        if (count > 0) {
            mcMMO.p.getLogger().info("Unregistered " + count + " custom block(s) from " + pluginName);
        }
        return count;
    }

    /**
     * Gets the custom block definition for a block, if any.
     * This checks the block's PersistentDataContainer for known custom block keys.
     *
     * @param block the block to check
     * @return the definition if this is a registered custom block, empty otherwise
     */
    public Optional<CustomBlockDefinition> getCustomBlock(@NotNull Block block) {
        String customId = getCustomBlockId(block);
        if (customId == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(registeredBlocks.get(customId.toLowerCase()));
    }

    /**
     * Gets the custom block definition by its full ID.
     *
     * @param fullId the full identifier in "plugin:block" format
     * @return the definition if registered, empty otherwise
     */
    public Optional<CustomBlockDefinition> getCustomBlock(@NotNull String fullId) {
        return Optional.ofNullable(registeredBlocks.get(fullId.toLowerCase()));
    }

    /**
     * Checks if a block is a registered custom block.
     *
     * @param block the block to check
     * @return true if this is a registered custom block
     */
    public boolean isCustomBlock(@NotNull Block block) {
        return getCustomBlock(block).isPresent();
    }

    /**
     * Gets all registered custom blocks.
     *
     * @return an unmodifiable view of all registered blocks
     */
    public Map<String, CustomBlockDefinition> getRegisteredBlocks() {
        return Map.copyOf(registeredBlocks);
    }

    /**
     * Clears all registered custom blocks from the runtime registry.
     * <p>
     * Note: This does not affect the config file.
     */
    public void clear() {
        registeredBlocks.clear();
    }

    /**
     * Reloads custom blocks from config.
     * <p>
     * This clears the runtime registry and reloads from {@code custom-blocks.yml}.
     */
    public void reload() {
        clear();
        CustomBlocksConfig.getInstance().reload();
        loadFromConfig();
    }

    /**
     * Attempts to extract a custom block ID from a block.
     * Checks common NamespacedKeys used by popular plugins.
     *
     * @param block the block to check
     * @return the custom block ID in "plugin:block" format, or null if not a custom block
     */
    @Nullable
    private String getCustomBlockId(@NotNull Block block) {
        // For tile entities that have PDC
        if (block.getState() instanceof org.bukkit.block.TileState tileState) {
            PersistentDataContainer pdc = tileState.getPersistentDataContainer();

            // Check Oraxen
            String oraxenId = pdc.get(ORAXEN_KEY, PersistentDataType.STRING);
            if (oraxenId != null) {
                return "oraxen:" + oraxenId;
            }

            // Check ItemsAdder
            String iaId = pdc.get(ITEMSADDER_KEY, PersistentDataType.STRING);
            if (iaId != null) {
                return "itemsadder:" + iaId;
            }
        }

        // For non-tile-entity blocks, plugins typically use chunk-level PDC
        // or custom block tracking. Those plugins should handle XP themselves
        // or use the event-based approach.

        return null;
    }

    /**
     * Definition of a custom block that awards mcMMO XP.
     *
     * @param pluginName              the plugin that registered this block
     * @param blockId                 the block identifier within the plugin
     * @param skill                   the mcMMO skill that receives XP
     * @param xp                      the XP amount awarded
     * @param registeredThisSession   true if a plugin registered it this session
     */
    public record CustomBlockDefinition(
            @NotNull String pluginName,
            @NotNull String blockId,
            @NotNull PrimarySkillType skill,
            int xp,
            boolean registeredThisSession) {

        /**
         * Gets the full identifier for this block.
         *
         * @return the full ID in "plugin:block" format
         */
        public String getFullId() {
            return pluginName.toLowerCase() + ":" + blockId.toLowerCase();
        }
    }
}
