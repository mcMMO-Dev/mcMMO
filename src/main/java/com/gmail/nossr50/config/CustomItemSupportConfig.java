package com.gmail.nossr50.config;

import java.io.File;

public class CustomItemSupportConfig extends BukkitConfig {
    public CustomItemSupportConfig(File dataFolder) {
        super("custom_item_support.yml", dataFolder);
        validate();
    }

    @Override
    protected void loadKeys() {

    }

    public boolean isCustomRepairAllowed() {
        return config.getBoolean("Custom_Item_Support.Repair.Allow_Repair_On_Items_With_Custom_Model_Data", true);
    }

    public boolean isCustomSalvageAllowed() {
        return config.getBoolean("Custom_Item_Support.Salvage.Allow_Salvage_On_Items_With_Custom_Model_Data", true);
    }
}
