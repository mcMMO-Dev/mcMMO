package com.gmail.nossr50.config;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import com.gmail.nossr50.util.sounds.SoundType;

public class SoundConfig extends BukkitConfig {
    private static SoundConfig instance;

    public SoundConfig() {
        super("sounds.yml");
        validate();
        instance = this;
    }

    public static SoundConfig getInstance() {
        if (instance == null) {
            return new SoundConfig();
        }

        return instance;
    }

    @Override
    protected void loadKeys() {

    }

    @Override
    protected boolean validateKeys() {
        for (SoundType soundType : SoundType.values()) {
            if (config.getDouble("Sounds." + soundType + ".Volume") < 0) {
                LogUtils.debug(mcMMO.p.getLogger(),
                        "[mcMMO] Sound volume cannot be below 0 for " + soundType);
                return false;
            }

            //Sounds with custom pitching don't use pitch values
            if (!soundType.usesCustomPitch()) {
                if (config.getDouble("Sounds." + soundType + ".Pitch") < 0) {
                    LogUtils.debug(mcMMO.p.getLogger(),
                            "[mcMMO] Sound pitch cannot be below 0 for " + soundType);
                    return false;
                }
            }
        }
        return true;
    }

    public float getMasterVolume() {
        return (float) config.getDouble("Sounds.MasterVolume", 1.0);
    }

    public float getVolume(SoundType soundType) {
        String key = "Sounds." + soundType + ".Volume";
        return (float) config.getDouble(key, 1.0);
    }

    public float getPitch(SoundType soundType) {
        String key = "Sounds." + soundType + ".Pitch";
        return (float) config.getDouble(key, 1.0);
    }

    public String getSound(SoundType soundType) {
        final String key = "Sounds." + soundType + ".CustomSoundId";
        return config.getString(key);
    }

    public boolean getIsEnabled(SoundType soundType) {
        String key = "Sounds." + soundType + ".Enable";
        return config.getBoolean(key, true);
    }
}
