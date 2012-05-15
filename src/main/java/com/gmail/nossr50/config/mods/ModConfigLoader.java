package com.gmail.nossr50.config.mods;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.ConfigLoader;
import com.gmail.nossr50.datatypes.mods.CustomItem;

public abstract class ModConfigLoader extends ConfigLoader{
    public List<Integer> customIDs = new ArrayList<Integer>();
    public List<CustomItem> customItems = new ArrayList<CustomItem>();

    public ModConfigLoader(mcMMO plugin, String fileName) {
        super(plugin, "ModConfigs" + File.separator + fileName);
    }
}
