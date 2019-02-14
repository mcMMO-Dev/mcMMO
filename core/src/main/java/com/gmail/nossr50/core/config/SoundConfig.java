package com.gmail.nossr50.core.config;

import com.gmail.nossr50.core.McmmoCore;
import com.gmail.nossr50.core.util.sounds.SoundType;

import java.util.ArrayList;
import java.util.List;

public class SoundConfig extends ConfigValidated {
    private static SoundConfig instance;

    public SoundConfig() {
        super(McmmoCore.getDataFolderPath().getAbsoluteFile(), "sounds.yml");
        this.instance = this;
    }

    public static SoundConfig getInstance() {
        if (instance == null)
            return new SoundConfig();

        return instance;
    }

    @Override
    public void unload() {
        instance = null;
    }

    /**
     * The version of this config
     *
     * @return
     */
    @Override
    public double getConfigVersion() {
        return 1;
    }

    @Override
    public List<String> validateKeys() {
        ArrayList<String> reasons = new ArrayList<>();

        for (SoundType soundType : SoundType.values()) {
            if (getDoubleValue("Sounds." + soundType.toString() + ".Volume") < 0) {
                reasons.add("[mcMMO] Sound volume cannot be below 0 for " + soundType.toString());
            }

            //Sounds with custom pitching don't use pitch values
            if (!soundType.usesCustomPitch()) {
                if (getDoubleValue("Sounds." + soundType.toString() + ".Pitch") < 0) {
                    reasons.add("[mcMMO] Sound pitch cannot be below 0 for " + soundType.toString());
                }
            }
        }

        return reasons;
    }

    public float getMasterVolume() {
        return (float) getDoubleValue("Sounds.MasterVolume", 1.0);
    }

    public float getVolume(SoundType soundType) {
        String key = "Sounds." + soundType.toString() + ".Volume";
        return (float) getDoubleValue(key);
    }

    public float getPitch(SoundType soundType) {
        String key = "Sounds." + soundType.toString() + ".Pitch";
        return (float) getDoubleValue(key);
    }

    public boolean getIsEnabled(SoundType soundType) {
        String key = "Sounds." + soundType.toString() + ".Enabled";
        return getBooleanValue(key, true);
    }
}
