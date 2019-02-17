package com.gmail.nossr50.core.mcmmo.plugin;

/**
 * Represents mcMMO as it is seen by various APIs
 * In Bukkit, its useful to have a Plugin reference to schedule tasks etc, that is why this abstraction exists
 * I'm actually not sure I need this though
 */
public interface Plugin {
    /**
     * In the event of some critical failure in mcMMO shut down the plugin
     */
    void disablePlugin();
}
