package com.gmail.nossr50.config;

import com.gmail.nossr50.mcMMO;

import java.util.List;

public class ConfigurableTest extends ConfigLoaderConfigurable {

    public final static String relativePath = "configurabletest.yml";
    private static ConfigurableTest instance;



    public ConfigurableTest() {
        super(mcMMO.p.getDataFolder(), relativePath);
    }

    public static ConfigurableTest getInstance() {
        if(instance == null)
            instance = new ConfigurableTest();

        return instance;
    }

    @Override
    public List<String> validateKeys() {
        return null;
    }
}
