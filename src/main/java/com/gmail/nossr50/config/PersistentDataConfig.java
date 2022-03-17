package com.gmail.nossr50.config;

import com.gmail.nossr50.metadata.MobMetaFlagType;

public class PersistentDataConfig extends BukkitConfig {
    private static PersistentDataConfig instance;

    private PersistentDataConfig() {
        super("persistent_data.yml");
        validate();
    }

    public static PersistentDataConfig getInstance() {
        if (instance == null) {
            instance = new PersistentDataConfig();
        }

        return instance;
    }

    @Override
    protected void loadKeys() {
        //Sigh this old config system...
    }

    @Override
    protected boolean validateKeys() {
        return true;
    }

    //Persistent Data Toggles
    public boolean isMobPersistent(MobMetaFlagType mobMetaFlagType) {
        String key = "Persistent_Data.Mobs.Flags." + mobMetaFlagType.toString() + ".Saved_To_Disk";
        return config.getBoolean(key, false);
    }

    public boolean useBlockTracker() {
        return config.getBoolean("mcMMO_Region_System.Enabled", true);
    }


}