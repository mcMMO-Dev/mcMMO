package com.gmail.nossr50.config;

import com.gmail.nossr50.mcMMO;
import java.io.InputStreamReader;
import org.bukkit.configuration.file.YamlConfiguration;

public class HiddenConfig {
    private static HiddenConfig instance;
    private final String fileName;
    private YamlConfiguration config;
    private int conversionRate;
    private boolean useEnchantmentBuffs;

    public HiddenConfig(String fileName) {
        this.fileName = fileName;
        load();
    }

    public static HiddenConfig getInstance() {
        if (instance == null) {
            instance = new HiddenConfig("hidden.yml");
        }

        return instance;
    }

    public void load() {
        InputStreamReader reader = mcMMO.p.getResourceAsReader(fileName);
        if (reader != null) {
            config = YamlConfiguration.loadConfiguration(reader);
            conversionRate = config.getInt("Options.ConversionRate", 1);
            useEnchantmentBuffs = config.getBoolean("Options.EnchantmentBuffs", true);
        }
    }


    public int getConversionRate() {
        return conversionRate;
    }

    public boolean useEnchantmentBuffs() {
        return useEnchantmentBuffs;
    }
}
