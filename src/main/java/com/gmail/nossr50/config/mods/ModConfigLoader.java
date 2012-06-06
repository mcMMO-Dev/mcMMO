package com.gmail.nossr50.config.mods;

import java.io.File;

import com.gmail.nossr50.McMMO;
import com.gmail.nossr50.config.ConfigLoader;

public abstract class ModConfigLoader extends ConfigLoader{

    public ModConfigLoader(McMMO plugin, String fileName) {
        super(plugin, "ModConfigs" + File.separator + fileName);
    }
}
