package com.gmail.nossr50.config;

import com.gmail.nossr50.mcMMO;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class ConfigurableTest extends ConfigLoaderConfigurable {

    public final static String relativePath = "configurabletest.yml";
    private static ConfigurableTest instance;

    public ConfigurableTest() {
        super(mcMMO.p.getDataFolder(), relativePath);

        mcMMO.p.getLogger().severe("The value of bone "+boneValue);
    }

    public static ConfigurableTest getInstance() {
        if(instance == null)
            instance = new ConfigurableTest();

        return instance;
    }

    @Setting(value = "woof.bone", comment = "Finally we have found the value of bone")
    double boneValue = 9.4447;

    @Override
    public List<String> validateKeys() {
        return null;
    }
}
