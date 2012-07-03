package com.gmail.nossr50.config;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.nossr50.mcMMO;

public class HiddenConfig {
    private static HiddenConfig instance;
    private static String fileName;
    private static YamlConfiguration config;
    private static boolean chunkletsEnabled;

    public HiddenConfig(String fileName) {
        HiddenConfig.fileName = fileName;
        load();
    }

    public static HiddenConfig getInstance() {
        if (instance == null) {
            instance = new HiddenConfig("hidden.yml");
        }

        return instance;
    }

    public void load() {
        if (mcMMO.p.getResource(fileName) != null) {
            config = YamlConfiguration.loadConfiguration(mcMMO.p.getResource(fileName));
            chunkletsEnabled = config.getBoolean("Options.Chunklets", true);
        }
    }

    public boolean getChunkletsEnabled() {
        return chunkletsEnabled;
    }
}
