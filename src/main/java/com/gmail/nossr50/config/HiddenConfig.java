package com.gmail.nossr50.config;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.nossr50.McMMO;

public class HiddenConfig extends ConfigLoader {
    private static String fileName;
    private static HiddenConfig instance;
    private static YamlConfiguration config;

    private static boolean chunkletsEnabled;

    public HiddenConfig(McMMO plugin, String fileName) {
        super(plugin, fileName);
        HiddenConfig.fileName = fileName;
    }


    public static HiddenConfig getInstance() {
        if (instance == null) {
            instance = new HiddenConfig(McMMO.p, "hidden.yml");
            instance.load();
        }

        return instance;
    }

    @Override
    public void load() {
        if (plugin.getResource(fileName) != null) {
            loadKeys();
        }
    }

    @Override
    protected void loadKeys() {
        config = YamlConfiguration.loadConfiguration(plugin.getResource(fileName));

        chunkletsEnabled = config.getBoolean("Options.Chunklets", true);
    }

    public boolean getChunkletsEnabled() {
        return chunkletsEnabled;
    }
}
