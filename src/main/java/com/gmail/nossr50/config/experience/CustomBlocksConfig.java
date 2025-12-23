package com.gmail.nossr50.config.experience;

import com.gmail.nossr50.config.BukkitConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.text.StringUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Configuration file for custom blocks registered by third-party plugins.
 * <p>
 * When plugins like Oraxen or ItemsAdder register custom blocks via the API,
 * entries are created in this config file. Server owners can then customize
 * XP values or disable specific blocks.
 * <p>
 * This follows the "first contact" principle - the config entry is only created
 * when a block is first registered. Existing entries are never overwritten,
 * allowing user customizations to persist.
 *
 * @since 2.2.026
 */
public class CustomBlocksConfig extends BukkitConfig {

    private static CustomBlocksConfig instance;

    private CustomBlocksConfig() {
        super("custom-blocks.yml", false); // Don't copy defaults, this is user-generated content
    }

    public static CustomBlocksConfig getInstance() {
        if (instance == null) {
            instance = new CustomBlocksConfig();
        }
        return instance;
    }

    @Override
    protected void loadKeys() {
        // No validation needed on load - entries are created dynamically
    }

    /**
     * Gets the XP value for a custom block from config.
     *
     * @param pluginName the plugin that registered the block
     * @param blockId    the block identifier
     * @return the XP value, or -1 if not configured
     */
    public int getCustomBlockXp(@NotNull String pluginName, @NotNull String blockId) {
        String path = getConfigPath(pluginName, blockId) + ".xp";
        return config.getInt(path, -1);
    }

    /**
     * Gets the skill type for a custom block from config.
     *
     * @param pluginName the plugin that registered the block
     * @param blockId    the block identifier
     * @return the skill type, or null if not configured
     */
    @Nullable
    public PrimarySkillType getCustomBlockSkill(@NotNull String pluginName, @NotNull String blockId) {
        String path = getConfigPath(pluginName, blockId) + ".skill";
        String skillName = config.getString(path);
        if (skillName == null) {
            return null;
        }
        return mcMMO.p.getSkillTools().matchSkill(skillName);
    }

    /**
     * Checks if a custom block is enabled (not disabled by user).
     *
     * @param pluginName the plugin that registered the block
     * @param blockId    the block identifier
     * @return true if enabled, false if disabled
     */
    public boolean isCustomBlockEnabled(@NotNull String pluginName, @NotNull String blockId) {
        String path = getConfigPath(pluginName, blockId) + ".enabled";
        return config.getBoolean(path, true);
    }

    /**
     * Checks if a custom block exists in the config.
     *
     * @param pluginName the plugin that registered the block
     * @param blockId    the block identifier
     * @return true if the block exists in config
     */
    public boolean hasCustomBlock(@NotNull String pluginName, @NotNull String blockId) {
        return config.contains(getConfigPath(pluginName, blockId));
    }

    /**
     * Registers a custom block in config if it doesn't already exist (first contact).
     * <p>
     * This method does NOT overwrite existing user configurations, ensuring that
     * user customizations persist across server restarts and plugin updates.
     *
     * @param pluginName the plugin registering the block
     * @param blockId    the block identifier
     * @param skill      the default skill type
     * @param defaultXp  the default XP value
     * @return true if this was first contact (new entry created), false if already existed
     */
    public boolean registerIfAbsent(@NotNull String pluginName, @NotNull String blockId,
            @NotNull PrimarySkillType skill, int defaultXp) {
        String basePath = getConfigPath(pluginName, blockId);

        // Check if already exists in config - don't overwrite user settings
        if (config.contains(basePath)) {
            return false;
        }

        // First contact - create default entry
        config.set(basePath + ".skill", StringUtils.getCapitalized(skill.toString()));
        config.set(basePath + ".xp", defaultXp);
        config.set(basePath + ".enabled", true);

        saveConfig();
        return true;
    }

    /**
     * Gets all custom blocks configured in this file.
     *
     * @return map of "plugin:block" -> CustomBlockEntry for all enabled blocks
     */
    public Map<String, CustomBlockEntry> getAllCustomBlocks() {
        Map<String, CustomBlockEntry> blocks = new HashMap<>();

        Set<String> plugins = config.getKeys(false);
        for (String pluginName : plugins) {
            if (!config.isConfigurationSection(pluginName)) {
                continue;
            }

            var pluginSection = config.getConfigurationSection(pluginName);
            if (pluginSection == null) {
                continue;
            }

            Set<String> blockIds = pluginSection.getKeys(false);
            for (String blockId : blockIds) {
                String fullId = pluginName.toLowerCase() + ":" + blockId.toLowerCase();

                PrimarySkillType skill = getCustomBlockSkill(pluginName, blockId);
                int xp = getCustomBlockXp(pluginName, blockId);
                boolean enabled = isCustomBlockEnabled(pluginName, blockId);

                if (skill != null && xp > 0 && enabled) {
                    blocks.put(fullId, new CustomBlockEntry(pluginName, blockId, skill, xp));
                }
            }
        }

        return blocks;
    }

    /**
     * Removes a custom block entry from config.
     *
     * @param pluginName the plugin name
     * @param blockId    the block identifier
     * @return true if removed, false if it didn't exist
     */
    public boolean removeCustomBlock(@NotNull String pluginName, @NotNull String blockId) {
        String path = getConfigPath(pluginName, blockId);
        if (config.contains(path)) {
            config.set(path, null);

            // Clean up empty plugin sections
            String pluginPath = pluginName.toLowerCase();
            var section = config.getConfigurationSection(pluginPath);
            if (section != null && section.getKeys(false).isEmpty()) {
                config.set(pluginPath, null);
            }

            saveConfig();
            return true;
        }
        return false;
    }

    /**
     * Removes all custom blocks from a specific plugin.
     *
     * @param pluginName the plugin name
     * @return the number of blocks removed
     */
    public int removeAllFromPlugin(@NotNull String pluginName) {
        String pluginPath = pluginName.toLowerCase();
        var section = config.getConfigurationSection(pluginPath);
        if (section == null) {
            return 0;
        }

        int count = section.getKeys(false).size();
        config.set(pluginPath, null);
        saveConfig();
        return count;
    }

    private String getConfigPath(String pluginName, String blockId) {
        return pluginName.toLowerCase() + "." + blockId.toLowerCase();
    }

    private void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            mcMMO.p.getLogger().severe("Failed to save custom-blocks.yml: " + e.getMessage());
        }
    }

    /**
     * Reloads the config from disk.
     */
    public void reload() {
        instance = new CustomBlocksConfig();
    }

    /**
     * Entry representing a custom block configuration.
     *
     * @param pluginName the plugin that registered this block
     * @param blockId    the block identifier within the plugin
     * @param skill      the mcMMO skill that receives XP
     * @param xp         the XP amount awarded
     */
    public record CustomBlockEntry(
            @NotNull String pluginName,
            @NotNull String blockId,
            @NotNull PrimarySkillType skill,
            int xp) {

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

