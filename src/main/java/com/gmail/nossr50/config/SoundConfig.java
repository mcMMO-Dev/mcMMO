package com.gmail.nossr50.config;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.sounds.SoundType;

public class SoundConfig extends BukkitConfig {
    private static SoundConfig instance;

    public SoundConfig() {
        super("sounds.yml");
        instance = this;
    }

    public static SoundConfig getInstance() {
        if (instance == null)
            return new SoundConfig();

        return instance;
    }

    @Override
    protected void loadKeys() {

    }

    @Override
    protected void validateConfigKeys() {
        //TODO: Rewrite legacy validation code
        for (SoundType soundType : SoundType.values()) {
            if (config.getDouble("Sounds." + soundType.toString() + ".Volume") < 0) {
                mcMMO.p.getLogger().info("[mcMMO] Sound volume cannot be below 0 for " + soundType);
                //TODO: Rewrite legacy validation code
                //return false;
            }

            //Sounds with custom pitching don't use pitch values
            if (!soundType.usesCustomPitch()) {
                if (config.getDouble("Sounds." + soundType + ".Pitch") < 0) {
                    mcMMO.p.getLogger().info("[mcMMO] Sound pitch cannot be below 0 for " + soundType);
                    //TODO: Rewrite legacy validation code
                    //return false;
                }
            }
        }
    }

    public float getMasterVolume() {
        return (float) config.getDouble("Sounds.MasterVolume", 1.0);
    }

    public float getVolume(SoundType soundType) {
        String key = "Sounds." + soundType.toString() + ".Volume";
        return (float) config.getDouble(key);
    }

    public float getPitch(SoundType soundType) {
        String key = "Sounds." + soundType.toString() + ".Pitch";
        return (float) config.getDouble(key);
    }

    public boolean getIsEnabled(SoundType soundType) {
        String key = "Sounds." + soundType.toString() + ".Enabled";
        return config.getBoolean(key, true);
    }
}
